package com.example.monitoringtanaman.config;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Environment;
import android.webkit.MimeTypeMap;

import org.json.JSONArray;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Config {

    public static final String IMAGE_DIRECTORY_NAME = "Monitoring Tanaman";
//    public static final String BASE_URL = "http://10.0.2.2/ci_monitoring_tanaman/";
    public static final String BASE_URL = "https://monta.yuminci.com/";
//    public static final String BASE_URL = "http://192.168.1.5/ci_monitoring_tanaman/";
    public static final String URL = BASE_URL+"Api/";
    public static final String URL_FOTO_TANAMAN = BASE_URL+"assets/img/tanaman/";
    public static final String URL_FOTO_GALERI = BASE_URL+"assets/img/galeri/";
    public static final String URL_FOTO_EVALUASI = BASE_URL+"assets/img/evaluasi/";
    public static JSONArray jsonArray = null;

    public static Retrofit getRetrofit(String url) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();
        return new Retrofit.Builder().baseUrl(url).client(okHttpClient).addConverterFactory(GsonConverterFactory.create()).build();
    }

    public static File setFileResize(String filePath, int index_ke){
        File imgFileOrig = new File(filePath);
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        int index = imgFileOrig.getName().lastIndexOf('.')+1;
        String ext = imgFileOrig.getName().substring(index).toLowerCase();
        String type = mime.getMimeTypeFromExtension(ext);
        int file_size = Integer.parseInt(String.valueOf(imgFileOrig.length()/1024));
        int size_resize=0;

        ExifInterface exif = null;
        try {
            exif = new ExifInterface(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
        int orientation = orientString != null ? Integer.parseInt(orientString) :  ExifInterface.ORIENTATION_NORMAL;

        int rotationAngle = 0;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;

        Bitmap b = BitmapFactory.decodeFile(imgFileOrig.getAbsolutePath());
        int origWidth = b.getWidth();
        int origHeight = b.getHeight();

        if (file_size>1000){
            origWidth = origWidth/2;
            origHeight = origHeight/2;
        }

        if (rotationAngle == 90){
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            b = Bitmap.createBitmap(b, 0, 0, origWidth, origHeight, matrix, true);
        }

        Bitmap b2;
        if (rotationAngle == 90){
            b2 = Bitmap.createScaledBitmap(b, origHeight, origWidth, false);
        } else {
            b2 = Bitmap.createScaledBitmap(b, origWidth, origHeight, false);
        }

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();

        if (file_size<=500){
            size_resize=50;
        } else if (file_size>500){
            size_resize=50;
        }

        if (type.equals("image/jpeg") || type.equals("image/jpg")){
            b2.compress(Bitmap.CompressFormat.JPEG,size_resize , outStream);
        } else if (type.equals("image/png")){
            b2.compress(Bitmap.CompressFormat.PNG,size_resize , outStream);
        }

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), Config.IMAGE_DIRECTORY_NAME);
        if(!mediaStorageDir.exists()) {
            mediaStorageDir.mkdirs();
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        File file = new File(mediaStorageDir.getPath() + File.separator + "IMG_"+ timeStamp+index_ke+ ".jpg");
        try {
            file.createNewFile();
            FileOutputStream fo = new FileOutputStream(file);
            fo.write(outStream.toByteArray());
            fo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }
}
