import java.time.LocalTime;
import java.util.ArrayList;

/**
 * Courses class
 *
 * <p>Purdue University -- CS18000 -- Fall 2021</p>
 *
 * @author Aditi Patel Purdue CS
 * @version December 12, 2021
 */

public class Courses {
    private String name;
    private ArrayList<DiscussionForum> discussionForums;
    private ArrayList<DiscussionPost> discussionPosts;
    private String title;
    private String prompt;
    private LocalTime timeCreated;

    /**
     * creates a Course constructor and initializes the Courses and Discussion Forum array
     */

    public Courses(String name) {
        this.name = name;
        discussionForums = new ArrayList<>();
        discussionPosts = new ArrayList<>();
    }

    public Courses() {
    }

    public Courses(LocalTime timeCreated) {
        this.timeCreated = timeCreated;
    }


    /**
     * returns course name
     */

    public String getName() {
        return name;
    }


    public void setTitle(String title) {
        this.title = title;
    }


    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    /**
     * adds a discussion forum object to the discussionForums arraylist
     */

    public ArrayList<DiscussionForum> getForums() {
        return this.discussionForums;
    }

    public void addDiscussionForum(DiscussionForum forum) {
        discussionForums.add(forum);
    }

    /**
     * edits a discussion forum object (title and prompt) and replaces the old discussion forum
     */

    public void editDiscussionForum(String oldDiscussionTitle, String newDiscussionTitle, String newDiscussionPrompt) {
        for (int i = 0; i < discussionForums.size(); i++) {
            if (oldDiscussionTitle.equals(discussionForums.get(i).getDiscussionTitle())) {
                DiscussionForum forum = new DiscussionForum(newDiscussionTitle, newDiscussionPrompt);
                discussionForums.set(i, forum);
            }
        }
    }

    /**
     * deletes a discussion forum object from the discussionForums arraylist
     */

    public void deleteDiscussionForum(String forumName) {
        for (int i = 0; i < discussionForums.size(); i++) {
            if (forumName.equals(discussionForums.get(i).getDiscussionTitle())) {
                discussionForums.remove(discussionForums.get(i));
            }
        }
    }

    /**
     * prints a list of all the existing forums within a course
     */

    public void viewForumTitle() {
        for (int i = 0; i < discussionForums.size(); i++) {
            System.out.println(discussionForums.get(i).getDiscussionTitle());
        }
    }

    public ArrayList<String> viewArrayofForumTitle() {
        ArrayList<String> listOfForums = new ArrayList<>();
        for (int i = 0; i < discussionForums.size(); i++) {
            listOfForums.add(discussionForums.get(i).getDiscussionTitle());
        }
        return listOfForums;
    }


    /**
     * returns the discussion forum with the matching title that the user enters
     */

    public DiscussionForum findForum(String forumTitle) {
        for (int i = 0; i < discussionForums.size(); i++) {
            if (forumTitle.equals(discussionForums.get(i).getDiscussionTitle())) {
                return discussionForums.get(i);
            }
        }
        return null;
    }

    public boolean findMatchingForum(String forumTitle) {
        for (int i = 0; i < discussionForums.size(); i++) {
            if (forumTitle.equals(discussionForums.get(i).getDiscussionTitle())) {
                return true;
            }
        }
        return false;
    }


    @Override
    public String toString() {
        String returnString = "CCCC%%" + getName() + "\n";
        for (DiscussionForum df : discussionForums) {
            returnString = returnString + df.toString();
        }
        return returnString;
    }
}
