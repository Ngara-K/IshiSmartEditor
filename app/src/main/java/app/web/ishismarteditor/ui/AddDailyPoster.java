package app.web.ishismarteditor.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.lxj.xpopup.util.PermissionConstants;
import com.lxj.xpopup.util.XPermission;

import java.io.File;
import java.util.List;
import java.util.UUID;

import app.web.ishismarteditor.R;
import app.web.ishismarteditor.databinding.ActivityAddDailyPosterBinding;
import app.web.ishismarteditor.utils.glide.GlideEngine;

import static app.web.ishismarteditor.utils.AppUtils.firebaseUser;

public class AddDailyPoster extends AppCompatActivity {

    private static String TAG = "Add Daily Poster Activity";
    private String compressed_image_path = null;
    private ActivityAddDailyPosterBinding binding;
    private StorageReference postersStorageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddDailyPosterBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        /*on Backpressed*/
        binding.backBtn.setOnClickListener(v -> onBackPressed());

        /*pick image*/
        binding.pickImage.setOnClickListener(v -> getPermission());

        /*if Image exists*/
        if (compressed_image_path != null) {
            binding.pickImage.setVisibility(View.GONE);
            binding.noImageTv.setVisibility(View.GONE);
        }

        else {
            binding.pickImage.setVisibility(View.VISIBLE);
            binding.noImageTv.setVisibility(View.VISIBLE);
        }

        binding.removeImageChip.setOnClickListener(v -> {
            compressed_image_path = null;
            binding.posterImage.setVisibility(View.GONE);
            binding.removeImageChip.setVisibility(View.GONE);

            /*show pick btn*/
            binding.pickImage.setVisibility(View.VISIBLE);
            binding.noImageTv.setVisibility(View.VISIBLE);
        });
    }

    /*storage permission*/
    private void getPermission() {
        XPermission.create(AddDailyPoster.this, PermissionConstants.STORAGE)
                .callback(new XPermission.SimpleCallback() {
                    @Override
                    public void onGranted() {
                        Log.d(TAG, "onGranted: ");
                        /*open Image Picker*/
                        openImagePicker();
                    }

                    @Override
                    public void onDenied() {
                        Log.d(TAG, "onDenied: ");
                        /*request again*/
                        getPermission();
                    }
                }).request();
    }

    /*Image Chooser*/
    private void openImagePicker() {
        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofImage())
                .imageEngine(GlideEngine.createGlideEngine())
                .selectionMode(PictureConfig.SINGLE)
                .isCamera(false)
                .isCompress(true)
                .isEnableCrop(false) /*disable crop for sometime*/
                .freeStyleCropEnabled(false)
                .setCropDimmedColor(R.color.gray)
                .showCropFrame(true)
                .isPreviewImage(true)
                .forResult(PictureConfig.CHOOSE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // onResult Callback
                    List<LocalMedia> image_path = PictureSelector.obtainMultipleResult(data);
                    /*setting imageview*/
                    compressed_image_path = image_path.get(0).getCompressPath();
                    setImage(compressed_image_path);
                    break;
                default:
                    break;
            }
        }
    }

    /*setting ImageView*/
    private void setImage(String compressPath) {
        Glide.with(AddDailyPoster.this)
                .load(compressPath).into(binding.posterImage);

        /*after setting imageView*/
        binding.posterImage.setVisibility(View.VISIBLE);
        binding.removeImageChip.setVisibility(View.VISIBLE);

        /*hide pick btn*/
        binding.pickImage.setVisibility(View.GONE);
        binding.noImageTv.setVisibility(View.GONE);
    }

    private void uploadImage(String compressPath) {
        Log.d(TAG, "uploadImage: ");

        Uri file = Uri.fromFile(new File(compressPath));

        // Create file metadata including the content type
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("image/jpg")
                .build();

        UUID uuid = UUID.nameUUIDFromBytes(compressPath.getBytes());

        /*storage reference*/
        postersStorageReference = FirebaseStorage.getInstance().getReference()
                .child("daily_posters/" + firebaseUser.getUid()).child(uuid.toString());

        UploadTask uploadTask = postersStorageReference.putFile(file, metadata);
        uploadTask.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                getDownloadUri(task);
            } else {
                Log.d(TAG, "onComplete() returned: " + task.getException().getMessage());
            }
        });
    }

    /*getting download uri*/
    private void getDownloadUri(Task<UploadTask.TaskSnapshot> task) {
        task.continueWithTask(task1 -> {
            if (!task1.isSuccessful()) {
                throw task1.getException();
            }

            return postersStorageReference.getDownloadUrl();
        }).addOnCompleteListener(task12 -> {
            if (task12.isSuccessful()) {
                Uri uri = task12.getResult();
                Log.d(TAG, "onComplete() returned: " + uri.toString());
            } else {
                /*failed*/
                Log.d(TAG, "onComplete() returned: " + task12.getException().getMessage());
            }
        });
    }
}