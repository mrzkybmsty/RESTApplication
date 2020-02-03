package winteq.restapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AlignmentSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import Helper.ConnectionHelper;
import Helper.Preferences;

public class HomeActivity extends AppCompatActivity {

    private Button btnRestIn, btnRestOut, btnReport;
    private IntentIntegrator qrScan;
    private String wo, status;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.logout, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btnLogout:
                logout();
                return true;
        }
        return false;
    }

    private void logout() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure want to logout?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Preferences.clearLoggedInUser(getBaseContext());
                        intentToLogin();
//                        loginSharedPreference.setUser(null);
//                        loginSharedPreference.setHasLogin(false);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        dialog.show();
    }

    private void intentToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getSupportActionBar().setTitle("Home");

        btnRestIn = findViewById(R.id.btnRestIn);
        btnRestOut = findViewById(R.id.btnRestOut);
        btnReport = findViewById(R.id.btnReport);

        qrScan = new IntentIntegrator(this);
        qrScan.setCaptureActivity(AnyOrientationCaptureActivity.class);
        qrScan.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        qrScan.setPrompt("Scan a barcode");
        qrScan.setOrientationLocked(false);
        qrScan.setBeepEnabled(true);

        btnRestIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qrScan.initiateScan();
            }
        });

        btnRestOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, RestOutActivity.class);
                startActivity(intent);
            }
        });

        btnReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ReportActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
            } else {
                try {
                    JSONObject obj = new JSONObject(result.getContents());
                    wo = obj.getString("wo_id");

                    try {
                        ConnectionHelper con = new ConnectionHelper();
                        Connection connect = ConnectionHelper.CONN();

                        String sp = "EXEC sp_GetStatusWO '" + wo + "'";
                        PreparedStatement ps = connect.prepareStatement(sp);

                        Log.w("query", sp);
                        ResultSet rs = ps.executeQuery();
                        if (rs.next()) {
                            status = rs.getString("wo_status");
                            Log.w("status", status);
                            connect.close();
                            rs.close();
                            ps.close();
                        }
                    } catch (SQLException e) {
                        e.getMessage();
                    }

                    if (status.equals("2")) {
                        String text = "WO Already stored in rack, Please try again";
                        Spannable centeredText = new SpannableString(text);
                        centeredText.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
                                0, text.length() - 1,
                                Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                        Toast.makeText(this, centeredText, Toast.LENGTH_LONG).show();
                    } else if (status.equals("1")){
                        Intent intent = RestInActivity.newIntent(HomeActivity.this, wo);
                        startActivity(intent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Wrong QR Code, Please try again", Toast.LENGTH_LONG).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private static long back_pressed;

    @Override
    public void onBackPressed() {
        if (back_pressed + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            finishAffinity();
        } else {
            Toast.makeText(getBaseContext(), "Press once again to quit", Toast.LENGTH_SHORT).show();
            back_pressed = System.currentTimeMillis();
        }
    }
}
