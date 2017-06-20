var exec = require('cordova/exec');
function NIMPlugin() {}
// 登录
NIMPlugin.prototype.login = function(account, token, onSuccess, onError) {
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
NIMPlugin.prototype.exitChatRoom = function(roomId) {
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

module.exports = new NIMPlugin();