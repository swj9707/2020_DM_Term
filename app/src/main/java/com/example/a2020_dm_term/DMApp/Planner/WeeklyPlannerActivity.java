package com.example.a2020_dm_term.DMApp.Planner;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.session.PlaybackState;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
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
    ArrayList<CustomTextView> taskList = new ArrayList<CustomTextView>();//배치된 작업 목록
    ArrayList<CustomTextView> blockList = new ArrayList<CustomTextView>();//생성된 블럭 목록

    int CELL_SIZE;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weeklyplan);

        downloadPlnDB();
        downloadTaskDB();

        //시간표 셀 크기
        Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        CELL_SIZE = (width - dp2px(1) * 7 - dp2px(10) * 2 - dp2px(20)) / 7;


        context = this;
        task_layout = (LinearLayout) findViewById(R.id.task_layout);
        timeTable = findViewById(R.id.time_table);
        timeTable.setBackgroundColor(Color.BLACK);

        //더이 데이터 추가
        CustomTextView dummy_task = new CustomTextView(this, 1);
        dummy_task.droppable = 0;
        dummy_task.task.hour = 1;
        dummy_task.task.period = 5;
        dummy_task.task.day = 2;
        dummy_task.task.title = "dummy";
        taskList.add(dummy_task);

        CustomTextView dummy_block = new CustomTextView(this, 0);
        dummy_block.droppable = 1;
        dummy_block.task.hour = -1;
        dummy_block.task.period = 5;
        dummy_block.task.day = -1;
        dummy_block.task.title = "dummy";
        blockList.add(dummy_block);

        mkTimeTable();

        Button main_confirm = findViewById(R.id.week_confirm);
        Button main_cancel = findViewById(R.id.week_cancel);

        main_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*현재 추가한 데이터들 DB에 추가*/
                uploadPlnDB();
                uploadTaskDB();
                finish();
            }
        });

        main_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        loadData();
    }

    public void loadData() {
        for (CustomTextView item : taskList) {//이전에 생성된 일정 불러오고 배치하기
            int id = (item.task.hour + 1) * 10 + (item.task.day + 1);
            timeTable.removeView((CustomTextView) findViewById(id));
            item.setId(id);

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(CELL_SIZE, CELL_SIZE);
            params.addRule(RelativeLayout.RIGHT_OF, id / 10 * 10);
            params.addRule(RelativeLayout.ALIGN_TOP, id / 10 * 10);
            params.setMargins(dp2px(1) * (id % 10) + CELL_SIZE * (id % 10 - 1), 0, 0, dp2px(1));
            item.setLayoutParams(params);

            item.setGravity(Gravity.CENTER);
            item.setBackgroundColor(Color.parseColor("#505355"));
            item.setTextColor(Color.parseColor("#BEBEBE"));
            item.setText(item.task.title);
            setDrag(item);
            setDeleteDialog(item);
            timeTable.addView(item);
            mergeCells(item.task.period, id);
        }

        for (CustomTextView item : blockList) {//이전에 생성된 블럭 불러오고 배치하기

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dp2px(100), dp2px(100));
            params.setMargins(dp2px(10), dp2px(10), dp2px(10), dp2px(10));

            item.setText(item.task.title + "\n" + item.task.period);
            item.setGravity(Gravity.CENTER);
            item.setBackgroundDrawable(getResources().getDrawable(R.drawable.edge));
            item.setTextColor(Color.parseColor("#BEBEBE"));
            item.setLayoutParams(params);

            setDrag(item);
            setDeleteDialog(item);
            task_layout.addView(item);
        }
    }

    public void mkTimeTable() {
        /*
        시간표 생성
        테이블 레이아웃으로 표현할 수 있지만 세로로 셀병합과 분리가 불가능하기 때문에 상대적 레이아웃으로 구현
        시간표 셀들의 아이디는 행 * 10 + 열로 배정해서 원하는 셀에 직접 접근할 수 있음
        주의할 점은 (1,1)부터 시작되기 때문에 원하는 시간과 요일 값에 1을 더해줘야할 필요가 있음
        timeTable의 배경을 검정으로 설정하고 셀들의 마진을 1dp씩 설정해서 테두리를 표시하고 셀들은 흰색으로 설정
        */
        int idx = 0;
        for (String day : new String[]{"일", "월", "화", "수", "목", "금", "토"}) {
            RelativeLayout.LayoutParams day_params = new RelativeLayout.LayoutParams(CELL_SIZE, RelativeLayout.LayoutParams.WRAP_CONTENT);
            day_params.setMargins(dp2px(1) * (idx + 1) + CELL_SIZE * idx, 0, 0, 0);
            day_params.addRule(RelativeLayout.RIGHT_OF, R.id.blank);
            day_params.addRule(RelativeLayout.ALIGN_TOP, R.id.blank);
            TextView days = new TextView(this);
            days.setText(day);
            days.setBackgroundColor(Color.parseColor("#3C3F41"));
            days.setTextColor(Color.parseColor("#BEBEBE"));
            days.setLayoutParams(day_params);
            days.setGravity(Gravity.CENTER);
            timeTable.addView(days);
            idx++;
        }

        for (int hour = 1; hour <= 24; hour++) {
            //행의 가장 첫 셀 시간을 나타내는 인덱스 생성
            TextView rowIndex = new TextView(this);
            rowIndex.setText(Integer.toString(hour - 1));
            rowIndex.setGravity(Gravity.CENTER);
            rowIndex.setBackgroundColor(Color.parseColor("#3C3F41"));
            rowIndex.setTextColor(Color.parseColor("#BEBEBE"));
            rowIndex.setId(hour * 10);

            RelativeLayout.LayoutParams idx_params = new RelativeLayout.LayoutParams(dp2px(20), CELL_SIZE);
            idx_params.setMargins(0, dp2px(1), 0, 0);
            if (hour == 1)//좌측 상단의 빈 셀을 기준으로 차례로 아래에 연결한다.
                idx_params.addRule(RelativeLayout.BELOW, R.id.blank);
            else
                idx_params.addRule(RelativeLayout.BELOW, (hour - 1) * 10);

            rowIndex.setLayoutParams(new RelativeLayout.LayoutParams(idx_params));//w, h
            timeTable.addView(rowIndex);

            for (int day = 1; day <= 7; day++) {
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(CELL_SIZE, CELL_SIZE);
                params.addRule(RelativeLayout.RIGHT_OF, hour * 10);
                params.addRule(RelativeLayout.ALIGN_TOP, hour * 10);
                CustomTextView cell = new CustomTextView(this, TABLE_CELL);
                params.setMargins(dp2px(1) * day + CELL_SIZE * (day - 1), 0, 0, dp2px(1));

                cell.setId(hour * 10 + day);
                cell.setOnDragListener(new myOnDragListener());//timeTable 위에서 다른 일정으로 배치할 수 있도록 드래스 리스너 등록
                cell.setGravity(Gravity.CENTER);
                cell.setBackgroundColor(Color.parseColor("#505355"));
                cell.setTextColor(Color.parseColor("#BEBEBE"));
                cell.setLayoutParams(params);

                timeTable.addView(cell);
            }
        }
    }

    public void uploadPlnDB() {
        MainActivity.plnDBC.deleteAllColumns();
        //컨펌 하는 순간 현재 저장되어 있는 모든 정보들은 자동 삭제
        for (CustomTextView element : taskList) {
            //이후 taskList 내에 있는 모든 정보들로 다시 갱신해줌
            //foreach 문을 사용
            MainActivity.plnDBC.insertColumn(element.type, element.task.title,
                    element.droppable, element.task.period, element.task.hour, element.task.day);
            //droppable 같은 경우는 SQLITE에서 따로 Boolean을 제공하지 않음
            //그래서 True or false 를 1 or 0으로 integer 데이터타입으로 변환해서 저장하는 방법을 채택함.
        }
    }

    public void uploadTaskDB() {
        MainActivity.tskDBC.deleteAllColumns();
        for (CustomTextView element : blockList) {
            MainActivity.tskDBC.insertColumn(element.type, element.task.title,
                    element.droppable, element.task.period, element.task.hour, element.task.day);
        }
    }

    public void downloadPlnDB() {
        //프로그램이 시작될 때 TaskList 갱신해주는 메서드
        Cursor c = MainActivity.plnDBC.selectColumns();
        while (c.moveToNext()) {
            int Type = c.getInt(1);
            String Title = c.getString(2);
            int Droppable = c.getInt(3);
            int Period = c.getInt(4);
            int Hour = c.getInt(5);
            int Day = c.getInt(6);
            Log.d("DownLoadPlanDB", "Type:" + Type
                    + " ,Title:" + Title + " ,Droppable:" + Droppable + " ,Period:" + Period + " ,Hour:" + Hour + " ,Day:" + Day);
            CustomTextView element = new CustomTextView(this, Type);
            element.task.setTitle(Title);
            element.setDroppable(Droppable);
            element.task.setPeriod(Period);
            element.task.setHour(Hour);
            element.task.setDay(Day);
            taskList.add(element);
        }
    }

    public void downloadTaskDB() {
        Cursor c = MainActivity.tskDBC.selectColumns();
        while (c.moveToNext()) {
            int Type = c.getInt(1);
            String Title = c.getString(2);
            int Droppable = c.getInt(3);
            int Period = c.getInt(4);
            int Hour = c.getInt(5);
            int Day = c.getInt(6);
            Log.d("DownLoadTaskDB", "Type:" + Type
                    + " ,Title:" + Title + " ,Droppable:" + Droppable + " ,Period:" + Period + " ,Hour:" + Hour + " ,Day:" + Day);
            CustomTextView element = new CustomTextView(this, Type);
            element.task.setTitle(Title);
            element.setDroppable(Droppable);
            element.task.setPeriod(Period);
            element.task.setHour(Hour);
            element.task.setDay(Day);
            blockList.add(element);
        }
    }

    public void addBtnOnClick(View view) {//추가 버튼 눌렀을 시 동작
        System.out.println("add btn onclick");

        final Dialog dialog = new Dialog(this);
        final TaskBlock task = new TaskBlock();//새로 생성하는 작업 내용을 담는 클래스
        dialog.setContentView(R.layout.add_new_task);

        Button confirm = dialog.findViewById(R.id.add_confirm);
        Button cancel = dialog.findViewById(R.id.add_cancel);

        final EditText task_name = dialog.findViewById(R.id.task_title);
        final NumberPicker numberPicker = dialog.findViewById(R.id.numberPicker);//작업 시간 설정
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(24);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("confirm button is clicked.");
                task.title = task_name.getText().toString();
                if (task.title.equals("")) {//제목 설정하지 않으면 무시
                    Toast.makeText(getApplicationContext(), "제목을 설정해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                task.period = numberPicker.getValue();

                CustomTextView newBtn = new CustomTextView(context, TASK_BLOCK);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dp2px(100), dp2px(100));
                params.setMargins(dp2px(10), dp2px(10), dp2px(10), dp2px(10));
                newBtn.task = task;
                //여기 있는 task 친구들은 일단은 Hour, Day 정보가 기본적으론 -1로 설정되어 있는 상황
                newBtn.type = TASK_BLOCK;
                newBtn.setText(task.title + "\n" + task.period);
                newBtn.setGravity(Gravity.CENTER);
                newBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.edge));
                newBtn.setTextColor(Color.parseColor("#BEBEBE"));
                newBtn.setLayoutParams(params);

                setDrag(newBtn);//배치된 일정을 드래그해서 다른 시간에 배치할 수 있도록 롱클릭 이벤트 설정
                setDeleteDialog(newBtn);//클릭하면 일정을 삭제할 수 있도록 이벤트 설정
                blockList.add(newBtn);
                task_layout.addView(newBtn);
                //block List 에 Append하는 상황
                dialog.dismiss();
            }
        });
        //컨펌 버튼 눌렀을 때 액션 리스너

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("cancel button is clicked.");
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void setDeleteDialog(final CustomTextView customView) {//클릭된 뷰를 삭제
        customView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);//확인 팝업창
                builder.setTitle("삭제");
                builder.setMessage("일정을 삭제하시겠습니까?");
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        switch (customView.type) {
                            case TABLE_CELL://테이블에 배치된 작업인 경우
                                splitCell(customView.task.period, customView.getId());//셀 분할
                                taskList.remove((CustomTextView) v);
                                //설정된 값들을 빈 값으로 변경하고 할당된 이벤트들을 모두 삭제함
                                customView.task = null;
                                customView.droppable = 1;
                                customView.setOnClickListener(null);
                                customView.setOnLongClickListener(null);
                                break;
                            case TASK_BLOCK://테이블에 드롭할 블럭인 경우
                                //뷰 삭제
                                task_layout.removeView(customView);
                                blockList.remove((CustomTextView) v);
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

    public void setDrag(View view1) {//드래그 시작하기 위해 롱클릭 이벤트 설정
        view1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view2) {
                CustomTextView customTextView = (CustomTextView) view2;
                View.DragShadowBuilder shadow = new myDragShadowBuilder(view2);//드래그 시 생성되는 그림자 설정
                view2.startDragAndDrop(null, shadow, customTextView, 0);//그래그시 전달할 데이터 설정. CustomTextView 객체를 전달
                //(전달할 데이터(클립보드), 그림자 생성자, 전달할 데이터(로컬), 플래그
                //local state = When dispatching drag events to views in the same activity this object will be available through getLocalState().
                return false;
            }
        });
    }

    class myOnDragListener implements View.OnDragListener {
        @Override
        public boolean onDrag(View view, DragEvent event) {
            final CustomTextView targetCell = (CustomTextView) view;//드롭된 위치에 있는 목표 셀
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
                    CustomTextView droppedCell = (CustomTextView) event.getLocalState();//드랍했을 때 전달되는 데이터

                    if (24 - row + 1 < droppedCell.task.period) {//배치된 작업이 테이블 범위를 벗어나는지 검사
                        Toast.makeText(getApplicationContext(), "배치가 범위를 벗어납니다.", Toast.LENGTH_SHORT).show();
                        break;
                    }

                    for (int i = 0; i <= droppedCell.task.period - 1; i++) {//배치된 작업이 다른 일정을 겹치는지 검사
                        CustomTextView belowCell = (CustomTextView) findViewById(view.getId() + i * 10);
                        if (belowCell.droppable == 0) {
                            Toast.makeText(getApplicationContext(), "다른 일정과 겹칩니다.", Toast.LENGTH_SHORT).show();
                            ext = true;
                            break;
                        }
                    }
                    if (ext)
                        break;

                    //드롭되었을 때 목표 셀에 정보 설정하고 이벤트 등록
                    targetCell.setText(droppedCell.task.title);
                    targetCell.droppable = 0;
                    targetCell.task = droppedCell.task;
                    targetCell.task.hour = row;
                    targetCell.task.day = column;
                    setDrag(targetCell);
                    setDrag(targetCell);
                    setDeleteDialog(targetCell);

                    //만약 배치된 일정을 다른 시간으로 변경하는 경우 설정된 정보, 이벤트 초기화
                    if (droppedCell.type == TABLE_CELL) {
                        splitCell(droppedCell.task.period, droppedCell.getId());
                        droppedCell.task = null;
                        droppedCell.droppable = 1;
                        droppedCell.setOnLongClickListener(null);
                        droppedCell.setOnClickListener(null);
                    }

                    if (taskList.indexOf(targetCell) == -1)//taskList에 등록이 안되어 있으면 등록
                        taskList.add(targetCell);

                    System.out.println(view.getId() + " ACTION_DROP period: " + targetCell.task.period + " hour: " + (row - 1));

                    //1시간 작업인 경우 셀 병합 작업을 거치지 않고 종료 외에는 셀 병합작업을 수행
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

    public void mergeCells(int period, int id) {//셀 병합 작업
        CustomTextView cell = (CustomTextView) findViewById(id);
        for (int i = 1; i <= period - 1; i++)//period의 길이만큼 드롭된 위치에서 아래 셀을 모두 삭제
            timeTable.removeView(findViewById(id + i * 10));
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) cell.getLayoutParams();
        params.height = period * params.height + (period - 1) * dp2px(1);//셀 길이 증가
        cell.setLayoutParams(params);
    }

    public void splitCell(int period, int id) {//병합된 셀을 다시 분리하고 데이터를 초기화
        CustomTextView currentCell = (CustomTextView) findViewById(id);//분할되지 않은 셀
        currentCell.droppable = 1;
        currentCell.setText("");
        //설정 초기화

        //셀 폭, 높이 설정. 최좌측의 인덱스 셀을 기준으로 상단, 우측 배열
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(CELL_SIZE, CELL_SIZE);
        params.addRule(RelativeLayout.RIGHT_OF, id / 10 * 10);
        params.addRule(RelativeLayout.ALIGN_TOP, id / 10 * 10);
        params.setMargins(dp2px(1) * (id % 10) + CELL_SIZE * ((id % 10) - 1), 0, 0, dp2px(1));//마진 값 설정

        currentCell.setLayoutParams(params);

        for (int i = 1; i < period; i++) {//현재 셀의 하단에 있는 셀들을 새로 생성하고 초기화
            CustomTextView newCell = new CustomTextView(this, TABLE_CELL);
            int newId = id + (10 * i);
            newCell.setId(newId);

            params = new RelativeLayout.LayoutParams(CELL_SIZE, CELL_SIZE);
            params.setMargins(dp2px(1) * (id % 10) + CELL_SIZE * ((id % 10) - 1), 0, 0, dp2px(1));
            int targetId = newId / 10 * 10;//최좌측 인덱스의 아이디
            params.addRule(RelativeLayout.RIGHT_OF, targetId);
            params.addRule(RelativeLayout.ALIGN_TOP, targetId);

            newCell.setOnDragListener(new myOnDragListener());//드래그를 받을 수 있는 리스너 등록
            newCell.setGravity(Gravity.CENTER);
            newCell.setBackgroundColor(Color.parseColor("#505355"));
            newCell.setTextColor(Color.parseColor("#BEBEBE"));
            newCell.setLayoutParams(params);

            timeTable.addView(newCell);
        }
    }

    public int dp2px(int dp) {//dp값을 px값으로 변경하는 함수
        float scale = getResources().getDisplayMetrics().density;
        int px = (int) (dp * scale + 0.5f);
        return px;
    }
}

class myDragShadowBuilder extends View.DragShadowBuilder {//드래그 시 그림자 생성하는 클래스
    private static Drawable shadow;

    public myDragShadowBuilder(View view) {
        super(view);
        shadow = new ColorDrawable(Color.LTGRAY);
    }

    public void onProvideShadowMetrics(Point size, Point touch) {//그림자 이미지 설정(크기, 위치)
        int width, height;
        width = getView().getWidth();
        height = getView().getHeight();

        shadow.setBounds(0, 0, width, height);
        size.set((int) (width * 0.9), (int) (height * 0.9));

        int[] location = new int[2];
        getView().getLocationOnScreen(location);

        touch.set(width / 2, height / 2);//터치한 위치 아래에 생성되는 그림자 내부의 좌표
    }

    public void onDrawShadow(Canvas canvas) {
        shadow.draw(canvas);
    }
}