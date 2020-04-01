package com.zbht.hgb.base;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
import butterknife.Unbinder;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * 所有Fragment的基类
 */
public abstract class BaseFragment extends Fragment {

    public final String TAG = this.getClass().getSimpleName();

    public Context mContext;
    // Fragment根布局
    protected View mRoot;
    // 实现管理网络请求
    private CompositeDisposable mCompositeDisposable;
    private LoadingDialog mLoadDialog;
    private Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = getActivity();
        if (mRoot == null) {
            mRoot = inflater.inflate(initView(), container, false);
        } else {
            ViewGroup parent = (ViewGroup) mRoot.getParent();
            if (parent != null) {
                parent.removeAllViews();
            }
        }
        return mRoot;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, mRoot);
        if (initEventBus()) {
            EventBus.getDefault().register(this);
        }
        initData();
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
     * 是否决定初始化EventBus
     */
    public boolean initEventBus() {
        return false;
    }

    /**
     * 防止多次点击点击统一处理方法
     */
    public boolean isValidClick() {
        return ClickUtil.isFastDoubleClick();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
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
            mLoadDialog = new LoadingDialog(mContext);

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
        boolean isLogin = (boolean) SPUtil.get(mContext, Constant.SPKey.IS_LOGIN, false);
        if (!isLogin) {
            startActivity(new Intent(mContext, LoginActivity.class));
            return false;
        }
        return true;
    }
}
