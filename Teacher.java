import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Teacher class
 *
 * <p>Purdue University -- CS18000 -- Fall 2021</p>
 *
 * @author Aditi Patel, Prachi Sacheti, Donna Prince Purdue CS
 * @version December 12, 2021
 */


public class Teacher extends UserInfo {

    private ArrayList<Courses> courses;
    private ArrayList<DiscussionForum> discussionForums;
    private ArrayList<Reply> replies;
    DataBase d = new DataBase("backup.txt");
    Scanner scanner = new Scanner(System.in);

    /**
     * creates a Teacher object with their inputted username and password
     */

    public Teacher(String username) throws IOException {
        super(username);
        this.courses = new ArrayList<Courses>();
        this.discussionForums = new ArrayList<DiscussionForum>();
        this.replies = new ArrayList<Reply>();
    }

    public DiscussionForum discussionForumImportFile() throws IOException {
        System.out.println("Enter the path of the file you would like to import.");
        ArrayList<String> fileImportInfo = new ArrayList<String>();
        String fileName = scanner.nextLine();
        File file = new File(fileName);
        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String fileImportLine = bufferedReader.readLine();
        while (fileImportLine != null) {
            fileImportInfo.add(fileImportLine);
            fileImportLine = bufferedReader.readLine();
        }
        bufferedReader.close();
        DiscussionForum discussionForum = new DiscussionForum(fileImportInfo.get(0), fileImportInfo.get(1));
        return discussionForum;
    }

    public DiscussionForum discussionForumImportFileWithString(String filePath) throws IOException {
        ArrayList<String> fileImportInfo = new ArrayList<String>();
        File file = new File(filePath);
        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String fileImportLine = bufferedReader.readLine();
        while (fileImportLine != null) {
            fileImportInfo.add(fileImportLine);
            fileImportLine = bufferedReader.readLine();
        }
        bufferedReader.close();
        DiscussionForum discussionForum = new DiscussionForum(fileImportInfo.get(0), fileImportInfo.get(1));
        return discussionForum;
    }

    public ArrayList<Courses> getCourses() {
        return courses;
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

    /**
     * allows the teacher to add a course to the Courses arraylist
     */

    public void addCourses(Courses course) {
        courses.add(course);
    }

    /**
     * allows the teacher to find a Courses object
     */

    public Courses findCourse(String courseName, List<Courses> coursesList) {
        for (int i = 0; i < coursesList.size(); i++) {
            if (courseName.equals(coursesList.get(i).getName())) {
                return coursesList.get(i);
            }
        }
        return null;
    }

    public boolean findMatchingCourse(List<Courses> coursesList, String courseName) {
        for (int i = 0; i < coursesList.size(); i++) {
            if (courseName.equals(coursesList.get(i).getName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * The method deleteAccount allows user to delete their account, which will
     * remove their username and password from within the stored array, the next time
     * they try to login after deleting their account, they will be unable to do so.
     */

    public void deleteAccount(String username) throws IOException {
        //reading the UserInfo document
        File f = new File("UserInfo.txt");
        FileReader fr = new FileReader(f);
        BufferedReader bfr = new BufferedReader(fr);

        ArrayList<String> userInfoContent = new ArrayList<String>(); // all information from the userinfo test
        //is going into the userInfoContent
        String line = bfr.readLine();
        if (line != null) {
            userInfoContent.add(line);
        }
        while (line != null) {
            line = bfr.readLine();
            if (line != null) {
                userInfoContent.add(line);
            }
        }
        bfr.close();

        ArrayList<String> userTemporary = new ArrayList<String>();
        boolean foundUser = false;
        while (!foundUser) { //until a valid username is found, continue looping
            System.out.println("Please enter your username to confirm deleting your account:");
            String userName = scanner.nextLine();

            for (int i = 0; i < userInfoContent.size(); i++) {
                line = userInfoContent.get(i);//get each element of the userInfoContent array
                String[] userInfoSeperated = line.split(",");
                if (!(userName.equals(userInfoSeperated[0]))) { //go through each line of userInfoContent and
                    //take the first element
                    userTemporary.add(line); //if username matches then it will not be written to file
                } else {
                    foundUser = true;
                }
            }
            if (foundUser == false) {
                System.out.println("Error, no such user exists: " + userName);
            }
        }

        f = new File("UserInfo.txt");
        FileOutputStream fos = new FileOutputStream(f, false);
        PrintWriter pw = new PrintWriter(fos);
        for (int i = 0; i < userTemporary.size(); i++) {
            line = userTemporary.get(i);
            pw.println(line);
        }
        pw.close();
        System.out.println("Your account has successfully been deleted");
    }

    /**
     * The method editAccount allows user to edit their account username and password. Once
     * they have selected to do so, they can type in their new username or password, and will be
     * notified if it already exists, if so they will be prompted ot type another username/password.
     * If they choose a unique identifier, this new combination will be saved into the users/passwords
     * array, allowing them to login in with the new combination.
     */

    public void editAccount() throws IOException {
        System.out.println("You have selected to edit your account password");
        File f = new File("UserInfo.txt");
        FileReader fr = new FileReader(f);
        BufferedReader bfr = new BufferedReader(fr);

        ArrayList<String> userInfoContent = new ArrayList<String>(); // all information from the userinfo test
        //is going into the userInfoContent
        String line = bfr.readLine();
        if (line != null) {
            userInfoContent.add(line);
        }
        while (line != null) {
            line = bfr.readLine();
            if (line != null) {
                userInfoContent.add(line);
            }
        }
        bfr.close();

        boolean foundUser = false;
        ArrayList<String> userTemporary = new ArrayList<String>();
        while (!foundUser) { //until a valid username is found, continue looping
            userTemporary.clear(); //clears an array
            System.out.println("Please enter your username to modify your password:");
            String userName = scanner.nextLine();

            for (int i = 0; i < userInfoContent.size(); i++) {
                line = userInfoContent.get(i);//get each element of the userInfoContent array
                String[] userInfoSeperated = line.split(","); //contains only 1 line from the file
                if (!(userName.equals(userInfoSeperated[0]))) { //go through each line of userInfoContent and
                    //take the second element
                    userTemporary.add(line); //if username matches then it will not be written to file
                } else {
                    foundUser = true;
                    System.out.println("Please enter your new password");
                    String newPassword = scanner.nextLine();
                    userInfoSeperated[1] = newPassword;
                    String newLine = userInfoSeperated[0] + ", " + userInfoSeperated[1] + "," + userInfoSeperated[2];
                    userTemporary.add(newLine);
                }
            }
            if (foundUser == false) {
                System.out.println("Error, no such user exists: " + userName);
            }
        } // !founduser

        f = new File("UserInfo.txt");
        FileOutputStream fos = new FileOutputStream(f, false);
        PrintWriter pw = new PrintWriter(fos);
        for (int i = 0; i < userTemporary.size(); i++) {
            line = userTemporary.get(i);
            pw.println(line);
        }
        pw.close();
        System.out.println("Your account password has been modified");
        // while loop want to edit account
    }
}
