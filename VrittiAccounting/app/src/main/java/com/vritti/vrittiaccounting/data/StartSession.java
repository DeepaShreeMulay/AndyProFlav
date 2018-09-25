package com.vritti.vrittiaccounting.data;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.vritti.vrittiaccounting.SplashActivity;
import com.vritti.vrittiaccounting.interfaces.CallbackInterface;

public class StartSession {
    private Context parent;
    private CallbackInterface callback;
    String responsemsg;
    utility ut;
    public ProgressDialog progressDialog, progressDialog1, progressDialog2;

    //   ProgressHUD progress;
    public StartSession(Context context, CallbackInterface callback) {
        parent = context;
        this.callback = callback;
        ut = new utility();
        new GetSessionId().execute();

    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(parent);
            progressDialog.setMessage("Loading...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
        }
        progressDialog.show();
/*
         if (progress == null) {
             progress = ProgressHUD.show(parent,
                     "Loading ...", false, true, null);
        }*/
        // progressDialog.show();

    }

    private void dismissProgressDialog() {
       /* if ((progress != null) && progress.isShowing()) {
            progress.dismiss();
        }*/

        if ((progressDialog != null) && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    class GetSessionId extends AsyncTask<Void, Void, Void> {
        String responsemsg = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();

        }

        @Override
        protected Void doInBackground(Void... params) {


                String url_web = AdatSoftData.URL+AdatSoftData.METHOD_SESSION_ACTIVATE_1 +
                        "?Mobileno=" + AdatSoftData.MOBILE + "&Version=1";

                url_web = url_web.replaceAll(" ", "%20");
                try {
                    responsemsg = SplashActivity.OpenConnection(url_web);
                    Log.d("resp", "resp" + responsemsg);
                } catch (NullPointerException e) {
                    responsemsg = "error";
                    e.printStackTrace();
                } catch (Exception e) {
                    responsemsg = "error";
                    e.printStackTrace();
                }
                Log.d("resp", "resp" + responsemsg);


                return null;

        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            dismissProgressDialog();
            if ((responsemsg==("")) || (responsemsg == null)) {
                Toast.makeText(parent, "The server is not responding OR check your internet connection.",
                        Toast.LENGTH_SHORT).show();
            }else if (responsemsg.equalsIgnoreCase("error")){
                Toast.makeText(parent, "Error..",Toast.LENGTH_SHORT).show();
            }else  if (responsemsg.contains("Mobileno Invalid")) {
                Toast.makeText(parent, "Invalid Mobile number",Toast.LENGTH_SHORT).show();
            }else {
             //   dismissProgressDialog();
                //progressDialog.dismiss();
                responsemsg = responsemsg.substring(1, responsemsg.length() - 1);
                AdatSoftData.SESSION_ID = responsemsg;
                new GetHandle().execute();
            }
        }

        class GetHandle extends AsyncTask<Void, Void, Void> {
            String responsemsg = null;

            @Override
            protected void onPreExecute() {

                super.onPreExecute();
                // showProgressDialog();
            }

            @Override
            protected Void doInBackground(Void... params) {

                    String url_web = AdatSoftData.URL+AdatSoftData.METHOD_SESSION_ACTIVATE_2 +
                            "?Mobileno=" + AdatSoftData.MOBILE + "&SessionId="
                            + AdatSoftData.SESSION_ID + "";

                    url_web = url_web.replaceAll(" ", "%20");
                    try {
                        responsemsg = SplashActivity.OpenConnection(url_web);
                        Log.d("resp", "resp" + responsemsg);
                    } catch (NullPointerException e) {
                        responsemsg = "error";
                        e.printStackTrace();
                    } catch (Exception e) {
                        responsemsg = "error";
                        e.printStackTrace();
                    }
                    Log.d("resp", "resp" + responsemsg);


                    return null;

            }

            @Override
            protected void onPostExecute(Void result) {

                super.onPostExecute(result);
                dismissProgressDialog();
                if (!responsemsg.equalsIgnoreCase("error")) {
                    responsemsg = responsemsg.substring(1, responsemsg.length() - 1);
                    AdatSoftData.HANDLE = responsemsg.replaceAll(
                            "[^0-9]", "");
                    new GetSessionFinalService().execute();
                } else {
                    //  dismissProgressDialog();
                    //progressDialog.dismiss();
                    Toast.makeText(parent, "The server is not responding OR check your iternet connection.",
                            Toast.LENGTH_SHORT).show();

                }
            }
        }

        class GetSessionFinalService extends AsyncTask<Void, Void, Void> {
            String responsemsg = null;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //   showProgressDialog();
            }

            @Override
            protected Void doInBackground(Void... params) {

                    String url_web = AdatSoftData.URL+AdatSoftData.METHOD_SESSION_ACTIVATE_3 +
                            "?SessionId="
                            + AdatSoftData.SESSION_ID + "&Handle="
                            + AdatSoftData.HANDLE + "&strSessionTime=30&Instance=0";

                    url_web = url_web.replaceAll(" ", "%20");
                    try {
                        responsemsg = SplashActivity.OpenConnection(url_web);
                        Log.d("resp", "resp" + responsemsg);
                    } catch (NullPointerException e) {
                        responsemsg = "error";
                        e.printStackTrace();
                    } catch (Exception e) {
                        responsemsg = "error";
                        e.printStackTrace();
                    }
                    Log.d("resp", "resp" + responsemsg);


                    return null;

            }

            @Override
            protected void onPostExecute(Void result) {

                super.onPostExecute(result);
                dismissProgressDialog();

                if (!responsemsg.equalsIgnoreCase("error")) {
                    callback.callMethod();
                } else {
                    // dismissProgressDialog();
                    Toast.makeText(parent, "The server is not responding OR check your iternet connection.",
                            Toast.LENGTH_SHORT).show();

                }
            }
        }
    }


}
