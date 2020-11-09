package com.example.a2020_dm_term.DMApp.Planner;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.a2020_dm_term.R;

public class RestrictActivity extends AppCompatActivity {
    Context context;
    String since;//지금까지 공부한 시간 타이머
    TextView countString = (TextView)findViewById(R.id.Timer1);
    TextView countTimer = (TextView)findViewById(R.id.Timer2);
    Button button = (Button)findViewById(R.id.stopButton);
    class RButtonListener implements View.OnClickListener{
        public void onClick(View v){
            Toast.makeText(getApplicationContext(), "방해금지 모드를 종료합니다.",Toast.LENGTH_SHORT).show();
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
    }
    public void countDown(String time) {

        long conversionTime = 0;
        //ms단위
        String getHour = time.substring(0, 2);
        String getMin = time.substring(2, 4);
        String getSecond = time.substring(4, 6);

        // "00"이 아니고, 첫번째 자리가 0 이면 제거
        if (getHour.substring(0, 1) == "0") {
            getHour = getHour.substring(1, 2);
        }

        if (getMin.substring(0, 1) == "0") {
            getMin = getMin.substring(1, 2);
        }

        if (getSecond.substring(0, 1) == "0") {
            getSecond = getSecond.substring(1, 2);
        }

        // 변환시간
        conversionTime = Long.valueOf(getHour) * 1000 * 3600 + Long.valueOf(getMin) * 60 * 1000 + Long.valueOf(getSecond) * 1000;

        // 첫번쨰 인자 : 원하는 시간 (예를들어 30초면 30 x 1000(주기))
        // 두번쨰 인자 : 주기( 1000 = 1초)
        new CountDownTimer(conversionTime, 1000) {

            // 특정 시간마다 뷰 변경
            public void onTick(long millisUntilFinished) {

                // 시간단위
                String hour = String.valueOf(millisUntilFinished / (60 * 60 * 1000));

                // 분단위
                long getMin = millisUntilFinished - (millisUntilFinished / (60 * 60 * 1000)) ;
                String min = String.valueOf(getMin / (60 * 1000)); // 몫

                // 초단위
                String second = String.valueOf((getMin % (60 * 1000)) / 1000); // 나머지

                // 밀리세컨드 단위
                String millis = String.valueOf((getMin % (60 * 1000)) % 1000); // 몫

                // 시간이 한자리면 0을 붙인다
                if (hour.length() == 1) {
                    hour = "0" + hour;
                }

                // 분이 한자리면 0을 붙인다
                if (min.length() == 1) {
                    min = "0" + min;
                }

                // 초가 한자리면 0을 붙인다
                if (second.length() == 1) {
                    second = "0" + second;
                }

                countTimer.setText(hour + ":" + min + ":" + second);
            }

            // 제한시간 종료시
            public void onFinish() {
                // 변경 후
                countTimer.setText("촬영종료!");

                // TODO : 타이머가 모두 종료될때 어떤 이벤트를 진행할지

            }
        }.start();

    }
}
