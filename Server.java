import java.io.*;
import java.net.*;
import java.util.ArrayList;

/**
 * FinalServer class
 *
 * <p>Purdue University -- CS18000 -- Fall 2021</p>
 *
 * @author Aditi Patel, Prachi Sacheti, Donna Prince Purdue CS
 * @version December 12, 2021
 */

public class FinalServer implements Serializable {
    private static String studentSelectedCourse;
    private static String studentSelectedForum;
    private static String username;
    private static String teacherSelectedCourse;
    private static String teacherSelectedForum;
    private static final Object gatekeeper = new Object();


    //main method
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        UserAccountMethods uam = new UserAccountMethods();
        DataBase dataBase = new DataBase("backup.txt");
        Student student = new Student(username);
        dataBase.readFromFile("backup.txt");
        Courses course = new Courses();
        UserAccountModifications accountDeletor = new UserAccountModifications();
        UserAccountModifications accoundEditor = new UserAccountModifications();
        Teacher teacher = new Teacher(username);
        ServerSocket serverSocket = new ServerSocket(4242);
        Socket socket = serverSocket.accept();
        uam.userLogInInfo();

        BufferedReader bfr = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter pw = new PrintWriter(socket.getOutputStream());

        boolean avail = true;
        //create run method and include all calculations within it
        String curLoggedInUser = null;
        UserInfo curUserObj = null;
        ArrayList<DiscussionPost> curSelectedPosts = null;
        boolean writeToDb = false;
        while (avail) {

            //reads the line sent from the client
            String logIn = bfr.readLine();

            /**
             * if a new user is logging in, gets the username, password and user type from client
             * calls the method to check if the username already exists
             * if it exists, an error message is sent to the client
             * if not, calls method that adds the new info to the UserInfo text file and arrayList.
             */

            if ("new user is logging in".equals(logIn)) {
                int accountCreated = 0;
                //checks if username exists
                do {
                    String UserType = bfr.readLine();
                    if (UserType.equals("new teacher")) {
                        String username = bfr.readLine();
                        String password = bfr.readLine();
                        if (uam.sameUsername(username).equals("this username already exists")) {
                            pw.println("username exists");
                            pw.flush();
                        } else {
                            pw.println("account created");
                            pw.flush();
                            pw.println("teacher");
                            pw.flush();
                            accountCreated++;
                            synchronized (gatekeeper) {
                                uam.addNewUserInfo(username, password, "Teacher");
                            }
                        }
                    } else if (UserType.equals("new student")) {
                        username = bfr.readLine();
                        String password = bfr.readLine();
                        if (uam.sameUsername(username).equals("this username already exists")) {
                            pw.println("username exists");
                            pw.flush();
                        } else {
                            pw.println("account created");
                            pw.flush();
                            pw.println("student");
                            pw.flush();
                            accountCreated++;
                            synchronized (gatekeeper) {
                                uam.addNewUserInfo(username, password, "Student");
                            }

                        }
                    }

                    // M logIn = bfr.readLine();
                } while (accountCreated == 0);

            }

            /**
             * if an existing user is logging in, gets the username, password from client
             * calls method to check if info is correct
             * if correct, sends logIn message to the client. else, sends an error message to the client
             */

            int userLoggedIn = 0;
            do {
                if ("existing user is logging in".equals(logIn)) {
                    username = bfr.readLine();
                    String password = bfr.readLine();
                    boolean incorrectInfo = uam.incorrectInfo(username, password);
                    if (incorrectInfo) {
                        pw.println("user cant login");
                        pw.flush();
                    } else {
                        pw.println("correct info. logging in!");
                        pw.flush();
                        if ("teacher".equals(uam.teacherOrStudent(username, password))) {
                            curLoggedInUser = username;
                            curUserObj = new Teacher(username);
                            pw.println("teacher is logging in");
                            pw.flush();
                        } else if ("student".equals(uam.teacherOrStudent(username, password))) {
                            curLoggedInUser = username;
                            curUserObj = new Student(username);
                            pw.println("student is logging in");
                            pw.flush();
                        }
                        userLoggedIn++;
                        logIn = bfr.readLine();
                    }
                } else {
                    break;
                }

            } while (userLoggedIn == 0);

            int studentCoursesCounter = 0;


            /**
             * if a student wants to view courses
             * makes an arraylist of all the courses in the text file
             * iterates through the arrayList and displays all the courses
             * stores the course selected by the student in a variable called studentSelectedCourse
             */

            if ("student is viewing existing courses".equals(logIn)) {
                ArrayList<Courses> existingCoursesArray = student.viewArrayOfCourses(dataBase.c);
                existingCoursesArray.toString();
                for (int i = 0; i < existingCoursesArray.size(); i++) {
                    pw.println("Course:" + existingCoursesArray.get(i));
                    pw.flush();
                }
                pw.println("done printing existing courses for student");
                pw.flush();
                studentCoursesCounter++;
                String studentIsSelectingCourse = bfr.readLine();
                if ("student is selecting a course".equals(studentIsSelectingCourse)) {
                    studentSelectedCourse = bfr.readLine();
                }

            }

            /**
             * if a student wants to view discussion forums
             * makes an arraylist of all the discussion forums in a course in the text file
             * iterates through the arrayList and displays all the forums
             * stores the forum selected by the student in a variable called studentSelectedForum
             */

            if ("student is viewing discussion forums".equals(logIn)) {
                ArrayList<String> existingForumsArray = student.findCourse(studentSelectedCourse, dataBase.c).
                    viewArrayofForumTitle();
                for (int i = 0; i < existingForumsArray.size(); i++) {
                    pw.println("Forum:" + existingForumsArray.get(i));
                    pw.flush();
                }
                pw.println("done printing existing student forums");
                pw.flush();
                //studentViewForumsCounter++;
                String studentIsSelectingForum = bfr.readLine();
                if ("student is selecting a forum".equals(studentIsSelectingForum)) {
                    studentSelectedForum = bfr.readLine();
                }


            }


            if ("student is importing post".equals(logIn)) {
                writeToDb = true;
                int currentPostNumber = (dataBase.getLatestPostNum() + 1);
                String filePath = bfr.readLine();
                synchronized (gatekeeper) {
                    student.findCourse(studentSelectedCourse, dataBase.c).findForum(studentSelectedForum).
                        addPost(student.discussionPostImportFile(filePath, username, currentPostNumber));
                }
                currentPostNumber++;
                pw.println("post was imported");
                pw.flush();
            }


            /**
             * if a student wants to add a post
             * assigns a post number to the post, creates a new discussion post object.
             * calls the method that adds discussion posts to the database arrayList
             * sends the post was added successfully message to the client which is displayed to the user
             */

            if ("student is adding a post".equals(logIn)) {
                writeToDb = true;
                int currentPostNumber = (dataBase.getLatestPostNum() + 1);
                String postContents = bfr.readLine();

                DiscussionPost discussionPost = new DiscussionPost(postContents, username, currentPostNumber);
                currentPostNumber++;
                synchronized (gatekeeper) {
                    student.findCourse(studentSelectedCourse, dataBase.c).findForum(studentSelectedForum).
                        addPost(discussionPost);
                }
                pw.println("post was added");
                pw.flush();
            }

            /**
             * if a student wants to view all the posts in a discussion forum
             * calls the method that calls the discussionPost toString.
             * sends the toString back to the client which is displayed to the user
             */

            if (("student is trying to view the posts to reply to").equals(logIn)) {
                curSelectedPosts = student.findCourse(studentSelectedCourse,
                    dataBase.c).findForum(studentSelectedForum).getPosts();
                String discussionPosts = student.findCourse(studentSelectedCourse,
                    dataBase.c).findForum(studentSelectedForum).toString();

                // Pack here
                String packedPosts = discussionPosts.replaceAll("\n", "||");
                pw.println(packedPosts);

                pw.flush();
            }

            if (("check_user_and_post_num").equals(logIn)) {
                String postedStudentName = bfr.readLine();
                int postNumber = Integer.parseInt(bfr.readLine());
                String foundPost = "post not found";
                for (DiscussionPost post : curSelectedPosts) {
                    if (post.getPostNumber() == postNumber && post.getUsername().equals(postedStudentName)) {
                        foundPost = "post found";
                    }
                }
                pw.println(foundPost);
                pw.flush();
            }

            if (("user is replying to post").equals(logIn)) {
                writeToDb = true;
                PostReplier postReplier = new PostReplier();
                String postedStudentName = bfr.readLine();
                int postNumber = Integer.parseInt(bfr.readLine());
                String replyContent = bfr.readLine();
                String postResult = postReplier.replyToPost(postNumber, replyContent, postedStudentName,
                    curLoggedInUser, curSelectedPosts, curUserObj);
                pw.println(postResult);
                pw.flush();
            }

            /**
             * if a teacher wants to view courses
             * makes an arraylist of all the courses in the text file
             * iterates through the arrayList and displays all the courses
             * stores the course selected by the student in a variable called studentSelectedCourse
             */

            if ("teacher is viewing existing courses".equals(logIn)) {
                ArrayList<Courses> existingCoursesArray = teacher.viewArrayOfCourses(dataBase.c);
                existingCoursesArray.toString();
                for (int i = 0; i < existingCoursesArray.size(); i++) {
                    pw.println("Course:" + existingCoursesArray.get(i));
                    pw.flush();
                }
                pw.println("done printing existing courses");
                pw.flush();
                String teacherIsSelectingCourse = bfr.readLine();
                if ("teacher is selecting a course".equals(teacherIsSelectingCourse)) {
                    teacherSelectedCourse = bfr.readLine();
                }
                // M logIn = bfr.readLine();
            }

            /**
             * if a teacher wants to add a course
             * takes in the course that the teacher enters in order to make sure it doesn't exist already
             * if it exists, the user is prompted to enter another name
             * if it does not exist, it is added to the courses arraylist within the database and stores it in the
             * backup.txt file
             */

            if ("teacher is adding a new course".equals(logIn)) {
                writeToDb = true;
                int courseAdded = 0;
                do {
                    String courseName = bfr.readLine();
                    if (teacher.findMatchingCourse(dataBase.c, courseName)) {
                        pw.println("course already exists");
                        pw.flush();
                    } else {
                        Courses newCourse = new Courses(courseName);
                        synchronized (gatekeeper) {
                            dataBase.c.add(newCourse);
                        }
                        pw.println("course was added");
                        pw.flush();
                        courseAdded++;
                    }
                    // M logIn = bfr.readLine();
                } while (courseAdded == 0);

            }

            /**
             * if a teacher wants to add a discussion forum
             * takes in the title and prompt that the teacher enters and creates a discussion forum object, which is
             * added to the specific class the teacher is in
             */

            if ("teacher is adding a discussion forum".equals(logIn)) {
                writeToDb = true;
                String forumTitle = bfr.readLine();
                String forumPrompt = bfr.readLine();
                DiscussionForum forum = new DiscussionForum(forumTitle, forumPrompt);
                synchronized (gatekeeper) {
                    teacher.findCourse(teacherSelectedCourse, dataBase.c).addDiscussionForum(forum);
                }
                pw.println("discussion forum has been added");
                pw.flush();
            }

            /**
             * if a teacher wants to add a discussion forum by importing the file
             * reads the file that the user imports in order to take in the title and creates a discussion
             * forum object, which is added to the course
             */

            if ("teacher is importing forum".equals(logIn)) {
                writeToDb = true;
                String filePath = bfr.readLine();
                synchronized (gatekeeper) {
                    teacher.findCourse(teacherSelectedCourse, dataBase.c).addDiscussionForum(teacher.
                        discussionForumImportFileWithString(filePath));
                }
                pw.println("forum was imported");
                pw.flush();
            }

            /**
             * if a teacher wants to edit a previously made discussion forum
             * takes in the old title of the forum, the new title, and the prompt in order to replace the old
             * discussion forum object with the new information
             * if the forum does not exist, it prompts the user to enter the old title in again
             */

            if ("teacher is editing a discussion forum".equals(logIn)) {
                writeToDb = true;
                int forumEdited = 0;
                do {
                    String oldTitle = bfr.readLine();
                    if (!(teacher.findCourse(teacherSelectedCourse, dataBase.c).findMatchingForum(oldTitle))) {
                        pw.println("forum does not exist");
                        pw.flush();
                    } else {

                        String editTitle = bfr.readLine();

                        String editPrompt = bfr.readLine();
                        synchronized (gatekeeper) {
                            teacher.findCourse(teacherSelectedCourse, dataBase.c).
                                editDiscussionForum(oldTitle, editTitle,
                                editPrompt);
                        }
                        pw.println("discussion forum has been edited");
                        pw.flush();
                        forumEdited++;
                    }
                } while (forumEdited == 0);
            }

            /**
             * if a teacher wants to grade a specifc discussion forum
             * makes an arraylist of all the discussion forums in a course in the text file
             * iterates through the arrayList and displays all the forums
             * stores the forum selected by the teacher in a variable called teacherSelectedForum
             */

            if ("teacher is viewing discussion forums for grading".equals(logIn)) {
                ArrayList<String> existingForumsArray = teacher.findCourse(teacherSelectedCourse, dataBase.c)
                    .viewArrayofForumTitle();
                for (int i = 0; i < existingForumsArray.size(); i++) {
                    pw.println("Forum:" + existingForumsArray.get(i));
                    pw.flush();
                }
                pw.println("done printing existing teacher forums");

                pw.flush();
                //logIn = bfr.readLine();
            }

            /**
             * if a teacher wants to delete a forum
             * prints out a list of the forums within the course to the client
             * the teacher is then allowed to choose which forum they want to delete, and the delete method is called
             * on the forum in order to delete it from the discussion forums array list, which also deletes it from
             * the database
             */

            if ("teacher is viewing discussion forums for deletion".equals(logIn)) {
                ArrayList<String> existingForumsArray = teacher.findCourse(teacherSelectedCourse, dataBase.c)
                    .viewArrayofForumTitle();
                for (int i = 0; i < existingForumsArray.size(); i++) {
                    pw.println("Forum:" + existingForumsArray.get(i));
                    pw.flush();
                }
                pw.println("done printing existing forums");
                pw.flush();
                String isDeletingForum = bfr.readLine();
                if ("teacher is deleting forum".equals(isDeletingForum)) {
                    writeToDb = true;
                    String deleteForum = bfr.readLine();
                    synchronized (gatekeeper) {
                        teacher.findCourse(teacherSelectedCourse, dataBase.c).deleteDiscussionForum(deleteForum);
                    }
                    pw.println("forum is deleted");
                    pw.flush();
                }
            }

            /**
             * if a teacher wants to view discussion forums
             * makes an arraylist of all the discussion forums in a course in the text file
             * iterates through the arrayList and displays all the forums
             * stores the forum selected by the teacher in a variable called teacherSelectedForum
             */

            if ("teacher is viewing discussion forums".equals(logIn)) {
                ArrayList<String> existingForumsArray = teacher.findCourse(teacherSelectedCourse, dataBase.c)
                    .viewArrayofForumTitle();
                for (int i = 0; i < existingForumsArray.size(); i++) {
                    pw.println("Forum:" + existingForumsArray.get(i));
                    pw.flush();
                }
                pw.println("done printing existing teacher forums");
                pw.flush();
                String teacherIsSelectingForum = bfr.readLine();
                if ("teacher is selecting a forum".equals(teacherIsSelectingForum)) {
                    teacherSelectedForum = bfr.readLine();
                }

            }

            /**
             * if a teacher wants to reply to a student post
             * finds the discussion forum object the teacher is viewing in order to get all the posts for that forum
             * and prints all the posts for a specific forum to the client
             */


            if ("teacher is trying to view the posts to reply to".equals(logIn)) {
                curSelectedPosts = teacher.findCourse(teacherSelectedCourse,
                    dataBase.c).findForum(teacherSelectedForum).getPosts();
                String discussionPosts = teacher.findCourse(teacherSelectedCourse,
                    dataBase.c).findForum(teacherSelectedForum).toString();

                String packedPosts = discussionPosts.replaceAll("\n", "||");
                pw.println(packedPosts);
                pw.flush();
                // M logIn = bfr.readLine();
            }

            /**
             * if a user wants to delete their account
             * reads through the UserInfo.txt file to find the username of the account that wants to be deleted and
             * removes it from the text file
             */

            if ("delete account".equals(logIn)) {
                writeToDb = true;
                String deleteOutput = "";
                String username = bfr.readLine();
                synchronized (gatekeeper) {
                    deleteOutput = accountDeletor.deleteAccount(username);
                }
                pw.println(deleteOutput);
                pw.flush();
            }

            /**
             * if a user wants to edit their account
             * reads through the UserInfo.txt file to find the username of the account that wants to be deleted and
             * allows the user to change their password and rewrites the UserInfo.txt file to make the necessary
             * changes
             */

            if ("edit account".equals(logIn)) {
                writeToDb = true;
                String editOutput = "";
                String username = bfr.readLine();
                String newPassword = bfr.readLine();
                synchronized (gatekeeper) {
                    editOutput = accoundEditor.editAccount(username, newPassword);
                }
                pw.println(editOutput);
                pw.flush();
            }
            if (writeToDb) {
                dataBase.writeToFile(dataBase.c);
                writeToDb = false;
            }
        }
        bfr.close();
        pw.close();
        socket.close();
    }
}
