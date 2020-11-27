package com.example.a2020_dm_term.DMApp.Planner;

import android.content.Context;

public class CustomTextView extends androidx.appcompat.widget.AppCompatTextView {
    //블럭인지 아니면 시간표에 배치된 작업인지 구분(0=블럭, 1=작업)
    //한 뷰에 복수의 드래그 리스너를 등록할 수 없으며 어떤 클래스가 드롭되었는지 구분하기 힘들기 때문에
    //블럭과 작업을 같은 클래스로 구현하고 구분하는 인자를 만듬
    int type;
    boolean droppable;//드롭할 수 있는 여부
    TaskBlock task;//배치된 작업의 정보

    CustomTextView(Context context, int type) {
        super(context);
        this.type = type;
        droppable = true;
    }
}