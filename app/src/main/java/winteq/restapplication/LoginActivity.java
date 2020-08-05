package winteq.restapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AlignmentSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import Helper.ConnectionHelper;
import Helper.Preferences;

public class LoginActivity extends AppCompatActivity {

    private EditText txtUname, txtPass;
    private ImageView ivBack;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtUname = findViewById(R.id.txtUsername);
        txtPass = findViewById(R.id.txtPassword);
        ivBack = findViewById(R.id.ivBack);
        btnLogin = findViewById(R.id.btnLogin);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DoLoginForUser login = new DoLoginForUser();
                login.execute("");
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Preferences.getLoggedInStatus(getBaseContext())) {
            startActivity(new Intent(getBaseContext(), HomeActivity.class));
            finish();
        }
    }

    private class DoLoginForUser extends AsyncTask<String, Void, String> {
        String username, password, role, name, id, stat;
        TextView uname, pass;

        View fokus = null;
        boolean cancel = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            username = txtUname.getText().toString();
            password = txtPass.getText().toString();

            uname = txtUname;
            pass = txtPass;

            txtUname.setError(null);
            txtPass.setError(null);

        }

        @Override
        protected String doInBackground(String... params) {

            try {
                ConnectionHelper con = new ConnectionHelper();
                Connection connect = ConnectionHelper.CONN();

                String query = "EXEC rst_checkLogin '" + username + "'";
                PreparedStatement ps = connect.prepareStatement(query);

                Log.w("query", query);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    String passcode = rs.getString("usr_password");
                    role = rs.getString("usr_role");
                    name = rs.getString("usr_name");
                    id = rs.getString("usr_id");
                    stat = rs.getString("usr_status");

                    Preferences.setUserId(getBaseContext(), id);

                    connect.close();
                    rs.close();
                    ps.close();
                    if (passcode != null && !passcode.trim().equals("") && passcode.equals(password) && stat.equals("Active")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (TextUtils.isEmpty(username)) {
                                    fokus = txtUname;
                                    cancel = true;
                                } else if (!cekUser(username)) {
                                    fokus = txtUname;
                                    cancel = true;
                                }

                                if (TextUtils.isEmpty(password)) {
                                    fokus = txtPass;
                                    cancel = true;
                                } else if (!cekPassword(password)) {
                                    fokus = txtPass;
                                    cancel = true;
                                }

                                /* Jika cancel true, variable fokus mendapatkan fokus */
                                if (cancel) {
                                    fokus.requestFocus();
                                    masuk();
                                }
                            }
                        });
                        return "Login Success";
                    } else if (passcode != null && !passcode.trim().equals("") && !passcode.equals(password) && stat.equals("Active")) {
                        return "Invalid Credentials";
                    } else if (passcode != null && !passcode.trim().equals("") && passcode.equals(password) && stat.equals("Inactive")) {
                        return "Inactive Account";
                    } else
                        return "Failed login";
                } else if (username.equals("") || password.equals("")) {
                    return "Please insert username or password";
                } else {
                    return "";
                }
            } catch (Exception e) {
                return "Error:" + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {

            Spannable centeredText = new SpannableString(result);
            centeredText.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
                    0, result.length() - 1,
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            Toast.makeText(LoginActivity.this, centeredText, Toast.LENGTH_SHORT).show();

//            ShowSnackBar(result);
//            btnLogin.setVisibility(View.VISIBLE);
            if (result.equals("Login Success") && role.equals("Operator")) {
                String text = "Welcome, " + name;
                Spannable centeredNotifOp = new SpannableString(text);
                centeredNotifOp.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
                        0, text.length() - 1,
                        Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                Toast.makeText(LoginActivity.this, centeredNotifOp, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            } else if (result.equals("Login Success") && role.equals("Admin")) {
                String text = name + ", anda admin";
                Spannable centeredNotifAdmin = new SpannableString(text);
                centeredNotifAdmin.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
                        0, text.length() - 1,
                        Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                Toast.makeText(LoginActivity.this, centeredNotifAdmin, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(LoginActivity.this, centeredText, Toast.LENGTH_SHORT).show();
//                ShowSnackBar(result);
            }
        }
    }

    private static long back_pressed;

    @Override
    public void onBackPressed() {
        if (back_pressed + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            finishAffinity();
        } else {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    /**
     * Menuju ke MainActivity dan Set User dan Status sedang login, di Preferences
     */
    private void masuk() {
        Preferences.setLoggedInUser(getBaseContext(), Preferences.getRegisteredUser(getBaseContext()));
        Preferences.setLoggedInStatus(getBaseContext(), true);
        startActivity(new Intent(getBaseContext(), HomeActivity.class));
        finish();
    }

    /**
     * True jika parameter password sama dengan data password yang terdaftar dari Preferences
     */
    private boolean cekPassword(String password) {
        return password.equals(Preferences.getRegisteredPass(getBaseContext()));
    }

    /**
     * True jika parameter user sama dengan data user yang terdaftar dari Preferences
     */
    private boolean cekUser(String user) {
        return user.equals(Preferences.getRegisteredUser(getBaseContext()));
    }
}
