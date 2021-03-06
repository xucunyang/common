package com.yang.me.lib.data.http;

import android.text.TextUtils;
import android.util.Log;

import com.yang.me.lib.BuildConfig;

import java.io.IOException;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

public class LoggerInterceptor implements Interceptor {
    private static final String TAG = "LoggerInterceptor";

    private boolean showLog = BuildConfig.DEBUG;

    @Override
    public Response intercept(Chain chain) throws IOException {
        if (showLog) {
            Request request = chain.request();
            logForRequest(request);
            Response response = chain.proceed(request);
            return logForResponse(response);
        } else {
            return chain.proceed(chain.request());
        }
    }

    private Response logForResponse(Response response) {
        try {
            Log.e(TAG, "========response'log=======");
            Response.Builder builder = response.newBuilder();
            Response clone = builder.build();
            Log.e(TAG, "url : " + clone.request().url());
            Log.e(TAG, "code : " + clone.code());
            Log.e(TAG, "protocol : " + clone.protocol());
            if (!TextUtils.isEmpty(clone.message()))
                Log.e(TAG, "message : " + clone.message());

            ResponseBody body = clone.body();
            if (body != null) {
                MediaType mediaType = body.contentType();
                if (mediaType != null) {
                    Log.e(TAG, "responseBody's contentType : " + mediaType.toString());
                    if (isText(mediaType)) {
                        String resp = body.string();
                        Log.e(TAG, "responseBody's content : " + resp);

                        body = ResponseBody.create(mediaType, resp);
                        return response.newBuilder().body(body).build();
                    } else {
                        Log.e(TAG, "responseBody's content : " + " maybe [file part] , too large too print , ignored!");
                    }
                }
            }
            Log.e(TAG, "========response'log=======end");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    private void logForRequest(Request request) {
        try {
            String url = request.url().toString();
            Headers headers = request.headers();

            Log.e(TAG, "========request'log=======");

            Log.e(TAG, "method : " + request.method());
            Log.e(TAG, "url : " + url);
            if (headers != null && headers.size() > 0) {
                Log.e(TAG, "headers : " + headers.toString());
            }
            RequestBody requestBody = request.body();
            if (requestBody != null) {
                MediaType mediaType = requestBody.contentType();
                if (mediaType != null) {
                    Log.e(TAG, "requestBody's contentType : " + mediaType.toString());
                    if (isText(mediaType)) {
                        Log.e(TAG, "requestBody's content : " + bodyToString(request));
                    } else {
                        Log.e(TAG, "requestBody's content : " + " maybe [file part] , too large too print , ignored!");
                    }
                }
            }
            Log.e(TAG, "========request'log=======end");
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    private String bodyToString(final Request request) {
        try {
            final Request copy = request.newBuilder().build();
            final Buffer buffer = new Buffer();
            copy.body().writeTo(buffer);
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "something error when show requestBody.";
        }
    }
}

