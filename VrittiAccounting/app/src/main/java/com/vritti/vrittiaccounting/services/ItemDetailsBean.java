package com.vritti.vrittiaccounting.services;

/**
 * Created by Admin-3 on 10/12/2017.
 */

public class ItemDetailsBean {

    public String getItem_code() {
        return item_code;
    }

    public void setItem_code(String item_code) {
        this.item_code = item_code;
    }

    public String getItem_desc() {
        return item_desc;
    }

    public void setItem_desc(String item_desc) {
        this.item_desc = item_desc;
    }

    public String getRatefactor() {
        return ratefactor;
    }

    public void setRatefactor(String ratefactor) {
        this.ratefactor = ratefactor;
    }

    public String getQty_Exp() {
        return Qty_Exp;
    }

    public void setQty_Exp(String qty_Exp) {
        Qty_Exp = qty_Exp;
    }

    public String getWt_Exp() {
        return Wt_Exp;
    }

    public void setWt_Exp(String wt_Exp) {
        Wt_Exp = wt_Exp;
    }

    public String getAmt_Exp() {
        return Amt_Exp;
    }

    public void setAmt_Exp(String amt_Exp) {
        Amt_Exp = amt_Exp;
    }

    String item_code ;String item_desc ; String ratefactor ;String Qty_Exp ; String Wt_Exp ;String Amt_Exp ;
}
