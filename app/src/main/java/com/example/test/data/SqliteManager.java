package com.example.test.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class SqliteManager {
    private SqliteHelper sqliteHelper = null;
    private SQLiteDatabase sqLiteDatabase = null;
    Context context;
    public SqliteManager(Context context){
        this.context = context;
        sqliteHelper = new SqliteHelper(context);

    }

    //打开数据库
    public void openWrite(){
        this.sqLiteDatabase = sqliteHelper.getWritableDatabase();
    }
    public void openRead(){
        this.sqLiteDatabase = sqliteHelper.getReadableDatabase();
    }

    //关闭数据库
    public void closeDB(){
       
        if(sqLiteDatabase != null){
            sqLiteDatabase.close();
        }
        if(sqLiteDatabase != null){
            sqLiteDatabase.close();
        }
    }

    //插入
    public boolean insert(String table, String nullColumnHack, ContentValues values){
        boolean f = false;
        openWrite();
        long id = sqLiteDatabase.insert(table,nullColumnHack,values);
        if(id > 0) f = true;
        closeDB();
        return f;
    }
    //删除
    public boolean delete(String table, String whereClause, String[] whereArgs){
        boolean f = false;
        openWrite();
        long id = sqLiteDatabase.delete(table,whereClause,whereArgs);
        if(id > 0) f = true;
        closeDB();
        return f;
    }
    //修改
    public boolean update(String table, ContentValues values, String whereClause, String[] whereArgs){
        boolean f = false;
        openWrite();
        long id = sqLiteDatabase.update(table,values,whereClause,whereArgs);
        if(id > 0) f = true;
        closeDB();
        return f;
    }

    //查询 simple version
    public String select(String table, String where, String whereNeed){
        //where -> where ** = **
        //whereNeed -> 需要查询的属性
        String anser = null;
        openRead();

        Cursor cursor = sqLiteDatabase.rawQuery("select * from "+ table +" where account = '"+ where + "'",null);
        if(cursor.getCount() != 0){
            while(cursor.moveToNext()){
                anser = cursor.getString(cursor.getColumnIndex("password"));
            }
        }
        cursor.close();
        closeDB();
        return anser;
    }

    public void dropTable(){
        openWrite();
        sqLiteDatabase.execSQL("drop table USER");
        sqLiteDatabase.close();
    }


}
