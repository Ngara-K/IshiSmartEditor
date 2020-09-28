package app.web.ishismarteditor.popups;

import android.content.Context;
import android.graphics.Typeface;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;
import com.lxj.xpopup.impl.FullScreenPopupView;

import org.commonmark.node.Emphasis;
import org.commonmark.node.StrongEmphasis;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Executors;

import app.web.ishismarteditor.R;
import app.web.ishismarteditor.models.MorningTea;
import app.web.ishismarteditor.models.MorningTea.*;
import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.Markwon;
import io.noties.markwon.MarkwonConfiguration;
import io.noties.markwon.MarkwonSpansFactory;
import io.noties.markwon.RenderProps;
import io.noties.markwon.SpanFactory;
import io.noties.markwon.editor.MarkwonEditor;
import io.noties.markwon.editor.MarkwonEditorTextWatcher;

import static app.web.ishismarteditor.R.string.required;
import static app.web.ishismarteditor.R.string.required_10;
import static app.web.ishismarteditor.R.string.required_100;
import static app.web.ishismarteditor.R.string.required_25;
import static app.web.ishismarteditor.utils.AppUtils.editorsCollection;
import static app.web.ishismarteditor.utils.AppUtils.firebaseUser;
import static app.web.ishismarteditor.utils.AppUtils.morningTeaReference;

public class MorningTeaPopUp extends FullScreenPopupView {

    private static String TAG = "Morning Tea Popup";

    private TextInputEditText title_input, summary_input, message_input;
    private TextInputLayout title_inLayout, summary_inLayout, message_inLayout;
    private MaterialButton back_btn, submit_btn;
    private static boolean valid;
    private BasePopupView popupView;
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
                builder.setFactory(Emphasis.class, new SpanFactory() {
                    @Nullable
                    @Override
                    public Object getSpans(@NonNull MarkwonConfiguration configuration, @NonNull RenderProps props) {
                        return new StyleSpan(Typeface.BOLD);
                    }
                }).setFactory(StrongEmphasis.class, new SpanFactory() {
                    @Nullable
                    @Override
                    public Object getSpans(@NonNull MarkwonConfiguration configuration, @NonNull RenderProps props) {
                        return new StyleSpan(Typeface.BOLD);
                    }
                });
            }
        }).build();

        markwonEditor = MarkwonEditor.create(markwon);

        /*markwon editor watcher on text change*/
        title_input.addTextChangedListener(MarkwonEditorTextWatcher.withPreRender(markwonEditor,
                Executors.newCachedThreadPool(), title_input));

        summary_input.addTextChangedListener(MarkwonEditorTextWatcher.withPreRender(markwonEditor,
                Executors.newCachedThreadPool(), summary_input));

        message_input.addTextChangedListener(MarkwonEditorTextWatcher.withPreRender(markwonEditor,
                Executors.newCachedThreadPool(), message_input));

        /*initializing popupView*/
        popupView = new XPopup.Builder(getContext())
                .autoDismiss(false)
                .dismissOnBackPressed(false)
                .dismissOnTouchOutside(false)
                .asLoading();

        back_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                smartDismiss();
            }
        });

        submit_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validation()) {
                    /*if validation fails*/
                    showPopUp("Check for errors");
                }
                else {
                    /*show loader...*/
                    showLoading();

                    postDate = new PostDate(string_date, string_month, string_year,
                            new Timestamp(new Date()));

                    /*setting pojo class*/
                    morningTea = new MorningTea(
                            title_input.getText().toString(),
                            summary_input.getText().toString(),
                            message_input.getText().toString(),
                            firebaseUser.getUid(),
                            editorsCollection.document(firebaseUser.getUid()),
                            System.currentTimeMillis(), postDate
                    );

                    /*sending data*/
                    sendMorningTea();
                }
            }
        });
    }

    private void showLoading() {
        popupView.show();
    }

    /*validating inputs*/
    private boolean validation() {
        Log.d(TAG, "validation: ");

        if (title_input.getText().toString().isEmpty()) {
            title_inLayout.setError(getContext().getString(required));
            valid = false;
        }
        
        else if (title_input.getText().toString().length() < 10) {
            title_inLayout.setError(getContext().getString(required_10));
            valid = false;
        }
        
        else if (summary_input.getText().toString().isEmpty()) {
            summary_inLayout.setError(getContext().getString(required));
            valid = false;
        }
        
        else if (summary_input.getText().toString().length() < 25) {
            summary_inLayout.setError(getContext().getString(required_25));
            valid = false;
        }

        else if (message_input.getText().toString().isEmpty()) {
            message_inLayout.setError(getContext().getString(required));
            valid = false;
        }

        else if (message_input.getText().toString().length() < 100) {
            message_inLayout.setError(getContext().getString(required_100));
            valid = false;
        }

        else {
            valid = true;
        }
        
        return valid;
    }

    private void sendMorningTea() {
        Log.d(TAG, "sendMorningTea: ");

        morningTeaReference.document().set(morningTea).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull final Task<Void> task) {
                if (task.isSuccessful()) {
                    /*show message when successful */
                    popupView.dismissWith(new Runnable() {
                        @Override
                        public void run() {
                            showPopUp("Morning tea sent ...");
                            /**/
                            smartDismiss();
                        }
                    });
                }

                else {
                    /*show message when failed*/
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

    /*show popup*/
    private void showPopUp(String string_notify) {
        new XPopup.Builder(getContext())
                .asConfirm("IshiSmart.!", string_notify, null,
                        "Okay", null, null,
                        true, 0).show();
    }
}
