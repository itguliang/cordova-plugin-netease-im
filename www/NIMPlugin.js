var exec = require('cordova/exec');

var NIMPluginObjects = {};

var NIMPlugin = function(account, observeOnlineStatus, observeChatRoomMsg) {
    NIMPluginObjects[account] = this;
    this.observeOnlineStatus = observeOnlineStatus;
    this.observeChatRoomMsg = observeChatRoomMsg;
    exec(null, null, "NIMPlugin", "addObserver", [account]);
};
// 登录
NIMPlugin.prototype.login = function(account, token, onSuccess, onError,onStatus) {
	exec(onSuccess, onError, "NIMPlugin", "login", [account, token]);
}
// 登出
NIMPlugin.prototype.logout = function(onSuccess, onError) {
	onSuccess = onSuccess || function(){};
	onError = onError || function(e){ console.log(e); };
	exec(onSuccess, onError, "NIMPlugin", "logout",[]);
}
// 获取登录状态
NIMPlugin.prototype.getStatus = function(onSuccess, onError) {
	exec(onSuccess, onError, "NIMPlugin", "getStatus",[]);
}
// 最近会话
NIMPlugin.prototype.queryRecentContacts = function(onSuccess, onError) {
	exec(onSuccess, onError, "NIMPlugin", "queryRecentContacts", []);
}
// 文本消息
NIMPlugin.prototype.sendTextMsg = function(sessionId, sessionType,content, onSuccess, onError) {
	exec(onSuccess, onError, "NIMPlugin", "sendTextMsg", [sessionId, sessionType,content]);
}
// 图片消息
NIMPlugin.prototype.sendImageMsg = function(sessionId, sessionType,filePath, onSuccess, onError) {
	exec(onSuccess, onError, "NIMPlugin", "sendImageMsg", [sessionId, sessionType,filePath]);
}
// 语音消息
NIMPlugin.prototype.sendAudioMsg = function(sessionId, sessionType,filePath,duration, onSuccess, onError) {
	exec(onSuccess, onError, "NIMPlugin", "sendAudioMsg", [sessionId, sessionType,filePath,duration]);
}
// 视频消息
NIMPlugin.prototype.sendVideoMessage = function(sessionId, sessionType,filePath,duration,width,height,displayName, onSuccess, onError) {
	exec(onSuccess, onError, "NIMPlugin", "sendVideoMessage", [sessionId, sessionType,filePath,duration,width,height,displayName]);
}
// 获取历史消息
NIMPlugin.prototype.pullMessageHistory = function(sessionId, sessionType, limit, persist, onSuccess, onError) {
	exec(onSuccess, onError, "NIMPlugin", "pullMessageHistory", [sessionId,sessionType, limit, persist]);
}

// NIMPlugin.prototype.createChatRoom = function(roomId,onSuccess, onError) {
// 	exec(onSuccess, onError, "NIMPlugin", "createChatRoom", [roomId]);
// }

// 进入聊天室
NIMPlugin.prototype.enterChatRoom = function(roomId, onSuccess, onError) {
	exec(onSuccess, onError, "NIMPlugin", "enterChatRoom", [roomId]);
}
// 离开聊天室
NIMPlugin.prototype.exitChatRoom = function(roomId, onSuccess, onError) {
	onSuccess = onSuccess || function(){};
	onError = onError || function(e){ console.log(e); };
	exec(onSuccess, onError, "NIMPlugin", "exitChatRoom", [roomId]);
}
// 聊天室文本消息
NIMPlugin.prototype.sendChatRoomTextMsg = function(roomId, content, onSuccess, onError) {
	exec(onSuccess, onError, "NIMPlugin", "sendChatRoomTextMsg", [roomId, content]);
}
// 聊天室图片消息
NIMPlugin.prototype.sendChatRoomImageMsg = function(roomId, filePath, displayName, onSuccess, onError) {
	exec(onSuccess, onError, "NIMPlugin", "sendChatRoomImageMsg", [roomId, filePath, displayName]);
}
// 聊天室语音消息
NIMPlugin.prototype.sendChatRoomAudioMsg = function(roomId, filePath, duration, onSuccess, onError) {
	exec(onSuccess, onError, "NIMPlugin", "sendChatRoomAudioMsg", [roomId, filePath, duration]);
}
// 聊天室接收消息

// 聊天获取历史消息
NIMPlugin.prototype.pullChatRoomMessageHistory = function(roomId,startTime,limit,onSuccess, onError) {
	exec(onSuccess, onError, "NIMPlugin", "pullChatRoomMessageHistory", [roomId,startTime,limit]);
}

// 获取聊天室成员
NIMPlugin.prototype.fetchRoomMembers = function(roomId,memberQueryType,time,limit,onSuccess, onError) {
	exec(onSuccess, onError, "NIMPlugin", "fetchRoomMembers", [roomId,memberQueryType,time,limit]);
}

NIMPlugin.onMessageReceived = function(account, action, value){
	var Nim=NIMPluginObjects[account];
	switch(action) {
        case "OnlineStatus":
          Nim.observeOnlineStatus(value);
          break;
        case "ChatRoomMsg":
          Nim.observeChatRoomMsg(value);
          break;
        default :
          break;
      }
};

module.exports = NIMPlugin;

function messageObserver(msg) {
    NIMPlugin.onMessageReceived(msg.account,msg.action,msg.value)
}

var channel = require('cordova/channel');

channel.createSticky('onMediaPluginReady');
channel.waitForInitialization('onNIMPluginReady');

channel.onCordovaReady.subscribe(function() {
    exec(messageObserver, undefined, 'NIMPlugin', 'messageChannel', []);
    channel.initializationComplete('onNIMPluginReady');
});
