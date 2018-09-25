package com.vritti.vrittiaccounting.services;

/**
 * Created by Admin-3 on 7/20/2017.
 */

public class BalanceDetailsBean {
    String Acno ;String Name ;String City ;String ac_type ;String mobile ;String Balance ;String Bal_cd ;String updatedate;

    public String getAcno() {
        return Acno;
    }

    public void setAcno(String acno) {
        Acno = acno;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public String getAc_type() {
        return ac_type;
    }

    public void setAc_type(String ac_type) {
        this.ac_type = ac_type;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getBalance() {
        return Balance;
    }

    public void setBalance(String balance) {
        Balance = balance;
    }

    public String getBal_cd() {
        return Bal_cd;
    }

    public void setBal_cd(String bal_cd) {
        Bal_cd = bal_cd;
    }

    public String getUpdatedate() {
        return updatedate;
    }

    public void setUpdatedate(String updatedate) {
        this.updatedate = updatedate;
    }
}
