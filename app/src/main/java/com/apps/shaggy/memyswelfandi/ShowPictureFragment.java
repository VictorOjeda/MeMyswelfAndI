package com.apps.shaggy.memyswelfandi;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ShowPictureFragment extends Fragment {
    public static final String TAG = "ShowPictureFragment";
    public static final String EXTRA_BITMAP_ID = "MyBitmap";

    private Bitmap mBitmap;
    private String id;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        id = getActivity().getIntent().getStringExtra(EXTRA_BITMAP_ID);
        //mBitmap =  (Bitmap) getActivity().getIntent().getParcelableExtra(EXTRA_BITMAP);
        //mBitmap = BitmapFactory.decodeByteArray( getActivity().getIntent().getByteArrayExtra(EXTRA_BITMAP), 0, getActivity().getIntent().getByteArrayExtra(EXTRA_BITMAP).length);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.show_picture_fragment, container, false);

        //Getting the imageView
        ImageView image = (ImageView) view.findViewById(R.id.standard_picture);
        image.setImageBitmap(PictureListFragment.instagramAdapter.getImagesCache().get(id));
        return view;
    }


    @Override
    public void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
        //TODO Fix rotation crash, create a LRUCacheManager
        //mBitmap.recycle();
        //mBitmap = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach");
    }
}

