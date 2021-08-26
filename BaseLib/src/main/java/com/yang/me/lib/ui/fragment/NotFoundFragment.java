package com.yang.me.lib.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout.LayoutParams;

import com.yang.me.lib.R;
import com.yang.me.lib.databinding.FragmentNotFoundBinding;
import com.yang.me.lib.ui.FragmentDisplayActivity;

/**
 * <pre>
 *
 * description: 配合{@link FragmentDisplayActivity#start(Context, String, Bundle, boolean)}
 *              传的名字找不到或者异常情况展示找不到页面
 * author:      xucunyang@outlook.com
 * time:        2021-07-14 22:41:40:542
 *
 * </pre>
 */
public class NotFoundFragment extends BaseBindFragment<FragmentNotFoundBinding> {
    private final boolean flag;

    public NotFoundFragment(boolean flag) {
        this.flag = flag;
    }

    @Override
    protected int getFragmentLayoutId() {
        return R.layout.fragment_not_found;
    }

    @Override
    protected void init() {
        initView();
    }

    private void initView() {
        Bundle arguments = this.getArguments();

        if (flag) {
            mViewBinding.tvContent.setText("暂无内容");
        }

        if (null != arguments) {
            String title = arguments.getString("title");
            boolean isFromActivity = arguments.getBoolean("isFromActivity");

            if (TextUtils.isEmpty(title)) {
                mViewBinding.tvTitle.setText(title);
            }
            mViewBinding.top.setVisibility(isFromActivity ? View.VISIBLE : View.GONE);
        }

        mViewBinding.ivBack.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                checkPageCanFinish();
            }
        });

        if (null != arguments) {
            boolean isCancelFitsSystemWindows = arguments.getBoolean("isCancelFitsSystemWindows");
            if (isCancelFitsSystemWindows) {
                LayoutParams lp = (LayoutParams) mViewBinding.top.getLayoutParams();
                lp.setMargins(0, 0, 0, 0);
            }
        }

    }

    public void checkPageCanFinish() {
        if (null == getParentFragment() && getActivity() != null) {
            getActivity().finish();
        }
    }
}
