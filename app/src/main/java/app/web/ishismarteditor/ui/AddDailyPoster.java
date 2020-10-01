package app.web.ishismarteditor.ui;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
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
import com.tapadoo.alerter.Alerter;

import org.commonmark.node.Emphasis;
import org.commonmark.node.StrongEmphasis;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;

import app.web.ishismarteditor.R;
import app.web.ishismarteditor.databinding.ActivityAddDailyPosterBinding;
import app.web.ishismarteditor.models.DailyPoster;
import app.web.ishismarteditor.models.DailyPoster.PostDate;
import app.web.ishismarteditor.utils.glide.GlideEngine;
import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.Markwon;
import io.noties.markwon.MarkwonSpansFactory;
import io.noties.markwon.editor.MarkwonEditor;
import io.noties.markwon.editor.MarkwonEditorTextWatcher;
import io.noties.markwon.linkify.LinkifyPlugin;

import static app.web.ishismarteditor.utils.AppUtils.dailyPosterReference;
import static app.web.ishismarteditor.utils.AppUtils.editorsCollection;
import static app.web.ishismarteditor.utils.AppUtils.firebaseUser;

public class AddDailyPoster extends AppCompatActivity {

    private static String TAG = "Add Daily Poster Activity";
    private String compressed_image_path = null;
    private ActivityAddDailyPosterBinding binding;
    private StorageReference postersStorageReference;
    private String poster_image_url = null;
    private DailyPoster dailyPoster;
    private PostDate postDate;

    /*markwon library*/
    private Markwon markwon;
    private MarkwonEditor markwonEditor;

    private Calendar calendar;
    private SimpleDateFormat date, month, year;

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
            binding.submitBtn.setEnabled(true);
        }

        else {
            binding.pickImage.setVisibility(View.VISIBLE);
            binding.noImageTv.setVisibility(View.VISIBLE);
            binding.submitBtn.setEnabled(false);
        }

        /*remove picked image*/
        binding.removeImageChip.setOnClickListener(v -> {
            compressed_image_path = null;
            binding.posterImage.setVisibility(View.GONE);
            binding.removeImageChip.setVisibility(View.GONE);
            binding.submitBtn.setEnabled(false);

            /*show pick btn*/
            binding.pickImage.setVisibility(View.VISIBLE);
            binding.noImageTv.setVisibility(View.VISIBLE);
        });

        /*send daily poster*/
        binding.submitBtn.setOnClickListener(v -> {
            showLoading(getResources().getString(R.string.uploading_poster));
            /*upload image first and get download url*/
            uploadImage(compressed_image_path);
        });


        calendar = Calendar.getInstance();

        date = new SimpleDateFormat("dd");
        month = new SimpleDateFormat("MMM");
        year = new SimpleDateFormat("yyyy");

        /*initialing markwon*/
        markwon = Markwon.builder(AddDailyPoster.this).usePlugin(new AbstractMarkwonPlugin() {
            @Override
            public void configureSpansFactory(@NonNull MarkwonSpansFactory.Builder builder) {
                builder.setFactory(Emphasis.class,
                        (configuration, props) ->
                                new StyleSpan(Typeface.BOLD)).setFactory(StrongEmphasis.class,
                        (configuration, props) ->
                                new StyleSpan(Typeface.BOLD));
            }
        }).usePlugin(LinkifyPlugin.create()).build();

        markwonEditor = MarkwonEditor.create(markwon);

        /*markwon editor watcher on text change*/
        binding.summaryInput.addTextChangedListener(MarkwonEditorTextWatcher.withPreRender(markwonEditor,
                Executors.newCachedThreadPool(), binding.summaryInput));
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
        binding.submitBtn.setEnabled(true);

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
                poster_image_url = uri.toString();

                /*setting pojo class for upload*/
                String string_date = date.format(calendar.getTime());
                String string_month = month.format(calendar.getTime());
                String string_year = year.format(calendar.getTime());

                postDate = new PostDate(string_date, string_month, string_year,
                        new Timestamp(new Date()));

                dailyPoster = new DailyPoster(
                        System.nanoTime(),
                        binding.summaryInput.getText().toString(),
                        poster_image_url, firebaseUser.getUid(),
                        editorsCollection.document(firebaseUser.getUid()),
                        System.currentTimeMillis(), postDate );

                sendDailyPoster(dailyPoster);
            }
            else {
                /*failed*/
                Log.d(TAG, "onComplete() returned: " + task12.getException().getMessage());
            }
        });
    }

    /*sending daily poster*/
    private void sendDailyPoster(DailyPoster dailyPoster) {
        Log.d(TAG, "sendDailyPoster: ");

        dailyPosterReference.document().set(dailyPoster).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                /*show message when successful */
                showMessage(getString(R.string.poster_sent));

                /*move task back*/
                dismissActivity();

            } else {
                /*show message when failed*/
                showMessage(task.getException().getMessage());
            }
        });
    }

    private void dismissActivity() {
        new Handler().postDelayed(this::onBackPressed, 2500);
    }

    private void showMessage(String message) {
        Alerter.create(AddDailyPoster.this)
                .setTitle(message)
                .setDismissable(true)
                .enableClickAnimation(true)
                .hideIcon()
                .enableSwipeToDismiss()
                .setDuration(3500)
                .setSound()
                .show();
    }

    private void showLoading(String message) {
        Alerter.create(AddDailyPoster.this)
                .setTitle(getResources().getString(R.string.please_wait))
                .setText(message)
                .disableOutsideTouch()
                .setDismissable(false)
                .enableProgress(true)
                .show();
    }
}