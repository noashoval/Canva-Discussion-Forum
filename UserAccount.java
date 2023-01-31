import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * UserAccountMethods class
 *
 * <p>Purdue University -- CS18000 -- Fall 2021</p>
 *
 * @author Prachi Sacheti Purdue CS
 * @version December 12, 2021
 */


public class UserAccountMethods {
    private static String accountAnswer;
    private static String newUsername;
    String userType;
    String user = "";
    String username;
    String password;
    String y = "";

    //method that reads the userInfo file and adds the log in info to an array list called logInDetails
    public ArrayList<String> userLogInInfo() throws IOException {
        ArrayList<String> userLogin = new ArrayList<>();
        String[] breaking = new String[userLogin.size() * 2];
        ArrayList<String> logInDetails = new ArrayList<>();

        //reading from the file that has stored all the login info and converting it into an arraylist(logInDets)
        File f = new File("UserInfo.txt");
        FileReader fr = new FileReader(f);
        BufferedReader bfr = new BufferedReader(fr);
        String s1 = "";

        while (s1 != null) {
            s1 = bfr.readLine();
            if (s1 == null) {
                break;
            } else {
                userLogin.add(s1);
                for (int i = 0; i < userLogin.size(); i++) {
                    String x = userLogin.get(i);
                    breaking = x.split(", ");
                    String userUsername = breaking[0];
                    String userPassword = breaking[1];
                    String teacherOrStudent = breaking[2];
                    logInDetails.add(userUsername);
                    logInDetails.add(userPassword);
                    logInDetails.add(teacherOrStudent);
                } // end of else statement that reads line from userInfo text file and stores it as an array.
            } // emd of if statement that checks whether line is null.
        } //end of while statement
        return logInDetails;
    }

    //method that checks if the username exists
    public String sameUsername(String newUsername) throws IOException {
        String usernameExists = "";
        for (int i = 0; i < userLogInInfo().size(); i += 3) {
            y = userLogInInfo().get(i);
            if (y.equals(newUsername)) {
                usernameExists = "this username already exists";
                break;
            } else {
                usernameExists = "account created";
            }
        }
        return usernameExists;
    }

    //method that adds new user info to the array list (logInDetails) and the text file (UserInfo.txt)
    public void addNewUserInfo(String newUsername, String password, String user) throws IOException {
        userLogInInfo().add(newUsername);
        userLogInInfo().add(password);
        UserInfo userInfo = new UserInfo(newUsername, password, user);
        File file = new File("UserInfo.txt");
        FileOutputStream fos = new FileOutputStream(file, true);
        PrintWriter pw = new PrintWriter(fos);
        pw.println(userInfo.toString());
        pw.close();
    }

    //Takes username and password fom the user and checks if the info is correct. if it is, the user logs in.
    public boolean incorrectInfo(String username, String password) throws IOException {
        boolean incorrectInfo = true;
        ArrayList<String> loggingInInformation = userLogInInfo();
        for (int i = 0; i < loggingInInformation.size(); i += 3) {
            if (username.equals(loggingInInformation.get(i)) && password.equals(loggingInInformation.get(i + 1))) {
                incorrectInfo = false;
                break;
            }
        }
        return incorrectInfo;
    }

    public String teacherOrStudent(String username, String password) throws IOException {
        String teacherOrStudent = "";
        for (int i = 0; i < userLogInInfo().size(); i += 3) {
            if (username.equals(userLogInInfo().get(i)) && password.equals(userLogInInfo().get(i + 1)) &&
                "Teacher".equals(userLogInInfo().get(i + 2))) {
                teacherOrStudent = "teacher";
                break;
                // implement teacher code
            } else if (username.equals(userLogInInfo().get(i)) && password.equals(userLogInInfo().get(i + 1))
                && "Student".equals(userLogInInfo().get(i + 2))) {
                teacherOrStudent = "student";
                break;
            }
        }
        return teacherOrStudent;
    }
}
