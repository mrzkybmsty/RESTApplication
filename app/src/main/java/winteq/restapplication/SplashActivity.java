package winteq.restapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashActivity extends AppCompatActivity {
    private ImageView mOpeningLogo;
    private TextView txtCopyright;
    Animation fromBot, fromTop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mOpeningLogo = findViewById(R.id.opening_logo);
        txtCopyright = findViewById(R.id.txtCopyright);

        fromBot = AnimationUtils.loadAnimation(this, R.anim.frombottom);
        fromTop = AnimationUtils.loadAnimation(this, R.anim.fromtop);

        mOpeningLogo.setAnimation(fromTop);
        txtCopyright.setAnimation(fromBot);

        final Intent i = new Intent(this, MainActivity.class);
        Thread timer = new Thread() {
            public void run() {
                try {
                    sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    startActivity(i);
                    finish();
                }
            }
        };
        timer.start();
    }
}
