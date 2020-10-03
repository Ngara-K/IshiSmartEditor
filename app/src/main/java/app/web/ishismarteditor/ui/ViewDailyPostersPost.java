package app.web.ishismarteditor.ui;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnCancelListener;
import com.lxj.xpopup.interfaces.OnConfirmListener;
import com.tapadoo.alerter.Alerter;

import org.commonmark.node.Emphasis;
import org.commonmark.node.StrongEmphasis;

import app.web.ishismarteditor.R;
import app.web.ishismarteditor.databinding.ActivityViewDailyPostersPostBinding;
import app.web.ishismarteditor.models.DailyPoster;
import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.Markwon;
import io.noties.markwon.MarkwonSpansFactory;
import io.noties.markwon.linkify.LinkifyPlugin;

import static app.web.ishismarteditor.utils.AppUtils.dailyPosterReference;
import static app.web.ishismarteditor.utils.AppUtils.firebaseUser;

public class ViewDailyPostersPost extends AppCompatActivity {

    private ActivityViewDailyPostersPostBinding binding;
    private static String TAG = "View Daily Poster Activity";
    private static long post_id;
    private static String document_id;
    private DailyPoster dailyPoster;
    private Markwon markwon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewDailyPostersPostBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        /*get bundle from previous activity*/
        if (getIntent().getExtras() != null) {
            post_id = getIntent().getLongExtra("id", 0);
        } else {
            Log.d(TAG, "onCreate() returned: " + "ID NOT FOUND");
        }

        binding.backBtn.setOnClickListener(v -> onBackPressed());

        /*getting post details*/
        getPostDetails();

        /*initialing markwon*/
        markwon = Markwon.builder(ViewDailyPostersPost.this).usePlugin(new AbstractMarkwonPlugin() {
            @Override
            public void configureSpansFactory(@NonNull MarkwonSpansFactory.Builder builder) {
                builder.setFactory(Emphasis.class,
                        (configuration, props) ->
                                new StyleSpan(Typeface.BOLD)).setFactory(StrongEmphasis.class,
                        (configuration, props) ->
                                new StyleSpan(Typeface.BOLD));
            }
        }).usePlugin(LinkifyPlugin.create()).build();

        /*deleting post*/
        binding.deleteBtn.setOnClickListener(v -> showConfirmationDialog());

        /*editing post*/
        binding.editBtn.setOnClickListener(v -> showEditDialog());

        /*swipe to refresh post details*/
        binding.refreshPostLayout.setOnRefreshListener(this::getPostDetails);
    }

    /*xpop edit dialog*/
    private void showEditDialog() {
        new XPopup.Builder(ViewDailyPostersPost.this)
                .asInputConfirm(getResources().getString(R.string.poster_summary), null,
                        dailyPoster.getPoster_summary(), null, text -> {
                            editPostSummary(text);
                        }).show();
    }

    /*editing post*/
    private void editPostSummary(String text) {
        if (text.isEmpty()) {
            showAlert(getResources().getString(R.string.required));
        }
        else {
            dailyPosterReference.document(document_id).update("poster_summary", text)
                .addOnSuccessListener(aVoid -> {
                    showAlert("Edited Successfully");
                }).addOnFailureListener(e -> {
                    Log.d(TAG, "editPostSummary() returned: " + e.getMessage());
                    showAlert(getResources().getString(R.string.something_went_wrong));
                });
        }
    }

    /*xpopup dialog*/
    private void showConfirmationDialog() {
        new XPopup.Builder(ViewDailyPostersPost.this)
                .asConfirm(getResources().getString(R.string.app_name),
                        "You are about to delete this post \nContinue ?",
                        "NO", "YES",
                        new OnConfirmListener() {
                            @Override
                            public void onConfirm() {
                                deletePost();
                            }
                        },
                        new OnCancelListener() {
                            @Override
                            public void onCancel() {
                                /*dismiss dialog*/
                            }
                        }, false, 0).show();
    }

    /*deleting post*/
    private void deletePost() {
        showLoading(getResources().getString(R.string.deleting_post));

        /*deleting storage file first*/
        StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(dailyPoster.getPoster_image_url());

        reference.delete().addOnSuccessListener(aVoid ->
            dailyPosterReference.document(document_id).delete().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    showAlert(getString(R.string.post_deleted_successfully));
                    /*move task back*/
                    dismissActivity();
                } else {
                    /*on failure*/
                    showAlert(getString(R.string.something_went_wrong));
                }
        })).addOnFailureListener(e -> {
            /*on failure*/
            showAlert(getResources().getString(R.string.something_went_wrong));
        });
    }

    /*getting posts details*/
    private void getPostDetails() {
        dailyPosterReference.whereEqualTo("id", post_id).get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {
                        /*dismiss freshing*/
                        binding.refreshPostLayout.setRefreshing(false);

                        for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                            /*getting document id*/
                            document_id = doc.getId();
                            /*document to pojo*/
                            dailyPoster = doc.toObject(DailyPoster.class);
                            /*details setter*/
                            setPostDetails(dailyPoster);
                        }
                    } else {
                        binding.refreshPostLayout.setRefreshing(false);
                        /*id not found*/
                        showAlert(getResources().getString(R.string.something_went_wrong));
                    }
                }).addOnFailureListener(e -> {
            Log.d(TAG, "onFailure() returned: " + e.getMessage());
            binding.refreshPostLayout.setRefreshing(false);
        });
    }

    /*details setter*/
    private void setPostDetails(DailyPoster dailyPoster) {
        binding.userDisplayName.setText(firebaseUser.getDisplayName());
        binding.userEmail.setText(firebaseUser.getEmail());
        markwon.setMarkdown(binding.posterSummary, dailyPoster.getPoster_summary());
        binding.timeAgo.setDate(dailyPoster.getPost_date().getTimestamp().toDate());

        Glide.with(ViewDailyPostersPost.this).load(dailyPoster.getPoster_image_url()).into(binding.posterImage);
    }

    private void dismissActivity() {
        new Handler().postDelayed(this::onBackPressed, 2500);
    }

    /*loading alerter*/
    private void showLoading(String message) {
        Alerter.create(ViewDailyPostersPost.this)
                .setTitle(getResources().getString(R.string.please_wait))
                .setText(message)
                .disableOutsideTouch()
                .setDismissable(false)
                .enableProgress(true)
                .show();
    }

    /*alerter*/
    private void showAlert(String message) {
        Alerter.create(ViewDailyPostersPost.this)
                .setTitle(getResources().getString(R.string.app_name))
                .hideIcon()
                .setText(message)
                .show();
    }
}