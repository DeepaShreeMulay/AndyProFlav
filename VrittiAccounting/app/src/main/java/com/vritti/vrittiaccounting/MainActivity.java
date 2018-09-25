package com.vritti.vrittiaccounting;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ListView;
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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity {

    ListView listview_firm_list;
    ProgressDialog progressDialog;
    DatabaseHelper db1;
    TextView tvnodata;
    String responsemsg = "";
    Toolbar toolbar;

    utility ut;
    Context parent;
    FirmMaster firmMaster;
    SharedPreferences sharedpreferences;
    public static ArrayList<FirmMaster> firmMasterArrayList;

    String dialogopen="no";
    Dialog dialog;

    String PlayStoreVersion = null;
    String MyAppVersion = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //getSupportActionBar().setLogo(R.drawable.logo_gray_small);
        getSupportActionBar().setTitle(AdatSoftData.MODULE_TITLE);
        //getSupportActionBar().setDisplayShowTitleEnabled(false);
        //getSupportActionBar().setDisplayUseLogoEnabled(true);
        initView();

        if (NetworkUtils.isNetworkAvailable(parent)) {
            new PlayStoreVersionAsyncTask().execute();
        }
        if (dbvalue()) {
            getDataFromDataBase();
        } else{
            tvnodata.setVisibility(View.VISIBLE);
            listview_firm_list.setVisibility(View.GONE);
        }
        setListner();
    }




    public void showUpdateDialog(String PSVersion, final String Projectid, Context context) {
        try {
            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("New Update Available");
            builder.setMessage(" New "+AdatSoftData.MODULE_TITLE +" "+ PSVersion + " is on Playstore."
                    /*"(Note: In playstore 'OPEN' button is visible instead of 'UPDATE', Uninstall and Install app)"*/);

            builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse
                            ("market://details?id="+Projectid)));
                    dialogopen = "no";
                    dialog.dismiss();
                }
            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //background.start();
                    dialogopen = "no";
                    dialog.dismiss();
                }
            });

            builder.setCancelable(false);

            dialog = builder.show();
            dialogopen = "yes";
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean dbvalue(){
        try{
            DatabaseHelper db1 = new DatabaseHelper(getBaseContext());
            SQLiteDatabase sql = db1.getWritableDatabase();
            Cursor cursor = sql.rawQuery("Select * from "+AdatSoftConstants.TABLE_FIRM, null);
            cursor.moveToFirst();
            if (cursor != null && cursor.getCount()>0) {
                cursor.close();
                sql.close();
                db1.close();
                return true;
            }else{
                cursor.close();
                sql.close();
                db1.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }


    private void initView() {
        parent = MainActivity.this;
        ut = new utility();
        firmMasterArrayList = new ArrayList<FirmMaster>();
        db1 = new DatabaseHelper(parent);
        tvnodata = (TextView) findViewById(R.id.tvnodata);
        listview_firm_list = (ListView) findViewById(R.id.listview_firm_list);
        sharedpreferences = getSharedPreferences(RegistrationActivity.MyPREFERENCES,
                Context.MODE_PRIVATE);
        AdatSoftData.Lno = sharedpreferences.getString("licenseno", null);
        AdatSoftData.Sno = sharedpreferences.getString("shopnumber", null);
        AdatSoftData.Selected_Sno=null;
    }

    private void setListner() {
        listview_firm_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,final int position, long id) {
                /*Animation myAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.escape_left_to_right);
                view.startAnimation(myAnim);*/
                Animation animation1 = new AlphaAnimation(0.3f, 1.0f);
                animation1.setDuration(800);
                view.startAnimation(animation1);
                view.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        //Intent intent = new Intent(MainActivity.this, FirmDetailsActivity.class);
                        Intent intent = new Intent(MainActivity.this, Main2Activity.class);
                        Bundle extras = new Bundle();
                        AdatSoftData.Selected_Sno = firmMasterArrayList.get(position).getShop_no();
                        extras.putString("cust_no", firmMasterArrayList.get(position).getCust_no());
                        extras.putString("shop_no", firmMasterArrayList.get(position).getShop_no());
                        extras.putString("name_mar", firmMasterArrayList.get(position).getName_mar());
                        extras.putString("address", firmMasterArrayList.get(position).getAddress());
                        extras.putString("mobile", firmMasterArrayList.get(position).getMobile());
                        extras.putString("AMCExpireDt", firmMasterArrayList.get(position).getAMCExpireDt());
                        extras.putString("Module", firmMasterArrayList.get(position).getModule());
                        intent.putExtras(extras);
                        startActivity(intent);
                        finish();
                    }
                }, 0);
            }
        });
    }

    private void getDataFromDataBase() {
        // TODO Auto-generated method stub
        firmMasterArrayList.clear();

        DatabaseHelper db1 = new DatabaseHelper(parent);
        SQLiteDatabase db = db1.getWritableDatabase();

        Cursor cursor1 = db.rawQuery("Select * from "
                + AdatSoftConstants.TABLE_FIRM +
                " order by shop_no", null);
        Log.d("test", "" + cursor1.getCount());
        if (cursor1.getCount() > 0) {
            cursor1.moveToFirst();
            try {
                do {
                    firmMaster = new FirmMaster();
                    firmMaster.setCust_no(cursor1.getString(cursor1.getColumnIndex("cust_no")));
                    firmMaster.setShop_no(cursor1.getString(cursor1.getColumnIndex("shop_no")));
                    firmMaster.setName_mar(cursor1.getString(cursor1.getColumnIndex("name_mar")));
                    firmMaster.setAddress(cursor1.getString(cursor1.getColumnIndex("address")));
                    firmMaster.setMobile(cursor1.getString(cursor1.getColumnIndex("mobile")));
                    firmMaster.setAMCExpireDt(cursor1.getString(cursor1.getColumnIndex("AMCExpireDt")));
                    firmMaster.setModule(cursor1.getString(cursor1.getColumnIndex("Module")));
                    firmMasterArrayList.add(firmMaster);
                } while (cursor1.moveToNext());
            } finally {
                cursor1.close();
            }
        }
        FirmListAdapter firmListAdapter = new FirmListAdapter(parent, firmMasterArrayList);
        listview_firm_list.setAdapter(firmListAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.actionbarmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                if (NetworkUtils.isNetworkAvailable(parent)) {
                    if ((AdatSoftData.SESSION_ID != null)
                            && (AdatSoftData.HANDLE != null)) {
                        new FirmMasterAPI().execute();
                    } else {
                        new StartSession(parent, new CallbackInterface() {

                            @Override
                            public void callMethod() {
                                new FirmMasterAPI().execute();
                            }
                        });
                    }
                   // Toast.makeText(parent, "No internet..", Toast.LENGTH_LONG).show();
                    // callSnackbar();
                }
                else{
                    Toast.makeText(parent, "No internet..", Toast.LENGTH_LONG).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class PlayStoreVersionAsyncTask extends AsyncTask<String, Void, String> {


       @Override
        protected String doInBackground(String... params) {
           try {
               MyAppVersion = (getPackageManager().getPackageInfo(getPackageName(), 0).versionName);

               Document doc = Jsoup.connect("https://play.google.com/store/apps/details?id="//com.stavigilmonitoring
                       + AdatSoftData.ProjectID).get();
               String AllStr = doc.text();
               String parts[] = AllStr.split("Current Version");
               String newparts[] = parts[1].split("Requires Android");
               PlayStoreVersion = newparts[0].trim();

           }
           catch (PackageManager.NameNotFoundException e) {
               e.printStackTrace();

           }catch (NullPointerException e){
               e.printStackTrace();

           }catch (Exception e){
               e.printStackTrace();

           }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if(!MyAppVersion.equals(PlayStoreVersion)){
                if(dialogopen.equalsIgnoreCase("no")) {
                    SharedPreferences LoginPref = getApplicationContext()
                            .getSharedPreferences("SetupPref",Context.MODE_PRIVATE); // 0 - for private mode
                    SharedPreferences.Editor edtcv = LoginPref.edit();
                    edtcv.putString("Dialog", "NoDialog");
                    edtcv.commit();
                    showUpdateDialog(PlayStoreVersion,AdatSoftData.ProjectID,parent);
                }
            }
        }
    }

    public class FirmMasterAPI extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(parent);
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
                Log.e("resp", "resp" + responsemsg);
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
                Toast.makeText(getBaseContext(), "error in firmlist..", Toast.LENGTH_LONG).show();
            } else if (responsemsg.contains("SessionExpired")) {
                new StartSession(parent, new CallbackInterface() {

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

                if (NetworkUtils.isNetworkAvailable(parent)) {
                    if ((AdatSoftData.SESSION_ID != null)
                            && (AdatSoftData.HANDLE != null)) {
                        new FirmMasterAPI_data().execute(responsemsg);
                    } else {
                        new StartSession(parent, new CallbackInterface() {

                            @Override
                            public void callMethod() {
                                new FirmMasterAPI_data().execute(responsemsg);
                            }
                        });
                    }
                } else {
                    Toast.makeText(parent, "No internet..", Toast.LENGTH_LONG).show();
                    // callSnackbar();
                }

            }
        }
    }

    public class FirmMasterAPI_data extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(parent);
            progressDialog.setMessage("Sync Setup Master");
            progressDialog.show();
        }


        @Override
        protected String doInBackground(String... params) {
            int resp_count = Integer.parseInt(params[0]);
            int k = 0;

            String urlString1 = AdatSoftData.URL + AdatSoftData.METHOD_GET_DATA
                    + "?from=" + k + "&to=" + resp_count;
            try {
                urlString1 = urlString1.replaceAll(" ", "%20");
                responsemsg = SplashActivity.OpenConnection(urlString1);
                Log.e("resp", "resp" + responsemsg);
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
                                    "AMCExpireDt"), jsonArray.getJSONObject(i1).getString(
                                    "Module"));
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
            return responsemsg;
        }


        @Override
        protected void onPostExecute(String result) {
            progressDialog.dismiss();
            if (responsemsg.contains("error")) {
                Toast.makeText(getBaseContext(), "error!", Toast.LENGTH_LONG).show();
            } else if (responsemsg.contains("SessionExpired")) {
                new StartSession(parent, new CallbackInterface() {

                    @Override
                    public void callMethod() {
                        new FirmMasterAPI().execute();
                    }
                });
            }
            getDataFromDataBase();
        }
    }
}