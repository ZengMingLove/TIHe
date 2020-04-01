package com.zbht.hgb.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.base.core.widget.StateLayout;
import com.zbht.hgb.R;

/**
 * 带数据状态显示view的Fragment的基类
 */
public abstract class BaseStatusFragment extends BaseFragment {

    public StateLayout mStateLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = getActivity();
        if (mRoot == null) {
            mRoot = inflater.inflate(R.layout.layout_base_fragment_status, container, false);
            mStateLayout = mRoot.findViewById(R.id.sl_base_status);
            mStateLayout.addView(inflater.inflate(initView(), null, false));
        } else {
            ViewGroup parent = (ViewGroup) mRoot.getParent();
            if (parent != null) {
                parent.removeAllViews();
            }
        }
        return mRoot;
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
}
