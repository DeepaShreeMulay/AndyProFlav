package com.vritti.vrittiaccounting;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

import com.vritti.vrittiaccounting.data.AdatSoftData;


public class IncomingSmsReceiver extends BroadcastReceiver {

	SharedPreferences sharedpreferences;
	public static final String MyPREFERENCES = "AdatSoft";
	final SmsManager sms = SmsManager.getDefault();

	public void onReceive(Context context, Intent intent) {
		sharedpreferences = context.getSharedPreferences(MyPREFERENCES,
				Context.MODE_PRIVATE);
		final Bundle bundle = intent.getExtras();
		Log.e("InSmsReceiver", "got intent");
		try {
			if (bundle != null) {
				final Object[] pdusObj = (Object[]) bundle.get("pdus");
				for (int i = 0; i < pdusObj.length; i++) {
					SmsMessage currentMessage = SmsMessage
							.createFromPdu((byte[]) pdusObj[i]);
					String phoneNumber = currentMessage
							.getDisplayOriginatingAddress();
					String senderNum = phoneNumber;
					String message = currentMessage.getDisplayMessageBody();
					String msg = message;
					Log.e("InSmsReceiver", "senderNum: " + senderNum
							+ "; message: " + message);
					if ((senderNum.contains("Vritti")||senderNum.contains("VRITTI")||senderNum.contains("vritti"))){

						if (message.contains("OTP")) {

							AdatSoftData.OTP_MSG = msg.replaceAll("\\D+","");
							SharedPreferences.Editor editor = sharedpreferences.edit();
							editor.putString("otpfrommsg", AdatSoftData.OTP_MSG);
							editor.commit();
							ConfirmOTPActivity.editTextOTP.setText(AdatSoftData.OTP_MSG);
							//ConfirmOTPActivity.editTextOTP.setText(message.replaceAll("[^0-9]", ""));
						}
					}
				}
			}
		} catch (Exception e) {
			Log.e("SmsReceiver", "Exception smsReceiver" + e);
		}
	}
}