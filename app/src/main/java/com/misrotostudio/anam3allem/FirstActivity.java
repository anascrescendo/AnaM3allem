package com.misrotostudio.anam3allem;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.media.ExifInterface;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Time;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.misrotostudio.anam3allem.helper.DialogATask;
import com.misrotostudio.anam3allem.helper.SessionManager;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;


public class FirstActivity extends AppCompatActivity {

    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private SessionManager session;
    private ImageButton takeButton;

    private static final int TAKE_PICTURE = 1;
    static Uri imageUri;







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_first);
        callbackManager = CallbackManager.Factory.create();

        takeButton = (ImageButton) findViewById(R.id.to_take_selfie_button);


        // Session manager
        session = new SessionManager(getApplicationContext());

        // Check if user is already logged in or not





        if (!session.isLoggedIn() ) {
            takeButton.setVisibility(View.INVISIBLE);
        }

        // Facebook Login
        loginButton = (LoginButton)findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("email", "public_profile"));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest.newMeRequest(
                        loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject me, GraphResponse response) {
                                if (response.getError() != null) {
                                    Log.e("Facebook", "error response to get user info");// handle error
                                } else {
                                    //String email = me.optString("email");
                                    //String id = me.optString("id");
                                    //String name = new String(me.optString("last_name") + " " + me.optString("first_name"));

                                    session.setLogin(true);
                                    takeButton.setVisibility(View.VISIBLE);
                                    /*
                                     Here start the next activity
                                     */
                                }
                            }
                        }).executeAsync();
            }

            @Override
            public void onCancel() {
                session.setLogin(false);
                setButton();
            }

            @Override
            public void onError(FacebookException e) {

            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(AccessToken.getCurrentAccessToken()!=null){
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            checkConnection();
                            setButton();
                        }
                    }, 4000);

                    Log.d("LISTENER", "wasConnected");
                }
                else {
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            checkConnection();
                            setButton();
                        }
                    }, 4000);

                    Log.d("LISTENER", "wasDisconnected");
                }
                //checkConnection();
                //setButton();
                Log.d("LISTENER", "Click");
            }

        });

        takeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto(v);

            }
        });


    }

    public void setButton(){
        if (session.isLoggedIn() ) {
            takeButton.setVisibility(View.VISIBLE);
        }
        else takeButton.setVisibility(View.INVISIBLE);
    }

    public void checkConnection(){
        if(AccessToken.getCurrentAccessToken()!=null){
            session.setLogin(true);
        }else session.setLogin(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkConnection();
        setButton();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case TAKE_PICTURE:
                if (resultCode == Activity.RESULT_OK) {

                    Intent i = new Intent(FirstActivity.this, MainActivity.class);
                    startActivity(i);
                }
                break;
            default:
                callbackManager.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    public void takePhoto(View view) {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        File photo = new File(Environment.getExternalStorageDirectory(),  "tmp.jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(photo));

        imageUri = Uri.fromFile(photo);


        startActivityForResult(intent, TAKE_PICTURE);
    }





}
