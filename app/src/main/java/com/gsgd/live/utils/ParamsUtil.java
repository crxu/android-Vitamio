package com.gsgd.live.utils;

import com.gsgd.live.AppConfig;
import com.gsgd.live.MainApplication;

/**
 * 数据管理
 */
public class ParamsUtil {

    private static int switchMode = -1;

    /**
     * 获取操控切换模式
     */
    public static int getSwitchMode() {
        if (-1 == switchMode) {
            switchMode = SPUtil.getInt(MainApplication.getContext(), AppConfig.SP_NAME, AppConfig.KEY_SWITCH_MODE, 1);
        }
        return switchMode;
    }

    private static int screenMode = -1;

    /**
     * 获取屏幕缩放模式
     */
    public static int getScreenMode() {
        if (-1 == screenMode) {
            screenMode = SPUtil.getInt(MainApplication.getContext(), AppConfig.SP_NAME, AppConfig.KEY_SCREEN_MODE, 0);
        }
        return screenMode;
    }

}
