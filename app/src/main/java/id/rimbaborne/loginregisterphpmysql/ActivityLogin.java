package id.rimbaborne.loginregisterphpmysql;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import id.rimbaborne.loginregisterphpmysql.services.Address;
import id.rimbaborne.loginregisterphpmysql.services.Controller;
import id.rimbaborne.loginregisterphpmysql.services.SQLite;
import id.rimbaborne.loginregisterphpmysql.services.Session;

/**
 * Created by Admin on 7/1/2017.
 */

public class ActivityLogin extends Activity {
    private static final String TAG = ActivityRegister.class.getSimpleName();
    private EditText inputEmail;
    private EditText inputPassword;
    private Button buttonLogin;
    private Button buttonRegister;
    private ProgressDialog Loading;

    // class yang diambil dari packages services
    private Session session;
    private SQLite dbsqlite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputEmail     = (EditText) findViewById(R.id.login_txt_email);
        inputPassword  = (EditText) findViewById(R.id.login_txt_password);
        buttonLogin    = (Button)   findViewById(R.id.login_btn_login);
        buttonRegister = (Button)   findViewById(R.id.login_btn_register);

        Loading = new ProgressDialog(this);
        Loading.setCancelable(false);

        dbsqlite = new SQLite(getApplicationContext());

        session = new Session(getApplicationContext());

        // Check if user is already logged in or not
        // Memeriksa apakah user sedang login atau tidak, konsepnya seperti method onResume
        if (session.isLoggedIn()) {
            // User is already logged in. Take to ActivityUser
            // Jika User telah login maka akan diarahkan ke ActivityUser
            Intent intent = new Intent(ActivityLogin.this, ActivityUser.class);
            startActivity(intent);
        }

        // ketika button login di klik
        buttonLogin.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                // Check for empty data in the form
                if (!email.isEmpty() && !password.isEmpty()) {
                    // login user
                    // proses email dan password
                    checkLogin(email, password);
                } else {
                    // Prompt user to enter credentials
                    // jika tidak di isi
                    Toast.makeText(getApplicationContext(),
                            "Please enter the credentials!", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });

        // ketika button register di klik
        buttonRegister.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Intent i = new Intent(getApplicationContext(),
                        ActivityRegister.class);
                startActivity(i);
                finish();
            }
        });
    }

    public void checkLogin(final String email, final String password){
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        Loading.setMessage("Logging in .. ");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                Address.URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response.toString());
                hideDialog();

                try{
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean("error");

                    // Check for error node in json
                    // jika tidak ada eror, mulai mengeksekusi proses mengam data
                    if (!error) {
                        // user successfully logged in
                        // Create login session - membuat session
                        session.setLogin(true);

                        // Now store the user in SQLite
                        // mengambil data dan dimasukkan ke dalam variabel
                        String uid = jsonObject.getString("uid");

                        JSONObject user = jsonObject.getJSONObject("user");
                        String name = user.getString("name");
                        String email = user.getString("email");
                        String created_at = user
                                .getString("created_at");

                        // Inserting row in users table
                        // memasukkan data kedalam SQLite
                        dbsqlite.addUser(name, email, uid, created_at);

                        // Launch main activity
                        // Memanggil Activity
                        Intent intent = new Intent(ActivityLogin.this,
                                ActivityUser.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // Error in login. Get the error message
                        // Jika terjadi error dalam pengambilan data
                        String errorMsg = jsonObject.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                }  catch (JSONException e) {
                    // JSON error
                    // Jika terjadi eror pada proses json
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // terjadi ketidak sesuain data user pada saat login
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("password", password);

                return params;
            }

        };
        // Adding request to request queue
        // menambahkan request dalam antrian system request data
        Controller.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    // menampilkan dialog untuk loading
    private void showDialog() {
        if (!Loading.isShowing())
            Loading.show();
    }

    // menyembunyikan dialog jika loading selesai
    private void hideDialog() {
        if (Loading.isShowing())
            Loading.dismiss();
    }
}
