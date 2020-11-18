package com.example.a2020_dm_term.DMApp.DB;

import android.provider.BaseColumns;

public class TaskDB {
    public static final class CreateDB implements BaseColumns{
        public static final String TASKNAME = "TASKNAME";
        public static final String PERIOD = "PERIOD";
        public static final String _TABLENAME2 = "TASK";
        public static final String _CREATE2 ="create table if not exists "+_TABLENAME2+"("
                +_ID+" integer primary key autoincrement, "
                +TASKNAME+" text not null , "
                +PERIOD+" text not null); ";
    }
}
