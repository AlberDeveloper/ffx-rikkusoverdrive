package com.alber.developer.rikkusoverdrive;

import android.os.Bundle;
import android.app.Activity;

import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.alber.developer.rikkusoverdrive.csvreader.CsvReader;


public class MainActivity extends Activity implements View.OnClickListener, OnItemSelectedListener {
	
	private final static String MIX_CHART_FILE = "/rikkus_mix_chart.csv";
	
	private Spinner spinItem1;
	private Spinner spinItem2;
	private Button buttonCheck;
	private TextView textViewResult;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initialize();		
	}

	private void initialize() {

		spinItem1 = (Spinner) findViewById(R.id.spinItem1);
		spinItem2 = (Spinner) findViewById(R.id.spinItem2);	
		buttonCheck = (Button) findViewById(R.id.buttonCheck);
		textViewResult = (TextView) findViewById(R.id.textViewResult);
		
		spinItem1.setOnItemSelectedListener(this);
		spinItem2.setOnItemSelectedListener(this);
		buttonCheck.setOnClickListener(this);
		
		loadMixChart();
	}
	
	private void loadMixChart(){
		
		File mixChartFile = new File(getFilesDir().getPath() + MIX_CHART_FILE);
		
		if (!mixChartFile.exists()){
			try{
				InputStream in = getBaseContext().getResources().openRawResource(R.raw.rikkus_mix_chart);
				OutputStream out = new FileOutputStream(mixChartFile);
				
				byte[] buffer = new byte[1024];
		         int read;
		         while((read = in.read(buffer)) != -1){
		           out.write(buffer, 0, read);
		         }
		         in.close();
		         out.flush();
		         out.close();
	         } 
			catch (IOException e) {
	             e.printStackTrace();
	         }
		} 
	}
	
	private void checkExtraItem(int x, int y){
		int countY = 0, auxX;
		String result = "extra_";
		
		//Si x es mayor a y, se intercambian los valores.
		if(x > y){
			auxX = x;
			x = y;
			y = auxX;
		}
		
		x++;
		
		System.out.println("X: " + x);
		System.out.println("Y: " + y);
		
        try {      
            CsvReader extraItemsChart = new CsvReader(getFilesDir().getPath() + MIX_CHART_FILE, ';');
            extraItemsChart.readHeaders();

            while (extraItemsChart.readRecord() || countY <= y) {
				if(countY == y){
					System.out.println("RESULTADO 0: " + extraItemsChart.get(x)); 
					result += extraItemsChart.get(x);					

					System.out.println("RESULTADO 1: " + result);
					String a = getString(getResources().getIdentifier(result, "string", getPackageName()));
					textViewResult.setText(a);					
				}
				countY++;
            }
            extraItemsChart.close();
        } catch (FileNotFoundException e) {
            //e.printStackTrace();
        	System.out.println("ERROR 1: " + e.getMessage());
        } catch (IOException e) {
        	System.out.println("ERROR 2: " + e.getMessage());
            //e.printStackTrace();
        }catch (Exception e){
        	System.out.println("ERROR 3: " + e.getMessage());
        }
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onClick(View v) {
		int x, y;
		if(v.getId() == R.id.buttonCheck){
			x = spinItem1.getSelectedItemPosition();
			y = spinItem2.getSelectedItemPosition();
			checkExtraItem(x,y);
		}
	}
}
