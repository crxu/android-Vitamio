package com.gsgd.live.data.response;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 栏目信息
 */
public class RespChannelType implements Parcelable {

    public long id;//栏目id
    public String type;//栏目名称

    public RespChannelType() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.type);
    }

    protected RespChannelType(Parcel in) {
        this.id = in.readLong();
        this.type = in.readString();
    }

    public static final Creator<RespChannelType> CREATOR = new Creator<RespChannelType>() {
        @Override
        public RespChannelType createFromParcel(Parcel source) {
            return new RespChannelType(source);
        }

        @Override
        public RespChannelType[] newArray(int size) {
            return new RespChannelType[size];
        }
    };
}
