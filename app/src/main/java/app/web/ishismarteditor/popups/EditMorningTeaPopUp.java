package app.web.ishismarteditor.popups;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.text.style.StyleSpan;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.lxj.xpopup.impl.FullScreenPopupView;
import com.tapadoo.alerter.Alerter;

import org.commonmark.node.Emphasis;
import org.commonmark.node.StrongEmphasis;

import java.util.concurrent.Executors;

import app.web.ishismarteditor.R;
import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.Markwon;
import io.noties.markwon.MarkwonSpansFactory;
import io.noties.markwon.editor.MarkwonEditor;
import io.noties.markwon.editor.MarkwonEditorTextWatcher;
import io.noties.markwon.linkify.LinkifyPlugin;

import static app.web.ishismarteditor.R.string.please_wait;
import static app.web.ishismarteditor.R.string.required;
import static app.web.ishismarteditor.R.string.required_10;
import static app.web.ishismarteditor.R.string.required_100;
import static app.web.ishismarteditor.R.string.required_25;
import static app.web.ishismarteditor.utils.AppUtils.morningTeaReference;

@SuppressLint("ViewConstructor")
public class EditMorningTeaPopUp extends FullScreenPopupView {

    private static String TAG = "Morning Tea Popup";
    private static boolean valid;
    private static String doc_id, message_title, message_summary, message_body;
    private TextInputEditText title_input, summary_input, message_input;
    private TextInputLayout title_inLayout, summary_inLayout, message_inLayout;
    private MaterialButton back_btn, submit_btn;
    /*markwon library*/
    private Markwon markwon;
    private MarkwonEditor markwonEditor;

    public EditMorningTeaPopUp(@NonNull Context context, String document_id,
                               String title, String summary, String message) {
        super(context);
        doc_id = document_id;
        message_title = title;
        message_summary = summary;
        message_body = message;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.popup_add_morning_tea;
    }

    @Override
    protected void onCreate() {
        super.onCreate();

        back_btn = findViewById(R.id.back_btn);
        submit_btn = findViewById(R.id.submit_btn);

        title_input = findViewById(R.id.title_input);
        summary_input = findViewById(R.id.summary_input);
        message_input = findViewById(R.id.message_input);

        title_inLayout = findViewById(R.id.title_inLayout);
        summary_inLayout = findViewById(R.id.summary_inLayout);
        message_inLayout = findViewById(R.id.message_inLayout);

        /*setters*/
        title_input.setText(message_title);
        summary_input.setText(message_summary);
        message_input.setText(message_body);

        /*initialing markwon*/
        markwon = Markwon.builder(getContext()).usePlugin(new AbstractMarkwonPlugin() {
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
        summary_input.addTextChangedListener(MarkwonEditorTextWatcher.withPreRender(markwonEditor,
                Executors.newCachedThreadPool(), summary_input));

        message_input.addTextChangedListener(MarkwonEditorTextWatcher.withPreRender(markwonEditor,
                Executors.newCachedThreadPool(), message_input));

        back_btn.setOnClickListener(v -> smartDismiss());

        submit_btn.setOnClickListener(v -> {
            if (!validation()) {
                /*if validation fails*/
                showMessage("Check for errors");
            } else {
                /*show loader...*/
                showLoading("Editing Post ...");

                /*sending data*/
                editMorningTea();
            }
        });
    }

    /*validating inputs*/
    private boolean validation() {
        Log.d(TAG, "validation: ");

        if (title_input.getText().toString().isEmpty()) {
            title_inLayout.setError(getContext().getString(required));
            valid = false;
        } else if (title_input.getText().toString().length() < 10) {
            title_inLayout.setError(getContext().getString(required_10));
            valid = false;
        } else if (summary_input.getText().toString().isEmpty()) {
            summary_inLayout.setError(getContext().getString(required));
            valid = false;
        } else if (summary_input.getText().toString().length() < 25) {
            summary_inLayout.setError(getContext().getString(required_25));
            valid = false;
        } else if (message_input.getText().toString().isEmpty()) {
            message_inLayout.setError(getContext().getString(required));
            valid = false;
        } else if (message_input.getText().toString().length() < 100) {
            message_inLayout.setError(getContext().getString(required_100));
            valid = false;
        } else {
            valid = true;
        }

        return valid;
    }

    private void editMorningTea() {
        Log.d(TAG, "editMorningTea: ");

        morningTeaReference.document(doc_id).update("message_title", title_input.getText().toString(),
                "message_summary", summary_input.getText().toString(),
                "message_body", message_input.getText().toString())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        /*show message when successful */
                        dismissWith(() -> showMessage("Edited Successful ..."));
                    } else {
                        /*show message when failed*/
                        showMessage(task.getException().getMessage());
                    }
                }).addOnFailureListener(e ->
                Log.d(TAG, "onFailure() returned: " + e.getMessage()));
    }

    private void showMessage(String message) {
        Alerter.create((Activity) getContext())
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
        Alerter.create((Activity) getContext())
                .setTitle(getContext().getString(please_wait))
                .setText(message)
                .disableOutsideTouch()
                .setDismissable(false)
                .enableProgress(true)
                .show();
    }
}
