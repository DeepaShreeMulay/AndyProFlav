package com.vritti.vrittiaccounting;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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

import org.json.JSONArray;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Admin-3 on 7/21/2017.
 */

public class FirmDetailsActivity extends AppCompatActivity {

    String cust_no ;String shop_no ;String name_mar ;String address ;String mobile ;String AMCExpireDt; String Module;

    int icondrawable;
    TextView tvcash, tvbank, tvsupp, tvcust, tvtrans, tvother;
    LinearLayout llcash, llbank, llsupp, llcust, lltrans, llother;
    Context parent;
    utility ut;
    ProgressDialog progressDialog;
    DatabaseHelper db1;
    String responsemsg = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firm_details);
        GetIntentExtras();
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //getSupportActionBar().setIcon(icondrawable);
        getSupportActionBar().setTitle(name_mar);
        // getSupportActionBar().setDisplayUseLogoEnabled(true);
        initView();
        if (!dbvalue()){

            tvcash.setText("Rs "+"0.00");
            tvbank.setText("Rs "+"0.00");
            tvsupp.setText("Rs "+"0.00");
            tvcust.setText("Rs "+"0.00");
            tvtrans.setText("Rs "+"0.00");
            tvother.setText("Rs "+"0.00");

            if ((AdatSoftData.SESSION_ID != null)
                    && (AdatSoftData.HANDLE != null)) {
                new FirmDetailAPI().execute();
            } else {
                new StartSession(parent, new CallbackInterface() {

                    @Override
                    public void callMethod() {
                        new FirmDetailAPI().execute();
                    }
                });
            }
        } else {
            getDataFromDataBase();
        }
        setListner();
    }

    private void getDataFromDataBase() {
        getCashBal(); getBankBal();
        getSuppBal(); getCustBal();
        getTransBal(); getOtherBal();
    }

    private void getCashBal() {
        float SumCr = (float) 0.00;        float SumDr = (float) 0.00;        float SumT = (float) 0.00;
        String Scr, Sdr, St;
        DatabaseHelper db1 = new DatabaseHelper(parent);
        SQLiteDatabase db = db1.getWritableDatabase();

        Cursor cursor1 = db.rawQuery("Select CAST(Sum(Balance) as float) as Crtotal from "+AdatSoftConstants.TABLE_FIRM_DETAILS
                +AdatSoftData.Selected_Sno+" where ac_type ='Cash' and Bal_cd='Cr'"+
                " order by Name", null);
        Log.d("test", "" + cursor1.getCount());
        Log.e("Cr count", ""+cursor1.getCount());
        if (cursor1.getCount() > 0) {
            cursor1.moveToFirst();
            if (String.valueOf(cursor1.getFloat(cursor1.getColumnIndex("Crtotal"))).equals(null)){
                Log.e("Cr Sum", "Rs 0.00");
            }else {
                Scr=String.valueOf(cursor1.getFloat(cursor1.getColumnIndex("Crtotal")));
                Log.e("Cr Sum", String.valueOf(cursor1.getFloat(cursor1.getColumnIndex("Crtotal"))));
                SumCr = Float.parseFloat(Scr);
            }
        } else {
            Log.e("Cr Sum", "Not found");
        }

        Cursor cursor2 = db.rawQuery("Select CAST(Sum(Balance) as float) as Drtotal from "+AdatSoftConstants.TABLE_FIRM_DETAILS
                +AdatSoftData.Selected_Sno+" where ac_type ='Cash' and Bal_cd='Dr'"+
                " order by Name", null);
        Log.d("test", "" + cursor2.getCount());
        Log.e("Dr count", ""+cursor2.getCount());
        if (cursor2.getCount() > 0) {
            cursor2.moveToFirst();
            if (String.valueOf(cursor2.getFloat(cursor2.getColumnIndex("Drtotal"))).equals(null)){
                Log.e("Dr Sum", "Rs 0.00");
            }else {
                Sdr=String.valueOf(cursor2.getFloat(cursor2.getColumnIndex("Drtotal")));
                Log.e("Dr Sum", String.valueOf(cursor2.getFloat(cursor2.getColumnIndex("Drtotal"))));
                SumDr = Float.parseFloat(Sdr);
            }
        } else {
            Log.e("Dr Sum", "Not found");
        }

        SumT = ((SumDr - SumCr)/100000);
        //SumT = df.setRoundingMode(SumT);
        St = String.format("%.2f",SumT);
        SumT = Float.parseFloat(St);
        if (SumT>0){
            Log.e("set text", "Rs "+SumT+" Debit");
            tvcash.setText("Rs "+SumT+" Lakh Dr");
        } else if (SumT<0){
            Log.e("set text", "Rs "+Math.abs(SumT)+" Credit");
            tvcash.setText("Rs "+Math.abs(SumT)+" Lakh Cr");
        } else if (SumT==0){
            Log.e("set text", "Rs "+"0.00");
            tvcash.setText("Rs "+"0.00");
        }
    }

    private void getBankBal() {
        float SumCr = (float) 0.00;        float SumDr = (float) 0.00;        float SumT = (float) 0.00;
        String Scr, Sdr, St;
        DatabaseHelper db1 = new DatabaseHelper(parent);
        SQLiteDatabase db = db1.getWritableDatabase();

        Cursor cursor1 = db.rawQuery("Select CAST(Sum(Balance) as float) as Crtotal from "+AdatSoftConstants.TABLE_FIRM_DETAILS
                +AdatSoftData.Selected_Sno+" where ac_type ='Bank' and Bal_cd='Cr'"+
                " order by Name", null);
        Log.d("test", "" + cursor1.getCount());
        Log.e("Cr count", ""+cursor1.getCount());
        if (cursor1.getCount() > 0) {
            cursor1.moveToFirst();
            if (String.valueOf(cursor1.getFloat(cursor1.getColumnIndex("Crtotal"))).equals(null)){
                Log.e("Cr Sum", "Rs 0.00");
            }else {
                Scr=String.valueOf(cursor1.getFloat(cursor1.getColumnIndex("Crtotal")));
                Log.e("Cr Sum", String.valueOf(cursor1.getFloat(cursor1.getColumnIndex("Crtotal"))));
                SumCr = Float.parseFloat(Scr);
            }
        } else {
            Log.e("Cr Sum", "Not found");
        }

        Cursor cursor2 = db.rawQuery("Select CAST(Sum(Balance) as float) as Drtotal from "+AdatSoftConstants.TABLE_FIRM_DETAILS
                +AdatSoftData.Selected_Sno+" where ac_type ='Bank' and Bal_cd='Dr'"+
                " order by Name", null);
        Log.d("test", "" + cursor2.getCount());
        Log.e("Dr count", ""+cursor2.getCount());
        if (cursor2.getCount() > 0) {
            cursor2.moveToFirst();
            if (String.valueOf(cursor2.getFloat(cursor2.getColumnIndex("Drtotal"))).equals(null)){
                Log.e("Dr Sum", "Rs 0.00");
            }else {
                Sdr=String.valueOf(cursor2.getFloat(cursor2.getColumnIndex("Drtotal")));
                Log.e("Dr Sum", String.valueOf(cursor2.getFloat(cursor2.getColumnIndex("Drtotal"))));
                SumDr = Float.parseFloat(Sdr);
            }
        } else {
            Log.e("Dr Sum", "Not found");
        }

        SumT = ((SumDr - SumCr)/100000);
        //SumT = df.setRoundingMode(SumT);
        St = String.format("%.2f",SumT);
        SumT = Float.parseFloat(St);
        if (SumT>0){
            Log.e("set text", "Rs "+SumT+" Debit");
            tvbank.setText("Rs "+SumT+" Lakh Dr");
        } else if (SumT<0){
            Log.e("set text", "Rs "+Math.abs(SumT)+" Credit");
            tvbank.setText("Rs "+Math.abs(SumT)+" Lakh Cr");
        } else if (SumT==0){
            Log.e("set text", "Rs "+"0.00");
            tvbank.setText("Rs "+"0.00");
        }
    }

    private void getSuppBal() {
        float SumCr = (float) 0.00;        float SumDr = (float) 0.00;        float SumT = (float) 0.00;
        String Scr, Sdr, St;
        DatabaseHelper db1 = new DatabaseHelper(parent);
        SQLiteDatabase db = db1.getWritableDatabase();

        Cursor cursor1 = db.rawQuery("Select CAST(Sum(Balance) as float) as Crtotal from "+AdatSoftConstants.TABLE_FIRM_DETAILS
                +AdatSoftData.Selected_Sno+" where ac_type ='Supp' and Bal_cd='Cr'"+
                " order by Name", null);
        Log.d("test", "" + cursor1.getCount());
        Log.e("Cr count", ""+cursor1.getCount());
        if (cursor1.getCount() > 0) {
            cursor1.moveToFirst();
            if (String.valueOf(cursor1.getFloat(cursor1.getColumnIndex("Crtotal"))).equals(null)){
                Log.e("Cr Sum", "Rs 0.00");
            }else {
                Scr=String.valueOf(cursor1.getFloat(cursor1.getColumnIndex("Crtotal")));
                Log.e("Cr Sum", String.valueOf(cursor1.getFloat(cursor1.getColumnIndex("Crtotal"))));
                SumCr = Float.parseFloat(Scr);
            }
        } else {
            Log.e("Cr Sum", "Not found");
        }

        Cursor cursor2 = db.rawQuery("Select CAST(Sum(Balance) as float) as Drtotal from "+AdatSoftConstants.TABLE_FIRM_DETAILS
                +AdatSoftData.Selected_Sno+" where ac_type ='Supp' and Bal_cd='Dr'"+
                " order by Name", null);
        Log.d("test", "" + cursor2.getCount());
        Log.e("Dr count", ""+cursor2.getCount());
        if (cursor2.getCount() > 0) {
            cursor2.moveToFirst();
            if (String.valueOf(cursor2.getFloat(cursor2.getColumnIndex("Drtotal"))).equals(null)){
                Log.e("Dr Sum", "Rs 0.00");
            }else {
                Sdr=String.valueOf(cursor2.getFloat(cursor2.getColumnIndex("Drtotal")));
                Log.e("Dr Sum", String.valueOf(cursor2.getFloat(cursor2.getColumnIndex("Drtotal"))));
                SumDr = Float.parseFloat(Sdr);
            }
        } else {
            Log.e("Dr Sum", "Not found");
        }

        SumT = ((SumDr - SumCr)/100000);
        //SumT = df.setRoundingMode(SumT);
        St = String.format("%.2f",SumT);
        SumT = Float.parseFloat(St);

        if (SumT>0){
            Log.e("set text", "Rs "+SumT+" Debit");
            tvsupp.setText("Rs "+SumT+" Lakh Dr");
        } else if (SumT<0){
            Log.e("set text", "Rs "+Math.abs(SumT)+" Credit");
            tvsupp.setText("Rs "+Math.abs(SumT)+" Lakh Cr");
        } else if (SumT==0){
            Log.e("set text", "Rs "+"0.00");
            tvsupp.setText("Rs "+"0.00");
        }
    }

    private void getCustBal() {
        float SumCr = (float) 0.00;        float SumDr = (float) 0.00;        float SumT = (float) 0.00;
        String Scr, Sdr, St;
        DatabaseHelper db1 = new DatabaseHelper(parent);
        SQLiteDatabase db = db1.getWritableDatabase();

        Cursor cursor1 = db.rawQuery("Select CAST(Sum(Balance) as float) as Crtotal from "+AdatSoftConstants.TABLE_FIRM_DETAILS
                +AdatSoftData.Selected_Sno+" where ac_type ='Cust' and Bal_cd='Cr'"+
                " order by Name", null);
        Log.d("test", "" + cursor1.getCount());
        Log.e("Cr count", ""+cursor1.getCount());
        if (cursor1.getCount() > 0) {
            cursor1.moveToFirst();
            if (String.valueOf(cursor1.getFloat(cursor1.getColumnIndex("Crtotal"))).equals(null)){
                Log.e("Cr Sum", "Rs 0.00");
            }else {
                Scr=String.valueOf(cursor1.getFloat(cursor1.getColumnIndex("Crtotal")));
                Log.e("Cr Sum", String.valueOf(cursor1.getFloat(cursor1.getColumnIndex("Crtotal"))));
                SumCr = Float.parseFloat(Scr);
            }
        } else {
            Log.e("Cr Sum", "Not found");
        }

        Cursor cursor2 = db.rawQuery("Select CAST(Sum(Balance) as float) as Drtotal from "+AdatSoftConstants.TABLE_FIRM_DETAILS
                +AdatSoftData.Selected_Sno+" where ac_type ='Cust' and Bal_cd='Dr'"+
                " order by Name", null);
        Log.d("test", "" + cursor2.getCount());
        Log.e("Dr count", ""+cursor2.getCount());
        if (cursor2.getCount() > 0) {
            cursor2.moveToFirst();
            if (String.valueOf(cursor2.getFloat(cursor2.getColumnIndex("Drtotal"))).equals(null)){
                Log.e("Dr Sum", "Rs 0.00");
            }else {
                Sdr=String.valueOf(cursor2.getFloat(cursor2.getColumnIndex("Drtotal")));
                Log.e("Dr Sum", String.valueOf(cursor2.getFloat(cursor2.getColumnIndex("Drtotal"))));
                SumDr = Float.parseFloat(Sdr);
            }
        } else {
            Log.e("Dr Sum", "Not found");
        }
        //DecimalFormat df = new DecimalFormat("#.##");
        SumT = ((SumDr - SumCr)/100000);
        //SumT = df.setRoundingMode(SumT);
        St = String.format("%.2f",SumT);
        SumT = Float.parseFloat(St);

        if (SumT>0){
            Log.e("set text", "Rs "+SumT+" Debit");
            tvcust.setText("Rs "+SumT+" Lakh Dr");
        } else if (SumT<0){
            Log.e("set text", "Rs "+Math.abs(SumT)+" Credit");
            tvcust.setText("Rs "+Math.abs(SumT)+" Lakh Cr");
        } else if (SumT==0){
            Log.e("set text", "Rs "+"0.00");
            tvcust.setText("Rs "+"0.00");
        }
    }

    private void getTransBal() {
        float SumCr = (float) 0.00;        float SumDr = (float) 0.00;        float SumT = (float) 0.00;
        String Scr, Sdr, St;
        DatabaseHelper db1 = new DatabaseHelper(parent);
        SQLiteDatabase db = db1.getWritableDatabase();

        Cursor cursor1 = db.rawQuery("Select CAST(Sum(Balance) as float) as Crtotal from "+AdatSoftConstants.TABLE_FIRM_DETAILS
                +AdatSoftData.Selected_Sno+" where ac_type ='Hund' and Bal_cd='Cr'"+
                " order by Name", null);
        Log.d("test", "" + cursor1.getCount());
        Log.e("Cr count", ""+cursor1.getCount());
        if (cursor1.getCount() > 0) {
            cursor1.moveToFirst();
            if (String.valueOf(cursor1.getFloat(cursor1.getColumnIndex("Crtotal"))).equals(null)){
                Log.e("Cr Sum", "Rs 0.00");
            }else {
                Scr=String.valueOf(cursor1.getFloat(cursor1.getColumnIndex("Crtotal")));
                Log.e("Cr Sum", String.valueOf(cursor1.getFloat(cursor1.getColumnIndex("Crtotal"))));
                SumCr = Float.parseFloat(Scr);
            }
        } else {
            Log.e("Cr Sum", "Not found");
        }

        Cursor cursor2 = db.rawQuery("Select CAST(Sum(Balance) as float) as Drtotal from "+AdatSoftConstants.TABLE_FIRM_DETAILS
                +AdatSoftData.Selected_Sno+" where ac_type ='Hund' and Bal_cd='Dr'"+
                " order by Name", null);
        Log.d("test", "" + cursor2.getCount());
        Log.e("Dr count", ""+cursor2.getCount());
        if (cursor2.getCount() > 0) {
            cursor2.moveToFirst();
            if (String.valueOf(cursor2.getFloat(cursor2.getColumnIndex("Drtotal"))).equals(null)){
                Log.e("Dr Sum", "Rs 0.00");
            }else {
                Sdr=String.valueOf(cursor2.getFloat(cursor2.getColumnIndex("Drtotal")));
                Log.e("Dr Sum", String.valueOf(cursor2.getFloat(cursor2.getColumnIndex("Drtotal"))));
                SumDr = Float.parseFloat(Sdr);
            }
        } else {
            Log.e("Dr Sum", "Not found");
        }
        SumT = ((SumDr - SumCr)/100000);
        //SumT = df.setRoundingMode(SumT);
        St = String.format("%.2f",SumT);
        SumT = Float.parseFloat(St);
        if (SumT>0){
            Log.e("set text", "Rs "+SumT+" Debit");
            tvtrans.setText("Rs "+SumT+" Lakh Dr");
        } else if (SumT<0){
            Log.e("set text", "Rs "+Math.abs(SumT)+" Credit");
            tvtrans.setText("Rs "+Math.abs(SumT)+" Lakh Cr");
        } else if (SumT==0){
            Log.e("set text", "Rs "+"0.00");
            tvtrans.setText("Rs "+"0.00");
        }
    }

    private void getOtherBal() {
        float SumCr = (float) 0.00;        float SumDr = (float) 0.00;        float SumT = (float) 0.00;
        String Scr, Sdr, St;
        DatabaseHelper db1 = new DatabaseHelper(parent);
        SQLiteDatabase db = db1.getWritableDatabase();

        Cursor cursor1 = db.rawQuery("Select CAST(Sum(Balance) as float) as Crtotal from "+AdatSoftConstants.TABLE_FIRM_DETAILS
                +AdatSoftData.Selected_Sno+" where ac_type ='Other' and Bal_cd='Cr'"+
                " order by Name", null);
        Log.d("test", "" + cursor1.getCount());
        Log.e("Cr count", ""+cursor1.getCount());
        if (cursor1.getCount() > 0) {
            cursor1.moveToFirst();
            if (String.valueOf(cursor1.getFloat(cursor1.getColumnIndex("Crtotal"))).equals(null)){
                Log.e("Cr Sum", "Rs 0.00");
            }else {
                Scr=String.valueOf(cursor1.getFloat(cursor1.getColumnIndex("Crtotal")));
                Log.e("Cr Sum", String.valueOf(cursor1.getFloat(cursor1.getColumnIndex("Crtotal"))));
                SumCr = Float.parseFloat(Scr);
            }
        } else {
            Log.e("Cr Sum", "Not found");
        }

        Cursor cursor2 = db.rawQuery("Select CAST(Sum(Balance) as float) as Drtotal from "+AdatSoftConstants.TABLE_FIRM_DETAILS
                +AdatSoftData.Selected_Sno+" where ac_type ='Other' and Bal_cd='Dr'"+
                " order by Name", null);
        Log.d("test", "" + cursor2.getCount());
        Log.e("Dr count", ""+cursor2.getCount());
        if (cursor2.getCount() > 0) {
            cursor2.moveToFirst();
            if (String.valueOf(cursor2.getFloat(cursor2.getColumnIndex("Drtotal"))).equals(null)){
                Log.e("Dr Sum", "Rs 0.00");
            }else {
                Sdr=String.valueOf(cursor2.getFloat(cursor2.getColumnIndex("Drtotal")));
                Log.e("Dr Sum", String.valueOf(cursor2.getFloat(cursor2.getColumnIndex("Drtotal"))));
                SumDr = Float.parseFloat(Sdr);
            }
        } else {
            Log.e("Dr Sum", "Not found");
        }

        SumT = ((SumDr - SumCr)/100000);
        //SumT = df.setRoundingMode(SumT);
        St = String.format("%.2f",SumT);
        SumT = Float.parseFloat(St);
        if (SumT>0){
            Log.e("set text", "Rs "+SumT+" Debit");
            tvother.setText("Rs "+SumT+" Lakh Dr");
        } else if (SumT<0){
            Log.e("set text", "Rs "+Math.abs(SumT)+" Credit");
            tvother.setText("Rs "+Math.abs(SumT)+" Lakh Cr");
        } else if (SumT==0){
            Log.e("set text", "Rs "+"0.00");
            tvother.setText("Rs "+"0.00");
        }
    }

    private boolean dbvalue(){
        try{
            DatabaseHelper db1 = new DatabaseHelper(getBaseContext());
            SQLiteDatabase sql = db1.getWritableDatabase();
            Cursor cursor = sql.rawQuery("Select * from "+AdatSoftConstants.TABLE_FIRM_DETAILS+AdatSoftData.Selected_Sno, null);
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

    private void GetIntentExtras() {
        Bundle bundle = getIntent().getExtras();
        if( bundle != null){
            cust_no = (String) bundle.get("cust_no");
            shop_no = (String) bundle.get("shop_no");
            name_mar = (String) bundle.get("name_mar");
            address = (String) bundle.get("address");
            AMCExpireDt = (String) bundle.get("AMCExpireDt");
            mobile = (String) bundle.get("mobile");
            Module = (String) bundle.get("Module");
            if (Module.equals("Adat")){
                icondrawable = R.drawable.adatlogo;
            }else if (Module.equals("Petro")){
                icondrawable = R.drawable.petrosoft;
            }else if (Module.equals("Aims")){
                icondrawable = R.drawable.aimslogo;
            }

        }
    }

    private void initView() {
        parent = FirmDetailsActivity.this;

        tvcash = (TextView) findViewById(R.id.tvcashbal);
        tvbank = (TextView) findViewById(R.id.tvbankbal);
        tvsupp = (TextView) findViewById(R.id.tvsuppbal);
        tvcust = (TextView) findViewById(R.id.tvcustbal);
        tvtrans = (TextView) findViewById(R.id.tvtransbal);
        tvother = (TextView) findViewById(R.id.tvotherbal);

        llcash = (LinearLayout) findViewById(R.id.llcash);
        llbank = (LinearLayout) findViewById(R.id.llbank);
        llsupp = (LinearLayout) findViewById(R.id.llsupp);
        llcust = (LinearLayout) findViewById(R.id.llcust);
        lltrans = (LinearLayout) findViewById(R.id.lltrans);
        llother = (LinearLayout) findViewById(R.id.llother);

        db1 = new DatabaseHelper(parent);

        ut = new utility();
    }

    private void setListner() {
        llcash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(parent, BalanceDetailsActivity.class);
                Bundle extras = new Bundle();
                extras.putString("category", "Cash");
                extras.putString("selected_shop_no",AdatSoftData.Selected_Sno );
                extras.putString("name_mar", name_mar);
                extras.putString("Module", Module);
                intent.putExtras(extras);
                startActivity(intent);
                //finish();
            }
        });
        llbank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(parent, BalanceDetailsActivity.class);
                Bundle extras = new Bundle();
                extras.putString("category", "Bank");
                extras.putString("selected_shop_no",AdatSoftData.Selected_Sno );
                extras.putString("name_mar", name_mar);
                extras.putString("Module", Module);
                intent.putExtras(extras);
                startActivity(intent);
                //finish();
            }
        });
        llsupp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(parent, BalanceDetailsActivity.class);
                Bundle extras = new Bundle();
                extras.putString("category", "Supp");
                extras.putString("selected_shop_no",AdatSoftData.Selected_Sno );
                extras.putString("name_mar", name_mar);
                extras.putString("Module", Module);
                intent.putExtras(extras);
                startActivity(intent);
                //finish();
            }
        });
        llcust.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(parent, BalanceDetailsActivity.class);
                Bundle extras = new Bundle();
                extras.putString("category", "Cust");
                extras.putString("selected_shop_no",AdatSoftData.Selected_Sno );
                extras.putString("name_mar", name_mar);
                extras.putString("Module", Module);
                intent.putExtras(extras);
                startActivity(intent);
                //finish();
            }
        });
        lltrans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(parent, BalanceDetailsActivity.class);
                Bundle extras = new Bundle();
                extras.putString("category", "Hund");
                extras.putString("selected_shop_no",AdatSoftData.Selected_Sno );
                extras.putString("name_mar", name_mar);
                extras.putString("Module", Module);
                intent.putExtras(extras);
                startActivity(intent);
                //finish();
            }
        });
        llother.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(parent, BalanceDetailsActivity.class);
                Bundle extras = new Bundle();
                extras.putString("category", "Other");
                extras.putString("selected_shop_no",AdatSoftData.Selected_Sno );
                extras.putString("name_mar", name_mar);
                extras.putString("Module", Module);
                intent.putExtras(extras);
                startActivity(intent);
                //finish();
            }
        });
    }

    public class FirmDetailAPI extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(parent);
            //progressDialog.setMessage("Sync Setup Master");
            progressDialog.setMessage("Sync Firm Details");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }


        @Override
        protected String doInBackground(String... params) {

            String urlString = AdatSoftData.URL + AdatSoftData.METHOD_GET_DATA_COUNT + "?sessionId=" + AdatSoftData.SESSION_ID
                    + "&handler=" + AdatSoftData.HANDLE + "&data=ACBALANCES&cust_no=" + AdatSoftData.Lno +"&shop_no=" + AdatSoftData.Selected_Sno;
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
                Toast.makeText(getBaseContext(), "error in ACBalance", Toast.LENGTH_LONG).show();
            } else if (responsemsg.contains("SessionExpired")) {
                new StartSession(parent, new CallbackInterface() {

                    @Override
                    public void callMethod() {
                        new FirmDetailAPI().execute();
                    }
                });
            } else {
                if (Integer.parseInt(responsemsg) > 0) {

                    SQLiteDatabase db = db1.getWritableDatabase();
                    db.execSQL("DROP TABLE IF EXISTS "
                            + AdatSoftConstants.TABLE_FIRM_DETAILS+AdatSoftData.Selected_Sno);
                    db.execSQL(AdatSoftConstants.CREATE_TABLE_FIRM_DETAILS);
                }

                if (NetworkUtils.isNetworkAvailable(parent)) {
                    if ((AdatSoftData.SESSION_ID != null)
                            && (AdatSoftData.HANDLE != null)) {
                        new FirmDetailAPI_data().execute(responsemsg);
                    } else {
                        new StartSession(parent, new CallbackInterface() {

                            @Override
                            public void callMethod() {
                                new FirmDetailAPI_data().execute(responsemsg);
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

    public class FirmDetailAPI_data extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(parent);
            progressDialog.setMessage("Sync Firm Details");
            progressDialog.setCancelable(false);
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

                            db1.add_Firm_Details(jsonArray.getJSONObject(i1).getString(
                                    "Acno"), jsonArray.getJSONObject(i1).getString(
                                    "Name"), jsonArray.getJSONObject(i1).getString(
                                    "City"), jsonArray.getJSONObject(i1).getString(
                                    "mobile"), jsonArray.getJSONObject(i1).getString(
                                    "ac_type"), jsonArray.getJSONObject(i1).getString(
                                    "Balance"),jsonArray.getJSONObject(i1).getString(
                                    "Bal_cd"), jsonArray.getJSONObject(i1).getString(
                                    "updatedate"));
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
                        new FirmDetailAPI().execute();
                    }
                });
            } else {

                new AgingBalanceAPI().execute();
                if(dbvalue()) {
                    getDataFromDataBase();
                }
            }
        }



    }


    public class AgingBalanceAPI extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(parent);
            //progressDialog.setMessage("Sync Setup Master");
            progressDialog.setMessage("Sync Aging Balances");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }


        @Override
        protected String doInBackground(String... params) {

            String urlString = AdatSoftData.URL + AdatSoftData.METHOD_GET_DATA_COUNT + "?sessionId=" + AdatSoftData.SESSION_ID
                    + "&handler=" + AdatSoftData.HANDLE + "&data=BALANCE&cust_no=" + AdatSoftData.Lno +"&shop_no=" + AdatSoftData.Selected_Sno;
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
                Toast.makeText(getBaseContext(), "error in Aging Balance", Toast.LENGTH_LONG).show();
            } else if (responsemsg.contains("SessionExpired")) {
                new StartSession(parent, new CallbackInterface() {

                    @Override
                    public void callMethod() {
                        new AgingBalanceAPI().execute();
                    }
                });
            } else {
                if (Integer.parseInt(responsemsg) > 0) {

                    SQLiteDatabase db = db1.getWritableDatabase();
                    db.execSQL("DROP TABLE IF EXISTS "
                            + AdatSoftConstants.TABLE_AGING_BALANCE+AdatSoftData.Selected_Sno);
                    db.execSQL(AdatSoftConstants.CREATE_TABLE_AGING_BALANCE);
                }

                if (NetworkUtils.isNetworkAvailable(parent)) {
                    if ((AdatSoftData.SESSION_ID != null)
                            && (AdatSoftData.HANDLE != null)) {
                        new AgingBalanceAPI_data().execute(responsemsg);
                    } else {
                        new StartSession(parent, new CallbackInterface() {

                            @Override
                            public void callMethod() {
                                new AgingBalanceAPI_data().execute(responsemsg);
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

    public class AgingBalanceAPI_data extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(parent);
            progressDialog.setMessage("Sync Aging Balances");
            progressDialog.setCancelable(false);
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

                            db1.add_AgingBalance(jsonArray.getJSONObject(i1).getString(
                                    "acno"), jsonArray.getJSONObject(i1).getString(
                                    "Bal_0_15"), jsonArray.getJSONObject(i1).getString(
                                    "Bal_16_30"), jsonArray.getJSONObject(i1).getString(
                                    "Bal_31_60"), jsonArray.getJSONObject(i1).getString(
                                    "Bal_61_90"),jsonArray.getJSONObject(i1).getString(
                                    "Bal_m_90"), jsonArray.getJSONObject(i1).getString(
                                    "Total_Bal"), jsonArray.getJSONObject(i1).getString(
                                    "Avg_Total"), jsonArray.getJSONObject(i1).getString(
                                    "Bal_Avg"),jsonArray.getJSONObject(i1).getString(
                                    "Bal_Uplod_Dt"));
                           // Log.e("inserted", "inserted "+i1);
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
                        new AgingBalanceAPI().execute();
                    }
                });
            } else {
                /*if(dbvalue()) {
                    getDataFromDataBase();
                }*/
                new LedgerBalanceAPI().execute();
            }
        }



    }

    public class LedgerBalanceAPI extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(parent);
            //progressDialog.setMessage("Sync Setup Master");
            progressDialog.setMessage("Sync Ledger Balances");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }


        @Override
        protected String doInBackground(String... params) {

            String urlString = AdatSoftData.URL + AdatSoftData.METHOD_GET_DATA_COUNT + "?sessionId=" + AdatSoftData.SESSION_ID
                    + "&handler=" + AdatSoftData.HANDLE + "&data=GLedg&cust_no=" + AdatSoftData.Lno +"&shop_no=" + AdatSoftData.Selected_Sno;
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
                Toast.makeText(getBaseContext(), "error in Ledger Balance", Toast.LENGTH_LONG).show();
            } else if (responsemsg.contains("SessionExpired")) {
                new StartSession(parent, new CallbackInterface() {

                    @Override
                    public void callMethod() {
                        new LedgerBalanceAPI().execute();
                    }
                });
            } else {
                if (Integer.parseInt(responsemsg) > 0) {

                    SQLiteDatabase db = db1.getWritableDatabase();
                    db.execSQL("DROP TABLE IF EXISTS "
                            + AdatSoftConstants.TABLE_LEDGER_BALANCE+AdatSoftData.Selected_Sno);
                    db.execSQL(AdatSoftConstants.CREATE_TABLE_LEDGER_BALANCE);
                }

                if (NetworkUtils.isNetworkAvailable(parent)) {
                    if ((AdatSoftData.SESSION_ID != null)
                            && (AdatSoftData.HANDLE != null)) {
                        new LedgerBalanceAPI_data().execute(responsemsg);
                    } else {
                        new StartSession(parent, new CallbackInterface() {

                            @Override
                            public void callMethod() {
                                new LedgerBalanceAPI_data().execute(responsemsg);
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

    public class LedgerBalanceAPI_data extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();/*
            progressDialog = new ProgressDialog(parent);
            progressDialog.setMessage("Sync Aging Balances");
            progressDialog.show();*/
            progressDialog = new ProgressDialog(parent);
            //progressDialog.setMessage("Sync Setup Master");
            progressDialog.setMessage("Sync Ledger Balances");
            progressDialog.setCancelable(false);
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

                            db1.add_LedgerBalance(jsonArray.getJSONObject(i1).getString(
                                    "acno"), getDateinFormat(jsonArray.getJSONObject(i1).getString(
                                    "date")), jsonArray.getJSONObject(i1).getString(
                                    "Amount"), jsonArray.getJSONObject(i1).getString(
                                    "CD"), jsonArray.getJSONObject(i1).getString(
                                    "narr"));
                            // Log.e("inserted", "inserted "+i1);
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
                        new LedgerBalanceAPI().execute();
                    }
                });
            } else {
                /*if(dbvalue()) {
                    getDataFromDataBase();
                }*/
                new ItemMasterAPI().execute();
            }
        }



    }

    public class ItemMasterAPI extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(parent);
            //progressDialog.setMessage("Sync Setup Master");
            progressDialog.setMessage("Sync Item Master");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }


        @Override
        protected String doInBackground(String... params) {

            String urlString = AdatSoftData.URL + AdatSoftData.METHOD_GET_DATA_COUNT + "?sessionId=" + AdatSoftData.SESSION_ID
                    + "&handler=" + AdatSoftData.HANDLE + "&data=item&cust_no=" + AdatSoftData.Lno +"&shop_no=" + AdatSoftData.Selected_Sno;
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
                Toast.makeText(getBaseContext(), "error in Ledger Balance", Toast.LENGTH_LONG).show();
            } else if (responsemsg.contains("SessionExpired")) {
                new StartSession(parent, new CallbackInterface() {

                    @Override
                    public void callMethod() {
                        new ItemMasterAPI().execute();
                    }
                });
            } else {
                if (Integer.parseInt(responsemsg) > 0) {

                    SQLiteDatabase db = db1.getWritableDatabase();
                    db.execSQL("DROP TABLE IF EXISTS "
                            + AdatSoftConstants.TABLE_ITEM_MASTER+AdatSoftData.Selected_Sno);
                    db.execSQL(AdatSoftConstants.CREATE_TABLE_ITEM_MASTER);
                }

                if (NetworkUtils.isNetworkAvailable(parent)) {
                    if ((AdatSoftData.SESSION_ID != null)
                            && (AdatSoftData.HANDLE != null)) {
                        new ItemMasterAPI_data().execute(responsemsg);
                    } else {
                        new StartSession(parent, new CallbackInterface() {

                            @Override
                            public void callMethod() {
                                new ItemMasterAPI_data().execute(responsemsg);
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

    public class ItemMasterAPI_data extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();/*
            progressDialog = new ProgressDialog(parent);
            progressDialog.setMessage("Sync Aging Balances");
            progressDialog.show();*/
            progressDialog = new ProgressDialog(parent);
            //progressDialog.setMessage("Sync Setup Master");
            progressDialog.setMessage("Sync Item Master");
            progressDialog.setCancelable(false);
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

                            db1.add_ItemMaster(jsonArray.getJSONObject(i1).getString(
                                    "item_code"), getDateinFormat(jsonArray.getJSONObject(i1).getString(
                                    "item_desc")), jsonArray.getJSONObject(i1).getString(
                                    "ratefactor"), jsonArray.getJSONObject(i1).getString(
                                    "Qty_Exp"), jsonArray.getJSONObject(i1).getString(
                                    "Wt_Exp"), jsonArray.getJSONObject(i1).getString(
                                    "Amt_Exp"));
                            // Log.e("inserted", "inserted "+i1);
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
                        new ItemMasterAPI().execute();
                    }
                });
            } else {
                /*if(dbvalue()) {
                    getDataFromDataBase();
                }*/
            }
        }



    }

    private String getDateinFormat(String amcExpireDt) {
        String result= null;
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa");
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy/MM/dd  HH:mm:ss");
        try {
            Date date2 = dateFormat1.parse(amcExpireDt);
            result = dateFormat2.format(date2);
        }catch( Exception e){
            e.printStackTrace();
        }
        return result;
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
                        new FirmDetailAPI().execute();
                    } else {
                        new StartSession(parent, new CallbackInterface() {

                            @Override
                            public void callMethod() {
                                new FirmDetailAPI().execute();
                            }
                        });
                    }
                } else {
                    Toast.makeText(parent, "No internet..", Toast.LENGTH_LONG).show();
                    // callSnackbar();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        // your code.
        //Intent intent = new Intent(parent, MainActivity.class);
        Intent intent = new Intent(parent, Main2Activity.class);
        startActivity(intent);
        finish();
    }
}
