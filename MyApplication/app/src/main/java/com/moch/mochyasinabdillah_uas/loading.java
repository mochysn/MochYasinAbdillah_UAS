package com.moch.mochyasinabdillah_uas;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.widget.ProgressBar;
import android.os.Bundle;

public class loading extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        ProgressBar bar_tot = (ProgressBar) findViewById(R.id.bar_tot);

        ProgressBarAnimation mProgressAnimationTot = new ProgressBarAnimation(bar_tot, 3000);
        mProgressAnimationTot.setProgress(100);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                finish();
            }
        }, 4000);


    }


}