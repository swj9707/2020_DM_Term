package com.example.a2020_dm_term.DMApp.Planner;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.a2020_dm_term.R;

import java.util.Timer;
import java.util.TimerTask;

public class RestrictActivity extends AppCompatActivity {
    Context context;
    TextView countString = (TextView)findViewById(R.id.Timer1);
    TextView countTimer = (TextView)findViewById(R.id.Timer2);
    Button button = (Button)findViewById(R.id.stopButton);
    Timer timer;
    class RButtonListener implements View.OnClickListener{
        public void onClick(View v){
            Toast.makeText(getApplicationContext(), "방해금지 모드를 종료합니다.",Toast.LENGTH_SHORT).show();
            timer.cancel();
            finish();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weeklyplan);
        context = this;
        Toast.makeText(getApplicationContext(),"방해금지 모드가 실행되었습니다.",Toast.LENGTH_SHORT).show();
        RButtonListener buttonListener = new RButtonListener();
        button.setOnClickListener(buttonListener);
        timer = new Timer();
        /*아직까지 잘 모르겠는것 -> 화면 권한?
        * */

        TimerTask TT = new TimerTask(){
            int elapsedTime = 0;
            int second;
            int minute;
            int hour;
            String time;
            @Override
            public void run() {
                elapsedTime += 1;
                hour = elapsedTime / 3600;
                minute = (elapsedTime % 3600) / 60;
                second = (elapsedTime % 3600) % 60;
                time = hour + ":" + minute + ":" + second;
                Log.d(this.getClass().getName(), time);
                countTimer.setText(time);
            }
        };
        timer.schedule(TT,0,1000);
    }
}
