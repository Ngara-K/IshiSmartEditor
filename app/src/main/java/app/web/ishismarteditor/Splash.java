package app.web.ishismarteditor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.os.HandlerCompat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;

import com.bumptech.glide.Glide;

import app.web.ishismarteditor.auth.SignIn;
import app.web.ishismarteditor.databinding.ActivitySplashBinding;

public class Splash extends AppCompatActivity {

    ActivitySplashBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Glide.with(Splash.this).asGif().load(R.drawable.phone_glasses).into(binding.logo);

        HandlerCompat.createAsync(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(Splash.this, SignIn.class));
            }
        }, 3000);
    }
}