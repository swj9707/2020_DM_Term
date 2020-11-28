package com.example.a2020_dm_term.DMApp.Planner;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import com.example.a2020_dm_term.R;
import com.example.a2020_dm_term.DMApp.DB.*;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.Locale;

/*
실제 플래너가 실행되는 공간.
* */

public class MainActivity extends AppCompatActivity {
    Context context;
    TextView todayView;
    TextView studyTimeView;
    Button weeklyPlanButton;
    Button restrictModeButton;
    Intent restrictIntent;
    Intent weeklyIntent;
    RelativeLayout timeTable;
    NestedScrollView nestedScrollView;

    public static PlanDBController plnDBC;
    public static TaskDBController tskDBC;
    public static StudyHourDBController sHrDBC;
    private Date date;
    private int ContinuousTime;

    ArrayList<CustomTextView> taskList = new ArrayList<CustomTextView>();

    int CELL_SIZE;

    class weeklyButtonListener implements View.OnClickListener {
        public void onClick(View v) {
            //주간 계획으로 넘어감
            startActivity(weeklyIntent);
        }
    }

    class restrictButtonListener implements View.OnClickListener {
        public void onClick(View v) {
            //제한모드로 넘어감
            startActivity(restrictIntent);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_main);
        //레이아웃 불러오기

        todayView = (TextView) findViewById(R.id.today);
        studyTimeView = (TextView) findViewById(R.id.studyTime);
        weeklyPlanButton = (Button) findViewById(R.id.weeklyPlanButton);
        restrictModeButton = (Button) findViewById(R.id.restrictModeButton);
        //xml 상에 있는 요소들 불러오기

        restrictIntent = new Intent(getApplicationContext(), RestrictActivity.class);
        weeklyIntent = new Intent(getApplicationContext(), WeeklyPlannerActivity.class);
        //인텐트 객체들 선언

        weeklyButtonListener wButtonListener = new weeklyButtonListener();
        restrictButtonListener rButtonListener = new restrictButtonListener();
        weeklyPlanButton.setOnClickListener(wButtonListener);
        restrictModeButton.setOnClickListener(rButtonListener);
        //버튼 리스터 객체 선언 및 설정

        plnDBC = new PlanDBController(this);
        tskDBC = new TaskDBController(this);
        sHrDBC = new StudyHourDBController(this);

        plnDBC.open();
        plnDBC.create();
        tskDBC.open();
        tskDBC.create();
        sHrDBC.open();
        sHrDBC.create();
        //데이터베이스 컨트롤러 불러온 다음 데이터베이스 세팅하기
        //Open -> Create -> Close 사이클인 이유는
        //create -> open 같은 경우는 에러 남

        SyncDB_Date();//DB와 날짜, 공부 시간 등을 동기화 해주는 메서드

        mkTimeTable();
    }

    @Override
    public void onResume() {
        super.onResume();
        SyncDB_Date();
        for (CustomTextView item : taskList) {
            int id = (item.task.hour + 1) * 10 + (item.task.day + 1);
            mergeCells(item.task.period, id);
            CustomTextView cell = (CustomTextView) findViewById(id);
            cell.setText(item.task.title);
        }
    }

    public void mkTimeTable() {
        //시간표 생성
        timeTable = findViewById(R.id.main_time_table);


        Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        CELL_SIZE = (width - dp2px(1) * 7 - dp2px(10) * 2 - dp2px(20)) / 7;

        int nested_width = width - dp2px(10) * 2;
        int nested_height = (width - dp2px(10) * 2) / 8 * 7;
        nestedScrollView = (NestedScrollView) findViewById(R.id.nested_scroll);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(nested_width, nested_height);
        nestedScrollView.setLayoutParams(layoutParams);


        int idx = 0;
        for (String day : new String[]{"일", "월", "화", "수", "목", "금", "토"}) {
            RelativeLayout.LayoutParams day_params = new RelativeLayout.LayoutParams(CELL_SIZE, RelativeLayout.LayoutParams.WRAP_CONTENT);
            day_params.setMargins(dp2px(1) * (idx + 1) + CELL_SIZE * idx, 0, 0, 0);
            day_params.addRule(RelativeLayout.RIGHT_OF, R.id.blank);
            day_params.addRule(RelativeLayout.ALIGN_TOP, R.id.blank);
            TextView days = new TextView(this);
            days.setText(day);
            days.setBackgroundColor(Color.parseColor("#3C3F41"));
            days.setLayoutParams(day_params);
            days.setGravity(Gravity.CENTER);
            days.setTextColor(Color.parseColor("#BEBEBE"));
            timeTable.addView(days);
            idx++;
        }

        for (int hour = 1; hour <= 24; hour++) {
            TextView rowIndex = new TextView(this);
            rowIndex.setText(Integer.toString(hour - 1));
            rowIndex.setGravity(Gravity.CENTER);
            rowIndex.setBackgroundColor(Color.parseColor("#3C3F41"));
            rowIndex.setId(hour * 10);
            rowIndex.setTextColor(Color.parseColor("#BEBEBE"));

            RelativeLayout.LayoutParams idx_params = new RelativeLayout.LayoutParams(dp2px(20), CELL_SIZE);

            idx_params.setMargins(0, dp2px(1), 0, 0);
            if (hour == 1)
                idx_params.addRule(RelativeLayout.BELOW, R.id.blank);
            else
                idx_params.addRule(RelativeLayout.BELOW, (hour - 1) * 10);

            rowIndex.setLayoutParams(new RelativeLayout.LayoutParams(idx_params));//w, h
            timeTable.addView(rowIndex);

            for (int day = 1; day <= 7; day++) {
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(CELL_SIZE, CELL_SIZE);
                params.addRule(RelativeLayout.RIGHT_OF, hour * 10);
                params.addRule(RelativeLayout.ALIGN_TOP, hour * 10);
                CustomTextView cell = new CustomTextView(this, 1);
                params.setMargins(dp2px(1) * day + CELL_SIZE * (day - 1), 0, 0, dp2px(1));

                cell.setId(hour * 10 + day);
                cell.setGravity(Gravity.CENTER);
                cell.setBackgroundColor(Color.parseColor("#505355"));
                cell.setLayoutParams(params);
                cell.setTextColor(Color.parseColor("#BEBEBE"));

                timeTable.addView(cell);
            }
        }
    }

    public void SyncDB_Date() {
        date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일 (E)", Locale.KOREAN);
        String today = sdf.format(date);
        todayView.setText(today);

        plnDBC.SelectAll();
        tskDBC.SelectAll();
        sHrDBC.SelectAll();
        //테스트용으로 전체 데이터베이스들 전부 콘솔에 띄워보는 코드
        ContinuousTime = sHrDBC.sync(today);
        /*
        Sync 메서드 설명
        매 날짜가 변할 때 마다 그날 공부 지속 시간을 초기화 해 줄 필요가 있기 때문에
        초기화 해 주는 김에 그날 공부한 총 시간까지 계산해서 Return 해 주는 메서드
        * */
        String time = CalculateTime(ContinuousTime);
        //그날 공부 총 지속 시간을 계산 해 내는 코드
        //방법은 간단하니 생략하도록 하겠음
        Log.d("MainActivity", "ContinuousTime : " + time);
        studyTimeView.setText("오늘 공부한 시간 : "+time);
        downloadPlnDB();
    }

    public String CalculateTime(int ContinuousTime) {
        int Hr = ContinuousTime / 3600;
        int Min = (ContinuousTime % 3600) / 60;
        int Sec = (ContinuousTime % 3600) % 60;
        return Hr + ":" + Min + ":" + Sec;
    }

    public void mergeCells(int period, int id) {
        CustomTextView cell = (CustomTextView) findViewById(id);
        for (int i = 1; i <= period - 1; i++)
            timeTable.removeView(findViewById(id + i * 10));

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) cell.getLayoutParams();
        params.height = period * CELL_SIZE + (period - 1) * dp2px(1);
        cell.setLayoutParams(params);
    }

    public int dp2px(int dp) {
        float scale = getResources().getDisplayMetrics().density;
        int px = (int) (dp * scale + 0.5f);
        return px;
    }

    public void downloadPlnDB() {
        Cursor c = plnDBC.selectColumns();
        while (c.moveToNext()) {
            int Type = c.getInt(1);
            String Title = c.getString(2);
            int Droppable = c.getInt(3);
            int Period = c.getInt(4);
            int Hour = c.getInt(5);
            int Day = c.getInt(6);
            Log.d("DownLoadPlanDB", "Type:" + Type
                    + " ,Title:" + Title + " ,Droppable:" + Droppable + " ,Period:" + Period + " ,Hour:" + Hour + " ,Day:" + Day);
            CustomTextView element = new CustomTextView(this, Type);
            element.task.setTitle(Title);
            element.setDroppable(Droppable);
            element.task.setPeriod(Period);
            element.task.setHour(Hour);
            element.task.setDay(Day);
            taskList.add(element);
        }
    }
}