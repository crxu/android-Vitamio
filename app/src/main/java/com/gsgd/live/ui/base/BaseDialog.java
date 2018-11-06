package com.gsgd.live.ui.base;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.gsgd.live.utils.MeasureUtil;

import butterknife.ButterKnife;

public abstract class BaseDialog extends Dialog {

    private double perWidth = 0.76;
    private boolean canceledOnTouchOutside = true;
    private boolean canceled = true;

    private View root;
    protected Context context;

    /**
     * 设置点击返回键是否dismiss
     */
    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }

    /**
     * 设置点击外部是否dismiss
     */
    public void setCancelOnTouchOutside(boolean canceledOnTouchOutside) {
        this.canceledOnTouchOutside = canceledOnTouchOutside;
    }

    /**
     * 设置宽度百分比
     *
     * @param perWidth
     */
    public void setPerWidth(double perWidth) {
        this.perWidth = perWidth;
    }

    public BaseDialog(Context context, int theme) {
        super(context, theme);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        root = LayoutInflater.from(context).inflate(getLayoutResId(), null);
        ButterKnife.bind(this, root);
        setContentView(root);
        initParams();
        setCanceledOnTouchOutside(canceledOnTouchOutside);
        setCancelable(canceled);
        screenAdapter();
    }

    /**
     * 获取布局
     *
     * @return
     */
    abstract protected int getLayoutResId();

    protected void initParams() {
    }

    /**
     * 屏幕宽度适配
     */
    protected void screenAdapter() {
        if (null == context) {
            return;
        }
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = MeasureUtil.per2px(context.getResources(), perWidth);
        getWindow().setAttributes(params);
        getWindow().setGravity(Gravity.CENTER);
    }

}
