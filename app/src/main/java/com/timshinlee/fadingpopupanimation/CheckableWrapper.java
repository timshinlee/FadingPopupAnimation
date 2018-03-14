package com.timshinlee.fadingpopupanimation;

import android.view.View;

/**
 * Created by Administrator on 2018/3/14.
 */

public abstract class CheckableWrapper {
    private View mView;

    public CheckableWrapper(View view) {
        mView = view;
    }

    public abstract boolean isCheck();

    public View getView() {
        return mView;
    }
}
