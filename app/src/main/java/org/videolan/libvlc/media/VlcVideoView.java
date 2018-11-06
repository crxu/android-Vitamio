package org.videolan.libvlc.media;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.TextureView;

import com.gsgd.live.utils.JLog;

import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;

import java.util.ArrayList;

public class VlcVideoView extends TextureView {

    private final String tag = "VlcVideoView";

    private boolean isAttached = false;

    private LibVLC libvlc;
    private Media media;
    private MediaPlayer mediaPlayer = null;
    private IVLCVout ivlcVout;
    private MediaPlayer.EventListener mEventListener;

    public VlcVideoView(Context context) {
        this(context, null);
    }

    public VlcVideoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VlcVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (isInEditMode()) {
            return;
        }

        ArrayList<String> options = new ArrayList<>();
        options.add("--aout=opensles");
        options.add("--audio-time-stretch");
        options.add("-vvv");
        libvlc = new LibVLC(context, options);

        mediaPlayer = new MediaPlayer(libvlc);
        mediaPlayer.setEventListener(eventListener);

        setSurfaceTextureListener(textureListener);

        ivlcVout = mediaPlayer.getVLCVout();
        ivlcVout.setVideoView(VlcVideoView.this);
    }

    public void setEventListener(MediaPlayer.EventListener eventListener) {
        this.mEventListener = eventListener;
    }

    private MediaPlayer.EventListener eventListener = new MediaPlayer.EventListener() {
        @Override
        public void onEvent(MediaPlayer.Event event) {
            if (null != mEventListener) {
                mEventListener.onEvent(event);
            }
        }
    };

    private TextureView.SurfaceTextureListener textureListener = new SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            ivlcVout.attachViews();
            isAttached = true;

            mediaPlayer.getVLCVout().setWindowSize(width, height);
            mediaPlayer.setAspectRatio("16:9");
            mediaPlayer.setScale(0);

            play();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, final int width, final int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            //TODO
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };

    //根据播放状态 打开关闭旋转动画
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (isInEditMode()) {
            return;
        }
        setKeepScreenOn(true);
        //TODO
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (isInEditMode()) {
            return;
        }
        setKeepScreenOn(false);
        //TODO
    }

    /**
     * 播放
     *
     * @param url 视频地址
     */
    public void play(String url) {
        media = new Media(libvlc, Uri.parse(url));

        if (isAttached) {
            play();
        }
    }

    public void play() {
        if (null != media) {
            stop();
            mediaPlayer.setMedia(media);
            mediaPlayer.play();
        }
    }

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    public void pause() {
        mediaPlayer.pause();
    }

    public void stop() {
        mediaPlayer.stop();
    }

    public void release() {
        try {
            if (mediaPlayer != null) {
                if (!mediaPlayer.isReleased()) {
                    mediaPlayer.release();
                    ivlcVout.detachViews();
                    libvlc.release();
                }
            }
        } catch (Exception e) {
            JLog.e(e.getLocalizedMessage());
        }
    }

}