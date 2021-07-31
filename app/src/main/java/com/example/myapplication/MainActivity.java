package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.share.Share;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;


public class MainActivity extends AppCompatActivity {

    private CallbackManager callbackManager;
    private LoginButton loginButton;
    private ImageView fbImageView;
    private boolean isLogged=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar_layout);

        System.out.println("da,merge");
        callbackManager = CallbackManager.Factory.create();
        fbImageView=findViewById(R.id.fbimage);
        loginButton=findViewById(R.id.login_button);
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {


            }

            @Override
            public void onCancel() {


            }

            @Override
            public void onError(FacebookException error) {


            }
        });
    }
    private void initScoreBoards()
    {



        SharedPreferences sharedPreferences=getSharedPreferences("prefs",0);
        SharedPreferences.Editor editor=sharedPreferences.edit();

        Set<String> initSet=new HashSet<>();
        initSet.add("init"+"\n"+"-1");


        if(sharedPreferences.getStringSet("Easy",null)==null)
        {
            editor.putStringSet("Easy",initSet);
        }
        if(sharedPreferences.getStringSet("Medium",null)==null)
        {
            editor.putStringSet("Medium",initSet);
        }
        if(sharedPreferences.getStringSet("Hard",null)==null)
        {
            editor.putStringSet("Hard",initSet);
        }
        editor.commit();


    }
    private void updateLogStatus()
    {
        SharedPreferences sharedPreferences=getSharedPreferences("prefs",0);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putBoolean("isLogged",isLogged);

        if(!isLogged)
            editor.putString("pic","");
        editor.commit();

    }
    @Override
    protected void onActivityResult(int requestCode,int resultCode, Intent data)
    {
        callbackManager.onActivityResult(requestCode,resultCode,data);
        super.onActivityResult(requestCode,resultCode,data);
        GraphRequest graphRequest=GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {



                try {

                    String id=object.getString("id");
                    String pic = object.getJSONObject("picture").getJSONObject("data").getString("url");
                    SharedPreferences sharedPreferences=getSharedPreferences("prefs",0);
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.putString("pic",pic);
                    editor.commit();
                    Picasso.get().load(sharedPreferences.getString("pic","")).into(fbImageView);
                    isLogged=true;
                    updateLogStatus();

                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                    isLogged=false;
                    updateLogStatus();
                }
                catch (NullPointerException e)
                {
                    e.printStackTrace();
                    isLogged=false;
                    updateLogStatus();
                }
            }
        });
        Bundle bundle=new Bundle();
        bundle.putString("fields","name,id,first_name,last_name,mail, gender, picture");
        bundle.putString("fields","picture.width(150).height(150)");

        graphRequest.setParameters(bundle);
        graphRequest.executeAsync();
    }

    AccessTokenTracker accessTokenTracker =new AccessTokenTracker() {
        @Override
        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
            if(currentAccessToken==null)
            {
                LoginManager.getInstance().logOut();
                isLogged=false;
                updateLogStatus();
                fbImageView.setImageResource(0);
            }
        }
    };
    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        accessTokenTracker.stopTracking();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {

        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.maine_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.Settings:
            {
                startActivity(new Intent(this,MyPreferences.class));
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        fbImageView=findViewById(R.id.fbimage);
        SharedPreferences sharedPreferences=getSharedPreferences("prefs",0);
        if(sharedPreferences.getString("pic","")!="")
        Picasso.get().load(sharedPreferences.getString("pic","")).into(fbImageView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        fbImageView=findViewById(R.id.fbimage);
        SharedPreferences sharedPreferences=getSharedPreferences("prefs",0);
        if(!sharedPreferences.getString("pic","").contains("")) {

            Picasso.get().load(sharedPreferences.getString("pic", "")).into(fbImageView);
        }
    }


    public void startGame(View view) {
      Intent i=new Intent(this,Game.class);
      startActivity(i);
    }

    public void showScore(View view) {
        SharedPreferences sharedPreferences=getSharedPreferences("prefs",0);
        if((sharedPreferences.getStringSet("Easy",null)==null)||
                (sharedPreferences.getStringSet("Medium",null)==null)||
                sharedPreferences.getStringSet("Hard",null)==null)
            initScoreBoards();




        Set<String> s1=sharedPreferences.getStringSet("Easy",null);
        int n = s1.size();
        String arr1[] = new String[n];
        arr1 = s1.toArray(arr1);

        Arrays.sort(arr1, new Comparator<String>() {
            public int compare(String one, String two) {
                return Integer.valueOf(two.split("\n")[1])-Integer.valueOf(one.split("\n")[1]) ;
            }
        });

        Set<String> s2=sharedPreferences.getStringSet("Medium",null);
        int n2 = s2.size();
        String arr2[] = new String[n2];
        arr2 = s2.toArray(arr2);

        Arrays.sort(arr2, new Comparator<String>() {
            public int compare(String one, String two) {
                return Integer.valueOf(two.split("\n")[1])-Integer.valueOf(one.split("\n")[1]) ;
            }
        });


        Set<String> s3=sharedPreferences.getStringSet("Hard",null);
        int n3 = s3.size();
        String arr3[] = new String[n3];
        arr3 = s3.toArray(arr3);

        Arrays.sort(arr3, new Comparator<String>() {
            public int compare(String one, String two) {
                return Integer.valueOf(two.split("\n")[1])-Integer.valueOf(one.split("\n")[1]) ;
            }
        });

        if(n>5)
        arr1=Arrays.copyOfRange(arr1,0,5);
        if(n2>5)
        arr2=Arrays.copyOfRange(arr2,0,5);
        if(n3>5)
        arr3=Arrays.copyOfRange(arr3,0,5);


        Intent i=new Intent(this,Score.class);
        i.putExtra("easy",arr1);
        i.putExtra("medium",arr2);
        i.putExtra("hard",arr3);
        startActivity(i);







    }
}