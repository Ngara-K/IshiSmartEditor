package app.web.ishismarteditor.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;

import app.web.ishismarteditor.R;
import app.web.ishismarteditor.databinding.ActivityEditProfileBinding;
import app.web.ishismarteditor.models.EditorProfile;

import static app.web.ishismarteditor.utils.AppUtils.editorsCollection;
import static app.web.ishismarteditor.utils.AppUtils.firebaseUser;

public class EditProfile extends AppCompatActivity {

    private ActivityEditProfileBinding binding;

    private static String TAG = "Edit Profile Activity";
    private static boolean valid;
    private EditorProfile editorProfile;
    private BasePopupView popupView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        /*initializing popupView*/
        popupView = new XPopup.Builder(EditProfile.this)
                .autoDismiss(false)
                .dismissOnBackPressed(false)
                .dismissOnTouchOutside(false)
                .asLoading();

        /*getting profile*/
        getEditorProfile();

        binding.editUpdateBtn.setOnClickListener(v -> {
            if (!validation()) {
                /*if validation fails*/
                showPopUp(getResources().getString(R.string.check_for_errors));
            } else {
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
        });

        binding.backBtn.setOnClickListener(v -> onBackPressed());
    }

    /*getting profile*/
    private void getEditorProfile() {
        showLoading();

        editorsCollection.document(firebaseUser.getUid()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                popupView.smartDismiss();

                if (task.getResult().exists()) {
                    /*results ot pojo*/
                    editorProfile = task.getResult().toObject(EditorProfile.class);

                    /*setters*/
                    binding.firstNameInput.setText(editorProfile.getFirst_name());
                    binding.lastNameInput.setText(editorProfile.getLast_name());
                    binding.emailAddressInput.setText(editorProfile.getEmail());
                    binding.phoneNumberInput.setText(Long.toString(editorProfile.getPhone_number()));
                    binding.locationInput.setText(editorProfile.getLocation());
                    binding.organizationInput.setText(editorProfile.getOrganization());
                    binding.shortDescriptionInput.setText(editorProfile.getShort_description());
                }
            } else {
                Log.d(TAG, "onComplete: " + task.getException());
                /*On Error*/
                popupView.dismissWith(() ->
                        showPopUp(task.getException().getMessage()));
            }
        });
    }

    /*validating required inputs*/
    private boolean validation() {
        if (binding.firstNameInput.getText().toString().isEmpty()) {
            binding.firstNameInput.setError("Required");
            valid = false;
        } else if (binding.firstNameInput.getText().toString().length() < 3) {
            binding.firstNameInput.setError("Invalid name");
            valid = false;
        } else if (binding.lastNameInput.getText().toString().isEmpty()) {
            binding.lastNameInput.setError("Required");
            valid = false;
        } else if (binding.lastNameInput.getText().toString().length() < 3) {
            binding.lastNameInput.setError("Invalid name");
            valid = false;
        } else if (binding.emailAddressInput.getText().toString().isEmpty()) {
            binding.emailAddressInput.setError("Required");
            valid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(binding.emailAddressInput.getText().toString()).matches()) {
            binding.emailAddressInput.setError("Invalid format");
            valid = false;
        } else if (binding.phoneNumberInput.getText().toString().isEmpty()) {
            binding.phoneNumberInput.setError("Required");
            valid = false;
        } else if (binding.locationInput.getText().toString().isEmpty()) {
            binding.locationInput.setError("Required");
            valid = false;
        } else if (binding.locationInput.getText().toString().length() < 8) {
            binding.locationInput.setError("Required lenght 8");
            valid = false;
        } else {
            valid = true;
        }
        return valid;
    }

    /*checking id document exits*/
    private void checkIfExist() {
        editorsCollection.document(firebaseUser.getUid()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot snapshot = task.getResult();

                /*updating firebase profile first*/
                updateFirebaseProfile(snapshot);
            } else {
                Log.d(TAG, "onComplete: " + task.getException());
                /*On Error*/
                popupView.dismissWith(() ->
                        showPopUp(task.getException().getMessage()));
            }
        });
    }

    /*show error*/
    private void showPopUp(String string_notify) {
        new XPopup.Builder(EditProfile.this)
                .asConfirm(getResources().getString(R.string.app_name), string_notify, null,
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

        editorsCollection.document(firebaseUser.getUid()).set(editorProfile).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                popupView.dismissWith(() ->
                        showPopUp(getString(R.string.profile_updated)));
            } else {
                /*show error message*/
                popupView.dismissWith(() ->
                        showPopUp(task.getException().getMessage()));
            }
        });
    }

    /*merge Profile*/
    private void mergeProfile() {
        Log.d(TAG, "mergeProfile: ");

        editorsCollection.document(firebaseUser.getUid()).set(editorProfile, SetOptions.merge()).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                popupView.dismissWith(() ->
                        showPopUp(getString(R.string.profile_updated)));
            } else {
                /*show error message*/
                popupView.dismissWith(() ->
                        showPopUp(task.getException().getMessage()));
            }
        });
    }

    /*updating firebase user display name*/
    private void updateFirebaseProfile(final DocumentSnapshot snapshot) {

        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                .setDisplayName(
                        binding.firstNameInput.getText().toString()
                                + " " + binding.lastNameInput.getText().toString()
                ).build();

        firebaseUser.updateProfile(profileChangeRequest).addOnSuccessListener(aVoid -> {
            /*
             * Checking if document exists
             * Merge document
             * or
             * Create document
             * */

            if (snapshot.exists()) {
                mergeProfile();
            } else {
                setProfile();
            }

        }).addOnFailureListener(e -> {
            Log.d(TAG, "onFailure() returned: " + e.getMessage());
            popupView.smartDismiss();
        });
    }
}