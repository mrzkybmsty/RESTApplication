package winteq.restapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AlignmentSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import Helper.ConnectionHelper;
import Helper.Preferences;

public class RestInRecommendActivity extends AppCompatActivity {

    private static final String EXTRA_LAB = "winteq.restapplication.lab_id";

    public static String lab_id, uuid;

    public static TextView txtRecRack, txtRackPosition;
    public static EditText txtScanRack;
    public static Button btnVerify;

    public static Intent newIntent(Context packageContext, String id) {
        Intent i = new Intent(packageContext, RestInRecommendActivity.class);
        i.putExtra(EXTRA_LAB, id);
        lab_id = id;
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rest_in_recommend);

        txtRecRack = findViewById(R.id.txtRackRec);
        txtRackPosition = findViewById(R.id.txtRack);
        txtScanRack = findViewById(R.id.txtRackRestInCheck);
        btnVerify = findViewById(R.id.btnVerify);
        btnVerify.setEnabled(false);

        uuid = UUID.randomUUID().toString();

        txtScanRack.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    try {
                        ConnectionHelper con = new ConnectionHelper();
                        Connection connect = ConnectionHelper.CONN();

                        String sp_get = "EXEC rst_getRackPosition '" + txtScanRack.getText().toString() + "'";
                        PreparedStatement ps_get = connect.prepareStatement(sp_get);
                        ResultSet rs_get = ps_get.executeQuery();
                        if (rs_get.next()) {
                            if (!rs_get.getString("rde_label").equals("Nothing")) {
                                txtRackPosition.setText(rs_get.getString("rde_label"));
                                rs_get.close();
                                ps_get.close();
                                btnVerify.setEnabled(true);
                            } else {
                                String text = "Rack is full";
                                Spannable centeredText = new SpannableString(text);
                                centeredText.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
                                        0, text.length() - 1,
                                        Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                                Toast.makeText(RestInRecommendActivity.this, centeredText, Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            String text = "Wrong Rack ID, Please try again";
                            Spannable centeredText = new SpannableString(text);
                            centeredText.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
                                    0, text.length() - 1,
                                    Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                            Toast.makeText(RestInRecommendActivity.this, centeredText, Toast.LENGTH_SHORT).show();
                        }
                    } catch (SQLException e) {
                        e.getMessage();
                    }
                } else {
                    txtScanRack.setText("");
                }
            }
        });

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(RestInRecommendActivity.this)
                        .setTitle("Confirmation")
                        .setMessage("Are you sure?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    ConnectionHelper con = new ConnectionHelper();
                                    Connection connect = ConnectionHelper.CONN();

                                    String sp = "EXEC rst_updateLabelRestIn '" + lab_id + "'" + "," + "'" + txtScanRack.getText().toString() + "'" + "," + "'" + uuid + "'" + "," + "'" + Preferences.getUserId(getBaseContext()) + "'";
                                    PreparedStatement ps = connect.prepareStatement(sp);
                                    ps.executeQuery();
                                } catch (SQLException e) {
                                    e.getMessage();
                                }

                                AlertDialog.Builder builder = new AlertDialog.Builder(RestInRecommendActivity.this)
                                        .setTitle("Notification");
                                LayoutInflater inflater = getLayoutInflater();
                                View dialogLayout = inflater.inflate(R.layout.alert_dialog_with_imageview, null);
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = ConfirmRestInActivity.newIntent(RestInRecommendActivity.this, lab_id, txtRackPosition.getText().toString());
                                        startActivity(intent);
                                    }
                                });
                                builder.setCancelable(false);
                                builder.setView(dialogLayout);
                                builder.show();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                dialog.show();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            ConnectionHelper con = new ConnectionHelper();
            Connection connect = ConnectionHelper.CONN();

            String sp_get = "EXEC rst_getDetailRackRec '" + lab_id + "'";
            PreparedStatement ps_get = connect.prepareStatement(sp_get);
            ResultSet rs_get = ps_get.executeQuery();
            rs_get.next();
            txtRecRack.setText(rs_get.getString("rde_label"));
            rs_get.close();
            ps_get.close();
        } catch (SQLException e) {
            e.getMessage();
        }
    }
}