package app.gree.finalapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;

import java.net.URL;



/**
 * Icon loaderClass
 * download and saves icon in png
 */

public class IconLoader {
    private Context context;

    public void saveImage(Context context, Bitmap bitmap,String name){
        name+=".png";
        FileOutputStream outputStream;
        try{
            outputStream=context.openFileOutput(name,Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.PNG,90,outputStream);
            outputStream.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    public Bitmap getBitmap(Context context,String name){
        name+=".png";
        try{
            FileInputStream fileInputStream=context.openFileInput(name);
            Bitmap b=BitmapFactory.decodeStream(fileInputStream);
            fileInputStream.close();
            return b;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public Bitmap getBitmapFromUrl(String src,String name){
        try {
            URL url=new URL(src);
            HttpURLConnection connection=(HttpURLConnection) url.openConnection();
            connection.connect();
            InputStream inputStream=connection.getInputStream();
            Bitmap bitmap= BitmapFactory.decodeStream(inputStream);
            saveImage(context,bitmap,name);
            inputStream.close();
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
