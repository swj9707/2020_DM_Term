package com.example.a2020_dm_term.DMApp.DB;

import android.provider.BaseColumns;

public class TaskDB {
    public static final class CreateDB implements BaseColumns{
        public static final String TYPE = "TYPE";
        public static final String TASKNAME = "TASKNAME";
        public static final String PERIOD = "PERIOD";
        public static final String HOUR = "HOUR";
        public static final String _TABLENAME2 = "TASK";
        public static final String _CREATE2 ="create table if not exists "+_TABLENAME2+"("
                +_ID+" integer primary key autoincrement, "
                +TYPE+" integer not null , "
                +TASKNAME+" text not null , "
                +PERIOD+" integer not null , "
                +HOUR+" integer not null); ";
    }
}
/*
class CustomTextView extends androidx.appcompat.widget.AppCompatTextView {
    int type;
    boolean droppable;
    TaskBlock task;
    CustomTextView(Context context, int type) {
        super(context);
        this.type = type;
        droppable = true;
    }
}
입력 객체는 위의 CustomTextView
필요한 테이블 칼럼
type integer not null <- customTextView.type
title text not null <- customTextView.task.title
period integer not null <- customTextView.task.period
hour integer not null <- customTextView.task.hour
아니 또 왜 되는거지?
 */