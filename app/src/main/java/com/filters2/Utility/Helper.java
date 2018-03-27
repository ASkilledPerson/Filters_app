package com.filters2.Utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.annotation.NonNull;

import com.filters2.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Created by Smriti on 7/6/2017.
 */

public class Helper {


    public static Boolean writeDataIntoExternalStorage(Context context, String filename, Bitmap bitmap){
        File directory = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+context.getString(R.string.app_name));

        if(!directory.exists() && !directory.mkdirs()){
            return false;
        }
        File file = new File(directory.getAbsolutePath()+"/"+filename);
        if(file.exists() && !(file.canWrite())){
            return false;
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            return bitmap.compress(Bitmap.CompressFormat.PNG,100,fileOutputStream);
            } catch (FileNotFoundException e){
            e.printStackTrace();
            return false;
        }
    }

    public static File getFileFromExternalStorage( Context context, String filename) {
        File directory= new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+context.getString(R.string.app_name));

        File file= new File(directory.getAbsolutePath()+"/"+filename);

        if(!file.exists() || !file.canWrite()){
            return null;
        }

        return file;
    }

}
