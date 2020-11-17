package com.example.a2020_dm_term.DMApp.DB;

import com.example.a2020_dm_term.DMApp.DB.PlanDB;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.*;
import android.database.sqlite.SQLiteOpenHelper;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class TaskDBController {
    private static final String DATABASE_NAME = "InnerDatabase(SQLite).db";
    private static final int DATABASE_VERSION = 1;
    public static SQLiteDatabase mDB;
    private TaskDBController.DatabaseHelper mDBHelper;
    private Context mCtx;

    private class DatabaseHelper extends SQLiteOpenHelper{

        public DatabaseHelper(Context context, String name, CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db){
            db.execSQL(TaskDB.CreateDB._CREATE2);
        }
        //Table 생성

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS "+ TaskDB.CreateDB._TABLENAME2);
            onCreate(db);
        }
    }
    public TaskDBController(Context context){this.mCtx = context;}
    public TaskDBController open() throws SQLException {
        mDBHelper = new TaskDBController.DatabaseHelper(mCtx, DATABASE_NAME, null, DATABASE_VERSION);
        mDB = mDBHelper.getWritableDatabase();
        return this;
    }
    public void create(){
        mDBHelper.onCreate(mDB);
        System.out.println("Task DB Helper create");
    }
    public void close(){mDB.close();}

    // Insert DB
    public long insertColumn(String taskname, String continuousTime){
        ContentValues values = new ContentValues();
        values.put(TaskDB.CreateDB.TASKNAME, taskname);
        values.put(TaskDB.CreateDB.CONTIUOUSTIME,continuousTime);
        /*
        Cursor c = mDB.query(PlanDB.CreateDB._TABLENAME1, null, null, null, null, null, null);
        while (c.moveToNext()){
            String Name = c.getString(2);
            if( c.getString(1).equals(Name) && c.getString(2).equals(contents)){
                Log.d("", "Ingredient:"+contents+"가 이미 존재합니다.");
                return 0;
            }
        }*/
        //예전에 만들었던 코드에서 썼던 중복 확인 메서드. 지금은 필요없지만 나중에 혹시 필요하면 고쳐쓰게 냅둠

        return mDB.insert(TaskDB.CreateDB._TABLENAME2, null, values);
    }

    // Update DB
    public boolean updateColumn(long id, String taskname, String continuousTime){
        ContentValues values = new ContentValues();
        values.put(TaskDB.CreateDB.TASKNAME, taskname);
        values.put(TaskDB.CreateDB.CONTIUOUSTIME,continuousTime);
        /*
        Cursor c = mDB.query(PlanDB.CreateDB._TABLENAME1, null, null, null, null, null, null);
        while(c.moveToNext()){
            String Name = c.getString(1);
            if(c.getString(1).equals(name)){
                Log.d("","Name:"+Name+"가 이미 존재합니다.");
                return false;
            }
        }*/

        return mDB.update(TaskDB.CreateDB._TABLENAME2, values, "_id=" + id, null) > 0;
    }

    // Delete All
    public void deleteAllColumns() {
        mDB.delete(TaskDB.CreateDB._TABLENAME2, null, null);
    }

    // Delete DB
    public boolean deleteColumn(long id){
        return mDB.delete(TaskDB.CreateDB._TABLENAME2, "_id="+id, null) > 0;
    }
    // Select DB
    public Cursor selectColumns(){
        return mDB.query(TaskDB.CreateDB._TABLENAME2, null, null, null, null, null, null);
    }

    public void SelectAll(){
        Cursor c = selectColumns();
        while(c.moveToNext()){
            int _id = c.getInt(0);
            String TaskName = c.getString(1);
            String ContinuousTime = c.getString(2);
            Log.d("","_id:"+_id+",TaskName:"+TaskName
                    +",ContinuousTIme:"+ContinuousTime);
        }
    }

    public Cursor sortColumn(String sort){
        Cursor c = mDB.rawQuery( "SELECT * FROM items ORDER BY " + sort + ";", null);
        return c;
    }
}