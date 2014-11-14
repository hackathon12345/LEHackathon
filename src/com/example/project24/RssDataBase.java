package com.example.project24;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class RssDataBase extends SQLiteOpenHelper {
	
	public static final int DATABASE_VERSION = 1;
	public static final String DATABSE_NAME = "news_db";
	public static final String TABLE_NAME = "RssNewsReader";
	public static final String ID = "id";
	public static final String TITLE ="title";
	public static final String DESCRIPTION = "description";
	public static final String LINK ="link";

	public static final String PUBDATE ="pubdate";
		
private static final String CREATE_TABLE = "CREATE NEWS TABLE" + TABLE_NAME + "("+ 
		ID + "INTERGER PRIMARY KEY AUTOINCREMENT," +
		TITLE + "TEXT," + 
		DESCRIPTION + "TEXT,"+
		LINK + "TEXT," +
		PUBDATE +"TEXT" +")" ;
		
	public RssDataBase(Context context) {
		super(context, DATABSE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP EXISTING TABLE ");

		this.onCreate(db);	
	}
	public void insertNewsSite(NewsSite site){
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put(TITLE, site.getTitle());
		cv.put(DESCRIPTION, site.getDescription());
		cv.put(LINK, site.getLink());
		cv.put(PUBDATE, site.getPubdate());
		db.insert(TABLE_NAME, null, cv);
		updateSite(site);
		db.close();
	}

	private int updateSite(NewsSite site) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv =  new ContentValues();
		cv.put(TITLE, site.getTitle());
		cv.put(DESCRIPTION, site.getDescription());
		cv.put(LINK, site.getLink());
		cv.put(PUBDATE, site.getPubdate());
		
		int update = db.update(TABLE_NAME, cv, ID  + " = ?", 
				new String[]{String.valueOf(site.getId())});
		db.close();
		return update;	
	}
	public List<NewsSite> getSite(){
		List<NewsSite> newsList = new ArrayList<NewsSite>();
		String selectQuery = "SELECT * FROM " + TABLE_NAME;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		
		if(cursor.moveToFirst()){
			do{
				NewsSite ns = new NewsSite();
				ns.setId(Integer.parseInt(cursor.getString(0)));
				ns.setTitle(cursor.getString(1));
				ns.setDescription(cursor.getString(2));
				ns.setLink(cursor.getString(3));
				ns.setPubdate(cursor.getString(4));
				newsList.add(ns);
			}while(cursor.moveToNext());
		}
		Log.d("getSite()", newsList.toString());
		//cursor.close();
		db.close();
		return newsList;
		
	}
	public NewsSite getSite(int id){
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, new String[] {
			ID, TITLE, DESCRIPTION, LINK, PUBDATE}, 
			ID + "?" , new String[] {String.valueOf(id) },
			null, null, null, null);
		if (cursor!= null)
			cursor.moveToFirst();
		NewsSite ns = new NewsSite();
		ns.setId(Integer.parseInt(cursor.getString(0)));
		ns.setTitle(cursor.getString(1));
		ns.setDescription(cursor.getString(2));
		ns.setLink(cursor.getString(3));
		ns.setPubdate(cursor.getString(4));
		
		Log.d("get news("+id+")", ns.toString()); // added today
		
		///cursor.close();
		db.close();///
		return ns;
		
	}
	public void deleteNewsSite(NewsSite site){
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_NAME, ID +"=?", new String[]{String.valueOf(site.getId()) });
		db.close();
		Log.d("deleteNews", site.toString());
	}
	

}
