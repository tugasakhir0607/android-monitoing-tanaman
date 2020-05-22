package com.example.monitoringtanaman.menuutama.beranda.menu;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.monitoringtanaman.config.Config;
import com.example.monitoringtanaman.config.UserAPIServices;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.monitoringtanaman.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EvaluasiDetailActivity extends AppCompatActivity implements View.OnClickListener {
    Button btn_ubah, btn_cetak, btn_buka;
    EditText et_saran, et_keterangan;
    String id_tb_tanaman, saran, keterangan, foto, filename;
    ImageView imv;
    LinearLayout ln_download;
    ProgressBar mProgressBar;
    TextView progress_text;
    private static final int PERMISSION_REQUEST_CODE = 1;
    DownloadZipFileTask downloadZipFileTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluasi_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Evaluasi");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        Bundle b = getIntent().getExtras();
        id_tb_tanaman = b.getString("id_tb_tanaman");

        filename = "Monitoring Tanaman Kode Tanama "+id_tb_tanaman+".pdf";

        progress_text = findViewById(R.id.progress_text);
        ln_download = findViewById(R.id.ln_download);
        mProgressBar = findViewById(R.id.progress);
        et_keterangan = findViewById(R.id.et_keterangan);
        et_saran = findViewById(R.id.et_saran);
        imv = findViewById(R.id.imv);
        btn_ubah = findViewById(R.id.btn_ubah);
        btn_cetak = findViewById(R.id.btn_cetak);
        btn_buka = findViewById(R.id.btn_buka);
        btn_ubah.setOnClickListener(this);
        btn_cetak.setOnClickListener(this);
        btn_buka.setOnClickListener(this);

        if(checkPermission()){
            File mediaStorageDir = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), Config.IMAGE_DIRECTORY_NAME);
            }
            final File mediaFile = new File(mediaStorageDir.getPath() + File.separator + filename);
            if (mediaStorageDir.exists()) {
                if (mediaFile.exists()) {
                    btn_cetak.setVisibility(View.GONE);
                    btn_buka.setVisibility(View.VISIBLE);
                } else {
                    btn_cetak.setVisibility(View.VISIBLE);
                    btn_buka.setVisibility(View.GONE);
                }
            } else {
                mediaStorageDir.mkdirs();
                btn_cetak.setVisibility(View.VISIBLE);
                btn_buka.setVisibility(View.GONE);
            }
        } else {
            requestPermission();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }

    @Override
    public void onClick(View v) {
        if (v == btn_ubah){
            Intent i = new Intent(this, EvaluasiEditActivity.class);
            i.putExtra("id_tb_tanaman",id_tb_tanaman);
            i.putExtra("saran",saran);
            i.putExtra("keterangan",keterangan);
            i.putExtra("foto",foto);
            startActivity(i);
        } else if (v == btn_cetak){
            download();
        } else if (v == btn_buka){
            Intent i = new Intent(this, PDFActivity.class);
            i.putExtra("filename",filename);
            startActivity(i);
        }
    }

    private void download() {
        startDownload();
    }

    private void getData(){
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Tunggu sebentar...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();

        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        builder.addFormDataPart("id_tb_tanaman", id_tb_tanaman);
        MultipartBody requestBody = builder.build();

        UserAPIServices api = Config.getRetrofit(Config.URL).create(UserAPIServices.class);
        Call<ResponseBody> post = api.getEvaluasi(requestBody);
        post.enqueue(new Callback<ResponseBody>(){
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                pDialog.dismiss();
                try {
                    String json = response.body().string();
                    JSONObject jsonObj = new JSONObject(json);
                    Config.jsonArray = jsonObj.getJSONArray("result");
                    for(int i=0;i<Config.jsonArray.length();i++) {
                        JSONObject c = Config.jsonArray.getJSONObject(i);
                        saran  = c.getString("saran_evaluasi");
                        keterangan  = c.getString("keterangan_evaluasi");
                        foto  = c.getString("foto_evaluasi");

                        if (!foto.equals("null")){
                            Glide.with(EvaluasiDetailActivity.this).load(Config.URL_FOTO_EVALUASI+foto)
                                    .error(R.mipmap.not_found).diskCacheStrategy(DiskCacheStrategy.ALL).thumbnail(0.5f)
                                    .centerCrop().into(imv);
                        }
                        if (!keterangan.equals("null")){
                            et_keterangan.setText(keterangan);
                        }
                        if (!saran.equals("null")){
                            et_saran.setText(saran);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(EvaluasiDetailActivity.this, "Tidak bisa mengambil data!", Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(EvaluasiDetailActivity.this, "Tidak bisa mengambil data!!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                pDialog.dismiss();
                Toast.makeText(EvaluasiDetailActivity.this, "Internet Gagal", Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean checkPermission(){
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED){
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission(){
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(EvaluasiDetailActivity.this, "Permision Diperbolehkan..", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(EvaluasiDetailActivity.this, "Permision Tidak Diperbolehkan..", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private void startDownload(){
        ln_download.setVisibility(View.VISIBLE);
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        builder.addFormDataPart("id_tb_tanaman",id_tb_tanaman);
        MultipartBody requestBody = builder.build();

        UserAPIServices api = Config.getRetrofit(Config.URL).create(UserAPIServices.class);
        Call<ResponseBody> post = api.downloadEvaluasi(requestBody);
        post.enqueue(new Callback<ResponseBody>(){
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                downloadZipFileTask = new DownloadZipFileTask();
                downloadZipFileTask.execute(response.body());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ln_download.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "Tidak bisa mengirim data!!!", Toast.LENGTH_LONG).show();
            }
        });
    }

    private class DownloadZipFileTask extends AsyncTask<ResponseBody, Pair<Integer, Long>, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(ResponseBody... urls) {
            //Copy you logic to calculate progress and call
            saveToDisk(urls[0]);
            return null;
        }

        protected void onProgressUpdate(Pair<Integer, Long>... progress) {

            Log.d("API123", progress[0].second + " ");

            if (progress[0].first == 100){
                progress_text.setText("File downloaded successfully");
                btn_buka.setVisibility(View.VISIBLE);
                btn_cetak.setVisibility(View.GONE);
            }


            if (progress[0].second > 0) {
                int currentProgress = (int) ((double) progress[0].first / (double) progress[0].second * 100);
                mProgressBar.setProgress(currentProgress);
                progress_text.setText("Progress " + currentProgress + "%");
            }

            if (progress[0].first == -1) {
                progress_text.setText("Download failed");
            }

        }

        public void doProgress(Pair<Integer, Long> progressDetails) {
            publishProgress(progressDetails);
        }

        @Override
        protected void onPostExecute(String result) {

        }
    }

    private void saveToDisk(ResponseBody body) {
        try {
            File mediaStorageDir = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), Config.IMAGE_DIRECTORY_NAME);
            }
            final File destinationFile = new File(mediaStorageDir.getPath() + File.separator + filename);
            if(!mediaStorageDir.exists()) {
                mediaStorageDir.mkdirs();
            }

            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(destinationFile);
                byte data[] = new byte[4096];
                int count;
                int progress = 0;
                long fileSize = body.contentLength();
                while ((count = inputStream.read(data)) != -1) {
                    outputStream.write(data, 0, count);
                    progress += count;
                    Pair<Integer, Long> pairs = new Pair<>(progress, fileSize);
                    downloadZipFileTask.doProgress(pairs);
                }
                outputStream.flush();
                Pair<Integer, Long> pairs = new Pair<>(100, 100L);
                downloadZipFileTask.doProgress(pairs);
                return;
            } catch (IOException e) {
                e.printStackTrace();
                Pair<Integer, Long> pairs = new Pair<>(-1, Long.valueOf(-1));
                downloadZipFileTask.doProgress(pairs);
                return;
            } finally {
                if (inputStream != null) inputStream.close();
                if (outputStream != null) outputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }
}
