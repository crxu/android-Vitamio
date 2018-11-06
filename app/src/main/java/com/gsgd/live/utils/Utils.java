package com.gsgd.live.utils;

import android.text.TextUtils;

import com.gsgd.live.data.model.Channel;
import com.gsgd.live.data.model.ChannelType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * @author zhangqy
 * @Description
 * @date 2017/7/4
 */
public final class Utils {

    public static String getChannelId(long id) {
        String str = String.valueOf(id);
        if (str.length() == 1) {
            str = "00" + str;

        } else if (str.length() == 2) {
            str = "0" + str;
        }

        return str;
    }

    public static ArrayList<Channel> getMatchChannel(String mTvListStr, ArrayList<ChannelType> value) {
        ArrayList<Channel> list = new ArrayList<>();

        if (!TextUtils.isEmpty(mTvListStr) && null != value && value.size() > 0) {
            String[] strArr = mTvListStr.split("\\|");
            if (strArr.length > 0) {
                for (String tvName : strArr) {
                    for (Channel channel : value.get(0).channels) {
                        if (channel.channel.equals(tvName)) {
                            list.add(channel);
                            break;
                        }
                    }
                }
            }
        }

        //按id大小排序
        Collections.sort(list, new Comparator<Channel>() {
            @Override
            public int compare(Channel o1, Channel o2) {
                return (int) (o1.id - o2.id);
            }
        });

        return list;
    }

}
