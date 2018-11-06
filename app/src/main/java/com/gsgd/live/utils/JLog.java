package com.gsgd.live.utils;

import com.orhanobut.logger.Logger;

import hdpfans.com.BuildConfig;

/**
 * Created by Administrator on 2018/11/5.
 */

public class JLog {
    private static final boolean DEBUG = BuildConfig.DEBUG;

    /**
     * 是否开启debug
     * 注意：使用Eclipse打包的时候记得取消Build Automatically，否则一直是true
     */
    public static boolean isDebug = DEBUG;



    /**
     * 错误
     */
    public static void e(String tag, String msg) {
        if (isDebug) {
            Logger.t(tag).e(msg );
        }
    }

    /**
     * 调试
     */
    public static void d(String tag, String msg) {
        if (isDebug) {
            Logger.t(tag).d(msg );
        }
    }

    /**
     * 信息
     */
    public static void v(String tag, String msg) {
        if (isDebug) {
            Logger.t(tag).v(msg );
        }
    }

    /**
     * 信息
     */
    public static void i(String tag, String msg) {
        if (isDebug) {
            Logger.t(tag).i(msg );
        }
    }


    /**
     * 错误
     */
    public static void e(String msg) {
        if (isDebug) {
            Logger.e("<>"+msg );
        }
    }

    /**
     * 调试
     */
    public static void d(String msg) {
        if (isDebug) {
            Logger.d(msg);
        }
    }

    public static void d(Object o) {
        if (isDebug) {
            Logger.d(o);
        }
    }

    /**
     * 信息
     */
    public static void v(String msg) {
        if (isDebug) {
            Logger.v(msg );
        }
    }

    /**
     * 信息
     */
    public static void i(String msg) {
        if (isDebug) {
            Logger.i(msg );
        }
    }

    public static void json(String json) {
        if (isDebug) {
            Logger.json(json);
        }
    }



    public static void xml(String xml) {
        if (isDebug) {
            Logger.xml(xml);
        }
    }

    public static void json(String tag, String json) {
        if (isDebug) {
            Logger.t(tag).json(json);
        }
    }

    public static void xml(String tag, String xml) {
        if (isDebug) {
            Logger.t(tag).xml(xml);
        }
    }

}
