package com.vritti.vrittiaccounting;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.vritti.vrittiaccounting.data.AdatSoftConstants;
import com.vritti.vrittiaccounting.data.AdatSoftData;
import com.vritti.vrittiaccounting.data.NetworkUtils;
import com.vritti.vrittiaccounting.data.StartSession;
import com.vritti.vrittiaccounting.data.utility;
import com.vritti.vrittiaccounting.database.DatabaseHelper;
import com.vritti.vrittiaccounting.interfaces.CallbackInterface;

import org.json.JSONArray;
import org.json.JSONException;


public class ConfirmOTPActivity extends AppCompatActivity {
    private Context parent;

    private Button buttonVerify;
    public static EditText editTextOTP;
    private TextView txtResendotp;
    String responsemsg = "";
    String paramToken= "";
    String urlStringToken="";
    utility ut;
    String json;
    SharedPreferences sharedpreferences,pref;
    String usertype, userLocation, userid, username, otp,otpfrommsg;
    public static String MSG_OTP_ERROR = "Please check the OTP you provided and try again...";
    public static String TITLE_OTP_ERROR = "OTP error!!!";
    ProgressDialog progressDialog;
    DatabaseHelper db1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_otp);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //getSupportActionBar().setLogo(R.drawable.logo_gray_small);
        getSupportActionBar().setTitle(AdatSoftData.MODULE_TITLE);
        initViews();
        setListeners();


        if (otp != null) {
            if(otpfrommsg != null) {
                AdatSoftData.OTP_INPUT = otp;
                editTextOTP.setText(otpfrommsg + "");
            }
        } else {

        }

    }

    private void initViews() {
        parent = ConfirmOTPActivity.this;
        sharedpreferences = getSharedPreferences(RegistrationActivity.MyPREFERENCES,
                Context.MODE_PRIVATE);
        otp = sharedpreferences.getString("otp", null);
        pref = getSharedPreferences(IncomingSmsReceiver.MyPREFERENCES,Context.MODE_PRIVATE);
        otpfrommsg = pref.getString("otpfrommsg",null);
        buttonVerify = (Button) findViewById(R.id.button_confirm_otp_verify);
        editTextOTP = (EditText) findViewById(R.id.edittext_otp);
        db1 = new DatabaseHelper(ConfirmOTPActivity.this);
    }

    private void setListeners() {
        buttonVerify.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (editTextOTP.getText().toString().trim()
                        .equals(AdatSoftData.OTP_INPUT)) {

                    Log.d("test", "Mobile no : " + AdatSoftData.MOBILE);

                    SharedPreferences.Editor editor = sharedpreferences.edit();

                    editor.putString("registration", "done");
                    editor.commit();

                    if (NetworkUtils.isNetworkAvailable(ConfirmOTPActivity.this)) {
                        if ((AdatSoftData.SESSION_ID != null)
                                && (AdatSoftData.HANDLE != null)) {
                            new FirmMasterAPI().execute();
                        } else {
                            new StartSession(ConfirmOTPActivity.this, new CallbackInterface() {

                                @Override
                                public void callMethod() {
                                    new FirmMasterAPI().execute();
                                }
                            });
                        }
                    } else {
                        Toast.makeText(ConfirmOTPActivity.this, "No internet..", Toast.LENGTH_LONG).show();
                        // callSnackbar();
                    }

                   /* Intent intent = new Intent(ConfirmOTPActivity.this, WelcomeActivity.class);
                    startActivity(intent);
                    ConfirmOTPActivity.this.finish();*/
                } else {
                    SharedPreferences.Editor editor = sharedpreferences.edit();

                    editor.putString("registration", "notdone");
                    editor.commit();

                    utility.showMessageDialog(parent,
                            TITLE_OTP_ERROR,
                            MSG_OTP_ERROR);
                    editTextOTP.setText("");
                }
            }
        });


    }


    public class FirmMasterAPI extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(ConfirmOTPActivity.this);
            //progressDialog.setMessage("Sync Setup Master");
            progressDialog.setMessage("Sync Firm Master");
            progressDialog.show();
        }


        @Override
        protected String doInBackground(String... params) {

            String urlString = AdatSoftData.URL + AdatSoftData.METHOD_GET_DATA_COUNT + "?sessionId=" + AdatSoftData.SESSION_ID
                    + "&handler=" + AdatSoftData.HANDLE + "&data=firmlist&cust_no=" + AdatSoftData.Lno;
            try {
                urlString = urlString.replaceAll(" ", "%20");
                responsemsg = SplashActivity.OpenConnection(urlString);
                Log.d("resp", "resp" + responsemsg);
                responsemsg = responsemsg.substring(1, responsemsg.length() - 1);
                responsemsg = responsemsg.replaceAll("[^0-9]", "");
            } catch (NullPointerException e) {
                responsemsg = "error";
                e.printStackTrace();
            } catch (Exception e) {
                responsemsg = "error";
                e.printStackTrace();
            }


            return responsemsg;
        }


        @Override
        protected void onPostExecute(String result) {
            progressDialog.dismiss();
            if (responsemsg.contains("error")) {
                Toast.makeText(getBaseContext(), "error in firmlist", Toast.LENGTH_LONG).show();
            } else if (responsemsg.contains("SessionExpired")) {
                new StartSession(ConfirmOTPActivity.this, new CallbackInterface() {

                    @Override
                    public void callMethod() {
                        new FirmMasterAPI().execute();
                    }
                });
            } else {
                if (Integer.parseInt(responsemsg) > 0) {

                    SQLiteDatabase db = db1.getWritableDatabase();
                    db.execSQL("DROP TABLE IF EXISTS "
                            + AdatSoftConstants.TABLE_FIRM);
                    db.execSQL(AdatSoftConstants.CREATE_TABLE_FIRM);
                }

                if (NetworkUtils.isNetworkAvailable(ConfirmOTPActivity.this)) {
                    if ((AdatSoftData.SESSION_ID != null)
                            && (AdatSoftData.HANDLE != null)) {
                        new FirmMasterAPI_data().execute(responsemsg);
                    } else {
                        new StartSession(ConfirmOTPActivity.this, new CallbackInterface() {

                            @Override
                            public void callMethod() {
                                new FirmMasterAPI_data().execute(responsemsg);
                            }
                        });
                    }
                } else {
                    Toast.makeText(ConfirmOTPActivity.this, "No internet..", Toast.LENGTH_LONG).show();
                    // callSnackbar();
                }

            }
        }
    }

    public class FirmMasterAPI_data extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(ConfirmOTPActivity.this);
            progressDialog.setMessage("Sync Setup Master");
            progressDialog.show();
        }


        @Override
        protected String doInBackground(String... params) {
            int resp_count = Integer.parseInt(params[0]);
        /*    if (params[0].length() <= 2) {

            } else if (params[0].length() > 2) {
*/
            int j = 0;
            int k = 0;
              /*  for (int i = 0; i < resp_count; i++) {
                    j = resp_count / 10;
                    resp_count = resp_count - j;
                    if (resp_count > 0) {*/

            String urlString1 = AdatSoftData.URL + AdatSoftData.METHOD_GET_DATA
                    + "?from=" + k + "&to=" + resp_count;
            try {
                urlString1 = urlString1.replaceAll(" ", "%20");
                responsemsg = SplashActivity.OpenConnection(urlString1);
                Log.d("resp", "resp" + responsemsg);
                try {
                    JSONArray jsonArray = new JSONArray(responsemsg);
                    if (jsonArray.length() > 0) {


                        for (int i1 = 0; i1 < jsonArray.length(); i1++) {


                            db1.add_Firm(jsonArray.getJSONObject(i1).getString(
                                    "cust_no"), jsonArray.getJSONObject(i1).getString(
                                    "shop_no"), jsonArray.getJSONObject(i1).getString(
                                    "name_mar"), jsonArray.getJSONObject(i1).getString(
                                    "address"), jsonArray.getJSONObject(i1).getString(
                                    "mobile"), jsonArray.getJSONObject(i1).getString(
                                    "AMCExpireDt"),jsonArray.getJSONObject(i1).getString(
                                    "Module") );

                        }


                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } catch (NullPointerException e) {
                responsemsg = "error";
                e.printStackTrace();
            } catch (Exception e) {
                responsemsg = "error";
                e.printStackTrace();
            }

                  /*  }
k=j+i;

                }


            }
*/

            return responsemsg;
        }


        @Override
        protected void onPostExecute(String result) {
            progressDialog.dismiss();
            if (responsemsg.contains("error")) {
                Toast.makeText(getBaseContext(), "error!", Toast.LENGTH_LONG).show();
            } else if (responsemsg.contains("SessionExpired")) {
                new StartSession(ConfirmOTPActivity.this, new CallbackInterface() {

                    @Override
                    public void callMethod() {
                        new FirmMasterAPI().execute();
                    }
                });
            } else {
           /*     json = responsemsg;
                parseJson(json);*/


               /* try {
                    AdatSoftData.TOKEN = SplashActivity.registerGCM();
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("Pkg_name", "adatsoft.vritti.com.adatsoft");
                    jsonObject.put("Mobile", AdatSoftData.MOBILE);
                    jsonObject.put("UserName", AdatSoftData.Lno + "_" + AdatSoftData.Sno);
                    jsonObject.put("Device_Id", AdatSoftData.TOKEN);
                    paramToken=jsonObject.toString();
                    urlStringToken = AdatSoftData.URL + AdatSoftData.METHOD_SAVE_DATA +
                            "SessionId=" + AdatSoftData.SESSION_ID
                            + "&Handler=" + AdatSoftData.HANDLE + "&Table=ALERT_DEVICEMASTER";

                    if (NetworkUtils.isNetworkAvailable(ConfirmOTPActivity.this)) {
                        if ((AdatSoftData.SESSION_ID != null)
                                && (AdatSoftData.HANDLE != null)) {
                            new TokenRegIdAPI().execute(urlStringToken, paramToken, null, null);
                        } else {
                            new StartSession(ConfirmOTPActivity.this, new CallbackInterface() {

                                @Override
                                public void callMethod() {
                                    new TokenRegIdAPI().execute(urlStringToken, paramToken, null, null);
                                }
                            });
                        }
                    } else {
                        Toast.makeText(ConfirmOTPActivity.this, "No internet..", Toast.LENGTH_LONG).show();
                        // callSnackbar();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }*/
            }
            Intent intent = new Intent(ConfirmOTPActivity.this, MainActivity.class);
            //Intent intent = new Intent(ConfirmOTPActivity.this, Main2Activity.class);
            startActivity(intent);
            ConfirmOTPActivity.this.finish();
        }

    }

    class TokenRegIdAPI extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(ConfirmOTPActivity.this);
            progressDialog.setMessage("Processing...");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            Object res;
           /* responsemsg = "";
            inwid = "";
            inwtab = "";*/
            try {
                res = SplashActivity.OpenPostConnection(params[0], params[1]);
                responsemsg = res.toString();
            } catch (NullPointerException e) {
                responsemsg = "error";
                e.printStackTrace();
            } catch (Exception e) {
                responsemsg = "error";
                e.printStackTrace();
            }
            return responsemsg;
        }

        @Override
        protected void onPostExecute(String result) {
            String table = "";
            if (responsemsg.contains("error")) {
                Toast.makeText(getBaseContext(), "error!", Toast.LENGTH_LONG).show();
            } else if (responsemsg.contains("Success")) {
                Toast.makeText(getBaseContext(), "Token Added Successfully..", Toast.LENGTH_LONG).show();
            }
        }
    }
}
