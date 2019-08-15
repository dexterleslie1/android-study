package com.future.study.android.tablayoutviewpagerfragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 *
 */
public class FragmentPaying extends MyBaseFragment{
    @Override
    protected String getTitle() {
        return "支付中账单";
    }

    @Override
    protected int getIconInactive() {
        return R.drawable.tabbar_icon_fri_default;
    }

    @Override
    protected int getIconActive() {
        return R.drawable.tabbar_icon_fri_pressed;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_paying, null);
        return view;
    }
}
