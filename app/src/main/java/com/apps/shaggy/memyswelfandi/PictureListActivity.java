package com.apps.shaggy.memyswelfandi;

import android.support.v4.app.Fragment;

public class PictureListActivity extends MainActivity{
    protected Fragment createFragment(){
        return new PictureListFragment();
    }
}
