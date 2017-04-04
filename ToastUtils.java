package com.utils;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

/**
 * Created by lixing on 2016/6/23.
 * 防止重复不停显示Toast
 */
public class ToastUtils {
    private static final String TAG = "ToastUtils";
    private static final boolean SHOW = true;
    private static Toast mToast;

    public static void showLong(Context context, String message) {
        if(SHOW) {
            show(context, message, Toast.LENGTH_LONG);
        }
    }

    public static void showShort(Context context, String message) {
        if(SHOW) {
            show(context, message, Toast.LENGTH_SHORT);
        }
    }

    public static void showLong(Context context, int textId) {
        if(SHOW) {
            show(context, textId, Toast.LENGTH_LONG);
        }
    }

    public static void showShort(Context context, int textId) {
        if(SHOW) {
            show(context, textId, Toast.LENGTH_SHORT);
        }
    }

    private static void show(Context context, String text, int duration) {
        if(TextUtils.isEmpty(text)){
            LogUtils.v(TAG,"text is empty");
            return;
        }
        if(null == mToast) {
            mToast = Toast.makeText(context, text, duration);
        } else{
            mToast.setText(text);
        }
        mToast.show();
    }

    private static void show(Context context, int textId, int duration) {
        String text = context.getString(textId);
        if(TextUtils.isEmpty(text)){
            LogUtils.v(TAG,"text is empty");
            return;
        }
        if(null == mToast) {
            mToast = Toast.makeText(context, text, duration);
        } else{
            mToast.setText(text);
        }
        mToast.show();
    }

    public static void cancelToast() {
        if (mToast != null) {
            mToast.cancel();
            mToast = null;
        }
    }



}
