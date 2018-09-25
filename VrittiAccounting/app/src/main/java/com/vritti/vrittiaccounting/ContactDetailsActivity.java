package com.vritti.vrittiaccounting;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hsalf.smilerating.BaseRating;
import com.hsalf.smilerating.SmileRating;
import com.squareup.picasso.Picasso;
import com.vritti.vrittiaccounting.data.AdatSoftConstants;
import com.vritti.vrittiaccounting.data.AdatSoftData;
import com.vritti.vrittiaccounting.data.utility;
import com.vritti.vrittiaccounting.database.DatabaseHelper;
import com.vritti.vrittiaccounting.services.BalanceDetailsBean;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Admin-3 on 7/22/2017.
 */

public class ContactDetailsActivity extends AppCompatActivity {

    Context parent;
    int icondrawable;
    DatabaseHelper db1;
    ImageView next_arrow;
    LinearLayout balanceLayout;
    String userName = null, password = null ;
    FloatingActionButton fab_call,fab_msg,fab_whatsapp;
    TextView tvcontname,tvcontmob,tvcontcity,tvcontbal,tvcontdate;
    ProgressBar pb15, pb30, pb60, pb90, pb90plus, pbtotal;
    TextView textView5_0_15, textView5_16_30, textView5_31_60, textView5_61_90,textView5_m_90,textView5_Total_bal;
    ImageView textView5_Bal_Avg, add_contact;
    String name_mar,category,Name,City,ac_type,Balance,mobile,Bal_cd,updatedate,Acno,name_mar_title;

    EditText edt_password,edt_username, edt_password_1,edt_username_1;
    Button btn_submit, btn_submit_1;
    AppCompatCheckBox checkBox_show, checkBox_show_1;
    AlertDialog alertDialog;
    String pass,user;


    private int PERMISSION_REQUEST_CODE_READ_CONTACTS = 1;

    private int PERMISSION_REQUEST_CODE_WRITE_CONTACTS = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_details);
        GetIntentExtras();
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //getSupportActionBar().setIcon(icondrawable);
        getSupportActionBar().setTitle(Name);
        initView();
        setListner();
    }

    private void GetIntentExtras() {
        Bundle bundle = getIntent().getExtras();
        if( bundle != null){
            name_mar_title = (String) bundle.get("name_mar_title");
            category = (String) bundle.get("category");
            Acno = (String) bundle.get("Acno");
            Name = (String) bundle.get("Name");
            City = (String) bundle.get("City");
            ac_type = (String) bundle.get("ac_type");
            Balance = (String) bundle.get("Balance");
            mobile = (String) bundle.get("mobile");
            Bal_cd = (String) bundle.get("Bal_cd");
            updatedate = (String) bundle.get("updatedate");
            if (category.equals("Cash")){
                icondrawable = R.drawable.cash;
                name_mar = "Cash";
            }else if (category.equals("Bank")){
                icondrawable = R.drawable.bank;
                name_mar = "Bank";
            }else if (category.equals("Supp")){
                icondrawable = R.drawable.supplier;
                name_mar = "Supplier";
            } else if (category.equals("Cust")){
                icondrawable = R.drawable.customer;
                name_mar = "Customer";
            }else if (category.equals("Hund")){
                icondrawable = R.drawable.bus;
                name_mar = "Transporter";
            }else if (category.equals("Other")){
                icondrawable = R.drawable.other_reciept;
                name_mar = "Other";
            }

        }
    }

    private void initView() {
        parent = ContactDetailsActivity.this;
        db1 = new DatabaseHelper(parent);
        balanceLayout = (LinearLayout) findViewById(R.id.balancelayoutid);
        next_arrow = findViewById(R.id.next_arrow);
        tvcontname = findViewById(R.id.tvcontname);
        tvcontmob = findViewById(R.id.tvcontmob);
        tvcontcity = findViewById(R.id.tvcontcity);
        tvcontbal = findViewById(R.id.tvcontbal);
        tvcontdate = findViewById(R.id.tvcontdate);
        tvcontname.setText(Name);
        tvcontmob.setText(mobile);
        tvcontcity.setText(City);
        tvcontbal.setText("Balance \u20B9 "+getcurrencyformat(Balance)+" "+getCorD(Bal_cd));
        tvcontdate.setText("As on "+getDateinFormat(updatedate));
        fab_call = findViewById(R.id.fab_call);
        fab_msg = findViewById(R.id.fab_msg);
        fab_whatsapp = findViewById(R.id.fab_whatsapp);
        if (category.equals("Supp") ||category.equals("Cust")||category.equals("Hund")||category.equals("Bank")||category.equals("Other")){
            balanceLayout.setVisibility(View.VISIBLE);
           //balanceLayout.setVisibility(View.GONE);
            /*balanceLayout2.setVisibility(View.VISIBLE);
            smileRating = (SmileRating) findViewById(R.id.smile_rating);
            smileRating.setOnClickListener(null);
            smileRating.setOnSmileySelectionListener(null);
            smileRating.setOnRatingSelectedListener(null);*/

            textView5_0_15 = (TextView) findViewById(R.id.textView5_0_15);
            textView5_16_30 = (TextView) findViewById(R.id.textView5_16_30);
            textView5_31_60 = (TextView) findViewById(R.id.textView5_31_60);
            textView5_61_90 = (TextView) findViewById(R.id.textView5_61_90);
            textView5_m_90 = (TextView) findViewById(R.id.textView5_m_90);
            textView5_Total_bal = (TextView) findViewById(R.id.textView5_Total_bal);
            pb15 = (ProgressBar) findViewById(R.id.customProgress1);
            pb30 = (ProgressBar) findViewById(R.id.customProgress2);
            pb60 = (ProgressBar) findViewById(R.id.customProgress3);
            pb90 = (ProgressBar) findViewById(R.id.customProgress4);
            pb90plus = (ProgressBar) findViewById(R.id.customProgress5);
            /*pbtotal = (ProgressBar) findViewById(R.id.customProgress6);*/
            textView5_Bal_Avg = (ImageView) findViewById(R.id.textView5_Bal_Avg);
            add_contact = (ImageView) findViewById(R.id.ivcontmob);
            add_contact.setVisibility(View.GONE);
            getdaywisebalance();

            if(mobile!=null&&!mobile.equals("")) {
                Log.e("Contact",mobile);
                if(!hasPhoneContactsPermission(android.Manifest.permission.READ_CONTACTS))
                {
                    requestPermission(android.Manifest.permission.READ_CONTACTS, PERMISSION_REQUEST_CODE_READ_CONTACTS);
                }else
                {
                    checkContact();
                }

            }
        } else {
            balanceLayout.setVisibility(View.GONE);
           // balanceLayout2.setVisibility(View.GONE);
        }



    }

    // Check whether user has phone contacts manipulation permission or not.
    private boolean hasPhoneContactsPermission(String permission)
    {
        boolean ret = false;

        // If android sdk version is bigger than 23 the need to check run time permission.
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            // return phone read contacts permission grant status.
            int hasPermission = ContextCompat.checkSelfPermission(getApplicationContext(), permission);
            // If permission is granted then return true.
            if (hasPermission == PackageManager.PERMISSION_GRANTED) {
                ret = true;
            }
        }else
        {
            ret = true;
        }
        return ret;
    }

    // Request a runtime permission to app user.
    private void requestPermission(String permission, int requestCode)
    {
        String requestPermissionArray[] = {permission};
        ActivityCompat.requestPermissions(this, requestPermissionArray, requestCode);
    }

    // After user select Allow or Deny button in request runtime permission dialog
    // , this method will be invoked.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        int length = grantResults.length;
        if(length > 0)
        {
            int grantResult = grantResults[0];

            if(grantResult == PackageManager.PERMISSION_GRANTED) {

                if(requestCode==PERMISSION_REQUEST_CODE_READ_CONTACTS)
                {
                    // If user grant read contacts permission.
                    checkContact();
                }else if(requestCode==PERMISSION_REQUEST_CODE_WRITE_CONTACTS)
                {
                    // If user grant write contacts permission then start add phone contact activity.
                    //addContact(Name,mobile);
                    newaddContact(Name,mobile);
                }
            }else
            {
                Toast.makeText(getApplicationContext(), "You denied permission.", Toast.LENGTH_LONG).show();
            }
        }
    }


    private void checkContact() {
        String phoneCheck = "No";
        ContentResolver resolver = getContentResolver();
        Cursor cursor = resolver.query(ContactsContract.Contacts.CONTENT_URI,null,null,null,null);
        cursor.moveToFirst();
        while (cursor.moveToNext()){
            String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            Log.e("Contact",id +" = "+name);

            Cursor phoneCursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID+" = ?",new String[] { id },null);
            while (phoneCursor.moveToNext()){
                String phoneNo = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                phoneNo=phoneNo.replaceAll("\\s+", "");
                Log.e("Contact",phoneNo);
                    if (phoneNo.trim().equalsIgnoreCase(mobile)){
                        phoneCheck ="Yes";
                        break;
                    }

            }
            if(phoneCheck.equalsIgnoreCase("Yes")){
                break;
            }
        }
        if(phoneCheck.equalsIgnoreCase("Yes")){
            add_contact.setVisibility(View.GONE);
           // Toast.makeText(parent,"Contact available",Toast.LENGTH_SHORT).show();
        }else if(phoneCheck.equalsIgnoreCase("No")){
            add_contact.setVisibility(View.VISIBLE);
            //Toast.makeText(parent,"Contact not available",Toast.LENGTH_SHORT).show();
        }
    }

    private int calbalance(String total, String balance) {
        double tot = Double.parseDouble(total);
        double bal = Double.parseDouble(balance);
        double indicate = tot-(tot-bal);
        float f = (float) ((indicate*100)/tot);
        int i  = (int) (f);

        return i;
    }


    private String getcurrencyformat(String balance) {
        String parts[] = balance.split("\\.");
        int i  = Integer.parseInt(parts[0]);
        DecimalFormat formatter = new DecimalFormat("##,##,##,###");
        String yourFormattedString = formatter.format(i);
        return yourFormattedString+"."+parts[1];
    }
    private String getcurrencyformate(String balance) {
        //String parts[] = balance.split("\\.");
        int i  = Integer.parseInt(balance);
        DecimalFormat formatter = new DecimalFormat("##,##,##,###");
        String yourFormattedString = formatter.format(i);
        return yourFormattedString/*+"."+parts[1]*/;
    }

    private void addContact(String name, String phone) {
        ContentValues values = new ContentValues();
        values.put(Contacts.People.NUMBER, phone);
        values.put(Contacts.People.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_CUSTOM);
        values.put(Contacts.People.LABEL, name);
        values.put(Contacts.People.NAME, name);
        Uri dataUri = getContentResolver().insert(Contacts.People.CONTENT_URI, values);
        Uri updateUri = Uri.withAppendedPath(dataUri, Contacts.People.Phones.CONTENT_DIRECTORY);
        values.clear();
        values.put(Contacts.People.Phones.TYPE, Contacts.People.TYPE_MOBILE);
        values.put(Contacts.People.NUMBER, phone);
        updateUri = getContentResolver().insert(updateUri, values);
        Toast.makeText(parent,"Contact Added to PhoneBook",Toast.LENGTH_SHORT).show();
        checkContact();
    }

    private void newaddContact(String name, String phone) {
        // Creates a new Intent to insert a contact
        Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
        // Sets the MIME type to match the Contacts Provider
        intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);

        /*
         * Inserts new data into the Intent. This data is passed to the
         * contacts app's Insert screen
         */
        // Inserts an email address
        intent.putExtra(ContactsContract.Intents.Insert.NAME, name)
        /*
         * In this example, sets the email type to be a work email.
         * You can set other email types as necessary.
         */
                //.putExtra(ContactsContract.Intents.Insert.EMAIL_TYPE, CommonDataKinds.Email.TYPE_WORK)
                // Inserts a phone number
                .putExtra(ContactsContract.Intents.Insert.PHONE, phone)
        /*
         * In this example, sets the phone type to be a work phone.
         * You can set other phone types as necessary.
         */
                .putExtra(ContactsContract.Intents.Insert.PHONE_TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_WORK);

        /* Sends the Intent
         */
        startActivity(intent);

        /*ContentValues values = new ContentValues();
        values.put(Contacts.People.NUMBER, phone);
        values.put(Contacts.People.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_CUSTOM);
        values.put(Contacts.People.LABEL, name);
        values.put(Contacts.People.NAME, name);
        Uri dataUri = getContentResolver().insert(Contacts.People.CONTENT_URI, values);
        Uri updateUri = Uri.withAppendedPath(dataUri, Contacts.People.Phones.CONTENT_DIRECTORY);
        values.clear();
        values.put(Contacts.People.Phones.TYPE, Contacts.People.TYPE_MOBILE);
        values.put(Contacts.People.NUMBER, phone);
        updateUri = getContentResolver().insert(updateUri, values);
        Toast.makeText(parent,"Contact Added to PhoneBook",Toast.LENGTH_SHORT).show();
        checkContact();*/

    }

    private void getdaywisebalance() {
        try{
            DatabaseHelper db1 = new DatabaseHelper(getBaseContext());
            SQLiteDatabase sql = db1.getWritableDatabase();
            Cursor cursor = sql.rawQuery("Select * from "+AdatSoftConstants.TABLE_AGING_BALANCE+AdatSoftData.Selected_Sno+
                    " where acno ='"+Acno+"'", null);
            cursor.moveToFirst();
            if (cursor != null && cursor.getCount()>0) {
                textView5_0_15.setText(getcurrencyformate(cursor.getString(cursor.getColumnIndex("Bal_0_15"))));
                textView5_16_30.setText(getcurrencyformate(cursor.getString(cursor.getColumnIndex("Bal_16_30"))));
                textView5_31_60.setText(getcurrencyformate(cursor.getString(cursor.getColumnIndex("Bal_31_60"))));
                textView5_61_90.setText(getcurrencyformate(cursor.getString(cursor.getColumnIndex("Bal_61_90"))));
                textView5_m_90.setText(getcurrencyformate(cursor.getString(cursor.getColumnIndex("Bal_m_90"))));
                textView5_Total_bal.setText(getcurrencyformate(cursor.getString(cursor.getColumnIndex("Total_Bal"))));

                pb15.setProgress(calbalance(cursor.getString(cursor.getColumnIndex("Total_Bal")),
                        cursor.getString(cursor.getColumnIndex("Bal_0_15"))));
                pb30.setProgress(calbalance(cursor.getString(cursor.getColumnIndex("Total_Bal")),
                        cursor.getString(cursor.getColumnIndex("Bal_16_30"))));
                pb60.setProgress(calbalance(cursor.getString(cursor.getColumnIndex("Total_Bal")),
                        cursor.getString(cursor.getColumnIndex("Bal_31_60"))));
                pb90.setProgress(calbalance(cursor.getString(cursor.getColumnIndex("Total_Bal")),
                        cursor.getString(cursor.getColumnIndex("Bal_61_90"))));
                pb90plus.setProgress(calbalance(cursor.getString(cursor.getColumnIndex("Total_Bal")),
                        cursor.getString(cursor.getColumnIndex("Bal_m_90"))));
               // pbtotal.setProgress(Integer.parseInt(cursor.getString(cursor.getColumnIndex("Total_Bal"))));

                float rating = Float.parseFloat(cursor.getString(cursor.getColumnIndex("Bal_Avg")));
                if(rating <= 1.00) {
                    textView5_Bal_Avg.setImageResource(R.drawable.rate1);
                }else if(rating <= 2.00) {
                    textView5_Bal_Avg.setImageResource(R.drawable.rate2);
                }else if(rating <= 3.00) {
                    textView5_Bal_Avg.setImageResource(R.drawable.rate3);
                }else if(rating <= 4.00) {
                    textView5_Bal_Avg.setImageResource(R.drawable.rate4);
                }else if(rating <= 5.00) {
                    textView5_Bal_Avg.setImageResource(R.drawable.rate5);
                }


                if(rating <= 5.00 && rating >= 4.00){
                    /*Animation myFadeInAnimation = AnimationUtils.loadAnimation(ContactDetailsActivity.this, R.anim.clockspin);
                    fab_call.startAnimation(myFadeInAnimation);*/
                    Animation myRotation = AnimationUtils.loadAnimation(ContactDetailsActivity.this, R.anim.heartbeat);
                    fab_call.startAnimation(myRotation);
                }


                cursor.getString(cursor.getColumnIndex("acno"));
            }else{
                balanceLayout.setVisibility(View.GONE);
                //balanceLayout2.setVisibility(View.GONE);
                cursor.close();
                sql.close();
                db1.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void setListner() {
        balanceLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(parent,BalanceLedgerActivity.class);
                Bundle extras = new Bundle();
                extras.putString("name_mar_title", Name);
                extras.putString("Acno", Acno);
                intent.putExtras(extras);
                startActivity(intent);
                //finish();
            }
        });

        add_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!hasPhoneContactsPermission(android.Manifest.permission.WRITE_CONTACTS))
                {
                    requestPermission(android.Manifest.permission.WRITE_CONTACTS, PERMISSION_REQUEST_CODE_WRITE_CONTACTS);
                }else
                {
                    //addContact(Name,mobile);
                    newaddContact(Name,mobile);
                }

            }
        });

        fab_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation myAnim = AnimationUtils.loadAnimation(parent, R.anim.clockspin);
                fab_call.startAnimation(myAnim);
                fab_call.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        String num = mobile;

                        Intent callIntent = new Intent(Intent.ACTION_DIAL);
                        callIntent.setData(Uri.parse("tel:" + num));
                        startActivity(callIntent);
                        finish();
                    }
                }, 200);
            }
        });

        fab_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*getPassword();
                if (password.equals("N/A")||password.equals(null)) {
                    openDialog();
                } else {*/
                    //PasswordopenDialog();
                Animation myAnim = AnimationUtils.loadAnimation(parent, R.anim.clockspin);
                fab_msg.startAnimation(myAnim);
                fab_msg.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                    Intent intent = new Intent(parent, MessagingActivity.class);
                    Bundle extras = new Bundle();
                    extras.putString("name_mar_title", name_mar_title);
                    extras.putString("category", category);
                    extras.putString("Acno", Acno);
                    extras.putString("Name", Name);
                    extras.putString("City", City);
                    extras.putString("ac_type", ac_type);
                    extras.putString("Balance", getcurrencyformat(Balance));
                    extras.putString("mobile", mobile);
                    extras.putString("Bal_cd",Bal_cd);
                    extras.putString("updatedate", updatedate);
                    intent.putExtras(extras);
                    startActivity(intent);
               // }
                    }
                }, 200);
            }
        });


        fab_whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation myAnim = AnimationUtils.loadAnimation(parent, R.anim.clockspin);
                fab_whatsapp.startAnimation(myAnim);
                fab_whatsapp.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        PackageManager pm=getPackageManager();
                        try {
                            Intent i = new Intent(Intent.ACTION_SEND);
                            i.setType("text/plain");
                            i.putExtra(Intent.EXTRA_SUBJECT, "My application name");
                            String TextMsg = "Your outstanding as on : "+getDateinFormat(updatedate)+" is \u20B9 "+getcurrencyformat(Balance)+" "+getCorD(Bal_cd)+
                                    ".\n Please arrange for the payment. Ignore if already paid. Thank you." +
                                    "\n Contact : "+getFirmName()+"\n Phone : "+getFirmMob();
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
                }, 200);
            }
        });

    }


    private String getFirmName() {
        String result= null;
        try{
            DatabaseHelper db1 = new DatabaseHelper(getBaseContext());
            SQLiteDatabase sql = db1.getWritableDatabase();
            Cursor cursor = sql.rawQuery("Select name_mar from "+ AdatSoftConstants.TABLE_FIRM+" WHERE shop_no='"+AdatSoftData.Selected_Sno+"'", null);
            cursor.moveToFirst();
            if (cursor != null && cursor.getCount()>0) {
                result = cursor.getString(cursor.getColumnIndex("name_mar"));
            }else{
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }
    private String getFirmMob() {
        String result= null;
        try{
            DatabaseHelper db1 = new DatabaseHelper(getBaseContext());
            SQLiteDatabase sql = db1.getWritableDatabase();
            Cursor cursor = sql.rawQuery("Select mobile from "+ AdatSoftConstants.TABLE_FIRM+" WHERE shop_no='"+AdatSoftData.Selected_Sno+"'", null);
            cursor.moveToFirst();
            if (cursor != null && cursor.getCount()>0) {
                result = cursor.getString(cursor.getColumnIndex("mobile"));
            }else{
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
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

        edt_username = subView.findViewById(R.id.edt_username);
        edt_password = subView.findViewById(R.id.edt_password);
        btn_submit = subView.findViewById(R.id.btn_submit);
        checkBox_show = subView.findViewById(R.id.checkbox_show);
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
                        Intent intent = new Intent(parent, MessagingActivity.class);
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
                        startActivity(intent);
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
    private void PasswordopenDialog() {
        LayoutInflater inflater = LayoutInflater.from(parent);
        View subView1 = inflater.inflate(R.layout.password_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(subView1);

        edt_password_1 = subView1.findViewById(R.id.edt_password_1);
        edt_username_1 = subView1.findViewById(R.id.edt_username_1);
        btn_submit_1 = subView1.findViewById(R.id.btn_submit_1);
        checkBox_show_1 = subView1.findViewById(R.id.checkbox_show_1);
        try {

            btn_submit_1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String uname = edt_username_1.getText().toString();
                    String pwd = edt_password_1.getText().toString();

                    System.out.println("password_a" + password);
                    System.out.println("password_a" + userName);

                    if (pwd.equals(password) && uname.equals(userName)) {
                        Toast.makeText(parent, "Login Successfully", Toast.LENGTH_SHORT).show();
                        alertDialog.dismiss();
                       // Toast.makeText(parent, "WIP", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(parent, MessagingActivity.class);
                        Bundle extras = new Bundle();
                        extras.putString("name_mar_title", name_mar_title);
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
                    startActivity(intent);
                   // finish();
                    } else {
                        Toast.makeText(parent, "Invalid Password", Toast.LENGTH_SHORT).show();


                    }
                }
            });
        }catch(Exception e){
            e.printStackTrace();
        }
        alertDialog = builder.create();

        alertDialog.show();

        checkBox_show_1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int start, end;
                Log.i("inside checkbox chnge", "" + isChecked);
                if (!isChecked) {
                    start = edt_password_1.getSelectionStart();
                    end = edt_password_1.getSelectionEnd();
                    edt_password_1.setTransformationMethod(new PasswordTransformationMethod());
                    edt_password_1.setSelection(start, end);
                    checkBox_show_1.setText("Show Password");
                } else {
                    start = edt_password_1.getSelectionStart();
                    end = edt_password_1.getSelectionEnd();
                    edt_password_1.setTransformationMethod(null);
                    edt_password_1.setSelection(start, end);
                    checkBox_show_1.setText("Hide Password");
                }
            }

        });
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
}
