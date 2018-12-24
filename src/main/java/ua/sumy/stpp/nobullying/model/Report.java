package ua.sumy.stpp.nobullying.model;


import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@NamedQuery(name = "fetchAllReports", query = "SELECT r FROM Report r")
public class Report implements Model, Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String username;
    private String text;

    // todo: fields for photo, video and audio files.

    private Date sentDate;
    private Date beginMentoringDate;
    private Date finishedDate;

    public enum ProcessingState {
        NEW, MODERATING, FINISHED
    }

    @Enumerated
    private ProcessingState state = ProcessingState.NEW;

    public Report() {
    }

    public Report(String username, String text, Date sentDate) {
        this.username = username;
        this.text = text;
        this.sentDate = sentDate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getSentDate() {
        return sentDate;
    }

    public void setSentDate(Date sentDate) {
        this.sentDate = sentDate;
    }

    public Date getBeginMentoringDate() {
        return beginMentoringDate;
    }

    public void setBeginMentoringDate(Date beginMentoringDate) {
        this.beginMentoringDate = beginMentoringDate;
    }

    public Date getFinishedDate() {
        return finishedDate;
    }

    public void setFinishedDate(Date finishedDate) {
        this.finishedDate = finishedDate;
    }

    public ProcessingState getState() {
        return state;
    }

    public void setState(ProcessingState state) {
        this.state = state;
    }

    public boolean isNull() {
        return false;
    }
}
