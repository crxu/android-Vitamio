package com.gsgd.live.utils;

import com.gsgd.live.data.events.PlayEvent;
import com.gsgd.live.data.model.Channel;
import com.gsgd.live.data.model.ChannelType;
import com.gsgd.live.utils.JLog;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

/**
 * 播放控制工具类
 */
public final class PlayControlUtil {

    public interface PlayInfoListener {

        ArrayList<ChannelType> getChannelTypeList();

        ChannelType getCurrentChannelType();

        Channel getCurrentChannel();

        String getCurrentSource();

    }

    /**
     * 处理切台按键
     *
     * @param direction 0表示下，1表示上
     */
    public static void handlerPressSwitch(int direction, PlayInfoListener listener) {
        int mode_switch = ParamsUtil.getSwitchMode();
        if (direction == 0) {
            //下
            if (mode_switch == 0) {
                //上一个台
                calculationPosition(0, listener);

            } else {
                //下一个台
                calculationPosition(1, listener);
            }

        } else {
            //上
            if (mode_switch == 0) {
                //下一个台
                calculationPosition(1, listener);

            } else {
                //上一个台
                calculationPosition(0, listener);
            }
        }
    }

    /**
     * 计算将要播放的台的位置
     *
     * @param direction 0表示上一个台，1表示下一个台
     */
    private static void calculationPosition(int direction, PlayInfoListener listener) {
        ArrayList<ChannelType> channelTypes = listener.getChannelTypeList();
        ChannelType mCurrentType = listener.getCurrentChannelType();
        Channel mCurrentChannel = listener.getCurrentChannel();

        //计算当前栏目位置
        int typeSize = channelTypes.size();
        int typePosition = 0;
        for (int i = 0; i < typeSize; i++) {
            if (mCurrentType.id == channelTypes.get(i).id) {
                typePosition = i;
                break;
            }
        }

        //计算当前播放频道位置
        int size = mCurrentType.channels.size();
        int position = 0;
        for (int i = 0; i < size; i++) {
            if (mCurrentType.channels.get(i).id == mCurrentChannel.id) {
                position = i;
                break;
            }
        }

        JLog.d("******->当前栏目位置：" + typePosition + "||当前播放频道：" + position);

        if (direction == 0) {
            //上一个台
            position = position - 1;
            if (position < 0) {
                //切换到上一个栏目的最后一个频道
                typePosition = typePosition - 1;
                if (typePosition < 0) {
                    typePosition = typeSize - 1;
                }

                mCurrentType = channelTypes.get(typePosition);
                position = mCurrentType.channels.size() - 1;
            }

        } else {
            //下一个台
            position = position + 1;
            if (position >= size) {
                //切换到下一个栏目的第一个频道
                typePosition = typePosition + 1;
                if (typePosition >= typeSize) {
                    typePosition = 0;
                }

                mCurrentType = channelTypes.get(typePosition);
                position = 0;
            }
        }

        mCurrentChannel = mCurrentType.channels.get(position);

        JLog.d("******->将要播放栏目位置：" + typePosition + "||将要播放频道：" + position);

        EventBus.getDefault().post(new PlayEvent.SelectChannelEvent(mCurrentType, mCurrentChannel));
    }

    /**
     * 切换播放源或者自动换台
     */
    public static void handlerPlayError(PlayInfoListener listener) {
        Channel mCurrentChannel = listener.getCurrentChannel();
        String mCurrentSource = listener.getCurrentSource();

        int position = 0;
        int size = mCurrentChannel.sources.size();
        for (int i = 0; i < size; i++) {
            if (mCurrentSource.equals(mCurrentChannel.sources.get(i))) {
                position = i;
                break;
            }
        }
        position = position + 1;
        if (position > size - 1) {
            //切台
            ToastUtil.showToastLong("当前视频播放不流畅，正在为你自动换台！");

            //下一个台
            calculationPosition(1, listener);

        } else {
            //切源
            ToastUtil.showToastLong("当前视频播放不流畅，正在为你自动换源！");

            EventBus.getDefault().post(new PlayEvent.ChangeSourceEvent(mCurrentChannel.sources.get(position)));
        }
    }

}
