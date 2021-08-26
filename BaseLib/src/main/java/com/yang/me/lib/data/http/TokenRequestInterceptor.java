package com.yang.me.lib.data.http;

import com.yang.me.lib.data.Const;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class TokenRequestInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        try {

            Response.Builder builder = response.newBuilder();
            Response clone = builder.build();
            ResponseBody body = clone.body();
            if (body != null) {
                MediaType mediaType = body.contentType();
                if (mediaType != null) {
                    if (isText(mediaType)) {
                        String resp = body.string();
                        int code = parseJsonResultCode(resp, 0);
                        if (code == Const.LOSE_TOKEN) {
                            exit();
                        }
                        body = ResponseBody.create(mediaType, resp);
                        return response.newBuilder().body(body).build();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    private boolean isText(MediaType mediaType) {
        if (mediaType.type() != null && mediaType.type().equals("text") || mediaType.type().equals("application")) {
            return true;
        }
        if (mediaType.subtype() != null) {
            if (mediaType.subtype().equals("json") ||
                    mediaType.subtype().equals("xml") ||
                    mediaType.subtype().equals("html") ||
                    mediaType.subtype().equals("webviewhtml")
            )
                return true;
        }
        return false;
    }

    private int parseJsonResultCode(String msg, int def) {
        if (msg == null || msg.length() <= 0) {
            return def;
        }
        int start = msg.indexOf("\"code\"");
        if (start < 0) {
            return def;
        }
        StringBuilder sb = new StringBuilder();
        boolean contentStated = false;
        for (int i = start; i < msg.length(); i++) {
            char c = msg.charAt(i);
            if (contentStated) {
                if (c == '\"') {
                    continue;
                } else if (c == ',' || c == '}') {
                    break;
                }
                sb.append(c);
            } else if (c == ':') {
                contentStated = true;
            }
        }
        try {
            return Integer.valueOf(sb.toString());
        } catch (NumberFormatException e) {
            return def;
        }
    }

    private synchronized void exit() {
//        UserInfoBeanDao userInfoBeanDao = SxApp.getInstance().getDaoSession().getUserInfoBeanDao();
//        MobclickAgent.onProfileSignOff();
//        userInfoBeanDao.deleteAll();
//        SharedPreferences userSettings = SxApp.getInstance().getContext().getSharedPreferences(contacts.SP_TOKEN, Context.MODE_PRIVATE);
//        SharedPreferences.Editor edit = userSettings.edit();
//        edit.clear();
//        edit.commit();
//        JPushManager.getInstence().stopJPush();
//        if (!isForeground(getApplicationContext(), "LoginActivity")) {
//            LoginActivity.start(SxApp.getInstance());
//        }
//        new Handler(Looper.getMainLooper()).post(new Runnable() {
//            @Override
//            public void run() {
//                Toast.makeText(SxApp.getInstance(), "异地登陆，请重新登陆", Toast.LENGTH_SHORT).show();
//            }
//        });
    }
}
