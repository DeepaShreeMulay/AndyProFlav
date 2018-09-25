package com.vritti.vrittiaccounting;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
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
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class SavetoserverActivity extends AppCompatActivity {
    int icondrawable;
    String name_mar,category,selected_shop_no,Module,name_mar_title;

    utility ut;
    Context parent;
    TextView tvnodata;
    DatabaseHelper db1;
    ListView listview_balance_list;
    BalanceDetailsBean balanceDetailsBean;
    /*public static ArrayList<BalanceDetailsBean> balanceArrayList;*/
    public ArrayList<HashMap<String, String>> ArrayList;
    int Billcnt=0, RecCnt=0;
    
    JSONArray jsonArrayCust, jsonArraySale;
    JSONObject jsonCust, jsonSale;
    Timer autoUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GetIntentExtras();
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Send Data to Server");
        initView();
        getDataFromDataBase();
        setListner();
    }

    private void setListner() {
        listview_balance_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
          if (position == 0){
              SendCustRect();
          }else if (position == 1){
              SendSaleBill();
          }
            }
        });
    }


    private void SendCustRect() {
        DatabaseHelper db1 = new DatabaseHelper(getBaseContext());
        SQLiteDatabase sql = db1.getWritableDatabase();
        Cursor cursor = sql.rawQuery("Select * from "+AdatSoftConstants.TABLE_Cust_Rect
                +AdatSoftData.Selected_Sno+" where is_dwnld ='N'", null);
        cursor.moveToFirst();
        if (cursor != null && cursor.getCount()>0) {
            cursor.moveToFirst();
            jsonArrayCust = new JSONArray();
            try {
                do {
                    jsonCust = new JSONObject();
                    jsonCust.put("date", cursor.getString(cursor.getColumnIndex("date")));
                    jsonCust.put("cust_code", cursor.getString(cursor.getColumnIndex("cust_code")));
                    jsonCust.put("cust_name", cursor.getString(cursor.getColumnIndex("cust_name")));
                    jsonCust.put("amount", cursor.getString(cursor.getColumnIndex("amount")));
                    jsonCust.put("kasar", cursor.getString(cursor.getColumnIndex("kasar")));
                    jsonCust.put("total", cursor.getString(cursor.getColumnIndex("total")));
                    jsonCust.put("jali", cursor.getString(cursor.getColumnIndex("jali")));
                    jsonCust.put("pay_mode", cursor.getString(cursor.getColumnIndex("pay_mode")));
                    jsonCust.put("narration", cursor.getString(cursor.getColumnIndex("narration")));
                    jsonCust.put("is_dwnld", cursor.getString(cursor.getColumnIndex("is_dwnld")));
                    jsonArrayCust.put(jsonCust);
                }while(cursor.moveToNext());
            }catch(JSONException e){
                e.printStackTrace();
            }catch(Exception e){
            e.printStackTrace();
            }finally {
                cursor.close();
            }
            if (NetworkUtils.isNetworkAvailable(parent)) {
                if ((AdatSoftData.SESSION_ID != null)
                        && (AdatSoftData.HANDLE != null)) {
                    new SendCustAPI().execute("onlrect"+AdatSoftData.Selected_Sno,String.valueOf(jsonArrayCust));
                } else {
                    new StartSession(parent, new CallbackInterface() {

                        @Override
                        public void callMethod() {
                            new SendCustAPI().execute("onlrect"+AdatSoftData.Selected_Sno,String.valueOf(jsonArrayCust));
                        }
                    });
                }
            } else {
                Toast.makeText(parent, "No internet..", Toast.LENGTH_LONG).show();
                // callSnackbar();
            }
        }else {
            Toast.makeText(SavetoserverActivity.this, "Data is not Present", Toast.LENGTH_SHORT).show();
            cursor.close();
        }

    }
    private void SendSaleBill() {

        DatabaseHelper db1 = new DatabaseHelper(getBaseContext());
        SQLiteDatabase sql = db1.getWritableDatabase();
        Cursor cursor = sql.rawQuery("Select * from "+AdatSoftConstants.TABLE_SALE_BILL
                +AdatSoftData.Selected_Sno+" where is_dwnld ='N'", null);
        cursor.moveToFirst();
        if (cursor != null && cursor.getCount()>0) {
            cursor.moveToFirst();
            jsonArraySale = new JSONArray();
            try {
                do {
                    jsonSale = new JSONObject();
                    jsonSale.put("bill_date", cursor.getString(cursor.getColumnIndex("bill_date")));
                    jsonSale.put("cust_code", cursor.getString(cursor.getColumnIndex("cust_code")));
                    jsonSale.put("cust_name", cursor.getString(cursor.getColumnIndex("cust_name")));
                    jsonSale.put("item_code", cursor.getString(cursor.getColumnIndex("item_code")));
                    jsonSale.put("item_name", cursor.getString(cursor.getColumnIndex("item_name")));
                    jsonSale.put("qty", cursor.getString(cursor.getColumnIndex("qty")));
                    jsonSale.put("weight_str", cursor.getString(cursor.getColumnIndex("weight_str")));
                    jsonSale.put("total_wt", cursor.getString(cursor.getColumnIndex("total_wt")));
                    jsonSale.put("bill_rate", cursor.getString(cursor.getColumnIndex("bill_rate")));
                    jsonSale.put("amount", cursor.getString(cursor.getColumnIndex("amount")));
                    jsonSale.put("Qty_exp", cursor.getString(cursor.getColumnIndex("Qty_exp")));
                    jsonSale.put("Wt_exp", cursor.getString(cursor.getColumnIndex("Wt_exp")));
                    jsonSale.put("Amt_exp", cursor.getString(cursor.getColumnIndex("Amt_exp")));
                    jsonSale.put("is_dwnld", cursor.getString(cursor.getColumnIndex("is_dwnld")));
                    jsonArraySale.put(jsonSale);
                }while(cursor.moveToNext());
            }catch(JSONException e){
                e.printStackTrace();
            }catch(Exception e){
                e.printStackTrace();
            }finally {
                cursor.close();
            }
            if (NetworkUtils.isNetworkAvailable(parent)) {
                if ((AdatSoftData.SESSION_ID != null)
                        && (AdatSoftData.HANDLE != null)) {
                    new SendCustAPI().execute("onlsale"+AdatSoftData.Selected_Sno,String.valueOf(jsonArraySale));
                } else {
                    new StartSession(parent, new CallbackInterface() {

                        @Override
                        public void callMethod() {
                            new SendCustAPI().execute("onlsale"+AdatSoftData.Selected_Sno,String.valueOf(jsonArraySale));
                        }
                    });
                }
            } else {
                Toast.makeText(parent, "No internet..", Toast.LENGTH_LONG).show();
                // callSnackbar();
            }
        }else {
            Toast.makeText(SavetoserverActivity.this, "Data is not Present", Toast.LENGTH_SHORT).show();
            cursor.close();
        }
    }

    private void getDataFromDataBase() {
        // TODO Auto-generated method stub
        //ArrayList.clear();
        getCount();
        String item1 = "("+RecCnt+")    Customer Receipt";
        String item2 = "("+Billcnt+")    Sale Bill";

        String[] items = {item1,item2};

       ArrayAdapter<String> itemsAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
        listview_balance_list.setAdapter(itemsAdapter);
    }


    private void getCount(){
        try{
            DatabaseHelper db1 = new DatabaseHelper(getBaseContext());
            SQLiteDatabase sql = db1.getWritableDatabase();
            Cursor cursor = sql.rawQuery("Select * from "+AdatSoftConstants.TABLE_Cust_Rect
                    +AdatSoftData.Selected_Sno+" where is_dwnld ='N'", null);
            cursor.moveToFirst();
            if (cursor != null && cursor.getCount()>0) {
                RecCnt=cursor.getCount();
                cursor.close();

            }else{
                RecCnt =0;
                cursor.close();
            }
            Cursor cursor1 = sql.rawQuery("Select * from "+AdatSoftConstants.TABLE_SALE_BILL
                    +AdatSoftData.Selected_Sno+" where is_dwnld ='N'", null);
            cursor1.moveToFirst();
            if (cursor1 != null && cursor1.getCount()>0) {
                Billcnt=cursor1.getCount();
                cursor1.close();

            }else{
                Billcnt =0;
                cursor1.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
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
        parent = SavetoserverActivity.this;
        ut = new utility();
        db1 = new DatabaseHelper(parent);
        tvnodata = (TextView) findViewById(R.id.tvnodata);
        listview_balance_list = (ListView) findViewById(R.id.listview_firm_list);
    }

    class SendCustAPI extends AsyncTask<String, Void, String> {
        // ProgressDialog progressDialog;
        String responseString = null,dataof="k";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {
            try {
                if (params[0].contains("onlrect")){
                    dataof = "onlrect";
                }else if (params[0].contains("onlsale")){
                    dataof = "onlsale";
                }

                SoapObject request = new SoapObject(
                        AdatSoftData.NAMESPACE,
                        AdatSoftData.METHOD_SAVERECT_BILL);
                PropertyInfo propInfo = new PropertyInfo();
                propInfo.type = PropertyInfo.STRING_CLASS;

                request.addProperty("sessionId", AdatSoftData.SESSION_ID);
                request.addProperty("handler", AdatSoftData.HANDLE);
                request.addProperty("key", params[0]);
                request.addProperty("data", params[1]);
                request.addProperty("cust_no", AdatSoftData.Lno);
                request.addProperty("shop_no", AdatSoftData.Selected_Sno);

                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);

                HttpTransportSE androidHttpTransport = new HttpTransportSE(AdatSoftData.URL_SOAP);
                androidHttpTransport.call(AdatSoftData.NAMESPACE
                        + AdatSoftData.METHOD_SAVERECT_BILL, envelope);

                SoapObject response = (SoapObject) envelope.bodyIn;
                responseString = response.getProperty(0).toString()/* + "," + params[5] + "," + params[6]+ "," + params[2]*/;
                Log.e("responseString", responseString);

            } catch (Exception e) {
                responseString = "error";
                e.printStackTrace();
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {

            super.onPostExecute(result);

            if (responseString.equalsIgnoreCase("error")) {
                Toast.makeText(SavetoserverActivity.this, "The server is taking too long to respond OR something is wrong with your iternet connection. Please try again later.", Toast.LENGTH_LONG)
                        .show();
            }else {

                if (dataof.equalsIgnoreCase("onlrect")) {
                    String id[] = result.split(",");
                    DatabaseHelper db1 = new DatabaseHelper(SavetoserverActivity.this);
                    SQLiteDatabase db = db1.getWritableDatabase();
                    ContentValues newValues = new ContentValues();
                    newValues.put("is_dwnld", "Y");
                    db.update(AdatSoftConstants.TABLE_Cust_Rect + AdatSoftData.Selected_Sno, newValues, null, null);
                    db.delete(AdatSoftConstants.TABLE_Cust_Rect + AdatSoftData.Selected_Sno, "is_dwnld = 'Y'", null);

                }if (dataof.equalsIgnoreCase("onlsale")) {
                    String id[] = result.split(",");
                    DatabaseHelper db1 = new DatabaseHelper(SavetoserverActivity.this);
                    SQLiteDatabase db = db1.getWritableDatabase();
                    ContentValues newValues = new ContentValues();
                    newValues.put("is_dwnld", "Y");
                    db.update(AdatSoftConstants.TABLE_SALE_BILL + AdatSoftData.Selected_Sno, newValues, null, null);
                    db.delete(AdatSoftConstants.TABLE_SALE_BILL + AdatSoftData.Selected_Sno, "is_dwnld = 'Y'", null);

                }
                getDataFromDataBase();
            }

        }
    }


}
