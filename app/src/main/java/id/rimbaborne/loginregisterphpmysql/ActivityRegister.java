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

import id.rimbaborne.loginregisterphpmysql.services.SQLite;
import id.rimbaborne.loginregisterphpmysql.services.Session;
import id.rimbaborne.loginregisterphpmysql.services.Address;
import id.rimbaborne.loginregisterphpmysql.services.Controller;
import id.rimbaborne.loginregisterphpmysql.services.SQLite;
import id.rimbaborne.loginregisterphpmysql.services.Session;


/**
 * Created by Admin on 7/1/2017.
 */

public class ActivityRegister extends Activity {
    private static final String TAG = ActivityRegister.class.getSimpleName();
    private Button buttonRegister;
    private Button buttonToLogin;
    private EditText inputName;
    private EditText inputEmail;
    private EditText inputPassword;
    private ProgressDialog Loading;

    // class yang diambil dari packages services
    private Session session;
    private SQLite dbsqlite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        inputName      = (EditText) findViewById(R.id.signup_text_name);
        inputEmail     = (EditText) findViewById(R.id.signup_text_email);
        inputPassword  = (EditText) findViewById(R.id.signup_text_password);
        buttonRegister = (Button)   findViewById(R.id.signup_btn_register);
        buttonToLogin  = (Button)   findViewById(R.id.signup_btn_tologin);

        Loading = new ProgressDialog(this);
        Loading.setCancelable(false);

        // SqLite database handler
        dbsqlite = new SQLite(getApplicationContext());

        // session manager
        session = new Session(getApplicationContext());

        // Check if user is already logged in or not
        // Memeriksa apakah user sedang login atau tidak, konsepnya seperti method onResume
        if (session.isLoggedIn()) {
            // Jika User tidak tercatat di sesiion atau telah login, Maka user automatis akan terlogout.
            Intent intent = new Intent(ActivityRegister.this,
                    ActivityUser.class);
            startActivity(intent);
            finish();
        }

        // Register Button Click event
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String name = inputName.getText().toString().trim();
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                if (!name.isEmpty() && !email.isEmpty() && !password.isEmpty()) {
                    registerUser(name, email, password);
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Please enter your details!", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });

        // Link to Login Screen
        buttonToLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        ActivityLogin.class);
                startActivity(i);
                finish();
            }
        });
    }

    /**
     * to store user in MySQL database will post params(tag, name,
     * email, password) to register url
     * Menyimpan data user ke web
     */
    private void registerUser(final String name, final String email, final String password){
        // Tag used to cancel the request
        String tag_string_req = "req_register";

        Loading.setMessage("Registering ... ");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                Address.URL_REGISTER, new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean("error");

                    // Check for error node in json
                    // jika tidak ada eror, mulai mengeksekusi proses mengam data
                    if (!error) {

                        // user successfully registered
                        // Create login session - membuat session setelah terdaftar
                        // saya gunakan untuk auto login setelah terdaftar
                        session.setLogin(true);

                        // User successfully stored in MySQL
                        // Now store the user in sqlite
                        String uid = jsonObject.getString("uid");

                        JSONObject user = jsonObject.getJSONObject("user");
                        String name = user.getString("name");
                        String email = user.getString("email");
                        String created_at = user
                                .getString("created_at");

                        // Inserting row in users table
                        // memasukkan data kedalam SQLite
                        dbsqlite.addUser(name, email, uid, created_at);

                        Toast.makeText(getApplicationContext(), "User successfully registered. Welcome!", Toast.LENGTH_LONG).show();

                        // Launch login activity after registration
                        // memanggil ActivityUser setalah terdaftar, sehingga auto login
                        Intent intent = new Intent(
                                ActivityRegister.this,
                                ActivityUser.class);
                        startActivity(intent);
                        finish();
                    } else {

                        // Error occurred in registration. Get the error message
                        // Jika terjadi error dalam pengambilan data
                        String errorMsg = jsonObject.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    // Jika terjadi eror pada proses json
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "Registration Error: " + error.getMessage());
                    Toast.makeText(getApplicationContext(),
                            error.getMessage(), Toast.LENGTH_LONG).show();
                    hideDialog();
                }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", name);
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
