package pl.kamillis.quizy.utils;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Created by Kamil on 17.12.2015.
 */
public class RestClient {

    private static final String BASE_URL = "http://quizy.kamillis.pl/api/";
    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, ResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), responseHandler);
    }

    public static void get(String url, RequestParams params, ResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(Context context, String url, Object data, ResponseHandler responseHandler) {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        StringEntity entity = new StringEntity(gson.toJson(data), Charset.forName("UTF-8"));
        client.post(context, getAbsoluteUrl(url), entity, "application/json", responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

}
