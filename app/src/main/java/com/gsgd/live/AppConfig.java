package com.gsgd.live;

/**
 * APP配置
 */
public class AppConfig {

    public static boolean isOpenLog = true;//是否打开日志
    public static boolean isDebug = false;
    public static boolean isNeedReport = true;//是否上报数据
    public static boolean isAutoCheck = false;//是否开启自动检测
    public static final long CHECK_TIME = 2;//自动检测间隔 单位分钟

    //API地址
//    http://192.168.4.99:6262/
//    http://112.35.30.146:19090/
    //http://192.168.4.99:9988/
    //http://live.readyidu.com/
    public static final String BASE_API_URL = "http://live.readyidu.com/";

    public static long TIME_SPLASH = 3000;//闪屏时间
    public static long TIME_DISMISS = 5000;//隐藏时间

    public static final int PRESS_CODE_SELECT_TV = 1;
    public static final int PRESS_CODE_SELECT_SOURCE = 2;
    public static final int PRESS_CODE_SELECT_SETTING = 3;

    public static final String CHANNEL_LIST = "channel_list";
    public static final String TV_LIST = "TvList";
    public static final String CHANNEL_MATCH_LIST = "channel_match_list";

    //sp相关信息
    public static final String SP_NAME = "sp_live";
    public static final String KEY_CHANNEL_LIST = "key_channel_list";
    public static final String KEY_SCREEN_MODE = "key_screen_mode";
    public static final String KEY_SWITCH_MODE = "key_switch_mode";
    public static final String KEY_LAST_CHANNEL_TYPE = "key_last_channel_type";
    public static final String KEY_LAST_CHANNEL = "key_last_channel";
    public static final String KEY_LAST_SOURCE = "key_last_source";

    //action
    public static final String ACTION_CLOSE_PLAY_GSGD = "action_close_play_gsgd";

}
