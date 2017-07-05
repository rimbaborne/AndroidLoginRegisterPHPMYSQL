package id.rimbaborne.loginregisterphpmysql.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by Admin on 7/1/2017.
 * Menyimpan sesi login agar kapanpun aplikasi dibuka tetap login sebagai user masing-masing
 */

public class Session {
    private static final String TAG = Session.class.getSimpleName();

    SharedPreferences sPref;
    SharedPreferences.Editor editor;
    Context _context;

    // shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "userlogin";

    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";

    //deklarasi penggunaan session
    public Session(Context context){
        this._context = context;
        sPref  = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = sPref.edit();
    }

    //sebagai penanda - digunakan saat user akan login
    public void setLogin(boolean isLoggedIn){
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);
        editor.commit();
        Log.d(TAG, "User Login Session Modified");
    }

    public boolean isLoggedIn(){
        return sPref.getBoolean(KEY_IS_LOGGED_IN, false);
    }
}
