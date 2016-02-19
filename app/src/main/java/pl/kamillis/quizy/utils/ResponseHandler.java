package pl.kamillis.quizy.utils;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.loopj.android.http.TextHttpResponseHandler;

import cz.msebera.android.httpclient.Header;
import pl.kamillis.quizy.R;

/**
 * Created by Kamil on 09.01.2016.
 */
public abstract class ResponseHandler extends TextHttpResponseHandler {

    protected Context context;

    public ResponseHandler(Context context) {
        super();
        this.context = context;
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
        int text;

        Log.wtf("response", responseString);

        if (statusCode > 0) {
            text = R.string.server_error;
        } else {
            text = R.string.connection_error;
        }

        new AlertDialog.Builder(context)
                .setTitle(R.string.error)
                .setMessage(text)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

}
