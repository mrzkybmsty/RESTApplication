package winteq.restapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AlignmentSpan;
import android.util.Log;
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

import Helper.ConnectionHelper;
import Helper.MinMaxFilter;
import Helper.Preferences;

public class ConfirmRestOutActivity extends AppCompatActivity {

    private static final String EXTRA_BATT = "winteq.restapplication.bat_name";
    private static final String EXTRA_WO = "winteq.restapplication.wor_id";
    private static final String EXTRA_QTY = "winteq.restapplication.wor_quantity_counting";

    public static String batt_name, wo_id, qty_count, total, early_qty, total_lbl, temp_qty;
    TextView txtLabel, txtName, txtRack;
    EditText txtQty, txtLabelScan;
    Button btnConfirm;

    public static Intent newIntent(Context packageContext, String name, String wo, String qty) {
        Intent i = new Intent(packageContext, ConfirmRestOutActivity.class);
        i.putExtra(EXTRA_BATT, name);
        i.putExtra(EXTRA_WO, wo);
        i.putExtra(EXTRA_QTY, qty);
        batt_name = name;
        wo_id = wo;
        qty_count = qty;
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_rest_out);

        txtLabel = findViewById(R.id.txtLabelRestOut);
        txtName = findViewById(R.id.txtBatteryNameConfirmRestOut);
        txtRack = findViewById(R.id.txtRackPositionRestOut);
        txtQty = findViewById(R.id.txtQtyConfirmRestOut);
        txtLabelScan = findViewById(R.id.txtBarcodeLabelRestOut);
        btnConfirm = findViewById(R.id.btnConfirmRestOut);
        btnConfirm.setEnabled(false);
        txtQty.setEnabled(false);
        txtLabelScan.requestFocus();

        txtLabelScan.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (!txtLabelScan.getText().toString().equals(txtLabel.getText().toString())) {
                        String text = "Wrong Label, Please try again";
                        Spannable centeredText = new SpannableString(text);
                        centeredText.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
                                0, text.length() - 1,
                                Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                        Toast.makeText(ConfirmRestOutActivity.this, centeredText, Toast.LENGTH_SHORT).show();
                    } else if (txtLabelScan.getText().toString().equals(txtLabel.getText().toString())) {
                        btnConfirm.setEnabled(true);
                        txtQty.setEnabled(true);
                    }
                } else {
                    txtLabelScan.setText("");
                }
            }
        });

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
                                Integer qty_lbl, qty_wo, val, val_lbl, qty_lbl_upt, ear_qty;
                                qty_lbl = Integer.parseInt(txtQty.getText().toString());
                                qty_wo = Integer.parseInt(qty_count);
                                val = qty_lbl + qty_wo;

                                temp_qty = txtQty.getText().toString();
                                qty_lbl_upt = Integer.parseInt(temp_qty);
                                ear_qty = Integer.parseInt(early_qty);
                                val_lbl =  ear_qty - qty_lbl_upt;

                                total_lbl = String.valueOf(val_lbl);
                                total = String.valueOf(val);

                                try {
                                    ConnectionHelper con = new ConnectionHelper();
                                    Connection connect = ConnectionHelper.CONN();

                                    String sp_update = "EXEC rst_updateQtyCountingWO '" + wo_id + "'" + "," + "'" + total + "'" + "," + "'" + txtLabel.getText().toString() + "'" + "," + "'" + total_lbl + "'" + "," + "'" + Preferences.getUserId(getBaseContext()) + "'"  ;
                                    PreparedStatement ps_update = connect.prepareStatement(sp_update);
                                    ps_update.execute();
                                    ps_update.close();

                                } catch (SQLException e) {
                                    e.getMessage();
                                }

                                AlertDialog.Builder builder = new AlertDialog.Builder(ConfirmRestOutActivity.this)
                                        .setTitle("Notification");
                                LayoutInflater inflater = getLayoutInflater();
                                View dialogLayout = inflater.inflate(R.layout.alert_dialog_with_imageview, null);
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(ConfirmRestOutActivity.this, RestOutActivity.class);
                                        startActivity(intent);
                                        finish();
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

            String sp_get = "EXEC rst_getDetailRecRestOut '" + batt_name + "'";
            PreparedStatement ps_get = connect.prepareStatement(sp_get);
            ResultSet rs_get = ps_get.executeQuery();
            if (rs_get.next()) {
                txtName.setText(rs_get.getString("bat_name"));
                txtLabel.setText(rs_get.getString("lab_id"));
                txtQty.setText(rs_get.getString("lab_quantity"));
                early_qty = rs_get.getString("lab_quantity");
                txtQty.setFilters(new InputFilter[]{new  MinMaxFilter("0", rs_get.getString("lab_quantity"))});
                txtRack.setText(rs_get.getString("rde_label"));
            }
        } catch (SQLException e) {
            e.getMessage();
        }
    }

    //    private class UpdateQty extends AsyncTask<String, Void, String> {
//
//        @Override
//        protected String doInBackground(String... strings) {
//            try {
//                ConnectionHelper con = new ConnectionHelper();
//                Connection connect = ConnectionHelper.CONN();
//
//                String sp = "EXEC sp_UpdateQty '" + no_wo + "'" + "," + ConfirmRestOutActivity.total + "," + Preferences.getUserId(getBaseContext());
//                PreparedStatement ps = connect.prepareStatement(sp);
//
//                Log.w("query", sp);
//                ResultSet rs = ps.executeQuery();
//                connect.close();
//                rs.close();
//                ps.close();
//                return "Success";
//            } catch (SQLException e) {
//                return "Error : " + e.getMessage();
//            }
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
////            Toast.makeText(ConfirmRestInActivity.this, "Success", Toast.LENGTH_LONG).show();
//            AlertDialog.Builder builder = new AlertDialog.Builder(ConfirmRestOutActivity.this)
//                    .setTitle("Notification");
//            LayoutInflater inflater = getLayoutInflater();
//            View dialogLayout = inflater.inflate(R.layout.alert_dialog_with_imageview, null);
//            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    Intent intent = new Intent(ConfirmRestOutActivity.this, LoginActivity.class);
//                    startActivity(intent);
//                    finish();
//                }
//            });
//            builder.setCancelable(false);
//            builder.setView(dialogLayout);
//            builder.show();
//        }
//    }
}
