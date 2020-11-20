package com.example.a2020_dm_term.DMApp.Planner;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.a2020_dm_term.R;
import com.example.a2020_dm_term.DMApp.DB.*;

/*
실제 플래너가 실행되는 공간.
* */

public class MainActivity extends AppCompatActivity {
    Context context;
    TextView today;
    Button weeklyPlanButton;
    Button monthlyPlanButton;
    Button restrictModeButton;
    Intent restrictIntent;
    Intent weeklyIntent;
    public static PlanDBController plnDBC;
    public static TaskDBController tskDBC;


    class weeklyButtonListener implements View.OnClickListener{
        public void onClick(View v){
            //주간 계획으로 넘어감
            startActivity(weeklyIntent);
        }
    }
    class monthlyButtonListener implements View.OnClickListener{
        public void onClick(View v){
            //월간 계획으로 넘어감
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

        plnDBC = new PlanDBController(this);
        tskDBC = new TaskDBController(this);
        plnDBC.open();
        plnDBC.create();

        tskDBC.open();
        tskDBC.create();

        //데이터베이스 컨트롤러 불러온 다음 데이터베이스 세팅하기
        //Open -> Create -> Close 사이클인 이유는
        //create -> open 같은 경우는 에러 남
        //일단 열어보고 없으면 만들고 close해줘야함
        //close 하는 이유는 -> Connection Pool 문제 보단
        //Producer Consumer 문제 때문
        plnDBC.SelectAll();
        tskDBC.SelectAll();
        plnDBC.close();
        tskDBC.close();
        //테스트용으로 전체 데이터베이스들 전부 콘솔에 띄워보는 코드

        today =  (TextView)findViewById(R.id.today);
        weeklyPlanButton = (Button)findViewById(R.id.weeklyPlanButton);
        monthlyPlanButton = (Button)findViewById(R.id.monthlyPlanButton);
        restrictModeButton = (Button)findViewById(R.id.restrictModeButton);
        //xml 상에 있는 요소들 불러오기

        restrictIntent = new Intent(getApplicationContext(), RestrictActivity.class);
        weeklyIntent = new Intent(getApplicationContext(), WeeklyPlannerActivity.class);
        //인텐트 객체들 선언

        weeklyButtonListener wButtonListener = new weeklyButtonListener();
        monthlyButtonListener mButtonListener = new monthlyButtonListener();
        restrictButtonListener rButtonListener = new restrictButtonListener();
        weeklyPlanButton.setOnClickListener(wButtonListener);
        monthlyPlanButton.setOnClickListener(mButtonListener);
        restrictModeButton.setOnClickListener(rButtonListener);
        //버튼 리스터 객체 선언 및 설정


    }
}
