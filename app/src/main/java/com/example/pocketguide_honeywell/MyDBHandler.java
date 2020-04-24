package com.example.pocketguide_honeywell;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MyDBHandler extends SQLiteOpenHelper {

    // Initialize the variables to be used in the database
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "elGuide.db";
    private static final String TABLE_NAME = "elGuide";
    private static final String COLUMN_ID = "EAN";
    private static final String COLUMN_NAME = "GuideKoodi";
    private static final String COLUMN_DESCRIPTION = "Description";
    private static final String COLUMN_URL = "URL";
    private static final String COLUMN_IMAGEURL = "ImageURL";

    // Initialize the database
    public MyDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    // Override the onCreate to initialize our own database
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" + COLUMN_ID + " TEXT, " + COLUMN_NAME + " TEXT, " + COLUMN_DESCRIPTION + " TEXT, " + COLUMN_URL + " TEXT, " + COLUMN_IMAGEURL + " TEXT)";
        db.execSQL(CREATE_TABLE);
    }

    //Does nothing at the moment, but is mandatory to have
    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {}

    // Load all of the data from a the database
    String loadHandler() {
        String result = "";
        String query = "Select * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            String result_0 = cursor.getString(0);
            String result_1 = cursor.getString(1);
            String result_2 = cursor.getString(2);
            String result_3 = cursor.getString(3);
            String result_4 = cursor.getString(4);
            result += result_0 + " " + result_1 + " " + result_2 + " " + result_3 + " " + result_4 + System.getProperty("line.separator");
        }
        cursor.close();
        return result;
    }

    // Add data to the database
    void addHandler(elGuideDB elguide) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, elguide.getEAN());
        values.put(COLUMN_NAME, elguide.getGuideCode());
        values.put(COLUMN_DESCRIPTION, elguide.getDescription());
        values.put(COLUMN_URL, elguide.getURL());
        values.put(COLUMN_IMAGEURL, elguide.getImageURL());
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_NAME, null, values);
    }

    // Find elGuide code and description with EAN
    elGuideDB findHandler(String EAN) {
        SQLiteDatabase db = this.getWritableDatabase();
        if (EAN == null) {
            EAN = "0";
        } else if (EAN.equals("")) {
            EAN = "0";
        }
        String query = "Select * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = " + EAN + ";";
        Cursor cursor = db.rawQuery(query, null);
        elGuideDB elguide = new elGuideDB();
        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            elguide.setEAN(cursor.getString(0));
            elguide.setGuideCode(cursor.getString(1));
            elguide.setDescription(cursor.getString(2));
            elguide.setURL(cursor.getString(3));
            elguide.setImageURL(cursor.getString(4));
            cursor.close();
        } else {
            elguide = null;
        }
        return elguide;
    }

    elGuideDB guideHandler(String guideCode) {
        SQLiteDatabase db = this.getReadableDatabase();
        if (guideCode == null) {
            guideCode = "0";
        } else if (guideCode.equals("")) {
            guideCode = "0";
        }
        String query = "Select * FROM " + TABLE_NAME + " WHERE " + COLUMN_NAME  + " = " + guideCode + ";";
        Cursor cursor = db.rawQuery(query, null);
        elGuideDB elguide = new elGuideDB();
        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            elguide.setEAN(cursor.getString(0));
            elguide.setGuideCode(cursor.getString(1));
            elguide.setDescription(cursor.getString(2));
            elguide.setURL(cursor.getString(3));
            elguide.setImageURL(cursor.getString(4));
            cursor.close();
        } else {
            elguide = null;
        }
        return elguide;
    }

    // Delete data from the database, currently unused, but for the future
    boolean deleteHandler(String EAN) {
        boolean result = false;
        String query = "Select * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = " + EAN;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        elGuideDB elguide = new elGuideDB();
        if (cursor.moveToFirst()) {
            elguide.setEAN(cursor.getString(0));
            db.delete(TABLE_NAME, COLUMN_ID + "=?",
                    new String[] {
                String.valueOf(elguide.getEAN())
            });
            cursor.close();
            result = true;
        }
        return result;
    }

    // Update data in the database, currently unusued, but for the future
    boolean updateHandler(String EAN, String guideKoodi, String description, String URL, String ImageURL) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues args = new ContentValues();
        args.put(COLUMN_ID, EAN);
        args.put(COLUMN_NAME, guideKoodi);
        args.put(COLUMN_DESCRIPTION, description);
        args.put(COLUMN_URL, URL);
        args.put(COLUMN_IMAGEURL, ImageURL);
        return db.update(TABLE_NAME, args, COLUMN_ID + "=" + EAN, null) > 0;
    }

    // Close the database so it can save and not use processing power and no danger of data loss
    void closeDB() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.close();
    }
}
