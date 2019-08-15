package com.future.study.android.tablayoutviewpagerfragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 *
 */
public class MyFragmentPagerAdapter extends FragmentPagerAdapter {
    private List<MyBaseFragment> fragments = null;

    /**
     *
     * @param fragmentManager
     */
    public MyFragmentPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return fragments.get(position).getTitle();
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    /**
     *
     * @param fragments
     */
    public void setFragments(List<MyBaseFragment> fragments) {
        this.fragments = fragments;
    }
}
