package com.zbht.hgb.base;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.base.core.tools.ActManager;
import com.base.core.tools.ClickUtil;
import com.base.core.tools.SPUtil;
import com.blankj.utilcode.util.ToastUtils;
import com.gyf.immersionbar.ImmersionBar;
import com.zbht.hgb.R;
import com.zbht.hgb.common.Constant;
import com.zbht.hgb.ui.login.LoginActivity;
import com.zbht.hgb.util.ToastUtil;
import com.zbht.hgb.widget.dialog.LoadingDialog;

import org.greenrobot.eventbus.EventBus;

import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * 所有activity的基类
 */
public abstract class BaseActivity extends AppCompatActivity {

    public final String TAG = this.getClass().getSimpleName();

    public Context mContext;
    // 实现管理网络请求
    public CompositeDisposable mCompositeDisposable;
    private LoadingDialog mLoadDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!isStatusLayout()) {
            setContentView(initView());
            init();
        }
    }

    /**
     * 加载布局
     */
    public abstract int initView();

    /**
     * 初始化前缀操作
     */
    public abstract void initData();

    /**
     * 是否决定初始化沉浸式状态栏
     */
    public boolean isImmersionBar() {
        return true;
    }

    /**
     * 是否决定初始化EventBus
     */
    public boolean initEventBus() {
        return false;
    }

    /**
     * 是否是多状态布局页面
     */
    protected boolean isStatusLayout() {
        return false;
    }

    /**
     * 初始化方法
     */
    protected void init() {
        mContext = this;
        ActManager.getAppManager().addActivity(this); // 入栈
        ButterKnife.bind(this);
        if (isImmersionBar()) {
            initImmersionBar();
        }
        if (initEventBus()) {
            EventBus.getDefault().register(this);
        }
        initData();
    }

    /**
     * 防止多次点击点击统一处理方法
     */
    public boolean isValidClick() {
        return ClickUtil.isFastDoubleClick();
    }

    /**
     * 初始化沉浸式状态栏
     */
    private void initImmersionBar() {
        ImmersionBar.with(this).statusBarColor(R.color.white)
                .statusBarDarkFont(true).flymeOSStatusBarFontColor(R.color.color_333333).init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActManager.getAppManager().finishActivity(this); // 出栈
        unDispose(true);

        if (initEventBus()) {
            EventBus.getDefault().unregister(this);
        }

        if (null != mLoadDialog && mLoadDialog.isShowing()) {
            mLoadDialog.dismiss();
        }
    }

    /**
     * 将 {@link Disposable} 添加到 {@link CompositeDisposable} 中统一管理
     * 可在 Activity#onDestroy() 中使用 #unDispose() 停止正在执行的 RxJava 任务, 避免内存泄漏(框架已自行处理)
     *
     * @param disposable
     */
    public void addDispose(Disposable disposable) {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = new CompositeDisposable();
        }
        mCompositeDisposable.add(disposable);//将所有 Disposable 放入集中处理
    }

    /**
     * 停止集合中正在执行的 RxJava 任务
     */
    public void unDispose(boolean isDestroy) {
        if (mCompositeDisposable != null) {
            mCompositeDisposable.clear(); // 保证 Activity 结束时取消所有正在执行的订阅
        }
        if (isDestroy) {
            this.mCompositeDisposable = null;
        }
    }

    /**
     * 初始化加载框
     */
    public void showLoadingDialog() {
        if (null == mLoadDialog) {
            mLoadDialog = new LoadingDialog(this);

            /**
             * 当取消加载框时，回调事件取消请求
             */
            mLoadDialog.setOnDismissListener(new OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    Log.e(TAG, "onDismiss: ");
                    unDispose(false);
                }
            });
        }

        if (null != mLoadDialog && !mLoadDialog.isShowing()) {
            mLoadDialog.show();
        }
    }

    /**
     * 隐藏加载框
     */
    public void hideLoadingDialog() {
        if (null != mLoadDialog && mLoadDialog.isShowing()) {
            mLoadDialog.dismiss();
        }
    }
//
//    /**
//     * 完成提示框
//     * @param msg
//     */
//    public void showTipsDialog(String msg) {
//        TipsDialog tipsDialog = new TipsDialog(mContext);
//        tipsDialog.setContent(msg)
//                .goneCancel()
//                .setOnConfirmClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        tipsDialog.dismiss();
//
//                        finish();
//                    }
//                }).show();
//    }

    /**
     * 显示toast
     *
     * @param text
     */
    public void showToast(CharSequence text) {
        ToastUtil.showToast(text);
//        ToastUtils.showShort(text);
    }

    /**
     * 显示toast
     *
     * @param resId
     */
    public void showToast(int resId) {
        ToastUtil.showToast(getResources().getString(resId));
//        ToastUtils.showShort(resId);
    }

    /**
     * 显示long toast
     *
     * @param text
     */
    public void showLongToast(CharSequence text) {
        ToastUtil.showLongToast(text);
    }

    /**
     * 统一判断登录
     * @return 是否登录
     */
    public boolean isLogin() {
        boolean isLogin = (boolean) SPUtil.get(this, Constant.SPKey.IS_LOGIN, false);
        if (!isLogin) {
            startActivity(new Intent(this, LoginActivity.class));
            return false;
        }
        return true;
    }
}
