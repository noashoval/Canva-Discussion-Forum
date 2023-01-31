import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Student class
 *
 * <p>Purdue University -- CS18000 -- Fall 2021</p>
 *
 * @author Aditi Patel, Prachi Sacheti, Donna Prince, Noa Shoval Purdue CS
 * @version December 12, 2021
 */


public class Student extends UserInfo {
    DataBase d = new DataBase("backup.txt");
    private ArrayList<Courses> courses;
    Scanner scanner = new Scanner(System.in);
    ArrayList<DiscussionForum> forums;
    ArrayList<DiscussionPost> posts;

    public Student(String username) throws IOException {
        super(username);
        this.courses = new ArrayList<Courses>();
        this.forums = new ArrayList<DiscussionForum>();
        this.posts = new ArrayList<DiscussionPost>();
    }

    /**
     * prints a list of the courses in the Courses arraylist
     */

    public void viewCourses(List<Courses> courses) {
        for (int i = 0; i < courses.size(); i++) {
            System.out.println(courses.get(i).getName());
        }
    }

    public ArrayList<Courses> viewArrayOfCourses(List<Courses> courses) {
        ArrayList<Courses> listOfCourses = new ArrayList<>();
        for (int i = 0; i < courses.size(); i++) {
            listOfCourses.add(courses.get(i));
        }
        return listOfCourses;
    }

    public Courses findCourse(String courseName, List<Courses> coursesList) {
        for (int i = 0; i < coursesList.size(); i++) {
            if (courseName.equals(coursesList.get(i).getName())) {
                return coursesList.get(i);
            }
        }
        return null;
    }

    public DiscussionPost discussionPostImportFile(String filepath, String username, int postNumber) throws IOException {
        ArrayList<String> fileImportInfo = new ArrayList<String>();
        File file = new File(filepath);
        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String fileImportLine = bufferedReader.readLine();
        while (fileImportLine != null) {
            fileImportInfo.add(fileImportLine);
            fileImportLine = bufferedReader.readLine();
        }
        bufferedReader.close();
        String postContent = fileImportInfo.get(0);
        //System.out.println(postContent);
        DiscussionPost dp = new DiscussionPost(postContent, username, postNumber);
        return dp;
    }

    public void addForum(DiscussionForum forum) {
        this.forums.add(forum);
    }

    public void addPost(DiscussionPost post) {
        this.posts.add(post);
    }

    public ArrayList<DiscussionPost> getPosts() {
        return this.posts;
    }

}
