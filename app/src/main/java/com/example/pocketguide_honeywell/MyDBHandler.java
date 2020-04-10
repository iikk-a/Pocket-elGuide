package com.example.pocketguide_honeywell;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDBHandler extends SQLiteOpenHelper {
    //information of database
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "elGuide.db";
    public static final String TABLE_NAME = "elGuide";
    public static final String COLUMN_ID = "EAN";
    public static final String COLUMN_NAME = "GuideKoodi";
    public static final String COLUMN_DESCRIPTION = "Description";
    //initialize the database
    public MyDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
        SQLiteDatabase db = this.getWritableDatabase();
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" + COLUMN_ID + " TEXT, " + COLUMN_NAME + " TEXT, " + COLUMN_DESCRIPTION + " TEXT )";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {}


    public String loadHandler() {
        String result = "";
        String query = "Select * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            String result_0 = cursor.getString(0);
            String result_1 = cursor.getString(1);
            result += result_0 + " " + result_1 + System.getProperty("line.separator");
        }
        cursor.close();
        return result;
    }

    public void addHandler(elGuideDB elguide) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, elguide.getEAN());
        values.put(COLUMN_NAME, elguide.getGuideKoodi());
        values.put(COLUMN_DESCRIPTION, elguide.getDescription());
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_NAME, null, values);
    }

    public elGuideDB findHandler(String EAN) {
        SQLiteDatabase db = this.getWritableDatabase();
        if (EAN == null) {
            EAN = "0";
        }
        String query = "Select * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = " + EAN + ";";
        Cursor cursor = db.rawQuery(query, null);
        elGuideDB elguide = new elGuideDB();
        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            elguide.setEAN(cursor.getString(0));
            elguide.setGuideKoodi(cursor.getString(1));
            cursor.close();
        } else {
            elguide = null;
        }
        return elguide;
    }

    public boolean deleteHandler(String EAN) {
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

    public boolean updateHandler(String EAN, String guideKoodi) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues args = new ContentValues();
        args.put(COLUMN_ID, EAN);
        args.put(COLUMN_NAME, guideKoodi);
        return db.update(TABLE_NAME, args, COLUMN_ID + "=" + EAN, null) > 0;
    }
    public void closeDB() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.close();
    }
}
