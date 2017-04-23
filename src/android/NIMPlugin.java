package com.netease.nim;

import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.LoginInfo;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

public class NIMPlugin extends CordovaPlugin {

    @Override
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
        if ("login".equals(action)) {
            String account = args.getString(0).toLowerCase();
            String token = args.getString(1).toLowerCase();

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

        return true;
    }
}
