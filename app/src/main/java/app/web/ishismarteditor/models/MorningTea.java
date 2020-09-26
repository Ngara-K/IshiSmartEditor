package app.web.ishismarteditor.models;

import com.google.firebase.firestore.DocumentReference;

public class MorningTea {

    private String message_title;
    private String message_summary;
    private String message_body;
    private String author_id;
    private DocumentReference editor_ref;

    MorningTea () {

    }

    public MorningTea(String message_title, String message_summary, String message_body, String author_id, DocumentReference editor_ref) {
        this.message_title = message_title;
        this.message_summary = message_summary;
        this.message_body = message_body;
        this.author_id = author_id;
        this.editor_ref = editor_ref;
    }

    public String getMessage_title() {
        return message_title;
    }

    public void setMessage_title(String message_title) {
        this.message_title = message_title;
    }

    public String getMessage_summary() {
        return message_summary;
    }

    public void setMessage_summary(String message_summary) {
        this.message_summary = message_summary;
    }

    public String getMessage_body() {
        return message_body;
    }

    public void setMessage_body(String message_body) {
        this.message_body = message_body;
    }

    public DocumentReference getEditor_ref() {
        return editor_ref;
    }

    public void setEditor_ref(DocumentReference editor_ref) {
        this.editor_ref = editor_ref;
    }

    public String getAuthor_id() {
        return author_id;
    }

    public void setAuthor_id(String author_id) {
        this.author_id = author_id;
    }
}
