package winteq.restapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import Helper.ConnectionHelper;

public class ReportActivity extends AppCompatActivity {

    private static String no_wo, unload, restin, restout, picin, picout;
    EditText txtWO;
    TextView tvWO, tvUnload, tvRestIn, tvRestOut, tvPICIn, tvPICOut;
    Button btnFind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        getSupportActionBar().setTitle("Report");

        txtWO = findViewById(R.id.txtWOReport);

        tvWO = findViewById(R.id.tvWO);
        tvUnload = findViewById(R.id.tvDateUnload);
        tvRestIn = findViewById(R.id.tvDateRestIn);
        tvRestOut = findViewById(R.id.tvDateRestOut);
        tvPICIn = findViewById(R.id.tvPicIn);
        tvPICOut = findViewById(R.id.tvPicOut);

        btnFind = findViewById(R.id.btnFindReport);

        btnFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                no_wo = txtWO.getText().toString();
                FindWO wo = new FindWO();
                wo.execute("");
            }
        });
    }

    private class FindWO extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            try {
                ConnectionHelper con = new ConnectionHelper();
                Connection connect = ConnectionHelper.CONN();

                String sp = "EXEC sp_FindReport '" + no_wo + "'";
                PreparedStatement ps = connect.prepareStatement(sp);

                Log.w("query", sp);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    unload = rs.getString("wo_unload");
                    restin = rs.getString("wo_rest_in");
                    restout = rs.getString("wo_rest_out");
                    picin = rs.getString("pic_rest_in");
                    picout = rs.getString("pic_rest_out");
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
                Locale inLocale = new Locale("id", "ID");
                SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                SimpleDateFormat output = new SimpleDateFormat("dd MMMM yyyy HH:mm:ss", inLocale);

                tvWO.setText(no_wo);
                try {
                    Date date_unload, date_restin, date_restout;

                    date_unload = input.parse(unload);
                    tvUnload.setText(output.format(date_unload));

                    date_restin = input.parse(restin);
                    tvRestIn.setText(output.format(date_restin));

                    date_restout = input.parse(restout);
                    tvRestOut.setText(output.format(date_restout));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                tvPICIn.setText(picin);
                tvPICOut.setText(picout);
            } else {
                Toast.makeText(ReportActivity.this, "Wrong WO Number, Please try again", Toast.LENGTH_LONG).show();
            }
        }
    }
}
