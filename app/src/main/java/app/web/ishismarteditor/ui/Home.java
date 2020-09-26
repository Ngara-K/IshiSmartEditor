package app.web.ishismarteditor.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.lxj.xpopup.XPopup;

import app.web.ishismarteditor.databinding.ActivityHomeBinding;
import app.web.ishismarteditor.popups.PostTypePopUp;

import static app.web.ishismarteditor.utils.firebaseUtils.firebaseUser;

public class Home extends AppCompatActivity {

    private static String TAG = "Home Activity";
    private ActivityHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        /*view adding bottom sheet*/
        binding.addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*showing popup*/
                new XPopup.Builder(Home.this).asCustom(
                        new PostTypePopUp(Home.this)
                ).show();
            }
        });
    }
}