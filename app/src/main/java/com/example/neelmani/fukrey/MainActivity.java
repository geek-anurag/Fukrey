package com.example.neelmani.fukrey;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.neelmani.fukrey.Fragments.AreaUpdateFragment;
import com.example.neelmani.fukrey.GCM.GCMsgnHandler;
import com.example.neelmani.fukrey.MessageHandlers.MessageDetails;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private static final String TAG = "MainActivity";
    public static String gcmRegId;
    public static String isRegistered;
    public static LocationManager objLocationManager;
    public static Context context;
    public static boolean isActive;
    public static Dialog myDialog, postDialog, rangeDialog, selectImageDialog;
    private static Bitmap selectedProfileImage=null;
    private static Switch switchHideLocation;
    private final static String SIGN_UP_URI = "http://192.168.1.9/FukreyBase1/Service1.svc/RegisterUser";
    private final static String POST_URI = "http://192.168.1.9/FukreyBase1/Service1.svc/PostMessage";
    private final static String AgreeDisagree_URI = "http://192.168.1.9/FukreyBase1/Service1.svc/UpdateAgreeDisagree";
    private final static String UpdateProfileImage_URI = "http://192.168.1.9/FukreyBase1/Service1.svc/UpdateProfileImage";
    private final static String DeleteMessage_URI = "http://192.168.1.9/FukreyBase1/Service1.svc/DeleteMessage";

    /* private final static String SIGN_UP_URI = "http://neel911-001-site1.dtempurl.com/Service1.svc/RegisterUser";
    private final static String POST_URI = "http://neel911-001-site1.dtempurl.com/Service1.svc/PostMessage";
    private final static String AgreeDisagree_URI = "http://neel911-001-site1.dtempurl.com/Service1.svc/UpdateAgreeDisagree";
    private final static String UpdateProfileImage_URI = "http://neel911-001-site1.dtempurl.com/Service1.svc/UpdateProfileImage";
    private final static String DeleteMessage_URI = "http://neel911-001-site1.dtempurl.com/Service1.svc/DeleteMessage";
*/
    private static final String PREF_IS_REGISTERED = "PREF_IS_REGISTERED";
    public static final String PREF_EMAIL_ID = "PREF_EMAIL_ID";
    public static final String PREF_USER_NAME = "PREF_USER_NAME";
    public static final String PREF_RANGE = "PREF_RANGE";
    public static final String PREF_BITMAP_DP= "PREF_BITMAP_DP";
    public static final String PREF_SHOW_LOCATION= "PREF_SHOW_LOCATION";
    public static SharedPreferences prefs;
    private static Menu menu;

    private static int RESULT_LOAD_IMG = 1;
    String imgDecodableString;

    private static BitmapHandler objBH;
    private static DBHandler objDbh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context=this;

        objDbh=new DBHandler(this);
        objBH=new BitmapHandler();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

 //      getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        //show error dialog if GoolglePlayServices not available
        if (!isGooglePlayServicesAvailable()) {
            finish();
        }

        objLocationManager = new LocationManager(this);

        //region Get Registration Info
        gcmRegId = GCMsgnHandler.GetGCMInfo(this);
        if (TextUtils.isEmpty(gcmRegId)) {
            GCMsgnHandler.GenerateGCMInfo(MainActivity.context);
        }
        else
        {
            isRegistered = getSharedPreferences().getString(PREF_IS_REGISTERED, "");
            if (TextUtils.isEmpty(isRegistered)) {
                  callLoginDialog();
             }
        }
        //endregion
      }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        this.menu = menu;

        if(getSharedPreferences().getString(PREF_RANGE, "")=="")
            SetRange("1");
        else
            SetRange(null);
        SetDP();

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.action_post:
                callPostDialog();
                return  true;
            case R.id.action_refresh:
                AreaUpdateFragment.RefreshAUF(-1);
                return  true;
            case R.id.action_range:
                callRangeDialog();
                return  true;
            case R.id.action_settings:
                 Intent intent = new Intent(MainActivity.context, SettingsActivity.class);
                 startActivity(intent);
                 return  true;
            case R.id.action_select_dp:
                imgDecodableString=getSharedPreferences().getString(PREF_BITMAP_DP, "");
                if (imgDecodableString != "")
                   callSelectImageDialog();
               else
                    loadImagefromGallery();
                return  true;
            default:
                    return super.onOptionsItemSelected(item);
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new AreaUpdateFragment(), "My Area");
    //    adapter.addFragment(new FavouritesUpdateFragment(), "Friends");
//        adapter.addFragment(new HistoryFragment(), "History");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    public void OnClickAgree(View v)
    {
        String action;
        String emailID=getSharedPreferences().getString(PREF_EMAIL_ID, "");
        int rowId=v.getId();

        RelativeLayout row=(RelativeLayout)v.getParent();
        TextView txtDisagree=(TextView) row.findViewById(R.id.txtDisagree);
        TextView txtAgree=(TextView) row.findViewById(R.id.txtAgree);
        ImageButton btnDisagree=(ImageButton) row.findViewById(v.getId()+2000);

        MessageDetails messageDetails=objDbh.getMessageDetails(rowId);
        String agreeUsers=messageDetails.getAgree();
        String disagreeUsers=messageDetails.getDisagree();
        long agreeCount=Long.valueOf(agreeUsers.split(",").length - 1);
        long disagreeCount=Long.valueOf(disagreeUsers.split(",").length - 1);

        if(messageDetails.getAgree().contains(emailID))
        {
            action="NotAgree";
            v.setBackgroundResource(R.drawable.agree);
            txtAgree.setText(String.valueOf(agreeCount - 1));
            objDbh.updateMessageDetails("agree",agreeUsers.replace(","+emailID,""),String.valueOf(rowId));
        }
        else if(messageDetails.getDisagree().contains(emailID))
        {
            action="NotDisagree";
            btnDisagree.setBackgroundResource(R.drawable.disagree);
            txtDisagree.setText(String.valueOf(disagreeCount - 1));
            objDbh.updateMessageDetails("disagree", disagreeUsers.replace(","+emailID, ""), String.valueOf(rowId));
        }
        else
        {
            action="Agree";
            v.setBackgroundResource(R.drawable.agreed);
            txtAgree.setText(String.valueOf(agreeCount + 1));
            objDbh.updateMessageDetails("agree", agreeUsers+","+emailID, String.valueOf(rowId));
        }

        JSONObject jsonParam =GetjsonWithCurrentDeatils(rowId,action,false);
        SendAndReceiveMessage objSARM = new SendAndReceiveMessage();
        objSARM.ExecuteWebQuery(jsonParam, AgreeDisagree_URI, null);
    }

    public void OnClickDisagree(View v)
    {
        String action;
        String emailID=getSharedPreferences().getString(PREF_EMAIL_ID, "");
        int rowId=v.getId()-2000;

        ViewGroup row=(ViewGroup)v.getParent();
        TextView txtDisagree=(TextView) row.findViewById(R.id.txtDisagree);
        TextView txtAgree=(TextView) row.findViewById(R.id.txtAgree);
        ImageButton btnAgree=(ImageButton) row.findViewById(rowId);

        MessageDetails messageDetails=objDbh.getMessageDetails(v.getId()-2000);
        String agreeUsers=messageDetails.getAgree();
        String disagreeUsers=messageDetails.getDisagree();
        long agreeCount=Long.valueOf(agreeUsers.split(",").length - 1);
        long disagreeCount=Long.valueOf(disagreeUsers.split(",").length - 1);

        if(messageDetails.getDisagree().contains(emailID))
        {   action="NotDisagree";
            v.setBackgroundResource(R.drawable.disagree);
            txtDisagree.setText(String.valueOf(disagreeCount - 1));
            objDbh.updateMessageDetails("disagree", disagreeUsers.replace(","+emailID, ""), String.valueOf(rowId));
        }
        else if(messageDetails.getAgree().contains(emailID))
        {   action="NotAgree";
            btnAgree.setBackgroundResource(R.drawable.agree);
            txtAgree.setText(String.valueOf(agreeCount - 1));
            objDbh.updateMessageDetails("agree", agreeUsers.replace(","+emailID, ""), String.valueOf(rowId));
        }
        else
        { action="Disagree";
            v.setBackgroundResource(R.drawable.disagreed);
            txtDisagree.setText(String.valueOf(disagreeCount + 1));
            objDbh.updateMessageDetails("disagree", disagreeUsers + ","+emailID, String.valueOf(rowId));
        }
        JSONObject jsonParam =GetjsonWithCurrentDeatils(rowId,action,false);
        SendAndReceiveMessage objSARM = new SendAndReceiveMessage();
        objSARM.ExecuteWebQuery(jsonParam, AgreeDisagree_URI, null);

    }

    public void OnClickDelete(final View v)
    {
      final int id=v.getId() ;
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(MainActivity.context);
        alertDialog.setTitle("Confirm Delete");
        alertDialog.setMessage("Do you want to delete this post ?");
        alertDialog.setIcon(R.drawable.ic_menu_delete);
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                ProgressDialog progress = new ProgressDialog(MainActivity.context);
                progress.setTitle("Deleting");
                progress.setMessage("Please wait while post is being deleted...");
                progress.setCancelable(false);
                JSONObject jsonParam = GetjsonWithCurrentDeatils(id - 4000, "UpdateDelete",false);
                SendAndReceiveMessage objSARM = new SendAndReceiveMessage();
                objSARM.ExecuteWebQuery(jsonParam, DeleteMessage_URI, progress);
            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
       alertDialog.show();
    }

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }

    public static void HandleDeviceGCMRegistration()
    {
        gcmRegId = GCMsgnHandler.GetGCMInfo(context);
        isRegistered = getSharedPreferences().getString(PREF_IS_REGISTERED, "");
        if (TextUtils.isEmpty(isRegistered)) {
            {
                MainActivity objMA = new MainActivity();
                objMA.callLoginDialog();
            }
        }
    }

    private void callLoginDialog()
    {
        myDialog = new Dialog(MainActivity.context);
        myDialog.setContentView(R.layout.sign_up);
        myDialog.setCancelable(false);
        Button signup = (Button) myDialog.findViewById(R.id.btnSignUp);
        final EditText etEmailID = (EditText) myDialog.findViewById(R.id.etEmailID);
        final EditText etPassword1 = (EditText) myDialog.findViewById(R.id.etPassword1);
        final EditText etPassword2 = (EditText) myDialog.findViewById(R.id.etPassword2);
        final EditText etDisplayName = (EditText) myDialog.findViewById(R.id.etDisplayName);
        final EditText etSecretQuestion = (EditText) myDialog.findViewById(R.id.etSecretQuestion);
        final EditText etSecretAnswer = (EditText) myDialog.findViewById(R.id.etSecretAnswer);

       myDialog.show();

        signup.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                etEmailID.setBackgroundColor(0xf1ebff);
                etPassword1.setBackgroundColor(0xf1ebff);
                etPassword2.setBackgroundColor(0xf1ebff);
                etDisplayName.setBackgroundColor(0xf1ebff);
                etSecretQuestion.setBackgroundColor(0xf1ebff);
                etSecretAnswer.setBackgroundColor(0xf1ebff);

                //your login calculation goes here
                if (etEmailID.getText().toString().isEmpty()) {
                    etEmailID.setBackgroundColor(0xb2ff0f1b);
                    Toast.makeText(context, "Email ID cannot be left blank !!", Toast.LENGTH_SHORT).show();
                } else {
                    if (etDisplayName.getText().toString().isEmpty()) {
                        etDisplayName.setBackgroundColor(0xb2ff0f1b);
                        Toast.makeText(context, "Please Enter Name that you want to be displayed !!", Toast.LENGTH_SHORT).show();
                    } else if (etSecretQuestion.getText().toString().isEmpty()) {
                        etSecretQuestion.setBackgroundColor(0xb2ff0f1b);
                        Toast.makeText(context, "Please Secret Question for account recovery !!", Toast.LENGTH_SHORT).show();
                    } else if (etSecretQuestion.getText().toString().isEmpty()) {
                        etSecretQuestion.setBackgroundColor(0xb2ff0f1b);
                        Toast.makeText(context, "Please Secret Question for account recovery !!", Toast.LENGTH_SHORT).show();
                    } else if (etSecretAnswer.getText().toString().isEmpty()) {
                        etSecretAnswer.setBackgroundColor(0xb2ff0f1b);
                        Toast.makeText(context, "Please Secret Answer for account recovery !!", Toast.LENGTH_SHORT).show();
                    } else if (!etPassword1.getText().toString().equals(etPassword2.getText().toString()) ||
                            etPassword1.getText().toString().isEmpty()) {
                        etPassword1.setBackgroundColor(0xb2ff0f1b);
                        etPassword2.setBackgroundColor(0xb2ff0f1b);
                        Toast.makeText(context, "PASSWORDS DOES NOT MATCH !!", Toast.LENGTH_SHORT).show();
                    } else {//////

                        JSONObject jsonParam = new JSONObject();
                        try {
                            DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                            String date = df.format(Calendar.getInstance().getTime());

                            jsonParam.put("Email_ID", etEmailID.getText());
                            jsonParam.put("User_Name", etDisplayName.getText());
                            jsonParam.put("Password", etPassword1.getText());
                            jsonParam.put("Secret_Question", etSecretQuestion.getText());
                            jsonParam.put("Secret_Answer", etSecretAnswer.getText());
                            jsonParam.put("Latitude", LocationManager.currentLocation.getLatitude());
                            jsonParam.put("Longitude", LocationManager.currentLocation.getLongitude());
                            jsonParam.put("Last_Used_Time", date);
                            jsonParam.put("Status", "New");
                            jsonParam.put("Device_ID", gcmRegId);
                            jsonParam.put("Range", Integer.parseInt(getSharedPreferences().getString(PREF_RANGE, "")));

                            ProgressDialog progress = new ProgressDialog(MainActivity.context);
                            progress.setTitle("Registering");
                            progress.setMessage("Please wait while you are being registered...");
                            progress.setCancelable(false);
                            SendAndReceiveMessage objSARM = new SendAndReceiveMessage();
                            objSARM.ExecuteWebQuery(jsonParam, SIGN_UP_URI, progress);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

    }

    private void callPostDialog()
    {
        postDialog = new Dialog(this);
        postDialog.setContentView(R.layout.post);
        postDialog.setCancelable(false);
        TextView tvHeader=(TextView) postDialog.findViewById(R.id.tvHeader);
        Button btnPost = (Button) postDialog.findViewById(R.id.btnPost);
        Button btnCancel = (Button) postDialog.findViewById(R.id.btnCancel);
        final EditText etPost = (EditText) postDialog.findViewById(R.id.etPost);

        tvHeader.setText("My Post");

        postDialog.getWindow().getAttributes().windowAnimations = R.style.PostDialogAnimation;
        postDialog.show();

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
                        JSONObject jsonParam = GetjsonWithCurrentDeatils(0, etPost.getText().toString(),true);
                        ProgressDialog progress = new ProgressDialog(MainActivity.context);
                        progress.setTitle("Posting");
                        progress.setMessage("Please wait while it's being posted...");
                        progress.setCancelable(false);
                        SendAndReceiveMessage objSARM = new SendAndReceiveMessage();
                        objSARM.ExecuteWebQuery(jsonParam, POST_URI, progress);
                        postDialog.dismiss();
                    }
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postDialog.dismiss();
            }
        });

    }

    private void callRangeDialog()
    {
        rangeDialog = new Dialog(this);
        rangeDialog.setContentView(R.layout.select_range);
        rangeDialog.setCancelable(false);
        Button btn1km = (Button) rangeDialog.findViewById(R.id.btn1km);
        Button btn2km = (Button) rangeDialog.findViewById(R.id.btn2km);
        Button btn5km = (Button) rangeDialog.findViewById(R.id.btn5km);
        Button btn10km = (Button) rangeDialog.findViewById(R.id.btn10km);
        Button btn20km = (Button) rangeDialog.findViewById(R.id.btn20km);
        Button btn30km = (Button) rangeDialog.findViewById(R.id.btn30km);
        Button btn50km = (Button) rangeDialog.findViewById(R.id.btn50km);
        Button btn100km = (Button) rangeDialog.findViewById(R.id.btn100km);

        rangeDialog.getWindow().getAttributes().windowAnimations = R.style.RangeDialogAnimation;
        rangeDialog.show();

        btn1km.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetRange("1");
            }
        });
        btn2km.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 SetRange("2");
             }
         });
        btn5km.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {SetRange("5"); }});
        btn10km.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {SetRange("10"); }});
        btn20km.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {SetRange("20"); }});
        btn30km.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetRange("30");
            }
        });
        btn50km.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetRange("50");
            }
        });
        btn100km.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetRange("100");
            }
        });

        AreaUpdateFragment.RefreshAUF(-1);
    }

    private void callSelectImageDialog()
    {
        selectImageDialog = new Dialog(this);
        selectImageDialog.setContentView(R.layout.select_image);
        selectImageDialog.setCancelable(false);
        Button btnChange = (Button) selectImageDialog.findViewById(R.id.btnChange);
        Button btnDone = (Button) selectImageDialog.findViewById(R.id.btnDone);
        Button btnCancel = (Button) selectImageDialog.findViewById(R.id.btnCancel);
        ImageView ivSelectmage = (ImageView) selectImageDialog.findViewById(R.id.imageView);

        if(selectedProfileImage==null) {
            imgDecodableString = getSharedPreferences().getString(PREF_BITMAP_DP, "");
            if (imgDecodableString != "")
                ivSelectmage.setImageBitmap(BitmapFactory.decodeFile(imgDecodableString));
        }
        else
            ivSelectmage.setImageBitmap(selectedProfileImage);

        selectImageDialog.getWindow().getAttributes().windowAnimations = R.style.PostDialogAnimation;
        selectImageDialog.show();

        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImageDialog.dismiss();
                loadImagefromGallery();
            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedProfileImage != null) {
                    byte[] image = objBH.getBytesFromBitmap(selectedProfileImage);
                    JSONObject jsonParam = new JSONObject();
                    try {
                      //  String strImage = new String(image, "UTF-8");
                        String encodedImage = Base64.encodeToString(image, Base64.DEFAULT);

                        jsonParam.put("EmailID", getSharedPreferences().getString(PREF_EMAIL_ID, ""));
                        jsonParam.put("Image", encodedImage);

                        ProgressDialog progress = new ProgressDialog(MainActivity.context);
                        progress.setTitle("Uploading...");
                        progress.setMessage("Please wait while your pic is being uploaded...");
                        progress.setCancelable(false);
                        SendAndReceiveMessage objSARM = new SendAndReceiveMessage();
                        objSARM.ExecuteWebQuery(jsonParam, UpdateProfileImage_URI, progress);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    selectImageDialog.dismiss();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               selectImageDialog.dismiss();
            }
        });
    }

    public static void ExecuteSignUpResult(JSONObject obj)
    {
        try {
            String status= obj.getString("Status").toString();
            if(status.equals("Success")) {
                String emailId = obj.getString("EmailID");
                String userName = obj.getString("UserName");
                RegisterUserInFDB(emailId, userName);
                myDialog.dismiss();
            }
            else
                Toast.makeText(context, "Unable to register. "+status, Toast.LENGTH_SHORT).show();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void ExecutePostResult(JSONObject obj)
    {
        String status= null;
        try {
            status = obj.getString("Status").toString();
            if(status.equals("Success")) {
                postDialog.dismiss();
                Toast.makeText(context, "Posted.", Toast.LENGTH_SHORT).show();
            }
            else
                Toast.makeText(context, "Unable to post.", Toast.LENGTH_SHORT).show();
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public static void ExecuteUpdateAgreeDisagree(JSONObject obj)
    {
        String status;
        try {
            status = obj.getString("Status").toString();
            if(!status.equals("Success")) {
                Toast.makeText(context, "Unable to update. ", Toast.LENGTH_SHORT).show();
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public static void ExecuteUpdateProfileImage(JSONObject obj)
    {
        String status;
        try {
            status = obj.getString("Status");
            if(status.equals("Success")) {
                SaveImage(selectedProfileImage);
                SetDP();

            }
            else
                Toast.makeText(context, "Unable to update. ", Toast.LENGTH_SHORT).show();
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public static SharedPreferences getSharedPreferences() {
        if (prefs == null) {
            prefs = context.getApplicationContext().getSharedPreferences(
                    "AndroidMainFukrey", Context.MODE_PRIVATE);
        }
        return prefs;
    }

    public static void RegisterUserInFDB(String emailId, String userName) {
        // TODO Auto-generated method stub
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(PREF_EMAIL_ID, emailId);
        editor.putString(PREF_USER_NAME, userName);
        editor.putString(PREF_IS_REGISTERED, "Yes");
        editor.apply();
     }

    private static void SetRange(String range)
    {
        if(range!=null)
        {
            SharedPreferences.Editor editor = getSharedPreferences().edit();
            editor.putString(PREF_RANGE, range);
            editor.apply();
        }
        String prefRannge=getSharedPreferences().getString(PREF_RANGE, "");
        if(prefRannge.equals("1"))
            menu.getItem(4).setIcon(R.drawable.range1);
        else if(prefRannge.equals("2"))
            menu.getItem(4).setIcon(R.drawable.range2);
        else if(prefRannge.equals("5"))
            menu.getItem(4).setIcon(R.drawable.range5);
        else if(prefRannge.equals("10"))
            menu.getItem(4).setIcon(R.drawable.range10);
        else if(prefRannge.equals("20"))
            menu.getItem(4).setIcon(R.drawable.range20);
        else if(prefRannge.equals("30"))
            menu.getItem(4).setIcon(R.drawable.range30);
        else if(prefRannge.equals("50"))
            menu.getItem(4).setIcon(R.drawable.range50);
        else if(prefRannge.equals("100"))
            menu.getItem(4).setIcon(R.drawable.range100);
        else
            menu.getItem(4).setIcon(R.drawable.range);

       if(rangeDialog!=null)
           rangeDialog.dismiss();
    }

    private static void SetDP()
    {
        String imgDecodableString=getSharedPreferences().getString(PREF_BITMAP_DP, "");
            if (imgDecodableString != "")
                menu.getItem(3).setIcon(new BitmapDrawable(context.getResources(), BitmapFactory.decodeFile(imgDecodableString)));

    }

    public static JSONObject GetjsonWithCurrentDeatils(long rowID,String versatileCarrier, Boolean genAddress) {
        JSONObject jsonParam = new JSONObject();
        try {
            boolean hideAddress= getSharedPreferences().getString(PREF_SHOW_LOCATION, "")=="False";

            DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String date = df.format(Calendar.getInstance().getTime());

            double latitude = LocationManager.currentLocation.getLatitude();
            double longitude = LocationManager.currentLocation.getLongitude();
            StringBuilder address = new StringBuilder();
            String postAddress = "";
            if(genAddress) {
               List<Address> addresses;
               Geocoder geocoder = new Geocoder(context, Locale.getDefault());
               addresses = geocoder.getFromLocation(latitude, longitude, 1);
                if(!hideAddress) {
                    int maxLines = addresses.get(0).getMaxAddressLineIndex();
                    for (int i = 0; i < maxLines; i++) {
                        if (i == 0)//to hide exact address info
                        {
                            String[] add = addresses.get(0).getAddressLine(i).split(",");
                            for (int j = 1; j < add.length; j++) {
                                address.append(add[j]);
                                address.append(",");
                            }
                        } else {
                            address.append(addresses.get(0).getAddressLine(i));
                            address.append(",");
                        }
                    }
                    address.append(addresses.get(0).getAddressLine(maxLines));
                }
               postAddress = addresses.get(0).getLocality() + " | " + addresses.get(0).getAdminArea() + " | " + addresses.get(0).getCountryName();
           }

            jsonParam.put("EmailID", getSharedPreferences().getString(PREF_EMAIL_ID, ""));
            jsonParam.put("LastLine", 1);
            jsonParam.put("Accuracy","NotSent");// LocationManager.currentLocation.getAccuracy());
            jsonParam.put("LatitudeReceived",latitude);
            jsonParam.put("LongitudeReceived",longitude);
            jsonParam.put("Address",address.toString());
            jsonParam.put("LSC",postAddress);
            jsonParam.put("Range", getSharedPreferences().getString(PREF_RANGE, ""));
            jsonParam.put("RowID", rowID);
            jsonParam.put("State", "Active");
            jsonParam.put("TimeStamp", date);
            jsonParam.put("VersatileCarrier",versatileCarrier );

        } catch (JSONException e) {
            e.printStackTrace();
         } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonParam;
    }

   //select image from gallery
    public void loadImagefromGallery() {
        // Create intent to Open Image applications like Gallery, Google Photos
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgDecodableString = cursor.getString(columnIndex);
                cursor.close();

                selectedProfileImage=scaleDown(BitmapFactory.decodeFile(imgDecodableString), 512, false);
                callSelectImageDialog();
            } else {
                Toast.makeText(this, "You haven't picked Image!",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
          String str=e.getMessage();
            Toast.makeText(this, "Something went wrong...", Toast.LENGTH_LONG)
                    .show();
        }

    }

    private static void SaveImage(Bitmap finalBitmap) {

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/Fukrey Images");
        myDir.mkdirs();
        //DateFormat df = new SimpleDateFormat("yyyyMMddhhmmss");
       // String date = df.format(Calendar.getInstance().getTime());
        //Random rnd=new Random();
       // int r=rnd.nextInt(1000);
        String fname = "Image_profile.jpg";
        File file = new File (myDir, fname);
        if (file.exists ()) file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

            SharedPreferences.Editor editor = getSharedPreferences().edit();
            editor.putString(PREF_BITMAP_DP, file.getPath());
            editor.apply();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Bitmap scaleDown(Bitmap realImage, float maxImageSize,
                                   boolean filter) {
        float ratio = Math.min(
                (float)  realImage.getWidth()/maxImageSize,
                (float) realImage.getHeight()/maxImageSize);
        if(ratio<1)
            return realImage;
            else
            {
            int width = Math.round((int) realImage.getWidth()/ratio);
            int height = Math.round((int) realImage.getHeight()/ratio);

            Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,
                    height, filter);
            return newBitmap;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart fired ..............");
        if(!this.isActive )
             LocationManager.googleApiClient.connect();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!MainActivity.isActive) {
            if (LocationManager.googleApiClient.isConnected()) {
                objLocationManager.startLocationUpdates();
            }
            }
        this.isActive=true;
        Log.d(TAG, " resumed.....................");
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.isActive=false;
        if(!MainActivity.isActive) {
            {
                if (objLocationManager.googleApiClient.isConnected())
                    objLocationManager.stopLocationUpdates();
            }
            }
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop fired ..............");
        if(!MainActivity.isActive) {
            {
                LocationManager.googleApiClient.disconnect();
                Log.d(TAG, "isConnected ...............: " + objLocationManager.googleApiClient.isConnected());
            }
        }
}
}