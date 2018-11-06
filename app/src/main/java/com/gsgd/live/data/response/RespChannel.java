package com.gsgd.live.data.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * 节目信息
 */
public class RespChannel implements Parcelable {

    public long id;
    public String channel;//名称
    public String source;//播放源，以"|"分割
    @SerializedName(value = "typeid")
    public String typeId;//对应主栏目分类,有多个，以"|"分割

    public RespChannel() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.channel);
        dest.writeString(this.source);
        dest.writeString(this.typeId);
    }

    protected RespChannel(Parcel in) {
        this.id = in.readLong();
        this.channel = in.readString();
        this.source = in.readString();
        this.typeId = in.readString();
    }

    public static final Creator<RespChannel> CREATOR = new Creator<RespChannel>() {
        @Override
        public RespChannel createFromParcel(Parcel source) {
            return new RespChannel(source);
        }

        @Override
        public RespChannel[] newArray(int size) {
            return new RespChannel[size];
        }
    };
}
