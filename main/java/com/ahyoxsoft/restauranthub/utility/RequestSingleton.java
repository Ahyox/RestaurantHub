package com.ahyoxsoft.restauranthub.utility;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ayodeji on 06/05/2015.
 */
public class RequestSingleton {
    private RequestQueue mRequestQueue;
    private static Context mContext;
    private static RequestSingleton mInstance;
    private ImageLoader mImageLoader;

    private RequestSingleton(Context context) {
        mContext = context;
        mRequestQueue = getRequestQueue();
        //mImageLoader = new ImageLoader(mRequestQueue, new LruBitmapCache(LruBitmapCache.getCacheSize(context)));
    }

    /**
     * This instantiate the RequestQueue
     * @return an object of the RequestQueue
     */
    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            //getApplicationContext() doesn't make the application leak
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }
        return mRequestQueue;
    }

    /**
     * Gets an instance of the class
     * @param context
     * @return the instance of this class
     */
    public static synchronized RequestSingleton getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new RequestSingleton(context);
        }
        return mInstance;
    }

    /**
     * This adds the Request to the RequestQueue
     * @param request is the type of request sent to the Server
     * @param <T> Generic value for Request
     */
    public <T> void addToRequestQueue(Request<T> request) {
        getRequestQueue().add(request);
    }

    public ImageLoader getmImageLoader() {
        return mImageLoader;
    }

    public static String getEncodeURL(String url, HashMap<String, String> params) {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (first)
                    first = false;
                else
                    result.append("&");

                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            }
        }catch (UnsupportedEncodingException e) {

        }

        return url+"?"+result.toString();
    }

    public static String getEncodeURL(HashMap<String, String> params) {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (first)
                    first = false;
                else
                    result.append("&");

                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            }
        }catch (UnsupportedEncodingException e) {

        }

        return result.toString();
    }


}
