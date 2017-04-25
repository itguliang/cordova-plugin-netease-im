package com.netease.nim;

import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.StatusCode;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.RequestCallbackWrapper;

import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.LoginInfo;

import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.RecentContact;

import java.io.File;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;

public class NIMPlugin extends CordovaPlugin {

    @Override
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
        if ("login".equals(action)) {
            login(callbackContext, args.getString(0).toLowerCase(), args.getString(1).toLowerCase());
        }else if("logout".equals(action)){
            logout();
        }else if("sendTextMsg".equals(action)){
            sendTextMsg(callbackContext,args.getString(0),args.getString(1),args.getString(2));
        }else if("getStatus".equals(action)){
            getStatus(callbackContext);
        }else if("queryRecentContacts".equals(action)){
            queryRecentContacts(callbackContext);
        }else if("sendImageMessage".equals(action)){
            sendImageMessage(callbackContext,args.getString(0),args.getString(1),args.getString(2));
        }

        return true;
    }

    private void login(final CallbackContext callbackContext, final String account, final String token) {

        NIMClient.getService(AuthService.class).login(new LoginInfo(account, token))
        .setCallback(new RequestCallback<LoginInfo>() {
            @Override
            public void onSuccess(LoginInfo loginInfo) {
                callbackContext.success();
            }

            @Override
            public void onFailed(int code) {
                callbackContext.error(code);
            }

            @Override
            public void onException(Throwable exception) {
                callbackContext.error(exception.getMessage());
            }
        });
    }
    

    private void getStatus(CallbackContext callbackContext) {
        if (NIMClient.getStatus() == StatusCode.LOGINED){
            callbackContext.success("LOGINED");
        }
        if (NIMClient.getStatus() == StatusCode.UNLOGIN){
            callbackContext.success("UNLOGIN");
        }
    }

    private void logout() {
        NIMClient.getService(AuthService.class).logout();
    }

    private void queryRecentContacts(CallbackContext callbackContext) {
        NIMClient.getService(MsgService.class).queryRecentContacts()
        .setCallback(new RequestCallbackWrapper<List<RecentContact>>() {
            @Override
            public void onSuccess(List<RecentContact> recents) {
                callbackContext.success();
            }

            @Override
            public void onFailed(int code) {
                callbackContext.error(code);
            }

            @Override
            public void onException(Throwable exception) {
                callbackContext.error(exception.getMessage());
            }
        });
    }

    private void sendTextMsg(final CallbackContext callbackContext, final String sessionId, final String sessionType,final String content) {
        IMMessage message = MessageBuilder.createTextMessage(
            sessionId, 
            SessionTypeEnum.P2P, 
            content
            );
        NIMClient.getService(MsgService.class).sendMessage(message,true)
        .setCallback(new RequestCallback<Void>() {
            @Override
            public void onSuccess(Void param) {
                callbackContext.success();
            }

            @Override
            public void onFailed(int code) {
                callbackContext.error(code);
            }

            @Override
            public void onException(Throwable exception) {
                callbackContext.error(exception.getMessage());
            }
        });
    }

    private void sendImageMessage(CallbackContext callbackContext, String sessionId, String sessionType, String file) {
        IMMessage message = MessageBuilder.createImageMessage(
            sessionId, 
            SessionTypeEnum.P2P, 
            new File(file), 
            null // 文件显示名字，如果第三方 APP 不关注，可以为 null
            );
        NIMClient.getService(MsgService.class).sendMessage(message,true)
        .setCallback(new RequestCallback<Void>() {
            @Override
            public void onSuccess(Void param) {
                callbackContext.success();
            }

            @Override
            public void onFailed(int code) {
                callbackContext.error(code);
            }

            @Override
            public void onException(Throwable exception) {
                callbackContext.error(exception.getMessage());
            }
        });
    }

    
}
