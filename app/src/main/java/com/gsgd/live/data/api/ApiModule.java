package com.gsgd.live.data.api;

import com.gsgd.live.AppConfig;
import com.gsgd.live.MainApplication;
import com.gsgd.live.utils.JLog;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiModule {

    private volatile static ApiManager mNetDataManager;

    private ApiModule() {
    }

    public static ApiManager getApiManager() {
        if (null == mNetDataManager) {
            synchronized (ApiModule.class) {
                if (null == mNetDataManager) {
                    mNetDataManager = new ApiManager(init());
                }
            }
        }
        return mNetDataManager;
    }

    private static ApiService init() {
        //请求拦截器
//        Interceptor interceptor = new Interceptor() {
//            @Override
//            public Response intercept(Chain chain) throws IOException {
//                Request.Builder builder = chain.request().newBuilder();
//                builder.addHeader("appName", "znxy");
//                builder.addHeader("platform", "1");
//                builder.addHeader("appVersion", AppUtil.getAppVersionName(MainApplication.getContext()));
//                builder.addHeader("deviceId", ParamsUtil.getDeviceId());
//                builder.addHeader("token", ParamsUtil.getUserToken());
//                builder.addHeader("lat", SPUtil.getString(MainApplication.getContext(), Constants.SP_APP, Constants.SP_KEY_DEVICE_LAT, "30.201401"));
//                builder.addHeader("lon", SPUtil.getString(MainApplication.getContext(), Constants.SP_APP, Constants.SP_KEY_DEVICE_LON, "120.242501"));
//                return chain.proceed(builder.build());
//            }
//        };

        //日志拦截器
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor()
                .setLevel(AppConfig.isOpenLog ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);

        //缓存
//        Cache cache = new Cache(new File(AppConfig.FOLDER_RESPONSE_CACHE), 10 * 1024 * 1024);

        //初始化OkHttpClient
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
//        httpClientBuilder.addInterceptor(interceptor);
        httpClientBuilder.addNetworkInterceptor(httpLoggingInterceptor);
        if (AppConfig.BASE_API_URL.startsWith("https:")) {
            setSslSocketFactory(httpClientBuilder);
        }
        httpClientBuilder.retryOnConnectionFailure(true);
        httpClientBuilder.connectTimeout(15, TimeUnit.SECONDS);
        httpClientBuilder.readTimeout(15, TimeUnit.SECONDS);
//        httpClientBuilder.cache(cache);
        OkHttpClient httpClient = httpClientBuilder.build();

        //初始化Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConfig.BASE_API_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build();

        return retrofit.create(ApiService.class);
    }

    private static void setSslSocketFactory(OkHttpClient.Builder builder) {
        try {
            // 得到证书的输入流
            InputStream inputStream = MainApplication.getContext().getAssets().open("readyidu.cer");
            //以流的方式读入证书
            X509TrustManager trustManager = trustManagerForCertificates(inputStream);

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{trustManager}, null);
            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            builder.sslSocketFactory(sslSocketFactory, trustManager);

        } catch (Exception e) {
            JLog.e(e.getLocalizedMessage());
        }
    }

    private static X509TrustManager trustManagerForCertificates(InputStream in) throws GeneralSecurityException {
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        Collection<? extends Certificate> certificates = certificateFactory.generateCertificates(in);
        if (certificates.isEmpty()) {
            throw new IllegalArgumentException("expected non-empty set of trusted certificates");
        }

        // Put the certificates a key store.
        char[] password = "password".toCharArray(); // Any password will work.
        KeyStore keyStore = newEmptyKeyStore(password);
        int index = 0;
        for (Certificate certificate : certificates) {
            String certificateAlias = Integer.toString(index++);
            keyStore.setCertificateEntry(certificateAlias, certificate);
        }

        // Use it to build an X509 trust manager.
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, password);
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(keyStore);
        TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();

        if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
            throw new IllegalStateException("Unexpected default trust managers:" + Arrays.toString(trustManagers));
        }

        return (X509TrustManager) trustManagers[0];
    }

    private static KeyStore newEmptyKeyStore(char[] password) throws GeneralSecurityException {
        try {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType()); // 这里添加自定义的密码，默认
            InputStream in = null; // By convention, 'null' creates an empty key store.
            keyStore.load(in, password);
            return keyStore;

        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

}
