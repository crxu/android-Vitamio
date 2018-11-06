package com.gsgd.live.ui.dialog;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.TextView;

import hdpfans.com.R;
import com.gsgd.live.ui.base.BaseDialog;

import butterknife.BindView;

/**
 * 等待框
 */
public class WaitDialog extends BaseDialog {

    @BindView(R.id.tv_tip)
    TextView mTvTip;

    private String msg;

    public WaitDialog(Context context, int theme, String msg) {
        super(context, theme);
        this.msg = msg;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.dialog_wait;
    }

    @Override
    protected void screenAdapter() {
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(params);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        getWindow().setGravity(Gravity.CENTER);
    }

    @Override
    protected void initParams() {
        super.initParams();
        mTvTip.setText(TextUtils.isEmpty(msg) ? "正在加载，马上为你呈现" : msg);
    }

}
