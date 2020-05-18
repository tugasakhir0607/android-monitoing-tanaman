package com.example.monitoringtanaman;

import android.app.ProgressDialog;
import android.os.Bundle;

import com.example.monitoringtanaman.config.Config;
import com.example.monitoringtanaman.config.SharePreference;
import com.example.monitoringtanaman.config.UserAPIServices;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {
    private String nama, username, email, nohp, sex, alamat, password, password_ulangi;
    private SharePreference sharePreference;
    private EditText et_nama, et_username, et_email, et_nohp, et_alamat, et_password, et_password_ulangi;
    private RadioGroup rg_sex;
    private Button btn_daftar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Daftar");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        et_nama = findViewById(R.id.et_nama);
        et_username = findViewById(R.id.et_username);
        et_email = findViewById(R.id.et_email);
        et_nohp = findViewById(R.id.et_nohp);
        et_alamat = findViewById(R.id.et_alamat);
        et_password = findViewById(R.id.et_password);
        et_password_ulangi = findViewById(R.id.et_password_ulangi);
        rg_sex = findViewById(R.id.rg_sex);
        btn_daftar = findViewById(R.id.btn_daftar);
        btn_daftar.setOnClickListener(this);
        rg_sex.setOnCheckedChangeListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == btn_daftar){
            simpan();
        }
    }

    private void simpan() {
        et_nama.setError(null);
        et_username.setError(null);
        et_email.setError(null);
        et_nohp.setError(null);
        et_alamat.setError(null);
        et_password.setError(null);
        et_password_ulangi.setError(null);
        nama = et_nama.getText().toString();
        username = et_username.getText().toString();
        email = et_email.getText().toString();
        nohp = et_nohp.getText().toString();
        alamat = et_alamat.getText().toString();
        password = et_password.getText().toString();
        password_ulangi = et_password_ulangi.getText().toString();
        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(nama)){
            et_nama.setError("Silahkann diisi..");
            focusView = et_nama;
            cancel = true;
        } else if (TextUtils.isEmpty(username)){
            et_username.setError("Silahkann diisi..");
            focusView = et_username;
            cancel = true;
        } else if (TextUtils.isEmpty(email)){
            et_email.setError("Silahkann diisi..");
            focusView = et_email;
            cancel = true;
        } else if (TextUtils.isEmpty(nohp)){
            et_nohp.setError("Silahkann diisi..");
            focusView = et_nohp;
            cancel = true;
        } else if (TextUtils.isEmpty(alamat)){
            et_alamat.setError("Silahkann diisi..");
            focusView = et_alamat;
            cancel = true;
        } else if (TextUtils.isEmpty(password)){
            et_password.setError("Silahkann diisi..");
            focusView = et_password;
            cancel = true;
        } else if (TextUtils.isEmpty(password_ulangi)){
            et_password_ulangi.setError("Silahkann diisi..");
            focusView = et_password_ulangi;
            cancel = true;
        } else if (!password_ulangi.equals(password)){
            et_password_ulangi.setError("Password tidak sama..");
            focusView = et_password_ulangi;
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
            builder.addFormDataPart("nama_pengguna",nama);
            builder.addFormDataPart("username_pengguna",username);
            builder.addFormDataPart("email_pengguna",email);
            builder.addFormDataPart("nohp_pengguna",nohp);
            builder.addFormDataPart("sex_pengguna",sex);
            builder.addFormDataPart("alamat_pengguna",alamat);
            builder.addFormDataPart("password_pengguna",password_ulangi);
            MultipartBody requestBody = builder.build();

            UserAPIServices api = Config.getRetrofit(Config.URL).create(UserAPIServices.class);
            Call<ResponseBody> post = api.api_signup(requestBody);
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
                                Toast.makeText(getApplicationContext(), "Berhasil Mendaftar.", Toast.LENGTH_LONG).show();
                                onBackPressed();
                            } else {
                                Toast.makeText(getApplicationContext(), "Gagal Mendaftar.", Toast.LENGTH_LONG).show();
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

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId){
            case R.id.rb_laki_laki:
                sex = "Laki-Laki";
                break;
            case R.id.rb_perempuan:
                sex = "Perempuan";
                break;
        }
    }
}
