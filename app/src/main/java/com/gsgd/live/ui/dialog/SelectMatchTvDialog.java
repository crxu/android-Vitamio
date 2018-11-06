package com.gsgd.live.ui.dialog;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.WindowManager;

import hdpfans.com.R;
import com.gsgd.live.data.events.PlayEvent;
import com.gsgd.live.data.listener.OnItemListener;
import com.gsgd.live.data.model.Channel;
import com.gsgd.live.data.model.ChannelType;
import com.gsgd.live.ui.adapter.MatchTvAdapter;
import com.gsgd.live.ui.base.BaseDialog;
import com.gsgd.live.ui.widgets.DividerItemDecoration;
import com.gsgd.live.utils.MeasureUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * 选择匹配的源界面
 */
public class SelectMatchTvDialog extends BaseDialog {

    @BindView(R.id.rv_list)
    RecyclerView mRecyclerView;

    private LinearLayoutManager mLayoutManager;
    private MatchTvAdapter mAdapter;

    private ChannelType channelType;
    private ArrayList<Channel> channels;//源列表

    public SelectMatchTvDialog(Context context, int theme, ChannelType channelType, ArrayList<Channel> channels) {
        super(context, theme);
        this.channelType = channelType;
        this.channels = channels;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.dialog_match_tv;
    }

    @Override
    protected void screenAdapter() {
        WindowManager.LayoutParams p = getWindow().getAttributes();
        p.width = MeasureUtil.per2px(context.getResources(), 0.55);
        p.height = MeasureUtil.perh2px(context.getResources(), 0.55);
        p.gravity = Gravity.CENTER;
        getWindow().setAttributes(p);
    }

    @Override
    protected void initParams() {
        super.initParams();

        mAdapter = new MatchTvAdapter(context, channels, new OnItemListener() {
            @Override
            public void onItemClick(int position) {
                EventBus.getDefault().post(new PlayEvent.SelectChannelEvent(channelType, channels.get(position)));
                dismiss();
            }

            @Override
            public void onItemFocus(int position, boolean hasFocus) {

            }
        });
        mLayoutManager = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(context, 1));
        mRecyclerView.setAdapter(mAdapter);
    }

}
