package com.vritti.vrittiaccounting;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.vritti.vrittiaccounting.data.AdatSoftData;
import com.vritti.vrittiaccounting.data.NetworkUtils;
import com.vritti.vrittiaccounting.data.StartSession;
import com.vritti.vrittiaccounting.data.utility;
import com.vritti.vrittiaccounting.interfaces.CallbackInterface;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class RegistrationActivity extends AppCompatActivity {
    EditText licenseno, shopnumber, mobno, url;
    Button btnVerify;
    String valid_shopnumber = null, valid_Contact_no = null, valid_licenseno = null;
    StringBuilder sb;
    utility ut;
    String json = "", url_web = "", responsemsg;
    ProgressDialog progressDialog;
    String lno, sno;
    String Key, urlchk;
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "AdatSoft";
    String restoredMobile, otp, registration = "notdone";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //getSupportActionBar().setLogo(R.drawable.logo_gray_small);
        getSupportActionBar().setTitle(AdatSoftData.MODULE_TITLE);
        sharedpreferences = getSharedPreferences(MyPREFERENCES,
                Context.MODE_PRIVATE);

        restoredMobile = sharedpreferences.getString("Mobileno", null);
        otp = sharedpreferences.getString("otp", null);
        urlchk = sharedpreferences.getString("URL",null);
        registration = sharedpreferences.getString("registration", null);



        if (restoredMobile != null && otp != null) {
            if (registration == null || registration.equalsIgnoreCase("notdone")) {
                AdatSoftData.URL = urlchk;
                Intent intent = new Intent(RegistrationActivity.this,
                        ConfirmOTPActivity.class);

                startActivity(intent);
                finish();
            } else {

                AdatSoftData.MOBILE = restoredMobile;
                AdatSoftData.URL = urlchk;
                Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                //Intent intent = new Intent(RegistrationActivity.this,Main2Activity.class);
                startActivity(intent);
                finish();
                //  UserWithPref();
            }


        } else {
            if (otp == null) {
                initView();
                setListner();
            }

        }

    }

    private void initView() {
        licenseno = (EditText) findViewById(R.id.licenseno);
        shopnumber = (EditText) findViewById(R.id.shopnumber);
        mobno = (EditText) findViewById(R.id.mobno);
        btnVerify = (Button) findViewById(R.id.btnVerify);
        //url = (EditText) findViewById(R.id.url);
        sb = new StringBuilder();
        ut = new utility();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.url_menu,menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.live:
                Key = "WEBURL";
                if (NetworkUtils.isNetworkAvailable(RegistrationActivity.this)) {
                    new LiveURL_Task().execute(Key);
                } else {
                    Toast.makeText(RegistrationActivity.this, "No internet..", Toast.LENGTH_LONG).show();
                    // callSnackbar();
                }
                return true;
            case R.id.test:
                Key = "TESTURL";
                if (NetworkUtils.isNetworkAvailable(RegistrationActivity.this)) {
                    new LiveURL_Task().execute(Key);
                } else {
                    Toast.makeText(RegistrationActivity.this, "No internet..", Toast.LENGTH_LONG).show();
                    // callSnackbar();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void setListner() {
        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              /*  if (valid_shopnumber == null && valid_licenseno == null && valid_Contact_no == null) {
                    Toast.makeText(getApplicationContext(), "Please fill data correctly.", Toast.LENGTH_LONG).show();
                } else if (valid_shopnumber == null) {
                    shopnumber.setError("Please enter license no");
                } else if (valid_licenseno == null) {
                    licenseno.setError("Please enter license no");
                } else if (valid_Contact_no == null) {
                    mobno.setError("Please enter license no");
                } else if (url.getText().toString().equalsIgnoreCase("") ||
                        url.getText().toString().equalsIgnoreCase(" ") ||
                        url.getText().toString().equalsIgnoreCase(null)) {
                    url.setError("Please enter url");
                } else {*/

                if(AdatSoftData.URL==null || AdatSoftData.URL == ""){
                    Toast.makeText(RegistrationActivity.this, "Please Select URL", Toast.LENGTH_LONG).show();


                }else {

                    AdatSoftData.MOBILE = mobno.getText().toString().trim();
                    lno = licenseno.getText().toString().trim();
                    AdatSoftData.Lno = lno;
                    sno = shopnumber.getText().toString().trim();
                    AdatSoftData.Sno = sno;

                    if (NetworkUtils.isNetworkAvailable(RegistrationActivity.this)) {
                        if ((AdatSoftData.SESSION_ID != null)
                                && (AdatSoftData.HANDLE != null)) {
                            new RegristrTask().execute();
                        } else {
                            new StartSession(RegistrationActivity.this, new CallbackInterface() {

                                @Override
                                public void callMethod() {
                                    new RegristrTask().execute();
                                }
                            });
                        }
                    } else {
                        Toast.makeText(RegistrationActivity.this, "No internet..", Toast.LENGTH_LONG).show();
                        // callSnackbar();
                    }
                }


                // }
            }
        });


        mobno.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Is_Valid_Contact_no(mobno);
            }
        });

        shopnumber.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                Is_Valid_shopNumber(shopnumber);
            }
        });

        licenseno.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                Is_Valid_licenseno(licenseno);
            }
        });

    }

    public void Is_Valid_shopNumber(EditText edt) {

        if (edt.getText().toString().matches("[a-zA-Z0-9 ]+")) {

            valid_shopnumber = edt.getText().toString();
        } else if (edt.getText().toString().length() < 2) {

            edt.setError("Invalid Shop Number");
            valid_shopnumber = null;
        } else if (edt.getText().toString().length() > 2) {
            edt.setError("Invalid Shop Number");
            valid_shopnumber = null;
        } else if (!edt.getText().toString().matches("[a-zA-Z0-9 ]+")) {
            edt.setError("Invalid Shop Number");
            valid_shopnumber = null;
        } else if (edt.getText().toString().length() == 2) {
            valid_shopnumber = edt.getText().toString();
        }


    }

    public void Is_Valid_licenseno(EditText edt) {

        if (edt.getText().toString().matches("[a-zA-Z0-9 ]+")) {

            valid_licenseno = edt.getText().toString();
        } else if (edt.getText().toString().length() < 2) {

            edt.setError("Invalid license Number");
            valid_licenseno = null;
        } else if (edt.getText().toString().length() > 2) {
            edt.setError("Invalid license Number");
            valid_licenseno = null;
        } else if (!edt.getText().toString().matches("[a-zA-Z0-9 ]+")) {
            edt.setError("Invalid license Number");
            valid_licenseno = null;
        } else if (edt.getText().toString().length() == 2) {
            valid_licenseno = edt.getText().toString();
        }


    }


    public void Is_Valid_Contact_no(EditText edt) throws NumberFormatException {

        if (edt.getText().toString().length() <= 0) {
            edt.setError("Please enter Contact Number Field");

            valid_Contact_no = null;
        } else if (!edt.getText().toString().matches("[0-9 ]+")) {
            edt.setError("Accept Numbers Only.");

            valid_Contact_no = null;
        } else if (edt.getText().toString().length() < 10) {
            edt.setError("Please enter valid Contact Number");
            valid_Contact_no = null;
        } else if (edt.getText().toString().length() > 10) {
            edt.setError("Please enter valid Contact Number");
            valid_Contact_no = null;
        } else
            valid_Contact_no = edt.getText().toString();
    }



    class RegristrTask extends AsyncTask<String, Void, String> {

        private Exception exception;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(RegistrationActivity.this);
            progressDialog.setMessage("Verify User");
            progressDialog.show();
        }

        protected String doInBackground(String... urls) {


            String params="";
            sb.setLength(0);
            sb.append("{");
            sb.append("\"sessionId\"" + ":\"" + AdatSoftData.SESSION_ID + "\",");
            sb.append("\"handler\"" + ":\"" + AdatSoftData.HANDLE + "\",");
            sb.append("\"license\"" + ":\"" + lno + "\",");
            sb.append("\"shop\"" + ":\"" + sno + "\",");
            sb.append("\"moble\"" + ":\"" + AdatSoftData.MOBILE + "\"");
            sb.append("}");
           // params = sb.toString();
            try {
                JSONObject jsonObject = new JSONObject();

                jsonObject.put("sessionId", AdatSoftData.SESSION_ID);
                jsonObject.put("handler", AdatSoftData.HANDLE);
                jsonObject.put("license", lno);
                jsonObject.put("shop", sno);
                jsonObject.put("moble", AdatSoftData.MOBILE);
                params=jsonObject.toString();
            } catch (Exception e) {
                responsemsg = "error";
                e.printStackTrace();
            }
            try {
            String uri= AdatSoftData.URL + AdatSoftData.METHOD_USER_REGISTRATION +"?";

           // String request = uri + java.net.URLEncoder.encode("user=ankita&pass=1234", "UTF-8");

            url_web = uri + java.net.URLEncoder.encode("MUser="+params, "UTF-8");
             url_web = url_web.replaceAll("%3D", "=");


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

        protected void onPostExecute(String feed) {
            progressDialog.dismiss();

            if (responsemsg.contains("error")) {
                Toast.makeText(getBaseContext(), "error in Registration..", Toast.LENGTH_LONG).show();
            } else if (responsemsg.contains("Success")) {

                responsemsg = responsemsg.substring(1, responsemsg.length() - 1);
                AdatSoftData.OTP_INPUT = responsemsg.replaceAll("[^0-9]", "");

                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("otp", AdatSoftData.OTP_INPUT);
                editor.putString("Mobileno", AdatSoftData.MOBILE);
                editor.putString("shopnumber", sno);
                editor.putString("licenseno", lno);
                editor.putString("URL",AdatSoftData.URL);
                editor.commit();


                Intent intent = new Intent(RegistrationActivity.this, ConfirmOTPActivity.class);
                startActivity(intent);
                RegistrationActivity.this.finish();
            } else if (responsemsg.contains("Fail")) {
                Toast.makeText(RegistrationActivity.this, "Failed to Verify",Toast.LENGTH_SHORT).show();
            } else if (responsemsg.contains("Mobileno Invalid")) {
                Toast.makeText(RegistrationActivity.this, "Invalid Mobile number",Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(RegistrationActivity.this, "Error with service",Toast.LENGTH_SHORT).show();
            }
        }
    }


    /*private class HttpAsyncTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... urls) {
            return POST(urls[0]);
        }

        public  String POST(String url){
            InputStream inputStream = null;
            String result = "";
            try {

                HttpClien httpclient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost
                        ("http://192.168.1.207:420/api/RegisterUserAPI/VerifyMobileUser?MUser=");

                String json = "";
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("accesskey", "12345");
                jsonObject.put("license", "01201");
                jsonObject.put("shop", "01");
                jsonObject.put("moble", "9561068581");


                json = jsonObject.toString();
                StringEntity se = new StringEntity(json);
               // Content-Type: application/json and Accept: application/json
                httpPost.setEntity(se);

                httpPost.setHeader("accept", "application/json");
                httpPost.setHeader("Content-type", "application/json");

                HttpResponse httpResponse = httpclient.execute(httpPost);

                inputStream = httpResponse.getEntity().getContent();

                // convert inputstream to string
                if(inputStream != null)
                { result = convertInputStreamToString(inputStream);
                   // Log.d("json","inputStream result"+result);
                   }
                else
                    result = "Did not work!";
               // Log.d("json","result"+result);

            } catch (Exception e) {
                result = "Did not work!";
               // Log.d("json","e.getLocalizedMessage()"+ e.getLocalizedMessage());
            }

            //  return result
            return result;
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            if(result.equalsIgnoreCase("Did not work!"))
            {
                Toast.makeText(getBaseContext(), "Data not Sent!", Toast.LENGTH_LONG).show();
            }else
            {
                Toast.makeText(getBaseContext(), "Data Sent!", Toast.LENGTH_LONG).show();
            }

        }
    }
*/
    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }


    class LiveURL_Task extends AsyncTask<String, Void, String> {

        private Exception exception;
        String Url;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            AdatSoftData.URL=null;
            responsemsg =null;
            progressDialog = new ProgressDialog(RegistrationActivity.this);
            progressDialog.setMessage("Processing...");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... Key) {
            try{
                String key = Key[0].replaceAll("[^A-Za-z]+", "");
                Url = AdatSoftData.SERVER_URL + key;
                responsemsg = SplashActivity.OpenConnection(Url);
                Log.d("resp", "resp" + responsemsg);
                //Toast.makeText(RegistrationActivity.this,responsemsg,Toast.LENGTH_LONG).show();
            } catch (NullPointerException e) {
                responsemsg = "error";
                e.printStackTrace();
            } catch (Exception e) {
                responsemsg = "error";
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String feed) {
            progressDialog.dismiss();
            String[] parts = responsemsg.split("\"");
            parts[3] = parts[3].substring(0,parts[3].length()-1);
            AdatSoftData.URL = parts[3];
            Toast.makeText(RegistrationActivity.this,AdatSoftData.URL,Toast.LENGTH_LONG).show();
        }
    }
}
