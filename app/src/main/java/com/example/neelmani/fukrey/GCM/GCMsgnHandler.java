package com.example.neelmani.fukrey.GCM;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class GCMsgnHandler {

    private static Context contextReceived;
    private static final String PREF_GCM_REG_ID = "PREF_GCM_REG_ID";
    public static SharedPreferences prefs;
    // Your project number and web server url. Please change below.
    private static final String GCM_SENDER_ID = "152975918294";
    private static final String WEB_SERVER_URL = "http://192.168.1.9/TestService/Service1.svc/LogMessage";

    static GoogleCloudMessaging gcm;

    private static final int GCM_ACTION_PLAY_SERVICES_DIALOG = 100;
    protected static final int MESSAGE_REGISTER_WITH_GCM = 101;
    protected static final int MESSAGE_REGISTER_WEB_SERVER = 102;
    protected static final int MESSAGE_REGISTER_WEB_SERVER_SUCCESS = 103;
    protected static final int MESSAGE_REGISTER_WEB_SERVER_FAILURE = 104;
    private static String gcmRegId;

    public static void GenerateGCMInfo( Context contextPassed) {

       contextReceived=contextPassed;
        if (isGooglePlayInstalled()) {
                    gcm = GoogleCloudMessaging.getInstance(contextReceived.getApplicationContext());
                        handler.sendEmptyMessage(MESSAGE_REGISTER_WITH_GCM);
                }
    }

    public static String GetGCMInfo( Context contextPassed) {
        contextReceived=contextPassed;
        if (isGooglePlayInstalled()) {
           //TODO If working-Remove this line-> gcm = GoogleCloudMessaging.getInstance(contextReceived.getApplicationContext());
            // Read saved registration id from shared preferences.
            gcmRegId = getSharedPreferences().getString(PREF_GCM_REG_ID, "");
        }
        return gcmRegId;
    }

    private static boolean isGooglePlayInstalled() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(contextReceived);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, (Activity)contextReceived,
                        GCM_ACTION_PLAY_SERVICES_DIALOG).show();
            } else {
                Toast.makeText(contextReceived.getApplicationContext(),
                        "Google Play Service not installed",
                        Toast.LENGTH_SHORT).show();
                // TODO: TO Resolve finish --> finish();
            }
            return false;
        }
        return true;

    }

    private static SharedPreferences getSharedPreferences() {
        if (prefs == null) {
            prefs = contextReceived.getApplicationContext().getSharedPreferences(
                    "AndroidGCMFukrey", Context.MODE_PRIVATE);
        }
        return prefs;
    }

    public static void saveInSharedPref(String result) {
        // TODO Auto-generated method stub
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(PREF_GCM_REG_ID, result);
        editor.apply();
    }

    static Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MESSAGE_REGISTER_WITH_GCM:
                    new GCMRegistrationAsyncTask().execute();
                    break;
                case MESSAGE_REGISTER_WEB_SERVER:
                    new WebServerRegistrationAsyncTask().execute();
                    break;
                case MESSAGE_REGISTER_WEB_SERVER_SUCCESS:
                    Toast.makeText(contextReceived.getApplicationContext(),
                            "registered with the web server", Toast.LENGTH_LONG).show();
                    break;
                case MESSAGE_REGISTER_WEB_SERVER_FAILURE:
                    Toast.makeText(contextReceived.getApplicationContext(),
                            "registration with the web server failed",
                            Toast.LENGTH_LONG).show();
                    break;
            }
        };
    };

    private static class GCMRegistrationAsyncTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            // TODO Auto-generated method stub
            if (gcm == null && isGooglePlayInstalled()) {
                gcm = GoogleCloudMessaging.getInstance(contextReceived.getApplicationContext());
            }
            try {
                gcmRegId = gcm.register(GCM_SENDER_ID);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return gcmRegId;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                Toast.makeText(contextReceived.getApplicationContext(), "registered with GCM",
                        Toast.LENGTH_LONG).show();
                //regIdView.setText(result);
                saveInSharedPref(result);
                handler.sendEmptyMessage(MESSAGE_REGISTER_WEB_SERVER);
            }
        }

    }

    private static class WebServerRegistrationAsyncTask extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            String result="default";
            StringBuilder sb = new StringBuilder();

            try {

                URL url = new URL(WEB_SERVER_URL);// editText.getText());
                urlConnection = (HttpURLConnection) url.openConnection();
                // set the hedear to get the data in JSON formate
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setRequestProperty("Content-type", "application/json");
                urlConnection.setRequestMethod("POST");


                // BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                //get the response

                //Create JSONObject here
                if (true){//null != MainActivity.currentLocation) {
                    //String lat = String.valueOf(MainActivity.currentLocation.getLatitude());
                    //String lng = String.valueOf(MainActivity.currentLocation.getLongitude());

                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put("UserID", "Dummy_User_ID");
                    jsonParam.put("MessageReceived", "Dummy MessageRegId- "+gcmRegId);
                    jsonParam.put("LatitudeReceived","Duumy lat");
                    jsonParam.put("LongitudeReceived", "Duumy lng");
                    jsonParam.put("TimeStamp", "Duumy lastUpdateTime");
                    jsonParam.put("Accuracy","Duumy currentLocation.getAccuracy()");

                    DataOutputStream printout = new DataOutputStream(urlConnection.getOutputStream());
                    String str = jsonParam.toString();
                    byte[] data = str.getBytes("UTF-8");
                    printout.write(data);
                    printout.flush();
                    printout.close();


                    int HttpResult = urlConnection.getResponseCode();
                    if (HttpResult == HttpURLConnection.HTTP_OK) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(
                                urlConnection.getInputStream(), "utf-8"));
                        String line = null;
                        while ((line = br.readLine()) != null) {
                            sb.append(line + "\n");
                        }
                        br.close();
                        result=sb.toString();

                        result=sb.toString();


                    } else {
                        result = "ELSE -" + urlConnection.getResponseMessage();
                    }
                }
                else
                {
                    //Location is null.... DO Something.....
                }
            }
            catch (MalformedURLException e)
            {
                result="MalformedURLException_"+e.getMessage();
                e.printStackTrace();
            }
            catch (IOException e)
            {
                result="IOException_-"+e.getMessage();
                e.printStackTrace();
            }
            catch (JSONException e)
            {
                result="JSONException_"+e.getMessage();
                e.printStackTrace();
            }
            catch (Exception e)
            {
                result="Exception_"+e.getMessage();
                e.printStackTrace();
            }
            finally
            {
                if(urlConnection!=null)
                    urlConnection.disconnect();
            }

            return result;
        }

      /*  @Override
        protected Void doInBackground(Void... params) {
            URL url = null;
            try {
                url = new URL(WEB_SERVER_URL);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                handler.sendEmptyMessage(MESSAGE_REGISTER_WEB_SERVER_FAILURE);
            }
            Map<String, String> dataMap = new HashMap<String, String>();
            dataMap.put("regId", gcmRegId);

            StringBuilder postBody = new StringBuilder();
            Iterator iterator = dataMap.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry param = (Entry) iterator.next();
                postBody.append(param.getKey()).append('=')
                        .append(param.getValue());
                if (iterator.hasNext()) {
                    postBody.append('&');
                }
            }
            String body = postBody.toString();
            byte[] bytes = body.getBytes();

            HttpURLConnection conn = null;
            try {
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setUseCaches(false);
                conn.setFixedLengthStreamingMode(bytes.length);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type",
                        "application/x-www-form-urlencoded;charset=UTF-8");

                OutputStream out = conn.getOutputStream();
                out.write(bytes);
                out.close();

                int status = conn.getResponseCode();
                if (status == 200) {
                    // Request success
                    handler.sendEmptyMessage(MESSAGE_REGISTER_WEB_SERVER_SUCCESS);
                } else {
                    throw new IOException("Request failed with error code "
                            + status);
                }
            } catch (ProtocolException pe) {
                pe.printStackTrace();
                handler.sendEmptyMessage(MESSAGE_REGISTER_WEB_SERVER_FAILURE);
            } catch (IOException io) {
                io.printStackTrace();
                handler.sendEmptyMessage(MESSAGE_REGISTER_WEB_SERVER_FAILURE);
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }

            return null;
        }
        */

    }

}
