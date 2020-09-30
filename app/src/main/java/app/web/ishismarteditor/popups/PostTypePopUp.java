package app.web.ishismarteditor.popups;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.material.button.MaterialButton;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BottomPopupView;

import app.web.ishismarteditor.R;

public class PostTypePopUp extends BottomPopupView {

    private MaterialButton morning_tea, daily_poster;

    public PostTypePopUp(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.popup_bottom_post_type;
    }

    @Override
    protected void onCreate() {
        super.onCreate();

        morning_tea = findViewById(R.id.morning_tea);
        daily_poster = findViewById(R.id.daily_poster);

        /*morning tea click*/
        morning_tea.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                dismissWith(new Runnable() {
                    @Override
                    public void run() {
                        new XPopup.Builder(getContext()).hasStatusBar(true).hasNavigationBar(true)
                                .asCustom(new MorningTeaPopUp(getContext())).show();
                    }
                });

            }
        });
    }
}
