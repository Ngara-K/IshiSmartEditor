package app.web.ishismarteditor.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;

import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.util.Executors;
import com.lxj.xpopup.XPopup;

import java.util.ArrayList;
import java.util.List;

import app.web.ishismarteditor.adapters.DailyPostersRecyclerAdapter;
import app.web.ishismarteditor.adapters.MorningTeaRecyclerAdapter;
import app.web.ishismarteditor.databinding.ActivityHomeBinding;
import app.web.ishismarteditor.models.DailyPoster;
import app.web.ishismarteditor.models.MorningTea;
import app.web.ishismarteditor.popups.PostTypePopUp;

import static app.web.ishismarteditor.utils.AppUtils.dailyPosterReference;
import static app.web.ishismarteditor.utils.AppUtils.morningTeaReference;

public class Home extends AppCompatActivity {

    private static String TAG = "Home Activity";
    private ActivityHomeBinding binding;

    private List<MorningTea> teaList = new ArrayList<>();
    private List<DailyPoster> posterList = new ArrayList<>();
    private MorningTea morningTea;
    private DailyPoster dailyPoster;
    private MorningTeaRecyclerAdapter teaRecyclerAdapter;
    private DailyPostersRecyclerAdapter postersRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        /*view adding bottom sheet*/
        binding.addBtn.setOnClickListener(v -> {

            /*showing popup*/
            new XPopup.Builder(Home.this).asCustom(
                    new PostTypePopUp(Home.this)
            ).show();
        });

        /*getting my activities list*/
        Executors.BACKGROUND_EXECUTOR.execute(this::getLiveActivities);

        /**/
        binding.settingsBtn.setOnClickListener(v ->
                startActivity(new Intent(Home.this, MyProfile.class)));
    }

    private void getLiveActivities() {
        /*morning Tea*/
        morningTeaReference.orderBy("post_date.timestamp", Query.Direction.DESCENDING).limit(10).addSnapshotListener(Home.this, (querySnapshot, error) -> {
            if (error != null) {
                Log.w(TAG, "Listen failed.", error);
                return;
            }

            /*clear list first before adding data*/
            teaList.clear();

            for (QueryDocumentSnapshot queryDocumentSnapshot : querySnapshot) {
                Log.d(TAG, "MORNING TEA: " + queryDocumentSnapshot.getId());

                /*converting snapshot to pojo class*/
                morningTea = queryDocumentSnapshot.toObject(MorningTea.class);
                teaList.add(morningTea);
            }

            if (teaList.size() == 0) {
                binding.emptyTeaActivitiesLayout.setVisibility(View.VISIBLE);
            }
            else {
                binding.emptyTeaActivitiesLayout.setVisibility(View.GONE);
            }

            /*setting adapter*/
            teaRecyclerAdapter = new MorningTeaRecyclerAdapter(teaList);
            binding.morningTeaActivitiesList.setAdapter(teaRecyclerAdapter);
            binding.morningTeaActivitiesList.addItemDecoration(new DividerItemDecoration(Home.this, DividerItemDecoration.VERTICAL));
        });

        /*daily posters*/
        dailyPosterReference.orderBy("post_date.timestamp", Query.Direction.DESCENDING).limit(10).addSnapshotListener(Home.this, (querySnapshot, error) -> {
            if (error != null) {
                Log.w(TAG, "Listen failed.", error);
                return;
            }

            /*clear list first before adding data*/
            posterList.clear();

            for (QueryDocumentSnapshot queryDocumentSnapshot : querySnapshot) {
                Log.d(TAG, "DAILY POSTER: " + queryDocumentSnapshot.getId());

                /*converting snapshot to pojo class*/
                dailyPoster = queryDocumentSnapshot.toObject(DailyPoster.class);
                posterList.add(dailyPoster);

            }

            if (posterList.size() == 0) {
                binding.emptyPostersActivitiesLayout.setVisibility(View.VISIBLE);
            }
            else {
                binding.emptyPostersActivitiesLayout.setVisibility(View.GONE);
            }

            /*setting adapter*/
            postersRecyclerAdapter = new DailyPostersRecyclerAdapter(posterList);
            binding.dailyPostersActivitiesList.setAdapter(postersRecyclerAdapter);
            binding.dailyPostersActivitiesList.addItemDecoration(new DividerItemDecoration(Home.this, DividerItemDecoration.VERTICAL));
        });
    }

    @Override
    public void onBackPressed() {
        /*move app to background*/
        moveTaskToBack(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getLiveActivities();
    }

    @Override
    protected void onStart() {
        super.onStart();
        getLiveActivities();
    }
}