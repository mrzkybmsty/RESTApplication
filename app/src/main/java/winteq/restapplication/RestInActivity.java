package winteq.restapplication;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

    private static final String EXTRA_WO = "winteq.restapplication.wo_id";

    private static String no_wo, rack_id, category_name, wo_battery_amount, rack_id_qr, position;

    private Button btnVerify, btnScanRack;
    private static TextView txtNoWo, txtType, txtQty, txtRack, txtRackRec;

    Integer cekQR;

    private IntentIntegrator qrScan;

    public static Intent newIntent(Context packageContext, String Wo) {
        Intent i = new Intent(packageContext, RestInActivity.class);
        i.putExtra(EXTRA_WO, Wo);
        no_wo = Wo;
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rest_in);

        getSupportActionBar().setTitle("Rest In");

        btnVerify = findViewById(R.id.btnVerify);
        btnScanRack = findViewById(R.id.btnScanRack);
        txtNoWo = findViewById(R.id.txtWONumber);
        txtType = findViewById(R.id.txtBatteryType);
        txtQty = findViewById(R.id.txtQuantity);
        txtRack = findViewById(R.id.txtRack);
        txtRackRec = findViewById(R.id.txtRackRec);

        txtNoWo.setText(no_wo);

        qrScan = new IntentIntegrator(this);
        qrScan.setCaptureActivity(AnyOrientationCaptureActivity.class);
        qrScan.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        qrScan.setPrompt("Scan a barcode");
        qrScan.setOrientationLocked(false);
        qrScan.setBeepEnabled(true);

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cekQR = 1;
                qrScan.initiateScan();
            }
        });

        btnScanRack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cekQR = 2;
                qrScan.initiateScan();
            }
        });
        CheckWO wo = new CheckWO();
        wo.execute("");
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
                    rack_id = obj.getString("rack_id");

                    if (rack_id.equals(rack_id_qr)) {
                        Intent intent = ConfirmRestInActivity.newIntent(RestInActivity.this, no_wo, category_name, wo_battery_amount, position, rack_id);
                        startActivity(intent);
                    } else {
                        Toast.makeText(RestInActivity.this, "Invalid rack position, Please try again", Toast.LENGTH_LONG).show();
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

    private class CheckWO extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            try {
                ConnectionHelper con = new ConnectionHelper();
                Connection connect = ConnectionHelper.CONN();

                String sp = "EXEC sp_GetWO '" + no_wo + "'";
                PreparedStatement ps = connect.prepareStatement(sp);

                Log.w("query", sp);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    category_name = rs.getString("category_name");
                    wo_battery_amount = rs.getString("wo_battery_amount");
                    rack_id_qr = rs.getString("rack_id");
                    position = rs.getString("rack_position");
                    connect.close();
                    rs.close();
                    ps.close();
                    return "Success";
                } else
                    return "Failed";
            } catch (SQLException e) {
                return "Error : " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("Success")) {
                txtType.setText(category_name);
                txtQty.setText(wo_battery_amount);
                txtRackRec.setText(position);
            } else {
                Toast.makeText(RestInActivity.this, "Wrong WO Number, Please try again", Toast.LENGTH_LONG).show();
                btnVerify.setEnabled(false);
            }
        }
    }
}
