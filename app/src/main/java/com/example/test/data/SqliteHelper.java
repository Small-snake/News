package com.example.test.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SqliteHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 2;
    private final static  String DB_NAME = "snaker.db";


    public SqliteHelper(Context context){
        super(context,DB_NAME,null,DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE USER(_id INTEGER PRIMARY KEY AUTOINCREMENT,account VARCHAR(20),password VARCHAR(20))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
    }
}