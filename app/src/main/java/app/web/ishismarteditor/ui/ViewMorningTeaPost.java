package app.web.ishismarteditor.ui;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnCancelListener;
import com.lxj.xpopup.interfaces.OnConfirmListener;
import com.tapadoo.alerter.Alerter;

import org.commonmark.node.Emphasis;
import org.commonmark.node.StrongEmphasis;

import app.web.ishismarteditor.R;
import app.web.ishismarteditor.databinding.ActivityViewMorningTeaPostBinding;
import app.web.ishismarteditor.models.MorningTea;
import app.web.ishismarteditor.popups.EditMorningTeaPopUp;
import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.Markwon;
import io.noties.markwon.MarkwonSpansFactory;
import io.noties.markwon.linkify.LinkifyPlugin;

import static app.web.ishismarteditor.utils.AppUtils.firebaseUser;
import static app.web.ishismarteditor.utils.AppUtils.morningTeaReference;

public class ViewMorningTeaPost extends AppCompatActivity {

    private static String TAG = "View Morning Post Activity";
    private static long post_id;
    private static String document_id;
    private ActivityViewMorningTeaPostBinding binding;
    private Markwon markwon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewMorningTeaPostBinding.inflate(getLayoutInflater());
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
        markwon = Markwon.builder(ViewMorningTeaPost.this).usePlugin(new AbstractMarkwonPlugin() {
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
        binding.editBtn.setOnClickListener(v ->
                new XPopup.Builder(ViewMorningTeaPost.this)
                        .asCustom(new EditMorningTeaPopUp(ViewMorningTeaPost.this,
                                document_id, binding.messageTitle.getText().toString(),
                                binding.messageSummary.getText().toString(),
                                binding.message.getText().toString())).show());

        /*swipe to refresh post details*/
        binding.refreshPostLayout.setOnRefreshListener(this::getPostDetails);
    }

    /*xpopup dialog*/
    private void showConfirmationDialog() {
        new XPopup.Builder(ViewMorningTeaPost.this)
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

    /*getting posts details*/
    private void getPostDetails() {
        morningTeaReference.whereEqualTo("id", post_id).get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {
                        /*dismiss freshing*/
                        binding.refreshPostLayout.setRefreshing(false);

                        for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                            /*getting document id*/
                            document_id = doc.getId();
                            /*document to pojo*/
                            MorningTea morningTea = doc.toObject(MorningTea.class);
                            /*details setter*/
                            setPostDetails(morningTea);
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
    private void setPostDetails(MorningTea morningTea) {
        binding.userDisplayName.setText(firebaseUser.getDisplayName());
        binding.userEmail.setText(firebaseUser.getEmail());
        binding.messageTitle.setText(morningTea.getMessage_title());
        markwon.setMarkdown(binding.messageSummary, morningTea.getMessage_summary());
        markwon.setMarkdown(binding.message, morningTea.getMessage_body());

        binding.timeAgo.setDate(morningTea.getPost_date().getTimestamp().toDate());
    }

    /*deleting post*/
    private void deletePost() {
        showLoading(getResources().getString(R.string.deleting_post));
        morningTeaReference.document(document_id).delete().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                showAlert(getString(R.string.post_deleted_successfully));

                /*move task back*/
                dismissActivity();
            } else {
                /*on failure*/
                showAlert(getString(R.string.something_went_wrong));
            }
        });
    }

    private void dismissActivity() {
        new Handler().postDelayed(this::onBackPressed, 2500);
    }

    /*loading alerter*/
    private void showLoading(String message) {
        Alerter.create(ViewMorningTeaPost.this)
                .setTitle(getResources().getString(R.string.please_wait))
                .setText(message)
                .disableOutsideTouch()
                .setDismissable(false)
                .enableProgress(true)
                .show();
    }

    /*alerter*/
    private void showAlert(String message) {
        Alerter.create(ViewMorningTeaPost.this)
                .setTitle(getResources().getString(R.string.app_name))
                .hideIcon()
                .setText(message)
                .show();
    }
}