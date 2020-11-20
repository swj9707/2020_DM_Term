package com.example.a2020_dm_term.DMApp.Planner;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
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
    TextView countString ;
    TextView countTimer;
    Button button;
    Timer timer;
    String time;
    int elapsedTime = 0;
    int second;
    int minute;
    int hour;
    private final Handler handler = new Handler();

    class RButtonListener implements View.OnClickListener{
        public void onClick(View v){
            Toast.makeText(getApplicationContext(), "방해금지 모드를 종료합니다.",Toast.LENGTH_SHORT).show();
            timer.cancel();
            finish();
        }
    }

    final Handler syncTimer = new Handler(){
        public void timeSetting(String msg){
            countTimer.setText(msg);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restrict);
        context = this;
        Toast.makeText(getApplicationContext(),"방해금지 모드가 실행되었습니다.",Toast.LENGTH_SHORT).show();

        countString = (TextView)findViewById(R.id.Timer1);
        countTimer = (TextView)findViewById(R.id.Timer2);
        button = (Button)findViewById(R.id.stopButton);
        RButtonListener ExitListener = new RButtonListener();

        button.setOnClickListener(ExitListener);
        timer = new Timer();
        /*아직까지 잘 모르겠는것 -> 화면 권한?
        * */

        TimerTask TT = new TimerTask(){
            @Override
            public void run() {
                elapsedTime += 1;
                hour = elapsedTime / 3600;
                minute = (elapsedTime % 3600) / 60;
                second = (elapsedTime % 3600) % 60;
                time = hour + ":" + minute + ":" + second;
                Log.v(this.getClass().getName(), time);
                updateTime();
            }
        };
        timer.schedule(TT,0,1000);
    }
    protected void updateTime(){
        Runnable updater = new Runnable(){
            public void run(){
                countTimer.setText(time);
            }
        };
        handler.post(updater);
    }
}
