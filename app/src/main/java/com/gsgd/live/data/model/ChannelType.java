package com.gsgd.live.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;

import java.util.ArrayList;

public class ChannelType implements Parcelable {

    public long id;
    public String type;
    public ArrayList<Channel> channels = new ArrayList<>();

    public ChannelType() {
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.type);
        dest.writeTypedList(this.channels);
    }

    protected ChannelType(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.type = in.readString();
        this.channels = in.createTypedArrayList(Channel.CREATOR);
    }

    public static final Creator<ChannelType> CREATOR = new Creator<ChannelType>() {
        @Override
        public ChannelType createFromParcel(Parcel source) {
            return new ChannelType(source);
        }

        @Override
        public ChannelType[] newArray(int size) {
            return new ChannelType[size];
        }
    };
}
