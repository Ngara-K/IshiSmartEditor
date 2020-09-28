package app.web.ishismarteditor.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;

import app.web.ishismarteditor.R;
import app.web.ishismarteditor.databinding.ActivityMyProfileBinding;
import app.web.ishismarteditor.models.EditorProfile;

import static app.web.ishismarteditor.utils.AppUtils.editorsCollection;
import static app.web.ishismarteditor.utils.AppUtils.firebaseUser;

public class MyProfile extends AppCompatActivity {

    private static String TAG = "Sign Activity";
    private ActivityMyProfileBinding binding;
    private EditorProfile editorProfile;
    private static boolean valid;
    private BasePopupView popupView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        /*initializing popupView*/
        popupView = new XPopup.Builder(MyProfile.this)
                .autoDismiss(false)
                .dismissOnBackPressed(false)
                .dismissOnTouchOutside(false)
                .asLoading();

        binding.editUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validation()) {
                    /*if validation fails*/
                    showPopUp("Check for errors");
                }
                else {
                    /*show loader...*/
                    showLoading();

                    /*setting pojo class*/
                    editorProfile = new EditorProfile(
                            firebaseUser.getUid(),
                            binding.firstNameInput.getText().toString(),
                            binding.lastNameInput.getText().toString(),
                            Long.parseLong(binding.phoneNumberInput.getText().toString()),
                            binding.emailAddressInput.getText().toString(),
                            binding.locationInput.getText().toString(),
                            binding.organizationInput.getText().toString(),
                            binding.shortDescriptionInput.getText().toString()
                    );

                    /*check if doc exists*/
                    checkIfExist();
                }
            }
        });
    }

    private void getEditorProfile() {
        showLoading();

    }

    /*validating required inputs*/
    private boolean validation() {
        if (binding.firstNameInput.getText().toString().isEmpty()) {
            binding.firstNameInput.setError("Required");
            valid = false;
        }
        else if (binding.firstNameInput.getText().toString().length() < 3) {
            binding.firstNameInput.setError("Invalid name");
            valid = false;
        }
        else if (binding.lastNameInput.getText().toString().isEmpty()) {
            binding.lastNameInput.setError("Required");
            valid = false;
        }
        else if (binding.lastNameInput.getText().toString().length() < 3) {
            binding.lastNameInput.setError("Invalid name");
            valid = false;
        }
        
        else if (binding.emailAddressInput.getText().toString().isEmpty()) {
            binding.emailAddressInput.setError("Required");
            valid = false;
        }
        else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(binding.emailAddressInput.getText().toString()).matches()) {
            binding.emailAddressInput.setError("Invalid format");
            valid = false;
        }
        else if (binding.phoneNumberInput.getText().toString().isEmpty()) {
            binding.phoneNumberInput.setError("Required");
            valid = false;
        }
        else if (binding.locationInput.getText().toString().isEmpty()) {
            binding.locationInput.setError("Required");
            valid = false;
        }
        else if (binding.locationInput.getText().toString().length() < 8) {
            binding.locationInput.setError("Required lenght 8");
            valid = false;
        }
        else {
            valid = true;
        }
        return valid;
    }

    /*checking id document exits*/
    private void checkIfExist() {
        editorsCollection.document(firebaseUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull final Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot snapshot = task.getResult();

                    /*
                    * Checking if document exists
                    * Merge document
                    * or
                    * Create document
                    * */
                    
                    if (snapshot.exists()) {
                        mergeProfile();
                    }
                    else {
                        setProfile();
                    }

                }
                else {
                    Log.d(TAG, "onComplete: " + task.getException());
                    /*On Error*/
                    popupView.dismissWith(new Runnable() {
                        @Override
                        public void run() {
                            showPopUp(task.getException().getMessage());
                        }
                    });
                }
            }
        });
    }

    /*show error*/
    private void showPopUp(String string_notify) {
        new XPopup.Builder(MyProfile.this)
                .asConfirm("IshiSmart.!", string_notify, null,
                        "Okay", null, null,
                        true, 0).show();
    }

    /*show loading*/
    private void showLoading() {
        popupView.show();
    }

    /*set new Profile*/
    private void setProfile() {
        Log.d(TAG, "setProfile: ");

        editorsCollection.document(firebaseUser.getUid()).set(editorProfile).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull final Task<Void> task) {
                if (task.isSuccessful()) {
                    popupView.dismissWith(new Runnable() {
                        @Override
                        public void run() {
                            showPopUp(getString(R.string.profile_updated));
                        }
                    });
                }
                else {
                    /*show error message*/
                    popupView.dismissWith(new Runnable() {
                        @Override
                        public void run() {
                            showPopUp(task.getException().getMessage());
                        }
                    });
                }
            }
        });
    }

    /*merge Profile*/
    private void mergeProfile() {
        Log.d(TAG, "mergeProfile: ");

        editorsCollection.document(firebaseUser.getUid()).set(editorProfile, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull final Task<Void> task) {
                if (task.isSuccessful()) {
                    popupView.dismissWith(new Runnable() {
                        @Override
                        public void run() {
                            showPopUp(getString(R.string.profile_updated));
                        }
                    });
                }
                else {
                    /*show error message*/
                    popupView.dismissWith(new Runnable() {
                        @Override
                        public void run() {
                            showPopUp(task.getException().getMessage());
                        }
                    });
                }
            }
        });
    }
}