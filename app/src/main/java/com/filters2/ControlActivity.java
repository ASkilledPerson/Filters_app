package com.filters2;

import android.Manifest;
import android.app.Activity;
//import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ThemedSpinnerAdapter;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.filters2.Utility.Helper;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.imageprocessors.subfilters.BrightnessSubfilter;

import static com.filters2.R.id.centreImageView;

public class ControlActivity extends AppCompatActivity {
    static {
        System.loadLibrary("NativeImageProcessor");
    }

    Toolbar mControlToolbar;
    ImageView mTickImageView;
    ImageView mCenterImageView;

    ImageView mfirstImageView;
    ImageView msecondImageView;
    ImageView mthirdImageView;
    ImageView mfourthImageView;

    //The target function has been made into a variable so that the garbage is not deleted before it is even used.
    //It is to set the picture in the small icons
    Target mSmallTarget = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

            Bitmap workingBitmap = Bitmap.createBitmap(bitmap);
            Bitmap mutableBitmap = workingBitmap.copy(Bitmap.Config.ARGB_8888, true);

            Filter myFilter = new Filter();
            myFilter.addSubFilter(new BrightnessSubfilter(90));
            Bitmap outputImage = myFilter.processFilter(mutableBitmap);

            String filename = System.currentTimeMillis() + "_brightness.png";

            Helper.writeDataIntoExternalStorage(ControlActivity.this, filename, outputImage);

            Picasso.with(ControlActivity.this).load(Helper.getFileFromExternalStorage(ControlActivity.this, filename)).fit().centerInside().into(mfirstImageView);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };

    final static int PICK_IMAGE = 2;
    final static int MY_PERMISSIONS_REQUEST_STORAGE = 3;


    private static final String TAG = ControlActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        The setContentView binds the UI for XML file for this activity
        setContentView(R.layout.activity_control);

        mControlToolbar = (Toolbar) findViewById(R.id.toolbar);
        mCenterImageView = (ImageView) findViewById(centreImageView);

        mfirstImageView = (ImageView) findViewById(R.id.imageView4);
        msecondImageView = (ImageView) findViewById(R.id.imageView5);
        mthirdImageView = (ImageView) findViewById(R.id.imageView6);
        mfourthImageView = (ImageView) findViewById(R.id.imageView7);


        mControlToolbar.setTitle(getString(R.string.app_name));
        mControlToolbar.setNavigationIcon(R.drawable.icon);
        mControlToolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));

        mTickImageView = (ImageView) findViewById(R.id.imageView2);
        mTickImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ControlActivity.this, ImagePreviewActivity.class);
                startActivity(intent);
            }
        });

        mCenterImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                requestStoragePermissions();
                //delete not if doesn't work
                if (ContextCompat.checkSelfPermission(ControlActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }


                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });
    }



        public void onRequestPermissionResult(int requestCode, String permissions[], int[] grantResults){
            switch (requestCode){
                case MY_PERMISSIONS_REQUEST_STORAGE:
                    if(grantResults.length> 0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                        new MaterialDialog.Builder(ControlActivity.this).title( "Permission Granted")
                                .content("Thank you for providing storage permission")
                                .positiveText("OK")
                                .canceledOnTouchOutside(true)
                                .show();

                    }else {
                        Log.d(TAG, "Permission Denied");
                    }

            }
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data){
            if(requestCode== PICK_IMAGE && resultCode== Activity.RESULT_OK){
                Uri selectedImageUri= data.getData();
                Picasso.with(ControlActivity.this).load(selectedImageUri).fit().centerInside().into(mCenterImageView);

                Picasso.with(ControlActivity.this).load(R.drawable.center_image).into(mSmallTarget);

                //Picasso.with(ControlActivity.this).load(selectedImageUri).fit().centerInside().into(mfirstImageView);
                Picasso.with(ControlActivity.this).load(selectedImageUri).fit().centerInside().into(msecondImageView);
                Picasso.with(ControlActivity.this).load(selectedImageUri).fit().centerInside().into(mthirdImageView);
                Picasso.with(ControlActivity.this).load(selectedImageUri).fit().centerInside().into(mfourthImageView);

            }

        }

        public void requestStoragePermissions(){
            //checkSelfPermission is used to determine whether <em>you</em> have been granted a particular permission.
            if(ContextCompat.checkSelfPermission(ControlActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                //i.e. is the permission has not been granted
                if(ActivityCompat.shouldShowRequestPermissionRationale(ControlActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                    //if you should show Permission then the following code will display the message
                    new MaterialDialog.Builder(ControlActivity.this).title(R.string.Permission_title)
                            .content(R.string.Permission_content)
                            .negativeText(R.string.Permission_cancel)
                            .positiveText(R.string.Permission_agree_settings)
                            .canceledOnTouchOutside(true)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    startActivityForResult(new Intent(Settings.ACTION_APPLICATION_SETTINGS), 0);
                                }
                            })
                            .show();
                } else {
                    ActivityCompat.requestPermissions(ControlActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_STORAGE);
                }
                return;
            }
        }

    }
