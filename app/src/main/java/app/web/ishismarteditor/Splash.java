package app.web.ishismarteditor;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.os.HandlerCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import app.web.ishismarteditor.auth.SignIn;
import app.web.ishismarteditor.databinding.ActivitySplashBinding;
import app.web.ishismarteditor.ui.Home;

public class Splash extends AppCompatActivity {

    ActivitySplashBinding binding;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        /*getting current firebase user*/
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Glide.with(Splash.this).asGif().load(R.drawable.phone_glasses).into(binding.logo);

        HandlerCompat.createAsync(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {

                if (firebaseUser != null) {
                    startActivity(new Intent(Splash.this, Home.class));
                }
                else {
                    startActivity(new Intent(Splash.this, SignIn.class));
                }

                finish();
            }
        }, 3000);
    }
}