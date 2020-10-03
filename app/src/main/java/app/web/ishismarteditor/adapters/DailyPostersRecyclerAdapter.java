package app.web.ishismarteditor.adapters;

import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Random;

import app.web.ishismarteditor.R;
import app.web.ishismarteditor.models.DailyPoster;
import app.web.ishismarteditor.ui.ViewDailyPostersPost;
import me.ngarak.timeagotextview.TimeAgoTextView;

public class DailyPostersRecyclerAdapter extends RecyclerView.Adapter<DailyPostersRecyclerAdapter.ViewHolder> {

    private static String TAG = "Daily Poster TeaRecycler Adapter : ";
    private List<DailyPoster> dailyPosterList;

    public DailyPostersRecyclerAdapter(List<DailyPoster> dailyPosterList) {
        this.dailyPosterList = dailyPosterList;
    }

    @NonNull
    @Override
    public DailyPostersRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_activities_daily_posters_layout, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DailyPostersRecyclerAdapter.ViewHolder holder, int position) {

        Random random = new Random();
        int color = Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));

        if (dailyPosterList != null) {
            holder.view.setBackgroundColor(color);
            holder.poster_date.setText(
                    dailyPosterList.get(position).getPost_date().getDate() + " "
                            + dailyPosterList.get(position).getPost_date().getMonth() + " "
                            +dailyPosterList.get(position).getPost_date().getYear());

            holder.poster_summary.setText(dailyPosterList.get(position).getPoster_summary());
            holder.post_time.setDate(dailyPosterList.get(position).getPost_date().getTimestamp().toDate());
            Glide.with(holder.itemView.getContext()).load(dailyPosterList.get(position).getPoster_image_url()).into(holder.poster_image);
        }

        /*view full post*/
        holder.itemView.setOnClickListener(v -> {
            Log.d(TAG, "onBindViewHolder: ");

            Intent intent = new Intent(holder.itemView.getContext(), ViewDailyPostersPost.class);
            intent.putExtra("id", dailyPosterList.get(position).getId());
            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return this.dailyPosterList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        View view;
        TextView poster_date ;
        TextView poster_summary;
        ImageView poster_image;
        TimeAgoTextView post_time;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            view = itemView.findViewById(R.id.color_view);
            poster_date = itemView.findViewById(R.id.post_date);
            poster_summary = itemView.findViewById(R.id.poster_summary);
            poster_image = itemView.findViewById(R.id.poster_image);
            post_time = itemView.findViewById(R.id.post_time_ago);
        }
    }
}
