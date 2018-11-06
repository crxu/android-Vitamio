package com.gsgd.live.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IntRange;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.devbrackets.android.exomedia.core.video.scale.ScaleType;
import com.devbrackets.android.exomedia.listener.OnBufferUpdateListener;
import com.devbrackets.android.exomedia.listener.OnErrorListener;
import com.devbrackets.android.exomedia.listener.OnPreparedListener;
import com.devbrackets.android.exomedia.ui.widget.VideoView;
import com.google.gson.Gson;
import com.gsgd.live.AppConfig;
import com.gsgd.live.MainApplication;
import com.gsgd.live.data.api.ApiModule;
import com.gsgd.live.data.events.PlayEvent;
import com.gsgd.live.data.model.Channel;
import com.gsgd.live.data.model.ChannelType;
import com.gsgd.live.data.response.RespSource;
import com.gsgd.live.ui.base.BaseActivity;
import com.gsgd.live.ui.base.BaseDialog;
import com.gsgd.live.ui.dialog.SelectMatchTvDialog;
import com.gsgd.live.ui.dialog.SelectSourceDialog;
import com.gsgd.live.ui.dialog.SelectTvDialog;
import com.gsgd.live.ui.dialog.SettingDialog;
import com.gsgd.live.ui.widgets.PlayStatusView;
import com.gsgd.live.utils.NetworkSpeedUtil;
import com.gsgd.live.utils.PlayControlUtil;
import com.gsgd.live.utils.SPUtil;
import com.gsgd.live.utils.ToastUtil;
import com.gsgd.live.utils.Utils;
import com.gsgd.live.utils.JLog;
import com.pili.pldroid.player.AVOptions;
import com.pili.pldroid.player.PLMediaPlayer;
import com.pili.pldroid.player.widget.PLVideoTextureView;
import com.pili.pldroid.player.widget.PLVideoView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.media.VlcVideoView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import hdpfans.com.R;
import io.reactivex.observers.DisposableObserver;
import io.vov.vitamio.widget.CenterLayout;

/**
 * 视频播放界面
 */
public class VideoPlayerActivity extends BaseActivity {

    private static final String TAG = "VideoPlayerActivity";
    private static final int WHAT_SPEED = 888;

    @BindView(R.id.view_vitamio)
    CenterLayout mViewVitamio;
    @BindView(R.id.vitamio_video_view)
    io.vov.vitamio.widget.VideoView mVitamioVideoView;
    @BindView(R.id.vlc_video_view)
    VlcVideoView mVlcVideoView;
    @BindView(R.id.videoView)
    PLVideoTextureView mVideoView;
    @BindView(R.id.exo_video_view)
    VideoView mExoVideoView;
    @BindView(R.id.status_view)
    PlayStatusView mStatusView;
    @BindView(R.id.tv_name)
    TextView mTvName;
    @BindView(R.id.tv_address)
    TextView mTvAddress;

    //获取匹配的频道，以"|"分割
    private String mTvMatchStr;
    //频道列表
    private ArrayList<ChannelType> mChannelTypes;
    //是否获取到了频道列表
    private boolean isGetChannelList = false;
    private boolean mIsActivityPaused = false;

    //记录当前播放的信息
    private ChannelType mCurrentType;//当前栏目
    private Channel mCurrentChannel;//当前节目
    private String mCurrentSource;//当前播放源
    private int mPlayerType = 0;//播放器类型
    private boolean isPreparePlay = false;//是否准备播放
    private NetworkReceiver networkReceiver;

    private SelectTvDialog tvDialog;

    private SelectSourceDialog sourceDialog;

    private SelectMatchTvDialog selectMatchTvDialog;

    // 退出时间
    private long mExitTime = 0;
    // 点击返回键的时间间隔
    private static int TIMES = 2000;


    private void initPlVideo() {
        AVOptions options = new AVOptions();
        // the unit of timeout is ms
        options.setInteger(AVOptions.KEY_PREPARE_TIMEOUT, 15 * 1000);
        options.setInteger(AVOptions.KEY_GET_AV_FRAME_TIMEOUT, 15 * 1000);
        options.setInteger(AVOptions.KEY_PROBESIZE, 128 * 1024);
        options.setInteger(AVOptions.KEY_LIVE_STREAMING, 1);
        options.setInteger(AVOptions.KEY_DELAY_OPTIMIZATION, 1);
        options.setInteger(AVOptions.KEY_MEDIACODEC, 2);
        options.setInteger(AVOptions.KEY_START_ON_PREPARED, 1);

        mVideoView.setAVOptions(options);

        //播放状态监听
        mVideoView.setOnErrorListener(mOnErrorListener);
        mVideoView.setOnPreparedListener(mOnPreparedListener);
        mVideoView.setOnInfoListener(mOnInfoListener);
        mVideoView.setOnCompletionListener(mOnCompletionListener);

        mVideoView.setDisplayAspectRatio(PLVideoView.ASPECT_RATIO_16_9);

//        int mode_screen = SPUtil.getInt(MainApplication.getContext(), AppConfig.SP_NAME, AppConfig.KEY_SCREEN_MODE, 0);
//        refreshScreenMode(mode_screen);
    }

    private void initExoVideo() {
        mExoVideoView.setOnErrorListener(mExoOnErrorListener);
        mExoVideoView.setOnPreparedListener(mExoOnPreparedListener);
        mExoVideoView.setOnBufferUpdateListener(mExoOnBufferUpdateListener);
    }

    private void initVlcVideo() {
        mVlcVideoView.setEventListener(mEventListener);
    }

    private void initVitamioVideo() {
        mVitamioVideoView.setVideoLayout(io.vov.vitamio.widget.VideoView.VIDEO_LAYOUT_STRETCH, 0);

        mVitamioVideoView.setOnErrorListener(new io.vov.vitamio.MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(io.vov.vitamio.MediaPlayer mp, int what, int extra) {
                if (mPlayerType == 3) {
                    handelError();
                }
                return true;
            }
        });

        mVitamioVideoView.setOnPreparedListener(new io.vov.vitamio.MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(io.vov.vitamio.MediaPlayer mp) {
                if (mPlayerType == 3) {
                    mp.start();
                    toggleTip(false);
                }
            }
        });

        mVitamioVideoView.setOnInfoListener(new io.vov.vitamio.MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(io.vov.vitamio.MediaPlayer mp, int what, int extra) {
                if (mPlayerType == 3) {
                    switch (what) {
                        case io.vov.vitamio.MediaPlayer.MEDIA_INFO_BUFFERING_START:
                            toggleTip(true);
                            break;

                        case io.vov.vitamio.MediaPlayer.MEDIA_INFO_BUFFERING_END:
                            toggleTip(false);
                            break;
                    }
                    JLog.d("******->what:" + what + "||extra:" + extra);
                }
                return true;
            }
        });

        mVitamioVideoView.setOnCompletionListener(new io.vov.vitamio.MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(io.vov.vitamio.MediaPlayer mp) {
                if (mPlayerType == 3) {
                    preparePlay(mCurrentSource);
                }
            }
        });
    }

    private void refreshScreenMode(int mode) {
        switch (mode) {
            case 0:
                if (mPlayerType == 0) {
                    mVideoView.setDisplayAspectRatio(PLVideoView.ASPECT_RATIO_FIT_PARENT);

                } else if (mPlayerType == 1) {
                    mExoVideoView.setScaleType(ScaleType.FIT_CENTER);
                }
                break;

            case 1:
                if (mPlayerType == 0) {
                    mVideoView.setDisplayAspectRatio(PLVideoView.ASPECT_RATIO_16_9);

                } else if (mPlayerType == 1) {
                    mExoVideoView.setScaleType(ScaleType.FIT_CENTER);
                }
                break;

            case 2:
                if (mPlayerType == 0) {
                    mVideoView.setDisplayAspectRatio(PLVideoView.ASPECT_RATIO_4_3);

                } else if (mPlayerType == 1) {
                    mExoVideoView.setScaleType(ScaleType.FIT_CENTER);
                }
                break;

            case 3:
                if (mPlayerType == 0) {
                    mVideoView.setDisplayAspectRatio(PLVideoView.ASPECT_RATIO_PAVED_PARENT);

                } else if (mPlayerType == 1) {
                    mExoVideoView.setScaleType(ScaleType.CENTER_CROP);
                }
                break;

            default:
                if (mPlayerType == 0) {
                    mVideoView.setDisplayAspectRatio(PLVideoView.ASPECT_RATIO_FIT_PARENT);

                } else if (mPlayerType == 1) {
                    mExoVideoView.setScaleType(ScaleType.FIT_CENTER);
                }
                break;
        }
    }

    private void preparePlay(final String source) {
        isPreparePlay = true;
        mTvAddress.setVisibility(AppConfig.isDebug ? View.VISIBLE : View.GONE);
        if (AppConfig.isDebug) {
            mTvAddress.setText(source);
        }
        toggleTip(true);

        addDisposable(ApiModule.getApiManager()
                .getSource(source)
                .subscribeWith(new DisposableObserver<RespSource>() {
                    @Override
                    public void onNext(RespSource value) {
                        playVideo(value.source);
                        isPreparePlay = false;
                    }

                    @Override
                    public void onError(Throwable e) {
                        JLog.e(TAG, e.getLocalizedMessage());

                        isPreparePlay = false;

                        handelError();
                    }

                    @Override
                    public void onComplete() {
                    }
                })
        );
    }

    private void playVideo(String path) {
        toggleTip(true);

        JLog.d("******->当前播放地址：" + path);

        HashMap<String, String> map = new HashMap<>();//header参数
        int playerType = 0;//默认使用七牛，1使用EXO，2使用VLC，3使用vitamio

        //解析path，是否需要添加参数
        String[] paths = path.split("#");
        String realPath = paths[0];

        if (paths.length == 2) {
            String[] paths2 = paths[1].split("\\$");
            if (paths2.length == 2) {
                try {
                    playerType = Integer.parseInt(paths2[1]);
                } catch (Exception e) {
                    JLog.e(e.getLocalizedMessage());
                }
            }

            try {
                JSONObject object = new JSONObject(paths2[0]);
                Iterator<String> iterator = object.keys();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    String value = object.optString(key);
                    map.put(key, value);
                }

                JLog.d("******->header参数：" + map);

            } catch (Exception e) {
                JLog.e(e.getLocalizedMessage());
            }

        } else {
            //以$分割
            String[] paths2 = paths[0].split("\\$");
            realPath = paths2[0];
            if (paths2.length == 2) {
                try {
                    playerType = Integer.parseInt(paths2[1]);
                } catch (Exception e) {
                    JLog.e(e.getLocalizedMessage());
                }
            }
        }

        mPlayerType = playerType;
//        mPlayerType = 3;
        hideAndStopVideoByType(mPlayerType);

        if (1 == mPlayerType) {
            //使用EXO播放器
            JLog.d("******->当前使用EXO播放器");
            mExoVideoView.setVideoPath(realPath);

        } else if (2 == mPlayerType) {
            //使用VLC播放器
            JLog.d("******->当前使用VLC播放器");
            mVlcVideoView.play(realPath);

        } else if (3 == mPlayerType) {
            //使用vitamio播放器
            JLog.d("******->当前使用vitamio播放器");
            mVitamioVideoView.setVideoPath(path);

        } else {
            //使用七牛播放器
            JLog.d("******->当前使用七牛播放器");
            mVideoView.setVideoPath(realPath, map);
        }
    }

    private void hideAndStopVideoByType(int type) {
        mVideoView.setVisibility((type != 1 && type != 2 && type != 3) ? View.VISIBLE : View.GONE);
        mExoVideoView.setVisibility(type == 1 ? View.VISIBLE : View.GONE);
        mVlcVideoView.setVisibility(type == 2 ? View.VISIBLE : View.GONE);
        mViewVitamio.setVisibility(type == 3 ? View.VISIBLE : View.GONE);

        try {
            if (1 == type) {
                mVideoView.stopPlayback();
                mVlcVideoView.stop();
                mVitamioVideoView.stopPlayback();

            } else if (2 == type) {
                mVideoView.stopPlayback();
                mExoVideoView.stopPlayback();
                mVitamioVideoView.stopPlayback();

            } else if (3 == type) {
                mVideoView.stopPlayback();
                mExoVideoView.stopPlayback();
                mVlcVideoView.stop();

            } else {
                mExoVideoView.stopPlayback();
                mVlcVideoView.stop();
                mVitamioVideoView.stopPlayback();
            }

        } catch (Exception e) {
            Log.e(TAG, "", e);
        }
    }


    /**
     * 获取频道列表
     */
    private void getChannelList() {
        waiting("");
        addDisposable(ApiModule.getApiManager()
                .getAllChannel()
                .subscribeWith(new DisposableObserver<ArrayList<ChannelType>>() {
                    @Override
                    public void onNext(ArrayList<ChannelType> value) {
                        stopWaiting();
                        if (null != value && value.size() > 0) {
                            isGetChannelList = true;
                            mChannelTypes = value;
                        }

                        handleGetChannelResult();
                    }

                    @Override
                    public void onError(Throwable e) {
                        stopWaiting();
                        JLog.e(e.getMessage());
                        handleGetChannelResult();
                    }

                    @Override
                    public void onComplete() {
                    }
                })
        );
    }

    /**
     * 处理请求频道列表后逻辑
     */
    private void handleGetChannelResult() {
        if (null == mChannelTypes || mChannelTypes.size() == 0) {
            ToastUtil.showToast("无法获取视频资源，请检查网络后再试！");
            finish();
            return;
        }

        //初始化播放器
        initPlVideo();
        initExoVideo();
        initVlcVideo();
        initVitamioVideo();

        ArrayList<Channel> tvMatchList = Utils.getMatchChannel(mTvMatchStr, mChannelTypes);

        if (null != tvMatchList && tvMatchList.size() > 0) {
            //通过命令进入
            JLog.d(TAG, "******通过命令进入->size:" + tvMatchList.size());

            //默认播放匹配列表的第一个频道
            mCurrentType = mChannelTypes.get(0);
            mCurrentChannel = tvMatchList.get(0);

            if (tvMatchList.size() > 1) {
                //弹出框让用户选择
                showSelectMatchTvDialog(mCurrentType, tvMatchList);
            }

        } else {
            try {
                //读取上一次播放的台
                String json_type = SPUtil.getString(MainApplication.getContext(), AppConfig.SP_NAME, AppConfig.KEY_LAST_CHANNEL_TYPE, "");
                String json = SPUtil.getString(MainApplication.getContext(), AppConfig.SP_NAME, AppConfig.KEY_LAST_CHANNEL, "");
                mCurrentSource = SPUtil.getString(MainApplication.getContext(), AppConfig.SP_NAME, AppConfig.KEY_LAST_SOURCE, "");

                if (!TextUtils.isEmpty(json_type) && !TextUtils.isEmpty(json)) {
                    Gson gson = new Gson();
                    mCurrentType = gson.fromJson(json_type, ChannelType.class);
                    mCurrentChannel = gson.fromJson(json, Channel.class);
                }

            } catch (Exception e) {
                JLog.e(TAG, e.getLocalizedMessage());
            }
        }

        if (null == mCurrentType || null == mCurrentChannel) {
            mCurrentType = mChannelTypes.get(0);
            mCurrentChannel = mCurrentType.channels.get(0);
        }
        if (TextUtils.isEmpty(mCurrentSource)) {
            mCurrentSource = mCurrentChannel.sources.get(0);
        }

//        mCurrentSource = "http://202.158.177.67:8081/live/live13/index.m3u8";

        preparePlay(mCurrentSource);

        if (AppConfig.isAutoCheck) {
            openAutoCheck();
        }
    }

    /**
     * 开启源自动检测
     */
    private void openAutoCheck() {
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "******触发自动检测******");
                PlayControlUtil.handlerPlayError(mPlayInfoListener);
            }
        }, AppConfig.CHECK_TIME, AppConfig.CHECK_TIME, TimeUnit.MINUTES);
    }


    private PlayControlUtil.PlayInfoListener mPlayInfoListener = new PlayControlUtil.PlayInfoListener() {
        @Override
        public ArrayList<ChannelType> getChannelTypeList() {
            return mChannelTypes;
        }

        @Override
        public ChannelType getCurrentChannelType() {
            return mCurrentType;
        }

        @Override
        public Channel getCurrentChannel() {
            return mCurrentChannel;
        }

        @Override
        public String getCurrentSource() {
            return mCurrentSource;
        }

    };


    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case AppConfig.PRESS_CODE_SELECT_TV:
                    hideDialog(tvDialog);
                    break;

                case AppConfig.PRESS_CODE_SELECT_SOURCE:
                    hideDialog(sourceDialog);
                    break;

                case AppConfig.PRESS_CODE_SELECT_SETTING:
                    hideDialog(settingDialog);
                    break;

                case WHAT_SPEED:
                    mStatusView.setNetSpeed(NetworkSpeedUtil.getInstance().getNetSpeed());
                    mHandler.sendEmptyMessageDelayed(WHAT_SPEED, 1000);
                    break;

                default:
                    break;
            }

            return true;
        }
    });

    private void hideDialog(BaseDialog dialog) {
        try {
            if (null != dialog && dialog.isShowing()) {
                dialog.dismiss();
            }
        } catch (Exception e) {
            JLog.e(e.getLocalizedMessage());
        }
    }


    private void reportSource(String source) {
        if (AppConfig.isNeedReport) {
            addDisposable(ApiModule.getApiManager()
                    .reportSource(source)
                    .subscribeWith(new DisposableObserver<String>() {
                        @Override
                        public void onNext(String value) {
                            JLog.d("******->举报成功！");
                        }

                        @Override
                        public void onError(Throwable e) {
                            JLog.e(e.getLocalizedMessage());
                        }

                        @Override
                        public void onComplete() {
                        }
                    })
            );
        }
    }

    //VLC
    private MediaPlayer.EventListener mEventListener = new MediaPlayer.EventListener() {
        @Override
        public void onEvent(MediaPlayer.Event event) {
            if (mPlayerType != 2) {
                return;
            }
            switch (event.type) {
                case MediaPlayer.Event.Buffering:
                    toggleTip(event.getBuffering() < 100.0f);
                    break;

                case MediaPlayer.Event.Playing:
                    break;

                case MediaPlayer.Event.EncounteredError:
                    Log.i(TAG, "onEvent: error...");
                    toggleTip(false);
                    handelError();
                    break;
            }
        }
    };

    //EXO
    private OnPreparedListener mExoOnPreparedListener = new OnPreparedListener() {
        @Override
        public void onPrepared() {
            if (mPlayerType == 1) {
                JLog.d("******->mExoOnPreparedListener");
                mExoVideoView.start();
            }
        }
    };

    private OnErrorListener mExoOnErrorListener = new OnErrorListener() {
        @Override
        public boolean onError(Exception e) {
            if (mPlayerType == 1) {
                JLog.e(e.getLocalizedMessage());

                handelError();
            }
            return true;
        }
    };

    private OnBufferUpdateListener mExoOnBufferUpdateListener = new OnBufferUpdateListener() {
        @Override
        public void onBufferingUpdate(@IntRange(from = 0L, to = 100L) int percent) {
            if (mPlayerType == 1) {
                JLog.d("******->onBufferingUpdate:" + percent);
                toggleTip(percent < 100);
            }
        }
    };

    //PLVideoTextureView
    private PLMediaPlayer.OnCompletionListener mOnCompletionListener = new PLMediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(PLMediaPlayer plMediaPlayer) {
            if (mPlayerType == 0) {
                JLog.d("******->mOnCompletionListener");
                preparePlay(mCurrentSource);
            }
        }
    };

    private PLMediaPlayer.OnInfoListener mOnInfoListener = new PLMediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(PLMediaPlayer plMediaPlayer, int what, int extra) {
            if (mPlayerType == 0) {
                switch (what) {
                    case PLMediaPlayer.MEDIA_INFO_BUFFERING_START:
                        toggleTip(true);
                        break;

                    case PLMediaPlayer.MEDIA_INFO_BUFFERING_END:
                        toggleTip(false);
                        break;
                }
                JLog.d("******->what:" + what + "||extra:" + extra);
            }
            return true;
        }
    };

    private PLMediaPlayer.OnPreparedListener mOnPreparedListener = new PLMediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(PLMediaPlayer plMediaPlayer, int i) {
            if (mPlayerType == 0) {
                JLog.d("******->mOnPreparedListener");
                toggleTip(false);
            }
        }
    };

    private PLMediaPlayer.OnErrorListener mOnErrorListener = new PLMediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(PLMediaPlayer mp, int errorCode) {
            if (mPlayerType == 0) {
                boolean isNeedReconnect = false;
                switch (errorCode) {
                    case PLMediaPlayer.ERROR_CODE_CONNECTION_TIMEOUT:
                        JLog.e("Connection timeout !");
//                        isNeedReconnect = true;
                        break;
                    case PLMediaPlayer.ERROR_CODE_STREAM_DISCONNECTED:
                        JLog.e("Stream disconnected !");
//                        isNeedReconnect = true;
                        break;
                    case PLMediaPlayer.ERROR_CODE_IO_ERROR:
                        JLog.e("Network IO Error !");
//                        isNeedReconnect = true;
                        break;
                    case PLMediaPlayer.ERROR_CODE_PREPARE_TIMEOUT:
                        JLog.e("Prepare timeout !");
//                        isNeedReconnect = true;
                        break;
                    case PLMediaPlayer.ERROR_CODE_READ_FRAME_TIMEOUT:
                        JLog.e("Read frame timeout !");
                        isNeedReconnect = true;
                        break;

                    case PLMediaPlayer.ERROR_CODE_INVALID_URI:
                        JLog.e("Invalid URL !");
                        break;
                    case PLMediaPlayer.ERROR_CODE_404_NOT_FOUND:
                        JLog.e("404 resource not found !");
                        break;
                    case PLMediaPlayer.ERROR_CODE_CONNECTION_REFUSED:
                        JLog.e("Connection refused !");
                        break;
                    case PLMediaPlayer.ERROR_CODE_EMPTY_PLAYLIST:
                        JLog.e("Empty playlist !");
                        break;
                    case PLMediaPlayer.ERROR_CODE_UNAUTHORIZED:
                        JLog.e("Unauthorized Error !");
                        break;
                    case PLMediaPlayer.MEDIA_ERROR_UNKNOWN:
                        JLog.e("media error unknown !");
                        break;
                    default:
                        JLog.e("unknown error !");
                        break;
                }

                toggleTip(false);

                if (isNeedReconnect) {
                    //重连
                    preparePlay(mCurrentSource);

                } else {
                    handelError();
                }
            }
            return true;
        }
    };

    private void handelError() {
        reportSource(mCurrentSource);
        //切换播放源或者自动换台
        PlayControlUtil.handlerPlayError(mPlayInfoListener);
    }

    private void toggleTip(boolean needShow) {
        toggleStatusView(needShow);
        toggleTvNameView(needShow);
    }

    private void toggleStatusView(boolean needShow) {
        mStatusView.setVisibility(needShow ? View.VISIBLE : View.GONE);

        if (needShow) {
            mHandler.sendEmptyMessage(WHAT_SPEED);

        } else {
            mHandler.removeMessages(WHAT_SPEED);
        }

    }

    private void toggleTvNameView(boolean needShow) {
        mTvName.setVisibility(needShow ? View.VISIBLE : View.GONE);
        if (needShow) {
            mTvName.setText(Utils.getChannelId(mCurrentChannel.id) + " : " + mCurrentChannel.channel);
        }
    }

    private void registerNetReceiver() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(AppConfig.ACTION_CLOSE_PLAY_GSGD);
        networkReceiver = new NetworkReceiver();
        registerReceiver(networkReceiver, filter);
    }

    private class NetworkReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (null != intent) {
                String action = intent.getAction();
                if (AppConfig.ACTION_CLOSE_PLAY_GSGD.equals(action)) {

                    pauseVideo();

                } else if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
                    if (!NetworkSpeedUtil.getInstance().isConnected()) {
                        ToastUtil.showToast("网络已断开!");
                        //TODO

                    } else {
                        //TODO
                    }
                }
            }
        }
    }

    private void pauseVideo() {
        try {
            mIsActivityPaused = true;
            mVideoView.pause();
            mExoVideoView.pause();
            mVlcVideoView.pause();
            mVitamioVideoView.pause();

        } catch (Exception e) {
            Log.e(TAG, "", e);
        }
    }


    /**
     * 显示选择栏目界面
     */
    private void showSelectTvDialog() {
        hideDialog(tvDialog);

        tvDialog = new SelectTvDialog(mContext, R.style.TransparentDialog, mChannelTypes, mCurrentType, mCurrentChannel);
        tvDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mHandler.removeMessages(AppConfig.PRESS_CODE_SELECT_TV);
            }
        });
        tvDialog.show();

        //几秒钟无操作后自动隐藏
        mHandler.sendEmptyMessageDelayed(AppConfig.PRESS_CODE_SELECT_TV, AppConfig.TIME_DISMISS);
    }


    /**
     * 显示选择切源界面
     */
    private void showSelectSourceDialog() {
        hideDialog(sourceDialog);

        sourceDialog = new SelectSourceDialog(mContext, R.style.TransparentDialog, mCurrentChannel.sources, mCurrentSource);
        sourceDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mHandler.removeMessages(AppConfig.PRESS_CODE_SELECT_SOURCE);
            }
        });
        sourceDialog.show();

        //几秒钟无操作后自动隐藏
        mHandler.sendEmptyMessageDelayed(AppConfig.PRESS_CODE_SELECT_SOURCE, AppConfig.TIME_DISMISS);
    }

    SettingDialog settingDialog;

    /**
     * 显示设置界面
     */
    private void showSettingDialog() {
        hideDialog(settingDialog);

        settingDialog = new SettingDialog(mContext, R.style.TransparentDialog, new SettingDialog.OnSettingListener() {
            @Override
            public void setScreenMode(int mode) {
                refreshScreenMode(mode);
            }
        });
        settingDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mHandler.removeMessages(AppConfig.PRESS_CODE_SELECT_SETTING);
            }
        });
        settingDialog.show();

        //几秒钟无操作后自动隐藏
        mHandler.sendEmptyMessageDelayed(AppConfig.PRESS_CODE_SELECT_SETTING, AppConfig.TIME_DISMISS);
    }


    /**
     * 显示选择匹配的源界面
     */
    private void showSelectMatchTvDialog(ChannelType mCurrentType, ArrayList<Channel> tvList) {
        hideDialog(selectMatchTvDialog);
        selectMatchTvDialog = new SelectMatchTvDialog(mContext, R.style.TransparentDialog, mCurrentType, tvList);
        selectMatchTvDialog.show();
    }



    /*-----------------------------------------------------------*/

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_video_player;
    }

    @Override
    protected void initParams() {
        super.initParams();
        Intent intent = getIntent();
        mTvMatchStr = intent.getStringExtra(AppConfig.TV_LIST);
        isGetChannelList = false;
        getChannelList();
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

        Intent intent_new = getIntent();
        String tvMatchStr = intent_new.getStringExtra(AppConfig.TV_LIST);

        ArrayList<Channel> tvList = Utils.getMatchChannel(tvMatchStr, mChannelTypes);

        if (null != tvList && tvList.size() > 0) {
            //通过命令进入
            JLog.d(TAG, "******通过命令进入->size:" + tvList.size());

            ChannelType channelType = mChannelTypes.get(0);
            if (tvList.size() == 1) {
                Channel channel = tvList.get(0);
                EventBus.getDefault().post(new PlayEvent.SelectChannelEvent(channelType, channel));

            } else {
                //弹出框让用户选择
                showSelectMatchTvDialog(channelType, tvList);
            }
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        mIsActivityPaused = true;

        //记录当前播放的台
        if (null != mCurrentType && null != mCurrentChannel) {
            JLog.d("******->记录当前播放的台：" + mCurrentChannel.toString());
            SPUtil.putString(MainApplication.getContext(), AppConfig.SP_NAME, AppConfig.KEY_LAST_CHANNEL_TYPE, mCurrentType.toString());
            SPUtil.putString(MainApplication.getContext(), AppConfig.SP_NAME, AppConfig.KEY_LAST_CHANNEL, mCurrentChannel.toString());
            SPUtil.putString(MainApplication.getContext(), AppConfig.SP_NAME, AppConfig.KEY_LAST_SOURCE, mCurrentSource);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (networkReceiver == null) {
            registerNetReceiver();
        }

        if (isGetChannelList) {
            if (mIsActivityPaused) {
                try {
                    if (1 == mPlayerType) {
                        mExoVideoView.start();

                    } else if (2 == mPlayerType) {
                        mVlcVideoView.play();

                    } else if (3 == mPlayerType) {
                        mVitamioVideoView.start();

                    } else {
                        mVideoView.start();
                    }

                } catch (Exception e) {
                    Log.e(TAG, "", e);
                }
            }
        }

        mIsActivityPaused = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (networkReceiver != null) {
            unregisterReceiver(networkReceiver);
        }

        try {
            mVideoView.stopPlayback();
            mExoVideoView.release();
            mVlcVideoView.release();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (isGetChannelList && (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER)) {
            showSelectTvDialog();
            return true;
        }

//        if (isGetChannelList && keyCode == KeyEvent.KEYCODE_MENU) {
//            showSettingDialog();
//            return true;
//        }

        if (isGetChannelList && (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT)) {
            showSelectSourceDialog();
            return true;
        }

        if (isGetChannelList && keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            if (!isPreparePlay) {
                PlayControlUtil.handlerPressSwitch(0, mPlayInfoListener);
            }
            return true;
        }

        if (isGetChannelList && keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            if (!isPreparePlay) {
                PlayControlUtil.handlerPressSwitch(1, mPlayInfoListener);
            }
            return true;
        }

        return super.onKeyUp(keyCode, event);
    }


    @Override
    public void onBackPressed() {
        if ((System.currentTimeMillis() - mExitTime) > TIMES) {
            ToastUtil.showToast("再按一次退出视频播放！");
            mExitTime = System.currentTimeMillis();

        } else {
            super.onBackPressed();
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPressKeyOnDialogEvent(PlayEvent.PressKeyOnDialog event) {
        mHandler.removeMessages(event.type);
        mHandler.sendEmptyMessageDelayed(event.type, AppConfig.TIME_DISMISS);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSelectChannelEvent(PlayEvent.SelectChannelEvent event) {
        if (null != event) {
            mCurrentType = event.channelType;
            if (null == mCurrentChannel || mCurrentChannel.id != event.channel.id) {
                mCurrentChannel = event.channel;
                onChangeSourceEvent(new PlayEvent.ChangeSourceEvent(event.channel.sources.get(0)));

            } else {
                JLog.d("当前频道已在播放！");
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChangeSourceEvent(PlayEvent.ChangeSourceEvent event) {
        if (null != event && !TextUtils.isEmpty(event.source)) {
            if (event.source.equals(mCurrentSource)) {
                JLog.d("当前源已在播放！");
                return;
            }

            mCurrentSource = event.source;

            preparePlay(mCurrentSource);
        }
    }

}
