package winteq.restapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import Helper.ConnectionHelper;
import Helper.Preferences;

public class ConfirmActivity extends AppCompatActivity {

    private static final String EXTRA_WO = "winteq.restapplication.wo_id";
    private static final String EXTRA_BT = "winteq.restapplication.battery_type";
    private static final String EXTRA_QTY = "winteq.restapplication.battery_quantity";
    private static final String EXTRA_RP = "winteq.restapplication.rack_position";
    private static final String EXTRA_RID = "winteq.restapplication.rack_id";

    private static String no_wo, batt_type, quantity, rack_pos, rack_id;

    private TextView txtConfirmWO, txtConfirmBattery, txtConfirmQuantity, txtConfirmPosition;
    private Button btnConfirm;

    public static Intent newIntent(Context packageContext, String Wo, String bt, String qty, String rp, String rid) {
        Intent i = new Intent(packageContext, ConfirmActivity.class);
        i.putExtra(EXTRA_WO, Wo);
        i.putExtra(EXTRA_BT, bt);
        i.putExtra(EXTRA_QTY, qty);
        i.putExtra(EXTRA_RP, rp);
        i.putExtra(EXTRA_RID, rid);
        no_wo = Wo;
        batt_type = bt;
        quantity = qty;
        rack_pos = rp;
        rack_id = rid;
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);

        txtConfirmWO = findViewById(R.id.txtConfirmWO);
        txtConfirmBattery = findViewById(R.id.txtConfirmBattery);
        txtConfirmQuantity = findViewById(R.id.txtConfirmQty);
        txtConfirmPosition = findViewById(R.id.txtConfirmPosition);
        btnConfirm = findViewById(R.id.btnConfirmRestIn);

        txtConfirmWO.setText(no_wo);
        txtConfirmBattery.setText(batt_type);
        txtConfirmQuantity.setText(quantity);
        txtConfirmPosition.setText(rack_pos);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateRack update = new UpdateRack();
                update.execute("");
            }
        });
    }

    private class UpdateRack extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            try {
                ConnectionHelper con = new ConnectionHelper();
                Connection connect = ConnectionHelper.CONN();

                String sp = "EXEC sp_UpdateRack '" + no_wo + "'" + "," + rack_id + "," + Preferences.getUserId(getBaseContext());
                PreparedStatement ps = connect.prepareStatement(sp);

                Log.w("query", sp);
                ResultSet rs = ps.executeQuery();
                connect.close();
                rs.close();
                ps.close();
                return "Success";
            } catch (SQLException e) {
                return "Error : " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
//            Toast.makeText(ConfirmActivity.this, "Success", Toast.LENGTH_LONG).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(ConfirmActivity.this);
            LayoutInflater inflater = getLayoutInflater();
            View dialogLayout = inflater.inflate(R.layout.alert_dialog_with_imageview, null);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(ConfirmActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
            builder.setCancelable(false);
            builder.setView(dialogLayout);
            builder.show();
        }
    }
}
