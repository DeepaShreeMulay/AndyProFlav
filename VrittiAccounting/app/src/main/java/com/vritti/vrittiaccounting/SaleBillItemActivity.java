package com.vritti.vrittiaccounting;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.vritti.vrittiaccounting.services.ItemDetailsBean;
import com.zj.btsdk.BluetoothService;

import java.util.ArrayList;

public class SaleBillItemActivity extends AppCompatActivity {
    String name_mar,category,Name,City,ac_type,Balance,mobile,Bal_cd,updatedate,Acno,name_mar_title;
    String selected_shop_no,Module;

    utility ut;
    Context parent;
    TextView tvnodata;
    DatabaseHelper db1;
    ListView listview_balance_list;
    ItemDetailsBean itemDetailsBean;
    public static ArrayList<ItemDetailsBean> itemDetailsBeanArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GetIntentExtras();
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //getSupportActionBar().setIcon(icondrawable);
        getSupportActionBar().setTitle("Select Item");
        //getSupportActionBar().setTitle(Name);
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
                Intent intent = new Intent(SaleBillItemActivity.this, SaleBillMainActivity.class);
                Bundle extras = new Bundle();

                extras.putString("category", category);
                extras.putString("Module", Module);
                extras.putString("name_mar_title",name_mar_title);
                extras.putString("Acno", Acno);
                extras.putString("Name", Name);
                extras.putString("City", City);
                extras.putString("ac_type", ac_type);
                extras.putString("Balance", Balance);
                extras.putString("mobile", mobile);
                extras.putString("Bal_cd", Bal_cd);
                extras.putString("updatedate", updatedate);

                extras.putString("Item_code", itemDetailsBeanArrayList.get(position).getItem_code());
                extras.putString("Item_desc", itemDetailsBeanArrayList.get(position).getItem_desc());
                extras.putString("Ratefactor", itemDetailsBeanArrayList.get(position).getRatefactor());
                extras.putString("Qty_Exp", itemDetailsBeanArrayList.get(position).getQty_Exp());
                extras.putString("Wt_Exp", itemDetailsBeanArrayList.get(position).getWt_Exp());
                extras.putString("Amt_Exp", itemDetailsBeanArrayList.get(position).getAmt_Exp());

                intent.putExtras(extras);
                startActivity(intent);
                finish();
            }
        });
    }

    private void getDataFromDataBase() {
        // TODO Auto-generated method stub
        itemDetailsBeanArrayList.clear();

        DatabaseHelper db1 = new DatabaseHelper(parent);
        SQLiteDatabase db = db1.getWritableDatabase();

        Cursor cursor1 = db.rawQuery("Select * from "+AdatSoftConstants.TABLE_ITEM_MASTER
                +AdatSoftData.Selected_Sno+//" where ac_type ='"+category+"' and Name <> ''"+
                " order by item_desc", null);
        Log.d("test", "" + cursor1.getCount());
        if (cursor1.getCount() > 0) {
            cursor1.moveToFirst();
            try {
                do {
                    itemDetailsBean = new ItemDetailsBean();
                    itemDetailsBean.setItem_code(cursor1.getString(cursor1.getColumnIndex("item_code")));
                    itemDetailsBean.setItem_desc(cursor1.getString(cursor1.getColumnIndex("item_desc")));
                    itemDetailsBean.setRatefactor(cursor1.getString(cursor1.getColumnIndex("ratefactor")));
                    itemDetailsBean.setQty_Exp(cursor1.getString(cursor1.getColumnIndex("Qty_Exp")));
                    itemDetailsBean.setWt_Exp(cursor1.getString(cursor1.getColumnIndex("Wt_Exp")));
                    itemDetailsBean.setAmt_Exp(cursor1.getString(cursor1.getColumnIndex("Amt_Exp")));
                    itemDetailsBeanArrayList.add(itemDetailsBean);
                } while (cursor1.moveToNext());
            } finally {
                cursor1.close();
            }
        }
        ItemListAdapter itemListAdapter = new ItemListAdapter(parent, itemDetailsBeanArrayList);
        listview_balance_list.setAdapter(itemListAdapter);
    }

    private boolean dbvalue(){
        try{
            DatabaseHelper db1 = new DatabaseHelper(getBaseContext());
            SQLiteDatabase sql = db1.getWritableDatabase();
            Cursor cursor = sql.rawQuery("Select * from "+ AdatSoftConstants.TABLE_ITEM_MASTER
                    + AdatSoftData.Selected_Sno, null);
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
        if (bundle != null) {
            name_mar_title = (String) bundle.get("name_mar_title");
            category = (String) bundle.get("category");
            Module = (String) bundle.get("Module");
            Acno = (String) bundle.get("Acno");
            Name = (String) bundle.get("Name");
            City = (String) bundle.get("City");
            ac_type = (String) bundle.get("ac_type");
            Balance = (String) bundle.get("Balance");
            mobile = (String) bundle.get("mobile");
            Bal_cd = (String) bundle.get("Bal_cd");
            updatedate = (String) bundle.get("updatedate");
        }
    }

    private void initView() {
        parent = SaleBillItemActivity.this;
        ut = new utility();
        itemDetailsBeanArrayList = new ArrayList<ItemDetailsBean>();
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
                itemDetailsBeanArrayList.clear();

                DatabaseHelper db1 = new DatabaseHelper(parent);
                SQLiteDatabase db = db1.getReadableDatabase();
                String selectQuery =  "Select * from "+AdatSoftConstants.TABLE_ITEM_MASTER
                        +AdatSoftData.Selected_Sno+" where item_desc LIKE  '%" +s+ "%' "+" order by item_desc";

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
                            itemDetailsBean = new ItemDetailsBean();
                            itemDetailsBean.setItem_code(cursor.getString(cursor.getColumnIndex("item_code")));
                            itemDetailsBean.setItem_desc(cursor.getString(cursor.getColumnIndex("item_desc")));
                            itemDetailsBean.setRatefactor(cursor.getString(cursor.getColumnIndex("ratefactor")));
                            itemDetailsBean.setQty_Exp(cursor.getString(cursor.getColumnIndex("Qty_Exp")));
                            itemDetailsBean.setWt_Exp(cursor.getString(cursor.getColumnIndex("Wt_Exp")));
                            itemDetailsBean.setAmt_Exp(cursor.getString(cursor.getColumnIndex("Amt_Exp")));
                            itemDetailsBeanArrayList.add(itemDetailsBean);
                        } while (cursor.moveToNext());
                    } finally {
                        cursor.close();
                    }
                }
                ItemListAdapter itemListAdapter = new ItemListAdapter(parent, itemDetailsBeanArrayList);
                listview_balance_list.setAdapter(itemListAdapter);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Log.d("TAG", "onQueryTextChange ");
                itemDetailsBeanArrayList.clear();

                DatabaseHelper db1 = new DatabaseHelper(parent);
                SQLiteDatabase db = db1.getReadableDatabase();
                String selectQuery =  "Select * from "+AdatSoftConstants.TABLE_ITEM_MASTER
                        +AdatSoftData.Selected_Sno+" where item_desc LIKE  '%" +s+ "%' "+" order by item_desc";

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
                            itemDetailsBean = new ItemDetailsBean();
                            itemDetailsBean.setItem_code(cursor.getString(cursor.getColumnIndex("item_code")));
                            itemDetailsBean.setItem_desc(cursor.getString(cursor.getColumnIndex("item_desc")));
                            itemDetailsBean.setRatefactor(cursor.getString(cursor.getColumnIndex("ratefactor")));
                            itemDetailsBean.setQty_Exp(cursor.getString(cursor.getColumnIndex("Qty_Exp")));
                            itemDetailsBean.setWt_Exp(cursor.getString(cursor.getColumnIndex("Wt_Exp")));
                            itemDetailsBean.setAmt_Exp(cursor.getString(cursor.getColumnIndex("Amt_Exp")));
                            itemDetailsBeanArrayList.add(itemDetailsBean);
                        } while (cursor.moveToNext());
                    } finally {
                        cursor.close();
                    }
                }
                ItemListAdapter itemListAdapter = new ItemListAdapter(parent, itemDetailsBeanArrayList);
                listview_balance_list.setAdapter(itemListAdapter);
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
