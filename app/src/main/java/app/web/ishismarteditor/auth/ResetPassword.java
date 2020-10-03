package app.web.ishismarteditor.auth;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;

import app.web.ishismarteditor.R;
import app.web.ishismarteditor.databinding.ActivityResetPasswordBinding;

public class ResetPassword extends AppCompatActivity {

    private ActivityResetPasswordBinding binding;
    private FirebaseAuth firebaseAuth;
    private BasePopupView popupView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityResetPasswordBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        /*initializing firebase instance*/
        firebaseAuth = FirebaseAuth.getInstance();

        /*initializing popupView*/
        popupView = new XPopup.Builder(ResetPassword.this)
                .autoDismiss(false)
                .dismissOnBackPressed(false)
                .dismissOnTouchOutside(false)
                .asLoading();

        binding.backBtn.setOnClickListener(v -> onBackPressed());

        binding.sendBtn.setOnClickListener(v -> {

            if (binding.emailAddressInput.getText().toString().isEmpty()) {
                binding.emailAddressInput.setError("Email is Empty");
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(binding.emailAddressInput.getText().toString()).matches()) {
                binding.emailAddressInput.setError("Wrong email format");
            } else {
                sendResetInst();
            }
        });
    }

    private void sendResetInst() {
        popupView.show();

        firebaseAuth.sendPasswordResetEmail(binding.emailAddressInput.getText().toString())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        popupView.dismissWith(() ->
                                new XPopup.Builder(ResetPassword.this)
                                        .asConfirm("Successful..!", getString(R.string.reset_instruction_sent),
                                                null, "Okay", null, null,
                                                true, 0).show());
                    } else {

                        popupView.dismissWith(() ->
                                showError(task.getException()));

                    }
                });
    }

    private void showError(Exception exception) {
        /*move task back*/
        new XPopup.Builder(ResetPassword.this)
                .asConfirm("Oops..!", exception.getMessage(), null,
                        "Okay", null, null,
                        true, 0).show();
    }
}