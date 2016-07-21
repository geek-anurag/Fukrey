package com.example.neelmani.fukrey;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.neelmani.fukrey.CommentHandlers.CommentActivity;
import com.example.neelmani.fukrey.Fragments.AreaUpdateFragment;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Neelmani on 16-Aug-15.
 */
public class SendAndReceiveMessage {

    private String endpointUri;
    JSONObject jsonParam;
    ProgressDialog progress;
    AreaUpdateFragment objAUF=new AreaUpdateFragment();
    CommentActivity objCA=new CommentActivity();

    public void ExecuteWebQuery(JSONObject jsonParam , String endpointUri ,  ProgressDialog progress)
    {
        this.jsonParam=jsonParam;
        this.endpointUri=endpointUri;
        this.progress=progress;
        WebQuery objWQ = new WebQuery();
        objWQ.execute();
        if(this.progress!=null)
            this.progress.show();

    }

    private class WebQuery extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            String result="default";
            StringBuilder sb = new StringBuilder();

            Log.d("SendAndReceiveMessage", "doInBackground ...............................");

            try {
                URL url = new URL(endpointUri);// editText.getText());
                urlConnection = (HttpURLConnection) url.openConnection();
                // set the hedear to get the data in JSON formate
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setRequestProperty("Content-type", "application/json");
                urlConnection.setRequestMethod("POST");

                // BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                //get the response

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
            System.out.println("InWebQuery..............3 Result: "+result );
            return result;
        }

        @Override
        protected void onPostExecute(String response) {
            //  textView2.setText(response);
            //textView2.setText(this.exc;
            String responseType="Default";
            boolean isResponseReceived;
            JSONObject obj;
            try {
                obj = new JSONObject(response);

                responseType =obj.getString("ResponseType");
                isResponseReceived=true;
                switch(responseType)
                {
                    case "SignUpStatus":
                        MainActivity.ExecuteSignUpResult(obj);
                        break;
                    case "AreaUpdate":
                        objAUF.ExecuteRefreshResultAUF(obj);
                        break;
                    case "Post":
                        MainActivity.ExecutePostResult(obj);
                        break;
                    case "UpdateAgreeDisagree":
                        MainActivity.ExecuteUpdateAgreeDisagree(obj);
                        break;
                    case "UpdateProfileImage":
                        MainActivity.ExecuteUpdateProfileImage(obj);
                        break;
                    case "FetchComments":
                        objCA.ExecuteFetchCommentResult(obj);
                        break;
                    case "PostComments":
                        objCA.ExecutePostCommentResult(obj);
                        break;
                    case "Refresh":
                        objAUF.ExecuteRefreshResultAUF(obj);
                        break;
                    case "DeleteMessage":
                        Toast.makeText(MainActivity.context, "Post Deleted", Toast.LENGTH_SHORT).show();
                        break;
                    default :
                       Toast.makeText(MainActivity.context, "Connection Failed !!!", Toast.LENGTH_SHORT).show();

                }


               /* JSONArray array =obj.getJSONArray("UpdateData");

                for(int i = 0 ; i < array.length() ; i++) {
                    array.getJSONObject(i).getString("UerName");
                }*/
                }
            catch (Exception e) {
                e.printStackTrace();
                isResponseReceived=false;
            }

            HandleProgressBar();
            if(!isResponseReceived)
                Toast.makeText(MainActivity.context, "Connection Failed !!", Toast.LENGTH_SHORT).show();

        }


    }

    private void HandleProgressBar()
    {
        if(this.progress!=null)
            this.progress.dismiss();
    }
}
