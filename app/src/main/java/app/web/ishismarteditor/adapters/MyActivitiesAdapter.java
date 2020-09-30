package app.web.ishismarteditor.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;
import java.util.Random;

import app.web.ishismarteditor.R;
import app.web.ishismarteditor.models.MorningTea;
import me.ngarak.timeagotextview.TimeAgoTextView;

public class MyActivitiesAdapter extends ArrayAdapter<MorningTea> {

    private static String TAG = "My Activities Adapter";

    MorningTea morningTea;

    public MyActivitiesAdapter(@NonNull Context context, int resource, @NonNull List<MorningTea> teaList) {
        super(context, resource, teaList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        morningTea = getItem(position);

        if(convertView == null){
            convertView =  ((Activity)getContext()).getLayoutInflater().inflate(R.layout.my_activites_timeline_layout,
                    parent,false);
        }

        Random random = new Random();
        int color = Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));

        View view = convertView.findViewById(R.id.color_view);
        TextView date_tv = convertView.findViewById(R.id.activity_date);
        TextView month_tv = convertView.findViewById(R.id.activity_month);
        TextView year_tv = convertView.findViewById(R.id.activity_year);
        TextView message_title = convertView.findViewById(R.id.post_title);
        TimeAgoTextView post_time = convertView.findViewById(R.id.post_time_ago);

        if (morningTea != null) {
            view.setBackgroundColor(color);
            date_tv.setText(morningTea.getPost_date().getDate());
            date_tv.setTextColor(color);

            month_tv.setText(morningTea.getPost_date().getMonth());
            year_tv.setText(morningTea.getPost_date().getYear());

            message_title.setText(morningTea.getMessage_title());
            post_time.setDate(morningTea.getPost_date().getTimestamp().toDate());
        }

        return convertView;
    }
}
