package com.vritti.vrittiaccounting.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.vritti.vrittiaccounting.data.AdatSoftConstants;
import com.vritti.vrittiaccounting.data.AdatSoftData;


public class DatabaseHelper extends SQLiteOpenHelper {

    public DatabaseHelper(Context context) {
        super(context, AdatSoftConstants.DATABASE_NAME, null,
                AdatSoftConstants.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(AdatSoftConstants.CREATE_TABLE_FIRM);
        db.execSQL(AdatSoftConstants.CREATE_TABLE_FIRMWISE_USERNAME);
        db.execSQL("CREATE TABLE Bluetooth_Address(Address TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "
                + AdatSoftConstants.TABLE_FIRM);
        db.execSQL("DROP TABLE IF EXISTS "
                + AdatSoftConstants.TABLE_FIRMWISE_USERNAME);
        db.execSQL("DROP TABLE IF EXISTS Bluetooth_Address");
        onCreate(db);
    }

    public void add_Firmwise_Username(String shop_no ,String UserName ,String Password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("shop_no", shop_no);
        cv.put("UserName", UserName);
        cv.put("Password", Password);
        long a = db.insert(AdatSoftConstants.TABLE_FIRMWISE_USERNAME, null, cv);
    }

    public void add_AgingBalance(String acno , String Bal_0_15 , String Bal_16_30 , String Bal_31_60 , String Bal_61_90 ,String
                                     Bal_m_90 , String Total_Bal , String Avg_Total , String Bal_Avg ,String Bal_Uplod_Dt ) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("acno", acno);
        cv.put("Bal_0_15", Bal_0_15);
        cv.put("Bal_16_30", Bal_16_30);
        cv.put("Bal_31_60", Bal_31_60);
        cv.put("Bal_61_90", Bal_61_90);
        cv.put("Bal_m_90", Bal_m_90);
        cv.put("Total_Bal", Total_Bal);
        cv.put("Avg_Total", Avg_Total);
        cv.put("Bal_Avg", Bal_Avg);
        cv.put("Bal_Uplod_Dt", Bal_Uplod_Dt);
        long a = db.insert(AdatSoftConstants.TABLE_AGING_BALANCE+ AdatSoftData.Selected_Sno, null, cv);
    }

    public void add_LedgerBalance(String acno , String date , String Amount , String CD , String narr  ) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("acno", acno);
        cv.put("date", date);
        cv.put("Amount", Amount);
        cv.put("CD", CD);
        cv.put("narr", narr);
        long a = db.insert(AdatSoftConstants.TABLE_LEDGER_BALANCE+ AdatSoftData.Selected_Sno, null, cv);
    }

    public void add_ItemMaster(String item_code , String item_desc , String ratefactor , String Qty_Exp , String Wt_Exp, String Amt_Exp  ) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("item_code", item_code);
        cv.put("item_desc", item_desc);
        cv.put("ratefactor", ratefactor);
        cv.put("Qty_Exp", Qty_Exp);
        cv.put("Wt_Exp", Wt_Exp);
        cv.put("Amt_Exp", Amt_Exp);
        long a = db.insert(AdatSoftConstants.TABLE_ITEM_MASTER+ AdatSoftData.Selected_Sno, null, cv);
    }

    public void add_Cash_Balance(String Date ,String Opening ,String Receipt ,String Payment ,String Bal) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("Date", Date);
        cv.put("Opening", Opening);
        cv.put("Receipt", Receipt);
        cv.put("Payment", Payment);
        cv.put("Bal", Bal);
        long a = db.insert(AdatSoftConstants.TABLE_CASH_BALANCE + AdatSoftData.Selected_Sno, null, cv);
    }

    public void add_Firm_Details(String Acno ,String Name ,String City ,String mobile ,String ac_type ,String Balance ,String Bal_cd ,String updatedate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("Acno", Acno);
        cv.put("Name", Name);
        cv.put("City", City);
        cv.put("mobile", mobile);
        cv.put("ac_type", ac_type);
        cv.put("Balance", Balance);
        cv.put("Bal_cd", Bal_cd);
        cv.put("updatedate", updatedate);
        long a = db.insert(AdatSoftConstants.TABLE_FIRM_DETAILS+ AdatSoftData.Selected_Sno, null, cv);
    }

    public void add_Firm(String cust_no ,String shop_no ,String name_mar ,String address ,String mobile ,String AMCExpireDt ,String Module) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("cust_no", cust_no);
        cv.put("shop_no", shop_no);
        cv.put("name_mar", name_mar);
        cv.put("address", address);
        cv.put("mobile", mobile);
        cv.put("AMCExpireDt", AMCExpireDt);
        cv.put("Module", Module);
        long a = db.insert(AdatSoftConstants.TABLE_FIRM, null, cv);
    }

    public void add_SaleBill(String bill_date , String cust_code , String cust_name ,
                             String item_code , String item_name , String qty , String weight_str,
                             String total_wt , String bill_rate  , String amount , String Qty_exp ,
                             String Wt_exp, String Amt_exp,  String is_dwnld  ) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("bill_date", bill_date);
        cv.put("cust_code", cust_code);
        cv.put("cust_name", cust_name);
        cv.put("item_code", item_code);
        cv.put("item_name", item_name);
        cv.put("qty", qty);
        cv.put("weight_str", weight_str);
        cv.put("total_wt", total_wt);
        cv.put("bill_rate", bill_rate);
        cv.put("amount", amount);
        cv.put("Qty_exp", Qty_exp);
        cv.put("Wt_exp", Wt_exp);
        cv.put("Amt_exp", Amt_exp);
        cv.put("is_dwnld", is_dwnld);
        long a = db.insert(AdatSoftConstants.TABLE_SALE_BILL+ AdatSoftData.Selected_Sno, null, cv);
    }

    public void add_CustRect(String date , String cust_code , String cust_name ,
                             String amount , String kasar , String total , String jali,
                             String pay_mode, String narration ,  String is_dwnld  ) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("date", date);
        cv.put("cust_code", cust_code);
        cv.put("cust_name", cust_name);
        cv.put("amount", amount);
        cv.put("kasar", kasar);
        cv.put("total", total);
        cv.put("jali", jali);
        cv.put("pay_mode", pay_mode);
        cv.put("narration", narration);
        cv.put("is_dwnld", is_dwnld);
        long a = db.insert(AdatSoftConstants.TABLE_Cust_Rect+ AdatSoftData.Selected_Sno, null, cv);
    }

    public void AddBluetooth(String address) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("Address", address);
        db.insert("Bluetooth_Address", null, cv);
    }


}