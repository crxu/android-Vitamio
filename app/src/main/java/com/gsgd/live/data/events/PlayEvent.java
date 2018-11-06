package com.gsgd.live.data.events;

import com.gsgd.live.data.model.Channel;
import com.gsgd.live.data.model.ChannelType;

public class PlayEvent {

    /**
     * 切换播放地址
     */
    public static class ChangeSourceEvent {

        public String source;

        public ChangeSourceEvent(String source) {
            this.source = source;
        }
    }

    /**
     * 选择某频道
     */
    public static class SelectChannelEvent {

        public ChannelType channelType;
        public Channel channel;

        public SelectChannelEvent(ChannelType channelType, Channel channel) {
            this.channelType = channelType;
            this.channel = channel;
        }
    }

    /**
     * 在弹框界面按了按键
     */
    public static class PressKeyOnDialog {

        public int type;

        public PressKeyOnDialog(int type) {
            this.type = type;
        }
    }


}
