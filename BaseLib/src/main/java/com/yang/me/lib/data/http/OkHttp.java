package com.yang.me.lib.data.http;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.yang.me.lib.data.Const;
import com.yang.me.lib.util.SPUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by fan on 2016/11/9.
 */

public class OkHttp {

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    /**
     * 静态实例
     */
    private static OkHttp sOkHttpManager;
    private static String token;
    private Gson mGson;
    /**
     * okhttpclient实例
     */
    private OkHttpClient mClient;

    /**
     * 因为我们请求数据一般都是子线程中请求，在这里我们使用了handler
     */
    private Handler mHandler;

    /**
     * 构造方法
     */
    private OkHttp() {
        mGson = new Gson();

        mClient = new OkHttpClient.Builder()
                .addInterceptor(new TokenRequestInterceptor())
                .addInterceptor(new LoggerInterceptor())
                .connectTimeout(50, TimeUnit.SECONDS)
                .readTimeout(50, TimeUnit.SECONDS)
                .writeTimeout(50, TimeUnit.SECONDS)
                .build();
        /**
         * 如果是用的3.0之前的版本  使用以下直接设置连接超时.读取超时，写入超时
         */

        //client.setConnectTimeout(10, TimeUnit.SECONDS);
        //client.setWriteTimeout(10, TimeUnit.SECONDS);
        //client.setReadTimeout(30, TimeUnit.SECONDS);


        /**
         * 初始化handler
         */
        mHandler = new Handler(Looper.getMainLooper());
    }


    /**
     * 单例模式  获取OkHttp实例
     */
    public static OkHttp getInstance() {
        if (sOkHttpManager == null) {
            sOkHttpManager = new OkHttp();
        }
        return sOkHttpManager;
    }

    public void setApplicationContext(Context context) {
        token = SPUtil.getToken(context);
    }

    //-------------------------同步的方式请求数据--------------------------

    /**
     * 对外提供的get方法,同步的方式
     *
     * @param url 传入的地址
     * @return
     */
    public static Response getSync(String url) {

        //通过获取到的实例来调用内部方法
        return sOkHttpManager.inner_getSync(url);
    }

    /**
     * GET方式请求的内部逻辑处理方式，同步的方式
     *
     * @param url
     * @return
     */
    private Response inner_getSync(String url) {
        Request request = new Request.Builder()
                .addHeader(Const.KEY_TOKEN, token)
                .url(url).build();
        Response response = null;
        try {
            //同步请求返回的是response对象
            response = mClient.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    /**
     * 对外提供的同步获取String的方法
     *
     * @param url
     * @return
     */
    public static String getSyncString(String url) {
        return sOkHttpManager.inner_getSyncString(url);
    }


    /**
     * 同步方法
     */
    private String inner_getSyncString(String url) {
        String result = null;
        try {
            /**
             * 把取得到的结果转为字符串，这里最好用string()
             */
            result = inner_getSync(url).body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    //-------------------------异步的方式请求数据--------------------------
    public static void getAsync(String url, Map<String, String> params, DataCallBack callBack) {
        getInstance().inner_getAsync(url, params, callBack);
    }

    /**
     * 内部逻辑请求的方法
     *
     * @param url
     * @param callBack
     * @return
     */
    private void inner_getAsync(String url, Map<String, String> params, final DataCallBack callBack) {
        try {
            if (!params.isEmpty() && !TextUtils.isEmpty(url)) {
                Uri.Builder builder = Uri.parse(url).buildUpon();
                Set<Map.Entry<String, String>> entries = params.entrySet();
                for (Map.Entry<String, String> e : entries) {
                    if (e.getValue() == null) {
                        continue;
                    }
                    builder.appendQueryParameter(e.getKey(), e.getValue());
                }
                url = builder.build().toString();
            }
        } catch (Exception e) {

        }
        final Request request = new Request.Builder()
                .addHeader(Const.KEY_TOKEN, token)
                .url(url).build();
        requestCall(request, callBack);
    }

    public static void getAsync(String url, DataCallBack callBack) {
        getInstance().inner_getAsync(url, callBack);
    }

    /**
     * 内部逻辑请求的方法
     *
     * @param url
     * @param callBack
     * @return
     */
    private void inner_getAsync(String url, final DataCallBack callBack) {
        final Request request = new Request.Builder()
                .addHeader(Const.KEY_TOKEN, token)
                .url(url).build();
        requestCall(request, callBack);
    }

    public static void deleteAsync(String url, Map<String, String> map, DataCallBack callBack) {
        getInstance().inner_deleteAsync(url, map, callBack);
    }

    /**
     * 内部逻辑请求的方法
     *
     * @param url
     * @param params
     * @param callBack
     * @return
     */
    private void inner_deleteAsync(String url, Map<String, String> params, final DataCallBack callBack) {
        try {
            if (!params.isEmpty() && !TextUtils.isEmpty(url)) {
                Uri.Builder builder = Uri.parse(url).buildUpon();
                Set<Map.Entry<String, String>> entries = params.entrySet();
                for (Map.Entry<String, String> e : entries) {
                    if (e.getValue() == null) {
                        continue;
                    }
                    builder.appendQueryParameter(e.getKey(), e.getValue());
                }
                url = builder.build().toString();
            }
        } catch (Exception e) {

        }

        final Request request = new Request.Builder()
                .addHeader(Const.KEY_TOKEN, token)
                .delete()
                .url(url).build();
        requestCall(request, callBack);
    }

    public static void deleteAsync(String url, DataCallBack callBack) {
        getInstance().inner_deleteAsync(url, callBack);
    }

    /**
     * 内部逻辑请求的方法
     *
     * @param url
     * @param
     * @param callBack
     * @return
     */
    private void inner_deleteAsync(String url, final DataCallBack callBack) {
        final Request request = new Request.Builder()
                .addHeader(Const.KEY_TOKEN, token)
                .delete()
                .url(url).build();
        requestCall(request, callBack);
    }

    public static void patchAsync(String url, Map<String, String> map, DataCallBack callBack) {
        getInstance().inner_patchAsync(url, map, callBack);
    }

    /**
     * 内部逻辑请求的方法
     *
     * @param url
     * @param params
     * @param callBack
     * @return
     */
    private void inner_patchAsync(String url, Map<String, String> params, final DataCallBack callBack) {
        RequestBody requestBody = null;
        if (params == null) {
            params = new HashMap<>();
        }

        /**
         * 如果是3.0之前版本的，构建表单数据是下面的一句
         */
        //FormEncodingBuilder builder = new FormEncodingBuilder();

        /**
         * 3.0之后版本
         */
        FormBody.Builder builder = new FormBody.Builder();

        /**
         * 在这对添加的参数进行遍历，map遍历有四种方式，如果想要了解的可以网上查找
         */
        for (Map.Entry<String, String> map : params.entrySet()) {
            String key = map.getKey();
            String value = null;
            /**
             * 判断值是否是空的
             */
            if (map.getValue() == null) {
                value = "";
            } else {
                value = map.getValue();
            }
            /**
             * 把key和value添加到formbody中
             */
            builder.add(key, value);
        }
        requestBody = builder.build();

        final Request request = new Request.Builder()
                .addHeader(Const.KEY_TOKEN, token)
                .patch(requestBody)
                .url(url).build();
        requestCall(request, callBack);
    }


    /**
     * 分发失败的时候调用
     *
     * @param request
     * @param e
     * @param callBack
     */
    private void deliverDataFailure(final Request request, final IOException e, final DataCallBack callBack) {
        /**
         * 在这里使用异步处理
         */
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (callBack != null) {
                    callBack.requestFailure(request, e);
                }
            }
        });
    }

    /**
     * 分发成功的时候调用
     *
     * @param result
     * @param callBack
     */
    private void deliverDataSuccess(final String result, final DataCallBack callBack) {
        /**
         * 在这里使用异步线程处理
         */
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (callBack != null) {
                    try {
                        callBack.requestSuccess(result);
                    } catch (Exception e) {
                        callBack.requestFailure(null, new JsonPaseException());
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * 数据回调接口
     */
    public interface DataCallBack {
        void requestFailure(Request request, IOException e);

        void requestSuccess(String result) throws Exception;
    }

    //-------------------------提交表单--------------------------

    public static void postAsync(String url, Map<String, String> params, DataCallBack callBack) {
        getInstance().inner_postAsync(url, params, callBack);
    }

    private void inner_postAsync(String url, Map<String, String> params, final DataCallBack callBack) {

        RequestBody requestBody = null;
        if (params == null) {
            params = new HashMap<>();
        }

        /**
         * 如果是3.0之前版本的，构建表单数据是下面的一句
         */
        //FormEncodingBuilder builder = new FormEncodingBuilder();

        /**
         * 3.0之后版本
         */
        FormBody.Builder builder = new FormBody.Builder();

        /**
         * 在这对添加的参数进行遍历，map遍历有四种方式，如果想要了解的可以网上查找
         */
        for (Map.Entry<String, String> map : params.entrySet()) {
            String key = map.getKey();
            String value = null;
            /**
             * 判断值是否是空的
             */
            if (map.getValue() == null) {
                value = "";
            } else {
                value = map.getValue();
            }
            /**
             * 把key和value添加到formbody中
             */
            builder.add(key, value);
        }
        requestBody = builder.build();
        //结果返回
        // 请求对象
        final Request request = new Request.Builder()
                .addHeader(Const.KEY_TOKEN, token)
                .url(url).post(requestBody).build();
        requestCall(request, callBack);
    }


    /**
     * put请求
     *
     * @param url
     * @param params
     * @param callBack
     */
    public static void putAsync(String url, Map<String, String> params, DataCallBack callBack) {
        getInstance().inner_putAsync(url, params, callBack);
    }

    private void inner_putAsync(String url, Map<String, String> params, final DataCallBack callBack) {

        RequestBody requestBody = null;
        if (params == null) {
            params = new HashMap<>();
        }

        /**
         * 如果是3.0之前版本的，构建表单数据是下面的一句
         */
        //FormEncodingBuilder builder = new FormEncodingBuilder();

        /**
         * 3.0之后版本
         */
        FormBody.Builder builder = new FormBody.Builder();

        /**
         * 在这对添加的参数进行遍历，map遍历有四种方式，如果想要了解的可以网上查找
         */
        for (Map.Entry<String, String> map : params.entrySet()) {
            String key = map.getKey();
            String value = null;
            /**
             * 判断值是否是空的
             */
            if (map.getValue() == null) {
                value = "";
            } else {
                value = map.getValue();
            }
            /**
             * 把key和value添加到formbody中
             */
            builder.add(key, value);
        }
        requestBody = builder.build();
        //结果返回
        // 请求对象
        final Request request = new Request.Builder()
                .addHeader(Const.KEY_TOKEN, token)
                .url(url).put(requestBody).build();
        requestCall(request, callBack);
    }

    public static void postAsync(String url, DataCallBack callBack) {
        getInstance().inner_postAsync(url, callBack);
    }

    private void inner_postAsync(String url, final DataCallBack callBack) {

        RequestBody requestBody = null;

        /**
         * 3.0之后版本
         */
        FormBody.Builder builder = new FormBody.Builder();

        requestBody = builder.build();
        //结果返回
        // 请求对象
        final Request request = new Request.Builder()
                .addHeader(Const.KEY_TOKEN, token)
                .url(url).post(requestBody).build();
        requestCall(request, callBack);
    }

    //-------------------------Json提交--------------------------
    public static void postJson(String url, String json, DataCallBack callBack) {
        getInstance().postAllJson(url, json, callBack);
    }

    private void postAllJson(String url, String json, final DataCallBack callBack) {

        RequestBody body = RequestBody.create(JSON, json);
        final Request request = new Request.Builder()
                .url(url)
                .addHeader(Const.KEY_TOKEN, token)
                .post(body)
                .build();
        requestCall(request, callBack);
    }

    private void requestCall(final Request request, final DataCallBack callBack) {
        long beginMills = System.currentTimeMillis();
        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                deliverDataFailure(request, e, callBack);
            }

            @Override
            public void onResponse(Call call, Response response) {
                Exception exception = null;
                String result = "";
                //结果转换
                try {
                    result = response.body().string();
                } catch (IOException e) {
                    exception = e;
                    deliverDataFailure(request, e, callBack);
                }
                //结果分发
                try {
                    if (response.isSuccessful()) {
                        deliverDataSuccess(result, callBack);
                    } else {
                        IOException exp = new RequestFailedException(response.code());
                        exception = exp;
                        deliverDataFailure(request, exp, callBack);
                    }
                } catch (Exception e) {
                    exception = e;
                }
                //上送异常日志
                if (null == exception) return;
                long endMills = System.currentTimeMillis();
                int spendSec = (int) ((endMills - beginMills) / 1000);
//                LanQinEntity entity = ReportComponent.create(LanQinEntity.LEVEL_ERROR);
//                entity.setErrStack(LanQinEntity.Companion.stackTrace(exception));
//                entity.setNetSpendSec(spendSec);
//                //记录请求及响应结果
//                JsonObject jbt = new JsonObject();
//                jbt.addProperty("request", request.toString());
//                jbt.addProperty("response", result);
//                entity.setErrExtra(jbt.toString());
//                entity.setErrTag(OkHttp.class.getName());
//                ReportComponent.upload(entity);
            }
        });
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

    /**
     * 携带文件及参数上传
     *
     * @param url
     * @param file
     * @param fileName
     * @param params
     * @param callBack
     */
    public static void postFile(String url, String fileKey, File file, String fileName, Map<String, String>
            params, DataCallBack callBack) {
        getInstance().uploadFile(url, fileKey, file, fileName, params, callBack);
    }


    /**
     * post请求上传文件....包括图片....流的形式传任意文件...
     * 参数1 url
     * file表示上传的文件
     * fileName....文件的名字,,例如aaa.jpg
     * params ....传递除了file文件 其他的参数放到map集合
     */
    public void uploadFile(String url, String fileKey, File file, String fileName, Map<String, String>
            params, final DataCallBack callBack) {

        //MultipartBody多功能的请求实体对象,,,formBody只能传表单形式的数据
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);

        //参数
        if (params != null) {
            for (String key : params.keySet()) {
                builder.addFormDataPart(key, params.get(key));
            }
        }
        //文件...参数name指的是请求路径中所接受的参数...如果路径接收参数键值是fileeeee,
//此处应该改变
        builder.addFormDataPart(fileKey, fileName, RequestBody.create
                (MediaType.parse("application/octet-stream"), file));

        //构建
        MultipartBody multipartBody = builder.build();

        //创建Request
        final Request request = new Request.Builder()
                .addHeader(Const.KEY_TOKEN, token)
                .url(url).post(multipartBody).build();
        requestCall(request, callBack);
    }

    public static void postFile(String url, String fileKey, File file, String fileName, DataCallBack callBack) {
        getInstance().uploadFile(url, fileKey, file, fileName, callBack);
    }


    /**
     * post请求上传文件....包括图片....流的形式传任意文件...
     * 参数1 url
     * file表示上传的文件
     * fileName....文件的名字,,例如aaa.jpg
     * params ....传递除了file文件 其他的参数放到map集合
     */
    public void uploadFile(String url, String fileKey, File file, String fileName, final DataCallBack callBack) {

        //MultipartBody多功能的请求实体对象,,,formBody只能传表单形式的数据
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);

        //文件...参数name指的是请求路径中所接受的参数...如果路径接收参数键值是fileeeee,
//此处应该改变
        builder.addFormDataPart(fileKey, fileName, RequestBody.create
                (MediaType.parse("application/octet-stream"), file));

        //构建
        MultipartBody multipartBody = builder.build();

        //创建Request
        final Request request = new Request.Builder()
                .addHeader(Const.KEY_TOKEN, token)
                .url(url).post(multipartBody).build();
        requestCall(request, callBack);
    }

    public static void postFile(String url, List<File> files, final DataCallBack callBack) {
        getInstance().uploadFile(url, files, callBack);
    }

    public void uploadFile(String url, List<File> files, final DataCallBack callBack) {
        //MultipartBody多功能的请求实体对象,,,formBody只能传表单形式的数据
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        for (int i = 0; i < files.size(); i++) {
            builder.addFormDataPart("files", files.get(i).getName(), RequestBody.create
                    (MediaType.parse("application/octet-stream"), files.get(i)));
        }
        //构建
        MultipartBody multipartBody = builder.build();
        //创建Request
        final Request request = new Request.Builder()
                .addHeader(Const.KEY_TOKEN, token)
                .url(url).post(multipartBody).build();
        requestCall(request, callBack);
    }

    //-------------------------文件下载--------------------------
    public static void downloadAsync(String url, String desDir, DataCallBack callBack) {
        getInstance().inner_downloadAsync(url, desDir, callBack);
    }

    /**
     * 下载文件的内部逻辑处理类
     *
     * @param url      下载地址
     * @param desDir   目标地址
     * @param callBack
     */
    private void inner_downloadAsync(final String url, final String desDir, final DataCallBack callBack) {
        final Request request = new Request.Builder()
                .addHeader(Const.KEY_TOKEN, token)
                .url(url).build();
        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                deliverDataFailure(request, e, callBack);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                /**
                 * 在这里进行文件的下载处理
                 */
                if (response.isSuccessful()) {
                    InputStream inputStream = null;
                    FileOutputStream fileOutputStream = null;
                    try {
                        //文件名和目标地址
                        File file = new File(desDir, getFileName(url));
                        //把请求回来的response对象装换为字节流
                        inputStream = response.body().byteStream();
                        fileOutputStream = new FileOutputStream(file);
                        int len = 0;
                        byte[] bytes = new byte[2048];
                        //循环读取数据
                        while ((len = inputStream.read(bytes)) != -1) {
                            fileOutputStream.write(bytes, 0, len);
                        }
                        //关闭文件输出流
                        fileOutputStream.flush();
                        //调用分发数据成功的方法
                        deliverDataSuccess(file.getAbsolutePath(), callBack);
                    } catch (IOException e) {
                        //如果失败，调用此方法
                        deliverDataFailure(request, e, callBack);
                        e.printStackTrace();
                    } finally {
                        if (inputStream != null) {
                            inputStream.close();
                        }
                        if (fileOutputStream != null) {
                            fileOutputStream.close();
                        }

                    }
                } else {
                    deliverDataFailure(request, new RequestFailedException(response.code()), callBack);
                }

            }

        });
    }

    /**
     * 根据文件url获取文件的路径名字
     *
     * @param url
     * @return
     */
    private String getFileName(String url) {
        int separatorIndex = url.lastIndexOf("/");
        String path = (separatorIndex < 0) ? url : url.substring(separatorIndex + 1, url.length());
        return path;
    }

//-------------------------json上传--------------------------

    public static void postJsonAsync(String url, Map<String, String> params, DataCallBack callBack) {
        getInstance().performPost(url, params, callBack);
    }

    /* 执行普通的post请求，参数集合全部转为json
     *
     * @param map  传入的参数集合
     * @param netCallBack  回调接口
     */
    public void performPost(String url, Map<String, String> map, final DataCallBack callBack) {
        String params = mGson.toJson(map);
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, params);

        final Request request = new Request.Builder()
                .url(url)
                .addHeader(Const.KEY_TOKEN, token)
                .post(body)
                .build();
        requestCall(request, callBack);
    }


    /**
     * 新版本同步请求方法
     * 2019-05-16
     *
     * @param url    接口地址
     * @param params 请求参数
     * @return 响应结果
     * @throws IOException IO异常
     */
    public static Response post(String url, Map<String, String> params) throws IOException {
        params = null != params ? params : new HashMap<>(0);
        FormBody.Builder builder = new FormBody.Builder();
        for (Map.Entry<String, String> map : params.entrySet()) {
            String key = map.getKey();
            String value = null == map.getValue() ? "" : map.getValue();
            builder.add(key, value);
        }
        RequestBody requestBody = builder.build();
        Request request = new Request.Builder()
                .addHeader(Const.KEY_TOKEN, token)
                .url(url).post(requestBody).build();
        return getInstance().mClient.newCall(request).execute();
    }

    /**
     * 响应结果装换为字符串
     *
     * @param response 响应结果
     * @return 结果字符串
     * @throws IOException IO异常
     */
    public static String results(Response response) throws IOException {
        if (null == response || !response.isSuccessful()) return "";
        return response.body().string();
    }
}
