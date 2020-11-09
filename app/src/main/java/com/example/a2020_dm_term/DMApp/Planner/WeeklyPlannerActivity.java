package com.example.a2020_dm_term.DMApp.Planner;


import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.a2020_dm_term.R;
import org.w3c.dom.Text;

import java.util.ArrayList;

public class WeeklyPlannerActivity extends AppCompatActivity {
    ArrayList<TaskBlock> taskBlockArrayList = new ArrayList<TaskBlock>();
    Context context;
    LinearLayout task_layout;
    TextView dropTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weeklyplan);

        context = this;
        task_layout = (LinearLayout) findViewById(R.id.task_layout);
        dropTest = (TextView) findViewById(R.id.drop_test);
        dropTest.setOnDragListener(new myOnDragListener());
    }

    public void addBtnOnClick(View view) {//추가 버튼 눌렀을 시 동작
        System.out.println("add btn onclick");

        final Dialog dialog = new Dialog(this);
        final TaskBlock newTask = new TaskBlock();//새로 생성하는 작업 내용을 담는 클래스
        dialog.setContentView(R.layout.add_new_task);

        Button confirm = dialog.findViewById(R.id.add_confirm);
        Button cancel = dialog.findViewById(R.id.add_cancel);
        final EditText task_name = dialog.findViewById(R.id.task_title);
        final TimePicker timePicker = dialog.findViewById(R.id.timePicker);//삭제할 예정

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("confirm button is clicked.");
                newTask.hour = timePicker.getHour();//삭제할 예정
                newTask.minute = timePicker.getMinute();//삭제할 예정
                newTask.title = task_name.getText().toString();
                taskBlockArrayList.add(newTask);

                int lastIndex = taskBlockArrayList.size() - 1;
                dialog.dismiss();

                task_layout.addView(mkBtn(newTask.title));
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

    public Button mkBtn(String str) {
        Button newBtn = new Button(this.context);
        newBtn.setText(str);
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
                Button tmp = (Button)view2;
                View.DragShadowBuilder shadow = new myDragShadowBuilder(view2);
                view2.startDragAndDrop(null, shadow, tmp.getText(), 0);//(전달할 데이터, 그림자 생성자, 전달할 데이터(로컬), 플래그
                //local state = When dispatching drag events to views in the same activity this object will be available through getLocalState().
                return true;
            }
        });
    }

    class myOnDragListener implements View.OnDragListener {
        @Override
        public boolean onDrag(View v, DragEvent event) {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    System.out.println(getResources().getResourceName(v.getId()) + " ACTION_DRAG_STARTED");
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    System.out.println(getResources().getResourceName(v.getId()) + " ACTION_DRAG_ENTERED");
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    System.out.println(getResources().getResourceName(v.getId()) + " ACTION_DRAG_EXITED");
                    break;
                case DragEvent.ACTION_DROP:
                    String tmp = (String)event.getLocalState();
                    dropTest.setText(tmp);
                    System.out.println(getResources().getResourceName(v.getId()) + " ACTION_DROP");
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    System.out.println(getResources().getResourceName(v.getId()) + "ACTION_DRAG_ENDED");
                    break;
            }
            return true;
        }
    }

    public int dp2px(int dp) {
        float mScale = getResources().getDisplayMetrics().density;
        int px = (int) (dp * mScale);
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