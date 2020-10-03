package app.web.ishismarteditor.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import app.web.ishismarteditor.Splash;
import app.web.ishismarteditor.databinding.ActivityMyProfileBinding;

public class MyProfile extends AppCompatActivity {

    private static String TAG = "My Profile Activity";
    private ActivityMyProfileBinding binding;;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.backBtn.setOnClickListener(v -> onBackPressed());

        /*to settings*/
        binding.settingsBtn.setOnClickListener(v -> {
            startActivity(new Intent(MyProfile.this, EditProfile.class));
        });

        /*signing out user*/
        binding.signOutBtn.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MyProfile.this, Splash.class).addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY));
        });
    }
}