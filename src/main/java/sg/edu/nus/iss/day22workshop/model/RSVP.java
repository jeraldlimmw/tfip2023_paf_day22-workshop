package sg.edu.nus.iss.day22workshop.model;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.servlet.http.HttpServletRequest;

public class RSVP {
    private Integer id;
    private String name;
    private String email;
    private String phone;
    private DateTime confirmationDate;
    private String comments;
    
    public RSVP() {
    }

    public RSVP(Integer id, String name, String email, String phone
            , DateTime confirmationDate, String comments) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.confirmationDate = confirmationDate;
        this.comments = comments;
    }
    
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public DateTime getConfirmationDate() {
        return confirmationDate;
    }
    public void setConfirmationDate(DateTime confirmationDate) {
        this.confirmationDate = confirmationDate;
    }
    public String getComments() {
        return comments;
    }
    public void setComments(String comments) {
        this.comments = comments;
    }

    @Override
    public String toString() {
        return "RSVP [id=" + id + ", name=" + name + ", email=" + email + ", phone=" + phone + ", confirmationDate="
                + confirmationDate + ", comments=" + comments + "]";
    }

    public static RSVP create(SqlRowSet rs) {
        RSVP rsvp = new RSVP();

        rsvp.setId(rs.getInt("id"));
        rsvp.setName(rs.getString("name"));
        rsvp.setEmail(rs.getString("email"));
        rsvp.setPhone(rs.getString("phone"));
        DateTime d = new DateTime(DateTimeFormat
                .forPattern("dd-MM-yyyy")
                .parseDateTime(rs.getString("confirmation_date")));
        rsvp.setConfirmationDate(d);
        rsvp.setComments(rs.getString("comments"));

        return rsvp;
    }

    public JsonObject toJson() {
        return Json.createObjectBuilder()
            .add("id", getId())
            .add("name", getName())
            .add("email", getEmail())
            .add("phone", getPhone())
            .add("confirmation_date", getConfirmationDate().toString())
            .add("comments", getComments())
            .build();
    }

    public static RSVP createFromReq(HttpServletRequest httpRequest) {
        RSVP rsvp = new RSVP();
        rsvp.setName(httpRequest.getParameter("name"));
        rsvp.setEmail(httpRequest.getParameter("email"));
        rsvp.setPhone(httpRequest.getParameter("phone"));
        DateTime d = new DateTime(DateTimeFormat
                .forPattern("dd-MM-yyyy")
                .parseDateTime(httpRequest.getParameter("confirmation_date")));
        rsvp.setConfirmationDate(d);
        rsvp.setComments(httpRequest.getParameter("comments"));
        return rsvp;
    }
}
