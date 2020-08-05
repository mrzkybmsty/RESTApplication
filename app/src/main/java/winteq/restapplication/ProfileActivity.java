package winteq.restapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import Helper.ConnectionHelper;
import Helper.MyValueFormatter;
import Helper.Preferences;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private static TextView txtName, txtRole, txtNik;
    private static String nik, name, role;
    List<BarEntry> countTotal = new ArrayList<>();
    ArrayList<String> dateTotal = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        getSupportActionBar().setTitle("Profile");

        txtNik = findViewById(R.id.txtNik);
        txtName = findViewById(R.id.txtName);
        txtRole = findViewById(R.id.txtRole);

        getProfile profile = new getProfile();
        profile.execute("");

        createChart chart = new createChart();
        chart.execute("");
    }

    private class createChart extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            try {
                ConnectionHelper con = new ConnectionHelper();
                Connection connect = ConnectionHelper.CONN();

                String sp = "EXEC rst_getListRestIn '" + Preferences.getUserId(getBaseContext()) + "'";
                PreparedStatement ps = connect.prepareStatement(sp);
                ResultSet rs = ps.executeQuery();
                ResultSetMetaData rsmd = rs.getMetaData();
                int columnsNumber = rsmd.getColumnCount();
                int i = 0;
                while (rs.next()) {
                    countTotal.add(new BarEntry(Float.parseFloat((rs.getString("total"))), i));
                    dateTotal.add(rs.getString("tanggal"));
                    i++;
                }
                return "Success";
            } catch (SQLException e) {
                return "Error : " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("Success")) {
                BarChart chart = findViewById(R.id.bar_chart);

                MyValueFormatter f = new MyValueFormatter();

                BarDataSet bardataset = new BarDataSet(countTotal, "Total Rest In");
                bardataset.setColor(Color.GREEN);
                chart.animateY(3000);
                BarData data = new BarData(dateTotal, bardataset);
                data.setValueFormatter(f);
                chart.setDescription("");
                chart.setData(data);
                chart.getAxisRight().setEnabled(false);
                chart.setVisibleXRangeMinimum(3);
                chart.setVisibleXRangeMaximum(7);
                chart.getAxisLeft().setLabelCount(8, false);
                chart.getAxisLeft().setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
                chart.getAxisLeft().setValueFormatter(new YAxisValueFormatter() {
                    @Override
                    public String getFormattedValue(float value, YAxis yAxis) {
                        return ((int)value+" Times");
                    }
                });
                XAxis xAxis = chart.getXAxis();
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setDrawGridLines(false);
            } else {
                Toast.makeText(ProfileActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private class getProfile extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            try {
                ConnectionHelper con = new ConnectionHelper();
                Connection connect = ConnectionHelper.CONN();

                String sp = "EXEC rst_getDetailUser '" + Preferences.getUserId(getBaseContext()) + "'";
                PreparedStatement ps = connect.prepareStatement(sp);

                Log.w("query", sp);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    nik = rs.getString("usr_nik");
                    name = rs.getString("usr_name");
                    role = rs.getString("usr_role");
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
                txtNik.setText(nik);
                txtName.setText(name);
                txtRole.setText(role);
            } else {
                Toast.makeText(ProfileActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        }
    }
}