package com.gsgd.live.ui.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;

import com.gsgd.live.AppConfig;
import hdpfans.com.R;
import com.gsgd.live.data.events.PlayEvent;
import com.gsgd.live.data.listener.OnItemListener;
import com.gsgd.live.data.model.Channel;
import com.gsgd.live.data.model.ChannelType;
import com.gsgd.live.ui.adapter.ChannelAdapter;
import com.gsgd.live.ui.adapter.ChannelTypeAdapter;
import com.gsgd.live.ui.base.BaseDialog;
import com.gsgd.live.ui.widgets.DividerItemDecoration;
import com.gsgd.live.utils.JLog;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * 选择视频栏目
 */
public class SelectTvDialog extends BaseDialog {

    @BindView(R.id.lv_type)
    RecyclerView mLvChannelType;
    @BindView(R.id.lv_channel)
    RecyclerView mLvChannel;

    private Context mContext;
    private ArrayList<ChannelType> mChannelTypes;
    private LinearLayoutManager mLayoutManagerType;
    private LinearLayoutManager mLayoutManagerChannel;
    private ChannelTypeAdapter mChannelTypeAdapter;
    private ChannelAdapter mChannelAdapter;

    //栏目数目
    private int typeSize = 0;
    //当前播放视频选中的位置
    private int mCurrentSelectTypePosition = 0;
    private int mCurrentSelectPosition = 0;

    //当前光标选中的位置
    private int selectTypePosition = 0;//栏目
    private int selectChannelPosition = 0;//频道

    public SelectTvDialog(@NonNull Context context, @StyleRes int themeResId, ArrayList<ChannelType> channelTypes, ChannelType type, Channel channel) {
        super(context, themeResId);
        this.mContext = context;
        this.mChannelTypes = channelTypes;
        this.typeSize = mChannelTypes.size();

        mCurrentSelectTypePosition = getCurrentSelectTypePosition(type);
        mCurrentSelectPosition = getCurrentSelectChannelPosition(channel);

        JLog.e("******mCurrentSelectTypePosition:" + mCurrentSelectTypePosition + "||mCurrentSelectPosition:" + mCurrentSelectPosition);
    }

    private int getCurrentSelectTypePosition(ChannelType type) {
        int position = 0;
        try {
            if (null != type) {
                for (int i = 0; i < mChannelTypes.size(); i++) {
                    if (type.id == mChannelTypes.get(i).id) {
                        position = i;
                        break;
                    }
                }
            }

        } catch (Exception e) {
            JLog.e(e.getLocalizedMessage());
        }

        return position;
    }

    private int getCurrentSelectChannelPosition(Channel channel) {
        int position = 0;
        try {
            if (null != channel) {
                for (int i = 0; i < mChannelTypes.get(mCurrentSelectTypePosition).channels.size(); i++) {
                    if (channel.id == mChannelTypes.get(mCurrentSelectTypePosition).channels.get(i).id) {
                        position = i;
                        break;
                    }
                }
            }

        } catch (Exception e) {
            JLog.e(e.getLocalizedMessage());
        }

        return position;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.dialog_select_tv;
    }

    @Override
    protected void screenAdapter() {
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(params);
        window.setGravity(Gravity.LEFT);
        window.setWindowAnimations(R.style.dialogWindowAnim_menu);
    }

    @Override
    protected void initParams() {
        super.initParams();

        mLvChannelType.postDelayed(new Runnable() {
            @Override
            public void run() {
                //初始化栏目列表
                initTypeListUI();
                //初始化栏目子列表
                initChannelListUI();
                initSelectUI();
            }
        }, 200);
    }

    private void initTypeListUI() {
        mChannelTypeAdapter = new ChannelTypeAdapter(mContext, new OnItemListener() {
            @Override
            public void onItemClick(int position) {

            }

            @Override
            public void onItemFocus(int position, boolean hasFocus) {
                if (hasFocus) {
                    selectTypePosition = position;
                }

                if (hasFocus) {
                    //动态刷新栏目子列表
                    mChannelAdapter.setData(mChannelTypes.get(selectTypePosition).channels);
                    refreshChannelListUI();
                }
            }
        });
        mLayoutManagerType = new LinearLayoutManager(context);
        mLvChannelType.setLayoutManager(mLayoutManagerType);
        mLvChannelType.addItemDecoration(new DividerItemDecoration(context, 1));
        mLvChannelType.setAdapter(mChannelTypeAdapter);
        mChannelTypeAdapter.setData(mChannelTypes);
    }

    private void initChannelListUI() {
        mChannelAdapter = new ChannelAdapter(mContext, new OnItemListener() {
            @Override
            public void onItemClick(int position) {
                //选中播放
                ChannelType channelType = mChannelTypes.get(selectTypePosition);
                Channel channel = channelType.channels.get(position);
                EventBus.getDefault().post(new PlayEvent.SelectChannelEvent(channelType, channel));

                dismiss();
            }

            @Override
            public void onItemFocus(int position, boolean hasFocus) {
                if (hasFocus) {
                    selectChannelPosition = position;
                }
            }
        });
        mLayoutManagerChannel = new LinearLayoutManager(context);
        mLvChannel.setLayoutManager(mLayoutManagerChannel);
        mLvChannel.addItemDecoration(new DividerItemDecoration(context, 1));
        mLvChannel.setAdapter(mChannelAdapter);

        mChannelAdapter.setData(mChannelTypes.get(0).channels);
    }

    private void initSelectUI() {
        mLvChannelType.post(new Runnable() {
            @Override
            public void run() {
                focusItem(mLvChannelType, mLayoutManagerType, mCurrentSelectTypePosition, 1);
            }
        });
    }

    @Override
    public boolean dispatchKeyEvent(@NonNull KeyEvent event) {
        EventBus.getDefault().post(new PlayEvent.PressKeyOnDialog(AppConfig.PRESS_CODE_SELECT_TV));
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                dismiss();
                return true;

            case KeyEvent.KEYCODE_DPAD_UP:
                if (event.getRepeatCount() > 0 && event.getRepeatCount() % 4 != 0) {
                    return true;
                }
                if (mLvChannelType.hasFocus()) {
                    if (selectTypePosition == 0) {
                        //滚动至最下面
                        focusItem(mLvChannelType, mLayoutManagerType, typeSize - 1, 0);
                        return true;
                    }
                }
                if (mLvChannel.hasFocus()) {
                    if (selectChannelPosition == 0) {
                        //滚动至最下面
//                        focusItem(mLvChannel, mLayoutManagerChannel, mChannelTypes.get(selectTypePosition).channels.size() - 1, 0);
                        return true;
                    }
                }
                return super.onKeyDown(keyCode, event);

            case KeyEvent.KEYCODE_DPAD_DOWN:
                if (event.getRepeatCount() > 0 && event.getRepeatCount() % 4 != 0) {
                    return true;
                }
                if (mLvChannelType.hasFocus()) {
                    if (selectTypePosition == typeSize - 1) {
                        //滚动至最上面
                        focusItem(mLvChannelType, mLayoutManagerType, 0, 0);
                        return true;
                    }
                }
                if (mLvChannel.hasFocus()) {
                    if (selectChannelPosition == mChannelTypes.get(selectTypePosition).channels.size() - 1) {
                        //滚动至最上面
//                        focusItem(mLvChannel, mLayoutManagerChannel, 0, 0);
                        return true;
                    }
                }
                return super.onKeyDown(keyCode, event);

            case KeyEvent.KEYCODE_DPAD_LEFT:
                if (mLvChannel.hasFocus()) {
                    int position = selectTypePosition;
                    mLvChannelType.requestFocus();
                    refreshTypeListUI(position, true);
                }
                return true;

            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if (mLvChannelType.hasFocus()) {
                    int position = selectTypePosition;
                    mLvChannel.requestFocus();
                    refreshTypeListUI(position, false);
                }
                return true;

            default:
                return super.onKeyDown(keyCode, event);
        }
    }

    private void focusItem(final RecyclerView recyclerView, final LinearLayoutManager layoutManager, final int position, final int isNeedSet) {
        try {
            JLog.d("******->focusItem:" + position);
            layoutManager.scrollToPosition(position);
            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    RecyclerView.ViewHolder holder = recyclerView.findViewHolderForLayoutPosition(position);
                    if (null != holder) {
                        holder.itemView.requestFocus();
                    }

                    if (1 == isNeedSet) {
                        focusItem(mLvChannel, mLayoutManagerChannel, mCurrentSelectPosition, 2);

                        selectTypePosition = mCurrentSelectTypePosition;
                        selectChannelPosition = mCurrentSelectPosition;

                    } else if (2 == isNeedSet) {
                        refreshTypeListUI(mCurrentSelectTypePosition, false);
                    }
                }
            });

        } catch (Exception e) {
            JLog.e(e.getLocalizedMessage());
        }
    }

    private void refreshTypeListUI(int position, boolean hasFocus) {
        JLog.d("******->refreshTypeListUI:" + position);
        RecyclerView.ViewHolder holder = mLvChannelType.findViewHolderForLayoutPosition(position);
        if (null != holder) {
            if (hasFocus) {
                holder.itemView.requestFocus();
                holder.itemView.setBackgroundResource(R.drawable.bg_channel_select);

            } else {
                holder.itemView.setBackgroundResource(R.drawable.bg_channel_s_select);
            }
        }
    }

    private void refreshChannelListUI() {

    }

}
