package app.web.ishismarteditor.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.util.Executors;
import com.lxj.xpopup.XPopup;

import java.util.ArrayList;
import java.util.List;

import app.web.ishismarteditor.R;
import app.web.ishismarteditor.adapters.MyActivitiesAdapter;
import app.web.ishismarteditor.databinding.ActivityHomeBinding;
import app.web.ishismarteditor.models.MorningTea;
import app.web.ishismarteditor.popups.PostTypePopUp;

import static app.web.ishismarteditor.utils.AppUtils.morningTeaReference;

public class Home extends AppCompatActivity {

    private static String TAG = "Home Activity";
    private ActivityHomeBinding binding;

    private List<MorningTea> teaList = new ArrayList<>();
    private MorningTea morningTea;
    private MyActivitiesAdapter activitiesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        /*view adding bottom sheet*/
        binding.addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*showing popup*/
                new XPopup.Builder(Home.this).asCustom(
                        new PostTypePopUp(Home.this)
                ).show();
            }
        });

        Executors.BACKGROUND_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                /*getting my activities list*/
                getLiveActivities();
            }
        });

        binding.myActivitiesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: " + teaList.get(position).getId());

                Intent intent = new Intent(Home.this, ViewPost.class);
                intent.putExtra("id", teaList.get(position).getId());
                startActivity(intent);
            }
        });
    }

    private void getLiveActivities() {
        morningTeaReference.orderBy("post_date.timestamp", Query.Direction.DESCENDING).addSnapshotListener(Home.this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w(TAG, "Listen failed.", error);
                    return;
                }

                /*clear list first before adding data*/
                teaList.clear();

                for (QueryDocumentSnapshot queryDocumentSnapshot : querySnapshot) {
                    Log.d(TAG, "ID: " + queryDocumentSnapshot.getId() );

                    /*converting snapshot to pojo class*/
                    morningTea = queryDocumentSnapshot.toObject(MorningTea.class);
                    teaList.add(morningTea);
                }

                /*setting adapter*/
                activitiesAdapter = new MyActivitiesAdapter(Home.this, R.layout.my_activites_timeline_layout, teaList);
                binding.myActivitiesList.setAdapter(activitiesAdapter);

                if (teaList.size() == 0) {
                    binding.emptyActivitiesLayout.setVisibility(View.VISIBLE);
                }
                else {
                    binding.emptyActivitiesLayout.setVisibility(View.GONE);
                }
            }
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