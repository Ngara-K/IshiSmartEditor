package app.web.ishismarteditor.models;

public class EditorProfile {

    private String uid;
    private String first_name;
    private String last_name;
    private long phone_number;
    private String email;
    private String location;
    private String organization;
    private String short_description;

    EditorProfile() {

    }

    public EditorProfile(String uid, String first_name, String last_name, long phone_number,
                         String email, String location, String organization, String short_description) {
        this.uid = uid;
        this.first_name = first_name;
        this.last_name = last_name;
        this.phone_number = phone_number;
        this.email = email;
        this.location = location;
        this.organization = organization;
        this.short_description = short_description;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public long getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(long phone_number) {
        this.phone_number = phone_number;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getShort_description() {
        return short_description;
    }

    public void setShort_description(String short_description) {
        this.short_description = short_description;
    }
}
