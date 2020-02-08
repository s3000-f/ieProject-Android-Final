package ir.webgroup24.neproject;

import android.app.Application;

import com.androidnetworking.AndroidNetworking;

import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import okhttp3.OkHttpClient;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(200, TimeUnit.MINUTES)
                .writeTimeout(300, TimeUnit.MINUTES)
                .build();
        AndroidNetworking.initialize(getApplicationContext(), okHttpClient);
        Realm.init(this);

    }
}
