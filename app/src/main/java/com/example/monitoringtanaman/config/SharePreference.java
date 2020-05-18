package com.example.monitoringtanaman.config;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.SigningInfo;

import com.example.monitoringtanaman.SignInActivity;

import java.util.HashMap;

public class SharePreference {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "monitoring_tanaman";
    private static final String IS_LOGIN = "IsLoggedIn";
    public static final String KEY_ID_PENGGUNA = "id_tb_pengguna";
    public static final String KEY_NAMA_PENGGUNA = "nama_pengguna";
    public static final String KEY_USERNAME_PENGGUNA = "username_pengguna";
    public static final String KEY_EMAIL_PENGGUNA = "email_pengguna";
    public static final String KEY_NOHP_PENGGUNA = "nohp_pengguna";
    public static final String KEY_SEX_PENGGUNA = "sex_pengguna";
    public static final String KEY_ALAMAT_PENGGUNA = "alamat_pengguna";

    public SharePreference(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void create_session(String id, String nama, String username, String email, String nohp, String sex, String alamat){
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_ID_PENGGUNA, id);
        editor.putString(KEY_NAMA_PENGGUNA, nama);
        editor.putString(KEY_USERNAME_PENGGUNA, username);
        editor.putString(KEY_EMAIL_PENGGUNA, email);
        editor.putString(KEY_NOHP_PENGGUNA, nohp);
        editor.putString(KEY_SEX_PENGGUNA, sex);
        editor.putString(KEY_ALAMAT_PENGGUNA, alamat);
        editor.commit();
    }

    public void update(String nama, String username, String email, String nohp, String sex, String alamat){
        editor.putString(KEY_NAMA_PENGGUNA, nama);
        editor.putString(KEY_USERNAME_PENGGUNA, username);
        editor.putString(KEY_EMAIL_PENGGUNA, email);
        editor.putString(KEY_NOHP_PENGGUNA, nohp);
        editor.putString(KEY_SEX_PENGGUNA, sex);
        editor.putString(KEY_ALAMAT_PENGGUNA, alamat);
        editor.commit();
    }

    public void logoutUser(){
        editor.clear();
        editor.commit();

        Intent i = new Intent(_context, SignInActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        _context.startActivity(i);
    }

    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();

        user.put(KEY_ID_PENGGUNA, pref.getString(KEY_ID_PENGGUNA, null));
        user.put(KEY_NAMA_PENGGUNA, pref.getString(KEY_NAMA_PENGGUNA, null));
        user.put(KEY_USERNAME_PENGGUNA, pref.getString(KEY_USERNAME_PENGGUNA, null));
        user.put(KEY_EMAIL_PENGGUNA, pref.getString(KEY_EMAIL_PENGGUNA, null));
        user.put(KEY_NOHP_PENGGUNA, pref.getString(KEY_NOHP_PENGGUNA, null));
        user.put(KEY_SEX_PENGGUNA, pref.getString(KEY_SEX_PENGGUNA, null));
        user.put(KEY_ALAMAT_PENGGUNA, pref.getString(KEY_ALAMAT_PENGGUNA, null));
        return user;
    }

    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }
}
