package com.example.neelmani.fukrey.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.neelmani.fukrey.CommentHandlers.CommentActivity;
import com.example.neelmani.fukrey.DBHandler;
import com.example.neelmani.fukrey.BitmapHandler;
import com.example.neelmani.fukrey.LocationManager;
import com.example.neelmani.fukrey.MainActivity;
import com.example.neelmani.fukrey.MessageHandlers.MessageAdapter;
import com.example.neelmani.fukrey.MessageHandlers.MessageDetails;
import com.example.neelmani.fukrey.R;
import com.example.neelmani.fukrey.SendAndReceiveMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

    public class AreaUpdateFragment extends Fragment {
    private static ListView listView;
    private static MessageAdapter adapter;
    private View myFragmentView;
    public static int currentRow;
    private static int lastLine;
    private static DBHandler db;
    private static BitmapHandler objBH;
    private static final String TAG = "AreaMessageActivity";

    private final static String FETCH_UPDATE_URI = "http://192.168.1.9/FukreyBase1/Service1.svc/FetchUpdate";
    private final static String FETCH_ROW_ID_UPDATE_URI = "http://192.168.1.9/FukreyBase1/Service1.svc/FetchUpdateWithRowID";

    /*private final static String FETCH_UPDATE_URI = "http://neel911-001-site1.dtempurl.com/Service1.svc/FetchUpdate";
    private final static String FETCH_ROW_ID_UPDATE_URI = "http://neel911-001-site1.dtempurl.com/Service1.svc/FetchUpdateWithRowID";
*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        db =new DBHandler(MainActivity.context);
        objBH=new BitmapHandler();

        lastLine=10;

        myFragmentView = inflater.inflate(R.layout.fragment_area_update, container, false);
        listView = (ListView) myFragmentView.findViewById(R.id.MessageContainer);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                MessageDetails item = (MessageDetails) parent.getItemAtPosition(position);
                //Create intent
                String addressInfo=item.getAddress()!=""?item.getAddress()+"\n"+item.getLSC():item.getLSC();
                Intent intent = new Intent(MainActivity.context, CommentActivity.class);
                intent.putExtra("userName", item.getUserName());
                intent.putExtra("title", item.getMessage());
                intent.putExtra("rowId", item.getMessageId());
                intent.putExtra("agreeUsers", item.getAgree());
                intent.putExtra("disagreeUsers", item.getDisagree());
                intent.putExtra("postInfo", item.getTimeStamp()+"\n"+item.getDistance()+"\n"+addressInfo);

                startActivity(intent);

            }
        });

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                        && (listView.getLastVisiblePosition() - listView.getHeaderViewsCount() -
                        listView.getFooterViewsCount()) >= (adapter.getCount() - 1)) {

                    lastLine += 10;
                    RefreshAUF(-1);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                currentRow = firstVisibleItem;
            }
        });

       //region Display History
        ArrayList<MessageDetails> messageDetails=db.getAllMessageDetails(lastLine);
        for(MessageDetails messageDetail : messageDetails) {
            messageDetail.setBitmapImage(objBH.getBitmapFromBytes(messageDetail.getImage()));

        }
        SetListView(messageDetails);
        //endregion

        return myFragmentView;
    }

    public static void RefreshAUF(long rowId)
    {
        JSONObject jsonParam = new JSONObject();
        try {
            if (null != LocationManager.currentLocation ) {

                String lat = String.valueOf(LocationManager.currentLocation.getLatitude());
                String lng = String.valueOf(LocationManager.currentLocation.getLongitude());
                String acc = String.valueOf(LocationManager.currentLocation.getAccuracy());

                jsonParam.put("Accuracy", acc);
                jsonParam.put("EmailID", MainActivity.getSharedPreferences().getString(MainActivity.PREF_EMAIL_ID, ""));
                jsonParam.put("VersatileCarrier", "Refresh");
                jsonParam.put("LastLine", lastLine);
                jsonParam.put("LatitudeReceived", lat);
                jsonParam.put("LongitudeReceived", lng);
                jsonParam.put("Range", Integer.parseInt(MainActivity.getSharedPreferences().getString(MainActivity.PREF_RANGE, "")));
                jsonParam.put("RowID", rowId);
                jsonParam.put("State", "Active");
                jsonParam.put("TimeStamp", System.currentTimeMillis());

                SendAndReceiveMessage objSARM = new SendAndReceiveMessage();
                if(rowId==-1) {
                    objSARM.ExecuteWebQuery(jsonParam, FETCH_UPDATE_URI, null);
                    Toast.makeText(MainActivity.context, "Refresh...", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    objSARM.ExecuteWebQuery(jsonParam, FETCH_ROW_ID_UPDATE_URI, null);
                    Toast.makeText(MainActivity.context, "Refresh..."+rowId, Toast.LENGTH_SHORT).show();
                }
            } else {
               if(!LocationManager.isFristTimeLocationChanged)
                   Toast.makeText(MainActivity.context, "Unable to get device location :( Please check current settings", Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void ExecuteRefreshResultAUF(JSONObject obj)
    {
        String output="Dummy";
        try {
                db.deleteMessageDetails();
                JSONArray array = obj.getJSONArray("UpdateData");
                String distance="",dateTime="";
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

            if (null != LocationManager.currentLocation ) {
                Double lat = LocationManager.currentLocation.getLatitude();
                Double lng = LocationManager.currentLocation.getLongitude();

                for (int i = array.length() - 1; i >= 0; i--) {
                   //region  Generate Distance Information
                    distance = "Posted " + CalculateDistance(lat, lng,
                            Double.parseDouble(array.getJSONObject(i).getString("Latitude")), Double.parseDouble(array.getJSONObject(i).getString("Longitude"))) + " kms away";

                  String lastEditedDistance=array.getJSONObject(i).getString("LastEditedDistance");
                    if (!TextUtils.isEmpty(lastEditedDistance))
                        distance+=" | Last edited "+lastEditedDistance+ " kms away";
                    //endregion
                    //region Calculate Time
                    dateTime = CalculateTime(df.parse(array.getJSONObject(i).getString("TimeStamp"))
                            ,(Calendar.getInstance().getTime()));

                    String lastEditedTime=array.getJSONObject(i).getString("LastEditedTime");
                    if (!TextUtils.isEmpty(lastEditedTime))
                        dateTime+=" | Last edited at "+CalculateTime(df.parse(lastEditedTime)
                                ,(Calendar.getInstance().getTime()));
                    //endregion

                    db.addMessageDetails(new MessageDetails(array.getJSONObject(i).getString("CommentID")
                            , array.getJSONObject(i).getString("UerName")
                            , array.getJSONObject(i).getString("AgreeUsers")
                            , array.getJSONObject(i).getString("DisagreeUsers")
                            , array.getJSONObject(i).getString("Message")
                            , dateTime
                            , Base64.decode(array.getJSONObject(i).getString("Image").getBytes(), Base64.DEFAULT)
                            , array.getJSONObject(i).getString("Latitude")
                            , array.getJSONObject(i).getString("Longitude")
                            , array.getJSONObject(i).getString("Address")
                            , array.getJSONObject(i).getString("LSC")
                            ,distance));
                }

            }
                else {
                    Toast.makeText(MainActivity.context, "Unable to get device location :( Please check current settings", Toast.LENGTH_SHORT).show();
                }

            ArrayList<MessageDetails> messageDetails=db.getAllMessageDetails(lastLine);
            for(MessageDetails messageDetail : messageDetails) {
                messageDetail.setBitmapImage(objBH.getBitmapFromBytes(messageDetail.getImage()));
            }
             SetListView(messageDetails);

            }
        catch (Exception e) {
            output+=e.getMessage();
            e.printStackTrace();
        }
        System.out.println("InWebQuery.. post..............output: " + output);
    }

    public static void SetListView(ArrayList<MessageDetails> messageDetails)
    {
        adapter = new MessageAdapter((Activity)MainActivity.context, new ArrayList<MessageDetails>());
        listView.setAdapter(adapter);

        for(int i=0; i<messageDetails.size(); i++) {
            MessageDetails message = messageDetails.get(i);
            adapter.add(message);
        }
        adapter.notifyDataSetChanged();
        listView.setSelection(currentRow);

    }

    @Override
    public void onResume() {
        super.onResume();
        if (!TextUtils.isEmpty(MainActivity.isRegistered)) {
            RefreshAUF(-1);
        }
    }

    private String CalculateDistance(double rlat1, double rlong1, double rlat2, double rlong2)
    {
        double lat1 = ConvertToRadians(rlat1);
        double long1 = ConvertToRadians(rlong1);
        double lat2 = ConvertToRadians(rlat2);
        double long2 = ConvertToRadians(rlong2);

        double dlon = long1 - long2;
        double dlat = lat1 - lat2;

        double a = Math.pow(Math.sin(dlat / 2), 2) + (Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(dlon / 2), 2));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double result = c * 6371;

        return String.format("%.2f",result);
    }

    private String CalculateTime(Date date1,Date date2)
    {
        long diffInMillies=date2.getTime()-date1.getTime();
        if(diffInMillies<60000)
            return String.valueOf(TimeUnit.SECONDS.convert(diffInMillies,TimeUnit.MILLISECONDS))+" seconds ago";
        else if(diffInMillies<120000)
            return String.valueOf(TimeUnit.MINUTES.convert(diffInMillies,TimeUnit.MILLISECONDS))+" minute ago";
        else if(diffInMillies<3600000)
            return String.valueOf(TimeUnit.MINUTES.convert(diffInMillies, TimeUnit.MILLISECONDS))+" minutes ago";
        else if(diffInMillies<7200000)
            return String.valueOf(TimeUnit.HOURS.convert(diffInMillies, TimeUnit.MILLISECONDS))+" hour ago";
        else if(diffInMillies<86400000)
            return String.valueOf(TimeUnit.HOURS.convert(diffInMillies, TimeUnit.MILLISECONDS))+" hours ago";
        else if(diffInMillies<172800000)
            return String.valueOf(TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS))+" day ago";
      //  if(diffInMillies<604800000)
            return String.valueOf(TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS))+" days ago";

        //    return String.valueOf(date2.getTime());

    }

    public double ConvertToRadians(double angle)
    {
        return (Math.PI / 180) * angle;
    }

}
