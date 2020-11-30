package com.example.a2020_dm_term.DMApp.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class StudyHourDBController {
    private static final String DATABASE_NAME = "InnerDatabase(SQLite).db";
    private static final int DATABASE_VERSION = 1;
    public static SQLiteDatabase mDB;
    private StudyHourDBController.DatabaseHelper mDBHelper;
    private Context mCtx;

    private class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context, String name, CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(StudyHourDB.CreateDB._CREATE1);
        }
        //Table 생성

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + StudyHourDB.CreateDB._TABLENAME3);
            onCreate(db);
        }
    }

    public StudyHourDBController(Context context) {
        this.mCtx = context;
    }

    public StudyHourDBController open() throws SQLException {
        mDBHelper = new StudyHourDBController.DatabaseHelper(mCtx, DATABASE_NAME, null, DATABASE_VERSION);
        mDB = mDBHelper.getWritableDatabase();
        return this;
    }

    public void create() {
        mDBHelper.onCreate(mDB);
        System.out.println("Plan DB Helper create");
    }

    public void close() {
        mDB.close();
    }

    // INSERT 쿼리 처리 메서드
    public long insertColumn(String date, String continuous) {
        ContentValues values = new ContentValues();
        values.put(StudyHourDB.CreateDB.DATE, date);
        values.put(StudyHourDB.CreateDB.CONTINUOUS, continuous);
        return mDB.insert(StudyHourDB.CreateDB._TABLENAME3, null, values);
    }

    // 모든 컬럼을 삭제하는 메서드
    public void deleteAllColumns() {
        mDB.delete(StudyHourDB.CreateDB._TABLENAME3, null, null);
    }

    // DELETE 쿼리 처리 메서드
    public boolean deleteColumn(String date){
        return mDB.delete(StudyHourDB.CreateDB._TABLENAME3, "date="+"\""+date+"\"", null) > 0;
    }

    // SELECT 메서드 -> Cursor 를 사용해서 마치 파일 포인터 사용하듯 테이블에 접근
    public Cursor selectColumns() {
        return mDB.query(StudyHourDB.CreateDB._TABLENAME3, null, null, null, null, null, null);
    }

    public void SelectAll() {
        Cursor c = selectColumns();
        while (c.moveToNext()) {
            String Date = c.getString(0);
            String Continuous = c.getString(1);
            Log.d("StudyHourDBController", "Date:" + Date
                    + " ,Continuous:" + Continuous);
        }
    }

    public int sync(String Today){
        int totalTime = 0;
        Cursor c = selectColumns();
        while(c.moveToNext()){
            String Date = c.getString(0);
            String Continuous = c.getString(1);
            if(!Date.equals(Today)) {
                deleteColumn(Date);
            }
            else {
                totalTime += Integer.parseInt(Continuous);
            }
        }
        return totalTime;
    }
}