package winteq.restapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AlignmentSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import Helper.ConnectionHelper;

public class RestInActivity extends AppCompatActivity {

    private Button btnNext;
    private static TextView txtLabelRestIn, txtLabelNumber, txtBattName, txtQty;

//    Integer cekQR;
//
//    private IntentIntegrator qrScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rest_in);

        getSupportActionBar().setTitle("Rest In");

        txtLabelRestIn = findViewById(R.id.txtRestInCheck);
        txtLabelNumber = findViewById(R.id.txtLabelNumber);
        txtBattName = findViewById(R.id.txtBatteryNameRI);
        txtQty = findViewById(R.id.txtQuantity);
        btnNext = findViewById(R.id.btnNext);
        btnNext.setEnabled(false);

        txtLabelRestIn.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    try {
                        btnNext.setEnabled(false);
                        btnNext.setBackgroundResource(R.drawable.button_standard);
                        ConnectionHelper con = new ConnectionHelper();
                        Connection connect = ConnectionHelper.CONN();

                        String sp = "EXEC rst_getDetailLabelUnload '" + txtLabelRestIn.getText().toString() + "'";
                        PreparedStatement ps = connect.prepareStatement(sp);
                        ResultSet rs = ps.executeQuery();
                        if (rs.next()) {
                            txtLabelNumber.setText(txtLabelRestIn.getText().toString());
                            txtBattName.setText(rs.getString("bat_name"));
                            txtQty.setText(rs.getString("lab_quantity"));
                            btnNext.setEnabled(true);
                            btnNext.setBackgroundResource(R.drawable.tags_rounded_corners);
                            rs.close();
                            ps.close();
                        } else {
                            String text = "Wrong Label, Please try again";
                            Spannable centeredText = new SpannableString(text);
                            centeredText.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
                                    0, text.length() - 1,
                                    Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                            Toast.makeText(RestInActivity.this, centeredText, Toast.LENGTH_SHORT).show();
                        }
                    } catch (SQLException e) {
                        e.getMessage();
                    }
                } else {
                    txtLabelRestIn.setText("");
                    txtLabelNumber.setText("");
                    txtBattName.setText("");
                    txtQty.setText("");
                    btnNext.setEnabled(false);
                    btnNext.setBackgroundResource(R.drawable.button_standard);
                }
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = RestInRecommendActivity.newIntent(RestInActivity.this, txtLabelRestIn.getText().toString());
                startActivity(intent);
            }
        });
    }

}
//        qrScan = new IntentIntegrator(this);
//        qrScan.setCaptureActivity(AnyOrientationCaptureActivity.class);
//        qrScan.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
//        qrScan.setPrompt("Scan a barcode");
//        qrScan.setOrientationLocked(false);
//        qrScan.setBeepEnabled(true);

//        btnVerify.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(RestInActivity.this);
//                builder.setTitle("Confirmation");
//                builder.setMessage("Are you sure?");
//                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        Intent intent = ConfirmRestInActivity.newIntent(RestInActivity.this, no_wo, category_name, wo_battery_amount, rack_pos, rack_detail);
//                        startActivity(intent);
//                    }
//                });
//                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                    }
//                });
//                builder.setCancelable(false);
//                builder.show();
//            }
//        });

//        btnScanRack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                cekQR = 2;
//                qrScan.initiateScan();
//            }
//        });
//        CheckWO wo = new CheckWO();
//        wo.execute("");


//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
//        if (result != null) {
//            if (result.getContents() == null) {
//                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
//            } else {
//                if (cekQR == 1) {
//
//                } else if (cekQR == 2) {
////                    try {
////                        JSONObject obj = new JSONObject(result.getContents());
////                        rack_id = obj.getString("rack_id");
//                        rack_id = result.getContents();
//                        try {
//                            ConnectionHelper con = new ConnectionHelper();
//                            Connection connect = ConnectionHelper.CONN();
//
//                            String sp = "EXEC sp_GetRack '" + rack_id + "'";
//                            PreparedStatement ps = connect.prepareStatement(sp);
//
//                            Log.w("query", sp);
//                            ResultSet rs = ps.executeQuery();
//                            if (rs.next()) {
//                                rack_detail = rs.getString("rack_detail_id");
//                                rack_pos = rs.getString("rack_position");
//                                rack_detail_status = rs.getString("rack_detail_status");
//                                connect.close();
//                                rs.close();
//                                ps.close();
//
//                                txtRack.setText(rack_pos);
//                                btnVerify.setEnabled(true);
//                            } else {
//                                btnVerify.setEnabled(false);
//                                String text = "Wrong QR Code, Please try again";
//                                Spannable centeredText = new SpannableString(text);
//                                centeredText.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
//                                        0, text.length() - 1,
//                                        Spannable.SPAN_INCLUSIVE_INCLUSIVE);
//                                Toast.makeText(this, centeredText, Toast.LENGTH_LONG).show();
//                            }
//                        } catch (SQLException e) {
//                            e.getMessage();
//                        }
//
////                    } catch (JSONException e) {
////                        e.printStackTrace();
////                        Toast.makeText(this, "Wrong QR Code, Please try again", Toast.LENGTH_LONG).show();
////                    }
//                }
//            }
//        } else {
//            super.onActivityResult(requestCode, resultCode, data);
//        }
//    }
//
//    private class CheckWO extends AsyncTask<String, Void, String> {
//
//        @Override
//        protected String doInBackground(String... strings) {
//            try {
//                ConnectionHelper con = new ConnectionHelper();
//                Connection connect = ConnectionHelper.CONN();
//
//                String sp = "EXEC sp_GetWO '" + no_wo + "'";
//                PreparedStatement ps = connect.prepareStatement(sp);
//
//                Log.w("query", sp);
//                ResultSet rs = ps.executeQuery();
//                if (rs.next()) {
//                    category_name = rs.getString("category_name");
//                    wo_battery_amount = rs.getString("wo_battery_amount");
//                    rack_id_qr = rs.getString("rack_id");
//                    position = rs.getString("rack_position");
//                    connect.close();
//                    rs.close();
//                    ps.close();
//                    return "Success";
//                } else
//                    return "Failed";
//            } catch (SQLException e) {
//                return "Error : " + e.getMessage();
//            }
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            if (result.equals("Success")) {
//                txtType.setText(category_name);
//                txtQty.setText(wo_battery_amount);
//                txtRackRec.setText(position);
//            } else {
//                Toast.makeText(RestInActivity.this, "Wrong WO Number, Please try again", Toast.LENGTH_LONG).show();
//                btnVerify.setEnabled(false);
//            }
//        }
//    }

