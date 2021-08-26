package com.yang.me.lib.ui;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.yang.me.lib.ui.fragment.BaseBindFragment;

import java.lang.ref.WeakReference;

public class GlobalHandler extends Handler {
    public static final int CODE_TOAST_MSG = 100;
    private WeakReference<BaseBindFragment<?>> mReference;

    private final String Tag = GlobalHandler.class.getSimpleName();

    private GlobalHandler() {
        Log.d(Tag, "GlobalHandler创建");
    }

    private static class Holder {
        private static final GlobalHandler HANDLER = new GlobalHandler();
    }

    public static GlobalHandler get() {
        return Holder.HANDLER;
    }

    @Override
    public void handleMessage(Message msg) {
        Log.e(Tag, " handleMessage " + (mReference != null ? mReference.get() : " null"));
        super.handleMessage(msg);
        if (mReference != null && mReference.get() != null) {
            mReference.get().handleMsg(msg);
        } else {
            Log.e(Tag, "请传入HandleMsgListener对象");
        }
    }

    public interface IHandleMsg {

        void handleMsg(Message msg);

    }

    public void addFragment(BaseBindFragment<?> fragment) {
        //getInstance();
        this.mReference = new WeakReference<>(fragment);
    }

    /**
     * 将消息发送给消息队列
     *
     * @param what     what
     * @param object   object
     * @param mHandler mHandler
     */
    public void sendMsg(int what, Object object) {
        Message message = Message.obtain();
        message.what = what;
        message.obj = object;
        GlobalHandler.get().sendMessage(message);
    }

    /**
     * 将消息发送给消息队列
     *
     * @param what     what
     * @param object   object
     * @param fragment fragment
     */
    public void sendMsg(int what, Object object, BaseBindFragment<?> fragment) {
        GlobalHandler handler = GlobalHandler.get();
        handler.addFragment(fragment);
        sendMsg(what, object);
        handler.removeCallbacks(null);
    }

    public void sendToast(String msg) {
        sendMsg(CODE_TOAST_MSG, msg);
    }

}