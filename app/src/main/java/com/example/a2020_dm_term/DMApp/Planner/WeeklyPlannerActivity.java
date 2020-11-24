package com.example.a2020_dm_term.DMApp.Planner;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.example.a2020_dm_term.R;

import java.util.ArrayList;


public class WeeklyPlannerActivity extends AppCompatActivity {
    Context context;
    LinearLayout task_layout;
    RelativeLayout timeTable;
    final int TASK_BLOCK = 0;
    final int TABLE_CELL = 1;
    ArrayList<CustomTextView> taskList = new ArrayList<CustomTextView>();
    ArrayList<CustomTextView> blockList = new ArrayList<CustomTextView>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weeklyplan);

        context = this;
        task_layout = (LinearLayout) findViewById(R.id.task_layout);
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
                CustomTextView cell = new CustomTextView(this, TABLE_CELL);
                params.setMargins(dp2px(1) * day + dp2px(100) * (day - 1), 0, 0, dp2px(1));

                cell.setId(hour * 10 + day);
                cell.setOnDragListener(new myOnDragListener());
                cell.setGravity(Gravity.CENTER);
                cell.setBackgroundColor(Color.WHITE);
                cell.setLayoutParams(params);

                timeTable.addView(cell);
            }
        }
        Button main_confirm = findViewById(R.id.week_confirm);
        Button main_cancel = findViewById(R.id.week_cancel);

        main_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*현재 추가한 데이터들 DB에 추가*/
                finish();
            }
        });

        main_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("cancel button is clicked.");
                finish();
            }
        });
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
                dialog.dismiss();

                CustomTextView newBtn = new CustomTextView(context, TASK_BLOCK);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dp2px(130), dp2px(130));
                params.setMargins(dp2px(10), dp2px(10), dp2px(10), dp2px(10));
                newBtn.task = task;
                newBtn.type = TASK_BLOCK;
                newBtn.setText(task.title + "\n" + task.period);
                newBtn.setGravity(Gravity.CENTER);
                newBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.edge));
                newBtn.setLayoutParams(params);

                setDrag(newBtn);
                setDeleteEvent(newBtn);
                blockList.add(newBtn);

                task_layout.addView(newBtn);
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

    public void setDeleteEvent(final CustomTextView customView) {
        customView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("삭제");
                builder.setMessage("일정을 삭제하시겠습니까?");
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        switch (customView.type) {
                            case TABLE_CELL:
                                splitCell(customView.task.period, customView.getId());
                                taskList.remove((CustomTextView) v);
                                for (CustomTextView i : taskList) {
                                    System.out.println(i.task.title);
                                }
                                break;
                            case TASK_BLOCK:
                                task_layout.removeView(customView);
                                blockList.remove((CustomTextView) v);
                                for (CustomTextView i : blockList) {
                                    System.out.println(i.task.title);
                                }
                                break;
                        }
                    }
                });
                builder.setNegativeButton("취소", null);

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    public void setDrag(View view1) {
        view1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view2) {
                CustomTextView customTextView = (CustomTextView) view2;
                View.DragShadowBuilder shadow = new myDragShadowBuilder(view2);
                view2.startDragAndDrop(null, shadow, customTextView, 0);//(전달할 데이터, 그림자 생성자, 전달할 데이터(로컬), 플래그
                //local state = When dispatching drag events to views in the same activity this object will be available through getLocalState().
                return false;
            }
        });
    }

    class myOnDragListener implements View.OnDragListener {
        @Override
        public boolean onDrag(View view, DragEvent event) {
            final CustomTextView targetCell = (CustomTextView) view;
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
                    int row = targetCell.getId() / 10;
                    int column = targetCell.getId() % 10;

                    boolean ext = false;
                    CustomTextView droppedCell = (CustomTextView) event.getLocalState();

                    if (24 - row + 1 < droppedCell.task.period) {
                        Toast.makeText(getApplicationContext(), "배치가 범위를 벗어납니다.", Toast.LENGTH_SHORT).show();
                        break;
                    }

                    for (int i = 0; i <= droppedCell.task.period - 1; i++) {
                        CustomTextView belowCell = (CustomTextView) findViewById(view.getId() + i * 10);
                        if (!belowCell.droppable) {
                            Toast.makeText(getApplicationContext(), "다른 일정과 겹칩니다.", Toast.LENGTH_SHORT).show();
                            ext = true;
                            break;
                        }
                    }
                    if (ext)
                        break;

                    targetCell.setText(droppedCell.task.title);
                    targetCell.droppable = false;
                    targetCell.task = droppedCell.task;
                    targetCell.task.hour = row;
                    targetCell.task.day = column;
                    setDrag(targetCell);
                    setDeleteEvent(targetCell);

                    if (droppedCell.type == TABLE_CELL) {
                        splitCell(droppedCell.task.period, droppedCell.getId());
                        droppedCell.task = null;
                        droppedCell.droppable = true;
                    }

                    if (taskList.indexOf(targetCell) == -1)
                        taskList.add(targetCell);

                    System.out.println(view.getId() + " ACTION_DROP period: " + targetCell.task.period + " hour: " + (row - 1));

                    if (targetCell.task.period == 1)
                        break;
                    mergeCells(targetCell.task.period, view.getId());
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    //System.out.println(v.getId() + "ACTION_DRAG_ENDED");
                    break;
            }
            return true;
        }
    }

    public void mergeCells(int period, int id) {
        CustomTextView cell = (CustomTextView) findViewById(id);
        for (int i = 1; i <= period - 1; i++)
            timeTable.removeView(findViewById(id + i * 10));
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) cell.getLayoutParams();
        params.height = period * cell.getHeight() + (period - 1) * dp2px(1);
        cell.setLayoutParams(params);
    }

    public void splitCell(int period, int id) {
        CustomTextView currentCell = (CustomTextView) findViewById(id);
        currentCell.droppable = true;
        currentCell.setText("");

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(dp2px(100), dp2px(100));
        params.addRule(RelativeLayout.RIGHT_OF, id / 10 * 10);
        params.addRule(RelativeLayout.ALIGN_TOP, id / 10 * 10);
        params.setMargins(dp2px(1) * (id % 10) + dp2px(100) * ((id % 10) - 1), 0, 0, dp2px(1));

        currentCell.setLayoutParams(params);

        for (int i = 1; i < period; i++) {
            CustomTextView newCell = new CustomTextView(this, TABLE_CELL);
            int newId = id + (10 * i);
            newCell.setId(newId);

            params = new RelativeLayout.LayoutParams(dp2px(100), dp2px(100));
            params.setMargins(dp2px(1) * (id % 10) + dp2px(100) * ((id % 10) - 1), 0, 0, dp2px(1));
            int targetId = newId / 10 * 10;
            params.addRule(RelativeLayout.RIGHT_OF, targetId);
            params.addRule(RelativeLayout.ALIGN_TOP, targetId);

            newCell.setOnDragListener(new myOnDragListener());
            newCell.setGravity(Gravity.CENTER);
            newCell.setBackgroundColor(Color.WHITE);
            newCell.setLayoutParams(params);

            timeTable.addView(newCell);
        }
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


class CustomTextView extends androidx.appcompat.widget.AppCompatTextView {
    int type;
    boolean droppable;
    TaskBlock task;

    CustomTextView(Context context, int type) {
        super(context);
        this.type = type;
        droppable = true;
    }
}