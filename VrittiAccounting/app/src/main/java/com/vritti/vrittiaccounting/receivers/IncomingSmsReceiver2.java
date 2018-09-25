package com.vritti.vrittiaccounting.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.vritti.vrittiaccounting.ConfirmOTPActivity;

public class IncomingSmsReceiver2 extends BroadcastReceiver {

	final SmsManager sms = SmsManager.getDefault();
	public static final String MyPREFERENCES = "LoginPrefs";

	public void onReceive(Context context, Intent intent) {
		final Bundle bundle = intent.getExtras();
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

					//Toast.makeText(context, msg, Toast.LENGTH_LONG).show();

					Log.e("SmsReceiver", "senderNum: " + senderNum
							+ "; message: " + message);
					if ((senderNum.contains("Vritti")||senderNum.contains("VRITTI")||senderNum.contains("vritti"))){

						if (message.contains("OTP")) {
							ConfirmOTPActivity.editTextOTP.setText(message.replaceAll("[^0-9]", ""));

							msg = msg.replaceAll("\\D+","");
							Log.e("Msg",msg);
							ConfirmOTPActivity.editTextOTP.setText(msg);
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