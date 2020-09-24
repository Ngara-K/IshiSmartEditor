package app.web.ishismarteditor.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        binding.sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (binding.emailAddressInput.getText().toString().isEmpty()) {
                    binding.emailAddressInput.setError("Email is Empty");
                }
                else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(binding.emailAddressInput.getText().toString()).matches()) {
                    binding.emailAddressInput.setError("Wrong email format");
                }
                else {
                    sendResetInst();
                }
            }
        });
    }

    private void sendResetInst() {
        popupView.show();

        firebaseAuth.sendPasswordResetEmail(binding.emailAddressInput.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull final Task<Void> task) {
                        if (task.isSuccessful()) {
                            popupView.dismissWith(new Runnable() {
                                @Override
                                public void run() {

                                    new XPopup.Builder(ResetPassword.this)
                                            .asConfirm("Successful..!", getString(R.string.reset_instruction_sent),
                                                    null, "Okay", null, null,
                                                    true, 0).show();
                                }
                            });
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
        new XPopup.Builder(ResetPassword.this)
                .asConfirm("Oops..!", exception.getMessage(), null,
                        "Okay", null, null,
                        true, 0).show();
    }
}