package com.vritti.vrittiaccounting;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.vritti.vrittiaccounting.data.AdatSoftConstants;
import com.vritti.vrittiaccounting.data.AdatSoftData;
import com.vritti.vrittiaccounting.database.DatabaseHelper;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Admin-3 on 8/30/2017.
 */

public class DatewiseBalanceFragment extends Fragment {

    private View rootView;
    DatabaseHelper databaseHelper;
    Context context;
    TextView tv_tot_cred,tvcontbal_cred,tvcontdate_cred;
    String Data_Balance, Data_Bal_cd, Data_updatedate;
    ListView listview_balance_credit;
    CashBalanceBean cashBalanceBean;
    public static ArrayList<CashBalanceBean> cashBalanceBeanArrayList;

    public DatewiseBalanceFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_datewisebalance, container, false);
        initView(rootView);
        getData();
        //getTotCred();
        //getDataFromDataBase();
        return rootView;
    }

    private void getDataFromDataBase() {
        // TODO Auto-generated method stub

        DatabaseHelper db1 = new DatabaseHelper(context);
        SQLiteDatabase db = db1.getWritableDatabase();

        Cursor cursor1 = db.rawQuery("Select * from "+AdatSoftConstants.TABLE_FIRM_DETAILS
                +AdatSoftData.Selected_Sno+" where Acno ='"+AdatSoftData.Selected_Acno+"'", null);
        Log.d("test", "" + cursor1.getCount());
        if (cursor1.getCount() > 0) {
            cursor1.moveToFirst();
            try {
                    Data_Balance = cursor1.getString(cursor1.getColumnIndex("Balance"));
                    Data_Bal_cd= cursor1.getString(cursor1.getColumnIndex("Bal_cd"));
                    Data_updatedate = cursor1.getString(cursor1.getColumnIndex("updatedate"));
            } finally {
                cursor1.close();
            }

            tvcontbal_cred.setText("Balance \u20B9 "+getcurrencyformate(Data_Balance)+" "+getCorD(Data_Bal_cd));
            tvcontdate_cred.setText("As on "+getDateinFormat(Data_updatedate));
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

    private void getData() {
        cashBalanceBeanArrayList.clear();
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        Cursor cursor1 = db.rawQuery("Select Date, Opening, Bal from "
                + AdatSoftConstants.TABLE_CASH_BALANCE+ AdatSoftData.Selected_Sno
                +" order by Date desc", null);
        Log.e("test", "" + cursor1.getCount());
        if (cursor1.getCount() > 0) {
            cursor1.moveToFirst();
            try {
                do {
                    cashBalanceBean = new CashBalanceBean();
                    cashBalanceBean.setDate(cursor1.getString(cursor1.getColumnIndex("Date")));
                    cashBalanceBean.setAmount1(cursor1.getString(cursor1.getColumnIndex("Opening")));
                    cashBalanceBean.setAmount2(cursor1.getString(cursor1.getColumnIndex("Bal")));
                    cashBalanceBeanArrayList.add(cashBalanceBean);
                } while (cursor1.moveToNext());
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        CashBalanceListAdapter cashBalanceListAdapter = new CashBalanceListAdapter(context, cashBalanceBeanArrayList);
        listview_balance_credit.setAdapter(cashBalanceListAdapter);
    }

    private void getTotCred() {
        float SumCr = (float) 0.00;
        float SumDr = (float) 0.00;
        float SumT = (float) 0.00;
        String Scr, Sdr, St;
        DatabaseHelper db1 = new DatabaseHelper(context);
        SQLiteDatabase db = db1.getWritableDatabase();

        Cursor cursor1 = db.rawQuery("Select CAST(Sum(Amount) as float) as Crtotal from " + AdatSoftConstants.TABLE_LEDGER_BALANCE
                + AdatSoftData.Selected_Sno + " where acno ='" + AdatSoftData.Selected_Acno + "' and CD='C'", null);
        Log.d("test", "" + cursor1.getCount());
        Log.e("Cr count", "" + cursor1.getCount());
        if (cursor1.getCount() > 0) {
            cursor1.moveToFirst();
            if (String.valueOf(cursor1.getFloat(cursor1.getColumnIndex("Crtotal"))).equals(null)) {
                Log.e("Cr Sum", "\u20B9 0.00");
            } else {
                Scr = String.valueOf(cursor1.getFloat(cursor1.getColumnIndex("Crtotal")));
                Log.e("Cr Sum", String.valueOf(cursor1.getFloat(cursor1.getColumnIndex("Crtotal"))));
                SumCr = Float.parseFloat(Scr);
            }
        } else {
            Log.e("Cr Sum", "Not found");
        }
        tv_tot_cred.setText("\u20B9 "+getcurrencyformate(String.valueOf(SumCr)));
    }

    private String getcurrencyformate(String balance) {
        String parts[] = balance.split("\\.");
        int i  = Integer.parseInt(parts[0]);
        DecimalFormat formatter = new DecimalFormat("##,##,##,###");
        String yourFormattedString = formatter.format(i);
        return yourFormattedString+"."+parts[1];
    }

    private void initView(View rootView) {
        context = getActivity();
        cashBalanceBeanArrayList = new ArrayList<CashBalanceBean>();
        listview_balance_credit = (ListView) rootView.findViewById(R.id.listview_balance_credit);
        tv_tot_cred = (TextView) rootView.findViewById(R.id.tv_tot_cred);
        tvcontbal_cred = (TextView) rootView.findViewById(R.id.tvcontbal_cred);
        tvcontdate_cred = (TextView) rootView.findViewById(R.id.tvcontdate_cred);
        databaseHelper = new DatabaseHelper(context);
    }
}
