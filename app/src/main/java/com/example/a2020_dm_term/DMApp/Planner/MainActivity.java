package com.example.a2020_dm_term.DMApp.Planner;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.a2020_dm_term.R;

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

        today =  (TextView)findViewById(R.id.today);
        weeklyPlanButton = (Button)findViewById(R.id.weeklyPlanButton);
        monthlyPlanButton = (Button)findViewById(R.id.monthlyPlanButton);
        restrictModeButton = (Button)findViewById(R.id.restrictModeButton);

        restrictIntent = new Intent(this, RestrictActivity.class);
        weeklyIntent = new Intent(this, WeeklyPlannerActivity.class);

        //System.out.println("Test Commit WJ");

        weeklyButtonListener wButtonListener = new weeklyButtonListener();
        monthlyButtonListener mButtonListener = new monthlyButtonListener();
        restrictButtonListener rButtonListener = new restrictButtonListener();

        weeklyPlanButton.setOnClickListener(wButtonListener);
        monthlyPlanButton.setOnClickListener(mButtonListener);
        restrictModeButton.setOnClickListener(rButtonListener);

    }
}
