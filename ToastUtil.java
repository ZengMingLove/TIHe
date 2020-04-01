package com.zbht.hgb.util;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zbht.hgb.R;
import com.zbht.hgb.base.BaseApplication;

/**
 * Author: ming.zeng
 * Date: 21/12/2019 下午 3:30
 * Desc: 自定义toast
 */
public class ToastUtil {

    private static Toast toast;

    public static void showToast(CharSequence msg) {
        showToast(msg, 0, Toast.LENGTH_SHORT);
    }

    public static void showPatchToast(CharSequence msg) {
        showPathToast(msg, 0, Toast.LENGTH_SHORT);
    }

    public static void showToast(CharSequence msg, int res) {
        showToast(msg, res, Toast.LENGTH_SHORT);
    }

    public static void showLongToast(CharSequence msg) {
        showToast(msg, 0, Toast.LENGTH_LONG);
    }

    public static void showLongToast(CharSequence msg, int res) {
        showToast(msg, res, Toast.LENGTH_LONG);
    }

    /**
     * 自定义toast
     */
    private static void showToast(CharSequence tvStr, int res, int duration) {
        if (toast == null) {
            toast = new Toast(BaseApplication.getAppContenxt());
        }
        View view = LayoutInflater.from(BaseApplication.getAppContenxt()).inflate(R.layout.layout_toast, null);
        TextView tv_toast_msg = view.findViewById(R.id.tv_toast_msg);
        tv_toast_msg.setText(tvStr);
        ImageView iv_toast = view.findViewById(R.id.iv_toast);
        if (res > 0) {
            iv_toast.setImageResource(res);
        }
        toast.setView(view);
        toast.setDuration(duration);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    private static void showPathToast(CharSequence tvStr, int res, int duration) {
        if (toast == null) {
            toast = new Toast(BaseApplication.getAppContenxt());
        }
        View view = LayoutInflater.from(BaseApplication.getAppContenxt()).inflate(R.layout.item_patch_toast, null);
        TextView tv_toast_msg = view.findViewById(R.id.tv_toast_msg);
        tv_toast_msg.setText(tvStr);
        ImageView iv_toast = view.findViewById(R.id.iv_toast);
        if (res > 0) {
            iv_toast.setImageResource(res);
        }
        toast.setView(view);
        toast.setDuration(duration);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }


    /**
     * 取消toast显示
     */
    public static void cancel() {
        if (null != toast) {
            toast.cancel();
        }
    }
}
