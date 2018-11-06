package com.gsgd.live.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class Channel implements Parcelable {

    public long id;
    public List<String> parentId = new ArrayList<>();//对应的栏目id列表
    public String channel;//节目名称
    public List<String> sources = new ArrayList<>();//播放源地址

    public Channel() {
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
        dest.writeLong(this.id);
        dest.writeStringList(this.parentId);
        dest.writeString(this.channel);
        dest.writeStringList(this.sources);
    }

    protected Channel(Parcel in) {
        this.id = in.readLong();
        this.parentId = in.createStringArrayList();
        this.channel = in.readString();
        this.sources = in.createStringArrayList();
    }

    public static final Creator<Channel> CREATOR = new Creator<Channel>() {
        @Override
        public Channel createFromParcel(Parcel source) {
            return new Channel(source);
        }

        @Override
        public Channel[] newArray(int size) {
            return new Channel[size];
        }
    };
}
