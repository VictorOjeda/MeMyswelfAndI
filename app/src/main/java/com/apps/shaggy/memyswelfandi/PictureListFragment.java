package com.apps.shaggy.memyswelfandi;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.apps.shaggy.memyswelfandi.model.InstagramData;
import com.apps.shaggy.memyswelfandi.model.InstagramPage;
import com.apps.shaggy.memyswelfandi.parsers.InstagramJSONParser;
import com.apps.shaggy.memyswelfandi.requests.HttpManager;

import java.util.ArrayList;
import java.util.List;

public class PictureListFragment extends ListFragment implements InstagramAdapter.InstagramAdapterCallbacks{

    private final static String TAG = "PictureListFragment";
    private ArrayList<InstagramData> mInstagramDataList;
    static InstagramAdapter instagramAdapter;
    InstagramAdapter.PictureSize pictureSize = InstagramAdapter.PictureSize.LOW;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        if(savedInstanceState != null) {
            Log.d(TAG, "We have data from last instance");
            if(savedInstanceState.getString("SIZE").equals(InstagramAdapter.PictureSize.LOW.toString()))
                pictureSize = InstagramAdapter.PictureSize.LOW;
            else
                pictureSize = InstagramAdapter.PictureSize.STANDARD;
        }
        //Setting ActionBar title
        mInstagramDataList = InstagramPage.getInstance(getActivity()).getInstagramData();
        getActivity().setTitle(R.string.app_name);

        if(InstagramPage.getInstance(this.getActivity()).getInstagramData().size() == 0) {
            if (isOnline()) {
                requestData("https://api.instagram.com/v1/tags/selfie/media/recent?access_token=1471534881.1fb234f.6d2af9acee074787b2ac3717583aeaa0");//&max_tag_id=1410026806471923");
            } else {
                Toast.makeText(this.getActivity(), "Network is not available", Toast.LENGTH_LONG).show();
            }
        } else {
            Log.d(TAG, "We already have data, do not start to download");
            update();
        }

        instagramAdapter = new InstagramAdapter(this, R.layout.instagram_image, mInstagramDataList, pictureSize);
        setListAdapter(instagramAdapter);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("SIZE", pictureSize.toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        InstagramData instagramData = (InstagramData) getListAdapter().getItem(position);//InstagramPage.getInstance(getActivity()).getInstagramData().get(position).getStandardResolution().getImageUrl();
        String key = instagramData.getStandardResolution().getImageUrl();

        if(instagramAdapter.getImagesCache().get(key) != null) {
            Intent intent = new Intent(getActivity(), ShowPictureActivity.class);
            intent.putExtra(ShowPictureFragment.EXTRA_BITMAP_ID, key);
            startActivity(intent);
        }
        //ByteArrayOutputStream bs = new ByteArrayOutputStream();

        /*Intent intent = new Intent(getActivity(), ShowPictureActivity.class);
        String key = InstagramPage.getInstance(getActivity()).getInstagramData().get(position).getStandardResolution().getImageUrl();
        instagramAdapter.getImagesCache().get(key).compress(Bitmap.CompressFormat.PNG, 50, bs);
        intent.putExtra(ShowPictureFragment.EXTRA_BITMAP, bs.toByteArray());*/
    }

    @Override
    public void lastPictureReached() {
        Log.d(TAG, "lastPictureReached");
        downloadNextData();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch(id) {
            case R.id.low_to_standard:
                Log.d(TAG, "Low to standard selected");
                if(instagramAdapter.getCurrentSize() != InstagramAdapter.PictureSize.STANDARD) {
                    instagramAdapter.getImagesCache().evictAll();
                    pictureSize = InstagramAdapter.PictureSize.STANDARD;
                    instagramAdapter = new InstagramAdapter(this, R.layout.instagram_image, mInstagramDataList, InstagramAdapter.PictureSize.STANDARD);
                    this.setListAdapter(instagramAdapter);
                }
            break;

            case R.id.standard_to_low:
                Log.d(TAG, "Standard to low selected");
                if(instagramAdapter.getCurrentSize() != InstagramAdapter.PictureSize.LOW) {
                    pictureSize = InstagramAdapter.PictureSize.LOW;
                    instagramAdapter.getImagesCache().evictAll();
                    instagramAdapter = new InstagramAdapter(this, R.layout.instagram_image, mInstagramDataList, InstagramAdapter.PictureSize.LOW);
                    this.setListAdapter(instagramAdapter);
                }
            break;
        }

        return super.onOptionsItemSelected(item);
    }

    protected boolean isOnline(){
        ConnectivityManager cm = (ConnectivityManager) this.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            if(netInfo.getType() != ConnectivityManager.TYPE_WIFI){
                Toast.makeText(this.getActivity(), "This app does not work without WIFI, do not use Carrier (3G, 4G) internet access", Toast.LENGTH_SHORT).show();
                return false;
            }
            return true;
        } else {
            return false;
        }
    }

    private void requestData(String... uri) {
        //Creating new Task
        BackgroundDownloader task = new BackgroundDownloader();

        //Running serialized or parallel depending on api version
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB){
            task.execute(uri);
        }else{
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, uri);
        }
    }

    private void downloadNextData(){
        Log.d(TAG, "downloadNextData");
        if (isOnline()) {
            requestData(InstagramPage.getInstance(getActivity()).getPagination());
        } else {
            Toast.makeText(this.getActivity(), "Network is not available", Toast.LENGTH_LONG).show();
        }
    }

    public void update(){

    }

    private class BackgroundDownloader extends AsyncTask<String, String, List<InstagramData>> {


        static final String TAG = "BackgroundDownloader";

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected List<InstagramData> doInBackground(String... params) {

            String content = HttpManager.getDataHttpURL(params[0], "feeduser", "feedpassword");
            List<InstagramData> instagramDataList = InstagramJSONParser.parseFeed(PictureListFragment.this.getActivity(), content);

            return instagramDataList;
        }

        @Override
        protected void onPostExecute(List<InstagramData> result) {

            if(result == null || "".equals(result)){
                Toast.makeText(PictureListFragment.this.getActivity(), "Can not connect to web service", Toast.LENGTH_LONG).show();
                return;
            }

            for(InstagramData data : result){
                instagramAdapter.add(data);
                instagramAdapter.notifyDataSetChanged();
                //InstagramPage.getInstance(PictureListFragment.this.getActivity()).addInstagramData(data);
            }
        }

        @Override
        protected void onProgressUpdate(String... values) {
        }

    }
}
