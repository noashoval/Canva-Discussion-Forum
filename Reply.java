import java.time.LocalTime;

/**
 * Reply class
 *
 * <p>Purdue University -- CS18000 -- Fall 2021</p>
 *
 * @author Enoch Qin Purdue CS
 * @version December 12, 2021
 */


public class Reply {
    private int votes;
    private String message;
    private LocalTime timeCreated;
    private Student student;
    private Teacher teacher;

    public Reply(String message) {
        this.message = message;
        this.votes = 0;
        this.timeCreated = LocalTime.now();
    }

    public Reply(String message, Student student) {
        this.message = message;
        this.votes = 0;
        this.timeCreated = LocalTime.now();
        this.student = student;
    }

    public Reply(String message, Teacher teacher) {
        this.message = message;
        this.votes = 0;
        this.timeCreated = LocalTime.now();
        this.teacher = teacher;
    }

    public Reply(int votes, String message, LocalTime timeCreated, Student student) {
        this.votes = votes;
        this.message = message;
        this.timeCreated = timeCreated;
        this.student = student;
    }

    public Reply(int votes, String message, LocalTime timeCreated) {
        this.votes = votes;
        this.message = message;
        this.timeCreated = timeCreated;
    }

    public void addVote() {
        this.votes++;
    }

    public int getVotes() {
        return this.votes;
    }

    public String getMessage() {
        return this.message;
    }

    public Student getStudent() {
        return student;
    }

    public LocalTime getTimeCreated() {
        return timeCreated;
    }

    public String toString() {
        String userName = null;
        if (this.student != null) {
            userName = student.getUsername();
        } else if (this.teacher != null) {
            userName = teacher.getUsername();
        }
        return "\n\tRRRR%%" + getVotes() + "%%" + getMessage() + "%%" + getTimeCreated() + "%%" + userName;
    }
}
