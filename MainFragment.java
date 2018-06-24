package com.caihang.ylyim.fragment;

import android.os.Bundle;
import android.view.View;

import com.caihang.core.base.BaseFragment;
import com.caihang.ylyim.R;

public class MainFragment extends BaseFragment {

    public static MainFragment newInstance(String title) {
        Bundle args = new Bundle();
        MainFragment fragment = new MainFragment();
        args.putString("title", title);
        fragment.setArguments(args);
        return fragment;

    }
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {

    }

    @Override
    protected void initData(View view, Bundle savedInstanceState) {
        setTitle(getArguments().getString("title"));
    }
}
