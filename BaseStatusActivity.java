package com.zbht.hgb.base;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.base.core.widget.StateLayout;
import com.blankj.utilcode.util.NetworkUtils;
import com.zbht.hgb.R;
import com.zbht.hgb.view.TitleView;

/**
 * 带数据状态显示view的activity的基类
 */
public abstract class BaseStatusActivity extends BaseActivity implements NetworkUtils.OnNetworkStatusChangedListener {

    public TitleView title_view_status;
    public StateLayout mStateLayout;

    /**
     * 页面中当网络连接上时，会回调该方法
     */
    public abstract void inNetWork();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_base_activity_status);

        title_view_status = findViewById(R.id.title_view_status);
        title_view_status.setFinishClickListener(this);
        mStateLayout = findViewById(R.id.sl_base_status);
        mStateLayout.addView(getLayoutInflater().inflate(initView(), null, false));

        init();

        if (!NetworkUtils.isConnected()) {
            Log.d(TAG, "onCreate: 暂无网络连接");
            showError();
        }

        NetworkUtils.registerNetworkStatusChangedListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NetworkUtils.unregisterNetworkStatusChangedListener(this);
    }

    @Override
    protected boolean isStatusLayout() {
        return true;
    }

    /**
     * 设置标题
     */
    public void setTitleView(String title) {
        title_view_status.setTitleText(title);
    }

    /**
     * 状态设置
     */
    public void showLoading() {
        mStateLayout.showLoading();
    }

    public void showError() {
        mStateLayout.showError();
    }

    public void showEmpty() {
        mStateLayout.showEmpty();
    }

    public void showEmptyButton(View.OnClickListener emptyButtonClickListener) {
        mStateLayout.setEmptyButtonClickListener(emptyButtonClickListener);
    }

    public void showErrorButton(View.OnClickListener errorButtonClickListener) {
        mStateLayout.setErrorButtonClickListener(errorButtonClickListener);
    }

    public void hideStateLayout() {
        mStateLayout.hide();
    }

    /**
     * 监听网络状态
     */
    @Override
    public void onDisconnected() {
        Log.d(TAG, "onCreate: onDisconnected");
        showError();
    }

    @Override
    public void onConnected(NetworkUtils.NetworkType networkType) {
        Log.d(TAG, "onCreate: onConnected = " + networkType);
        mStateLayout.hide();
        inNetWork();
    }
}
