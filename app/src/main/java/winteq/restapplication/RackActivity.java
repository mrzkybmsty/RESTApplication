package winteq.restapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import Helper.ConnectionHelper;

public class RackActivity extends AppCompatActivity {

    private static final String EXTRA_WO = "winteq.restapplication.wo_id";

    private static String no_wo, category_name, wo_battery_amount, rack_id_qr, position;
    private static TextView txtNoWo, txtType, txtQty, txtRack;

    public static Intent newIntent(Context packageContext, String Wo) {
        Intent i = new Intent(packageContext, RackActivity.class);
        i.putExtra(EXTRA_WO, Wo);
        no_wo = Wo;
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rack);

        getSupportActionBar().setTitle("Rack Position");

        txtNoWo = findViewById(R.id.txtWONumber);
        txtType = findViewById(R.id.txtBatteryType);
        txtQty = findViewById(R.id.txtQuantity);
        txtRack = findViewById(R.id.txtRack);

        txtNoWo.setText(no_wo);
        CheckWO wo = new CheckWO();
        wo.execute("");
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
                txtRack.setText(position);
            } else {
                Toast.makeText(RackActivity.this, "Wrong WO Number, Please try again", Toast.LENGTH_LONG).show();
            }
        }
    }
}
