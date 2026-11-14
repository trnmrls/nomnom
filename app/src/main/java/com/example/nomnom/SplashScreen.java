package com.example.nomnom;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {
        private static final int SPLASH_DURATION = 3000; // 3 seconds
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.splash_screen);
            // Use a Handler to post a delayed action for transitioning to the main activity
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Start the login activity after the splash screen duration
                    Intent intent = new Intent(SplashScreen.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, SPLASH_DURATION);
        }
}
