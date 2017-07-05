package id.rimbaborne.loginregisterphpmysql;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;

import id.rimbaborne.loginregisterphpmysql.services.SQLite;
import id.rimbaborne.loginregisterphpmysql.services.Session;

public class ActivityUser extends Activity {

    private TextView textName;
    private TextView textEmail;
    private Button buttonLogout;

    // class yang diambil dari packages services
    private SQLite dbsqlite;
    private Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        textName = (TextView) findViewById(R.id.user_txt_name);
        textEmail = (TextView) findViewById(R.id.user_txt_email);
        buttonLogout = (Button) findViewById(R.id.user_btn_logout);

        // SqLite database handler
        dbsqlite = new SQLite(getApplicationContext());

        // session manager
        session = new Session(getApplicationContext());

        // Check if user is already logged in or not
        // Memeriksa apakah user sedang login atau tidak, konsepnya seperti method onResume
        if (!session.isLoggedIn()) {
            // Jika User tidak tercatat di sesiion atau telah login, Maka user automatis akan terlogout.
            logoutUser();
        }

        // Fetching user details from SQLite
        // Menyiapkan data user dari SQLite
        HashMap<String, String> user = dbsqlite.getUserDetails();

        // dan yang diambil adalah data nama dan email dari SQLite
        String name = user.get("name");
        String email = user.get("email");

        // Displaying the user details on the screen
        // Menampilkan data tersebut
        textName.setText(name);
        textEmail.setText(email);

        // Logout button click event
        // Operasi jika mengklik Tombol Logout
        buttonLogout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });
    }


    private void logoutUser() {
        session.setLogin(false);

        // menghapus data di SQLite jika di logout. sehingga tidak ada penyimpana di local database
        dbsqlite.deleteUsers();

        // Launching the login activity
        // Langsung diarahkan ke halaman login
        Intent intent = new Intent(ActivityUser.this, ActivityLogin.class);
        startActivity(intent);
        finish();
    }
}
