package com.vritti.vrittiaccounting;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.LinearLayout;
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
import com.vritti.vrittiaccounting.services.BalanceDetailsBean;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by Admin-3 on 7/20/2017.
 */

public class BalanceDetailsActivity extends AppCompatActivity {
    int icondrawable;
    String name_mar,category,selected_shop_no,Module,name_mar_title;
    LinearLayout llrate1,llrate2, llrate3, llrate4,llrate5,llrate;
    android.app.AlertDialog alertDialog;

    utility ut;
    Context parent;    
    TextView tvnodata;
    DatabaseHelper db1;
    ListView listview_balance_list;
    BalanceDetailsBean balanceDetailsBean;
    ProgressDialog progressDialog;
    String responsemsg = "";
    public static ArrayList<BalanceDetailsBean> balanceArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GetIntentExtras();
        /*getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(icondrawable);*/
        getSupportActionBar().setTitle("  "+name_mar);
        Bitmap bitmap= BitmapFactory.decodeResource(getResources(),icondrawable);
        BitmapDrawable d = new BitmapDrawable(getResources(),
                                Bitmap.createScaledBitmap(bitmap, 120, 120, true));
        getSupportActionBar().setIcon(d);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        initView();
        if (dbvalue()) {
            getDataFromDataBase();
        } else{
            tvnodata.setVisibility(View.VISIBLE);
            listview_balance_list.setVisibility(View.GONE);
        }
        setListner();
    }

    private void setListner() {
        listview_balance_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,final int position, long id) {
                if (name_mar.equals("Cash")){   //callforCashBal();
                }else if (name_mar.equals("Bank")){}
                else if (name_mar.equals("Supplier")){
                   /* Animation myAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slow_blink);
                    view.startAnimation(myAnim);*/
                    Animation animation1 = new AlphaAnimation(0.3f, 1.0f);
                    animation1.setDuration(1000);
                    view.startAnimation(animation1);
                    view.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(BalanceDetailsActivity.this,BalanceLedgerActivity.class);
                            Bundle extras = new Bundle();
                            extras.putString("name_mar_title", balanceArrayList.get(position).getName());
                            extras.putString("Acno", balanceArrayList.get(position).getAcno());
                            intent.putExtras(extras);
                            startActivity(intent);
                            //finish();
                        }
                    }, 800);
                } else {
                    /*Animation myAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slow_blink);
                    view.startAnimation(myAnim);*/
                    Animation animation1 = new AlphaAnimation(0.3f, 1.0f);
                    animation1.setDuration(1000);
                    view.startAnimation(animation1);
                    view.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(BalanceDetailsActivity.this, ContactDetailsActivity.class);
                            Bundle extras = new Bundle();

                            extras.putString("category", category);
                            extras.putString("Module", Module);
                            extras.putString("name_mar_title", name_mar_title);
                            extras.putString("Acno", balanceArrayList.get(position).getAcno());
                            extras.putString("Name", balanceArrayList.get(position).getName());
                            extras.putString("City", balanceArrayList.get(position).getCity());
                            extras.putString("ac_type", balanceArrayList.get(position).getAc_type());
                            extras.putString("Balance", balanceArrayList.get(position).getBalance());
                            extras.putString("mobile", balanceArrayList.get(position).getMobile());
                            extras.putString("Bal_cd", balanceArrayList.get(position).getBal_cd());
                            extras.putString("updatedate", balanceArrayList.get(position).getUpdatedate());
                            intent.putExtras(extras);
                            startActivity(intent);
                            //finish();
                        }
                    }, 800);
                }
            }
        });
    }

    /*private void callforCashBal() {
        if ((AdatSoftData.SESSION_ID != null) && (AdatSoftData.HANDLE != null)) {
                        new CashBalanceAPI().execute();
                    } else {
                        new StartSession(parent, new CallbackInterface() {
                            @Override
                            public void callMethod() {
                                new CashBalanceAPI().execute();
                            }
                        });
        }
    }*/

    private void getDataFromDataBase() {
        // TODO Auto-generated method stub
        balanceArrayList.clear();

        DatabaseHelper db1 = new DatabaseHelper(parent);
        SQLiteDatabase db = db1.getWritableDatabase();

        Cursor cursor1 = db.rawQuery("Select * from "+AdatSoftConstants.TABLE_FIRM_DETAILS
                        +AdatSoftData.Selected_Sno+" where ac_type ='"+category+"' and Name <> '' and Balance <> '0.00'"+
                " order by Name", null);
        Log.d("test", "" + cursor1.getCount());
        if (cursor1.getCount() > 0) {
            cursor1.moveToFirst();
            try {
                do {
                    balanceDetailsBean = new BalanceDetailsBean();
                    balanceDetailsBean.setAcno(cursor1.getString(cursor1.getColumnIndex("Acno")));
                    balanceDetailsBean.setName(cursor1.getString(cursor1.getColumnIndex("Name")));
                    balanceDetailsBean.setCity(cursor1.getString(cursor1.getColumnIndex("City")));
                    balanceDetailsBean.setAc_type(cursor1.getString(cursor1.getColumnIndex("ac_type")));
                    balanceDetailsBean.setBalance(cursor1.getString(cursor1.getColumnIndex("Balance")));
                    balanceDetailsBean.setMobile(cursor1.getString(cursor1.getColumnIndex("mobile")));
                    balanceDetailsBean.setBal_cd(cursor1.getString(cursor1.getColumnIndex("Bal_cd")));
                    balanceDetailsBean.setUpdatedate(cursor1.getString(cursor1.getColumnIndex("updatedate")));
                    balanceArrayList.add(balanceDetailsBean);
                } while (cursor1.moveToNext());
            } finally {
                cursor1.close();
            }
        }
        BalanceListAdapter balanceListAdapter = new BalanceListAdapter(parent, balanceArrayList);
        listview_balance_list.setAdapter(balanceListAdapter);
    }

    private void getDataFromDataBaseSorting(String Rate) {
        // TODO Auto-generated method stub
        String RateL="0.00" , RateU="0.00";
        if(Rate.equalsIgnoreCase("rate5")) {
            RateU = "5.00";  RateL = "4.00";
        }else if(Rate.equalsIgnoreCase("rate4")) {
            RateU = "4.00"; RateL = "3.00";
        }else if(Rate.equalsIgnoreCase("rate3")) {
            RateU = "3.00"; RateL = "2.00";
        }else if(Rate.equalsIgnoreCase("rate2")) {
            RateU = "2.00"; RateL = "1.00";
        }else if(Rate.equalsIgnoreCase("rate1")) {
            RateU = "1.00"; RateL = "0.00";
        }

        balanceArrayList.clear();

        DatabaseHelper db1 = new DatabaseHelper(parent);
        SQLiteDatabase db = db1.getWritableDatabase();

        Cursor cursor1 = db.rawQuery("Select * from "+AdatSoftConstants.TABLE_FIRM_DETAILS
                +AdatSoftData.Selected_Sno+" where ac_type ='"+category+"' and Name <> '' and Balance <> '0.00'"+
                " order by Name", null);
        Log.d("test", "" + cursor1.getCount());
        if (cursor1.getCount() > 0) {
            cursor1.moveToFirst();
            try {
                do {

                    String Acno = cursor1.getString(cursor1.getColumnIndex("Acno"));

                    Cursor cursor = db.rawQuery("Select * from "+AdatSoftConstants.TABLE_AGING_BALANCE+AdatSoftData.Selected_Sno+
                            " where acno = '"+Acno+"' and  Bal_Avg <= '"+RateU+"' and  Bal_Avg > '"+RateL+"'", null);
                    cursor.moveToFirst();
                    if (cursor != null && cursor.getCount()>0) {
                        String BalAvg = cursor.getString(cursor.getColumnIndex("Bal_Avg"));
                        balanceDetailsBean = new BalanceDetailsBean();
                        balanceDetailsBean.setAcno(cursor1.getString(cursor1.getColumnIndex("Acno")));
                        balanceDetailsBean.setName(cursor1.getString(cursor1.getColumnIndex("Name")));
                        balanceDetailsBean.setCity(cursor1.getString(cursor1.getColumnIndex("City")));
                        balanceDetailsBean.setAc_type(cursor1.getString(cursor1.getColumnIndex("ac_type")));
                        balanceDetailsBean.setBalance(cursor1.getString(cursor1.getColumnIndex("Balance")));
                        balanceDetailsBean.setMobile(cursor1.getString(cursor1.getColumnIndex("mobile")));
                        balanceDetailsBean.setBal_cd(cursor1.getString(cursor1.getColumnIndex("Bal_cd")));
                        balanceDetailsBean.setUpdatedate(cursor1.getString(cursor1.getColumnIndex("updatedate")));
                        balanceArrayList.add(balanceDetailsBean);
                    }
                } while (cursor1.moveToNext());
            } finally {
                cursor1.close();
            }
        }
        BalanceListAdapter balanceListAdapter = new BalanceListAdapter(parent, balanceArrayList);
        listview_balance_list.setAdapter(balanceListAdapter);
    }

    private boolean dbvalue(){
        try{
            DatabaseHelper db1 = new DatabaseHelper(getBaseContext());
            SQLiteDatabase sql = db1.getWritableDatabase();
            Cursor cursor = sql.rawQuery("Select * from "+AdatSoftConstants.TABLE_FIRM_DETAILS
                    +AdatSoftData.Selected_Sno+" where ac_type ='"+category+"'", null);
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
            name_mar_title = (String) bundle.get("name_mar");
            category = (String) bundle.get("category");
            selected_shop_no = (String) bundle.get("selected_shop_no");
            Module = (String) bundle.get("Module");
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
        parent = BalanceDetailsActivity.this;
        ut = new utility();
        balanceArrayList = new ArrayList<BalanceDetailsBean>();
        db1 = new DatabaseHelper(parent);
        tvnodata = (TextView) findViewById(R.id.tvnodata);
        listview_balance_list = (ListView) findViewById(R.id.listview_firm_list);
    }

    private void ConfirmDialog() {
        LayoutInflater inflater = LayoutInflater.from(parent);
        View subView1 = inflater.inflate(R.layout.confirm_dialoglist, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(parent);
        builder.setView(subView1);

        llrate = (LinearLayout) subView1.findViewById(R.id.llrate);
        llrate1 = (LinearLayout) subView1.findViewById(R.id.llrate1);
        llrate2 = (LinearLayout) subView1.findViewById(R.id.llrate2);
        llrate3 = (LinearLayout) subView1.findViewById(R.id.llrate3);
        llrate4 = (LinearLayout) subView1.findViewById(R.id.llrate4);
        llrate5 = (LinearLayout) subView1.findViewById(R.id.llrate5);

        llrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDataFromDataBase();
                alertDialog.dismiss();
            }
        });

        llrate1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDataFromDataBaseSorting("rate1");
                alertDialog.dismiss();
            }
        });
        llrate2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDataFromDataBaseSorting("rate2");
                alertDialog.dismiss();
            }
        });
        llrate3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDataFromDataBaseSorting("rate3");
                alertDialog.dismiss();
            }
        });
        llrate4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDataFromDataBaseSorting("rate4");
                alertDialog.dismiss();
            }
        });
        llrate5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDataFromDataBaseSorting("rate5");
                alertDialog.dismiss();
            }
        });
        alertDialog = builder.create();
        alertDialog.show();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.searchmenu, menu);
        View m = menu.findItem(R.id.search).getActionView();
       /* if (category.equals("Supp")){
            menu.findItem(R.id.ratefilter).setVisible(true);
        }else*/ if (category.equals("Cust")){
            menu.findItem(R.id.ratefilter).setVisible(true);
        }else if (category.equals("Hund")){
            menu.findItem(R.id.ratefilter).setVisible(true);
        }
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        SearchableInfo info = searchManager.getSearchableInfo(getComponentName());
        searchView.setSearchableInfo(info);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {
                Log.d("TAG", "onQueryTextSubmit ");
                balanceArrayList.clear();

                DatabaseHelper db1 = new DatabaseHelper(parent);
                SQLiteDatabase db = db1.getReadableDatabase();
                String selectQuery =  "Select * from "+AdatSoftConstants.TABLE_FIRM_DETAILS
                        +AdatSoftData.Selected_Sno+" where ac_type ='"+category+"' "+
                        "and Name <> '' and Balance <> '0.00'"+
                        " and Name LIKE  '% " +s+ "%' OR ac_type ='"+category+"' "+
                        "and Name <> '' and Balance <> '0.00'"+
                        " and Name LIKE  '" +s+ "%' " + " order by Name";


                Cursor cursor = db.rawQuery(selectQuery , null);
                if (cursor==null){
                    Toast.makeText(parent,"No records found!",Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(parent, cursor.getCount() + " records found!",Toast.LENGTH_LONG).show();
                }
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    try {
                        do {
                            balanceDetailsBean = new BalanceDetailsBean();
                            balanceDetailsBean.setAcno(cursor.getString(cursor.getColumnIndex("Acno")));
                            balanceDetailsBean.setName(cursor.getString(cursor.getColumnIndex("Name")));
                            balanceDetailsBean.setCity(cursor.getString(cursor.getColumnIndex("City")));
                            balanceDetailsBean.setAc_type(cursor.getString(cursor.getColumnIndex("ac_type")));
                            balanceDetailsBean.setBalance(cursor.getString(cursor.getColumnIndex("Balance")));
                            balanceDetailsBean.setMobile(cursor.getString(cursor.getColumnIndex("mobile")));
                            balanceDetailsBean.setBal_cd(cursor.getString(cursor.getColumnIndex("Bal_cd")));
                            balanceDetailsBean.setUpdatedate(cursor.getString(cursor.getColumnIndex("updatedate")));
                            balanceArrayList.add(balanceDetailsBean);
                        } while (cursor.moveToNext());
                    } finally {
                        cursor.close();
                    }
                }
                BalanceListAdapter balanceListAdapter = new BalanceListAdapter(parent, balanceArrayList);
                listview_balance_list.setAdapter(balanceListAdapter);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Log.d("TAG", "onQueryTextChange ");
                balanceArrayList.clear();

                DatabaseHelper db1 = new DatabaseHelper(parent);
                SQLiteDatabase db = db1.getReadableDatabase();
                // BuyerMaster buyerMaster = ;
                String selectQuery =  "Select * from "+AdatSoftConstants.TABLE_FIRM_DETAILS
                        +AdatSoftData.Selected_Sno+" where ac_type ='"+category+"' "+
                        "and Name <> '' and Balance <> '0.00'"+
                        " and Name LIKE  '% " +s+ "%' OR ac_type ='"+category+"' "+
                        "and Name <> '' and Balance <> '0.00'"+
                        " and Name LIKE  '" +s+ "%' " + " order by Name";

                Cursor cursor = db.rawQuery(selectQuery , null);
                //cursor=studentRepo.getStudentListByKeyword(s);
                if (cursor!=null && cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    try {
                        do {
                            balanceDetailsBean = new BalanceDetailsBean();
                            balanceDetailsBean.setAcno(cursor.getString(cursor.getColumnIndex("Acno")));
                            balanceDetailsBean.setName(cursor.getString(cursor.getColumnIndex("Name")));
                            balanceDetailsBean.setCity(cursor.getString(cursor.getColumnIndex("City")));
                            balanceDetailsBean.setAc_type(cursor.getString(cursor.getColumnIndex("ac_type")));
                            balanceDetailsBean.setBalance(cursor.getString(cursor.getColumnIndex("Balance")));
                            balanceDetailsBean.setMobile(cursor.getString(cursor.getColumnIndex("mobile")));
                            balanceDetailsBean.setBal_cd(cursor.getString(cursor.getColumnIndex("Bal_cd")));
                            balanceDetailsBean.setUpdatedate(cursor.getString(cursor.getColumnIndex("updatedate")));
                            balanceArrayList.add(balanceDetailsBean);

                        } while (cursor.moveToNext());
                    } finally {
                        cursor.close();
                    }
                }
                BalanceListAdapter balanceListAdapter = new BalanceListAdapter(parent, balanceArrayList);
                listview_balance_list.setAdapter(balanceListAdapter);
                return false;
            }

        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.search:
                break;
            case R.id.ratefilter:
                /*if (category.equals("Supp")){
                    ConfirmDialog();
                } else*/ if (category.equals("Cust")){
                    ConfirmDialog();
                }else if (category.equals("Hund")){
                    ConfirmDialog();
                }
               // ConfirmDialog();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


}
