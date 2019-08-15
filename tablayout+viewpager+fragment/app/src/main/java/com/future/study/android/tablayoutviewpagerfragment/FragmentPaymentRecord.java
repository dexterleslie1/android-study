package com.future.study.android.tablayoutviewpagerfragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 *
 */
public class FragmentPaymentRecord extends MyBaseFragment {
    @Override
    protected String getTitle() {
        return "历史账单";
    }

    @Override
    protected int getIconInactive() {
        return R.drawable.tabbar_icon_my_default;
    }

    @Override
    protected int getIconActive() {
        return R.drawable.tabbar_icon_my_pressed;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_payment_record, null);
        return view;
    }
}
