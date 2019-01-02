package com.project.niko.fortajava.Helper;

import android.content.Context;
import android.util.Base64;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.client.params.ClientPNames;
import org.apache.http.entity.StringEntity;

public class HelperAPI {
    private final static String BASE_URL = "https://developers.zomato.com/api/v2.1/";

    //private final static String USER_KEY = "7f9b7427028eeef040d8a466f7f10417";

    private final static String USER_KEY = "86f40ebf4aadc1172d88dca9d32a9600";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.setEnableRedirects(true, true, true);
        client.getHttpClient().getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);
        client.addHeader("user-key", USER_KEY);
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(Context context, String url, StringEntity entity, AsyncHttpResponseHandler responseHandler){
        client.setTimeout(100000);
        client.post(context, getAbsoluteUrl(url), entity, "application/json", responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}