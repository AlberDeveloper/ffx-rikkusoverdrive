package com.alber.developer.rikkusoverdrive;

import android.content.Context;

public class ItemAux {
	int mainItem, secondayItem;
	String items[];
	private final Context myContext;
	
	public ItemAux(Context context, int mainItem, int secondayItem){
		super();
		this.myContext = context;
		this.mainItem = mainItem;
		this.secondayItem = secondayItem;
		items = this.myContext.getApplicationContext().getResources().getStringArray(R.array.itemList);
	}

	public int getMainItem() {
		return mainItem;
	}

	public void setMainItem(int mainItem) {
		this.mainItem = mainItem;
	}

	public int getSecondayItem() {
		return secondayItem;
	}

	public void setSecondayItem(int secondayItem) {
		this.secondayItem = secondayItem;
	}

	@Override
	public String toString() {
		String valores;
		valores = items[this.mainItem] + " + " + items[this.secondayItem];
		return valores;
	}
	
	

}
