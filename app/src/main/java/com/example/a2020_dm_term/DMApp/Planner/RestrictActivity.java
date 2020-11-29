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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class RestrictActivity extends AppCompatActivity {
    private Context context;
    //Context
    private TextView countString;
    private TextView countTimer;
    private Button button;
    //View들 선언
    private Timer timer;
    private String time;
    private TimerTask TT;
    private int elapsedTime = 0;
    private int second;
    private int minute;
    private int hour;
    private Date date;
    private String today;
    //Timer 관련 필드값들

    private IntentFilter intentFilter;
    private ScreenOnReceiver screenOnReceiver;
    private boolean screenOn = true;
    //화면 On Off 예외 처리를 하기 위해 선언 된 필드값들
    /*
    onStop 처리 되는 상황은 여러가지가 있음. 그 중에서 화면 종료가 되는 상황은 예외처리 시켜야 함.
    전화가 왔을 때 상황도 가능하면 체크해볼 예정. -> 도전
    * */

    private final Handler handler = new Handler();
    //타이머 변화에 따라 뷰에 변화를 주는 Handler

    class ScreenOnReceiver extends BroadcastReceiver {
        //화면이 꺼지거나 켜지거나 -> Broadcast로 처리한다. 모든 앱 들이 영향을 받기 때문
        public void onReceive(Context context, Intent intent) {
            //onReceive가 선언되었을 때
            String action = intent.getAction();
            //intent.getAction 메서드를 통해 받아 온 결과를 확인한다. 여기서 intent는 따로 선언해줄 필요는 없음
            if (action.equals(Intent.ACTION_SCREEN_ON)) {
                //만약 String action이 Intent.ACTION_SCREEN_ON과 같다면 -> 즉 화면이 켜지는 Broadcast를 catch했다면
                screenOn = true;
                //boolean screenOn을 true로 처리함
            } else if (action.equals(Intent.ACTION_SCREEN_OFF)) {
                //else if -> String action == Intent.ACTION.SCREEN_OFF
                screenOn = false;
                //else if니까 위 랑은 다른 결과.
            }
        }
    }

    class RButtonListener implements View.OnClickListener {
        //종료 버튼 OnClickListener
        public void onClick(View v) {
            Toast.makeText(getApplicationContext(), "방해금지 모드를 종료합니다.", Toast.LENGTH_SHORT).show();
            timer.cancel();
            stopTimer();
            finish();
            //더이상의 자세한 설명은 생략한다.
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //onCreate 메서드
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restrict);
        context = this;
        //여기 까지는 설명 생략
        Toast.makeText(getApplicationContext(), "방해금지 모드가 실행되었습니다.", Toast.LENGTH_SHORT).show();
        //이 Activity가 실행 되는 순간 toast로 방해금지 모드가 실행되었다는 메시지가 나오도록 함
        countString = (TextView) findViewById(R.id.Timer1);
        countTimer = (TextView) findViewById(R.id.Timer2);
        button = (Button) findViewById(R.id.stopButton);
        //각 View들 관련 필드값들에 어떤 View가 들어갈 지를 findViewById 메서드로 처리함
        RButtonListener ExitListener = new RButtonListener();
        button.setOnClickListener(ExitListener);
        //위에서 설명한 종료 버튼 리스너를 선언 후 Input

        date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일 (E)", Locale.KOREA);
        today = sdf.format(date);

        screenOnReceiver = new ScreenOnReceiver();
        intentFilter = new IntentFilter();
        //화면 On/Off에 따른 예외처리를 위한 클래스들 선언
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        //intentFilter에 ACTION_SCREEN_ON/OFF 등록
        registerReceiver(screenOnReceiver, intentFilter);
        //screenOnReceiver, intentFilter를 인수로 registerReceiver 함수 사용.

        timer = new Timer();
        //Timer 객체 선언
        TT = new TimerTask() {
            @Override
            public void run() {
                //TimerTask가 매 초마다 작동해야 하는 작업
                elapsedTime += 1;
                hour = elapsedTime / 3600;
                minute = (elapsedTime % 3600) / 60;
                second = (elapsedTime % 3600) % 60;
                time = String.format("%02d", hour) + ":" + String.format("%02d", minute) + ":" + String.format("%02d", second);
                /*
                elapsedTime은 초당 1씩 올라가고
                그 값들을 통해 H,M,S 정보들을 받아와서 String time을 완성*/
                updateTime();
                //updateTime 메서드 발동 -> 매 초마다 지나가고 있는 시간을 TextView에 반영
            }
        };
        timer.schedule(TT, 0, 1000);
        //매 1000ms당 이 메서드 작동
    }

    @Override
    protected void onStop() {
        /*
        onStop이 걸렸을 때 -> Activity가 완전히 뒤로 넘어갔을 때
        여러가지 경우가 있겠지만 아마 이 앱을 넘기고 딴짓을 하는 상황이라 판단하고
        Timer를 완전히 꺼버림
        * */
        new Handler().postDelayed(new Runnable() {
            @Override
            /*
            postDelayed를 사용한 이유는 화면이 꺼지는 Broadcast 이후에
            intent가 그걸 받고 처리하기 전에 TT.cancel이 처리가 되어버리기 때문에
            화면이 꺼지는 상황의 예외처리가 어려움이 있었음.
            다양한 방법이 있겠지만 지금은 postDelayed를 통해 1초 딜레이 후에 판단하는 것으로
            더 좋은 방법 있으면 교체 충분히 가능
            * */
            public void run() {
                if (screenOn == true) {
                    Log.d("onStop", "TT.cancel");
                    TT.cancel();
                }
            }
        }, 1000);
        super.onStop();
    }

    @Override
    protected void onRestart() {
        /*
        onRestart가 작동해서 다시 이 액티비티로 돌아오게 되었을 때는
        timer는 종료된 상황이고 더이상 TT는 작동하지 않음
        그리고 방해금지 모드가 종료되었다는 안내가 나오고 끝
        * */
        stopTimer();
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        /*
        onDestroy 될 때 또한 당연히 TT를 멈춰 줄 필요가 있음.
        * */
        TT.cancel();
        super.onDestroy();
    }

    protected void updateTime() {
        /*
        매 초마다 지나간 시간을 표시해주는 메서드
        * */
        Runnable updater = new Runnable() {
            public void run() {
                countTimer.setText(time);
                //계속해서 갱신되는 String time을 countTimer TextView에 setText해줌
            }
        };
        handler.post(updater);
    }

    protected void stopTimer() {
        /*
        updateTime과 원리는 같음
        차이가 있다면 이제 더이상 타이머를 재지 않는다고 처리해주는 것이라 생각해주면 됨.
        * */
        Runnable updater = new Runnable() {
            @Override
            public void run() {
                countTimer.setText("방해금지 모드가 종료되었습니다.");
            }
        };
        handler.post(updater);
        MainActivity.sHrDBC.insertColumn(today, Integer.toString(elapsedTime));
    }
}
