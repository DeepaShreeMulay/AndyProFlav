package com.vritti.vrittiaccounting;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings.Secure;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.vritti.vrittiaccounting.data.AdatSoftData;
import com.vritti.vrittiaccounting.data.NetworkUtils;
import com.vritti.vrittiaccounting.data.WakeLocker;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;


public class SplashActivity extends AppCompatActivity {
	private Context parent;
	private ImageView layout;
	//private ImageView splashImageView, vrittiCreatingWaves;
	public static DefaultHttpClient httpClient = new DefaultHttpClient();
	public static final String REG_ID = "regId";
	//GoogleCloudMessaging gcm;
	public static String regId,ProjectID;
	private static final String APP_VERSION = "appVersion";
	public static final String MyPREFERENCES = "LoginPrefs";
	SharedPreferences sharedpreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);

		if(Constants.type == Constants.Type.PETROKONNECT) {
			AdatSoftData.MODULE = "PETRO";
		} else if(Constants.type == Constants.Type.ADATKONNECT) {
			AdatSoftData.MODULE = "ADAT";
		}
		//AdatSoftData.MODULE = "PETRO";

		if (AdatSoftData.MODULE.equals("ADAT")){
			AdatSoftData.MODULE_TITLE = "AdatKonnect";
			AdatSoftData.ProjectID = "com.vritti.vrittiaccounting.adatKonnect";
		} else if (AdatSoftData.MODULE.equals("PETRO")){
			AdatSoftData.MODULE_TITLE = "PetroKonnect";
			AdatSoftData.ProjectID = "com.vritti.vrittiaccounting.petroKonnect";
		} else if (AdatSoftData.MODULE.equals("VRITTI")) {
			AdatSoftData.MODULE_TITLE = "AIMS";
		}

		setContentView(R.layout.activity_splash);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		//getSupportActionBar().setLogo(R.drawable.logo_gray_small);
		getSupportActionBar().setTitle(AdatSoftData.MODULE_TITLE);

		layout =(ImageView)findViewById(R.id.imageview_splash_vworkbench);


		if (AdatSoftData.MODULE.equals("ADAT")){
			layout.setBackgroundResource(R.mipmap.adat);
		} else if (AdatSoftData.MODULE.equals("PETRO")){
			layout.setBackgroundResource(R.drawable.petrosoft);
		} else if (AdatSoftData.MODULE.equals("VRITTI")) {
			layout.setBackgroundResource(R.drawable.icon_aims);
		}

		sharedpreferences = getSharedPreferences(MyPREFERENCES,
				Context.MODE_PRIVATE);
		initialize();

		AdatSoftData.DEVICE_ID = Secure.getString(getBaseContext()
				.getContentResolver(), Secure.ANDROID_ID);
		//registerGCM();
		regId = "APA91bGLHoalw3ZzW1OkJYdLt-r7of3Fx0UIOh9iFI6kCyV-IOoAaM0yxorChduO-4woQzOfsdxfxyfkS89Dh7A30UEUmxTPg5_aeLn1X7e29hWQ3aNjx98_qGTR1lTNhz4VF2y9yT4O";
		registerReceiver(mHandleMessageReceiver, new IntentFilter(
				AdatSoftData.DISPLAY_MESSAGE_ACTION));

		new Thread() {
			public void run() {

				try {
					Animation myAnim = AnimationUtils.loadAnimation(parent, R.anim.bounce_slow1);
					layout.startAnimation(myAnim);

					//applyAnimations();
					sleep(3 * 1000);
					/*if (AdatSoftData.MODULE.equals("VRITTI")) {
						startActivity(new Intent(parent,
								Main2Activity.class));
					} else {*/
						startActivity(new Intent(parent,
								RegistrationActivity.class));
					//}


						finish();
				} catch (Exception e) {
				}
			}
		}.start();
	}



	private static int getAppVersion(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			Log.d("test", "I never expected this! Going down, going down!" + e);
			throw new RuntimeException(e);
		}
	}

	public static Date getDateTime(String rtime) {
		Date date= new Date();
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			date = dateFormat.parse(rtime);
		}catch (Exception e){
			e.printStackTrace();
		}
		return date;
	}

	private void storeRegistrationId(Context context, String regId) {
		final SharedPreferences prefs = getSharedPreferences(
				SplashActivity.class.getSimpleName(), Context.MODE_PRIVATE);
		int appVersion = getAppVersion(context);
		Log.i("test", "Saving regId on app version " + appVersion);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(REG_ID, regId);
		editor.putInt(APP_VERSION, appVersion);
		editor.commit();
	}

	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String newMessage = intent.getExtras().getString(
					AdatSoftData.EXTRA_MESSAGE);
			// Waking up mobile if it is sleeping
			WakeLocker.acquire(getApplicationContext());



			// Showing received message
			// lblMessage.append(newMessage + "\n");
			/*Toast.makeText(getApplicationContext(),
					"New Message: " + newMessage, Toast.LENGTH_LONG).show();*/

			// Releasing wake lock
			WakeLocker.release();
		}
	};

	@Override
	protected void onDestroy() {
		// Cancel AsyncTask

		try {
			// Unregister Broadcast Receiver
			unregisterReceiver(mHandleMessageReceiver);

			// Clear internal resources.

		} catch (Exception e) {
			//Log.e("UnRegister Receiver Error", "> " + e.getMessage());
		}
		super.onDestroy();
	}
	private void applyAnimations() {
		new AnimationUtils();
		Animation animation = AnimationUtils.loadAnimation(parent,
				R.anim.appear);
		animation.reset();
		final Animation animation1 = AnimationUtils.loadAnimation(parent,
				R.anim.appear);
		animation1.reset();


		animation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {

			}
		});
	}
	private void initialize() {
		parent = SplashActivity.this;
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
	}


	public static String OpenConnection(String url) {
		String res = null;
		try {
			URL url1 = new URL(url);
			HttpGet httppost = new HttpGet(url);
			HttpResponse response = httpClient.execute(httppost);

			HttpEntity entity = response.getEntity();
			res = EntityUtils.toString(entity);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return res;
	}

	public static Object OpenPostConnection(String url, String FinalObj) {
		String res = null;
		Object response = null;
		try {
			URL url1 = new URL(url);
			HttpPost httppost = new HttpPost(url.toString());
			StringEntity se = new StringEntity(FinalObj.toString());
			httppost.setEntity(se);
			httppost.setHeader("Accept", "application/json");
			httppost.setHeader("Content-type", "application/json");
			ResponseHandler responseHandler = new BasicResponseHandler();
			response = httpClient.execute(httppost, responseHandler);
			Log.i("Common Data", response + "");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return response;
	}
}
