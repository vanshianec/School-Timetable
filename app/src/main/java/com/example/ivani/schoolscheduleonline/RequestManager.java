package com.example.ivani.schoolscheduleonline;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

public class RequestManager {

    private Context context;
    private Context applicationContext;
    private ErrorManager errorManager;
    private RequestQueue mQueue;
    private SharedPreferences sharedPreferences;

    public RequestManager(Context context, Context applicationContext, ErrorManager errorManager) {
        this.context = context;
        this.applicationContext = applicationContext;
        this.errorManager = errorManager;
        this.mQueue = Volley.newRequestQueue(context);
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void showLoadingDialogUntilResponse(RequestQueue queue) {
        final Dialog loadingDialog = createLoadingDialog();
        queue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<String>() {
            @Override
            public void onRequestFinished(Request<String> request) {
                if (loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }
            }
        });
    }

    @NonNull
    public Dialog createLoadingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(R.layout.progress_bar);
        final Dialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        return dialog;
    }

}
