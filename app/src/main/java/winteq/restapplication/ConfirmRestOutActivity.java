package winteq.restapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import Helper.ConnectionHelper;
import Helper.MinMaxFilter;
import Helper.Preferences;

public class ConfirmRestOutActivity extends AppCompatActivity {

    private static final String EXTRA_WO = "winteq.restapplication.wo_id";
    private static final String EXTRA_BT = "winteq.restapplication.category_name";
    private static final String EXTRA_QTY = "winteq.restapplication.wo_battery_amount";
    private static final String EXTRA_RP = "winteq.restapplication.rack_position";
    private static final String EXTRA_RID = "winteq.restapplication.rack_detail_id";

    public static String no_wo, type, quantity, rack_pos, rack_id, total, val;
    TextView tvWO, tvType, tvQty, tvRack;
    EditText txtMinus;
    Button btnConfirm;

    public static Intent newIntent(Context packageContext, String Wo, String bt, String qty, String rp, String rid) {
        Intent i = new Intent(packageContext, ConfirmRestOutActivity.class);
        i.putExtra(EXTRA_WO, Wo);
        i.putExtra(EXTRA_BT, bt);
        i.putExtra(EXTRA_QTY, qty);
        i.putExtra(EXTRA_RP, rp);
        i.putExtra(EXTRA_RID, rid);
        no_wo = Wo;
        type = bt;
        quantity = qty;
        rack_pos = rp;
        rack_id = rid;
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_rest_out);

        tvWO = findViewById(R.id.txtWoConfirmRO);
        tvType = findViewById(R.id.txtBatteryTypeConfirmRO);
        tvQty = findViewById(R.id.txtQtyConfirmRO);
        tvRack = findViewById(R.id.txtRackConfirmRO);
        txtMinus = findViewById(R.id.txtMinus);
        btnConfirm = findViewById(R.id.btnConfirmRestOut);

        tvWO.setText(no_wo);
        tvType.setText(type);
        tvQty.setText(quantity);
        tvRack.setText(rack_pos);

        val = tvQty.getText().toString();
        Integer finalVal = Integer.parseInt(val);

        txtMinus.setFilters(new InputFilter[]{new MinMaxFilter(1, finalVal)});

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(ConfirmRestOutActivity.this)
                        .setTitle("Confirmation")
                        .setMessage("Are you sure?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                total = txtMinus.getText().toString();
                                UpdateQty update = new UpdateQty();
                                update.execute("");
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

    private class UpdateQty extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            try {
                ConnectionHelper con = new ConnectionHelper();
                Connection connect = ConnectionHelper.CONN();

                String sp = "EXEC sp_UpdateQty '" + no_wo + "'" + "," + ConfirmRestOutActivity.total;
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
//            Toast.makeText(ConfirmRestInActivity.this, "Success", Toast.LENGTH_LONG).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(ConfirmRestOutActivity.this)
                    .setTitle("Notification");
            LayoutInflater inflater = getLayoutInflater();
            View dialogLayout = inflater.inflate(R.layout.alert_dialog_with_imageview, null);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(ConfirmRestOutActivity.this, LoginActivity.class);
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
