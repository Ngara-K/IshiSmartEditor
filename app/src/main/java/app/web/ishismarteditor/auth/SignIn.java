package app.web.ishismarteditor.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;

import app.web.ishismarteditor.databinding.ActivitySignInBinding;
import app.web.ishismarteditor.ui.MyProfile;

import static app.web.ishismarteditor.utils.firebaseUtils.firebaseAuth;
import static app.web.ishismarteditor.utils.firebaseUtils.firebaseUser;

public class SignIn extends AppCompatActivity {

    private static String TAG = "Sign Activity";
    private ActivitySignInBinding binding;
    private BasePopupView popupView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        /*initializing popupView*/
        popupView = new XPopup.Builder(SignIn.this)
                .autoDismiss(false)
                .dismissOnBackPressed(false)
                .dismissOnTouchOutside(false)
                .asLoading();

        binding.forgotPasswordTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignIn.this, ResetPassword.class));
            }
        });

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveTaskToBack(true);
            }
        });

        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (binding.emailAddressInput.getText().toString().isEmpty()) {
                    binding.emailAddressInput.setError("Email is empty");
                }
                else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(binding.emailAddressInput.getText().toString()).matches()) {
                    binding.emailAddressInput.setError("Wrong email format");
                }

                if (binding.passwordInput.getText().toString().isEmpty()) {
                    binding.passwordInput.setError("Password id empty");
                }
                else {
                    loginUser();
                }
            }
        });
    }

    /*signing user*/
    private void loginUser() {
        popupView.show();

        firebaseAuth.signInWithEmailAndPassword(binding.emailAddressInput.getText().toString(),
                binding.passwordInput.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull final Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    firebaseUser = firebaseAuth.getCurrentUser();
                    if (firebaseUser != null) {
                        startActivity(new Intent(SignIn.this, MyProfile.class));
                        finish();
                    }
                    else {

                        new XPopup.Builder(SignIn.this)
                                .asConfirm("Oops..!", "An error occurred", null,
                                        "Okay", null, null,
                                        true, 0).show();
                    }
                }

                else {

                    popupView.dismissWith(new Runnable() {
                        @Override
                        public void run() {
                            showError(task.getException());
                        }
                    });

                }
            }
        });
    }

    private void showError(Exception exception) {
        /*move task back*/
        new XPopup.Builder(SignIn.this)
                .asConfirm("Oops..!", exception.getMessage(), null,
                        "Okay", null, null,
                        true, 0).show();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}