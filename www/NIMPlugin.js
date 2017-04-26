var exec = require('cordova/exec');
function NIMPlugin() {}
// exec 通过传入配置文件中的 js-module/clobbers/target 的属性值 、传给 java 类的 action 参数来调用 java 方法
// arg1：成功回调
// arg2：失败回调
// arg3：将要调用类配置的标识
// arg4：调用的原生方法名
// arg5：参数
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

NIMPlugin.prototype.sendImageMessage = function(sessionId, sessionType,file, onSuccess, onError) {
	exec(onSuccess, onError, "NIMPlugin", "sendImageMessage", [sessionId, sessionType,file]);
}

NIMPlugin.prototype.pullMessageHistory = function(sessionId, sessionType,limit, persist, onSuccess, onError) {
	exec(onSuccess, onError, "NIMPlugin", "pullMessageHistory", [sessionId,sessionType, limit, persist]);
}

module.exports = new NIMPlugin();