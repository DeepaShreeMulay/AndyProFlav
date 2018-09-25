package com.vritti.vrittiaccounting.data;


/**
 * Created by sharvari on 9/19/16.
 */
public class AdatSoftConstants {
    public static final int DATABASE_VERSION = 3;

    public static final String DATABASE_NAME = "AdatSoftDB";


    public static final String TABLE_FIRM_DETAILS = "firmDetails_shopno_";
    public static final String TABLE_CASH_BALANCE = "CashBalance_shopno_";
    public static final String TABLE_FIRMWISE_USERNAME = "firmwiseUsername";
    public static final String TABLE_FIRM = "firmMaster";
    public static final String TABLE_AGING_BALANCE = "agingBalance_shopno_";
    public static final String TABLE_LEDGER_BALANCE = "LedgerBalance_shopno_";
    public static final String TABLE_ITEM_MASTER = "ItemMaster_shopno_";
    public static final String TABLE_SALE_BILL = "SaleBill_shopno_";
    public static final String TABLE_Cust_Rect= "Cust_Rect_shopno_";

    public static final String CREATE_TABLE_FIRMWISE_USERNAME = "CREATE TABLE IF NOT EXISTS "

            + TABLE_FIRMWISE_USERNAME
            + "(shop_no TEXT,"+
            " UserName TEXT,"+
            " Password TEXT)";

    public static final String CREATE_TABLE_AGING_BALANCE = "CREATE TABLE "

            + TABLE_AGING_BALANCE+AdatSoftData.Selected_Sno
            + "(acno TEXT,"+
            " Bal_0_15 TEXT,"+
            " Bal_16_30 TEXT,"+
            " Bal_31_60 TEXT,"+
            " Bal_61_90 TEXT,"+
            " Bal_m_90 TEXT,"+
            " Total_Bal TEXT,"+
            " Avg_Total TEXT,"+
            " Bal_Avg TEXT,"+
            " Bal_Uplod_Dt TEXT)";

    public static final String CREATE_TABLE_LEDGER_BALANCE = "CREATE TABLE "

            + TABLE_LEDGER_BALANCE+AdatSoftData.Selected_Sno
            + "(acno TEXT,"+
            " date TEXT,"+
            " Amount TEXT,"+
            " CD TEXT,"+
            " narr TEXT)";

    public static final String CREATE_TABLE_ITEM_MASTER = "CREATE TABLE "

            + TABLE_ITEM_MASTER+AdatSoftData.Selected_Sno
            + "(item_code TEXT,"+
            " item_desc TEXT,"+
            " ratefactor TEXT,"+
            " Qty_Exp TEXT,"+
            " Wt_Exp TEXT,"+
            " Amt_Exp TEXT)";

    public static final String CREATE_TABLE_CASH_BALANCE = "CREATE TABLE "

            + TABLE_CASH_BALANCE+AdatSoftData.Selected_Sno
            + "(Date TEXT,"+
            " Opening TEXT,"+
            " Receipt TEXT,"+
            " Payment TEXT,"+
            " Bal TEXT)";

    public static final String CREATE_TABLE_FIRM_DETAILS = "CREATE TABLE "

            + TABLE_FIRM_DETAILS+AdatSoftData.Selected_Sno
            + "(Acno TEXT,"+
            " Name TEXT,"+
            " City TEXT,"+
            " mobile TEXT,"+
            " ac_type TEXT,"+
            " Balance TEXT,"+
            " Bal_cd TEXT,"+
            " updatedate TEXT)";

    public static final String CREATE_TABLE_FIRM = "CREATE TABLE "
            + TABLE_FIRM
            + "(SNo TEXT PRIMARY KEY , cust_no TEXT, shop_no TEXT, name_mar TEXT, address TEXT, mobile TEXT, AMCExpireDt TEXT, Module TEXT )";

    public static final String CREATE_TABLE_SALE_BILL = "CREATE TABLE IF NOT EXISTS "

            + TABLE_SALE_BILL+AdatSoftData.Selected_Sno
            + "(bill_date TEXT,"+
            " cust_code TEXT,"+
            " cust_name TEXT,"+
            " item_code TEXT,"+
            " item_name TEXT,"+
            " qty TEXT,"+
            " weight_str TEXT,"+
            " total_wt TEXT,"+
            " bill_rate TEXT,"+
            " amount TEXT,"+
            " Qty_exp TEXT,"+
            " Wt_exp TEXT,"+
            " Amt_exp TEXT,"+
            " is_dwnld TEXT)";


    public static final String CREATE_TABLE_Cust_Rect = "CREATE TABLE IF NOT EXISTS "

            + TABLE_Cust_Rect+AdatSoftData.Selected_Sno
            + "(date TEXT,"+
            " cust_code TEXT,"+
            " cust_name TEXT,"+
            " amount TEXT,"+
            " kasar TEXT,"+
            " total TEXT,"+
            " jali TEXT,"+
            " pay_mode TEXT,"+
            " narration TEXT,"+
            " is_dwnld TEXT)";


}