package com.example.a2020_dm_term.DMApp.Planner;

import android.content.Context;
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
    TextView today = (TextView)findViewById(R.id.today);
    Button weeklyPlanButton = (Button)findViewById(R.id.weeklyPlanButton);
    Button monthlyPlanButton = (Button)findViewById(R.id.monthlyPlanButton);
    Button restrictModeButton = (Button)findViewById(R.id.restrictModeButton);


    class weeklyButtonListener implements View.OnClickListener{
        public void onClick(View v){
            //주간 계획으로 넘어감
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
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //System.out.println("Test Commit WJ");
        context = this;
        weeklyButtonListener wButtonListener = new weeklyButtonListener();
        monthlyButtonListener mButtonListener = new monthlyButtonListener();
        restrictButtonListener rButtonListener = new restrictButtonListener();
        weeklyPlanButton.setOnClickListener(wButtonListener);
        monthlyPlanButton.setOnClickListener(mButtonListener);
        restrictModeButton.setOnClickListener(rButtonListener);



    }
}
