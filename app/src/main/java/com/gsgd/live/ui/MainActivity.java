package com.gsgd.live.ui;

import android.content.Intent;
import android.os.Handler;

import com.gsgd.live.AppConfig;
import hdpfans.com.R;
import com.gsgd.live.data.api.ApiModule;
import com.gsgd.live.data.model.ChannelType;
import com.gsgd.live.ui.base.BaseActivity;
import com.gsgd.live.utils.JLog;
import com.gsgd.live.utils.Utils;


import java.util.ArrayList;

import io.reactivex.observers.DisposableObserver;

/**
 * 主界面
 */
public class MainActivity extends BaseActivity {

    String mTvListStr;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initParams() {
        super.initParams();

        Intent intent = getIntent();
        mTvListStr = intent.getStringExtra(AppConfig.TV_LIST);

        waiting("");
        initData();
    }

    @Override
    public void onBackPressed() {

    }

    private void initData() {
        final long startTime = System.currentTimeMillis();
        addDisposable(ApiModule.getApiManager()
                .getAllChannel()
                .subscribeWith(new DisposableObserver<ArrayList<ChannelType>>() {
                    @Override
                    public void onNext(ArrayList<ChannelType> value) {
                        stopWaiting();
                        jumpNextPageTemp(startTime, value);
                    }

                    @Override
                    public void onError(Throwable e) {
                        stopWaiting();
                        JLog.e(e.getLocalizedMessage());
                        jumpNextPageTemp(startTime, null);
                    }

                    @Override
                    public void onComplete() {
                    }
                })
        );
    }

    private void jumpNextPageTemp(long startTime, ArrayList<ChannelType> value) {
        long endTime = System.currentTimeMillis();
        long dex = AppConfig.TIME_SPLASH - (endTime - startTime);
        JLog.d("******->dex:" + dex);
        if (dex > 0) {
            jumpNextPage(dex, value);

        } else {
            jumpNextPage(10, value);
        }
    }

    private void jumpNextPage(long time, final ArrayList<ChannelType> value) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (null == value || value.size() <= 0) {
                    finish();
                    return;
                }

                //跳转至视频播放界面
                Intent intent = new Intent(MainActivity.this, VideoPlayerActivity.class);
                intent.putParcelableArrayListExtra(AppConfig.CHANNEL_LIST, value);
                intent.putParcelableArrayListExtra(AppConfig.CHANNEL_MATCH_LIST, Utils.getMatchChannel(mTvListStr, value));
                mContext.startActivity(intent);
                finish();
                overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
            }
        }, time);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        JLog.d("dshdsjhd");
    }

}
