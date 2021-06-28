package com.example.cha;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

public class mainActivity extends AppCompatActivity implements FragmentCallBack{
    //Fragment related hooks
    FragmentManager fragmentManager = getSupportFragmentManager();
    DashFragment dashFragment;
    FloorFragment floorFragment;
    RoomsFragment roomsFragment;
    LoadingFragment loadingFragment;
    ResultFragment resultFragment;
    TextView title,un;
    RelativeLayout support;
    //for invisibility
    FrameLayout activityFrame;
    //for Result
    Bundle sData;
    String rResult;
    String BitmapStr;
    //setting username
    String setName = null;
    SharedPreferences holdUname;
    //setting Animation to username
    Animation unAnimation,titleAnimation;
    String roomNumber;
    //setting room numbers
    String[] rMain;
    Rooms room;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        un = findViewById(R.id.display_un);
        activityFrame = findViewById(R.id.activity_frame);
        support = findViewById(R.id.support);

        //shared preference
        holdUname = getSharedPreferences("username",MODE_PRIVATE);
        //set Username
        if(holdUname.getString("username","none").equals("none"))
        {
            setName = getIntent().getStringExtra("getUsername");
            holdUname.edit().putString("username",setName).apply();
        }
        //room numbers
        rMain = new String[3];
        room = new Rooms();
        iniFragment();
    }

    @Override
    protected void onResume() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Welcome Back").append(" ").append(holdUname.getString("username","none"));
        unAnimation = AnimationUtils.loadAnimation(this,R.anim.dash_left);
        un.setText(stringBuilder);
        un.setAnimation(unAnimation);
        support.setAnimation(unAnimation);
        super.onResume();
    }

    //implemented CallBacks
    @Override
    public void callNextFrag(String fragments) {
        switch(fragments)
        {
            case "floor": Floor();break;
            case "rooms":Rooms();break;
            case "loading":Loading();break;
            case "result":Result();break;
            case "profile":Profile();break;
            case "logout":logout();break;
        }
    }

    @Override
    public void getTextView(String s) {
        titleAnimation = AnimationUtils.loadAnimation(this,R.anim.alp);
        title = findViewById(R.id.title_text);
        switch(s)
        {
            case "dash":
                title.setText(R.string.dashboard_title);
                title.setAnimation(titleAnimation);
                break;
            case "floor":
                title.setText(R.string.floor_title);
                title.setAnimation(titleAnimation);
                break;
            case "profile":
                title.setText(R.string.profile_title);
                title.setAnimation(titleAnimation);
                break;
            case "rooms":
                title.setText(R.string.room_title);
                title.setAnimation(titleAnimation);
                break;
            case "result":
                title.setText(R.string.result_title);
                title.setAnimation(titleAnimation);
                break;
        }

    }

    @Override
    public void setResultData(String result,Bitmap img) {
        rResult = result;
        BitmapStr = encodeBitmap(img);
    }

    @Override
    public void hideActionBar(boolean flag) {
        if(flag)
        {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }else
        {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    @Override
    public void exception(String e) {
        Log.i("exception",e);
    }

    @Override
    public void pop() {
        getSupportFragmentManager().popBackStack();
    }

    @Override
    public void setRoom(String r) {
        this.roomNumber = r;
    }

    @Override
    public void setFloorRooms(String floor) {
        switch(floor)
        {
            case "gf": System.arraycopy(room.gf(),0,rMain,0,room.gf().length);break;
            case "ff": System.arraycopy(room.ff(),0,rMain,0,room.ff().length);break;
            case "sf": System.arraycopy(room.sf(),0,rMain,0,room.sf().length);break;
            case "tf": System.arraycopy(room.tf(),0,rMain,0,room.tf().length);break;
        }
    }

    //I defined functions

    public void iniFragment()
    {
        dashFragment = new DashFragment();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.activity_frame,dashFragment,null);
        dashFragment.setFragmentCallBack(this);
        fragmentTransaction.commit();
    }
    public void Floor()
    {
        floorFragment = new FloorFragment();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_right,0,R.anim.slide_in_left,0);
        fragmentTransaction.replace(R.id.activity_frame,floorFragment,null);
        fragmentTransaction.addToBackStack("frag_stack");
        floorFragment.setFragmentCallBack(this);
        fragmentTransaction.commit();
    }
    public void Rooms()
    {
        sData = new Bundle();
        sData.putStringArray("roomNumbers",rMain);
        roomsFragment = new RoomsFragment();
        roomsFragment.setArguments(sData);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_right,0,R.anim.slide_in_left,0);
        fragmentTransaction.replace(R.id.activity_frame,roomsFragment,"gf");
        fragmentTransaction.addToBackStack("frag_stack");
        roomsFragment.setFragmentCallBack(this);
        fragmentTransaction.commit();
    }
    public void Loading()
    {
        sData = new Bundle();
        sData.putString("rNumber",this.roomNumber);
        loadingFragment = new LoadingFragment();
        loadingFragment.setArguments(sData);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_right,0,0,R.anim.slide_out_left);
        fragmentTransaction.replace(R.id.main_constraint,loadingFragment,"loading");
        fragmentTransaction.addToBackStack("frag_stack");
        loadingFragment.setFragmentCallBack(this);
        fragmentTransaction.commit();
    }
    public void Result()
    {
        sData = new Bundle();
        sData.putString("students",rResult);
        sData.putString("image",BitmapStr);
        resultFragment = new ResultFragment();
        resultFragment.setArguments(sData);
        un.setVisibility(View.INVISIBLE);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_right,0,R.anim.slide_in_left,R.anim.slide_out_right);
        fragmentTransaction.replace(R.id.activity_frame,resultFragment,"result");
        fragmentTransaction.addToBackStack("frag_stack");
        resultFragment.setFragmentCallBack(this);
        fragmentTransaction.commit();
    }

    public void Profile()
    {
        un.setVisibility(View.INVISIBLE);
        support.setVisibility(View.GONE);
        ProfileFragment profileFragment = new ProfileFragment();
        sData = new Bundle();
        sData.putString("profileName",holdUname.getString("username","none"));
        profileFragment.setArguments(sData);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_right,0,R.anim.slide_in_left,R.anim.slide_out_right);
        fragmentTransaction.addToBackStack("frag_stack");
        fragmentTransaction.replace(R.id.activity_frame,profileFragment,"profile");
        profileFragment.setFragmentCallBack(this);
        fragmentTransaction.commit();
    }

    public void logout(){
        SplashScreen.ld.edit().putBoolean("login",false).apply();
        holdUname.edit().putString("username","none").apply();
        startActivity(new Intent(this,LoginActivity.class));
        finish();
    }
    public String encodeBitmap(Bitmap img)
    {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        img.compress(Bitmap.CompressFormat.PNG,100,byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray,Base64.DEFAULT);
    }

    public void UserInterrupt()
    {
        Toast.makeText(getApplicationContext(),"user Interrupted Please Wait...",Toast.LENGTH_LONG).show();
    }



    public void onBackPressed()
    {
        if(fragmentManager.getBackStackEntryCount()>0) {
            //interrupt by user while loading
            if(fragmentManager.findFragmentByTag("loading")!=null)
            {
                LoadingFragment loadingFragment = (LoadingFragment)fragmentManager.findFragmentByTag("loading");
                assert loadingFragment != null;
                loadingFragment.executorService.shutdownNow();
                loadingFragment.revAnim();
                UserInterrupt();
            }else if(fragmentManager.findFragmentByTag("result")!=null)  //setting username visibility
            {
                un.setVisibility(View.VISIBLE);
                fragmentManager.popBackStack();
            }else if(fragmentManager.findFragmentByTag("profile")!=null)
            {
                un.setVisibility(View.VISIBLE);
                fragmentManager.popBackStack();
            }
            else
            {
                //default pop
                fragmentManager.popBackStack();
            }
        }
        else {
            super.onBackPressed();
        }

    }
}

