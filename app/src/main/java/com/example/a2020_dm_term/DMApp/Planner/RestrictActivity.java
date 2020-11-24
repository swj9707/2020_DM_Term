package com.example.a2020_dm_term.DMApp.Planner;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
    TimerTask TT;
    IntentFilter intentFilter;

    private static final String ScreenOff = "android.intent.action.SCREEN_OFF";
    private static final String ScreenOn = "android.intent.action.SCREEN_ON";
    ScreenOnReceiver screenOnReceiver;

    int elapsedTime = 0;
    int second;
    int minute;
    int hour;
    int screenOn = 1;
    private final Handler handler = new Handler();

    class ScreenOnReceiver extends BroadcastReceiver{
        public void onReceive(Context context, Intent intent){
            String action = intent.getAction();
            Log.d("ScreenOnReceiver","ScreenOnReceiver, onReceive:"+action);
            if(action.equals(Intent.ACTION_SCREEN_ON)){
                screenOn = 1;
                Log.d("ScreenOnReceiver","ScreenOnReceiver, ScreenOn");
            }
            else if (action.equals(Intent.ACTION_SCREEN_OFF)){
                screenOn = 0;
                Log.d("ScreenOnReceiver","ScreenOnReceiver, ScreenOFF");
            }
        }
    }
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

        screenOnReceiver = new ScreenOnReceiver();
        intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(screenOnReceiver,intentFilter);
        //intent 설정들

       button.setOnClickListener(ExitListener);
        timer = new Timer();
        /*아직까지 잘 모르겠는것 -> 화면 권한?
        onPause, onStop에서 어떻게든 해결해야 함.
        * */

        TT = new TimerTask(){
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

    @Override
    protected void onStop(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(screenOn == 1){
                    Log.d("onStop","TT.cancel");
                    TT.cancel();
                }
            }
        }, 1000);
        super.onStop();
    }
    /*
    @Override
    protected  void onPause(){
        TT.cancel();
        super.onPause();
    }*/

    @Override
    protected void onRestart(){
        stopTimer();
        super.onRestart();
    }

    @Override
    protected  void onDestroy(){
        TT.cancel();
        super.onDestroy();
    }
    protected void updateTime(){
        Runnable updater = new Runnable(){
            public void run(){
                countTimer.setText(time);
            }
        };
        handler.post(updater);
    }
    protected void stopTimer(){
        Runnable updater = new Runnable() {
            @Override
            public void run() {
                countTimer.setText("방해금지 모드가 종료되었습니다.");
            }
        };
        handler.post(updater);
    }
}
