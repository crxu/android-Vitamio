package com.gsgd.live.ui.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.WindowManager;

import com.gsgd.live.AppConfig;
import hdpfans.com.R;
import com.gsgd.live.data.events.PlayEvent;
import com.gsgd.live.data.listener.OnItemListener;
import com.gsgd.live.ui.adapter.SourceAdapter;
import com.gsgd.live.ui.base.BaseDialog;
import com.gsgd.live.ui.widgets.HorizontalDividerItemDecoration;
import com.gsgd.live.utils.JLog;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;

/**
 * 选择播放源
 */
public class SelectSourceDialog extends BaseDialog {

    @BindView(R.id.rv_list)
    RecyclerView mRecyclerView;

    private LinearLayoutManager mLayoutManager;
    private SourceAdapter mAdapter;

    private List<String> sources;//源列表
    private String source;//当前源
    private int selectPosition;

    public SelectSourceDialog(Context context, int theme, List<String> sources, String source) {
        super(context, theme);
        this.sources = sources;
        this.source = source;

        if (null != sources && !TextUtils.isEmpty(source)) {
            for (int i = 0; i < sources.size(); i++) {
                if (source.equals(sources.get(i))) {
                    selectPosition = i;
                    break;
                }
            }
        }
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.dialog_source_list;
    }

    @Override
    protected void screenAdapter() {
        WindowManager.LayoutParams p = getWindow().getAttributes();
        p.width = WindowManager.LayoutParams.MATCH_PARENT;
        p.gravity = Gravity.BOTTOM;
        getWindow().setAttributes(p);
        getWindow().setWindowAnimations(R.style.dialogWindowAnim_source);
    }

    @Override
    protected void initParams() {
        super.initParams();

        mAdapter = new SourceAdapter(context, sources, selectPosition, new OnItemListener() {
            @Override
            public void onItemClick(int position) {
                EventBus.getDefault().post(new PlayEvent.ChangeSourceEvent(sources.get(position)));
                dismiss();
            }

            @Override
            public void onItemFocus(int position, boolean hasFocus) {

            }
        });
        mLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration(context, 15));
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                focusSelected();
            }
        });
    }

    @Override
    public boolean dispatchKeyEvent(@NonNull KeyEvent event) {
        EventBus.getDefault().post(new PlayEvent.PressKeyOnDialog(AppConfig.PRESS_CODE_SELECT_SOURCE));
        return super.dispatchKeyEvent(event);
    }

    private void focusSelected() {
        JLog.d("******->source selectPosition:" + selectPosition);
        mLayoutManager.scrollToPosition(selectPosition);
        mRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                RecyclerView.ViewHolder holder = mRecyclerView.findViewHolderForLayoutPosition(selectPosition);
                if (holder instanceof SourceAdapter.ViewHolder) {
                    holder.itemView.requestFocus();
                    mRecyclerView.scrollToPosition(selectPosition);
                }
            }
        });
    }

}
