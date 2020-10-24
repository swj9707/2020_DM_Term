package com.example.a2020_dm_term.DMApp.DB;


import android.provider.BaseColumns;

public class PlanDB {
    public static final class CreateDB implements BaseColumns {
        public static final String NAME = "NAME";
        public static final String CONTENTS = "CONTENTS";
        public static final String STARTDATE = "STARTDATE";
        public static final String ENDDATE = "ENDDATE";
        public static final String _TABLENAME1 = "ingredients";
        public static final String _CREATE1 ="create table if not exists "+_TABLENAME1+"("
                +_ID+" integer primary key autoincrement, "
                +NAME+" text not null , "
                +CONTENTS+" text not null , "
                +STARTDATE+" text not null , "
                +ENDDATE+" text not null); ";
    }

}
