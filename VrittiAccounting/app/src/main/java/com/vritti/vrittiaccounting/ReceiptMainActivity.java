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
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.vritti.vrittiaccounting.data.AdatSoftConstants;
import com.vritti.vrittiaccounting.data.AdatSoftData;
import com.vritti.vrittiaccounting.data.NetworkUtils;
import com.vritti.vrittiaccounting.data.utility;
import com.vritti.vrittiaccounting.database.DatabaseHelper;
import com.zj.btsdk.BluetoothService;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Admin-3 on 10/12/2017.
 */

public class ReceiptMainActivity extends AppCompatActivity {

    String name_mar,category,Name,City,ac_type,Balance,mobile,Bal_cd,updatedate,Acno,name_mar_title,Module;
    EditText edt_rcvdamount,edt_rcvdkasar,edt_rcvdjali,edt_rcvdnarration;
    Button btnrcvdsave, btnrcvdcancel;
    Spinner spinner_paymode;
    TextView tvrcvdname;

    String userName = null, password = null ;
    EditText edt_password,edt_username, edt_password_1,edt_username_1;
    Button btn_submit, btn_submit_1;
    AppCompatCheckBox checkBox_show, checkBox_show_1;
    AlertDialog alertDialog;
    String pass,user;

    MessagingURL asynctask;
    String responsemsg="";

    EditText edt_mob;
    Button btn_confirm;
    AlertDialog alertDialog1;
    String confirmed_mobile ,TextMsg;
    String amount,kasar,jali,narration,pay_mode,total;

    utility ut;
    Context parent;
    DatabaseHelper db1;
    String CheckedResult;
    List<CharSequence> list = new ArrayList<CharSequence>();

    /*private static final int REQUEST_ENABLE_BT = 2;
    BluetoothService mService = null;
    BluetoothDevice con_dev = null;
    private static final int REQUEST_CONNECT_DEVICE = 1;*/

    String FirmName, FirmAdd, FirmMob;

    BluetoothService mService = null;
    BluetoothDevice con_dev = null;
    private boolean deviceConnected = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt_main);

        GetIntentExtras();
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Customer Receipt");
        initView();
        //scanBluetooth();
        setListner();
        connectDevice();

    }

    private void initView() {
        parent = ReceiptMainActivity.this;
        ut = new utility();
        //itemDetailsBeanArrayList = new ArrayList<ItemDetailsBean>();
        db1 = new DatabaseHelper(parent);
        tvrcvdname = (TextView) findViewById(R.id.tvrcvdname);
        edt_rcvdamount = (EditText) findViewById(R.id.edt_rcvdamount);
        edt_rcvdkasar = (EditText) findViewById(R.id.edt_rcvdkasar);
        edt_rcvdjali = (EditText) findViewById(R.id.edt_rcvdjali);
        spinner_paymode = (Spinner) findViewById(R.id.spinner_paymode);
        edt_rcvdnarration= (EditText) findViewById(R.id.edt_rcvdnarration);
        btnrcvdsave = (Button) findViewById(R.id.btnrcvdsave);
        btnrcvdcancel = (Button) findViewById(R.id.btnrcvdcancel);
        tvrcvdname.setText(Name);
        mService = new BluetoothService(parent, mHandler);

        if (mService.isAvailable() == false) {
            Toast.makeText(parent, "Bluetooth is not available",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void setListner() {
        btnrcvdsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //saveentrytolocal();
                amount = edt_rcvdamount.getText().toString();
                kasar = edt_rcvdkasar.getText().toString();
                jali = edt_rcvdjali.getText().toString();
                narration = edt_rcvdnarration.getText().toString();
                pay_mode = spinner_paymode.getSelectedItem().toString();
                total = String.valueOf(Double.parseDouble(amount) + Double.parseDouble(kasar));
                SaveReceipt();

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
        btnrcvdcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitfun();
            }
        });
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
                        //SaveReceipt();
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
        Intent I=new Intent(parent,ReceiptCustActivity.class);
        Bundle extras = new Bundle();
        extras.putString("category", "Cust");
        extras.putString("selected_shop_no",AdatSoftData.Selected_Sno );
        extras.putString("name_mar", name_mar);
        extras.putString("Module", Module);
        I.putExtras(extras);
        startActivity(I);
        finish();
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

    private void SaveReceipt() {

        SQLiteDatabase db = db1.getWritableDatabase();
        db.execSQL(AdatSoftConstants.CREATE_TABLE_Cust_Rect);
        db1.add_CustRect( getbilldate() , Acno , Name ,
                amount , kasar ,total  , jali,
                pay_mode , narration,"N" );
        Toast.makeText(parent, "Data added Successfully", Toast.LENGTH_SHORT).show();
    }

    private String getbilldate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("yyyy/MM/dd");
        String bill_date = mdformat.format(calendar.getTime());
        return bill_date;
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

        msg += "Date : " + datetime + "\n";
        String heading = "RECEIPT";
        if (heading.length() <= 32) {
            int diff = 32 - heading.length();
            for (int i = 0; i < diff / 2; i++) {
                heading = " " + heading;
            }
        }
        msg += Name+"\n";
        msg += "--------------------------------\n";
        msg += heading+"\n";
        msg += "--------------------------------\n";

        String text1 = amount;
        if (text1.length() <= 25) {
            int diff = 25 - text1.length();
            if (diff > 0) {
                for (int i = 0; i <= diff; i++) {
                    text1 = " " + text1;
                }
            }
        }
        msg += "Amount" + text1 + "\n";
        String text2 = kasar;
        if (text2.length() <= 26) {
            int diff = 26 - text2.length();
            if (diff > 0) {
                for (int i = 0; i <= diff; i++) {
                    text2 = " " + text2;
                }
            }
        }
        msg += "Kasar" + text2 + "\n";

        msg += "--------------------------------\n";
        String amountToPrint = total;
        if (amountToPrint.length() <= 26) {
            int diff = 26 - amountToPrint.length();
            if (diff > 0) {
                for (int i = 0; i <= diff; i++) {
                    amountToPrint = " " + amountToPrint;
                }
            }
        }

        msg += "TOTAL" + amountToPrint + "\n";
        msg += "--------------------------------\n\n";
        if (jali!=null&&!(jali.equalsIgnoreCase(""))&&(Double.parseDouble(jali)!=0)) {
            String text3 = jali;
            if (text3.length() <= 26) {
                int diff = 26 - text3.length();
                if (diff > 0) {
                    for (int i = 0; i <= diff; i++) {
                        text3 = " " + text3;
                    }
                }
            }
            msg += "Crate" + text3 + "\n";
        }
        String text4 = pay_mode;
        if (text4.length() <= 21) {
            int diff = 21 - text4.length();
            if (diff > 0) {
                for (int i = 0; i <= diff; i++) {
                    text4 = " " + text4;
                }
            }
        }
        msg += "Payment By" + text4 + "\n";
        String text5 = narration;
        /*if (text5.length() <= 32) {
            int diff = 32 - text5.length();
            if (diff > 0) {
                for (int i = 0; i <= diff; i++) {
                    text5 = " " + text5;
                }
            }
        }*/
        msg += text5 + "\n\n";
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

    private void whatsAppbill() {
        PackageManager pm=getPackageManager();
        try {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, "My application name");
            TextMsg = "Receipt:\nDear Customer, received amount Rs. "+total+" by "+pay_mode+" on "+getbilldate()+
                    "and your balance is Rs."+Balance+".\nNote- after bill completion consider the balance.\nThank You.";
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
        TextMsg = "Receipt:\nDear Customer, received amount Rs. "+total+" by "+pay_mode+" on "+getbilldate()+
                "and your balance is Rs."+Balance+".\nNote- after bill completion consider the balance.\nThank You.";
        ConfirmDialog();
    }

    private void getPassword() {
        try{
            DatabaseHelper db1 = new DatabaseHelper(getBaseContext());
            SQLiteDatabase sql = db1.getWritableDatabase();
            Cursor cursor = sql.rawQuery("Select * from "+ AdatSoftConstants.TABLE_FIRMWISE_USERNAME +
                    " where shop_no='"+ AdatSoftData.Selected_Sno+"'", null);
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

        }
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
