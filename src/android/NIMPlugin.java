package com.netease.nim;

import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.StatusCode;
import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

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
            getStatus();
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
    

    private void getStatus() {
        StatusCode status = NIMClient.getStatus();
        return status;
    }

    private void logout() {
        NIMClient.getService(AuthService.class).logout();
    }

    private void sendTextMsg(final CallbackContext callbackContext, final String sessionId, final String sessionType,final String content) {
        Log.v("sessionId", sessionId);
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
}
