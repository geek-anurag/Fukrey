package com.example.neelmani.fukrey.CommentHandlers;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.neelmani.fukrey.LocationManager;
import com.example.neelmani.fukrey.MainActivity;
import com.example.neelmani.fukrey.R;
import com.example.neelmani.fukrey.SendAndReceiveMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Anurag on 12/10/2015.
 */
public class CommentActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private static EditText commentsET;
    private static ListView commentsContiner;
    private static Button commentBtn;
    private static TextView txtuserName;
    private static CommentsAdapter adapter;
    private  static String title,userName;
    private  static long rowId;
    private static Context context;
    private static Menu menu;
    public static Dialog adDialog, editPostDialog;
    public static String agreeUsers;
    public static String disagreeUsers;
    public static String deviceEmailId;
    public static String postInfo;
    private static boolean isFirstTime;
    private static int indexFrom;
    private static int indexTo;
    private final static String FETCH_COMMENTS_URI="http://192.168.1.9/FukreyBase1/Service1.svc/FetchComments";
    private final static String POST_COMMENT_URI="http://192.168.1.9/FukreyBase1/Service1.svc/PostComment";
    private final static String EDIT_POST_URI = "http://192.168.1.9/FukreyBase1/Service1.svc/EditMessage";


    /*private final static String FETCH_COMMENTS_URI="http://neel911-001-site1.dtempurl.com/Service1.svc/FetchComments";
    private final static String POST_COMMENT_URI="http://neel911-001-site1.dtempurl.com/Service1.svc/PostComment";
    private final static String EDIT_POST_URI="http://neel911-001-site1.dtempurl.com/Service1.svc/EditMessage";
*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
       getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initControls();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_comment, menu);
        CommentActivity.menu = menu;

        commentsContiner.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {
                Toast.makeText(context, "Comment Long Clicked..", Toast.LENGTH_SHORT).show();

                return true;
            }
        });

        if(deviceEmailId.equals(userName))
            menu.getItem(2).setVisible(true);
        else
            menu.getItem(2).setVisible(false);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.action_show_agree:
                indexFrom=0;
                indexTo=49;
                callADUsersDialog("ShowAgree");
                return  true;
            case R.id.action_show_disagree:
                indexFrom=0;
                indexTo=49;
                callADUsersDialog("ShowDisagree");
                return  true;
            case R.id.action_edit_post:
                callEditPostDialog();
                return  true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initControls() {

        deviceEmailId= MainActivity.getSharedPreferences().getString(MainActivity.PREF_EMAIL_ID, "");

        commentsContiner = (ListView) findViewById(R.id.CommentsContainer);
        commentsET = (EditText) findViewById(R.id.commentEdit);
        commentBtn = (Button) findViewById(R.id.commentsButton);
        txtuserName = (TextView) findViewById(R.id.txtuserName);
        context=this;

        title = getIntent().getStringExtra("title");
        userName= getIntent().getStringExtra("userName");
        rowId=Long.parseLong(getIntent().getStringExtra("rowId"));
        agreeUsers=getIntent().getStringExtra("agreeUsers");
        disagreeUsers=getIntent().getStringExtra("disagreeUsers");
        postInfo=getIntent().getStringExtra("postInfo");

        txtuserName.setText(userName);
        isFirstTime=true;
        FetchComments(rowId);

        commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = commentsET.getText().toString();
                if (TextUtils.isEmpty(messageText)) {
                    return;
                }

                DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                String date = df.format(Calendar.getInstance().getTime());
                JSONObject jsonParam = new JSONObject();
                try {
                    if (null != LocationManager.currentLocation ) {
                        System.out.println("InGCMIntentService.............................FetchUpdate-1");

                        jsonParam.put("EmailID",  MainActivity.getSharedPreferences().getString(MainActivity.PREF_EMAIL_ID, ""));
                        jsonParam.put("LastLine", 1);
                        jsonParam.put("Accuracy", "NotSent");//LocationManager.currentLocation.getAccuracy());
                        jsonParam.put("LatitudeReceived", LocationManager.currentLocation.getLatitude());
                        jsonParam.put("LongitudeReceived", LocationManager.currentLocation.getLongitude());
                        jsonParam.put("Range", MainActivity.getSharedPreferences().getString(MainActivity.PREF_RANGE, ""));
                        jsonParam.put("RowID", rowId);
                        jsonParam.put("State", "Active");
                        jsonParam.put("TimeStamp", date);
                        jsonParam.put("VersatileCarrier",messageText );

                        SendAndReceiveMessage objSARM = new SendAndReceiveMessage();
                        objSARM.ExecuteWebQuery(jsonParam, POST_COMMENT_URI, null);
                        Toast.makeText(MainActivity.context, "loading comments", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(MainActivity.context, "Unable to get location.", Toast.LENGTH_SHORT).show();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Comments msg = new Comments();
              //  chatMessage.setId(122);//dummy
                msg.setMessage(messageText);
                msg.setMe(true);
                msg.setIsInfo(false);
                msg.setTitle(false);

                commentsET.setText("");

                displayMessage(msg);
            }
        });
    }

    public static void FetchComments(long rowId)
    {

        //region load and display titile message & location
        ArrayList<Comments> commentHistory=new ArrayList<Comments>();
        Comments msg = new Comments();
        msg.setId(1);
        msg.setMe(false);
        msg.setTitle(false);
        msg.setIsInfo(true);
        msg.setMessage(postInfo);
        commentHistory.add(msg);

        Comments msg2 = new Comments();
        msg2.setId(2);
        msg2.setMe(false);
        msg2.setTitle(true);
        msg2.setIsInfo(false);
        msg2.setMessage(title);
        commentHistory.add(msg2);
        loadHistory(commentHistory);
        //endregion

        JSONObject jsonParam = new JSONObject();
        try {
               jsonParam.put("EmailID", MainActivity.getSharedPreferences().getString(MainActivity.PREF_EMAIL_ID, ""));
                jsonParam.put("VersatileCarrier", "FetchComment");
                jsonParam.put("LastLine", 1);
                jsonParam.put("RowID", rowId);
                jsonParam.put("State", "Active");
                jsonParam.put("TimeStamp", System.currentTimeMillis());

                SendAndReceiveMessage objSARM = new SendAndReceiveMessage();
                    objSARM.ExecuteWebQuery(jsonParam, FETCH_COMMENTS_URI, null);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void ExecuteFetchCommentResult(JSONObject obj)
    {
        final ArrayList<Comments> commentHistory = new ArrayList<>();
        try {
            String selfId=MainActivity.getSharedPreferences().getString(MainActivity.PREF_EMAIL_ID, "");
            Comments msg = new Comments();
            msg.setId(1);
            msg.setMe(false);
            msg.setTitle(false);
            msg.setIsInfo(true);
            msg.setMessage(postInfo);
            msg.setDate(DateFormat.getDateTimeInstance().format(new Date()));
            commentHistory.add(msg);

            Comments msg2 = new Comments();
            msg2.setId(2);
            msg2.setMe(false);
            msg2.setTitle(true);
            msg2.setIsInfo(false);
            msg2.setMessage(title);
            msg2.setDate(DateFormat.getDateTimeInstance().format(new Date()));
            commentHistory.add(msg2);

            JSONArray array =obj.getJSONArray("UpdateData");

            for(int i = array.length()-1 ; i >= 0 ; i--) {
                Comments msg1 = new Comments();
                msg1.setId(i + 3);
                if(array.getJSONObject(i).getString("EmailID").equals(selfId))
                    msg1.setMe(true);
                else
                    msg1.setMe(false);
                msg1.setMessage(array.getJSONObject(i).getString("Message"));
                msg1.setTitle(false);
                msg.setIsInfo(false);
                msg1.setDate(array.getJSONObject(i).getString("TimeStamp"));
                commentHistory.add(msg1);
             }
            loadHistory(commentHistory);
            isFirstTime=false;
            } catch (Exception e) {
           e.printStackTrace();
        }

    }

    public void ExecutePostCommentResult(JSONObject obj)
    {
        try {
            String status= obj.getString("Status").toString();
            if(status.equals("Success")) {
                Toast.makeText(context, "Posted."+status, Toast.LENGTH_SHORT).show();
                FetchComments(rowId);
            }
            else
                Toast.makeText(context, "Unable to post comment. "+status, Toast.LENGTH_SHORT).show();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void displayMessage(Comments message) {
        adapter.add(message);
        adapter.notifyDataSetChanged();
        if(!isFirstTime)
        scroll();
    }

    private static void scroll() {
        commentsContiner.setSelection(commentsContiner.getCount() - 1);
    }

    private static void loadHistory(ArrayList<Comments> commentHistory){

        adapter = new CommentsAdapter((Activity) context, new ArrayList<Comments>());
        commentsContiner.setAdapter(adapter);

        for(int i=0; i<commentHistory.size(); i++) {
            Comments message = commentHistory.get(i);
            displayMessage(message);
        }
    }

    private void callADUsersDialog(String actionReceived)
    {
        final String action=actionReceived;
        adDialog = new Dialog(this);
        adDialog.setContentView(R.layout.agree_disagree_users);
        adDialog.setCancelable(false);
        Button btnNext = (Button) adDialog.findViewById(R.id.btnNext);
        Button btnOk = (Button) adDialog.findViewById(R.id.btnOk);
        Button btnPrev = (Button) adDialog.findViewById(R.id.btnPrev);
        final TextView tvUsers = (TextView) adDialog.findViewById(R.id.tvUsers);
        final TextView tvHeader = (TextView) adDialog.findViewById(R.id.tvHeader);
       if(action=="ShowAgree")
{
    String users= getUserNamesSorted(agreeUsers);
    if(users.isEmpty())
        users="No One Agrees Till Now ! ";
    tvHeader.setText("Agreed Users ("+indexFrom+"-"+indexTo+")");
    tvUsers.setText(users);
}
        else
{
    String users= getUserNamesSorted(disagreeUsers);
    if(users.isEmpty())
        users="No One Disagrees Till Now ! ";
    tvHeader.setText("Disagreed Users("+indexFrom+"-"+indexTo+")");
    tvUsers.setText(users);
}
        adDialog.getWindow().getAttributes().windowAnimations = R.style.PostDialogAnimation;
        adDialog.show();

              btnNext.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                      indexFrom += 50;
                      indexTo += 50;
                      adDialog.dismiss();
                      callADUsersDialog(action);
                  }
              });

        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                indexFrom -= 50;
                indexTo -= 50;
                adDialog.dismiss();
                callADUsersDialog(action);
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adDialog.dismiss();
            }
        });

    }

    private void callEditPostDialog()
    {
        editPostDialog = new Dialog(this);
        editPostDialog.setContentView(R.layout.post);
        editPostDialog.setCancelable(false);
        TextView tvHeader=(TextView) editPostDialog.findViewById(R.id.tvHeader);
        Button btnPost = (Button) editPostDialog.findViewById(R.id.btnPost);
        Button btnCancel = (Button) editPostDialog.findViewById(R.id.btnCancel);
        final EditText etPost = (EditText) editPostDialog.findViewById(R.id.etPost);
        etPost.setText(title);
        etPost.setSelection(title.length());

        tvHeader.setText("Edit Post");

        editPostDialog.getWindow().getAttributes().windowAnimations = R.style.PostDialogAnimation;
        editPostDialog.show();

        btnPost.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //your login calculation goes here
                if (etPost.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please post something..!!", Toast.LENGTH_SHORT).show();
                } else {
                    ConnectivityManager cm=(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo activeNetwork=cm.getActiveNetworkInfo();
                    Boolean isConnected=activeNetwork!=null && activeNetwork.isConnectedOrConnecting();
                    if(!isConnected)
                        Toast.makeText(MainActivity.context, "Can't post.You are offline. Check Internet Connection", Toast.LENGTH_SHORT).show();
                    else {
                        JSONObject jsonParam = MainActivity.GetjsonWithCurrentDeatils(rowId, etPost.getText().toString(), true);
                        ProgressDialog progress = new ProgressDialog(context);
                        progress.setTitle("Updating");
                        progress.setMessage("Please wait while post is being updated...");
                        progress.setCancelable(false);
                        SendAndReceiveMessage objSARM = new SendAndReceiveMessage();
                        objSARM.ExecuteWebQuery(jsonParam, EDIT_POST_URI, progress);
                        editPostDialog.dismiss();
                    }
                }
                     }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editPostDialog.dismiss();
            }
        });

    }

    private String getUserNamesSorted(String users)
    {
        StringBuilder sb=new StringBuilder();
        String names[] =users.split(",");
        Arrays.sort(names);

        if(indexFrom<0) {
            indexFrom = 0;
            indexTo = 49;
        }
        if(indexTo>names.length-1)
            indexTo=names.length-1;

        if(indexFrom>=names.length-1)
            indexFrom=names.length-1-50;
        if(indexFrom<0)
            indexFrom = 0;


        for(int i = indexFrom; i <= indexTo; i++) {
            sb.append(names[i]);
            sb.append('\n');
        }
        return sb.toString();
    }

    @Override
    public void onResume() {
        super.onResume();
      }

}
