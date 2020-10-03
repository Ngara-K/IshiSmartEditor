package app.web.ishismarteditor.adapters;

import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Random;

import app.web.ishismarteditor.R;
import app.web.ishismarteditor.models.MorningTea;
import app.web.ishismarteditor.ui.ViewMorningTeaPost;
import me.ngarak.timeagotextview.TimeAgoTextView;

public class MorningTeaRecyclerAdapter extends RecyclerView.Adapter<MorningTeaRecyclerAdapter.ViewHolder> {

    private static String TAG = "Morning TeaRecycler Adapter : ";
    private List<MorningTea> morningTeaList;

    public MorningTeaRecyclerAdapter(List<MorningTea> morningTeaList) {
        this.morningTeaList = morningTeaList;
    }

    @NonNull
    @Override
    public MorningTeaRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_activites_morning_tea_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MorningTeaRecyclerAdapter.ViewHolder holder, int position) {

        Random random = new Random();
        int color = Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));

        if (morningTeaList != null) {
            holder.view.setBackgroundColor(color);
            holder.date_tv.setText(morningTeaList.get(position).getPost_date().getDate());
            holder.date_tv.setTextColor(color);

            holder.month_tv.setText(morningTeaList.get(position).getPost_date().getMonth());
            holder.year_tv.setText(morningTeaList.get(position).getPost_date().getYear());

            holder.message_title.setText(morningTeaList.get(position).getMessage_title());
            holder.post_time.setDate(morningTeaList.get(position).getPost_date().getTimestamp().toDate());
        }

        /*view full post*/
        holder.itemView.setOnClickListener(v -> {
            Log.d(TAG, "onBindViewHolder: ");

            Intent intent = new Intent(holder.itemView.getContext(), ViewMorningTeaPost.class);
            intent.putExtra("id", morningTeaList.get(position).getId());
            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return morningTeaList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        View view;
        TextView date_tv;
        TextView month_tv;
        TextView year_tv;
        TextView message_title;
        TimeAgoTextView post_time;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            view = itemView.findViewById(R.id.color_view);
            date_tv = itemView.findViewById(R.id.activity_date);
            month_tv = itemView.findViewById(R.id.activity_month);
            year_tv = itemView.findViewById(R.id.activity_year);
            message_title = itemView.findViewById(R.id.post_title);
            post_time = itemView.findViewById(R.id.post_time_ago);

        }
    }
}
