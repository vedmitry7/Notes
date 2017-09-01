package com.vedmitryapps.notes.helper;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;

public class SpacesItemDecoration extends RecyclerView.ItemDecoration {

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

        int marginLeftRight = 3;
        int spaceLeftRight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, marginLeftRight, view.getResources().getDisplayMetrics());
        outRect.left = spaceLeftRight;
        outRect.right = spaceLeftRight;

        int marginTop = 3;
        int spaceTop = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, marginTop, view.getResources().getDisplayMetrics());
        outRect.top = spaceTop;
        outRect.bottom = spaceTop;

    }
}