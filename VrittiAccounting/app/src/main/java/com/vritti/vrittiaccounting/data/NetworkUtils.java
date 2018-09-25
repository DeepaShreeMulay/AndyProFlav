package com.vritti.vrittiaccounting.data;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@SuppressWarnings("deprecation")
public class NetworkUtils {
	public static boolean isNetworkAvailable(Context parent) {
		boolean haveConnectedMobile = false;
		boolean haveConnectedWifi = false;
		ConnectivityManager connectivityManager = (ConnectivityManager) parent
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
		for (NetworkInfo network : networkInfo) {
			if (network.getTypeName().equalsIgnoreCase("WIFI")) {
				if (network.isConnected()) {
					haveConnectedWifi = true;
				}
			}
			if (network.getTypeName().equalsIgnoreCase("MOBILE")) {
				if (network.isConnected()) {
					haveConnectedMobile = true;
				}
			}
		}
		// Log.i("isNetworkAvailable", ":"
		// + (haveConnectedWifi || haveConnectedMobile));
		return haveConnectedWifi || haveConnectedMobile;
	}

	/*public static StringBuilder getResponse(Context parent,
			List<NameValuePair> nameValuePairs, String url)*/ /*{
		InputStream inputStream;
		StringBuilder stringBuilder = null;
		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);
			if (nameValuePairs != null)
				httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse httpResponse = httpClient.execute(httpPost);
			inputStream = httpResponse.getEntity().getContent();
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(inputStream, "UTF-8"));
			if (httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				throw new Exception(httpResponse.getStatusLine()
						.getReasonPhrase());
			}
			stringBuilder = new StringBuilder();
			String str;
			while (true) {
				str = bufferedReader.readLine();
				if (str == null || str.length() == 0)
					break;
				stringBuilder.append(str);
			}
			bufferedReader.close();
			inputStream.close();

		} catch (ClientProtocolException e) {
			// TODO: handle exception
			Log.e("Client Protocol", "Log_tagNU");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO: handle exception
			Log.e("Log_tag", "IO Exception");
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e("Exception Response", "Exception");
			e.printStackTrace();
		}
		return stringBuilder;
	}*/

	public static Bitmap downloadUrl(String strUrl) throws IOException {
		Bitmap bitmap = null;
		InputStream iStream = null;
		try {
			URL url = new URL(strUrl);
			HttpURLConnection urlConnection = (HttpURLConnection) url
					.openConnection();
			urlConnection.connect();
			iStream = urlConnection.getInputStream();
			bitmap = BitmapFactory.decodeStream(iStream);
		} catch (Exception e) {
			//Log.d("Exception while downloading image from url", e.toString());
		} finally {
			iStream.close();
		}
		return bitmap;
	}
}
