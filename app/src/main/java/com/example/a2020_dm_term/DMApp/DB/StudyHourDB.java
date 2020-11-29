package com.example.a2020_dm_term.DMApp.DB;

import android.provider.BaseColumns;

public class StudyHourDB {
    public static final class CreateDB implements BaseColumns {
        public static final String DATE = "DATE";
        public static final String CONTINUOUS = "CONTINUOUS";
        public static final String _TABLENAME3 = "STUDYHOUR";
        public static final String _CREATE1 ="create table if not exists "+_TABLENAME3+"("
                +DATE+" text not null , "
                +CONTINUOUS+" integer not null); ";
    }
}
