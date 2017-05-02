package com.ionicframework.starter;

import android.app.Application;
import android.content.Context;

import com.netease.nimlib.sdk.NIMClient;

public class NimApplication extends Application {

    public void onCreate() {
        super.onCreate();
        // SDK初始化（启动后台服务，若已经存在用户登录信息，SDK 将完成自动登录）
        NIMClient.init(this, null, null);
    }

}
