package winteq.restapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.AlignmentSpan;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

public class RestOutActivity extends AppCompatActivity {

    EditText noWo;
    Button btnTake;
    TextView txtName, txtQtyNeed, txtQtyTaken;
    String id, wo, rack_wo, rack_qr, qty, type, rack_id;
    Integer cekQR;
//    private IntentIntegrator qrScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rest_out);

        getSupportActionBar().setTitle("Rest Out");

        noWo = findViewById(R.id.txtWORestOut);

        txtName = findViewById(R.id.txtBatteryNameRO);
        txtQtyNeed = findViewById(R.id.txtQtyNeededRO);
        txtQtyTaken = findViewById(R.id.txtQtyTakenRO);

        btnTake = findViewById(R.id.btnTake);

//        qrScan = new IntentIntegrator(RestOutActivity.this);
//        qrScan.setCaptureActivity(AnyOrientationCaptureActivity.class);
//        qrScan.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
//        qrScan.setPrompt("Scan a barcode");
//        qrScan.setOrientationLocked(false);
//        qrScan.setBeepEnabled(true);

        btnTake.setEnabled(false);

        noWo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    try {
                        String wo = noWo.getText().toString();
                        String[] separated = wo.split(",");

                        noWo.setText(separated[0]);
                        txtName.setText(separated[1]);

                        try {
                            ConnectionHelper con = new ConnectionHelper();
                            Connection connect = ConnectionHelper.CONN();

                            String sp = "EXEC rst_checkBatteryName '" + txtName.getText().toString() + "'";
                            PreparedStatement ps = connect.prepareStatement(sp);
                            ResultSet rs = ps.executeQuery();
                            rs.next();
                            if (rs.getString(1).equals("Yes")) {
                                rs.close();
                                txtQtyNeed.setText(separated[2]);
                                txtQtyTaken.setText("0");
                                btnTake.setEnabled(true);

                                String sp_check = "EXEC rst_checkWORestOut '" + noWo.getText().toString() + "'";
                                PreparedStatement ps_check = connect.prepareStatement(sp_check);
                                ResultSet rs_check = ps_check.executeQuery();
                                rs_check.next();
                                if (rs_check.getString(1).equals("No")) {
                                    rs_check.close();
                                    String sp_insert = "EXEC rst_insertWORestOut '" + noWo.getText().toString() + "'" + "," + "'" + txtName.getText().toString() + "'" + "," + "'" + txtQtyNeed.getText().toString() + "'" + "," + "'" + Preferences.getUserId(getBaseContext()) + "'";
                                    PreparedStatement ps_insert = connect.prepareStatement(sp_insert);
                                    ResultSet rs_insert = ps_insert.executeQuery();
                                    rs_insert.close();
                                } else if (rs_check.getString(1).equals("Yes")) {
                                    rs_check.close();
                                    String sp_get = "EXEC rst_getDetailWORestOut '" + noWo.getText().toString() + "'";
                                    PreparedStatement ps_get = connect.prepareStatement(sp_get);
                                    ResultSet rs_get = ps_get.executeQuery();
                                    if (rs_get.next()) {
                                        txtName.setText(rs_get.getString("bat_name"));
                                        txtQtyNeed.setText(rs_get.getString("wor_quantity_target"));
                                        txtQtyTaken.setText(rs_get.getString("wor_quantity_counting"));
                                        rs_get.close();
                                    }
                                }
                                if (Integer.parseInt(txtQtyTaken.getText().toString()) >= Integer.parseInt(txtQtyNeed.getText().toString())) {
                                    btnTake.setEnabled(false);
                                } else {
                                    btnTake.setEnabled(true);
                                }
                            } else if (rs.getString(1).equals("No")) {
                                rs.close();
                                String text = "Wrong Battery Name, Please try again";
                                Spannable centeredText = new SpannableString(text);
                                centeredText.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
                                        0, text.length() - 1,
                                        Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                                Toast.makeText(RestOutActivity.this, centeredText, Toast.LENGTH_SHORT).show();
                            } else if (rs.getString(1).equals("Empty")) {
                                rs.close();
                                String text = "No available battery in rack";
                                Spannable centeredText = new SpannableString(text);
                                centeredText.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
                                        0, text.length() - 1,
                                        Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                                Toast.makeText(RestOutActivity.this, centeredText, Toast.LENGTH_SHORT).show();
                            }
                        } catch (SQLException e) {
                            e.getMessage();
                        }
                    } catch (Exception e) {
                        Toast.makeText(RestOutActivity.this, "Wrong format", Toast.LENGTH_SHORT).show();
                    }

//                    try {
//                        ConnectionHelper con = new ConnectionHelper();
//                        Connection connect = ConnectionHelper.CONN();
//
//                        String sp = "EXEC sp_GetWORestOut '" + wo + "'";
//                        PreparedStatement ps = connect.prepareStatement(sp);
//
//                        Log.w("query", sp);
//                        ResultSet rs = ps.executeQuery();

//                        if (rs.next()) {
//                            id = rs.getString("wo_id");
//                            rack_id = rs.getString("rack_detail_id");
//                            type = rs.getString("category_name");
//                            rack_wo = rs.getString("rack_position");
//                            qty = rs.getString("wo_battery_amount");
//                            connect.close();
//                            rs.close();
//                            ps.close();
//
//                            if (qty.equals("0")) {
//                                String text = "Battery amount already empty, Please try again";
//                                Spannable centeredText = new SpannableString(text);
//                                centeredText.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
//                                        0, text.length() - 1,
//                                        Spannable.SPAN_INCLUSIVE_INCLUSIVE);
//                                Toast.makeText(RestOutActivity.this, centeredText, Toast.LENGTH_SHORT).show();
//                            } else if (wo.equals(id) && !wo.equals("")) {
//                                tvType.setText(type);
//                                tvRack.setText(rack_wo);
//                                tvQty.setText(qty);
//                                btnTake.setEnabled(true);
//                            } else {
//                                btnTake.setEnabled(false);
//                                String text = "Wrong QR Code, Please try again";
//                                Spannable centeredText = new SpannableString(text);
//                                centeredText.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
//                                        0, text.length() - 1,
//                                        Spannable.SPAN_INCLUSIVE_INCLUSIVE);
//                                Toast.makeText(RestOutActivity.this, centeredText, Toast.LENGTH_SHORT).show();
//                            }
//                        } else {
//                            btnTake.setEnabled(false);
//                            String text = "Wrong QR Code, Please try again";
//                            Spannable centeredText = new SpannableString(text);
//                            centeredText.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
//                                    0, text.length() - 1,
//                                    Spannable.SPAN_INCLUSIVE_INCLUSIVE);
//                            Toast.makeText(RestOutActivity.this, centeredText, Toast.LENGTH_SHORT).show();
//                        }
//                    } catch (SQLException e) {
//                        e.getMessage();
//                    }
                } else {
                    noWo.setText("");
                    txtName.setText("");
                    txtQtyNeed.setText("");
                    txtQtyTaken.setText("");
                }
            }
        });

//        btnScan.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                qrScan.setRequestCode(1);
//                cekQR = 1;
//                qrScan.initiateScan();
//            }
//        });
//
//        btnFind.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String nowo = noWo.getText().toString();
//                try {
//                    ConnectionHelper con = new ConnectionHelper();
//                    Connection connect = ConnectionHelper.CONN();
//
//                    String sp = "EXEC sp_GetWORestOut '" + nowo + "'";
//                    PreparedStatement ps = connect.prepareStatement(sp);
//
//                    Log.w("query", sp);
//                    ResultSet rs = ps.executeQuery();
//                    if (rs.next()) {
//                        id = rs.getString("wo_id");
//                        rack_id = rs.getString("rack_detail_id");
//                        type = rs.getString("category_name");
//                        rack_wo = rs.getString("rack_position");
//                        qty = rs.getString("wo_battery_amount");
//                        connect.close();
//                        rs.close();
//                        ps.close();
//
//                        if (qty.equals("0")) {
//                            String text = "Battery amount already empty, Please try again";
//                            Spannable centeredText = new SpannableString(text);
//                            centeredText.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
//                                    0, text.length() - 1,
//                                    Spannable.SPAN_INCLUSIVE_INCLUSIVE);
//                            Toast.makeText(RestOutActivity.this, centeredText, Toast.LENGTH_LONG).show();
//                        } else if (nowo.equals(id) && !nowo.equals("")) {
//                            tvType.setText(type);
//                            tvRack.setText(rack_wo);
//                            tvQty.setText(qty);
//                            btnTake.setEnabled(true);
//                        } else {
//                            btnTake.setEnabled(false);
//                            String text = "Wrong QR Code, Please try again";
//                            Spannable centeredText = new SpannableString(text);
//                            centeredText.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
//                                    0, text.length() - 1,
//                                    Spannable.SPAN_INCLUSIVE_INCLUSIVE);
//                            Toast.makeText(RestOutActivity.this, centeredText, Toast.LENGTH_LONG).show();
//                        }
//                    } else {
//                        btnTake.setEnabled(false);
//                        String text = "Wrong QR Code, Please try again";
//                        Spannable centeredText = new SpannableString(text);
//                        centeredText.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
//                                0, text.length() - 1,
//                                Spannable.SPAN_INCLUSIVE_INCLUSIVE);
//                        Toast.makeText(RestOutActivity.this, centeredText, Toast.LENGTH_LONG).show();
//                    }
//                } catch (SQLException e) {
//                    e.getMessage();
//                }
//            }
//        });

        btnTake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = ConfirmRestOutActivity.newIntent(RestOutActivity.this, txtName.getText().toString(), noWo.getText().toString(), txtQtyTaken.getText().toString());
                startActivity(intent);
                finish();
//                cekQR = 2;
//                qrScan.initiateScan();
            }
        });
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
//        if (result != null) {
//            if (result.getContents() == null) {
//                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
//            } else {
//                if (cekQR == 1) {
//                    try {
//                        JSONObject obj = new JSONObject(result.getContents());
//                        wo = obj.getString("wo_id");
//                        wo = result.getContents();
//                        try {
//                            ConnectionHelper con = new ConnectionHelper();
//                            Connection connect = ConnectionHelper.CONN();
//
//                            String sp = "EXEC sp_GetWORestOut '" + wo + "'";
//                            PreparedStatement ps = connect.prepareStatement(sp);
//
//                            Log.w("query", sp);
//                            ResultSet rs = ps.executeQuery();
//                            if (rs.next()) {
//                                id = rs.getString("wo_id");
//                                rack_id = rs.getString("rack_detail_id");
//                                type = rs.getString("category_name");
//                                rack_wo = rs.getString("rack_position");
//                                qty = rs.getString("wo_battery_amount");
//                                connect.close();
//                                rs.close();
//                                ps.close();
//
//                                if (qty == null){
//                                    btnTake.setEnabled(false);
//                                    String text = "Wrong QR Code, Please try again";
//                                    Spannable centeredText = new SpannableString(text);
//                                    centeredText.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
//                                            0, text.length() - 1,
//                                            Spannable.SPAN_INCLUSIVE_INCLUSIVE);
//                                    Toast.makeText(this, centeredText, Toast.LENGTH_LONG).show();
//                                }else if (qty.equals("0")) {
//                                    String text = "Battery amount already empty, Please try again";
//                                    Spannable centeredText = new SpannableString(text);
//                                    centeredText.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
//                                            0, text.length() - 1,
//                                            Spannable.SPAN_INCLUSIVE_INCLUSIVE);
//                                    Toast.makeText(this, centeredText, Toast.LENGTH_LONG).show();
//                                } else if (wo.equals(id) && !wo.equals("")) {
//                                    noWo.setText(id);
//                                    tvType.setText(type);
//                                    tvRack.setText(rack_wo);
//                                    tvQty.setText(qty);
//                                    btnTake.setEnabled(true);
//                                } else {
//                                    btnTake.setEnabled(false);
//                                    String text = "Wrong QR Code, Please try again";
//                                    Spannable centeredText = new SpannableString(text);
//                                    centeredText.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
//                                            0, text.length() - 1,
//                                            Spannable.SPAN_INCLUSIVE_INCLUSIVE);
//                                    Toast.makeText(this, centeredText, Toast.LENGTH_LONG).show();
//                                }
//                            } else {
//                                btnTake.setEnabled(false);
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
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                        Toast.makeText(this, "Wrong QR Code, Please try again", Toast.LENGTH_LONG).show();
//                    }
//                } else if (cekQR == 2) {
//                    try {
//                        JSONObject obj = new JSONObject(result.getContents());
//                        rack_qr = obj.getString("rack_id");
//                        rack_qr = result.getContents();
//                        if (rack_id.equals(rack_qr)) {
//                            Intent intent = ConfirmRestOutActivity.newIntent(RestOutActivity.this, id, type, qty, rack_wo, rack_id);
//                            startActivity(intent);
//                        } else {
//                            Toast.makeText(this, "Invalid rack position, try again", Toast.LENGTH_LONG).show();
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                        Toast.makeText(this, "Wrong QR Code, Please try again", Toast.LENGTH_LONG).show();
//                    }
//                }
//            }
//        } else {
//            super.onActivityResult(requestCode, resultCode, data);
//        }
//    }
}
