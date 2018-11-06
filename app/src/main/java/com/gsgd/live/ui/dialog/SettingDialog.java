package com.gsgd.live.ui.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.WindowManager;

import com.gsgd.live.AppConfig;
import com.gsgd.live.MainApplication;
import hdpfans.com.R;
import com.gsgd.live.data.events.PlayEvent;
import com.gsgd.live.data.listener.OnItemListener;
import com.gsgd.live.ui.adapter.SettingAdapter;
import com.gsgd.live.ui.adapter.SettingMenuAdapter;
import com.gsgd.live.ui.base.BaseDialog;
import com.gsgd.live.ui.widgets.DividerItemDecoration;
import com.gsgd.live.utils.MeasureUtil;
import com.gsgd.live.utils.SPUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.BindView;

/**
 * 设置界面
 */
public class SettingDialog extends BaseDialog {

    @BindView(R.id.rv_list)
    RecyclerView mRvList;
    @BindView(R.id.lv_list)
    RecyclerView mLvMenu;

    SettingAdapter mSettingAdapter;
    SettingMenuAdapter mAdapter;

    List<String> mScreenModeList = new ArrayList<>();
    List<String> mSwitchModeList = new ArrayList<>();
    private int mListPosition = 0;
    private int mMenuPosition = 0;

    private OnSettingListener mListener;

    public SettingDialog(Context context, int theme, OnSettingListener listener) {
        super(context, theme);
        this.mListener = listener;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.dialog_setting;
    }

    @Override
    protected void screenAdapter() {
        WindowManager.LayoutParams p = getWindow().getAttributes();
        p.width = WindowManager.LayoutParams.WRAP_CONTENT;
        p.height = MeasureUtil.perh2px(context.getResources(), 0.5);
        p.gravity = Gravity.CENTER;
        getWindow().setAttributes(p);
    }

    @Override
    protected void initParams() {
        super.initParams();

        mScreenModeList.clear();
        mSwitchModeList.clear();
        mScreenModeList.add("原始大小");
        mScreenModeList.add("16 : 9");
        mScreenModeList.add("4 : 3");
        mScreenModeList.add("全屏");
        mSwitchModeList.add("上键加台");
        mSwitchModeList.add("下键加台");

        initMenu();
        initUI();

        mRvList.requestFocus();
    }

    private void initMenu() {
        mAdapter = new SettingMenuAdapter(context, new OnItemListener() {
            @Override
            public void onItemClick(int position) {
                if (mAdapter.getType() == 0) {
                    SPUtil.putInt(MainApplication.getContext(), AppConfig.SP_NAME, AppConfig.KEY_SCREEN_MODE, position);
                    if (null != mListener) {
                        mListener.setScreenMode(position);
                    }

                } else if (mAdapter.getType() == 1) {
                    SPUtil.putInt(MainApplication.getContext(), AppConfig.SP_NAME, AppConfig.KEY_SWITCH_MODE, position);
                }

                dismiss();
            }

            @Override
            public void onItemFocus(int position, boolean hasFocus) {
                if (hasFocus) {
                    mMenuPosition = position;
                }
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        mLvMenu.setLayoutManager(layoutManager);
        mLvMenu.addItemDecoration(new DividerItemDecoration(context, 40));
        mLvMenu.setAdapter(mAdapter);
    }

    private void initUI() {
        mSettingAdapter = new SettingAdapter(context, new OnItemListener() {
            @Override
            public void onItemClick(int position) {

            }

            @Override
            public void onItemFocus(int position, boolean hasFocus) {
                if (hasFocus) {
                    mListPosition = position;
                }
                if (position == 0 && hasFocus) {
                    mAdapter.setData(mScreenModeList);
                    mAdapter.setType(0);

                } else if (position == 1 && hasFocus) {
                    mAdapter.setData(mSwitchModeList);
                    mAdapter.setType(1);
                }
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        mRvList.setLayoutManager(layoutManager);
        mRvList.addItemDecoration(new DividerItemDecoration(context, 180));
        mRvList.setAdapter(mSettingAdapter);

        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("屏幕比例：", getScreenMode());
        map.put("上下换台：", getSwitchMode());

        List<String> list = new ArrayList<>();
        list.add("屏幕比例：");
        list.add("上下换台：");
        mSettingAdapter.setData(map, list);
    }

    private String getScreenMode() {
        int mode_screen = SPUtil.getInt(MainApplication.getContext(), AppConfig.SP_NAME, AppConfig.KEY_SCREEN_MODE, 0);
        switch (mode_screen) {
            case 0:
                //原始比例
                return "原始大小";

            case 1:
                //16：9
                return "16 : 9";

            case 2:
                //4：3
                return "4 : 3";

            case 3:
                //全屏
                return "全屏";

            default:
                return "原始大小";
        }
    }

    private String getSwitchMode() {
        int mode_switch = SPUtil.getInt(MainApplication.getContext(), AppConfig.SP_NAME, AppConfig.KEY_SWITCH_MODE, 1);
        if (0 == mode_switch) {
            return "上键加台";

        } else {
            return "下键加台";
        }
    }

    public interface OnSettingListener {

        void setScreenMode(int mode);
    }

    @Override
    public boolean dispatchKeyEvent(@NonNull KeyEvent event) {
        EventBus.getDefault().post(new PlayEvent.PressKeyOnDialog(AppConfig.PRESS_CODE_SELECT_SETTING));
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        switch (keyCode) {

            case KeyEvent.KEYCODE_BACK:
                dismiss();
                return true;

            case KeyEvent.KEYCODE_DPAD_UP:
                if (mRvList.hasFocus()) {
                    return mListPosition == 0;
                }

                if (mLvMenu.hasFocus()) {
                    return mMenuPosition == 0;
                }

                return super.onKeyDown(keyCode, event);

            case KeyEvent.KEYCODE_DPAD_DOWN:
                if (mRvList.hasFocus()) {
                    return mListPosition == 1;
                }

                if (mLvMenu.hasFocus()) {
                    return mAdapter.getType() == 1
                            ? (mMenuPosition == mSwitchModeList.size() - 1)
                            : (mMenuPosition == mScreenModeList.size() - 1);
                }

                return super.onKeyDown(keyCode, event);

            case KeyEvent.KEYCODE_DPAD_LEFT:
                if (mLvMenu.hasFocus()) {
                    int position = mListPosition;
                    mRvList.requestFocus();
                    refreshListUI(position, true);
                }
                return true;

            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if (mRvList.hasFocus()) {
                    int position = mListPosition;
                    mLvMenu.requestFocus();
                    refreshListUI(position, false);
                }
                return true;

            default:
                return super.onKeyDown(keyCode, event);
        }
    }

    private void refreshListUI(int position, boolean hasFocus) {
        RecyclerView.ViewHolder holder = mRvList.findViewHolderForLayoutPosition(position);
        if (null != holder && holder instanceof SettingAdapter.ViewHolder) {
            if (hasFocus) {
                holder.itemView.requestFocus();
                holder.itemView.setBackgroundResource(R.drawable.bg_setting_m_select);
            } else {
                holder.itemView.setBackgroundResource(R.drawable.bg_setting_ml_select);
            }
        }
    }

}
