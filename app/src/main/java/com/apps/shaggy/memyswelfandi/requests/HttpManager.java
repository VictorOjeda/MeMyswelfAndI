package com.apps.shaggy.memyswelfandi.requests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.squareup.okhttp.OkHttpClient;

import android.net.http.AndroidHttpClient;
import android.util.Base64;
import android.util.Log;

public class HttpManager {
	
	public static Libraries useLib = Libraries.NONE;
	
	public static Libraries getUseLib() {
		return useLib;
	}

	public static void setUseLib(Libraries useLib) {
		HttpManager.useLib = useLib;
	}

	public enum Libraries{
		NONE,
		OKHTTP,
		VOLLEY,
		RETROFIT
	}
	
	public static String getData(String uri){
		AndroidHttpClient client = AndroidHttpClient.newInstance("AndroidAgent");
		HttpGet request = new HttpGet(uri);
		HttpResponse response;
		
		try {
			response = client.execute(request);
			return EntityUtils.toString(response.getEntity());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			client.close();
		}
		
	}
	
	public static String getDataHttpURL(String uri)
	{
		BufferedReader reader = null;
		
		try {
			URL url = new URL(uri);
			HttpURLConnection con = null; 
			
			switch(useLib) {
			case NONE:
				con = (HttpURLConnection) url.openConnection();
				break;
			case OKHTTP:
				OkHttpClient client = new OkHttpClient();
				con = client.open(url);
			case VOLLEY:
				//Not a case
			case RETROFIT:
			default:
				con = (HttpURLConnection) url.openConnection();
				break;
			}
			
			
			StringBuilder sb = new StringBuilder();
			reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
			
			String line;
			
			while((line = reader.readLine()) != null){
				sb.append(line + "\n");
			}
			
			return sb.toString();
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if(reader != null){
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
					return null;
				}
			}
		}
	}
	
	public static String getDataHttpURL(String uri, String username, String password)
	{
		BufferedReader reader = null;
		
		byte[] loginBytes = (username + ":" + password).getBytes();
		StringBuilder loginBuilder = new StringBuilder()
			.append("Basic ")
			.append(Base64.encodeToString(loginBytes, Base64.DEFAULT));
		
		try {
			URL url = new URL(uri);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			
			con.addRequestProperty("Authorization", loginBuilder.toString());
			
			StringBuilder sb = new StringBuilder();
			reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
			
			String line;
			
			while((line = reader.readLine()) != null){
				sb.append(line + "\n");
			}
            Log.d("HTTPManager", "response: " + sb.toString() );
            return sb.toString();
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if(reader != null){
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
					return null;
				}
			}
		}
	}
	
	public static String getDataHttpURL(RequestPackage p)
	{
		BufferedReader reader = null;
		String uri = p.getUri();
		
		/*byte[] loginBytes = (username + ":" + password).getBytes();
		StringBuilder loginBuilder = new StringBuilder()
			.append("Basic ")
			.append(Base64.encodeToString(loginBytes, Base64.DEFAULT));*/
		
		if ("GET".equals(p.getMethod())) {
			uri += "?" + p.getEncodedParams();
		}
		
		
		try {
			URL url = new URL(uri);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod(p.getMethod());
			
			JSONObject json = new JSONObject(p.getParams());
			String params = "params=" + json.toString();
			
			if ("POST".equals(p.getMethod())) {
				con.setDoOutput(true);
				OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
				writer.write(params); //p.getEncodedParams()
				writer.flush();
			}
			
			StringBuilder sb = new StringBuilder();
			reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
			
			String line;
			
			while((line = reader.readLine()) != null){
				sb.append(line + "\n");
			}
			
			return sb.toString();
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if(reader != null){
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
					return null;
				}
			}
		}
	}
}
