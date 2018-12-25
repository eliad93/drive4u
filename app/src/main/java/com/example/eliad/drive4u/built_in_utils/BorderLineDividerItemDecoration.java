package com.example.eliad.drive4u.built_in_utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.eliad.drive4u.R;

public class BorderLineDividerItemDecoration extends RecyclerView.ItemDecoration {
    private Drawable mDivider;

    public BorderLineDividerItemDecoration(Context context) {
        mDivider = ContextCompat.getDrawable(context,R.drawable.border_line_list_item);
    }

    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent,
                           @NonNull RecyclerView.State state) {
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + mDivider.getIntrinsicHeight();

            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }
}
