package com.example.a2020_dm_term.DMApp.DB;


import android.provider.BaseColumns;

public class PlanDB {
    public static final class CreateDB implements BaseColumns {
        public static final String TYPE = "TYPE";
        public static final String TITLE = "TITLE";
        public static final String DROPPABLE = "DROPPABLE";
        public static final String PERIOD = "PERIOD";
        public static final String HOUR = "HOUR";
        public static final String DAY = "DAY";
        public static final String _TABLENAME1 = "PLAN";
        public static final String _CREATE1 ="create table if not exists "+_TABLENAME1+"("
                +TYPE+" integer not null , "
                +TITLE+" text not null , "
                +DROPPABLE+" integer not null , "
                +PERIOD+" integer not null , "
                +HOUR+" integer not null , "
                +DAY+" integer not null); ";
    }
}
