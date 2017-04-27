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

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;

public class NIMPlugin extends CordovaPlugin {

    private static final String TAG = "NIMPlugin";

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
        }else if("pullMessageHistory".equals(action)){
            pullMessageHistory(callbackContext,args.getString(0),args.getString(1),args.getInt(2),args.getBoolean(3));
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
        Log.i(TAG, "logout");
        NIMClient.getService(AuthService.class).logout();
    }

    private void queryRecentContacts(final CallbackContext callbackContext) {
        NIMClient.getService(MsgService.class).queryRecentContacts()
        .setCallback(new RequestCallbackWrapper<List<RecentContact>>() {
            @Override
            public void onResult(int code, List<RecentContact> recents, Throwable e) {
                JSONArray json = new JSONArray();
                try {  
                    for(RecentContact r : recents){
                        JSONObject jo = new JSONObject();
                        jo.put("id", r.getContactId());
                        jo.put("content", r.getContent());
                        jo.put("unreadCount", r.getUnreadCount());
                        jo.put("msgType", r.getMsgType());
                        jo.put("time", r.getTime());
                        jo.put("sessionType", r.getSessionType());
                        jo.put("recentMessageId", r.getRecentMessageId());
                        jo.put("fromAccount", r.getFromAccount());
                        jo.put("fromNick", r.getFromNick());
                        json.put(jo);
                    }  
                } catch (JSONException je) {  
                    je.printStackTrace();  
                }  
                
                callbackContext.success(json);
            }
        });
    }

    private void sendTextMsg(final CallbackContext callbackContext,String sessionId,String sessionType,String content) {
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

    private void sendImageMessage(final CallbackContext callbackContext,String sessionId,String sessionType,String filePath) {
        IMMessage message = MessageBuilder.createImageMessage(
            sessionId, 
            SessionTypeEnum.P2P, 
            new File(filePath), 
            null // 文件显示名字，如果第三方 APP 不关注，可以为 null
            );
        // File file = new File(filePath);
        // callbackContext.success(filePath+":"+file.length());
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

    private void pullMessageHistory(final CallbackContext callbackContext,String sessionId,String sessionType,Integer limit,Boolean persist) {
        IMMessage imMessage = MessageBuilder.createEmptyMessage(sessionId, SessionTypeEnum.valueOf(sessionType), 0);
        NIMClient.getService(MsgService.class).pullMessageHistory(imMessage,10,persist)
        .setCallback(new RequestCallbackWrapper<List<IMMessage>>() {
            @Override
            public void onResult(int code, List<IMMessage> messages, Throwable e) {
                JSONArray json = new JSONArray();
                try {  
                    for(IMMessage m : messages){
                        JSONObject jo = new JSONObject();
                        jo.put("sessionId", m.getSessionId());
                        jo.put("fromAccount", m.getFromAccount());
                        jo.put("msgType", m.getMsgType());
                        jo.put("content", m.getContent());
                        json.put(jo);
                    }  
                } catch (JSONException je) {  
                    je.printStackTrace();  
                }
                callbackContext.success(json);
            }

        });
    }
    
}