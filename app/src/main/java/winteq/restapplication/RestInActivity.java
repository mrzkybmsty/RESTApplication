package winteq.restapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

public class RestInActivity extends AppCompatActivity {

    private static final String EXTRA_WO = "winteq.restapplication.no_wo";

    private static String no_wo, rack_id, row, column;

    private Button btnVerify;
    private TextView txtNoWo, txtType, txtQty, txtRack;


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
        txtNoWo = findViewById(R.id.txtWONumber);
        txtType = findViewById(R.id.txtBatteryType);
        txtQty = findViewById(R.id.txtQuantity);
        txtRack = findViewById(R.id.txtRack);

        txtNoWo.setText(no_wo);

        qrScan = new IntentIntegrator(this);
        qrScan.setCaptureActivity(AnyOrientationCaptureActivity.class);
        qrScan.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
        qrScan.setPrompt("Scan a barcode");
        qrScan.setOrientationLocked(false);
        qrScan.setBeepEnabled(true);

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                try {
                    JSONObject obj = new JSONObject(result.getContents());
                    rack_id = obj.getString("rack_id");
                    row = obj.getString("row");
                    column = obj.getString("column");
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
