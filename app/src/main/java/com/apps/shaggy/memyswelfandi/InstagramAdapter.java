package com.apps.shaggy.memyswelfandi;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.view.ViewGroup.LayoutParams;
import com.apps.shaggy.memyswelfandi.model.InstagramData;
import com.apps.shaggy.memyswelfandi.model.InstagramPage;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

public class InstagramAdapter extends ArrayAdapter<InstagramData> {

    public enum PictureSize{
        LOW,
        STANDARD
    }

    public interface InstagramAdapterCallbacks{
        public void lastPictureReached();
    }

    private static final String TAG = "InstagramAdapter";

    private Context mContext;
    private LruCache<String, Bitmap> imagesCache;
    private List<InstagramData> mInstagramDataList;
    private InstagramAdapterCallbacks instagramAdapterCallbacks;
    private PictureSize mCurrentSize;

    public InstagramAdapter(Fragment fragment, int resource, List<InstagramData> instagramDataList, PictureSize currentSize){
        super(fragment.getActivity(), resource, instagramDataList);

        Log.d(TAG, "InstagramAdapter");
        if(fragment instanceof InstagramAdapterCallbacks){
            instagramAdapterCallbacks = (InstagramAdapterCallbacks)fragment;
        } else {
            throw new IllegalStateException("");
        }

        mContext = fragment.getActivity();
        mInstagramDataList = instagramDataList;
        mCurrentSize = currentSize;

        Log.d(TAG, "Constructor: " + mCurrentSize);

        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 2048;
        imagesCache = new LruCache<String, Bitmap>(cacheSize){
            @Override
            protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
                //super.entryRemoved(evicted, key, oldValue, newValue);
                Log.d(TAG, "entryRemoved");
                oldValue.recycle();
                oldValue = null;
            }
        };
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        Log.d(TAG, "getView");
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.instagram_image, parent, false);

        //Display InstagramImage in the ImageView
        InstagramData instagramData = mInstagramDataList.get(position);
        Bitmap bitmap = null;
        bitmap = getBitmap(instagramData);
        ImageView image = (ImageView) view.findViewById(R.id.instagram_low_picture);

        LayoutParams lp = image.getLayoutParams();
        lp.height = getBitmapHeight(instagramData);
        lp.width = getBitmapWidth(instagramData);
        image.setLayoutParams(lp);

        if(bitmap != null){
            Log.d(TAG, "Already here");
            image.setImageBitmap(bitmap);
        } else {
            Log.d(TAG, "Reloading...");
            InstagramDataAndView container = new InstagramDataAndView();
            container.instagramData = instagramData;
            container.view = view;

            ImageLoader loader = new ImageLoader();
            loader.execute(container);
        }

        //Log.d(TAG, "Last position: " + position + "size()" + mInstagramDataList.size());
        if(position == mInstagramDataList.size() - 5){
            Log.d(TAG, "Last position: " + position);
            instagramAdapterCallbacks.lastPictureReached();
        }
        return view;
    }

    @Override
    public void add(InstagramData object) {
        //super.add(object);
        InstagramPage.getInstance(mContext).addInstagramData(object);
    }

    public Bitmap getBitmap(InstagramData instagramData){
        Log.d(TAG, "mCurrentSize:[" + mCurrentSize + "], LOW: [" + PictureSize.LOW + "], STANDARD: [" + PictureSize.STANDARD + "]");
        if(instagramData != null) {
            if (mCurrentSize == PictureSize.LOW) {
                Log.d(TAG, "Getting Low picture");
                return imagesCache.get(instagramData.getLowResolution().getImageUrl());
            } else if (mCurrentSize == PictureSize.STANDARD) {
                Log.d(TAG, "Getting Standard picture");
                return imagesCache.get(instagramData.getStandardResolution().getImageUrl());
            }
        }
        return null;
    }

    public int getBitmapHeight(InstagramData instagramData){
        if(instagramData != null) {
            if (mCurrentSize == PictureSize.LOW) {
                Log.d(TAG, "Getting Low Height");
                return instagramData.getLowResolution().getHeight();
            } else if (mCurrentSize == PictureSize.STANDARD) {
                Log.d(TAG, "Getting Standard Height");
                return instagramData.getStandardResolution().getHeight();
            }
        }
        return -1;
    }

    public Context getContext() {
        return mContext;
    }

    public int getBitmapWidth(InstagramData instagramData){
        if(instagramData != null) {
            if (mCurrentSize == PictureSize.LOW) {
                Log.d(TAG, "Getting Low Width");
                return instagramData.getLowResolution().getWidth();
            } else if (mCurrentSize == PictureSize.STANDARD) {
                Log.d(TAG, "Getting Standard Width");
                return instagramData.getStandardResolution().getWidth();
            }
        }
        return -1;
    }

    public LruCache<String, Bitmap> getImagesCache() {
        return imagesCache;
    }

    public PictureSize getCurrentSize() {
        return mCurrentSize;
    }

    public void setCurrentSize(PictureSize currentSize) {
        this.mCurrentSize = currentSize;
    }

    class InstagramDataAndView{
        public InstagramData instagramData;
        public View view;
        public Bitmap lowBitmap;
        public Bitmap highBitmap;
    }

    private class ImageLoader extends AsyncTask<InstagramDataAndView, Void, InstagramDataAndView> {

        @Override
        protected InstagramDataAndView doInBackground(InstagramDataAndView... params) {
            InstagramDataAndView container = params[0];
            InstagramData data = container.instagramData;

            InputStream in = null;
            try {
                String lowImageUrl = data.getLowResolution().getImageUrl();
                in = (InputStream) new URL(lowImageUrl).getContent();
                container.lowBitmap = BitmapFactory.decodeStream(in);
                //data.getLowResolution().setBitmap(bitmap);

                String highImageUrl = data.getStandardResolution().getImageUrl();
                in = (InputStream) new URL(highImageUrl).getContent();
                container.highBitmap = BitmapFactory.decodeStream(in);
                //data.getLowResolution().setBitmap(bitmap);

                return container;
            } catch (Exception e) {
                container.lowBitmap = BitmapFactory.decodeResource(InstagramAdapter.this.getContext().getResources(), R.drawable.ic_launcher);
                container.highBitmap = BitmapFactory.decodeResource(InstagramAdapter.this.getContext().getResources(), R.drawable.ic_launcher);
                e.printStackTrace();
                return null;
            } finally {
                if(in != null){
                    try {
                        in.close();
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(InstagramDataAndView result) {
            //Display InstagramDataAndView image in the ImageView widget

            if(result != null) {
                imagesCache.put(result.instagramData.getLowResolution().getImageUrl(), result.lowBitmap);
                imagesCache.put(result.instagramData.getStandardResolution().getImageUrl(), result.highBitmap);

                ImageView image = (ImageView) result.view.findViewById(R.id.instagram_low_picture);
                image.setImageBitmap(getBitmap(result.instagramData));
                result.lowBitmap = null;
                result.highBitmap = null;
            } else {
                Log.d(TAG, "IMAGE DOES NOT EXIST");
            }
        }
    }
}
