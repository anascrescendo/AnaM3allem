package com.misrotostudio.anam3allem;


import android.content.ContentResolver;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Time;
import android.util.Log;

import android.widget.ImageView;
import android.widget.Toast;



import com.facebook.FacebookSdk;


import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareButton;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends AppCompatActivity {

    private Bitmap hashTag;
    private Bitmap jeVote;
    private Bitmap bitmap;

    private Bitmap background;

    private ShareButton shareButton;
    private SharePhotoContent content;

    private ImageView imageView;





   protected void onCreate(Bundle savedInstanceState){
       super.onCreate(savedInstanceState);
       FacebookSdk.sdkInitialize(getApplicationContext());
       setContentView(R.layout.activity_main);

       shareButton = (ShareButton)findViewById(R.id.fb_share_button);
       imageView = (ImageView) findViewById(R.id.image_view);

       //hashTag = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.hashtag);



       Uri selectedImage = FirstActivity.imageUri;
       getContentResolver().notifyChange(selectedImage, null);
       ContentResolver cr = getContentResolver();

       int orientation = 0;

        try {
            ExifInterface ei = new ExifInterface(selectedImage.getPath());
            orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            Log.d("Orientation", orientation + "");
            //Log.d("Orientation", selectedImage.getPath() + " " + orientation + " " + ExifInterface.ORIENTATION_ROTATE_90 + " " + ExifInterface.ORIENTATION_ROTATE_180);

        }
        catch (IOException e){
            e.printStackTrace();
        }


       try {
           bitmap = android.provider.MediaStore.Images.Media
                   .getBitmap(cr, selectedImage);

           //Log.d("OTHMANE", font.getWidth() + " " + font.getHeight());


           switch(orientation) {
               case ExifInterface.ORIENTATION_ROTATE_90: //Portrait
                   bitmap = rotate(bitmap, 90);
                   background = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.i_will);
                   bitmap = mcreateBitmap(bitmap);
                   Log.d("Rotate", "90");
                   break;
               case ExifInterface.ORIENTATION_ROTATE_180:
                   bitmap = rotate(bitmap, 180);
                   background = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.je_vais);
                   bitmap = mcreateBitmap(bitmap);
                   Log.d("Rotate", "180");
                   break;
               case 8:
                   bitmap = rotate(bitmap, -90);
                   background = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.i_will);
                   bitmap = mcreateBitmap(bitmap);
                   Log.d("Rotate", "8");
                   break;
               default:
                   background = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.je_vais);
                   bitmap = mcreateBitmap(bitmap);
                   Log.d("Rotate", "180");
                   break;

           }

           //bitmap = cropFromCenterBitmap(bitmap);

           //font.recycle();
           imageView.setImageBitmap(bitmap);
           storeImage(bitmap);
           //bitmap.recycle();


       } catch (Exception e) {
           Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT)
                   .show();
           Log.e("Camera", e.toString());
       }


       SharePhoto photo = new SharePhoto.Builder()
               .setBitmap(bitmap)
               .build();
       content = new SharePhotoContent.Builder()
               .addPhoto(photo)
               .build();
       shareButton.setShareContent(content);
       //bitmap.recycle();

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
                Environment.DIRECTORY_PICTURES), "Ana M3alem");
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

    public Bitmap mcreateBitmap(Bitmap bitmap){

        int bitmap_width = bitmap.getWidth();
        int bitmap_height = bitmap.getHeight();

        int background_width = background.getWidth();
        int background_height = background.getHeight();

        int background_center_x = background_width/2;
        int background_center_y = background_height/2;

        int h1 = background_height * 95/660;
        int h2 = background_height * 444/660;
        int h3 = background_height * 129/660;

        Bitmap bmpTemp = Bitmap.createBitmap(background_width, background_height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bmpTemp);
        Paint photoPaint = new Paint();
        photoPaint.setDither(true);
        photoPaint.setFilterBitmap(true);

        Rect src_dst_background = new Rect(0, 0, background_width, background_height);
        canvas.drawBitmap(background, src_dst_background, src_dst_background, photoPaint);

        if (bitmap_width >= bitmap_height && background_height/((background_width*bitmap_height)/bitmap_width) <= 766/408) {  //Landscape
            int new_bitmap_width = background_width;
            int new_bitmap_height = (new_bitmap_width*bitmap_height)/bitmap_width;

            Rect src_bitmap = new Rect(0, 0, bitmap_width, bitmap_height);
            Rect dst_bitmap = new Rect(0, background_center_y-new_bitmap_height/2, new_bitmap_width, background_center_y + new_bitmap_height/2);
            canvas.drawBitmap(bitmap, src_bitmap, dst_bitmap, photoPaint);

        }
        else { //Portrait

            int new_bitmap_height = h2;
            int new_bitmap_width = (new_bitmap_height*bitmap_width)/bitmap_height;

            Rect src_bitmap = new Rect(0, 0, bitmap_width, bitmap_height);
            Rect dst_bitmap = new Rect(background_center_x - new_bitmap_width/2, h1, background_center_x + new_bitmap_width/2, h1 + new_bitmap_height);
            canvas.drawBitmap(bitmap, src_bitmap, dst_bitmap, photoPaint);

        }

        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();

        return bmpTemp;


    }

    public static Bitmap rotate(Bitmap bitmap, int degree) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        Matrix mtx = new Matrix();
        mtx.postRotate(degree);

        return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
    }




}
