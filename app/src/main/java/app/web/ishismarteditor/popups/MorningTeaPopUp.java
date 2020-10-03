package app.web.ishismarteditor.popups;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.text.format.DateFormat;
import android.text.style.StyleSpan;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.functions.FirebaseFunctions;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.impl.FullScreenPopupView;
import com.tapadoo.alerter.Alerter;

import org.commonmark.node.Emphasis;
import org.commonmark.node.StrongEmphasis;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Executors;

import app.web.ishismarteditor.R;
import app.web.ishismarteditor.models.MorningTea;
import app.web.ishismarteditor.models.MorningTea.PostDate;
import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.Markwon;
import io.noties.markwon.MarkwonSpansFactory;
import io.noties.markwon.editor.MarkwonEditor;
import io.noties.markwon.editor.MarkwonEditorTextWatcher;
import io.noties.markwon.linkify.LinkifyPlugin;

import static app.web.ishismarteditor.R.string.creating_tea_message;
import static app.web.ishismarteditor.R.string.please_wait;
import static app.web.ishismarteditor.R.string.required;
import static app.web.ishismarteditor.R.string.required_10;
import static app.web.ishismarteditor.R.string.required_100;
import static app.web.ishismarteditor.R.string.required_25;
import static app.web.ishismarteditor.utils.AppUtils.editorsCollection;
import static app.web.ishismarteditor.utils.AppUtils.firebaseUser;
import static app.web.ishismarteditor.utils.AppUtils.morningTeaReference;

public class MorningTeaPopUp extends FullScreenPopupView {

    private static String TAG = "Morning Tea Popup";
    private static boolean valid;
    private TextInputEditText title_input, summary_input, message_input;
    private TextInputLayout title_inLayout, summary_inLayout, message_inLayout;
    private MaterialButton back_btn, submit_btn;
    private MorningTea morningTea;
    private PostDate postDate;

    /*markwon library*/
    private Markwon markwon;
    private MarkwonEditor markwonEditor;

    private Calendar calendar;
    private SimpleDateFormat date, month, year;

    public MorningTeaPopUp(@NonNull Context context) {
        super(context);
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

        calendar = Calendar.getInstance();

        date = new SimpleDateFormat("dd");
        month = new SimpleDateFormat("MMM");
        year = new SimpleDateFormat("yyyy");

        final String string_date = date.format(calendar.getTime());
        final String string_month = month.format(calendar.getTime());
        final String string_year = year.format(calendar.getTime());

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
                showMessage(getContext().getResources().getString(R.string.check_for_errors));
            }
            else {
                /*show loader...*/
                showLoading(getContext().getString(creating_tea_message));

                postDate = new PostDate(string_date, string_month, string_year,
                        new Timestamp(new Date()));

                /*setting pojo class*/
                morningTea = new MorningTea(
                        System.nanoTime(), title_input.getText().toString(),
                        summary_input.getText().toString(),
                        message_input.getText().toString(),
                        firebaseUser.getUid(),
                        editorsCollection.document(firebaseUser.getUid()),
                        System.currentTimeMillis(), postDate
                );

                /*getting firebase timestamp*/
                getTimeFromServer();
            }
        });
    }

    /*getting time before posting*/
    private void getTimeFromServer() {
        /*getting server time*/
        FirebaseFunctions.getInstance().getHttpsCallable("getTime")
            .call()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    long timestamp = (long) task.getResult().getData();

//                            CharSequence test =  DateFormat.format("dd/MM/yyyy HH:mm:ss", timestamp);
                    CharSequence charSequence =  DateFormat.format("HH", timestamp);

                    if (Integer.parseInt(String.valueOf(charSequence)) > 10) {
                        showDenied(getContext().getResources().getString(R.string.you_can_only_post_in_the_morning));
                    }
                    else {
                        /*sending data*/
                        sendMorningTea();
                    }

                } else {
                    Log.d(TAG, "ERROR: ");
                }
            });
    }

    private void showDenied(String string) {
        new XPopup.Builder(getContext())
                .asConfirm(getResources().getString(R.string.app_name), string,
                        null, "OKAY", () -> {
                            /*dismiss dialog*/
                        }, null, true, 0).show();
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

    private void sendMorningTea() {
        Log.d(TAG, "sendMorningTea: ");

        morningTeaReference.document().set(morningTea).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                /*show message when successful */
                dismissWith(() -> showMessage(getContext().getResources().getString(R.string.tea_sent)));
            } else {
                /*show message when failed*/
                showMessage(task.getException().getMessage());
            }
        });
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
