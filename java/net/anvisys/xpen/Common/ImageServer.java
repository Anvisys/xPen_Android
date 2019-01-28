package net.anvisys.xpen.Common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Base64;

import net.anvisys.xpen.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageServer {

    private static String AppName = "XPEN";
    private static String directoryName = "images";

    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        try {
            String state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                return true;
            }
            else
            {
                return false;
            }

        }
        catch (Exception ex)
        {
            return false;
        }
    }

    public static boolean SaveFileToExternal(byte[] b, String FileName, Context mContext)
    {
        try
        {
            if(isExternalStorageWritable()) {
                File root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                File myDir = new File(root + "/"+ AppName);
                myDir.mkdirs();
                //File directory = mContext.getDir(directoryName, Context.MODE_PRIVATE);
                //File directory = getAlbumStorageDir("LetsMeet");
                File file = new File(myDir, FileName);
                // file.createNewFile();

                FileOutputStream fos = new FileOutputStream(file);
                fos.write(b,0,b.length);
                fos.flush();
                fos.close();

                MediaScannerConnection.scanFile(mContext,
                        new String[]{file.toString()}, null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            public void onScanCompleted(String path, Uri uri) {

                            }
                        });
                return true;
            }
            else
            {return false;}
        }
        catch (Exception ex)
        {
            return false;
        }
    }
    public static void SaveStringAsBitmap(String strImage,String name, Context context) {
        FileOutputStream fileOutputStream = null;
        try {

            File directory = context.getDir(directoryName, Context.MODE_PRIVATE);
            File file = new File(directory, name);
            fileOutputStream = new FileOutputStream(file);

            byte[] imgByte = Base64.decode(strImage, Base64.DEFAULT);
            Bitmap bmp = BitmapFactory.decodeByteArray(imgByte, 0, imgByte.length);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Bitmap getBitmapFromString(String strImage, Context context)
    {
        Bitmap bmp;
        try {
            byte[] imgByte = Base64.decode(strImage, Base64.DEFAULT);
            bmp = BitmapFactory.decodeByteArray(imgByte, 0, imgByte.length);
            return bmp;
        }
        catch (Exception ex)
        {
            int a =5;
            return GetDefaultImage(context);
        }
    }

    public static Bitmap GetImageBitmapFromExternal(String fileName, Context context)
    {
        Bitmap thumbnail = null;
        try {
            if(isExternalStorageWritable()) {
                File root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                File myDir = new File(root + "/"+ AppName);
                myDir.mkdirs();
                // File directory = context.getDir(directoryName, Context.MODE_PRIVATE);
                //File directory = getAlbumStorageDir("LetsMeet");
                File file = new File(myDir, fileName);
                FileInputStream fi = new FileInputStream(file);
                thumbnail = BitmapFactory.decodeStream(fi);
            }
            else
            {
                thumbnail = GetDefaultImage(context);
            }
        }
        catch (Exception ex)
        {
            thumbnail = GetDefaultImage(context);
        }
        return thumbnail;
    }

    public static Bitmap GetDefaultImage(Context context)
    {
        Bitmap defBMP = null;
        try {
            Drawable d = ResourcesCompat.getDrawable(context.getResources(), R.drawable.user_image, null);
            defBMP = ((BitmapDrawable) d).getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            defBMP.compress(Bitmap.CompressFormat.JPEG, 100, stream);

        }
        catch (Exception ex)
        {

        }
        return defBMP;
    }

    public static String getStringFromBitmap(Bitmap bitmapPicture) {
        try {
            final int COMPRESSION_QUALITY = 100;

            String encodedImage;
            ByteArrayOutputStream byteArrayBitmapStream = new ByteArrayOutputStream();
            bitmapPicture.compress(Bitmap.CompressFormat.JPEG, COMPRESSION_QUALITY, byteArrayBitmapStream);

            byte[] ImageByte = byteArrayBitmapStream.toByteArray();


            encodedImage = Base64.encodeToString(ImageByte, Base64.DEFAULT);
            return encodedImage;
        }
        catch (Exception ex)
        {
            return "";
        }
    }

    public static byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }
}
