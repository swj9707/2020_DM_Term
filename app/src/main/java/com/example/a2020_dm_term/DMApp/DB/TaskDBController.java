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

    // INSERT 쿼리 처리 메서드
    public long insertColumn(int Type, String Title, int Droppable, int Period, int Hour, int Day){
        ContentValues values = new ContentValues();
        values.put(TaskDB.CreateDB.TYPE, Type);
        values.put(TaskDB.CreateDB.TITLE, Title);
        values.put(TaskDB.CreateDB.DROPPABLE, Droppable);
        values.put(TaskDB.CreateDB.PERIOD, Period);
        values.put(TaskDB.CreateDB.HOUR, Hour);
        values.put(TaskDB.CreateDB.DAY, Day);
        return mDB.insert(TaskDB.CreateDB._TABLENAME2, null, values);
    }

    // 모든 컬럼을 삭제하는 메서드
    public void deleteAllColumns() {
        mDB.delete(TaskDB.CreateDB._TABLENAME2, null, null);
    }

    // DELETE 쿼리 처리 메서드
    public boolean deleteColumn(int Type, String Title, int Droppable, int Period, int Hour, int Day){
        return mDB.delete(TaskDB.CreateDB._TABLENAME2,
                "TYPE="+Type+"TITLE="+Title+",DROPPABLE="+Droppable+",PERIOD="+Period+",HOUR="+Hour+",DAY="+Day,
                null) > 0;
    }
    // SELECT 메서드 -> Cursor 를 사용해서 마치 파일 포인터 사용하듯 테이블에 접근
    public Cursor selectColumns(){
        return mDB.query(TaskDB.CreateDB._TABLENAME2, null, null, null, null, null, null);
    }

    public void SelectAll(){
        Cursor c = selectColumns();
        while(c.moveToNext()){
            int Type = c.getInt(0);
            String Title = c.getString(1);
            int Droppable = c.getInt(2);
            int Period = c.getInt(3);
            int Hour = c.getInt(4);
            int Day = c.getInt(5);
            Log.d("PlanDBController","Type:"+Type
                    +" ,Title:"+Title+" ,Droppable:"+Droppable+" ,Period:"+Period+" ,Hour:"+Hour+" ,Day:"+Day);
        }
    }
}