package com.gsgd.live.data.api;

import com.gsgd.live.data.response.RespBase;
import com.gsgd.live.data.response.RespChannel;
import com.gsgd.live.data.response.RespChannelType;
import com.gsgd.live.data.response.RespSource;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {

    /**
     * 获取频道类型列表
     */
    @POST("channel/channelType.do")
    Call<RespBase<List<RespChannelType>>> getChannelType(
    );

    /**
     * 获取全部频道列表
     */
    @GET("channel/channel.do")
    Call<RespBase<List<RespChannel>>> getChannel();

    /**
     * 获取具体的源播放地址
     */
    @FormUrlEncoded
    @POST("source/source.do")
    Observable<RespBase<RespSource>> getSource(
            @Field("sourceUri") String sourceUri
    );

    /**
     * 举报不能播放的源
     */
    @FormUrlEncoded
    @POST("report/add.do")
    Observable<RespBase<String>> report(
            @Field("sourceUri") String source
    );

}
