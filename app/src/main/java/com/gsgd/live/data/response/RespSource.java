package com.gsgd.live.data.response;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 视频源信息
 */
public class RespSource implements Parcelable {

    public String source;

    public RespSource() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.source);
    }

    protected RespSource(Parcel in) {
        this.source = in.readString();
    }

    public static final Creator<RespSource> CREATOR = new Creator<RespSource>() {
        @Override
        public RespSource createFromParcel(Parcel source) {
            return new RespSource(source);
        }

        @Override
        public RespSource[] newArray(int size) {
            return new RespSource[size];
        }
    };
}
