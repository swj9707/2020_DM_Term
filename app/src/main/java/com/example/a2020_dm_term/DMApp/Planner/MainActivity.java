package com.example.a2020_dm_term.DMApp.Planner;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.a2020_dm_term.R;
import com.example.a2020_dm_term.DMApp.DB.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;

/*
실제 플래너가 실행되는 공간.
* */

public class MainActivity extends AppCompatActivity {
    Context context;
    TextView todayView;
    Button weeklyPlanButton;
    Button restrictModeButton;
    Intent restrictIntent;
    Intent weeklyIntent;
    RelativeLayout timeTable;

    public static PlanDBController plnDBC;
    public static TaskDBController tskDBC;
    public static StudyHourDBController sHrDBC;
    private Date date;
    private int ContinuousTime;

    ArrayList<CustomTextView> taskList = new ArrayList<CustomTextView>();

    int CELL_WIDTH;
    int CELL_HEIGHT;

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

        todayView =  (TextView)findViewById(R.id.today);
        weeklyPlanButton = (Button)findViewById(R.id.weeklyPlanButton);
        restrictModeButton = (Button)findViewById(R.id.restrictModeButton);
        //xml 상에 있는 요소들 불러오기

        restrictIntent = new Intent(getApplicationContext(), RestrictActivity.class);
        weeklyIntent = new Intent(getApplicationContext(), WeeklyPlannerActivity.class);
        //인텐트 객체들 선언

        weeklyButtonListener wButtonListener = new weeklyButtonListener();
        restrictButtonListener rButtonListener = new restrictButtonListener();
        weeklyPlanButton.setOnClickListener(wButtonListener);
        restrictModeButton.setOnClickListener(rButtonListener);
        //버튼 리스터 객체 선언 및 설정

        date = new Date();
        //final String today = date.toString();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy년MM월dd일");
        String today = sdf.format(date);
        todayView.setText(today);

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
        //일단 열어보고 없으면 만들고 close해줘야함
        //close 하는 이유는 -> 계속 열어둔다 해도 이 앱에선 뭐 상관없겠지만
        //그래도 혹시나 모르는 에러를 방지하기 위함
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
        int hour = ContinuousTime / 3600;
        int minute = (ContinuousTime % 3600) / 60;
        int second = (ContinuousTime % 3600) % 60;
        String time = hour + ":" + minute + ":" + second;
        //그날 공부 총 지속 시간을 계산 해 내는 코드
        //방법은 간단하니 생략하도록 하겠음
        Log.d("MainActivity","ContinuousTime : "+time);

        today = (TextView) findViewById(R.id.today);
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


        //시간표 생성
        timeTable = findViewById(R.id.main_time_table);
        CustomTextView[] dummies = new CustomTextView[5];

        dummies[0] = new CustomTextView(this, 1);
        dummies[0].task = new TaskBlock();
        dummies[0].task.period = 3;
        dummies[0].task.day = 2;
        dummies[0].task.hour = 0;
        dummies[0].task.title = "dummy1";

        dummies[1] = new CustomTextView(this, 1);
        dummies[1].task = new TaskBlock();
        dummies[1].task.period = 1;
        dummies[1].task.day = 2;
        dummies[1].task.hour = 5;
        dummies[1].task.title = "dummy2";

        dummies[2] = new CustomTextView(this, 1);
        dummies[2].task = new TaskBlock();
        dummies[2].task.period = 5;
        dummies[2].task.day = 0;
        dummies[2].task.hour = 0;
        dummies[2].task.title = "dummy3";

        dummies[3] = new CustomTextView(this, 1);
        dummies[3].task = new TaskBlock();
        dummies[3].task.period = 2;
        dummies[3].task.day = 0;
        dummies[3].task.hour = 5;
        dummies[3].task.title = "dummy4";

        dummies[4] = new CustomTextView(this, 1);
        dummies[4].task = new TaskBlock();
        dummies[4].task.period = 1;
        dummies[4].task.day = 5;
        dummies[4].task.hour = 2;
        dummies[4].task.title = "dummy5";

        for (CustomTextView item : dummies)
            taskList.add(item);

        CELL_WIDTH = (dp2px(390) - dp2px(20) - dp2px(1) * 7) / 7;
        CELL_HEIGHT = dp2px(70);

        int idx = 0;
        for (String day : new String[]{"일", "월", "화", "수", "목", "금", "토"}) {
            RelativeLayout.LayoutParams day_params = new RelativeLayout.LayoutParams(CELL_WIDTH, RelativeLayout.LayoutParams.WRAP_CONTENT);
            day_params.setMargins(dp2px(1) * (idx + 1) + CELL_WIDTH * idx, 0, 0, 0);
            day_params.addRule(RelativeLayout.RIGHT_OF, R.id.blank);
            day_params.addRule(RelativeLayout.ALIGN_TOP, R.id.blank);
            TextView days = new TextView(this);
            days.setText(day);
            days.setBackgroundColor(Color.WHITE);
            days.setLayoutParams(day_params);
            days.setGravity(Gravity.CENTER);
            timeTable.addView(days);
            idx++;
        }

        for (int hour = 1; hour <= 24; hour++) {
            TextView rowIndex = new TextView(this);
            rowIndex.setText(Integer.toString(hour - 1));
            rowIndex.setGravity(Gravity.CENTER);
            rowIndex.setBackgroundColor(Color.WHITE);
            rowIndex.setId(hour * 10);

            RelativeLayout.LayoutParams idx_params = new RelativeLayout.LayoutParams(dp2px(20), CELL_HEIGHT);

            idx_params.setMargins(0, dp2px(1), 0, 0);
            if (hour == 1)
                idx_params.addRule(RelativeLayout.BELOW, R.id.blank);
            else
                idx_params.addRule(RelativeLayout.BELOW, (hour - 1) * 10);

            rowIndex.setLayoutParams(new RelativeLayout.LayoutParams(idx_params));//w, h
            timeTable.addView(rowIndex);

            for (int day = 1; day <= 7; day++) {
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(CELL_WIDTH, CELL_HEIGHT);
                params.addRule(RelativeLayout.RIGHT_OF, hour * 10);
                params.addRule(RelativeLayout.ALIGN_TOP, hour * 10);
                CustomTextView cell = new CustomTextView(this, 1);
                params.setMargins(dp2px(1) * day + CELL_WIDTH * (day - 1), 0, 0, dp2px(1));

                cell.setId(hour * 10 + day);
                cell.setGravity(Gravity.CENTER);
                cell.setBackgroundColor(Color.WHITE);
                cell.setLayoutParams(params);

                timeTable.addView(cell);
            }
        }

        for (CustomTextView item : dummies) {
            int id = (item.task.hour + 1) * 10 + (item.task.day + 1);
            mergeCells(item.task.period, id);
            CustomTextView cell = (CustomTextView) findViewById(id);
            cell.setText(item.task.title);
        }
    }

    public void mergeCells(int period, int id) {
        System.out.println(id);
        CustomTextView cell = (CustomTextView) findViewById(id);
        for (int i = 1; i <= period - 1; i++)
            timeTable.removeView(findViewById(id + i * 10));

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) cell.getLayoutParams();
        params.height = period * CELL_HEIGHT + (period - 1) * dp2px(1);
        cell.setLayoutParams(params);
    }

    public int dp2px(int dp) {
        float scale = getResources().getDisplayMetrics().density;
        int px = (int) (dp * scale + 0.5f);
        return px;
    }
}
