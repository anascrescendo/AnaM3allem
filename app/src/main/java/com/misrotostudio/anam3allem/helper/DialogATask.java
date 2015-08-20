package com.misrotostudio.anam3allem.helper;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.misrotostudio.anam3allem.MainActivity;

/**
 * Created by othmaneelmassari on 20/08/15.
 */
public class DialogATask  extends AsyncTask<String, String, String> {
    private Context context;
    private ProgressDialog progressDialog;

    public DialogATask(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(context);
        progressDialog.show();
    }

    @Override
    protected String doInBackground(String... params) {

        return "finish";
    }


    @Override
    protected void onPostExecute(String result) {
        progressDialog.dismiss();

    }
}