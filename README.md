# cordova-plugin-netease-im

Ionic1项目中用到网易云信IM功能，试着做个android的插件


## Demo

ionic1添加此插件实现的demo：[cordova-plugin-netease-im-demo](https://github.com/itguliang/cordova-plugin-netease-im-demo)

## Usage

    cordova plugin add https://github.com/itguliang/cordova-plugin-netease-im

	ionic platform add android 

在platforms>android>src>com下会有ionicframework和netease文件夹

在ionicframework下cordovapluginneteaseimdemo782645文件夹下添加网易云信 SDK 的初始化代码

	<!-- NimApplication.java -->
	<!-- cordovapluginneteaseimdemo782645：换成自己的 -->
    
	package com.ionicframework.cordovapluginneteaseimdemo782645;

	import android.app.Application;
	import android.content.Context;

	import com.netease.nimlib.sdk.NIMClient;

	public class NimApplication extends Application {

	    public void onCreate() {
	        super.onCreate();

	        NIMClient.init(this, null, null);
	    }

	}
	

修改AndroidManifest.xml

	<!-- 添加android:name=".NimApplication" -->
	<application android:name=".NimApplication">
		<!-- android:value 换成自己的appKey值 -->
		<meta-data android:name="com.netease.nim.appKey" android:value="91edf0ebde8828cbfa8a6b1502adc8a1" />
	</application>
	

## 概述

`cordova-plugin-netease-im` 更新ing……
