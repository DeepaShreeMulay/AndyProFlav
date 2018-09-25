package com.vritti.vrittiaccounting.data;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;

import com.vritti.vrittiaccounting.database.DatabaseHelper;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class utility {

	public static void showMessageDialog(final Context parent, String title,
										 String msg) {
		AlertDialog.Builder builder = new AlertDialog.Builder(parent);
		builder.setTitle(title);
		builder.setCancelable(false);
		builder.setMessage(msg);

		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int arg1) {
				dialog.dismiss();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}
	public void ErrLogFile() {
		SimpleDateFormat dff = new SimpleDateFormat("dd-MM-yyyy");
		String Logfile = dff.format(new Date());
		File file = new File(Environment.getExternalStorageDirectory()
				+ "/Logs", Logfile + ".txt");
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void addErrLog(String err) {
		SimpleDateFormat dff = new SimpleDateFormat("dd-MM-yyyy");
		String Logfile = dff.format(new Date());
		File file = new File(Environment.getExternalStorageDirectory()
				+ "/Vigil_Logs", Logfile + ".txt");
		if (file.exists()) {
			try {
				FileOutputStream fOut = new FileOutputStream(file, true);
				OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
				myOutWriter.append("\n" + "*" + err + "\n");
				myOutWriter.close();
				fOut.close();

			} catch (Exception e) {

			}
		}

	}

	public boolean checkErrLogFile() {
		SimpleDateFormat dff = new SimpleDateFormat("dd-MM-yyyy");
		String Logfile = dff.format(new Date());

		File Logsdir = new File(Environment.getExternalStorageDirectory()
				+ "/VigilLogs");

		if (!Logsdir.exists()) {
			Logsdir.mkdirs();
		}
		File file = new File(Environment.getExternalStorageDirectory()
				+ "/Logs", Logfile + ".txt");
		if (file.exists()) {
			return true;
		} else {
			return false;
		}

	}

	public static String httpGet(String urlString) throws IOException {
		URL url = new URL(urlString);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		// Check for successful response code or throw error
		// if (conn.getResponseCode() != 200) {
		// throw new IOException(conn.getResponseMessage());
		// }

		// Buffer the result into a string
		BufferedReader buffrd = new BufferedReader(new InputStreamReader(
				conn.getInputStream()));
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = buffrd.readLine()) != null) {
			sb.append(line);
		}

		buffrd.close();
		conn.disconnect();
		return sb.toString();
	}


	public NodeList getnode(String xml, String Tag) {

		Log.e("get node", " xml :" + xml + " tag: " + Tag);

		Document doc = null;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {

			DocumentBuilder db = dbf.newDocumentBuilder();

			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(xml));
			doc = db.parse(is);
			Log.e("get node", " doc: " + doc);

		} catch (ParserConfigurationException e) {
			Log.e("Error: ", e.getMessage());
			return null;
		} catch (SAXException e) {
			Log.e("Error: ", e.getMessage());
			return null;
		} catch (IOException e) {
			Log.e("Error: ", e.getMessage());
			return null;
		}
		// return DOM
		NodeList nl = doc.getElementsByTagName(Tag);
		Log.e("get node", " nl: " + nl);
		Log.e("get node", " nl len: " + nl.getLength());
		return nl;
	}

	public String getValue(Element e, String str) {
		NodeList n = e.getElementsByTagName(str);
		return this.getElementValue(n.item(0));
	}

	public final String getElementValue(Node elem) {
		Node child;
		if (elem != null) {
			if (elem.hasChildNodes()) {
				for (child = elem.getFirstChild(); child != null; child = child
						.getNextSibling()) {
					if (child.getNodeType() == Node.TEXT_NODE) {
						return child.getNodeValue();
					}
				}
			}
		}
		return "";
	}


	public boolean isnet(Context context) {

		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

	public String createMoney(String money) {
		double Money = Double.parseDouble(money);
		NumberFormat MoneyFormatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
		money = MoneyFormatter.format(Money);
		String mny[] = money.split("\\s+");
		money = mny[1];
		return (money);
	}

	public static String getBluetoothAddress(Context parent) {
		DatabaseHelper db1 = new DatabaseHelper(parent);
		SQLiteDatabase sql = db1.getWritableDatabase();
		Cursor cursor = sql.rawQuery("Select * from Bluetooth_Address", null);

		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			String str = cursor.getString(0);
			cursor.close();
			sql.close();
			db1.close();
			return str;

		} else {

			cursor.close();
			sql.close();
			db1.close();
			return null;
		}
	}

	public static void clearTable(Context parent, String tablename) {
		DatabaseHelper db = new DatabaseHelper(parent);
		SQLiteDatabase sql = db.getWritableDatabase();
		sql.delete(tablename, null, null);

		sql.close();
		db.close();
	}
}
