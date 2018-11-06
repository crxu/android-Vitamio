package com.gsgd.live.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;

import com.gsgd.live.MainApplication;
import com.gsgd.live.utils.JLog;

import java.text.DecimalFormat;

/**
 * 网速工具类
 */
public class NetworkSpeedUtil {

    private volatile static NetworkSpeedUtil mInstance;

    private NetworkSpeedUtil() {
    }

    public static NetworkSpeedUtil getInstance() {
        if (null == mInstance) {
            synchronized (NetworkSpeedUtil.class) {
                if (null == mInstance) {
                    mInstance = new NetworkSpeedUtil();
                }
            }
        }

        return mInstance;
    }

    private long lastTotalRxBytes = 0;
    private long lastTimeStamp = 0;
    DecimalFormat df = new DecimalFormat("0.00");//格式化

    public String getNetSpeed() {
        try {
            long nowTotalRxBytes = TrafficStats.getUidRxBytes(MainApplication.getContext().getApplicationInfo().uid) == TrafficStats.UNSUPPORTED ? 0 : (TrafficStats.getTotalRxBytes() / 1024);//转为KB
            long nowTimeStamp = System.currentTimeMillis();

            long speed = ((nowTotalRxBytes - lastTotalRxBytes) * 1000 / (nowTimeStamp - lastTimeStamp));//毫秒转换
            long speed2 = ((nowTotalRxBytes - lastTotalRxBytes) * 1000 % (nowTimeStamp - lastTimeStamp));//毫秒转换

            lastTimeStamp = nowTimeStamp;
            lastTotalRxBytes = nowTotalRxBytes;

            double realSpeed = Double.parseDouble(speed + "." + speed2);

            if (realSpeed >= 1024) {
                return df.format(realSpeed / 1024) + " m/s";
            }

            return df.format(realSpeed) + " kb/s";

        } catch (Exception e) {
            JLog.e(e.getLocalizedMessage());
        }

        return "0.0 kb/s";
    }

    /**
     * 判断网络是否连接
     */
    public boolean isConnected() {
        ConnectivityManager connectivity = (ConnectivityManager) MainApplication.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (null != connectivity) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (null != info && info.isConnected()) {
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }

}
