package winteq.restapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

public class RestInActivity extends AppCompatActivity {

    private static final String EXTRA_WO = "winteq.restapplication.no_wo";
    private static final String EXTRA_TYPE = "winteq.restapplication.type";
    private static final String EXTRA_QTY = "winteq.restapplication.qty";

    private static String no_wo, type, qty;

    private Button btnVerify;
    private TextView txtNoWo, txtType, txtQty;


    private IntentIntegrator qrScan;

    public static Intent newIntent(Context packageContext, String Wo, String Type, String Qty) {
        Intent i = new Intent(packageContext, RestInActivity.class);
        i.putExtra(EXTRA_WO, Wo);
        i.putExtra(EXTRA_TYPE, Type);
        i.putExtra(EXTRA_QTY, Qty);
        no_wo = Wo;
        type = Type;
        qty = Qty;
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rest_in);

        getSupportActionBar().setTitle("Rest In");

        btnVerify = findViewById(R.id.btnVerify);
        txtNoWo = findViewById(R.id.txtNoWo);
        txtType = findViewById(R.id.txtType);
        txtQty = findViewById(R.id.txtQty);

        txtNoWo.setText(no_wo);
        txtType.setText(type);
        txtQty.setText(qty);

        qrScan = new IntentIntegrator(this);

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
