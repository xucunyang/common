package com.yang.me.lib.util;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class RxUtil {
    private static final String TAG = "RxUtil";


    public static <T, R> void rx(@NonNull Function<ObservableEmitter<T>, R> ioFun,
                                 @Nullable Function<T, Void> onNextFun) {
        rx(ioFun, null, null, onNextFun, new Function<Throwable, Void>() {
            @Override
            public Void apply(@NonNull Throwable throwable) throws Exception {
                Log.e(TAG, "on error: " + throwable.getMessage());
                return null;
            }
        }, null);
    }

    public static <T, R> void rxNormal(@NonNull Function<ObservableEmitter<T>, R> ioFun,
                                 @Nullable Function<T, Void> uiFun) {
        rx(ioFun, uiFun, null, null, new Function<Throwable, Void>() {
            @Override
            public Void apply(@NonNull Throwable throwable) throws Exception {
                Log.e(TAG, "on error: " + throwable.getMessage());
                return null;
            }
        }, null);
    }

    public static <T, R> void rx(@NonNull Function<ObservableEmitter<T>, R> ioFun,
                                 @Nullable Function<T, Void> uiFun,
                                 @Nullable Function<T, Void> onNextFun,
                                 @Nullable Function<R, Void> onCompleteFun) {
        rx(ioFun, uiFun, null, onNextFun, new Function<Throwable, Void>() {
            @Override
            public Void apply(@NonNull Throwable throwable) throws Exception {
                Log.e(TAG, "on error: " + throwable.getMessage());
                return null;
            }
        }, onCompleteFun);
    }

    public static  <T, R> void rx(
            @NonNull Function<ObservableEmitter<T>, R> ioFun,
            @Nullable Function<T, Void> uiFun,
            @Nullable Function<Disposable, Void> onSubscribeFun,
            @Nullable Function<T, Void> onNextFun,
            @Nullable Function<Throwable, Void> onErrorFun,
            @Nullable Function<R, Void> onCompleteFun) {
        try {
            Observable.create(
                    new ObservableOnSubscribe<T>() {
                        @Override
                        public void subscribe(@NonNull ObservableEmitter<T> emitter) throws Exception {
                            Log.w(TAG, "subscribe: " + Thread.currentThread().getName());
                            ioFun.apply(emitter);
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<T>() {
                        T tempT;
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {
                            doFun(onSubscribeFun, d);
                        }

                        @Override
                        public void onNext(@NonNull T t) {
                            doFun(onNextFun, t);
                            tempT = t;
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            doFun(onErrorFun, e);
                        }

                        @Override
                        public void onComplete() {
                            doFun(onCompleteFun, null);
                            doFun(uiFun, tempT);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "rx: " + e.getMessage());
        }
    }

    private static <T, R> R doFun(Function<T, R> fun, T t) {
        if (fun == null) {
            return null;
        }

        R r = null;
        try {
            r = fun.apply(t);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "doFun: " + e.getMessage());
        }
        return r;
    }

}
