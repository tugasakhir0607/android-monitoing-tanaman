package com.example.monitoringtanaman;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import com.example.monitoringtanaman.config.Config;
import com.example.monitoringtanaman.config.SharePreference;
import com.example.monitoringtanaman.config.UserAPIServices;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {
    Button btn_masuk, btn_daftar;
    EditText et_username, et_password;
    SharePreference sharePreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Masuk");
        requestPermissions();
        sharePreference = new SharePreference(this);
        if (sharePreference.isLoggedIn() == true) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        btn_daftar = findViewById(R.id.btn_daftar);
        btn_masuk = findViewById(R.id.btn_masuk);
        et_username = findViewById(R.id.et_username);
        et_password = findViewById(R.id.et_password);
        btn_daftar.setOnClickListener(this);
        btn_masuk.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == btn_daftar){
            startActivity(new Intent(this, SignUpActivity.class));
        } else if (v == btn_masuk){
            login();
        }
    }

    private void login(){
        et_username.setError(null);
        et_password.setError(null);
        String username = et_username.getText().toString();
        String password = et_password.getText().toString();
        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(username)){
            et_username.setError("Silahkann diisi..");
            focusView = et_username;
            cancel = true;
        } else if (TextUtils.isEmpty(password)){
            et_password.setError("Silahkann diisi..");
            focusView = et_password;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            final ProgressDialog pDialog = new ProgressDialog(this);
            pDialog.setMessage("Tunggu sebentar...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

            MultipartBody.Builder builder = new MultipartBody.Builder();
            builder.setType(MultipartBody.FORM);
            builder.addFormDataPart("username_pengguna",username);
            builder.addFormDataPart("password_pengguna",password);
            MultipartBody requestBody = builder.build();

            UserAPIServices api = Config.getRetrofit(Config.URL).create(UserAPIServices.class);
            Call<ResponseBody> post = api.api_login(requestBody);
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
                            String status = c.getString("status");

                            if (status.equals("1")) {
                                String id      = c.getString("id_tb_pengguna");
                                String nama    = c.getString("nama_pengguna");
                                String username   = c.getString("username_pengguna");
                                String nohp = c.getString("nohp_pengguna");
                                String email     = c.getString("email_pengguna");
                                String sex     = c.getString("sex_pengguna");
                                String alamat     = c.getString("alamat_pengguna");

                                sharePreference.create_session(id, nama, username, email, nohp, sex, alamat);
                                Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                                startActivity(intent);
                                SignInActivity.this.finish();
                            } else {
                                Toast.makeText(getApplicationContext(), "Username dan password salah.", Toast.LENGTH_LONG).show();
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Tidak bisa mengirim data!", Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Tidak bisa mengirim data!!", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    pDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Tidak bisa mengirim data!!!", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void requestPermissions(){
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.ACCESS_FINE_LOCATION
                ).withListener(new MultiplePermissionsListener() {
            @Override public void onPermissionsChecked(MultiplePermissionsReport report) {openMultipleThings();}
            @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {}
        }).check();
    }

    private void openMultipleThings(){
//        Toast.makeText(this,"Sukses",Toast.LENGTH_LONG).show();
    }
}
