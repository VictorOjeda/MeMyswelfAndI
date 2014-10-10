package com.apps.shaggy.memyswelfandi.parsers;
import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.apps.shaggy.memyswelfandi.model.ImageData;
import com.apps.shaggy.memyswelfandi.model.InstagramData;
import com.apps.shaggy.memyswelfandi.model.InstagramPage;

public class InstagramJSONParser {

	private static final String TAG = "InstagramJSONParser";
	
	public static List<InstagramData> parseFeed(Context context, String content){
		
		try {
			JSONObject response = new JSONObject(content);
            JSONArray data = response.getJSONArray("data");
			List<InstagramData> flowerList = new ArrayList<InstagramData>();

            InstagramPage.getInstance(context).setPagination(response.getJSONObject("pagination").getString("next_url"));

            Log.d(TAG, "Next URL: " + InstagramPage.getInstance(context).getPagination());

			for (int i = 0; i < data.length(); i++) {
				
				JSONObject obj = data.getJSONObject(i);
                InstagramData instagramData = new InstagramData();

                //setting Low resolution
                ImageData lowRes = new ImageData();
                lowRes.setImageUrl(obj.getJSONObject("images").getJSONObject("low_resolution").getString("url"));
                if(!lowRes.getImageUrl().startsWith("http"))
                    lowRes.setImageUrl("http://" + lowRes.getImageUrl());
                lowRes.setHeight(obj.getJSONObject("images").getJSONObject("low_resolution").getInt("height"));
                lowRes.setWidth(obj.getJSONObject("images").getJSONObject("low_resolution").getInt("width"));

                //setting thumbnail resolution
                ImageData thumbnail = new ImageData();
                thumbnail.setImageUrl(obj.getJSONObject("images").getJSONObject("thumbnail").getString("url"));
                thumbnail.setHeight(obj.getJSONObject("images").getJSONObject("thumbnail").getInt("height"));
                thumbnail.setWidth(obj.getJSONObject("images").getJSONObject("thumbnail").getInt("width"));

                //setting standard resolution
                ImageData stanRes = new ImageData();
                stanRes.setImageUrl(obj.getJSONObject("images").getJSONObject("standard_resolution").getString("url"));
                if(!stanRes.getImageUrl().startsWith("http"))
                    stanRes.setImageUrl("http://" + stanRes.getImageUrl());
                stanRes.setHeight(obj.getJSONObject("images").getJSONObject("standard_resolution").getInt("height"));
                stanRes.setWidth(obj.getJSONObject("images").getJSONObject("standard_resolution").getInt("width"));

                //setting instagramData
                instagramData.setLowResolution(lowRes);
                instagramData.setThumbnail(thumbnail);
                instagramData.setStandardResolution(stanRes);
				flowerList.add(instagramData);
			}
			
			return flowerList;
			
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e){
            e.printStackTrace();
            return null;
        }
	}
}
