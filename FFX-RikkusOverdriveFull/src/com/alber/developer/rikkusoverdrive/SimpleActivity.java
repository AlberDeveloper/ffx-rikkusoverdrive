package com.alber.developer.rikkusoverdrive;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.alber.developer.rikkusoverdrive.csvreader.CsvReader;

public class SimpleActivity extends Activity implements View.OnClickListener,
		OnItemSelectedListener {

	private final static String MIX_CHART_FILE = "/rikkus_mix_chart.csv";
	private final static String MIX_CHART_EXTRA_FILE = "/rikkus_mix_chart_extra.csv";

	private Spinner spinItem1;
	private Spinner spinItem2;
	private Spinner spinExtraItem;
	private Button buttonCheck;
	//private Button buttonExtraCheck;
	private TextView textViewResult;
	private TextView textViewExtraResult;
	private TabHost th;
	private String tab1Title;
	private String tab2Title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_simple);

		initialize();
		setUpTabHost();
	}

	private void initialize() {
		File mixChartFile;
		spinItem1 = (Spinner) findViewById(R.id.spinItem1);
		spinItem2 = (Spinner) findViewById(R.id.spinItem2);
		spinExtraItem = (Spinner) findViewById(R.id.spinExtraItem);
		buttonCheck = (Button) findViewById(R.id.buttonCheck);
		//buttonExtraCheck = (Button) findViewById(R.id.buttonExtraCheck);
		textViewResult = (TextView) findViewById(R.id.textViewResult);
		textViewExtraResult = (TextView) findViewById(R.id.textViewExtraResult);

		spinItem1.setOnItemSelectedListener(this);
		spinItem2.setOnItemSelectedListener(this);
		
		spinExtraItem.setOnItemSelectedListener(new OnItemSelectedListener() {

		    @Override
		    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
            	checkItemsForExtraItem();
		    }

		    @Override
		    public void onNothingSelected(AdapterView<?> parentView) {
		        // your code here
		    }
		});		
		
		
		buttonCheck.setOnClickListener(this);
		//buttonExtraCheck.setOnClickListener(this);

		tab1Title = getString(getResources().getIdentifier("search_by_item",
				"string", getPackageName()));
		tab2Title = getString(getResources().getIdentifier(
				"search_by_extra_item", "string", getPackageName()));

		mixChartFile = new File(getFilesDir().getPath() + MIX_CHART_FILE);
		if (!mixChartFile.exists()) {
			InputStream in = getBaseContext().getResources().openRawResource(
					R.raw.rikkus_mix_chart);
			loadMixChart(in, mixChartFile);
		}

		/* ¡¡ONLY FOR FULL VERSION!! */
		mixChartFile = new File(getFilesDir().getPath() + MIX_CHART_EXTRA_FILE);
		if (!mixChartFile.exists()) {
			InputStream in = getBaseContext().getResources().openRawResource(
					R.raw.rikkus_mix_chart_extra);
			loadMixChart(in, mixChartFile);
		}
	}

	private void setUpTabHost() {
		th = (TabHost) findViewById(R.id.tabhost);
		th.setup();
th.setMotionEventSplittingEnabled(true);

		TabSpec specs = th.newTabSpec("SimpleOverdrive");
		specs.setContent(R.id.tabSimpleOverdrive);
		specs.setIndicator(tab1Title);
		th.addTab(specs);

		specs = th.newTabSpec("ExtraOverdrive");
		specs.setContent(R.id.tabExtraOverdrive);
		specs.setIndicator(tab2Title);
		th.addTab(specs);

	}

	private void loadMixChart(InputStream in, File file) {
		try {
			OutputStream out = new FileOutputStream(file);

			byte[] buffer = new byte[1024];
			int read;
			while ((read = in.read(buffer)) != -1) {
				out.write(buffer, 0, read);
			}
			in.close();
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void checkExtraItem(int x, int y) {
		int countY = 0, auxX;
		String result = "extra_";

		// Si x es mayor a y, se intercambian los valores.
		if (x > y) {
			auxX = x;
			x = y;
			y = auxX;
		}

		x++;

		System.out.println("X: " + x);
		System.out.println("Y: " + y);

		try {
			CsvReader extraItemsChart = new CsvReader(getFilesDir().getPath()
					+ MIX_CHART_FILE, ';');
			extraItemsChart.readHeaders();

			while (extraItemsChart.readRecord() || countY <= y) {
				if (countY == y) {
					System.out
							.println("RESULTADO 0: " + extraItemsChart.get(x));
					result += extraItemsChart.get(x);

					System.out.println("RESULTADO 1: " + result);
					String a = getString(getResources().getIdentifier(result,
							"string", getPackageName()));
					textViewResult.setText(a);
				}
				countY++;
			}
			extraItemsChart.close();
		} catch (FileNotFoundException e) {
			// e.printStackTrace();
			System.out.println("ERROR 1: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("ERROR 2: " + e.getMessage());
			// e.printStackTrace();
		} catch (Exception e) {
			System.out.println("ERROR 3: " + e.getMessage());
		}
	}

	private void checkItemsForExtraItem() {
		int y = 0, i = 0, mainItemId, secondaryItemId, extraItem = spinExtraItem.getSelectedItemPosition();
		boolean end = false, extraItemFind = false;
		ItemAux itemAux;
		ArrayList<ItemAux> printList = new ArrayList<ItemAux>();
		String finalString="";

		try {
			CsvReader itemsChart = new CsvReader(getFilesDir().getPath()
					+ MIX_CHART_EXTRA_FILE, ';');

			itemsChart.readHeaders();

			while (!end) {

				// Localizar primera posición del extra item a buscar.
				if (itemsChart.readRecord()) {
					
					// Hay un item, ¿es el buscado?
					if (!itemsChart.get(0).isEmpty()) {
						if (y == extraItem) {
							// Es el item buscado.
							extraItemFind = true;

							mainItemId = Integer.parseInt(itemsChart.get(1));
							mainItemId--;
							
							// La primera columna de items es la 2. Al final se
							// le restará 2 al valor obtenido.
							for (i = 2; i < itemsChart.getColumnCount(); i++) {
								if (itemsChart.get(i).equals("1")) {
									secondaryItemId = i - 2;
									itemAux = new ItemAux(this,mainItemId,secondaryItemId);
									printList.add(itemAux);
								}
							}
						} else {
							// Si ya lo he encontrado y hay otro extra Item, es
							// que ya he terminado.
							if(extraItemFind)
								end = true;
						}

						y++;

					} else {
						if (extraItemFind) {
							// Aqui ya recojo la lista de items del item extra
							// que estoy buscando.
							
							mainItemId = Integer.parseInt(itemsChart.get(1));
							mainItemId--;
							for (i = 2; i < itemsChart.getColumnCount(); i++) {
								if (itemsChart.get(i).equals("1")) {
									secondaryItemId = i - 2;
									itemAux = new ItemAux(this,mainItemId,secondaryItemId);
									printList.add(itemAux);
								}
							}
						}
					}
				}else
					end = true;
			}

			itemsChart.close();
			//System.out.println(printList.size());	
			for (i = 0; i < printList.size(); i++){
				itemAux = printList.get(i);
				finalString += itemAux.toString() + "\n";
				//System.out.println(itemAux.toString());	
			}
			
			//System.out.println(finalString);	
			textViewExtraResult.setText(finalString);
			
		} catch (FileNotFoundException e) {
			// e.printStackTrace();
			System.out.println("ERROR 1: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("ERROR 2: " + e.getMessage());
			// e.printStackTrace();
		} catch (Exception e) {
			System.out.println("ERROR 3: " + e.getMessage());
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.simple, menu);
		return true;
	}
	
	/**
	 * Método sobrecargado que muestra la opción seleccionada.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		
		case R.id.action_about:
			Intent a = new Intent("com.alber.developer.rikkusoverdrive.About");
			startActivity(a);
			break;
		}

		return false;
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {
		int x, y;
		switch(v.getId()){
		case R.id.buttonCheck:
			x = spinItem1.getSelectedItemPosition();
			y = spinItem2.getSelectedItemPosition();
			checkExtraItem(x, y);
			break;
		/*case R.id.buttonExtraCheck:
			y = spinExtraItem.getSelectedItemPosition();
			checkItemsForExtraItem();
			break;	*/	
			
		}
	}
}
