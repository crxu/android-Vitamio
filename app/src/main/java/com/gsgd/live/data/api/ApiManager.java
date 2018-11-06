package com.gsgd.live.data.api;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gsgd.live.AppConfig;
import com.gsgd.live.MainApplication;
import com.gsgd.live.data.model.Channel;
import com.gsgd.live.data.model.ChannelType;
import com.gsgd.live.data.response.RespBase;
import com.gsgd.live.data.response.RespChannel;
import com.gsgd.live.data.response.RespChannelType;
import com.gsgd.live.data.response.RespSource;
import com.gsgd.live.utils.SPUtil;
import com.gsgd.live.utils.JLog;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Response;

public class ApiManager {

    private ApiService mApiService;

    public ApiManager(ApiService apiService) {
        this.mApiService = apiService;
    }

    /**
     * 统一判断返回结果是否正确
     */
    private <T> Observable<T> flatResult(final RespBase<T> result) {
        return Observable.create(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(ObservableEmitter<T> e) throws Exception {
                if (200 == result.code) {
                    if (null == result.data) {
                        e.onError(new ApiException(0, "没有数据"));

                    } else {
                        e.onNext(result.data);
                    }

                } else {
                    e.onError(new ApiException(result.code, result.message));
                }

                e.onComplete();
            }
        });
    }

    /**
     * 获取视频地址
     */
    public Observable<RespSource> getSource(final String sourceUri) {
        if (sourceUri.startsWith("sourceUri:")) {
            return mApiService
                    .getSource(sourceUri)
                    .flatMap(new Function<RespBase<RespSource>, ObservableSource<RespSource>>() {
                        @Override
                        public ObservableSource<RespSource> apply(RespBase<RespSource> resp) throws Exception {
                            return flatResult(resp);
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io());

        } else {
            return Observable
                    .create(new ObservableOnSubscribe<RespSource>() {
                        @Override
                        public void subscribe(@NonNull ObservableEmitter<RespSource> e) throws Exception {
                            RespSource respSource = new RespSource();
                            respSource.source = sourceUri;

                            e.onNext(respSource);
                            e.onComplete();
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io());
        }
    }

    public Observable<String> reportSource(String source) {
        return mApiService
                .report(source)
                .flatMap(new Function<RespBase<String>, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(RespBase<String> resp) throws Exception {
                        return flatResult(resp);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io());
    }

    /**
     * 获取缓存的频道列表
     */
    private ArrayList<ChannelType> getCacheChannel() {
        ArrayList<ChannelType> typeList = null;
        try {
            String json = SPUtil.getString(MainApplication.getContext(), AppConfig.SP_NAME, AppConfig.KEY_CHANNEL_LIST, null);

            Type type = new TypeToken<ArrayList<ChannelType>>() {
            }.getType();
            typeList = new Gson().fromJson(json, type);

        } catch (Exception e) {
            JLog.e(e.getLocalizedMessage());
        }

        if (null == typeList) {
            typeList = new ArrayList<>();
        }
        return typeList;
    }

    /**
     * 获取所有频道
     */
    public Observable<ArrayList<ChannelType>> getAllChannel() {
        return Observable
                .create(new ObservableOnSubscribe<ArrayList<ChannelType>>() {
                    @Override
                    public void subscribe(@NonNull ObservableEmitter<ArrayList<ChannelType>> e) throws Exception {
                        ArrayList<ChannelType> typeList = new ArrayList<>();
                        boolean hasData = false;
                        try {
                            //默认生成全部频道
                            ChannelType allType = new ChannelType();
                            allType.id = 0L;
                            allType.type = "全部频道";
                            typeList.add(allType);

                            //请求所有频道
                            Call<RespBase<List<RespChannelType>>> call = mApiService.getChannelType();
                            Response<RespBase<List<RespChannelType>>> response = call.execute();
                            if (null != response
                                    && response.isSuccessful()
                                    && null != response.body()
                                    && response.body().code == 200
                                    && null != response.body().data
                                    && response.body().data.size() > 0) {

                                for (RespChannelType respChannelType : response.body().data) {
                                    ChannelType channelType = new ChannelType();
                                    channelType.id = respChannelType.id;
                                    channelType.type = respChannelType.type;

                                    //加入频道
                                    typeList.add(channelType);
                                }

                                //请求所有节目
                                Call<RespBase<List<RespChannel>>> call_channel = mApiService.getChannel();
                                Response<RespBase<List<RespChannel>>> response_channel = call_channel.execute();

                                if (null != response_channel
                                        && response_channel.isSuccessful()
                                        && null != response_channel.body()
                                        && response_channel.body().code == 200
                                        && null != response_channel.body().data
                                        && response_channel.body().data.size() > 0) {

                                    hasData = true;

                                    for (RespChannel respChannel : response_channel.body().data) {
                                        if (!TextUtils.isEmpty(respChannel.source)) {
                                            Channel channel = new Channel();
                                            channel.id = respChannel.id;
                                            channel.channel = respChannel.channel;

                                            String[] sources = respChannel.source.split("\\|");
                                            channel.sources = Arrays.asList(sources);

                                            if (!TextUtils.isEmpty(respChannel.typeId)) {
                                                String[] ids = respChannel.typeId.split("\\|");
                                                channel.parentId = Arrays.asList(ids);

                                                for (String id : ids) {
                                                    for (ChannelType channelType : typeList) {
                                                        if (String.valueOf(channelType.id).equals(id)) {
                                                            //加入对应的分类频道
                                                            channelType.channels.add(channel);
                                                            break;
                                                        }
                                                    }
                                                }
                                            }

                                            //加入全部频道
                                            allType.channels.add(channel);

                                        } else {
                                            JLog.e("******->" + respChannel.channel + ":" + respChannel.id + ":没有源地址");
                                        }
                                    }

                                    //过滤节目数为0的栏目
                                    for (int i = 0; i < typeList.size(); i++) {
                                        if (null == typeList.get(i).channels
                                                || typeList.get(i).channels.size() == 0) {
                                            typeList.remove(i);
                                            i--;
                                        }
                                    }

                                    //保存列表
                                    JLog.d(typeList.toString());
//                                    SPUtil.putString(MainApplication.getContext(), AppConfig.SP_NAME, AppConfig.KEY_CHANNEL_LIST, typeList.toString());
                                }
                            }

                        } catch (Exception exc) {
                            JLog.e(exc.getLocalizedMessage());
                        }

                        if (hasData) {
                            e.onNext(typeList);
                        } else {
                            e.onError(new ApiException(-1, "没有数据"));
                        }
                        e.onComplete();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io());
    }

}
