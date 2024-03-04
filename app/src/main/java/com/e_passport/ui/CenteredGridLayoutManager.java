package com.e_passport.ui;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CenteredGridLayoutManager extends GridLayoutManager {
    private int spanCount;
    public CenteredGridLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
        this.spanCount = spanCount;
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        super.onLayoutChildren(recycler, state);
        int totalWidth = getWidth() - getPaddingRight() - getPaddingLeft();
        int itemWidth = totalWidth / spanCount;
        int lastRowCount = getItemCount() % spanCount;
        int lastRowWidth = lastRowCount * itemWidth;
        int horizontalOffset = (totalWidth - lastRowWidth) / 2;

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (getPosition(child) == getItemCount() - 1) {
                layoutDecoratedWithMargins(child, horizontalOffset, child.getTop(),
                        horizontalOffset + itemWidth, child.getBottom());
                break;
            }
        }
    }
}