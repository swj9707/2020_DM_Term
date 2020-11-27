package com.example.a2020_dm_term.DMApp.Planner;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.a2020_dm_term.R;
import com.example.a2020_dm_term.DMApp.DB.*;

import java.text.SimpleDateFormat;
import java.util.Date;

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
    public static PlanDBController plnDBC;
    public static TaskDBController tskDBC;
    public static StudyHourDBController sHrDBC;
    private Date date;
    private int ContinuousTime;

    class weeklyButtonListener implements View.OnClickListener{
        public void onClick(View v){
            //주간 계획으로 넘어감
            startActivity(weeklyIntent);
        }
    }
    class restrictButtonListener implements View.OnClickListener{
        public void onClick(View v){
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
    }
}
