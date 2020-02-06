package winteq.restapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

public class RestOutActivity extends AppCompatActivity {

    EditText noWo;
    Button btnScan, btnFind, btnTake;
    TextView tvRack, tvType, tvQty;
    String id, wo, rack_wo, rack_qr, qty, type, rack_id;
    Integer cekQR;
    private IntentIntegrator qrScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rest_out);

        getSupportActionBar().setTitle("Rest Out");

        noWo = findViewById(R.id.txtWORestOut);

        tvRack = findViewById(R.id.txtRackOut);
        tvType = findViewById(R.id.txtTypeOut);
        tvQty = findViewById(R.id.txtQtyOut);

        btnScan = findViewById(R.id.btnScan);
        btnFind = findViewById(R.id.btnFind);
        btnTake = findViewById(R.id.btnTake);

        qrScan = new IntentIntegrator(RestOutActivity.this);
        qrScan.setCaptureActivity(AnyOrientationCaptureActivity.class);
        qrScan.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        qrScan.setPrompt("Scan a barcode");
        qrScan.setOrientationLocked(false);
        qrScan.setBeepEnabled(true);

        btnTake.setEnabled(false);

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                qrScan.setRequestCode(1);
                cekQR = 1;
                qrScan.initiateScan();
            }
        });

        btnFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nowo = noWo.getText().toString();
                try {
                    ConnectionHelper con = new ConnectionHelper();
                    Connection connect = ConnectionHelper.CONN();

                    String sp = "EXEC sp_GetWORestOut '" + nowo + "'";
                    PreparedStatement ps = connect.prepareStatement(sp);

                    Log.w("query", sp);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        id = rs.getString("wo_id");
                        rack_id = rs.getString("rack_detail_id");
                        type = rs.getString("category_name");
                        rack_wo = rs.getString("rack_position");
                        qty = rs.getString("wo_battery_amount");
                        connect.close();
                        rs.close();
                        ps.close();

                        if (qty.equals("0")) {
                            String text = "Battery amount already empty, Please try again";
                            Spannable centeredText = new SpannableString(text);
                            centeredText.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
                                    0, text.length() - 1,
                                    Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                            Toast.makeText(RestOutActivity.this, centeredText, Toast.LENGTH_LONG).show();
                        } else if (nowo.equals(id) && !nowo.equals("")) {
                            tvType.setText(type);
                            tvRack.setText(rack_wo);
                            tvQty.setText(qty);
                            btnTake.setEnabled(true);
                        } else {
                            btnTake.setEnabled(false);
                            String text = "Wrong QR Code, Please try again";
                            Spannable centeredText = new SpannableString(text);
                            centeredText.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
                                    0, text.length() - 1,
                                    Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                            Toast.makeText(RestOutActivity.this, centeredText, Toast.LENGTH_LONG).show();
                        }
                    } else {
                        btnTake.setEnabled(false);
                        String text = "Wrong QR Code, Please try again";
                        Spannable centeredText = new SpannableString(text);
                        centeredText.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
                                0, text.length() - 1,
                                Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                        Toast.makeText(RestOutActivity.this, centeredText, Toast.LENGTH_LONG).show();
                    }
                } catch (SQLException e) {
                    e.getMessage();
                }
            }
        });

        btnTake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cekQR = 2;
                qrScan.initiateScan();
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
                if (cekQR == 1) {
                    try {
                        JSONObject obj = new JSONObject(result.getContents());
                        wo = obj.getString("wo_id");

                        try {
                            ConnectionHelper con = new ConnectionHelper();
                            Connection connect = ConnectionHelper.CONN();

                            String sp = "EXEC sp_GetWORestOut '" + wo + "'";
                            PreparedStatement ps = connect.prepareStatement(sp);

                            Log.w("query", sp);
                            ResultSet rs = ps.executeQuery();
                            if (rs.next()) {
                                id = rs.getString("wo_id");
                                rack_id = rs.getString("rack_detail_id");
                                type = rs.getString("category_name");
                                rack_wo = rs.getString("rack_position");
                                qty = rs.getString("wo_battery_amount");
                                connect.close();
                                rs.close();
                                ps.close();

                                if (qty.equals("0")) {
                                    String text = "Battery amount already empty, Please try again";
                                    Spannable centeredText = new SpannableString(text);
                                    centeredText.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
                                            0, text.length() - 1,
                                            Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                                    Toast.makeText(this, centeredText, Toast.LENGTH_LONG).show();
                                } else if (wo.equals(id) && !wo.equals("")) {
                                    noWo.setText(id);
                                    tvType.setText(type);
                                    tvRack.setText(rack_wo);
                                    tvQty.setText(qty);
                                    btnTake.setEnabled(true);
                                } else {
                                    btnTake.setEnabled(false);
                                    String text = "Wrong QR Code, Please try again";
                                    Spannable centeredText = new SpannableString(text);
                                    centeredText.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
                                            0, text.length() - 1,
                                            Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                                    Toast.makeText(this, centeredText, Toast.LENGTH_LONG).show();
                                }
                            } else {
                                btnTake.setEnabled(false);
                                String text = "Wrong QR Code, Please try again";
                                Spannable centeredText = new SpannableString(text);
                                centeredText.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
                                        0, text.length() - 1,
                                        Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                                Toast.makeText(this, centeredText, Toast.LENGTH_LONG).show();
                            }
                        } catch (SQLException e) {
                            e.getMessage();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Wrong QR Code, Please try again", Toast.LENGTH_LONG).show();
                    }
                } else if (cekQR == 2) {
                    try {
                        JSONObject obj = new JSONObject(result.getContents());
                        rack_qr = obj.getString("rack_id");

                        if (rack_id.equals(rack_qr)) {
                            Intent intent = ConfirmRestOutActivity.newIntent(RestOutActivity.this, id, type, qty, rack_wo, rack_id);
                            startActivity(intent);
                        } else {
                            Toast.makeText(this, "Invalid rack position, try again", Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Wrong QR Code, Please try again", Toast.LENGTH_LONG).show();
                    }
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
