package com.future.study.android.tablayoutviewpagerfragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 *
 */
public class FragmentUnpay extends MyBaseFragment {
    @Override
    protected String getTitle() {
        return "未支付账单";
    }

    @Override
    protected int getIconInactive() {
        return R.drawable.tabbar_icon_msg_default;
    }

    @Override
    protected int getIconActive() {
        return R.drawable.tabbar_icon_msg_pressed;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_unpay, null);
        return view;
    }
}
