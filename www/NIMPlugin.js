var exec = require('cordova/exec');
function NIMPlugin() {}

NIMPlugin.prototype.login = function(account, token, onSuccess, onError) {
	exec(onSuccess, onError, "NIMPlugin", "login", [account, token]);
}

NIMPlugin.prototype.logout = function(onSuccess, onError) {
	onSuccess = onSuccess || function(){};
	onError = onError || function(e){ console.log(e); };
	exec(onSuccess, onError, "NIMPlugin", "logout",[]);
}

NIMPlugin.prototype.getStatus = function(onSuccess, onError) {
	exec(onSuccess, onError, "NIMPlugin", "getStatus",[]);
}

NIMPlugin.prototype.queryRecentContacts = function(onSuccess, onError) {
	exec(onSuccess, onError, "NIMPlugin", "queryRecentContacts", []);
}

NIMPlugin.prototype.sendTextMsg = function(sessionId, sessionType,content, onSuccess, onError) {
	exec(onSuccess, onError, "NIMPlugin", "sendTextMsg", [sessionId, sessionType,content]);
}

NIMPlugin.prototype.sendImageMessage = function(sessionId, sessionType,filePath, onSuccess, onError) {
	exec(onSuccess, onError, "NIMPlugin", "sendImageMessage", [sessionId, sessionType,filePath]);
}

NIMPlugin.prototype.sendAudioMessage = function(sessionId, sessionType,filePath,duration, onSuccess, onError) {
	exec(onSuccess, onError, "NIMPlugin", "sendAudioMessage", [sessionId, sessionType,filePath,duration]);
}

NIMPlugin.prototype.sendVideoMessage = function(sessionId, sessionType,filePath,duration,width,height,displayName, onSuccess, onError) {
	exec(onSuccess, onError, "NIMPlugin", "sendVideoMessage", [sessionId, sessionType,filePath,duration,width,height,displayName]);
}

NIMPlugin.prototype.pullMessageHistory = function(sessionId, sessionType,limit, persist, onSuccess, onError) {
	exec(onSuccess, onError, "NIMPlugin", "pullMessageHistory", [sessionId,sessionType, limit, persist]);
}

module.exports = new NIMPlugin();