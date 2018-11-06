package com.gsgd.live.ui.widgets;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.gsgd.live.utils.MeasureUtil;

public class DividerItemDecoration extends RecyclerView.ItemDecoration {

    private int spacing = 0;//间距

    public DividerItemDecoration(Context context, float padding) {
        this.spacing = MeasureUtil.dip2px(context, padding);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildLayoutPosition(view);

        if (position == 0) {
            outRect.top = 0;

        } else {
            outRect.top = spacing;
        }
    }

}
