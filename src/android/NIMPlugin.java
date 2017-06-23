package com.netease.nim;
import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.security.MessageDigest;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
// import org.apache.http.HttpResponse;
// import org.apache.http.NameValuePair;
// import org.apache.http.client.HttpClient; 
// import org.apache.http.client.entity.UrlEncodedFormEntity;
// import org.apache.http.client.methods.HttpPost;
// import org.apache.http.impl.client.DefaultHttpClient;
// import org.apache.http.message.BasicNameValuePair;
// import org.apache.http.util.EntityUtils;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;

import com.netease.nimlib.sdk.StatusCode;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.RequestCallbackWrapper;

import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.auth.AuthServiceObserver;
import com.netease.nimlib.sdk.chatroom.ChatRoomServiceObserver;

import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.RecentContact;
import com.netease.nimlib.sdk.msg.attachment.FileAttachment;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.chatroom.ChatRoomService;
import com.netease.nimlib.sdk.chatroom.ChatRoomMessageBuilder;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomInfo;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMessage;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMember;
import com.netease.nimlib.sdk.chatroom.model.EnterChatRoomData;
import com.netease.nimlib.sdk.chatroom.model.EnterChatRoomResultData;
import com.netease.nimlib.sdk.chatroom.constant.MemberQueryType;


public class NIMPlugin extends CordovaPlugin {

    private static final String TAG = "NIMPlugin";

    public static String getCheckSum(String appSecret, String nonce, String curTime) {
        return encode("sha1", appSecret + nonce + curTime);
    }
    // 计算并获取md5值
    public static String getMD5(String requestBody) {
        return encode("md5", requestBody);
    }

    private static String encode(String algorithm, String value) {
        if (value == null) {
            return null;
        }
        try {
            MessageDigest messageDigest
                    = MessageDigest.getInstance(algorithm);
            messageDigest.update(value.getBytes());
            return getFormattedText(messageDigest.digest());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private static String getFormattedText(byte[] bytes) {
        int len = bytes.length;
        StringBuilder buf = new StringBuilder(len * 2);
        for (int j = 0; j < len; j++) {
            buf.append(HEX_DIGITS[(bytes[j] >> 4) & 0x0f]);
            buf.append(HEX_DIGITS[bytes[j] & 0x0f]);
        }
        return buf.toString();
    }
    private static final char[] HEX_DIGITS = { '0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

    private CallbackContext messageChannel;

    @Override
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
        if (action.equals("messageChannel")) {
            messageChannel = callbackContext;
            return true;
        }else if (action.equals("addObserver")) {
            addObserver(args.getString(0).toLowerCase());
            return true;
        }else if ("login".equals(action)) {
            login(callbackContext, args.getString(0).toLowerCase(), args.getString(1).toLowerCase());

        }else if("logout".equals(action)){
            logout();

        }else if("getStatus".equals(action)){
            getStatus(callbackContext);

        }else if("queryRecentContacts".equals(action)){
            queryRecentContacts(callbackContext);

        }else if("sendTextMsg".equals(action)){
            sendTextMsg(callbackContext,args.getString(0),args.getString(1),args.getString(2));

        }else if("sendImageMsg".equals(action)){
            sendImageMsg(callbackContext,args.getString(0),args.getString(1),args.getString(2));

        }else if("sendAudioMsg".equals(action)){
            sendAudioMsg(callbackContext,args.getString(0),args.getString(1),args.getString(2),args.getLong(3));

        }else if("sendVideoMessage".equals(action)){
            // sendVideoMessage(callbackContext,args.getString(0),args.getString(1),args.getString(2),args.getLong(3),args.getInt(4),args.getInt(5),args.getString(6));

        }else if("pullMessageHistory".equals(action)){
            pullMessageHistory(callbackContext,args.getString(0),args.getString(1),args.getInt(2),args.getBoolean(3));

        }else if("enterChatRoom".equals(action)){
            enterChatRoom(callbackContext,args.getString(0));

        }else if("exitChatRoom".equals(action)){
            exitChatRoom(args.getString(0));

        }else if("sendChatRoomTextMsg".equals(action)){
            sendChatRoomTextMsg(callbackContext,args.getString(0),args.getString(1));

        }else if("sendChatRoomImageMsg".equals(action)){
            sendChatRoomImageMsg(callbackContext, args.getString(0), args.getString(1), args.getString(2));

        }else if("sendChatRoomAudioMsg".equals(action)){
            sendChatRoomAudioMsg(callbackContext, args.getString(0), args.getString(1), args.getLong(2));

        }else if("pullChatRoomMessageHistory".equals(action)){
            pullChatRoomMessageHistory(callbackContext, args.getString(0), args.getLong(1), args.getInt(2));

        }else if("fetchRoomMembers".equals(action)){
            fetchRoomMembers(callbackContext, args.getString(0), args.getString(1), args.getLong(2), args.getInt(3));

        }

        return true;
    }

    private void addObserver(final String account) {
        // 在线状态监听
        NIMClient.getService(AuthServiceObserver.class).observeOnlineStatus(
            new Observer<StatusCode> () {
                public void onEvent(StatusCode code) {
                    Log.i(TAG, "Observer--OnlineStatus: " + code.toString());
                    JSONObject message = new JSONObject();
                    try {
                        message.put("account", account);
                        message.put("action", "OnlineStatus");
                        message.put("value", code.toString());
                    } catch (JSONException e) {
                        Log.e(TAG, "Failed to create event message", e);
                    }
                    PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, message);
                    pluginResult.setKeepCallback(true);
                    messageChannel.sendPluginResult(pluginResult);

                    if (code.wontAutoLogin()) {
                        Log.i(TAG, "User status changed-----wontAutoLogin");
                         // 被踢出、账号被禁用、密码错误等情况，自动登录失败，需要返回到登录界面进行重新登录操作
                    }
                }
        }, true);
        // 聊天室消息监听

        NIMClient.getService(ChatRoomServiceObserver.class).observeReceiveMessage(
            new Observer<List<ChatRoomMessage>>() {
                @Override
                public void onEvent(List<ChatRoomMessage> messages) {
                    // 处理新收到的消息
                    JSONArray json = new JSONArray();
                    JSONObject message = new JSONObject();
                    try {  
                        for(ChatRoomMessage m : messages){
                            JSONObject jo = new JSONObject();
                            jo.put("sessionId", m.getSessionId());
                            jo.put("fromAccount", m.getFromAccount());
                            jo.put("msgType", m.getMsgType());
                            jo.put("content", m.getContent());
                            json.put(jo);
                        }
                        message.put("account", account);
                        message.put("action", "ChatRoomMsg");
                        message.put("value", json);
                    } catch (JSONException je) {  
                        je.printStackTrace();  
                    }
                    Log.i(TAG, "Observer--ChatRoomMsg: " + json.toString());
                    PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, message);
                    pluginResult.setKeepCallback(true);
                    messageChannel.sendPluginResult(pluginResult);
                }
        }, true);
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

    private void sendImageMsg(final CallbackContext callbackContext,String sessionId,String sessionType,String filePath) {
        IMMessage message = MessageBuilder.createImageMessage(
            sessionId, 
            SessionTypeEnum.P2P, 
            new File(filePath), 
            null 
            );
        File file = new File(filePath);
        Log.i(TAG, "sendImageMsg ->"+filePath+":"+file.length());
        
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

    private void sendAudioMsg(final CallbackContext callbackContext,String sessionId,String sessionType,String filePath,Long duration) {
        IMMessage message = MessageBuilder.createAudioMessage(
            sessionId, 
            SessionTypeEnum.P2P, 
            new File(filePath), 
            duration 
            );
        Log.i(TAG, "sendAudioMsg ->"+filePath+":"+duration);
        
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
                        if(m.getMsgType()!=MsgTypeEnum.text){
                            jo.put("attachment",((FileAttachment)m.getAttachment()).getPath());
                        }
                        json.put(jo);
                    }  
                } catch (JSONException je) {  
                    je.printStackTrace();  
                }
                callbackContext.success(json);
            }

        });
    }



    private void createChatRoom(final CallbackContext callbackContext,String roomId) {
        // DefaultHttpClient httpclient = new DefaultHttpClient();
        // String url = "https://api.netease.im/nimserver/chatroom/create.action";
        // HttpPost httpPost = new HttpPost(url);

        // String appKey = "91edf0ebde8828cbfa8a6b1502adc8a1";
        // String appSecret = "ccd8eac0c58f";
        // String nonce =  "12345";
        // String curTime = String.valueOf((new Date()).getTime() / 1000L);
        // String checkSum = getCheckSum(appSecret, nonce ,curTime);//参考 计算CheckSum的java代码

        // // 设置请求的header
        // httpPost.addHeader("AppKey", appKey);
        // httpPost.addHeader("Nonce", nonce);
        // httpPost.addHeader("CurTime", curTime);
        // httpPost.addHeader("CheckSum", checkSum);
        // httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        // // 设置请求的参数
        // // creator String  是   聊天室属主的账号accid
        // // name    String  是   聊天室名称，长度限制128个字符
        // // announcement    String  否   公告，长度限制4096个字符
        // // broadcasturl    String  否   直播地址，长度限制1024个字符
        // // ext String  否   扩展字段，最长4096字符
        // List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        // nvps.add(new BasicNameValuePair("creator", "zhangxuan"));
        // nvps.add(new BasicNameValuePair("name", "聊天室名称"));
        // httpPost.setEntity(new UrlEncodedFormEntity(nvps));
        
        // // 执行请求
        // HttpResponse response = httpClient.execute(httpPost);

        // // 打印执行结果
        // System.out.println(EntityUtils.toString(response.getEntity(), "utf-8"));
        // // Log.i(TAG, "create chat room->"+EntityUtils.toString(response.getEntity());

    }

    private void enterChatRoom(final CallbackContext callbackContext,String roomId) {
        EnterChatRoomData data = new EnterChatRoomData(roomId);
        NIMClient.getService(ChatRoomService.class).enterChatRoom(data)
        .setCallback(new RequestCallback<EnterChatRoomResultData>() {

            @Override
            public void onSuccess(EnterChatRoomResultData result) {
                ChatRoomInfo roomInfo = result.getRoomInfo();
                // ChatRoomMember member = result.getMember();
                // member.setRoomId(roomInfo.getRoomId());
                // ChatRoomMemberCache.getInstance().saveMyMember(member);
                callbackContext.success("enterChatRoom success");
            }

            @Override
            public void onFailed(int code) {
                Log.i(TAG, "enter chat room failed, callback code= ->"+code);
                // if (code == ResponseCode.RES_CHATROOM_BLACKLIST) {
                //     Toast.makeText(ChatRoomActivity.this, "你已被拉入黑名单，不能再进入", Toast.LENGTH_SHORT).show();
                // } else if (code == ResponseCode.RES_ENONEXIST) {
                //     Toast.makeText(ChatRoomActivity.this, "聊天室不存在", Toast.LENGTH_SHORT).show();
                // } else {
                //     Toast.makeText(ChatRoomActivity.this, "enter chat room failed, code=" + code, Toast.LENGTH_SHORT).show();
                // }
                callbackContext.error(code);
            }

            @Override
            public void onException(Throwable exception) {
                Log.i(TAG, "enter chat room exception, e=->"+exception.getMessage());
                callbackContext.error(exception.getMessage());
            }
        });
    }

    private void exitChatRoom(String roomId) {
        Log.i(TAG, "exitChatRoom");
        NIMClient.getService(ChatRoomService.class).exitChatRoom(roomId);
    }

    private void sendChatRoomTextMsg(final CallbackContext callbackContext,String roomId,String content) {

        ChatRoomMessage message = ChatRoomMessageBuilder.createChatRoomTextMessage(
            roomId, 
            content 
        );

        NIMClient.getService(ChatRoomService.class).sendMessage(message,true)
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
    private void sendChatRoomImageMsg(final CallbackContext callbackContext,String roomId,String filePath,String displayName) {
        ChatRoomMessage message = ChatRoomMessageBuilder.createChatRoomImageMessage(
            roomId, 
            new File(filePath), 
            displayName 
            );
        File file = new File(filePath);
        Log.i(TAG, "sendChatRoomImageMsg ->"+filePath+":"+file.length());
        
        NIMClient.getService(ChatRoomService.class).sendMessage(message,true)
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

    private void sendChatRoomAudioMsg(final CallbackContext callbackContext, String roomId, String filePath, Long duration) {
        ChatRoomMessage message = ChatRoomMessageBuilder.createChatRoomAudioMessage(
            roomId , 
            new File(filePath), 
            duration 
            );
        Log.i(TAG, "sendChatRoomAudioMsg ->"+filePath+":"+duration);
        
        NIMClient.getService(ChatRoomService.class).sendMessage(message,true)
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

    private void pullChatRoomMessageHistory(final CallbackContext callbackContext,String roomId,Long startTime,Integer limit) {
       NIMClient.getService(ChatRoomService.class).pullMessageHistory(roomId, startTime, limit)
       .setCallback(new RequestCallbackWrapper<List<ChatRoomMessage>>(){
            @Override
            public void onResult(int code, List<ChatRoomMessage> messages, Throwable exception) {
                Log.i(TAG, "pullChatRoomMessageHistory, code=->"+code);
                if (messages != null) {
                    JSONArray json = new JSONArray();
                    try {  
                        for(ChatRoomMessage m : messages){
                            JSONObject jo = new JSONObject();
                            jo.put("sessionId", m.getSessionId());
                            jo.put("fromAccount", m.getFromAccount());
                            jo.put("msgType", m.getMsgType());
                            jo.put("content", m.getContent());
                            // if(m.getMsgType()!=MsgTypeEnum.text){
                            //     jo.put("attachment",((FileAttachment)m.getAttachment()).getPath());
                            // }
                            json.put(jo);
                        }
                    } catch (JSONException je) {  
                        je.printStackTrace();  
                    }  
                    callbackContext.success(json);
                }else{
                   callbackContext.success(0); 
                }

            }

        });
    }
    
    private void fetchRoomMembers(final CallbackContext callbackContext,String roomId,String memberQueryType,Long time,Integer limit) {
       Log.i(TAG, "fetchRoomMembers, roomId=->"+roomId);
       NIMClient.getService(ChatRoomService.class).fetchRoomMembers(roomId,MemberQueryType.NORMAL, 0, 10)
       .setCallback(new RequestCallbackWrapper<List<ChatRoomMember>>(){
            @Override
            public void onResult(int code, List<ChatRoomMember> members, Throwable exception) {
                Log.i(TAG, "fetchRoomMembers, code=->"+code);

                if (members != null) {
                    JSONArray json = new JSONArray();
                    try {  
                        for(ChatRoomMember m : members){
                            JSONObject jo = new JSONObject();
                            jo.put("account", m.getAccount());
                            jo.put("avatar", m.getAvatar());
                            jo.put("enterTime", m.getEnterTime());
                            jo.put("nick", m.getNick());
                            json.put(jo);
                        }  
                    } catch (JSONException je) {  
                        je.printStackTrace(); 
                        Log.i(TAG, "fetchRoomMembers, JSONException"); 
                    } 
                    callbackContext.success(json);
                }
            }

        });
    }
}
