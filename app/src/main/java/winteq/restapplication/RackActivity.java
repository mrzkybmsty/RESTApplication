package winteq.restapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AlignmentSpan;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import Helper.ConnectionHelper;

public class RackActivity extends AppCompatActivity {

//    private static final String EXTRA_WO = "winteq.restapplication.wo_id";

    private static String no_wo, category_name, wo_battery_amount, rack_id_qr, position;
    private static TextView txtLbl, txtType, txtQty, txtRackCheck, txtRackRec;
    private static Button btnTrigger;

//    public static Intent newIntent(Context packageContext, String Wo) {
//        Intent i = new Intent(packageContext, RackActivity.class);
//        i.putExtra(EXTRA_WO, Wo);
//        no_wo = Wo;
//        return i;
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rack);

        getSupportActionBar().setTitle("Rack Position");

        txtLbl = findViewById(R.id.txtLabel);
        txtType = findViewById(R.id.txtBatteryType);
        txtQty = findViewById(R.id.txtQuantity);
        txtRackCheck = findViewById(R.id.txtRackCheck);
        txtRackRec = findViewById(R.id.txtRack);
        btnTrigger = findViewById(R.id.buttonTrigger);
        btnTrigger.setEnabled(false);

        txtRackCheck.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    try {
                        ConnectionHelper con = new ConnectionHelper();
                        Connection connect = ConnectionHelper.CONN();

                        String sp = "EXEC rst_getDetailLabelUnload '" + txtRackCheck.getText().toString() + "'";
                        PreparedStatement ps = connect.prepareStatement(sp);
                        ResultSet rs = ps.executeQuery();
                        if (rs.next()) {
                            txtLbl.setText(txtRackCheck.getText().toString());
                            txtType.setText(rs.getString("bat_name"));
                            txtQty.setText(rs.getString("lab_quantity"));
                            rs.close();
                            ps.close();

                            String sp_get = "EXEC rst_getDetailRackRec '" + txtRackCheck.getText().toString() + "'";
                            PreparedStatement ps_get = connect.prepareStatement(sp_get);
                            ResultSet rs_get = ps_get.executeQuery();
                            rs_get.next();
                            txtRackRec.setText(rs_get.getString("rde_label"));
                            rs_get.close();
                            ps_get.close();

                        } else {
                            String text = "Wrong Label, Please try again";
                            Spannable centeredText = new SpannableString(text);
                            centeredText.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
                                    0, text.length() - 1,
                                    Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                            Toast.makeText(RackActivity.this, centeredText, Toast.LENGTH_SHORT).show();
                        }
                    } catch (SQLException e) {
                        e.getMessage();
                    }
                } else {
                    txtRackCheck.setText("");
                    txtLbl.setText("");
                    txtType.setText("");
                    txtQty.setText("");
                    txtRackRec.setText("");
                }
            }
        });

//        CheckWO wo = new CheckWO();
//        wo.execute("");
    }

//    private class CheckWO extends AsyncTask<String, Void, String> {
//
//        @Override
//        protected String doInBackground(String... strings) {
//            try {
//                ConnectionHelper con = new ConnectionHelper();
//                Connection connect = ConnectionHelper.CONN();
//
//                String sp = "EXEC sp_GetWO_Only '" + no_wo + "'";
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
//                txtRack.setText(position);
//            } else {
//                Toast.makeText(RackActivity.this, "Wrong WO Number, Please try again", Toast.LENGTH_LONG).show();
//            }
//        }
//    }
}
