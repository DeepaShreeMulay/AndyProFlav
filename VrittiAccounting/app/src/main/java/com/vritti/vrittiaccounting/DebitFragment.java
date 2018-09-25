package com.vritti.vrittiaccounting;

import android.app.Activity;
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

public class DebitFragment extends Fragment {

    private View rootView;
    DatabaseHelper databaseHelper;
    Context context;
    TextView tv_tot_deb,tvcontbal_deb,tvcontdate_deb;
    String Data_Balance, Data_Bal_cd, Data_updatedate;
    ListView listview_balance_debit;
    LedgerBalanceBean ledgerBalanceBean;
    public static ArrayList<LedgerBalanceBean> ledgerBalanceBeanArrayList;
    
    public DebitFragment() {
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
        rootView = inflater.inflate(R.layout.fragment_debit, container, false);
        initView(rootView);
        getData();
        getTotCred();
        getDataFromDataBase();
        return rootView;
    }

    private void getDataFromDataBase() {
        // TODO Auto-generated method stub

        DatabaseHelper db1 = new DatabaseHelper(context);
        SQLiteDatabase db = db1.getWritableDatabase();

        Cursor cursor1 = db.rawQuery("Select * from "+ AdatSoftConstants.TABLE_FIRM_DETAILS
                + AdatSoftData.Selected_Sno+" where Acno ='"+AdatSoftData.Selected_Acno+"'", null);
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

            tvcontbal_deb.setText("Balance \u20B9 "+getcurrencyformate(Data_Balance)+" "+getCorD(Data_Bal_cd));
            tvcontdate_deb.setText("As on "+getDateinFormat(Data_updatedate));
        }
    }

    private String getcurrencyformate(String balance) {
        String parts[] = balance.split("\\.");
        int i  = Integer.parseInt(parts[0]);
        DecimalFormat formatter = new DecimalFormat("##,##,##,###");
        String yourFormattedString = formatter.format(i);
        return yourFormattedString+"."+parts[1];
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

    private String getDateinFormat2(String amcExpireDt) {
        String result= null;
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy/MM/dd  HH:mm:ss");
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date date2 = dateFormat1.parse(amcExpireDt);
            result = dateFormat2.format(date2);
        }catch( Exception e){
            e.printStackTrace();
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
        ledgerBalanceBeanArrayList.clear();
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        Cursor cursor1 = db.rawQuery("Select * from "
                + AdatSoftConstants.TABLE_LEDGER_BALANCE+ AdatSoftData.Selected_Sno
                + " where CD='D' and acno='"+ AdatSoftData.Selected_Acno+"' order by date", null);
        Log.e("test", "" + cursor1.getCount());
        if (cursor1.getCount() > 0) {
            cursor1.moveToFirst();
            try {
                do {
                    ledgerBalanceBean = new LedgerBalanceBean();
                    ledgerBalanceBean.setAcno(cursor1.getString(cursor1.getColumnIndex("acno")));
                    ledgerBalanceBean.setAmount(cursor1.getString(cursor1.getColumnIndex("Amount")));
                    ledgerBalanceBean.setCD(cursor1.getString(cursor1.getColumnIndex("CD")));
                    ledgerBalanceBean.setDate(cursor1.getString(cursor1.getColumnIndex("date")));
                    ledgerBalanceBean.setNarr(cursor1.getString(cursor1.getColumnIndex("narr")));
                    ledgerBalanceBeanArrayList.add(ledgerBalanceBean);
                } while (cursor1.moveToNext());
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        LedgerBalanceListAdapter ledgerBalanceListAdapter = new LedgerBalanceListAdapter(context, ledgerBalanceBeanArrayList);
        listview_balance_debit.setAdapter(ledgerBalanceListAdapter);
    }
    private void getTotCred() {
        float SumCr = (float) 0.00;
        float SumDr = (float) 0.00;
        float SumT = (float) 0.00;
        String Scr, Sdr, St;
        DatabaseHelper db1 = new DatabaseHelper(context);
        SQLiteDatabase db = db1.getWritableDatabase();

        Cursor cursor1 = db.rawQuery("Select CAST(Sum(Amount) as float) as Crtotal from " + AdatSoftConstants.TABLE_LEDGER_BALANCE
                + AdatSoftData.Selected_Sno + " where acno ='" + AdatSoftData.Selected_Acno + "' and CD='D'", null);
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
        tv_tot_deb.setText("\u20B9 "+getcurrencyformate(String.valueOf(SumCr)));
    }
    private void initView(View rootView) {
        context = getActivity();
        ledgerBalanceBeanArrayList = new ArrayList<LedgerBalanceBean>();
        listview_balance_debit = (ListView) rootView.findViewById(R.id.listview_balance_debit);
        tv_tot_deb = (TextView) rootView.findViewById(R.id.tv_tot_deb);
        tvcontbal_deb = (TextView) rootView.findViewById(R.id.tvcontbal_deb);
        tvcontdate_deb = (TextView) rootView.findViewById(R.id.tvcontdate_deb);
        databaseHelper = new DatabaseHelper(context);
    }

}
