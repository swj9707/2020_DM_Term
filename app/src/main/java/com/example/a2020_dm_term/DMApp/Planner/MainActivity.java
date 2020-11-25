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
    Button restrictModeButton;
    Intent restrictIntent;
    Intent weeklyIntent;
    public static PlanDBController plnDBC;
    public static TaskDBController tskDBC;
    public static StudyHourDBController sHrDBC;


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
        plnDBC.close();
        tskDBC.close();
        sHrDBC.close();
        //테스트용으로 전체 데이터베이스들 전부 콘솔에 띄워보는 코드

        today =  (TextView)findViewById(R.id.today);
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


    }
}
