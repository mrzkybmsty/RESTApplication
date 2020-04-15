package winteq.restapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class RackActivity extends AppCompatActivity {

    private static final String EXTRA_WO = "winteq.restapplication.wo_id";

    private static String no_wo;

    public static Intent newIntent(Context packageContext, String Wo) {
        Intent i = new Intent(packageContext, RestInActivity.class);
        i.putExtra(EXTRA_WO, Wo);
        no_wo = Wo;
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rack);
    }
}
