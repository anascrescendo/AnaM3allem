package com.misrotostudio.anam3allem;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.Display;
import android.widget.ImageView;
import android.widget.Toast;


import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.share.ShareApi;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareButton;
import com.misrotostudio.anam3allem.helper.SessionManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends ActionBarActivity {

    private Bitmap font;
    private Bitmap bitmap;

    private CallbackManager callbackManager;
    private LoginManager manager;

    private ShareButton shareButton;
    private SessionManager session;
    private SharePhotoContent content;





   protected void onCreate(Bundle savedInstanceState){
       super.onCreate(savedInstanceState);
       FacebookSdk.sdkInitialize(getApplicationContext());
       setContentView(R.layout.activity_main);



       FacebookSdk.sdkInitialize(getApplicationContext());

       callbackManager = CallbackManager.Factory.create();




       shareButton = (ShareButton)findViewById(R.id.fb_share_button);









       font = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.je_vais);

       Uri selectedImage = FirstActivity.imageUri;
       getContentResolver().notifyChange(selectedImage, null);
       ImageView imageView = (ImageView) findViewById(R.id.image_view);
       ContentResolver cr = getContentResolver();
       try {
           bitmap = android.provider.MediaStore.Images.Media
                   .getBitmap(cr, selectedImage);
           Log.d("OTHMANE", font.getWidth() + " " + font.getHeight());
           bitmap = cropFromCenterBitmap(bitmap);
           bitmap = getResizedBitmap(bitmap, 800, 800);
           if(FirstActivity.rotation == 0 || FirstActivity.rotation == 0)
               bitmap = rotate(bitmap, 3*90);
           else if(FirstActivity.rotation == 3)
               bitmap = rotate(bitmap, 2*90);
           bitmap = mcreateBitmap(font, bitmap);
           imageView.setImageBitmap(bitmap);
           storeImage(bitmap);


       } catch (Exception e) {
           Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT)
                   .show();
           Log.e("Camera", e.toString());
       }

        /*
        rotation 0 = 3 * 90;
        rotation 3 lands droite =
        rota
         */
/*
       List<String> permissionNeeds = Arrays.asList("publish_actions");
       //try{
       manager = LoginManager.getInstance();

       manager.logInWithPublishPermissions(this, permissionNeeds);

       manager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
           @Override
           public void onSuccess(LoginResult loginResult) {

               Log.d("Facebook", "Sharing Success");
           }

           @Override
           public void onCancel() {
               Log.d("Facebook", "Sharing Cancel");
           }

           @Override
           public void onError(FacebookException exception) {
               Log.d("Facebook", "Sharing Error onError");
           }
       });

       */

       SharePhoto photo = new SharePhoto.Builder()
               .setBitmap(bitmap)
               .build();
       content = new SharePhotoContent.Builder()
               .addPhoto(photo)
               .build();
       shareButton.setShareContent(content);


   }


    public void sharePhoto(Bitmap image){
        SharePhoto photo = new SharePhoto.Builder()
                .setBitmap(image)
                .build();
        SharePhotoContent content = new SharePhotoContent.Builder()
                .addPhoto(photo)
                .build();
        ShareApi.share(content, null);
    }

    public int getScreenOrientation()
    {
        Display getOrient = this.getWindowManager().getDefaultDisplay();
        int orientation = Configuration.ORIENTATION_UNDEFINED;
        if(getOrient.getWidth()==getOrient.getHeight()){
            orientation = Configuration.ORIENTATION_SQUARE;
        } else{
            if(getOrient.getWidth() < getOrient.getHeight()){
                orientation = Configuration.ORIENTATION_PORTRAIT;
            }else {
                orientation = Configuration.ORIENTATION_LANDSCAPE;
            }
        }
        return orientation;
    }


    private void storeImage(Bitmap image) {
        File pictureFile = getOutputMediaFile();
        if (pictureFile == null) {
            Log.d("Main",
                    "Error creating media file, check storage permissions: ");// e.getMessage());
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d("Main", "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d("Main", "Error accessing file: " + e.getMessage());
        }
    }


    private static File getOutputMediaFile(){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Ana M3allem");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("Ana M3allem", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;

        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        return mediaFile;
    }













    public Bitmap mcreateBitmap(Bitmap src, Bitmap watermark) {

        Time t = new Time();
        t.setToNow();
        int w = src.getWidth();
        int h = src.getHeight();

        String mstrTitle = "11："+t.hour + ":" + t.minute + ":" + t.second;
        String xx="34："+60;
        String yy="44："+20;
        Bitmap bmpTemp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bmpTemp);
        Paint photoPaint = new Paint();
        photoPaint.setDither(true);
        photoPaint.setFilterBitmap(true);
        Rect s = new Rect(0, 0, src.getWidth(), src.getHeight());
        Rect d = new Rect(0, 0, w, h);
        canvas.drawBitmap(src, s, d, photoPaint);

        String familyName = "new";
        Typeface font = Typeface.create(familyName, Typeface.BOLD);
        photoPaint.setColor(Color.BLUE);
        photoPaint.setTypeface(font);
        photoPaint.setTextSize(18);
        //canvas.drawText(mstrTitle, 40, 20, photoPaint);
        //canvas.drawText(xx,40, 40, photoPaint);
        //canvas.drawText(yy, 40, 60, photoPaint);
        canvas.drawBitmap(watermark, src.getWidth()/2 - watermark.getWidth()/2, src.getHeight()/2 - watermark.getHeight()/2, null);
        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();

        return bmpTemp;
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }

    public static Bitmap rotate(Bitmap bitmap, int degree) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        Matrix mtx = new Matrix();
        mtx.postRotate(degree);

        return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
    }

    public Bitmap cropFromCenterBitmap(Bitmap source){
        Bitmap dest;
        if (source.getWidth() >= source.getHeight()){

            dest = Bitmap.createBitmap(
                    source,
                    source.getWidth()/2 - source.getHeight()/2,
                    0,
                    source.getHeight(),
                    source.getHeight()
            );

        }else{

            dest = Bitmap.createBitmap(
                    source,
                    0,
                    source.getHeight()/2 - source.getWidth()/2,
                    source.getWidth(),
                    source.getWidth()
            );
        }
        return dest;
    }




}
