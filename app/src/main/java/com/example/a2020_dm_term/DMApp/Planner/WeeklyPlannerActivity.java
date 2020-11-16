package com.example.a2020_dm_term.DMApp.Planner;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import com.example.a2020_dm_term.R;
import org.w3c.dom.Text;

import java.util.ArrayList;

public class WeeklyPlannerActivity extends AppCompatActivity {
    ArrayList<TaskBlock> taskBlockArrayList = new ArrayList<TaskBlock>();
    Context context;
    LinearLayout task_layout;
    GridLayout timeGrid;
    RelativeLayout timeTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weeklyplan);

        context = this;
        task_layout = (LinearLayout) findViewById(R.id.task_layout);
        //timeGrid = (GridLayout) findViewById(R.id.time_grid);
        timeTable = findViewById(R.id.time_table);
        timeTable.setBackgroundColor(Color.BLACK);

        for (int hour = 1; hour <= 24; hour++) {
            TextView rowIndex = new TextView(this);
            rowIndex.setText(Integer.toString(hour - 1));
            rowIndex.setGravity(Gravity.CENTER);
            rowIndex.setBackgroundColor(Color.WHITE);
            rowIndex.setId(hour * 10);

            RelativeLayout.LayoutParams idx_params = new RelativeLayout.LayoutParams(dp2px(20), dp2px(100));
            idx_params.setMargins(0, dp2px(1), 0, 0);
            if (hour == 1)
                idx_params.addRule(RelativeLayout.BELOW, R.id.blank);
            else
                idx_params.addRule(RelativeLayout.BELOW, (hour - 1) * 10);

            rowIndex.setLayoutParams(new RelativeLayout.LayoutParams(idx_params));//w, h
            timeTable.addView(rowIndex);

            for (int day = 1; day <= 7; day++) {
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(dp2px(100), dp2px(100));
                params.addRule(RelativeLayout.RIGHT_OF, hour * 10);
                params.addRule(RelativeLayout.ALIGN_TOP, hour * 10);
                TextView cell = new TextView(this);
                params.setMargins(dp2px(1) * day + dp2px(100) * (day - 1), 0, 0, dp2px(1));

                cell.setId(hour * 10 + day);
                cell.setOnDragListener(new myOnDragListener());
                cell.setGravity(Gravity.CENTER);
                cell.setBackgroundColor(Color.WHITE);
                cell.setLayoutParams(params);
                timeTable.addView(cell);
            }
        }

        /*for (int hour = 0; hour < 24; hour++) {
            TableRow tableRow = new TableRow(this);
            tableRow.setMinimumHeight(dp2px(100));
            tableRow.setBackgroundColor(Color.BLACK);

            TableRow.LayoutParams params = new TableRow.LayoutParams();
            params.height = TableRow.LayoutParams.MATCH_PARENT;

            TextView rowIndex = new TextView(this);
            rowIndex.setText(Integer.toString(hour));
            rowIndex.setGravity(Gravity.CENTER);
            rowIndex.setBackgroundColor(Color.WHITE);

            rowIndex.setLayoutParams(params);
            tableRow.addView(rowIndex);

            for (int day = 0; day < 7; day++) {
                TextView td = new TextView(this);
                params.setMargins(1, 1, 1, 1);

                td.setId(hour * 10 + day);
                td.setOnDragListener(new myOnDragListener());
                td.setWidth(dp2px(100));
                td.setGravity(Gravity.CENTER);
                td.setBackgroundColor(Color.WHITE);
                td.setLayoutParams(params);
                tableRow.addView(td);
            }
            timeTable.addView(tableRow);
        }*/
    }

    public void addBtnOnClick(View view) {//추가 버튼 눌렀을 시 동작
        System.out.println("add btn onclick");

        final Dialog dialog = new Dialog(this);
        final TaskBlock task = new TaskBlock();//새로 생성하는 작업 내용을 담는 클래스
        dialog.setContentView(R.layout.add_new_task);

        Button confirm = dialog.findViewById(R.id.add_confirm);
        Button cancel = dialog.findViewById(R.id.add_cancel);
        final EditText task_name = dialog.findViewById(R.id.task_title);
        final NumberPicker numberPicker = dialog.findViewById(R.id.numberPicker);
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(24);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("confirm button is clicked.");
                task.title = task_name.getText().toString();
                task.period = numberPicker.getValue();
                taskBlockArrayList.add(task);
                dialog.dismiss();

                task_layout.addView(mkBtn(task));
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

    public CustomButton mkBtn(TaskBlock task) {
        CustomButton newBtn = new CustomButton(this.context, task);
        newBtn.setText(task.title + "\n" + Integer.toString(task.period));
        //newBtn.setText(Html.fromHtml(task.title + "<br><small>" + Integer.toString(task.period) + "<small>"));
        newBtn.setWidth(dp2px(140));
        newBtn.setHeight(dp2px(140));
        setDrag(newBtn);

        return newBtn;
    }

    public static void setDrag(View view1) {
        view1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view2) {
                /*
                ClipData.Item item = new ClipData.Item((CharSequence) view.getTag());
                ClipData dragData = new ClipData((CharSequence) view.getTag(), new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN}, item);
                 */
                CustomButton tmp = (CustomButton) view2;
                View.DragShadowBuilder shadow = new myDragShadowBuilder(view2);
                view2.startDragAndDrop(null, shadow, tmp.task, 0);//(전달할 데이터, 그림자 생성자, 전달할 데이터(로컬), 플래그
                //local state = When dispatching drag events to views in the same activity this object will be available through getLocalState().
                return true;
            }
        });
    }

    class myOnDragListener implements View.OnDragListener {
        @Override
        public boolean onDrag(View v, DragEvent event) {
            TextView textView = (TextView) v;
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    //System.out.println(v.getId() + " ACTION_DRAG_STARTED");
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    //System.out.println(v.getId() + " ACTION_DRAG_ENTERED");
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    //System.out.println(v.getId() + " ACTION_DRAG_EXITED");
                    break;
                case DragEvent.ACTION_DROP:
                    TaskBlock task = (TaskBlock) event.getLocalState();
                    int period = task.period;
                    int hour = textView.getId() / 10;
                    textView.setText(task.title);
                    System.out.println(v.getId() + " ACTION_DROP period: " + period + " hour: " + (hour - 1));

                    if (period == 1)
                        break;

                    for (int i = 1; i <= period - 1; i++)
                        timeTable.removeView(findViewById(v.getId() + i * 10));
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) textView.getLayoutParams();
                    params.height = period * textView.getHeight() + (period - 1) * dp2px(1);
                    textView.setLayoutParams(params);
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    //System.out.println(v.getId() + "ACTION_DRAG_ENDED");
                    break;
            }
            return true;
        }
    }

    public void mergeCells(int period, int id) {

    }

    public int dp2px(int dp) {
        float scale = getResources().getDisplayMetrics().density;
        int px = (int) (dp * scale + 0.5f);
        return px;
    }
}

class myDragShadowBuilder extends View.DragShadowBuilder {//드래그 시 이미지 생성
    private static Drawable shadow;

    public myDragShadowBuilder(View view) {
        super(view);
        shadow = new ColorDrawable(Color.LTGRAY);
    }

    public void onProvideShadowMetrics(Point size, Point touch) {
        int width, height;
        width = getView().getWidth();
        height = getView().getHeight();

        shadow.setBounds(0, 0, width, height);
        size.set((int) (width * 0.9), (int) (height * 0.9));

        int[] location = new int[2];
        getView().getLocationOnScreen(location);

        touch.set(width / 2, height / 2);//터치한 위치 아래에 생성되는 이미지 내부의 좌표(=이미지 시작 점)
        //빈 화면에 드롭했을 때 그림자가 돌아가는 모션이 부자연스러워 차후 수정할 것.
    }

    public void onDrawShadow(Canvas canvas) {
        shadow.draw(canvas);
    }
}

class CustomButton extends AppCompatButton {
    TaskBlock task;

    CustomButton(Context context, TaskBlock task) {
        super(context);
        this.task = task;
    }
}