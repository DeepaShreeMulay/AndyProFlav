package com.vritti.vrittiaccounting;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
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

public class Main2Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    String cust_no ;String shop_no ;String name_mar ;String address ;String mobile ;String AMCExpireDt; String Module;

    int icondrawable;
    ImageView ivcash;
    TextView tvcash, tvbank, tvsupp, tvcust, tvtrans, tvother;
    LinearLayout llcash, llbank, llsupp, llcust, lltrans, llother;
    Context parent;
    utility ut;
    ProgressDialog progressDialog;
    DatabaseHelper db1;
    String responsemsg = "";

    TextView txtusername, txtmobileno, txtownername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        GetIntentExtras();
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(AdatSoftData.MODULE_TITLE);

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

            drawer.setDrawerListener(toggle);
            toggle.syncState();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (AdatSoftData.MODULE.equals("PETRO")) {
            navigationView.setVisibility(View.GONE);
        }else{
            navigationView.setVisibility(View.VISIBLE);
            navigationView.setNavigationItemSelectedListener(this);
            View header = navigationView.getHeaderView(0);

            txtusername = (TextView) header.findViewById(R.id.txtusername);
            txtmobileno = (TextView) header.findViewById(R.id.txtmobileno);
            txtusername.setText("Firm : " + name_mar);
            txtmobileno.setText("Mobile : " + mobile);
        }
        initView();
        if (!dbvalue()){

            tvcash.setText("0.00");
            tvbank.setText("0.00");
            tvsupp.setText("0.00");
            tvcust.setText("0.00");
            tvtrans.setText("0.00");
            tvother.setText("0.00");

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
            Log.e("set text", SumT+" Debit");
            tvcash.setText(SumT+" Lakh");
            tvcash.setTextColor(getResources().getColor(R.color.colorDebit));
        } else if (SumT<0){
            Log.e("set text", Math.abs(SumT)+" Credit");
            tvcash.setText(/*Math.abs(*/SumT/*)*/+" Lakh");
            ivcash.setImageResource(R.drawable.cashnegative);
            tvcash.setTextColor(getResources().getColor(R.color.colorCredit));
        } else if (SumT==0){
            Log.e("set text", "0.00");
            tvcash.setText("0.00");
            tvcash.setTextColor(getResources().getColor(R.color.colorDebit));
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
            Log.e("set text", SumT+" Debit");
            tvbank.setText(SumT+" Lakh");
            tvbank.setTextColor(getResources().getColor(R.color.colorDebit));
        } else if (SumT<0){
            Log.e("set text", Math.abs(SumT)+" Credit");
            tvbank.setText(/*Math.abs(*/SumT/*)*/+" Lakh");
            tvbank.setTextColor(getResources().getColor(R.color.colorCredit));
        } else if (SumT==0){
            Log.e("set text", "0.00");
            tvbank.setText("0.00");
            tvbank.setTextColor(getResources().getColor(R.color.colorDebit));
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
            Log.e("set text", SumT+" Debit");
            tvsupp.setText("-"+SumT+" Lakh");
            tvsupp.setTextColor(getResources().getColor(R.color.colorCredit));
        } else if (SumT<0){
            Log.e("set text", Math.abs(SumT)+" Credit");
            tvsupp.setText(Math.abs(SumT)+" Lakh");
            tvsupp.setTextColor(getResources().getColor(R.color.colorDebit));
        } else if (SumT==0){
            Log.e("set text", "0.00");
            tvsupp.setText("0.00");
            tvsupp.setTextColor(getResources().getColor(R.color.colorDebit));
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
            Log.e("set text", SumT+" Debit");
            tvcust.setText(SumT+" Lakh");
            tvcust.setTextColor(getResources().getColor(R.color.colorDebit));
        } else if (SumT<0){
            Log.e("set text", Math.abs(SumT)+" Credit");
            tvcust.setText(/*Math.abs(*/SumT/*)*/+" Lakh");
            tvcust.setTextColor(getResources().getColor(R.color.colorCredit));
        } else if (SumT==0){
            Log.e("set text", "0.00");
            tvcust.setText("0.00");
            tvcust.setTextColor(getResources().getColor(R.color.colorDebit));
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
            Log.e("set text", SumT+" Debit");
            tvtrans.setText(SumT+" Lakh");
            tvtrans.setTextColor(getResources().getColor(R.color.colorDebit));
        } else if (SumT<0){
            Log.e("set text", Math.abs(SumT)+" Credit");
            tvtrans.setText(/*Math.abs(*/SumT/*)*/+" Lakh");
            tvtrans.setTextColor(getResources().getColor(R.color.colorCredit));
        } else if (SumT==0){
            Log.e("set text", "0.00");
            tvtrans.setText("0.00");
            tvtrans.setTextColor(getResources().getColor(R.color.colorDebit));
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
            Log.e("set text", SumT+" Debit");
            tvother.setText(SumT+" Lakh");
            tvother.setTextColor(getResources().getColor(R.color.colorDebit));
        } else if (SumT<0){
            Log.e("set text", Math.abs(SumT)+" Credit");
            tvother.setText(/*Math.abs(*/SumT/*)*/+" Lakh");
            tvother.setTextColor(getResources().getColor(R.color.colorCredit));
        } else if (SumT==0){
            Log.e("set text", "0.00");
            tvother.setText("0.00");
            tvother.setTextColor(getResources().getColor(R.color.colorDebit));
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
        parent = Main2Activity.this;

        ivcash = (ImageView) findViewById(R.id.ivcash);

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
                Animation myAnim = AnimationUtils.loadAnimation(parent, R.anim.clickfadeinfadeout);
                llcash.startAnimation(myAnim);
                llcash.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        /*Intent intent = new Intent(parent, BalanceDetailsActivity.class);
                        Bundle extras = new Bundle();
                        extras.putString("category", "Cash");
                        extras.putString("selected_shop_no",AdatSoftData.Selected_Sno );
                        extras.putString("name_mar", name_mar);
                        extras.putString("Module", Module);
                        intent.putExtras(extras);
                        startActivity(intent);*/

                        Intent intent = new Intent(parent,BalanceCashActivity.class);
                Bundle extras = new Bundle();
                extras.putString("name_mar_title", "Cash");
                //extras.putString("Acno", Acno);
                intent.putExtras(extras);
                startActivity(intent);
                        //finish();
                    }
                }, 200);
            }
        });
        llbank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation myAnim = AnimationUtils.loadAnimation(parent, R.anim.clickfadeinfadeout);
                llbank.startAnimation(myAnim);
                llbank.postDelayed(new Runnable() {
                    @Override
                    public void run() {
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
                }, 200);

            }
        });
        llsupp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation myAnim = AnimationUtils.loadAnimation(parent, R.anim.clickfadeinfadeout);
                llsupp.startAnimation(myAnim);
                llsupp.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(parent, BalanceDetailsActivity.class);
                        Bundle extras = new Bundle();
                        extras.putString("category", "Supp");
                        extras.putString("selected_shop_no",AdatSoftData.Selected_Sno );
                        extras.putString("name_mar", name_mar);
                        extras.putString("Module", Module);
                        intent.putExtras(extras);
                        startActivity(intent);
                    }
                }, 200);
            }
        });
        llcust.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation myAnim = AnimationUtils.loadAnimation(parent, R.anim.clickfadeinfadeout);
                llcust.startAnimation(myAnim);
                llcust.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(parent, BalanceDetailsActivity.class);
                        Bundle extras = new Bundle();
                        extras.putString("category", "Cust");
                        extras.putString("selected_shop_no",AdatSoftData.Selected_Sno );
                        extras.putString("name_mar", name_mar);
                        extras.putString("Module", Module);
                        intent.putExtras(extras);
                        startActivity(intent);
                    }
                }, 200);
            }
        });
        lltrans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation myAnim = AnimationUtils.loadAnimation(parent, R.anim.clickfadeinfadeout);
                lltrans.startAnimation(myAnim);
                lltrans.postDelayed(new Runnable() {
                    @Override
                    public void run() {
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
                }, 200);
            }
        });
        llother.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation myAnim = AnimationUtils.loadAnimation(parent, R.anim.clickfadeinfadeout);
                llother.startAnimation(myAnim);
                llother.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(parent, BalanceDetailsActivity.class);
                        Bundle extras = new Bundle();
                        extras.putString("category", "Other");
                        extras.putString("selected_shop_no",AdatSoftData.Selected_Sno );
                        extras.putString("name_mar", name_mar);
                        extras.putString("Module", Module);
                        intent.putExtras(extras);
                        startActivity(intent);
                    }
                }, 200);
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
                                    "item_code"), jsonArray.getJSONObject(i1).getString(
                                    "item_desc"), jsonArray.getJSONObject(i1).getString(
                                    "ratefactor"), jsonArray.getJSONObject(i1).getString(
                                    "Qty_Exp"), jsonArray.getJSONObject(i1).getString(
                                    "Wt_Exp"), jsonArray.getJSONObject(i1).getString(
                                    "Amt_Exp"));
                             Log.e("inserted", "inserted "+i1);
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
                new CashBalanceAPI().execute();
                /*if(dbvalue()) {
                    getDataFromDataBase();
                }*/
            }
        }



    }

    public class CashBalanceAPI extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(parent);
            //progressDialog.setMessage("Sync Setup Master");
            progressDialog.setMessage("Sync Cash Balance");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
        @Override
        protected String doInBackground(String... params) {
            String urlString = AdatSoftData.URL + AdatSoftData.METHOD_GET_DATA_COUNT + "?sessionId=" + AdatSoftData.SESSION_ID
                    + "&handler=" + AdatSoftData.HANDLE + "&data=CASHBAL&cust_no=" + AdatSoftData.Lno +"&shop_no=" + AdatSoftData.Selected_Sno;
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
                        new CashBalanceAPI().execute();
                    }
                });
            } else {
                if (Integer.parseInt(responsemsg) > 0) {
                    SQLiteDatabase db = db1.getWritableDatabase();
                    db.execSQL("DROP TABLE IF EXISTS "
                            + AdatSoftConstants.TABLE_CASH_BALANCE+AdatSoftData.Selected_Sno);
                    db.execSQL(AdatSoftConstants.CREATE_TABLE_CASH_BALANCE);
                }
                if (NetworkUtils.isNetworkAvailable(parent)) {
                    if ((AdatSoftData.SESSION_ID != null)
                            && (AdatSoftData.HANDLE != null)) {
                        new CashBalanceAPI_data().execute(responsemsg);
                    } else {
                        new StartSession(parent, new CallbackInterface() {

                            @Override
                            public void callMethod() {
                                new CashBalanceAPI_data().execute(responsemsg);
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

    public class CashBalanceAPI_data extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(parent);
            progressDialog.setMessage("Sync Cash Balance");
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

                            db1.add_Cash_Balance(getDateinFormat(jsonArray.getJSONObject(i1).getString(
                                    "Date")), jsonArray.getJSONObject(i1).getString(
                                    "Opening"), jsonArray.getJSONObject(i1).getString(
                                    "Receipt"), jsonArray.getJSONObject(i1).getString(
                                    "Payment"), jsonArray.getJSONObject(i1).getString(
                                    "Bal"));
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
                        new CashBalanceAPI().execute();
                    }
                });
            } else {

                /*new AgingBalanceAPI().execute();
                if(dbvalue()) {
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
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            Intent intent = new Intent(parent, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_cust_receipt) {

            Intent intent = new Intent(parent, ReceiptCustActivity.class);
            Bundle extras = new Bundle();
            extras.putString("category", "Cust");
            extras.putString("selected_shop_no",AdatSoftData.Selected_Sno );
            extras.putString("name_mar", name_mar);
            extras.putString("Module", Module);
            intent.putExtras(extras);
            startActivity(intent);

        } else if (id == R.id.nav_sale_bill) {

            Intent intent = new Intent(parent, SaleBillCustActivity.class);
            Bundle extras = new Bundle();
            extras.putString("category", "Cust");
            extras.putString("selected_shop_no",AdatSoftData.Selected_Sno );
            extras.putString("name_mar", name_mar);
            extras.putString("Module", Module);
            intent.putExtras(extras);
            startActivity(intent);

        } else if (id == R.id.nav_sendtoserver) {

            Intent intent = new Intent(parent, SavetoserverActivity.class);
            Bundle extras = new Bundle();
            extras.putString("category", "Cust");
            extras.putString("selected_shop_no",AdatSoftData.Selected_Sno );
            extras.putString("name_mar", name_mar);
            extras.putString("Module", Module);
            intent.putExtras(extras);
            startActivity(intent);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
