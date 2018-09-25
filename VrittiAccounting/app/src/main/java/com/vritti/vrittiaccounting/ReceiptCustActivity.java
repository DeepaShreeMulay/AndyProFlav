package com.vritti.vrittiaccounting;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.vritti.vrittiaccounting.data.AdatSoftConstants;
import com.vritti.vrittiaccounting.data.AdatSoftData;
import com.vritti.vrittiaccounting.data.utility;
import com.vritti.vrittiaccounting.database.DatabaseHelper;
import com.vritti.vrittiaccounting.services.BalanceDetailsBean;

import java.util.ArrayList;

public class ReceiptCustActivity extends AppCompatActivity {
    int icondrawable;
    String name_mar,category,selected_shop_no,Module,name_mar_title;

    utility ut;
    Context parent;
    TextView tvnodata;
    DatabaseHelper db1;
    ListView listview_balance_list;
    BalanceDetailsBean balanceDetailsBean;
    public static ArrayList<BalanceDetailsBean> balanceArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GetIntentExtras();
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //getSupportActionBar().setIcon(icondrawable);
        getSupportActionBar().setTitle("Select Customer");
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
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ReceiptCustActivity.this, ReceiptMainActivity.class);
                Bundle extras = new Bundle();
                //AdatSoftData.Selected_Sno = balanceArrayList.get(position).getShop_no();

                extras.putString("category", category);
                extras.putString("Module", Module);
                extras.putString("name_mar_title",name_mar_title);
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
                finish();
            }
        });
    }

    private void getDataFromDataBase() {
        // TODO Auto-generated method stub
        balanceArrayList.clear();

        DatabaseHelper db1 = new DatabaseHelper(parent);
        SQLiteDatabase db = db1.getWritableDatabase();

        Cursor cursor1 = db.rawQuery("Select * from "+AdatSoftConstants.TABLE_FIRM_DETAILS
                +AdatSoftData.Selected_Sno+" where ac_type ='"+category+"' and Name <> ''"+
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
        }
    }

    private void initView() {
        parent = ReceiptCustActivity.this;
        ut = new utility();
        balanceArrayList = new ArrayList<BalanceDetailsBean>();
        db1 = new DatabaseHelper(parent);
        tvnodata = (TextView) findViewById(R.id.tvnodata);
        listview_balance_list = (ListView) findViewById(R.id.listview_firm_list);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.searchmenu, menu);
        View m = menu.findItem(R.id.search).getActionView();
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
                String selectQuery =  "Select * from "+ AdatSoftConstants.TABLE_FIRM_DETAILS
                        + AdatSoftData.Selected_Sno+" where ac_type ='"+category+"' "+
                        " and Name LIKE  '%" +s+ "%' "+" order by Name";

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
                        " and Name LIKE  '%" +s+ "%' "+" order by Name";

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
            default:
                break;
        }


        return super.onOptionsItemSelected(item);
    }
}
