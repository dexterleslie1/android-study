package com.future.study.android.tablayoutviewpagerfragment;

import android.support.v4.app.Fragment;

/**
 *
 */
public abstract class MyBaseFragment extends Fragment {
    /**
     *
     * @return
     */
    protected abstract String getTitle();

    /**
     *
     * @return
     */
    protected abstract int getIconInactive();

    /**
     *
     * @return
     */
    protected abstract int getIconActive();
}
