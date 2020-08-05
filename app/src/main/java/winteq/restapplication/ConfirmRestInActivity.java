package winteq.restapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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
import Helper.Preferences;

public class ConfirmRestInActivity extends AppCompatActivity {

    private static final String EXTRA_LAB = "winteq.restapplication.lab_id";
    private static final String EXTRA_RACK = "winteq.restapplication.rde_label";

    public static String lab_id, rack_pos;

    public static TextView txtConfirmLabel, txtConfirmBattName, txtConfirmQty, txtConfirmRack;
    public static Button btnConfirm;

    public static Intent newIntent(Context packageContext, String id, String rack) {
        Intent i = new Intent(packageContext, ConfirmRestInActivity.class);
        i.putExtra(EXTRA_LAB, id);
        i.putExtra(EXTRA_RACK, rack);
        lab_id = id;
        rack_pos = rack;
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_rest_in);

        txtConfirmLabel = findViewById(R.id.txtConfirmLabel);
        txtConfirmBattName = findViewById(R.id.txtConfirmBattery);
        txtConfirmQty = findViewById(R.id.txtConfirmQty);
        txtConfirmRack = findViewById(R.id.txtConfirmPosition);
        btnConfirm = findViewById(R.id.btnConfirmRestIn);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConfirmRestInActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            ConnectionHelper con = new ConnectionHelper();
            Connection connect = ConnectionHelper.CONN();

            String sp = "EXEC rst_getDetailLabel '" + lab_id + "'";
            PreparedStatement ps = connect.prepareStatement(sp);
            ResultSet rs = ps.executeQuery();
            rs.next();
            txtConfirmLabel.setText(lab_id);
            txtConfirmBattName.setText(rs.getString("bat_name"));
            txtConfirmQty.setText(rs.getString("lab_quantity"));
            txtConfirmRack.setText(rack_pos);
            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.getMessage();
        }
    }
}
