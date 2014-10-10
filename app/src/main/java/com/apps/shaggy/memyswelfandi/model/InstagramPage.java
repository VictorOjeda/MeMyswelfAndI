package com.apps.shaggy.memyswelfandi.model;

import android.content.Context;

import android.util.Log;
import java.util.ArrayList;

public class InstagramPage
{
    private final static String TAG = "InstagramPage";
    private Context mContext;
    private static InstagramPage sInstagramPage;
	private String pagination;
    ArrayList<InstagramData> mInstagramData;

    private InstagramPage(Context appContext){
        mInstagramData = new ArrayList<InstagramData>();
        mContext = appContext;
    }

    public static InstagramPage getInstance(Context context){
        if(sInstagramPage == null){
            sInstagramPage = new InstagramPage(context.getApplicationContext());
        }
        return sInstagramPage;
    }

    public String getPagination() {
        return pagination;
    }

    public void setPagination(String pagination) {
        this.pagination = pagination;
    }

    public ArrayList<InstagramData> getInstagramData() {
        return mInstagramData;
    }

    public void addInstagramData(InstagramData instagramData){
        Log.d(TAG, "addInstagramData" + mInstagramData.size());
        mInstagramData.add(instagramData);
    }
}
