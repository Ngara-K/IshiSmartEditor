package app.web.ishismarteditor.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

import java.io.Serializable;

public class DailyPoster implements Serializable {

    private long id;
    private String poster_summary;
    private String poster_image_url;
    private String author_id;
    private DocumentReference editor_ref;
    private long time_mills;
    private PostDate post_date;

    DailyPoster() {

    }

    public DailyPoster(long id, String poster_summary, String poster_image_url, String author_id, DocumentReference editor_ref, long time_mills, PostDate post_date) {
        this.id = id;
        this.poster_summary = poster_summary;
        this.poster_image_url = poster_image_url;
        this.author_id = author_id;
        this.editor_ref = editor_ref;
        this.time_mills = time_mills;
        this.post_date = post_date;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPoster_summary() {
        return poster_summary;
    }

    public void setPoster_summary(String poster_summary) {
        this.poster_summary = poster_summary;
    }

    public String getAuthor_id() {
        return author_id;
    }

    public void setAuthor_id(String author_id) {
        this.author_id = author_id;
    }

    public DocumentReference getEditor_ref() {
        return editor_ref;
    }

    public void setEditor_ref(DocumentReference editor_ref) {
        this.editor_ref = editor_ref;
    }

    public long getTime_mills() {
        return time_mills;
    }

    public void setTime_mills(long time_mills) {
        this.time_mills = time_mills;
    }

    public PostDate getPost_date() {
        return post_date;
    }

    public void setPost_date(PostDate post_date) {
        this.post_date = post_date;
    }

    public String getPoster_image_url() {
        return poster_image_url;
    }

    public void setPoster_image_url(String poster_image_url) {
        this.poster_image_url = poster_image_url;
    }

    public static class PostDate implements Serializable {
        private String date;
        private String month;
        private String year;
        private Timestamp timestamp;

        PostDate() {

        }

        public PostDate(String date, String month, String year, Timestamp timestamp) {
            this.date = date;
            this.month = month;
            this.year = year;
            this.timestamp = timestamp;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getMonth() {
            return month;
        }

        public void setMonth(String month) {
            this.month = month;
        }

        public String getYear() {
            return year;
        }

        public void setYear(String year) {
            this.year = year;
        }

        public Timestamp getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(Timestamp timestamp) {
            this.timestamp = timestamp;
        }
    }
}
