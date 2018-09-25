package com.vritti.vrittiaccounting.data;

import java.util.ArrayList;

/**
 * Created by sharvari on 9/22/2016.
 */
public class AdatSoftData {


    public static String SESSION_ID = null;
    public static String HANDLE = null;
    public static String MOBILE = null;
    public static String Lno = null;
    public static String Sno = null;
    public static String Selected_Sno = null;
    public static String Selected_Acno = null;
    public static String OTP_INPUT = null;
    public static String OTP_MSG = null;
    public static int TransporterName = 1;
    public static int FarmerName = 2;
    public static int ItemName = 3;
    public static int BuyerName = 4;
    public static String  SENDER_ID="000";
    public static String DEVICE_ID = "";
    public static String EXTRA_MESSAGE= "";
    public static String DISPLAY_MESSAGE_ACTION="";
    //public static String URL = null;
    public static String URL ="http://ccs.ekatm.com";
    //http://ccs.ekatm.com/api/RegisterUserAPI/VerifyMobileUser?
    public static String TOKEN = null;
    public static String MODULE;
    public static String MODULE_TITLE;
    public static String ProjectID= null;


    public static final int REQUEST_ENABLE_BT = 11;
    public static final int REQUEST_CONNECT_DEVICE = 12;

    public static final String SERVER_URL = "http://qm.vritti.co.in:421/api/GetUrl?key=";

    public static final String URL_SOAP =  URL+"/Service/AdatSoftPost.asmx?wsdl";
    public static final String NAMESPACE = "http://tempuri.org/";
    public static final String METHOD_SAVERECT_BILL = "SaveRect_Bill";


    public static final String METHOD_SESSION_ACTIVATE_1 = "/api/SessionAPI/SesActivate1";

    public static final String METHOD_SESSION_ACTIVATE_2 = "/api/SessionAPI/SesActivate2";

    public static final String METHOD_SESSION_ACTIVATE_3 = "/api/SessionAPI/SesActivate3";

    public static final String METHOD_USER_REGISTRATION = "/api/RegisterUserAPI/VerifyMobileUser";

    public static final String METHOD_ACCOUNT_MASTER = "/api/AcMAst";

  //  http://192.168.1.207:420/api/GetDataAPI/GenData?sessionId=66803889&handler=0863132206245800&data=setup01
  public static final String METHOD_GET_DATA_COUNT = "/api/GetDataAPI/GenData";
    //http://192.168.1.207:420/api/GetDataAPI?from=10&to=2
    public static final String METHOD_GET_DATA = "/api/GetDataAPI";

    public static final String METHOD_API = "/api/GetDataAPI/GetData";
///api/GetDataAPI/GetData

    public static final String METHOD_API_NEW = "/api/GetDataAPI/GenData";

    public static final String METHOD_API_DATA = "/api/GetDataAPI?";

    public static final String METHOD_SAVE_DATA = "/api/SaveData?";

    public static final String METHOD_SMSTEMPLT = "/GetSMSTemplt?";

    public static final String METHOD_SENDSMS = "/SendSMS";



}
