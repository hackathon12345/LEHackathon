package com.example.project24;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class MyCursorAdapter  extends CursorAdapter{

	public static final MyCursorAdapter defaultImplementation(Activity ctx, RssDataBase db) {
		Cursor c = db.getWritableDatabase().query(
				RssDataBase.TABLE_NAME, null, null, null, null, null, null);
		return new MyCursorAdapter(ctx, c);

	}
	private Activity ctx;
	
	public MyCursorAdapter(Activity context, Cursor c){
		super(context, c, 0);
		this.ctx = context;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		String title = cursor.getString(cursor.getColumnIndex(RssDataBase.TITLE));
		TextView tv = (TextView) view;
		tv.setText(title);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater inflater = ctx.getLayoutInflater();
		TextView tv = (TextView) inflater.inflate(android.R.layout.simple_list_item_1, null);
		String ID= cursor.getString(cursor.getColumnIndex(RssDataBase.ID));
		tv.setText(ID);
		return tv;		
	}
	@Override
	public long getItemId(int position){
		return super.getItemId(position);
	}	
}
