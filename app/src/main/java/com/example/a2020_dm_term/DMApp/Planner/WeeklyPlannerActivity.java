package com.example.a2020_dm_term.DMApp.Planner;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.a2020_dm_term.R;

import java.util.ArrayList;

public class WeeklyPlannerActivity extends AppCompatActivity {
    ArrayList<TaskBlock> taskBlockArrayList = new ArrayList<TaskBlock>();
    Context context;
    LinearLayout task_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weeklyplan);
        context = this;
        task_layout = (LinearLayout) findViewById(R.id.task_layout);
    }

    class TaskBlock {
        int period;
        String title;
        int hour;
        int minute;

        void TaskBlock() {
        }
    }

    int dp_to_px(int dp) {
        float mScale = getResources().getDisplayMetrics().density;
        int px = (int) (dp * mScale);
        return px;
    }

    Button make_button(String str) {
        Button newBtn = new Button(this.context);
        newBtn.setText(str);
        newBtn.setWidth(dp_to_px(140));
        newBtn.setHeight(dp_to_px(140));

        return newBtn;
    }

    public void addBtnOnClick(View view) {
        System.out.println("add btn onclick");
        final Dialog dialog = new Dialog(this);
        final TaskBlock newTask = new TaskBlock();
        dialog.setContentView(R.layout.add_new_task);

        Button confirm = dialog.findViewById(R.id.add_confirm);
        Button cancel = dialog.findViewById(R.id.add_cancel);
        final EditText task_name = dialog.findViewById(R.id.task_title);
        final TimePicker timePicker = dialog.findViewById(R.id.timePicker);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("confirm button is clicked.");
                newTask.hour = timePicker.getHour();
                newTask.minute = timePicker.getMinute();
                newTask.title = task_name.getText().toString();
                taskBlockArrayList.add(newTask);

                int lastIndex = taskBlockArrayList.size() - 1;
                System.out.printf("hour = %d, minute = %d\n",
                        taskBlockArrayList.get(lastIndex).hour,
                        taskBlockArrayList.get(lastIndex).minute);
                dialog.dismiss();

                task_layout.addView(make_button(newTask.title));
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("cancel button is clicked.");
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}

