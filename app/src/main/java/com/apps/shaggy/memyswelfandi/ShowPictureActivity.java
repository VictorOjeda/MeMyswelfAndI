package com.apps.shaggy.memyswelfandi;

import android.support.v4.app.Fragment;

public class ShowPictureActivity extends MainActivity{
    @Override
    protected Fragment createFragment() {
        return new ShowPictureFragment();
    }
}
