package com.vritti.vrittiaccounting;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.vritti.vrittiaccounting.data.AdatSoftConstants;
import com.vritti.vrittiaccounting.data.AdatSoftData;
import com.vritti.vrittiaccounting.data.NetworkUtils;
import com.vritti.vrittiaccounting.data.utility;
import com.vritti.vrittiaccounting.database.DatabaseHelper;
import com.vritti.vrittiaccounting.services.Item;
import com.vritti.vrittiaccounting.services.ItemDetailsBean;
import com.zj.btsdk.BluetoothService;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SaleBillMainActivity extends AppCompatActivity {
    String name_mar,category,Name,City,ac_type,Balance,mobile,Bal_cd,updatedate,Acno,name_mar_title,Module;
    String Item_code,Item_desc,Ratefactor,Qty_Exp,Wt_Exp,Amt_Exp, bill_rate;
    TextView tvsalename, tvsaleitem, textview_cart_count;
    int qty=0, count = 0;
    float sum=0, Amount = 0, Total_Amt = 0, OtherCharge = 0, Qty_Exp_f = 0, Wt_Exp_f = 0, Amt_Exp_f= 0;
    String Wtarray, CheckedResult ;
    DatabaseHelper db1;
    EditText edt_salequantity,edt_salewt,edt_saletotalamt,edt_salewdarray,edt_saletotalwt,edt_salerate,
            edt_saleamt,edt_saleoc;
    Button btnsaleadd,btnsalesave,btnsalenewitem;


    String userName = null, password = null ;
    EditText edt_password,edt_username, edt_password_1,edt_username_1;
    Button btn_submit, btn_submit_1;
    AppCompatCheckBox checkBox_show, checkBox_show_1;
    AlertDialog alertDialog;
    String pass,user;

    EditText edt_mob;
    Button btn_confirm;
    AlertDialog alertDialog1;
    String confirmed_mobile ,TextMsg;
    MessagingURL asynctask;
    String responsemsg="";
    String FirmName, FirmAdd, FirmMob;

    List<CharSequence> list = new ArrayList<CharSequence>();

    utility ut;
    Context parent;

    BluetoothService mService = null;
    BluetoothDevice con_dev = null;
    private boolean deviceConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_bill_main);


        GetIntentExtras();
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Sale Bill");
        initView();
        getpendingReceiptCount();
        connectDevice();
        TextChangeListners();
        setListner();
    }

    private void getpendingReceiptCount() {
        try{
            DatabaseHelper db1 = new DatabaseHelper(getBaseContext());
            SQLiteDatabase sql = db1.getWritableDatabase();

            Cursor cursor = sql.rawQuery("Select * from "+AdatSoftConstants.TABLE_SALE_BILL+AdatSoftData.Selected_Sno+
                    " where cust_code='"+ Acno+"' and is_dwnld='N'", null);
            cursor.moveToFirst();
            if (cursor != null && cursor.getCount()>0) {
                textview_cart_count.setText(String.valueOf(cursor.getCount()+1));
                cursor.close();
                sql.close();
                db1.close();
            }else{
                textview_cart_count.setText("1");
                cursor.close();
                sql.close();
                db1.close();
            }
        }catch(Exception e){
            e.printStackTrace();
            textview_cart_count.setText("1");
        }
    }

    private void TextChangeListners() {
        edt_salequantity.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int aft )
            {

            }

            @Override
            public void afterTextChanged(Editable s)
            {
                if (s.toString().equals("")|| s == null){
                    qty = 0;
                } else {
                    qty = Integer.parseInt(s.toString());
                }
                if (count == qty){
                    btnsaleadd.getBackground().setAlpha(50);
                    btnsaleadd.setEnabled(false);
                    edt_salewt.setText("0");
                    edt_salewt.setFocusable(false);
                    edt_salewt.setFocusableInTouchMode(false);
                    edt_salewt.setClickable(false);
                    //edt_salerate.requestFocus();
                }else if(count < qty){
                    btnsaleadd.getBackground().setAlpha(180);
                    btnsaleadd.setEnabled(true);
                    edt_salewt.setText("0");
                    edt_salewt.setFocusable(true);
                    edt_salewt.setFocusableInTouchMode(true);
                    edt_salewt.setClickable(true);
                    //edt_salewt.requestFocus();
                } else if (count > qty){
                    edt_salequantity.setError("Quantity can't be less than entered weight nos.");
                    btnsaleadd.getBackground().setAlpha(50);
                    btnsaleadd.setEnabled(false);
                    edt_salewt.setText("0");
                    edt_salewt.setFocusable(false);
                    edt_salewt.setFocusableInTouchMode(false);
                    edt_salewt.setClickable(false);
                    edt_salequantity.requestFocus();

                }

            }
        });
        edt_salerate.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                allcalc();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int aft )
            {

            }

            @Override
            public void afterTextChanged(Editable s)
            {

            }
        });
    }

    private void allcalc() {
        calcAmount();
        calcqtyexp();
        calcamtexp();
        calcwtexp();
        calcoc();
        calcta();
    }

    private void calcta() {
        //Total_Amt = Amount + OtherCharge;
        Total_Amt = Amount + OtherCharge;
        edt_saletotalamt.setText(String.valueOf(Total_Amt));
    }

    private void calcoc() {
        //OtherCharge = Wt_exp_f+ Amt_exp_f+Qty_exp_f;
        OtherCharge =Wt_Exp_f + Amt_Exp_f + Qty_Exp_f;
        edt_saleoc.setText(String.valueOf(OtherCharge));
    }

    private void calcwtexp() {
        //Wt_exp_f=Tot_wt*itemamster.Wt_exp
        Wt_Exp_f = sum * Float.parseFloat(Wt_Exp);
    }

    private void calcamtexp() {
        //Amt_exp_f=amount*itemmaster.amt_exp/100
        Amt_Exp_f = Amount * Float.parseFloat(Amt_Exp)/100;
    }

    private void calcqtyexp() {
        //Qty_exp_f=qty*itemmaster.qty_exp
        Qty_Exp_f = qty* Float.parseFloat(Qty_Exp);
    }

    private void calcAmount() {
        //Amount=tot_wt*bill_rate/itemmaster.ratefactor
        float ratefactor = Float.parseFloat(Ratefactor);
        int rf = (int) Math.round(ratefactor);
        if (edt_salerate.getText().toString().equals("")){
            bill_rate = "0";
        } else {
            bill_rate = edt_salerate.getText().toString();
        }
        Amount = sum * Float.parseFloat(bill_rate) / rf;
        edt_saleamt.setText(String.valueOf(Amount));
    }

    private void mytotalwtfunction() {
        if (count == 0){
            sum = Float.parseFloat(edt_salewt.getText().toString());
            edt_saletotalwt.setText(edt_salewt.getText().toString());
        }else if (count > 0 && count <qty){
            sum = sum + Float.parseFloat( edt_salewt.getText().toString());
            edt_saletotalwt.setText(String.valueOf(sum));
        }
    }

    private void mywtarrayfunction() {

        if (count == 0){
            Wtarray = edt_salewt.getText().toString();
            //count++;
            edt_salewdarray.setText(Wtarray);
        }else if (count > 0 && count <qty){
            Wtarray = Wtarray + "," + edt_salewt.getText().toString();
            //count ++;
            edt_salewdarray.setText(Wtarray);
        }

    }

    private void GetIntentExtras() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            name_mar_title = (String) bundle.get("name_mar_title");
            category = (String) bundle.get("category");
            Acno = (String) bundle.get("Acno");
            Module = (String) bundle.get("Module");
            Name = (String) bundle.get("Name");
            City = (String) bundle.get("City");
            ac_type = (String) bundle.get("ac_type");
            Balance = (String) bundle.get("Balance");
            mobile = (String) bundle.get("mobile");
            Log.e("Mobile",mobile);
            Bal_cd = (String) bundle.get("Bal_cd");
            updatedate = (String) bundle.get("updatedate");

            Item_code = (String) bundle.get("Item_code");
            Item_desc = (String) bundle.get("Item_desc");
            Ratefactor = (String) bundle.get("Ratefactor");
            Qty_Exp = (String) bundle.get("Qty_Exp");
            Wt_Exp = (String) bundle.get("Wt_Exp");
            Amt_Exp = (String) bundle.get("Amt_Exp");
        }
    }

    private void initView() {
        parent = SaleBillMainActivity.this;
        ut = new utility();
        //itemDetailsBeanArrayList = new ArrayList<ItemDetailsBean>();
        db1 = new DatabaseHelper(parent);
        textview_cart_count = (TextView) findViewById(R.id.textview_cart_count);
        tvsalename = (TextView) findViewById(R.id.tvsalename);
        tvsaleitem = (TextView) findViewById(R.id.tvsaleitem);
        edt_salequantity = (EditText) findViewById(R.id.edt_salequantity);
        edt_salewt = (EditText) findViewById(R.id.edt_salewt);
        edt_salewdarray = (EditText) findViewById(R.id.edt_salewdarray);
        edt_saletotalwt = (EditText) findViewById(R.id.edt_saletotalwt);
        edt_salerate= (EditText) findViewById(R.id.edt_salerate);
        edt_saleamt = (EditText) findViewById(R.id.edt_saleamt);
        edt_saleoc = (EditText) findViewById(R.id.edt_saleoc);
        edt_saletotalamt = (EditText) findViewById(R.id.edt_saletotalamt);
        btnsaleadd = (Button) findViewById(R.id.btnsaleadd);
        btnsalesave = (Button) findViewById(R.id.btnsalesave);
        btnsalenewitem = (Button) findViewById(R.id.btnsalenewitem);
        tvsalename.setText(Name);
        tvsaleitem.setText(Item_desc);

        mService = new BluetoothService(parent, mHandler);

        if (mService.isAvailable() == false) {
            Toast.makeText(parent, "Bluetooth is not available",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void setListner() {

        btnsalenewitem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveentrytolocal();
                Intent intent = new Intent(SaleBillMainActivity.this, SaleBillItemActivity.class);
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
                intent.putExtras(extras);
                startActivity(intent);
                finish();
            }
        });

        btnsaleadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mytotalwtfunction();
                mywtarrayfunction();
                count++;
                edt_salewt.setText("");
                if (count == qty){
                    btnsaleadd.getBackground().setAlpha(50);
                    btnsaleadd.setEnabled(false);
                    edt_salewt.setText("0");
                    edt_salewt.setFocusable(false);
                    edt_salewt.setFocusableInTouchMode(false);
                    edt_salewt.setClickable(false);
                    edt_salerate.requestFocus();
                }
            }
        });

        btnsalesave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveentrytolocal();
                list.clear();
                for (int i=0;i<3;i++) {
                    if(i==0) {
                        list.add(" SMS ");
                    } else if(i==1) {
                        list.add(" WhatsApp ");
                    } else if(i==2) {
                        list.add(" Take Print ");
                    }
                }
                setprompt();
                //checkedoptions();

            }
        });

    }

    private String getFirmDetails() {
        String result= null;
        try{
            DatabaseHelper db1 = new DatabaseHelper(getBaseContext());
            SQLiteDatabase sql = db1.getWritableDatabase();
            Cursor cursor = sql.rawQuery("Select name_mar,address,mobile from "+ AdatSoftConstants.TABLE_FIRM+" WHERE shop_no='"+AdatSoftData.Selected_Sno+"'", null);
            cursor.moveToFirst();
            if (cursor != null && cursor.getCount()>0) {
                FirmName = cursor.getString(cursor.getColumnIndex("name_mar"));
                FirmAdd = cursor.getString(cursor.getColumnIndex("address"));
                FirmMob = cursor.getString(cursor.getColumnIndex("mobile"));
            }else{
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }

    private void checkedoptions() {
        Log.e("CheckedResult",CheckedResult);
        getFirmDetails();

        if (CheckedResult.contains(" Take Print ")){
            printbill();
        }
        
        if (CheckedResult.contains(" SMS ")){
            getPassword();
            if (password.equals("N/A")||password.equals(null)) {
                openDialog();
            } else {
                sendsms();
            }
        }

        if (CheckedResult.contains(" WhatsApp ")){
            whatsAppbill();
        }
    }

    private void whatsAppbill() {
        PackageManager pm=getPackageManager();
        try {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, "My application name");
            TextMsg = "Bill Alert\nBill No.: "+/*Billno+*/"\nAmount: "+String.valueOf(Amount)+"\nDate: "+getbilldate()+
                    "\n Please arrange for the payment. Ignore if already paid. Thank you." +
                    "\n Contact : "+FirmName+"\n Phone : "+FirmMob;
            PackageInfo info=pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
            //Check if package exists or not. If not then code
            //in catch block will be called
            i.setPackage("com.whatsapp");
            i.putExtra(Intent.EXTRA_TEXT, TextMsg);
            startActivity(Intent.createChooser(i, "choose one"));
        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(parent, "WhatsApp not Installed", Toast.LENGTH_SHORT).show();
        } catch (Exception e) { //e.toString();
        }
    }

    private void sendsms() {
        TextMsg = "Bill Alert\nBill No.: "+/*Billno+*/"\nAmount: "+String.valueOf(Amount)+"\nDate: "+getbilldate()+
                "\n Please arrange for the payment. Ignore if already paid. Thank you." +
                "\n Contact : "+FirmName+"\n Phone : "+FirmMob;
        ConfirmDialog();
    }
    private void getPassword() {
        try{
            DatabaseHelper db1 = new DatabaseHelper(getBaseContext());
            SQLiteDatabase sql = db1.getWritableDatabase();
            Cursor cursor = sql.rawQuery("Select * from "+ AdatSoftConstants.TABLE_FIRMWISE_USERNAME +
                    " where shop_no='"+AdatSoftData.Selected_Sno+"'", null);
            if (cursor != null && cursor.getCount()>0) {
                cursor.moveToFirst();
                userName = cursor.getString(cursor.getColumnIndex("UserName"));
                password = cursor.getString(cursor.getColumnIndex("Password"));
            }else{
                userName = "N/A";
                password = "N/A";
            }
        }catch(Exception e){
            e.printStackTrace();
            userName = "N/A";
            password = "N/A";
        }
    }
    private void openDialog() {
        LayoutInflater inflater = LayoutInflater.from(parent);
        View subView = inflater.inflate(R.layout.login_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(subView);
        alertDialog = builder.create();

        edt_username = (EditText) subView.findViewById(R.id.edt_username);
        edt_password = (EditText) subView.findViewById(R.id.edt_password);
        btn_submit = (Button) subView.findViewById(R.id.btn_submit);
        checkBox_show = (AppCompatCheckBox) subView.findViewById(R.id.checkbox_show);
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edt_username.getText().toString().equals("")||edt_username.getText().toString().equals(null)){
                    Toast.makeText(parent, "Please enter Username", Toast.LENGTH_SHORT).show();
                } else {
                    if (edt_password.length() > 0) {
                        user = edt_username.getText().toString();
                        pass = edt_password.getText().toString();
                        db1.add_Firmwise_Username(AdatSoftData.Selected_Sno,user,pass);
                        Toast.makeText(parent, "Password Set Successfully", Toast.LENGTH_SHORT).show();
                        alertDialog.dismiss();
                        //getPassword();
                        //PasswordopenDialog();
                        sendsms();
                        /*Intent intent = new Intent(parent, MessagingActivity.class);
                        Bundle extras = new Bundle();
                        extras.putString("category", category);
                        extras.putString("Acno", Acno);
                        extras.putString("Name", Name);
                        extras.putString("City", City);
                        extras.putString("ac_type", ac_type);
                        extras.putString("Balance", Balance);
                        extras.putString("mobile", mobile);
                        extras.putString("Bal_cd",Bal_cd);
                        extras.putString("updatedate", updatedate);
                        intent.putExtras(extras);
                        startActivity(intent);*/
                    } else {
                        Toast.makeText(parent, "Please enter password", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        alertDialog = builder.create();

        alertDialog.show();

        checkBox_show.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int start, end;
                Log.i("inside checkbox chnge", "" + isChecked);
                if (!isChecked) {
                    start = edt_password.getSelectionStart();
                    end = edt_password.getSelectionEnd();
                    edt_password.setTransformationMethod(new PasswordTransformationMethod());
                    edt_password.setSelection(start, end);
                    checkBox_show.setText("Show Password");
                } else {
                    start = edt_password.getSelectionStart();
                    end = edt_password.getSelectionEnd();
                    edt_password.setTransformationMethod(null);
                    edt_password.setSelection(start, end);
                    checkBox_show.setText("Hide Password");
                }
            }

        });
    }
    private void ConfirmDialog() {
        LayoutInflater inflater = LayoutInflater.from(parent);
        View subView1 = inflater.inflate(R.layout.confirm_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Mobile No.");
        builder.setView(subView1);

        edt_mob = (EditText) subView1.findViewById(R.id.edt_mob);
        edt_mob.setHint("Enter Mobile no");
        btn_confirm = (Button) subView1.findViewById(R.id.btn_confirm);
        edt_mob.setText(mobile);

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmed_mobile = edt_mob.getText().toString();
                System.out.println("confirmed" + confirmed_mobile);
                if (NetworkUtils.isNetworkAvailable(parent)) {
                    asynctask = null;
                    asynctask = new MessagingURL();
                    asynctask.execute();
                } else {
                    Toast.makeText(parent, "No internet..", Toast.LENGTH_LONG).show();
                    // callSnackbar();
                }
                alertDialog1.dismiss();
            }
        });
        alertDialog1 = builder.create();
        alertDialog1.show();
    }
    private void setprompt() {
        // Intialize  readable sequence of char values

        final CharSequence[] dialogList=  list.toArray(new CharSequence[list.size()]);
        final AlertDialog.Builder builderDialog = new AlertDialog.Builder(parent);
                builderDialog.setTitle("Select bill format");
        int count_checked = dialogList.length;
        boolean[] is_checked = new boolean[count_checked]; // set is_checked boolean false; 
        // Creating multiple selection by using setMutliChoiceItem method 
        builderDialog.setMultiChoiceItems(dialogList, is_checked,
                new DialogInterface.OnMultiChoiceClickListener() {
                    public void onClick(DialogInterface dialog,
                            int whichButton, boolean isChecked) {
                    }
                });

        builderDialog.setPositiveButton("OK", 
                new DialogInterface.OnClickListener() { 
                    @Override 
                    public void onClick(DialogInterface dialog, int which) { 
                         ListView list = ((AlertDialog) dialog).getListView(); 
                                // make selected item in the comma seprated string 
                                StringBuilder stringBuilder = new StringBuilder(); 
                        for (int i = 0; i < list.getCount(); i++) { 
                            boolean checked = list.isItemChecked(i); 
                             if (checked) { 
                                if (stringBuilder.length() > 0) stringBuilder.append(","); 
                                        stringBuilder.append(list.getItemAtPosition(i)); 
                             } 
                        } 
                         /*Check string builder is empty or not. If string builder is not empty. 
                          It will display on the screen. 
                         */ 
                        if (stringBuilder.toString().trim().equals("")) {
                            // ((TextView) findViewById(R.id.text)).setText("Click here to open Dialog");
                                    stringBuilder.setLength(0);
                            //Toast.makeText(parent, "Please select bill format", Toast.LENGTH_SHORT).show();
                            CheckedResult = "No option";
                            Log.e("stringBuilder",stringBuilder.toString());
                         } else {
                            // ((TextView) findViewById(R.id.text)).setText(stringBuilder);
                            CheckedResult = stringBuilder.toString();
                            Log.e("stringBuilder",stringBuilder.toString());
                            checkedoptions();
                        }
                        exitfun();
                    } 
                });

        builderDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                            //((TextView) findViewById(R.id.text)).setText(&quot;Click here to open Dialog&quot;);<br />
                        Log.e("Cancel","Cancel");
                    }
        });
                AlertDialog alert = builderDialog.create();
                alert.show();

    }

    private void exitfun()
    {
        Intent intent = new Intent(parent, SaleBillCustActivity.class);
        Bundle extras = new Bundle();
        extras.putString("category", "Cust");
        extras.putString("selected_shop_no",AdatSoftData.Selected_Sno );
        extras.putString("name_mar", name_mar);
        extras.putString("Module", Module);
        intent.putExtras(extras);
        startActivity(intent);
        finish();
    }
    private void saveentrytolocal() {

        SQLiteDatabase db = db1.getWritableDatabase();
        db.execSQL(AdatSoftConstants.CREATE_TABLE_SALE_BILL);
        db1.add_SaleBill( getbilldate() , Acno , Name ,
                 Item_code , Item_desc , String.valueOf(qty) , Wtarray,
                String.valueOf(sum) , bill_rate  , String.valueOf(Amount) , String.valueOf(Qty_Exp_f) ,
                String.valueOf(Wt_Exp_f), String.valueOf(Amt_Exp_f),  "N" );

    }
    private String getbilldate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("yyyy/MM/dd");
        String bill_date = mdformat.format(calendar.getTime());
        return bill_date;
    }
    private String getUserid() {
        String result= null;
        try{
            DatabaseHelper db1 = new DatabaseHelper(getBaseContext());
            SQLiteDatabase sql = db1.getWritableDatabase();
            Cursor cursor = sql.rawQuery("Select UserName from "+ AdatSoftConstants.TABLE_FIRMWISE_USERNAME+" WHERE shop_no='"+AdatSoftData.Selected_Sno+"'", null);
            cursor.moveToFirst();
            if (cursor != null && cursor.getCount()>0) {
                result = cursor.getString(cursor.getColumnIndex("UserName"));
            }else{
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }
    private String getPassword2() {
        String result= null;
        try{
            DatabaseHelper db1 = new DatabaseHelper(getBaseContext());
            SQLiteDatabase sql = db1.getWritableDatabase();
            Cursor cursor = sql.rawQuery("Select Password from "+ AdatSoftConstants.TABLE_FIRMWISE_USERNAME+" WHERE shop_no='"+AdatSoftData.Selected_Sno+"'", null);
            cursor.moveToFirst();
            if (cursor != null && cursor.getCount()>0) {
                result = cursor.getString(cursor.getColumnIndex("Password"));
            }else{
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }

    public class MessagingURL extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            utility ut = new utility();
            String url;
            url ="http://qm.vritti.co.in:420/VrittiQM.asmx/CallWebService?m="
                    + confirmed_mobile
                    + "&u="
                    + getUserid()
                    + "&p="
                    + getPassword2()
                    + "&s="
                    + TextMsg;
            Log.e("material ", "url : " + url);
            url = url.replaceAll(" ", "%20");
            url = url.replaceAll("\\n", "%20");
            try {
                System.out.println("-------  activity url --- " + url);
                responsemsg = ut.httpGet(url);

                System.out.println("-------------  xx vale-- " + responsemsg);
                Log.e("Adhicha response",responsemsg);

            } catch (IOException e) {
                e.printStackTrace();
                responsemsg = "Error";
            } catch (Exception e) {
                responsemsg = "Error";
                e.printStackTrace();
            }

            return responsemsg;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                Log.e("Post madhe responsemsg", responsemsg);

                if (responsemsg.equals("Error")) {
                    Toast.makeText(parent," Error ",Toast.LENGTH_SHORT).show();
                } else if (responsemsg.equals("<?xml version=\"1.0\" encoding=\"utf-8\"?><string xmlns=\"http://tempuri.org/\">U</string>")) {
                    Toast.makeText(parent,"Please Contact CustomerCare",Toast.LENGTH_SHORT).show();
                } else if (responsemsg.equals("<?xml version=\"1.0\" encoding=\"utf-8\"?><string xmlns=\"http://tempuri.org/\">S</string>")) {
                    Toast.makeText(parent,"Message send Successfully",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(parent,"It seems to have some issue..\nTry again after sometime..",Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void printbill() {
        if (deviceConnected /*|| PetroSoftData.PRINTERSETTING.equals("System")*/) {
            print();
        } else {
            Toast.makeText(parent, "Check Bluetooth Connection", Toast.LENGTH_LONG).show();
        }
        //Toast.makeText(parent,"Work in Progress", Toast.LENGTH_SHORT).show();
    }

    private void print() {
        /*if(PetroSoftData.PRINTERSETTING.equals("System")){
            //Toast.makeText(parent, "Work in Progress",Toast.LENGTH_SHORT).show();
            new PrintBill().execute();
            //printSys();
            return;
        }*/

        //double total = 0;
        String datetime = getbilldate();/*
        String username = AdatSoftData.;*/
        String msg = null;
        msg = "\n" + FirmName + "\n";
        msg += FirmAdd + "\n";
        msg += FirmMob + "\n\n";

        /*msg += "Bill No. : " + formattedBillId + "\n";*/
        msg += "User : " + Name+ "\n\n";
        msg += "Date : " + datetime + "\n";
        msg += "--------------------------------\n";
        msg += "Item        Qty   Rate    Amount\n";
        msg += "--------------------------------\n";
        double total =0;

        try {
            DatabaseHelper db1 = new DatabaseHelper(parent);
            SQLiteDatabase db = db1.getWritableDatabase();

            Cursor cursor = db
                    .rawQuery(
                            "Select item_name,total_wt,bill_rate,amount,weight_str from "+AdatSoftConstants.TABLE_SALE_BILL+ AdatSoftData.Selected_Sno
                                    + " where cust_code='"+ Acno+"' AND is_dwnld='N'",
                            null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    String itemNameToPrint = cursor.getString(0)/*+" "+cursor.getString(4)*/;

                    if (itemNameToPrint.length() > 8) {
                        itemNameToPrint = itemNameToPrint.substring(0, 8);
                    } else if (itemNameToPrint.length() <= 8) {
                        int diff = 8 - itemNameToPrint.length();
                        for (int i = 0; i < diff; i++) {
                            itemNameToPrint += " ";
                        }
                    }
                    String itemQtyToPrint = cursor.getString(1);
                    itemQtyToPrint = itemQtyToPrint
                            .replaceFirst("^0+(?!$)", "");

                    if (itemQtyToPrint.contains(".")) {
                        {
                            itemQtyToPrint += "000";
                            itemQtyToPrint = itemQtyToPrint.substring(0,
                                    itemQtyToPrint.lastIndexOf(".") + 3);
                        }
                    } else {
                        itemQtyToPrint += ".00";
                    }

                    if (itemQtyToPrint.length() <= 5) {
                        int diff = 5 - itemQtyToPrint.length();
                        if (diff > 0) {
                            for (int i = 0; i < diff; i++) {
                                itemQtyToPrint = " " + itemQtyToPrint;
                            }
                        }
                    }
                    String itemRateToPrint = cursor.getString(2);
                    itemRateToPrint = itemRateToPrint.replaceFirst("^0+(?!$)",
                            "");
                    if (itemRateToPrint.contains(".")) {
                        itemRateToPrint += "000";
                        itemRateToPrint = itemRateToPrint.substring(0,
                                itemRateToPrint.lastIndexOf(".") + 3);
                    } else {
                        itemRateToPrint += ".00";
                    }

                    if (itemRateToPrint.length() <= 7) {
                        int diff = 7 - itemRateToPrint.length();
                        if (diff > 0) {
                            for (int i = 0; i < diff; i++) {
                                itemRateToPrint = " " + itemRateToPrint;
                            }
                        }
                    }
                    String itemAmountToPrint = cursor.getString(3);
                    itemAmountToPrint = itemAmountToPrint.replaceFirst(
                            "^0+(?!$)", "");

                    if (itemAmountToPrint.contains(".")) {
                        itemAmountToPrint += "000";
                        itemAmountToPrint = itemAmountToPrint.substring(0,
                                itemAmountToPrint.lastIndexOf(".") + 3);
                    } else {
                        itemAmountToPrint += ".00";
                    }

                    total += Double.parseDouble(itemAmountToPrint.trim());
                    if (itemAmountToPrint.length() <= 10) {
                        int diff = 10 - itemAmountToPrint.length();
                        if (diff > 0) {
                            for (int i = 0; i < diff; i++) {
                                itemAmountToPrint = " " + itemAmountToPrint;
                            }
                        }
                    }

                    msg += itemNameToPrint + itemQtyToPrint + itemRateToPrint
                            + itemAmountToPrint + "\n("
                            +cursor.getString(4)+ ")\n";
                } while (cursor.moveToNext());

                cursor.close();
                db.close();
                db1.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        msg += "--------------------------------\n";

        String amountToPrint = String.format("%.2f", /*input)Double.toString(*/total);
        //String amountToPrint = String.valueOf(Amount) ;
        if (amountToPrint.length() <= 26) {
            int diff = 26 - amountToPrint.length();
            if (diff > 0) {
                for (int i = 0; i <= diff; i++) {
                    amountToPrint = " " + amountToPrint;
                }
            }
        }

        msg += "Total" + amountToPrint + "\n";
        msg += "--------------------------------\n\n";
        String soft = "Software By :";
        if (soft.length() <= 32) {
            int diff = 32 - soft.length();
            for (int i = 0; i < diff / 2; i++) {
                soft = " " + soft;
            }
        }
        String company = "Vritti Solutions Ltd.";
        if (company.length() <= 32) {
            int diff = 32 - company.length();
            for (int i = 0; i < diff / 2; i++) {
                company = " " + company;
            }
        }
        msg += soft+"\n"+company+"\n";
        msg += "       --- Thank You --- \n";



        if (msg.length() > 0) {
            mService.sendMessage(msg + "\n", "GBK");
        }
    }

    private void scanBluetooth() {

        startActivityForResult(new Intent(parent, DeviceListActivity.class),
                AdatSoftData.REQUEST_CONNECT_DEVICE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // count = 0;

        if (mService.isBTopen() == false) {
            Intent enableIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent,
                    AdatSoftData.REQUEST_ENABLE_BT);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.bluetoothmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        switch (id) {
            case R.id.bluetooth:
                scanBluetooth();
                break;
            default:
                break;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void connectDevice() {
        // TODO
        String address = utility.getBluetoothAddress(parent);
        if (address != null) {
            con_dev = mService.getDevByMac(address);
            mService.connect(con_dev);
            Log.e("Auto connected", "state : " + mService.getState());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case AdatSoftData.REQUEST_ENABLE_BT:
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(parent, "Bluetooth open successful",
                            Toast.LENGTH_LONG).show();
                } else {
                    // finish();
                }
                break;
            case AdatSoftData.REQUEST_CONNECT_DEVICE:
                if (resultCode == Activity.RESULT_OK) {
                    String address = data.getExtras().getString(
                            DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    utility.clearTable(parent, "Bluetooth_Address");
                    db1.AddBluetooth(address);
                    con_dev = mService.getDevByMac(address);
                    mService.connect(con_dev);
                    Log.e("bluetooth state", "state : " + mService.getState());
                }

                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mService != null)
            mService.stop();
        mService = null;
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg1) {
            switch (msg1.what) {
                case BluetoothService.MESSAGE_STATE_CHANGE:
                    switch (msg1.arg1) {
                        case BluetoothService.STATE_CONNECTED:
                            Toast.makeText(parent, "Connect successful",
                                    Toast.LENGTH_SHORT).show();
                            deviceConnected = true;

                            break;
                        case BluetoothService.STATE_CONNECTING: // ��������
                            //Log.d("��������", "��������.....");
                            break;
                        case BluetoothService.STATE_LISTEN: // �������ӵĵ���
                        case BluetoothService.STATE_NONE:
                            //Log.d("��������", "�ȴ�����.....");
                            break;
                    }
                    break;
                case BluetoothService.MESSAGE_CONNECTION_LOST: // �����ѶϿ�����
                    Toast.makeText(parent, "Device connection was lost",
                            Toast.LENGTH_SHORT).show();
                    deviceConnected = false;
                    break;
                case BluetoothService.MESSAGE_UNABLE_CONNECT: // �޷������豸
                    Toast.makeText(parent, "Unable to connect device",
                            Toast.LENGTH_SHORT).show();
                    deviceConnected = false;
                    break;
            }
        }
    };


}
