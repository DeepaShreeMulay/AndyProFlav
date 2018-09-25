package com.vritti.vrittiaccounting;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vritti.vrittiaccounting.data.AdatSoftConstants;
import com.vritti.vrittiaccounting.data.AdatSoftData;
import com.vritti.vrittiaccounting.data.NetworkUtils;
import com.vritti.vrittiaccounting.data.StartSession;
import com.vritti.vrittiaccounting.data.utility;
import com.vritti.vrittiaccounting.database.DatabaseHelper;
import com.vritti.vrittiaccounting.interfaces.CallbackInterface;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.text.Layout.JUSTIFICATION_MODE_INTER_WORD;

/**
 * Created by Admin-3 on 7/25/2017.
 */

public class MessagingActivity extends AppCompatActivity {


    String TextMsg;
    Context parent;
    int icondrawable;
    DatabaseHelper db1;
    String responsemsg="";
    MessagingURL asynctask;
    TemplateURL asynstask;
    EditText edtextMessaging;
    FloatingActionButton fab_send;
    String name_mar,category,Name,City,ac_type,Balance,mobile,Bal_cd,updatedate,Acno,name_mar_title;

    EditText edt_mob;
    Button btn_confirm;
    AlertDialog confirmDialog, templateDialog;
    ProgressDialog progressDialog;
    String confirmed_mobile ;
    LinearLayout llsoft, llmild,llharsh;

    @Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_messaging);
    GetIntentExtras();
    getSupportActionBar().setDisplayShowHomeEnabled(true);
    //getSupportActionBar().setIcon(icondrawable);
    getSupportActionBar().setTitle(Name);

    initView();
    setListner();
}
    private void GetIntentExtras() {
        Bundle bundle = getIntent().getExtras();
        if( bundle != null){
            name_mar_title = (String) bundle.get("name_mar_title");
            category = (String) bundle.get("category");
            Acno = (String) bundle.get("Acno");
            Name = (String) bundle.get("Name");
            City = (String) bundle.get("City");
            ac_type = (String) bundle.get("ac_type");
            Balance = (String) bundle.get("Balance");
            mobile = (String) bundle.get("mobile");
            Bal_cd = (String) bundle.get("Bal_cd");
            updatedate = (String) bundle.get("updatedate");
            if (category.equals("Cash")){
                icondrawable = R.drawable.cash;
                name_mar = "Cash";
            }else if (category.equals("Bank")){
                icondrawable = R.drawable.bank;
                name_mar = "Bank";
            }else if (category.equals("Supp")){
                icondrawable = R.drawable.supplier;
                name_mar = "Supplier";
            } else if (category.equals("Cust")){
                icondrawable = R.drawable.customer;
                name_mar = "Customer";
            }else if (category.equals("Hund")){
                icondrawable = R.drawable.bus;
                name_mar = "Transporter";
            }else if (category.equals("Other")){
                icondrawable = R.drawable.other_reciept;
                name_mar = "Other";
            }

        }
    }
    private void initView() {
        parent = MessagingActivity.this;
        db1 = new DatabaseHelper(parent);
        ConfirmDialog();

        /*TextMsg = "Your outstanding as on : "+getDateinFormat(updatedate)+" is \u20B9 "+Balance+" "+getCorD(Bal_cd)+
                ".\n Please arrange for the payment. Ignore if already paid. Thank you." +
                "\n Contact : "+getFirmName()+"\n Phone : "+getFirmMob();*/
        edtextMessaging = (EditText) findViewById(R.id.edtextMessaging);

        fab_send = (FloatingActionButton) findViewById(R.id.fab_send);
    }

    private void TemplateDialog() {
        LayoutInflater inflater = LayoutInflater.from(parent);
        View subView1 = inflater.inflate(R.layout.confirm_templatelist, null);
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(parent);
        builder.setView(subView1);

        llsoft = (LinearLayout) subView1.findViewById(R.id.llsoft);
        llmild = (LinearLayout) subView1.findViewById(R.id.llmild);
        llharsh = (LinearLayout) subView1.findViewById(R.id.llharsh);

        llsoft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callForTemplateUrl("S");
                templateDialog.dismiss();
            }
        });

        llmild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callForTemplateUrl("M");
                templateDialog.dismiss();
            }
        });
        llharsh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callForTemplateUrl("H");
                templateDialog.dismiss();
            }
        });

        templateDialog = builder.create();
        templateDialog.show();
    }

    private void callForTemplateUrl(String s) {
        if (NetworkUtils.isNetworkAvailable(parent)) {
            asynstask = null;
            asynstask = new TemplateURL();
            asynstask.execute(s);
        } else {
            Toast.makeText(parent, "No internet..", Toast.LENGTH_LONG).show();
            // callSnackbar();
        }
    }

    private String getCorD(String Bal_cd) {
        String result=null;
        if(Bal_cd.equals("Cr")){
            result = "Credit";
        }else if(Bal_cd.equals("Dr")){
            result = "Debit";
        }
        return result;
    }

    private String getDateinFormat(String amcExpireDt) {
        String result= null;
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa");
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd MMM, yyyy");
        try {
            Date date2 = dateFormat1.parse(amcExpireDt);
            result = dateFormat2.format(date2);
        }catch( Exception e){
            e.printStackTrace();
        }
        return result;
    }
    private String getFirmName() {
        String result= null;
        try{
            DatabaseHelper db1 = new DatabaseHelper(parent);
            SQLiteDatabase sql = db1.getWritableDatabase();
            Cursor cursor = sql.rawQuery("Select name_mar from "+ AdatSoftConstants.TABLE_FIRM+" WHERE shop_no='"+AdatSoftData.Selected_Sno+"'", null);
            cursor.moveToFirst();
            if (cursor != null && cursor.getCount()>0) {
                result = cursor.getString(cursor.getColumnIndex("name_mar"));
            }else{
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }
    private String getFirmMob() {
        String result= null;
        try{
            DatabaseHelper db1 = new DatabaseHelper(parent);
            SQLiteDatabase sql = db1.getWritableDatabase();
            Cursor cursor = sql.rawQuery("Select mobile from "+ AdatSoftConstants.TABLE_FIRM+" WHERE shop_no='"+AdatSoftData.Selected_Sno+"'", null);
            cursor.moveToFirst();
            if (cursor != null && cursor.getCount()>0) {
                result = cursor.getString(cursor.getColumnIndex("mobile"));
            }else{
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }

    private String getUserid() {
        String result= null;
        try{
            DatabaseHelper db1 = new DatabaseHelper(parent);
            SQLiteDatabase sql = db1.getWritableDatabase();
            Cursor cursor = sql.rawQuery("Select UserName from "+ AdatSoftConstants.TABLE_FIRMWISE_USERNAME+" WHERE shop_no='"+AdatSoftData.Selected_Sno+"'", null);
            cursor.moveToFirst();
            if (cursor != null && cursor.getCount()>0) {
                result = cursor.getString(cursor.getColumnIndex("UserName"));
            }else{
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }
    private String getPassword() {
        String result= null;
        try{
            DatabaseHelper db1 = new DatabaseHelper(parent);
            SQLiteDatabase sql = db1.getWritableDatabase();
            Cursor cursor = sql.rawQuery("Select Password from "+ AdatSoftConstants.TABLE_FIRMWISE_USERNAME+" WHERE shop_no='"+AdatSoftData.Selected_Sno+"'", null);
            cursor.moveToFirst();
            if (cursor != null && cursor.getCount()>0) {
                result = cursor.getString(cursor.getColumnIndex("Password"));
            }else{
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }
    private void setListner() {
        fab_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextMsg = edtextMessaging.getText().toString();
                if (NetworkUtils.isNetworkAvailable(parent)) {
                    if ((AdatSoftData.SESSION_ID != null)
                            && (AdatSoftData.HANDLE != null)) {
                        new MessagingURL().execute();
                    } else {
                        new StartSession(parent, new CallbackInterface() {

                            @Override
                            public void callMethod() {
                                new MessagingURL().execute();
                            }
                        });
                    }
                } else {
                    Toast.makeText(parent, "No internet..", Toast.LENGTH_LONG).show();
                    // callSnackbar();
                }
                //ConfirmDialog();
            }
        });
    }


    private void ConfirmDialog() {
        LayoutInflater inflater = LayoutInflater.from(parent);
        View subView1 = inflater.inflate(R.layout.confirm_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(parent);
        builder.setView(subView1);

        edt_mob = (EditText) subView1.findViewById(R.id.edt_mob);
        edt_mob.setHint("Enter Mobile no");
        btn_confirm = (Button) subView1.findViewById(R.id.btn_confirm);
        edt_mob.setText(mobile);

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmed_mobile = edt_mob.getText().toString();
                System.out.println("confirmed" + confirmed_mobile);
                TemplateDialog();
                /*if (NetworkUtils.isNetworkAvailable(parent)) {
                    asynctask = null;
                    asynctask = new MessagingURL();
                    asynctask.execute();
                } else {
                    Toast.makeText(parent, "No internet..", Toast.LENGTH_LONG).show();
                    // callSnackbar();
                }*/
                confirmDialog.dismiss();
            }
        });
        confirmDialog = builder.create();
        confirmDialog.show();
    }


    public class TemplateURL extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            /*progressDialog = new ProgressDialog(parent);
            progressDialog.setMessage("Receiving Template");
            progressDialog.setCancelable(false);
            progressDialog.show();*/
        }

        @Override
        protected String doInBackground(String... params) {
            String urlString1 = AdatSoftData.URL + AdatSoftData.METHOD_GET_DATA + AdatSoftData.METHOD_SMSTEMPLT
                    + "key=" + params[0] + "&Cust_no=" + AdatSoftData.Lno;
            try {
                urlString1 = urlString1.replaceAll(" ", "%20");
                responsemsg = SplashActivity.OpenConnection(urlString1);
                Log.e("resp", "resp" + responsemsg);
            } catch (Exception e) {
                responsemsg = "Error";
                e.printStackTrace();
            }

            return responsemsg;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                Log.e("Post madhe responsemsg", responsemsg);
               // progressDialog.dismiss();
                if (responsemsg.equals("Error")) {
                    Toast.makeText(parent," Error ",Toast.LENGTH_SHORT).show();
                } else if (responsemsg.contains("#Amount#")) {
                    responsemsg = responsemsg.replace("\"","");
                    responsemsg = responsemsg.replace("#Date#",getDateinFormat(updatedate));
                    responsemsg = responsemsg.replace("RS #Amount#","\u20B9 "+Balance);
                    responsemsg = responsemsg.replace("RS. #Amount#","\u20B9 "+Balance);
                    responsemsg = responsemsg.replace("#Contact#",getFirmName()+"\n Phone : "+getFirmMob());
                    TextMsg = responsemsg;
                    edtextMessaging.setText(TextMsg);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        edtextMessaging.setJustificationMode(JUSTIFICATION_MODE_INTER_WORD);
                    }
                } else {
                    Toast.makeText(parent,"It seems to have some issue..\nTry again after sometime..",Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class MessagingURL extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            utility ut = new utility();
            String urlString1 = AdatSoftData.URL + AdatSoftData.METHOD_GET_DATA + AdatSoftData.METHOD_SENDSMS +
            "?sessionId=" + AdatSoftData.SESSION_ID + "&handler=" + AdatSoftData.HANDLE + "&cust_no=" + AdatSoftData.Lno +
                    "&MobNo=" + mobile + "&SMS=" + TextMsg;
            try {
                //urlString1 = urlString1.replaceAll(" ", "%20");
                urlString1 = urlString1.replaceAll(" ", "%20");
                urlString1 = urlString1.replaceAll("\\n", "%20");
                //responsemsg = ut.httpGet(urlString1);
                responsemsg = SplashActivity.OpenConnection(urlString1);
                Log.e("resp", "resp" + responsemsg);

            } catch (Exception e) {
                responsemsg = "Error";
                e.printStackTrace();
            }

            return responsemsg;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                Log.e("Post madhe responsemsg", responsemsg);

                if (responsemsg.equals("Error")) {
                    Toast.makeText(parent," Error ",Toast.LENGTH_SHORT).show();
                } else if (responsemsg.contains("error")) {
                    Toast.makeText(getBaseContext(), "error in sending message", Toast.LENGTH_LONG).show();
                } else if (responsemsg.contains("SessionExpired")) {
                    new StartSession(parent, new CallbackInterface() {

                        @Override
                        public void callMethod() {
                            new MessagingURL().execute();
                        }
                    });
                } else if (responsemsg.equals("\"SMS sent sucessfully\"")) {
                    Toast.makeText(parent,"Message send Successfully",Toast.LENGTH_SHORT).show();
                    TextMsg = "";
                    edtextMessaging.setText(TextMsg);
                } else {
                    Toast.makeText(parent,"It seems to have some issue..\nTry again after sometime..",Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
