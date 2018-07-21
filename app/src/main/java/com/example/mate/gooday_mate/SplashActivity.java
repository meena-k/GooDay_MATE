package com.example.mate.gooday_mate;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashActivity extends AppCompatActivity {

    ImageView splashImg_top, splashImg_mid, splashImg_bottom;
    TextView splashTxt;
    Animation fromtop, frombottom, fromleft, fade_in, fade_out;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initViews();
    }

    private void initViews() {

        splashImg_top = findViewById(R.id.splash_image_top);
        splashImg_mid = findViewById(R.id.splash_image_mid);
        splashImg_bottom = findViewById(R.id.splash_image_bottom);

        splashTxt = findViewById(R.id.splash_txt);
        fromtop = AnimationUtils.loadAnimation(this, R.anim.fromtop);
        fromleft = AnimationUtils.loadAnimation(this, R.anim.fromleft);
        frombottom = AnimationUtils.loadAnimation(this, R.anim.frombottom);
        fade_in = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        fade_out = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        fromtop.setStartOffset(1500);

        splashImg_top.setAnimation(fromtop);
        splashImg_mid.setAnimation(fade_in);
        splashImg_bottom.setAnimation(fromleft);

        splashTxt.setAnimation(fade_in);

        Handler handler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();    // Splash 액티비티 종료
            }
        };
        handler.sendEmptyMessageDelayed(0, 3000);  // 3000은 3초를 의미합니다.

    }

}
