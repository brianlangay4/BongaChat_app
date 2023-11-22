package com.georgefalk.moviesnew2018;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MySQLdbHelper extends SQLiteOpenHelper {

    public static final String DATA_BASE_NAME = "movies.db";
    public static final String TABLE_NAME = "movies";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "NAME";
    public static final String COL_3 = "PLOT";
    public static final String COL_4 = "URL";
    public static final String COL_5 = "YEAR";

    public MySQLdbHelper(Context context) {
        super(context, DATA_BASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +  COL_1 + " INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT, PLOT TEXT, URL TEXT, YEAR TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean isInserted(String name, String body, String url, String year) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, name);
        contentValues.put(COL_3, body);
        contentValues.put(COL_4, url);
        contentValues.put(COL_5, year);
        long indicator = db.insert(TABLE_NAME, null, contentValues);
        return (indicator != -1);
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        return cursor;
    }

    public boolean updateMovie(String id, String name, String plot, String url ) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, id);
        contentValues.put(COL_2, name);
        contentValues.put(COL_3, plot);
        contentValues.put(COL_4, url);
        db.update(TABLE_NAME, contentValues,"ID = ?", new String[] { id } );
        return true;
    }

    public Integer deleteMovie(String id) {
        SQLiteDatabase database = this.getWritableDatabase();
        return database.delete(TABLE_NAME, "ID = ?", new String[] { id } );
    }

    public void resetDatabase() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NAME, null,null);
        db.execSQL("DELETE  FROM " + TABLE_NAME );

    }




}
