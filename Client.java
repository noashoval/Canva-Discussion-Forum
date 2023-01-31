import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

/**
 * FinalClient class
 *
 * <p>Purdue University -- CS18000 -- Fall 2021</p>
 *
 * @author Aditi Patel, Prachi Sacheti, Donna Prince Purdue CS
 * @version December 12, 2021
 */


public class FinalClient implements Runnable {
    //user variables
    private Socket socket;

    public FinalClient(Socket socket) {
        this.socket = socket;
    }

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 4242);
        SwingUtilities.invokeLater(new FinalClient((socket)));
    }

    public void run() {
        BufferedReader bfr;
        PrintWriter pw;
        try {
            bfr = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            return;
        }
        try {
            pw = new PrintWriter(socket.getOutputStream());
        } catch (IOException e) {
            return;
        }
        mainFrame(bfr, pw);
    }

    /**
     * main frame
     * the frame that welcomes and asks user if they are a new or existing user
     * goes to the newUserFrame or ExistingUserFrame accordingly
     */

    public static void mainFrame(BufferedReader bfr, PrintWriter pw) {
        JLabel welcomeUser;
        JButton newUser;
        JButton existingUser;
        JFrame frame = new JFrame();
        frame.setTitle("Discussion Forum");
        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        };
        Container content = frame.getContentPane();
        content.setLayout(new BorderLayout());

        welcomeUser = new JLabel("Welcome to the discussion forum! Please select whatever applies.");
        newUser = new JButton("New User");
        newUser.addActionListener(actionListener);
        existingUser = new JButton("Existing User");
        existingUser.addActionListener(actionListener);

        newUser.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                NewUserFrame(bfr, pw);
            }
        });

        existingUser.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                ExistingUserFrame(bfr, pw);
            }
        });

        JPanel panel = new JPanel();
        panel.setBorder(new EmptyBorder(2, 3, 2, 3));
        JPanel layout = new JPanel(new GridBagLayout());
        layout.setBorder(new EmptyBorder(5, 5, 5, 5));
        JPanel buttonPanel = new JPanel(new GridLayout(6, 1, 10, 5));

        panel.add(welcomeUser);
        buttonPanel.add(newUser);
        buttonPanel.add(existingUser);
        content.add(panel, BorderLayout.NORTH);
        content.add(buttonPanel, BorderLayout.CENTER);
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }

    /**
     * allows the client to make a new user account. send the username, password and user type to server
     * goes to the TeacherMainFrame or StudentMainFrame according to what they choose from the dropdown
     */

    public static void NewUserFrame(BufferedReader bfr, PrintWriter pw) {
        JFrame frame = new JFrame();
        JPanel userTypePanel = new JPanel();
        JPanel logInPanel = new JPanel();
        JComboBox typeOfUser;
        JLabel newOrExistingUser;
        JLabel userTypeLabel;
        JLabel password;
        JLabel username;
        JTextField userUsername;
        JTextField userPassword;
        JButton newUserLoginButton;
        JButton backButton;
        JPanel bottomPanel = new JPanel();
        Container content = frame.getContentPane();
        content.setLayout(new BorderLayout());

        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        };

        String[] choices = {"Teacher", "Student"};
        typeOfUser = new JComboBox<String>(choices);
        newOrExistingUser = new JLabel("Please enter your details");
        userTypeLabel = new JLabel("Please select what applies.");
        password = new JLabel("Password: ");
        userPassword = new JTextField(20);
        userUsername = new JTextField(20);
        username = new JLabel("Username: ");
        newUserLoginButton = new JButton("Log In");
        backButton = new JButton("Back");
        //newUserLoginButton.addActionListener((ActionListener) this);

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                mainFrame(bfr, pw);
            }
        });

        newUserLoginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String doesUsernameExist = "";
                String teacherOrStudent = "";
                if (userUsername.getText().equals("") || userPassword.getText().equals("")) {
                    JOptionPane.showMessageDialog(null, "The username and/or password " +
                        "cannot be empty", "Discussion Forum", JOptionPane.ERROR_MESSAGE);
                } else {
                    pw.println("new user is logging in");
                    pw.flush();
                    //oos.writeObject("New user is logging in");
                    //oos.flush();
                    if (typeOfUser.getSelectedIndex() == 0) {
                        pw.println("new teacher");
                        pw.flush();
                        pw.println(userUsername.getText());
                        pw.flush();
                        pw.println(userPassword.getText());
                        pw.flush();
                    } else if (typeOfUser.getSelectedIndex() == 1) {
                        pw.println("new student");
                        pw.flush();
                        pw.println(userUsername.getText());
                        pw.flush();
                        pw.println(userPassword.getText());
                        pw.flush();
                    }

                    try {
                        doesUsernameExist = bfr.readLine();
                        if ("username exists".equals(doesUsernameExist)) {
                            JOptionPane.showMessageDialog(null, "The username already exists."
                                    + "Please enter another username.", "Discussion Forum",
                                JOptionPane.ERROR_MESSAGE);
                        } else if ("account created".equals(doesUsernameExist)) {
                            JOptionPane.showMessageDialog(null, "Account created! Logging in",
                                "Discussion Forum", JOptionPane.INFORMATION_MESSAGE);
                            frame.dispose();
                            teacherOrStudent = bfr.readLine();

                            if ("teacher".equals(teacherOrStudent)) {
                                TeacherGUI(bfr, pw);
                            } else if ("student".equals(teacherOrStudent)) {
                                StudentGUI(bfr, pw);
                            }
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        JPanel panel = new JPanel();
        panel.setBorder(new EmptyBorder(2, 3, 2, 3));
        JPanel layout = new JPanel(new GridBagLayout());
        layout.setBorder(new EmptyBorder(5, 5, 5, 5));

        panel.add(newOrExistingUser);
        userTypePanel.add(userTypeLabel);
        userTypePanel.add(typeOfUser);
        logInPanel.add(username);
        logInPanel.add(userUsername);
        logInPanel.add(password);
        logInPanel.add(userPassword);
        bottomPanel.add(backButton);
        bottomPanel.add(newUserLoginButton);
        content.add(panel, BorderLayout.NORTH);
        content.add(userTypePanel, BorderLayout.LINE_START);
        content.add(logInPanel, BorderLayout.CENTER);
        content.add(bottomPanel, BorderLayout.SOUTH);


        frame.setSize(600, 200);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);

    }

    /**
     * allows an existing user to enter the username and password
     * send the username and password to the server
     */

    public static void ExistingUserFrame(BufferedReader bfr, PrintWriter pw) {
        JFrame frame = new JFrame();
        JLabel password;
        JLabel username;
        JTextField userUsername;
        JTextField userPassword;
        JButton existingUserLoginButton;
        JButton backButton;
        JPanel logInPanel = new JPanel();
        JPanel bottomPanel = new JPanel();
        Container content = frame.getContentPane();
        content.setLayout(new BorderLayout());
        JLabel newOrExistingUser;

        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        };

        newOrExistingUser = new JLabel("Please enter your details");
        password = new JLabel("Password: ");
        userPassword = new JTextField(20);
        userUsername = new JTextField(20);
        username = new JLabel("Username: ");
        existingUserLoginButton = new JButton("Log In");
        backButton = new JButton("Back");
        //existingUserLoginButton.addActionListener(actionListener);

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                mainFrame(bfr, pw);
            }
        });


        existingUserLoginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String existingTeacherOrStudent = "";
                if ("".equals(userUsername.getText()) || "".equals(userPassword.getText())) {
                    JOptionPane.showMessageDialog(null, "The username and/or password cannot "
                        + "be empty", "Discussion Forum", JOptionPane.ERROR_MESSAGE);
                } else {
                    pw.println("existing user is logging in");
                    pw.flush();
                    pw.println(userUsername.getText());
                    pw.flush();
                    pw.println(userPassword.getText());
                    pw.flush();
                    String canUserLogIn = "";
                    try {
                        canUserLogIn = bfr.readLine();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    if ("user cant login".equals(canUserLogIn)) {
                        JOptionPane.showMessageDialog(null, "Incorrect info! Try again!",
                            "Discussion Forum", JOptionPane.ERROR_MESSAGE);
                    } else if ("correct info. logging in!".equals(canUserLogIn)) {
                        JOptionPane.showMessageDialog(null, "Logging in!",
                            "Discussion Forum", JOptionPane.INFORMATION_MESSAGE);
                        try {
                            existingTeacherOrStudent = bfr.readLine();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                        if ("teacher is logging in".equals(existingTeacherOrStudent)) {
                            frame.dispose();
                            TeacherGUI(bfr, pw);
                        } else if ("student is logging in".equals(existingTeacherOrStudent)) {
                            frame.dispose();
                            StudentGUI(bfr, pw);
                        }
                    }
                }
            }
        });


        JPanel panel = new JPanel();
        panel.setBorder(new EmptyBorder(2, 3, 2, 3));
        JPanel layout = new JPanel(new GridBagLayout());
        layout.setBorder(new EmptyBorder(5, 5, 5, 5));

        panel.add(newOrExistingUser);
        logInPanel.add(username);
        logInPanel.add(userUsername);
        logInPanel.add(password);
        logInPanel.add(userPassword);
        bottomPanel.add(backButton);
        bottomPanel.add(existingUserLoginButton);
        content.add(panel, BorderLayout.NORTH);
        content.add(logInPanel, BorderLayout.CENTER);
        content.add(bottomPanel, BorderLayout.SOUTH);


        frame.setSize(400, 200);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);

    }


    //--------- start of student section ---------//

    /**
     * This GUI Is the first Panel to appear after a user has created a student account
     * GUI contains five buttons allowing users to view existing courses, view grades,
     * edit their account, delete account, and logout
     */


    public static void StudentGUI(BufferedReader bfr, PrintWriter pw) {
        //GUI for Student Initial Menu w/ 5 buttons
        JFrame frame = new JFrame();
        JLabel welcomeStudent;
        JButton viewExistingCourses;
        JButton viewGrades;
        JButton editAccount;
        JButton deleteAccount;
        JButton logout;


        frame.setTitle("Discussion Forum");
        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        };

        Container content = frame.getContentPane();
        content.setLayout(new BorderLayout());

        welcomeStudent = new JLabel("Welcome Student! What would you like to do?");

        viewExistingCourses = new JButton("View Existing Courses");
        viewExistingCourses.addActionListener(actionListener);

        viewGrades = new JButton("View Grades");
        viewGrades.addActionListener(actionListener);

        editAccount = new JButton("Edit Account");
        editAccount.addActionListener(actionListener);

        deleteAccount = new JButton("Delete Account");
        deleteAccount.addActionListener(actionListener);

        logout = new JButton("Logout");
        logout.addActionListener(actionListener);

        viewExistingCourses.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                ExistingCoursesStudentFrame(bfr, pw);
            }
        });

        viewGrades.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                ViewGradesFrame(bfr, pw);
            }
        });

        editAccount.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                EditAccountFrame(bfr, pw);
            }
        });

        deleteAccount.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                DeleteAccountFrame(bfr, pw);
            }
        });

        logout.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                JOptionPane.showMessageDialog(null, "Thank you for using the Discussion Forum Maker!",
                    "Discussion Forum", JOptionPane.INFORMATION_MESSAGE);

                //go back to welcome screen
            }
        });

        JPanel panel = new JPanel();
        panel.setBorder(new EmptyBorder(2, 3, 2, 3));
        JPanel layout = new JPanel(new GridBagLayout());
        layout.setBorder(new EmptyBorder(5, 5, 5, 5));
        JPanel buttonPanel = new JPanel(new GridLayout(6, 1, 10, 5));

        panel.add(welcomeStudent);
        content.add(panel, BorderLayout.NORTH);

        buttonPanel.add(viewExistingCourses);
        buttonPanel.add(viewGrades);
        buttonPanel.add(editAccount);
        buttonPanel.add(deleteAccount);
        buttonPanel.add(logout);
        content.add(buttonPanel, BorderLayout.CENTER);


        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);

    }

    /**
     * This GUI allows Students to view existing courses in a dropdown menu
     * if they click back they will be brought to go back to the Student GUI
     * if they click enter they will be brought to choose a discussion forum
     */

    public static void ExistingCoursesStudentFrame(BufferedReader bfr, PrintWriter pw) {
        JComboBox listOfCourses = new JComboBox<>();
        JLabel studentPicksCourse;
        JButton backButton;
        JButton enterButton;
        JButton showCourses;
        ArrayList<String> choices = new ArrayList<>();
        JFrame frame = new JFrame();
        frame.setTitle("View Courses");
        JPanel headerPanel = new JPanel();
        JPanel coursesPanel = new JPanel();
        JPanel buttonPanel = new JPanel();
        Container content = frame.getContentPane();
        content.setLayout(new BorderLayout());

        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        };
        studentPicksCourse = new JLabel("Choose a course to view!  (click view courses in order to generate" +
            "courses)");
        showCourses = new JButton("View Courses");
        showCourses.addActionListener(actionListener);

        backButton = new JButton("Back");
        backButton.addActionListener(actionListener);

        enterButton = new JButton("Enter");
        enterButton.addActionListener(actionListener);

        showCourses.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String[] courseNameJCombo;
                String actualCourseName;

                pw.println("student is viewing existing courses");
                pw.flush();
                String courseNames = "";

                try {
                    courseNames = bfr.readLine();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                try {
                    while (!courseNames.equals("done printing existing courses for student")) {
                        if (courseNames.contains("Course:")) {
                            courseNameJCombo = courseNames.split(":");
                            actualCourseName = courseNameJCombo[1];
                            actualCourseName = actualCourseName.substring(6);
                            choices.add(actualCourseName);
                        }
                        courseNames = bfr.readLine();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                for (String choice : choices) {
                    listOfCourses.addItem(choice);
                }
            }
        });

        //work on back button
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                StudentGUI(bfr, pw);
            }
        });


        enterButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                pw.println("student is selecting a course");
                pw.flush();
                String selectedClass = listOfCourses.getSelectedItem().toString();
                pw.println(selectedClass);
                pw.flush();
                ViewExistingForumsFrame(bfr, pw);
            }
        });


        headerPanel.add(studentPicksCourse);
        content.add(headerPanel, BorderLayout.NORTH);

        coursesPanel.add(listOfCourses);
        content.add(coursesPanel, BorderLayout.CENTER);

        buttonPanel.add(showCourses);
        buttonPanel.add(backButton);
        buttonPanel.add(enterButton);
        content.add(buttonPanel, BorderLayout.SOUTH);


        frame.setSize(500, 200);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }

    /**
     * This GUI allows students to view existing forums once they have chosen a specific course
     * students can click back to go back to the courses menu
     * students can click return to get to the forum menu
     */

    public static void ViewExistingForumsFrame(BufferedReader bfr, PrintWriter pw) {
        JComboBox listOfForums = new JComboBox();
        JLabel studentPicksForum;
        JButton backButton;
        JButton viewForums;
        JButton enterButton;
        ArrayList<String> choices = new ArrayList<>();
        JFrame frame = new JFrame();
        frame.setTitle("View Courses");
        JPanel headerPanel = new JPanel();
        JPanel coursesPanel = new JPanel();
        JPanel buttonPanel = new JPanel();
        Container content = frame.getContentPane();
        content.setLayout(new BorderLayout());

        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        };

        studentPicksForum = new JLabel("Click view forums in order to view a forum");

        backButton = new JButton("Back");
        backButton.addActionListener(actionListener);

        viewForums = new JButton("View Forums");
        viewForums.addActionListener(actionListener);

        enterButton = new JButton("Enter");
        enterButton.addActionListener(actionListener);

        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                ExistingCoursesStudentFrame(bfr, pw);
            }
        });

        viewForums.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String[] discussionForumJCombo;
                String actualForumName;

                pw.println("student is viewing discussion forums");
                pw.flush();
                String forumName = "";

                try {
                    forumName = bfr.readLine();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }


                try {
                    while (!forumName.equals("done printing existing student forums")) {
                        if (forumName.contains("Forum:")) {
                            discussionForumJCombo = forumName.split(":");
                            actualForumName = discussionForumJCombo[1];

                            choices.add(actualForumName);
                        }
                        forumName = bfr.readLine();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }


                for (String choice : choices) {
                    listOfForums.addItem(choice);
                }

            }
        });

        enterButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pw.println("student is selecting a forum");
                pw.flush();
                String selectedForum = listOfForums.getSelectedItem().toString();
                pw.println(selectedForum);
                pw.flush();
                frame.dispose();
                ForumMenuStudentFrame(bfr, pw);

            }
        });

        headerPanel.add(studentPicksForum);
        content.add(headerPanel, BorderLayout.NORTH);

        coursesPanel.add(listOfForums);
        content.add(coursesPanel, BorderLayout.CENTER);

        buttonPanel.add(backButton);
        buttonPanel.add(viewForums);
        buttonPanel.add(enterButton);
        content.add(buttonPanel, BorderLayout.SOUTH);


        frame.setSize(400, 200);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }

    /**
     * This GUI allows students to either post, reply to another student, or vote on a reply
     * to a specific discussion forum they chose
     * by pressing back they are brought back to choose a discussion forum
     * by pressing enter they are brought to corresponding panel
     */

    public static void ForumMenuStudentFrame(BufferedReader bfr, PrintWriter pw) {
        JButton addPost;
        JButton replyToStudent;
        JButton voteOnReply;
        JButton backButton;
        JLabel header;
        JFrame frame = new JFrame();
        frame.setTitle("Discussion Forum");
        JPanel headerPanel = new JPanel();
        JPanel coursesPanel = new JPanel();
        JPanel buttonPanel = new JPanel();
        Container content = frame.getContentPane();
        content.setLayout(new BorderLayout());

        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        };

        addPost = new JButton("Add Post");
        addPost.addActionListener(actionListener);

        replyToStudent = new JButton("Reply to Another Student");
        replyToStudent.addActionListener(actionListener);

        voteOnReply = new JButton("Vote on a Reply");
        voteOnReply.addActionListener(actionListener);

        backButton = new JButton("Back");
        backButton.addActionListener(actionListener);

        header = new JLabel("What would you like to do?");

        addPost.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                AskStudentImportFileFrame(bfr, pw);
            }
        });

        replyToStudent.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                viewPostsFrame(bfr, pw);
            }
        });

        voteOnReply.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                ViewVotesFrame(bfr, pw);
            }
        });

        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                ViewExistingForumsFrame(bfr, pw);
            }
        });

        headerPanel.add(header);
        content.add(headerPanel, BorderLayout.NORTH);

        coursesPanel.add(addPost);
        coursesPanel.add(replyToStudent);
        coursesPanel.add(voteOnReply);
        content.add(coursesPanel, BorderLayout.CENTER);

        buttonPanel.add(backButton);
        content.add(buttonPanel, BorderLayout.SOUTH);

        frame.setSize(400, 200);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }

    /**
     * This GUI appears after a student user has chosen to add a post in Forum Menu Student Frame
     * GUI contains a text field for student to input their post's topic and a text area
     * to input their post's prompt, once done they hit enter a panel pops up with a message saying
     * "Your post has been added!" and then the user is brought back to Forum Menu Student Frame
     */

    public static void AskStudentImportFileFrame(BufferedReader bfr, PrintWriter pw) {
        JLabel importQuestion;
        JButton yesButton;
        JButton noButton;
        JFrame frame = new JFrame();
        frame.setTitle("Discussion Forum");
        JPanel headerPanel = new JPanel();
        JPanel buttonPanel = new JPanel();
        Container content = frame.getContentPane();
        content.setLayout(new BorderLayout());

        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        };

        importQuestion = new JLabel("Would you like to import a discussion post?");

        yesButton = new JButton("Yes");
        yesButton.addActionListener(actionListener);

        noButton = new JButton("No");
        noButton.addActionListener(actionListener);

        yesButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                ImportStudentPostFrame(bfr, pw);

            }
        });

        noButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                AddPostFrame(bfr, pw);
            }
        });

        headerPanel.add(importQuestion);
        content.add(headerPanel, BorderLayout.NORTH);

        buttonPanel.add(yesButton);
        buttonPanel.add(noButton);
        content.add(buttonPanel, BorderLayout.CENTER);

        frame.setSize(400, 200);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }

    public static void ImportStudentPostFrame(BufferedReader bfr, PrintWriter pw) {
        JLabel enterFilePathPrompt;
        JLabel filePathPrompt;
        JTextField filePath;
        JButton enterButton;
        JFrame frame = new JFrame();
        frame.setTitle("Discussion Forum");
        JPanel headerPanel = new JPanel();
        JPanel buttonPanel = new JPanel();
        JPanel filePathPanel = new JPanel();
        Container content = frame.getContentPane();
        content.setLayout(new BorderLayout());

        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        };

        enterFilePathPrompt = new JLabel("Enter the filepath of the file you want to import.");
        filePathPrompt = new JLabel("File Path:");
        filePath = new JTextField(20);

        enterButton = new JButton("Enter");
        enterButton.addActionListener(actionListener);

        enterButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (filePath.getText().equals("")) {
                    JOptionPane.showMessageDialog(null, "The filepath cannot be blank",
                        "Discussion Forum", JOptionPane.ERROR_MESSAGE);
                } else {
                    pw.println("student is importing post");
                    pw.flush();

                    String filePathString = filePath.getText();
                    pw.println(filePathString);
                    pw.flush();

                    String wasImported = null;
                    try {
                        wasImported = bfr.readLine();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    if ("post was imported".equals(wasImported)) {
                        frame.dispose();
                        JOptionPane.showMessageDialog(null, "Your post has been added!",
                            "Discussion Forum", JOptionPane.INFORMATION_MESSAGE);
                        CoursesMenuFrame(bfr, pw);
                    } else {
                        JOptionPane.showMessageDialog(null, "There was an error importing" +
                            " the file", "Discussion Forum", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        headerPanel.add(enterFilePathPrompt);
        content.add(headerPanel, BorderLayout.NORTH);

        filePathPanel.add(filePathPrompt);
        filePathPanel.add(filePath);
        content.add(filePathPanel, BorderLayout.CENTER);

        buttonPanel.add(enterButton);
        content.add(buttonPanel, BorderLayout.SOUTH);

        frame.setSize(400, 200);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }

    public static void AddPostFrame(BufferedReader bfr, PrintWriter pw) {
        JLabel addDiscussionPost;
        JLabel messagePrompt;
        JLabel spaces;
        JTextArea message;
        JButton enterButton;
        JFrame frame = new JFrame();
        frame.setTitle("Discussion Post");
        JPanel headerPanel = new JPanel();
        JPanel textFieldPanel = new JPanel();
        JPanel buttonPanel = new JPanel();
        Container content = frame.getContentPane();
        content.setLayout(new BorderLayout());

        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        };
        addDiscussionPost = new JLabel("Enter information to add a discussion post.");
        messagePrompt = new JLabel("Enter your response:");
        spaces = new JLabel("                      ");
        message = new JTextArea(2, 20);
        message.setLineWrap(true);
        message.setWrapStyleWord(true);

        enterButton = new JButton("Enter");
        enterButton.addActionListener(actionListener);

        enterButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (message.getText().equals("")) {
                    JOptionPane.showMessageDialog(null, "The response cannot be blank",
                        "Discussion Forum", JOptionPane.ERROR_MESSAGE);
                } else {
                    pw.println("student is adding a post");
                    pw.flush();
                    String addingPost = message.getText();
                    pw.println(addingPost);
                    pw.flush();


                    String isPostAdded = "";
                    try {

                        isPostAdded = bfr.readLine();

                    } catch (IOException exc) {
                        exc.printStackTrace();
                    } catch (Exception exc) {
                        exc.printStackTrace();
                    }

                    if (isPostAdded.equals("post was added")) {
                        frame.dispose();
                        JOptionPane.showMessageDialog(null, "Your post was added!",
                            "Discussion Forum", JOptionPane.INFORMATION_MESSAGE);
                        StudentGUI(bfr, pw);
                    }
                }

            }
        });

        headerPanel.add(addDiscussionPost);
        content.add(headerPanel, BorderLayout.NORTH);

        textFieldPanel.add(messagePrompt);
        textFieldPanel.add(message);
        //textFieldPanel.add(spaces);
        content.add(textFieldPanel, BorderLayout.CENTER);

        buttonPanel.add(enterButton);
        content.add(buttonPanel, BorderLayout.SOUTH);

        frame.setSize(500, 200);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }

    /**
     * This GUI allows students to choose if they want to add a vote to a reply
     * it features a yes and no button
     */

    public static void ViewVotesFrame(BufferedReader bfr, PrintWriter pw) {
        JLabel addVoteToPost;
        JButton yesButton;
        JButton noButton;
        JFrame frame = new JFrame();
        frame.setTitle("View Grades");
        JPanel headerPanel = new JPanel();
        JPanel coursesPanel = new JPanel();
        JPanel buttonPanel = new JPanel();
        Container content = frame.getContentPane();
        content.setLayout(new BorderLayout());

        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        };

        addVoteToPost = new JLabel("Would you like to add a vote to this Post?");

        yesButton = new JButton("Yes");
        yesButton.addActionListener(actionListener);

        noButton = new JButton("No");
        noButton.addActionListener(actionListener);

        yesButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                ForumMenuStudentFrame(bfr, pw);

            }
        });

        noButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                ForumMenuStudentFrame(bfr, pw);
            }
        });
    }

    /**
     * This GUI allows students to view all grades
     * if they click back they will be brought to Student GUI
     */

    public static void ViewGradesFrame(BufferedReader bfr, PrintWriter pw) {
        JLabel viewGrades;
        JButton backButton;
        JLabel header;
        JFrame frame = new JFrame();
        frame.setTitle("View Grades");
        JPanel headerPanel = new JPanel();
        JPanel coursesPanel = new JPanel();
        JPanel buttonPanel = new JPanel();
        Container content = frame.getContentPane();
        content.setLayout(new BorderLayout());

        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        };

        viewGrades = new JLabel("View Grades and use back button to go back to initial menu");
        backButton = new JButton("back");
        backButton.addActionListener(actionListener);

        header = new JLabel("What would you like to do?");

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                StudentGUI(bfr, pw);
            }
        });

        headerPanel.add(header);
        content.add(headerPanel, BorderLayout.NORTH);

        coursesPanel.add(viewGrades);
        content.add(coursesPanel, BorderLayout.CENTER);

        buttonPanel.add(backButton);
        content.add(buttonPanel, BorderLayout.SOUTH);

        frame.setSize(400, 200);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }

    /**
     * This GUI allows Students to edit their account by changing their password
     * if the text field is empty an error message appears on a new pop up panel
     */

    public static void EditAccountFrame(BufferedReader bfr, PrintWriter pw) {
        JLabel newPasswordTitle;
        JLabel confirmUsernamePrompt;
        JLabel newPasswordPrompt;
        JTextField username;
        JTextField password;
        JButton enterButton;
        JLabel spaces;
        JFrame frame = new JFrame();
        frame.setTitle("Discussion Forum");
        JPanel headerPanel = new JPanel();
        JPanel textFieldPanel = new JPanel();
        JPanel buttonPanel = new JPanel();
        Container content = frame.getContentPane();
        content.setLayout(new BorderLayout());

        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                TeacherGUI(bfr, pw);
            }
        };

        newPasswordTitle = new JLabel("Enter a new password.");
        confirmUsernamePrompt = new JLabel("Confirm Username:");
        newPasswordPrompt = new JLabel("New Password:");
        username = new JTextField(15);
        password = new JTextField(15);
        spaces = new JLabel("                   ");

        enterButton = new JButton("Enter");
        enterButton.addActionListener(actionListener);


        enterButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //frame.dispose();
                if (username.getText().equals("") || password.getText().equals("")) {
                    JOptionPane.showMessageDialog(null, "The username and/or password " +
                        "cannot be blank/incorrect", "Discussion Forum", JOptionPane.ERROR_MESSAGE);
                } else {
                    pw.println("edit account");
                    pw.println((username.getText()));

                    pw.println((password.getText()));

                    pw.flush();
                    String editResponse = null;
                    try {
                        editResponse = bfr.readLine();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                    if (editResponse.equals("Error, no such user exists! Try again")) {
                        JOptionPane.showMessageDialog(null, editResponse,
                            "Discussion Forum", JOptionPane.ERROR_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, "Your account has been edited!",
                            "Discussion Forum", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }

        });

        headerPanel.add(newPasswordTitle);
        content.add(headerPanel, BorderLayout.NORTH);


        textFieldPanel.add(confirmUsernamePrompt);
        textFieldPanel.add(username);
        textFieldPanel.add(spaces);
        textFieldPanel.add(newPasswordPrompt);
        textFieldPanel.add(password);
        content.add(textFieldPanel, BorderLayout.CENTER);

        buttonPanel.add(enterButton);
        content.add(buttonPanel, BorderLayout.SOUTH);

        frame.setSize(500, 200);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }

    /**
     * This GUI allows students to delete their account by confirming their username
     * if the text field is empty when the student clicks enter, an error message will appear
     */

    public static void DeleteAccountFrame(BufferedReader bfr, PrintWriter pw) {


        JLabel deleteAccountPrompt;
        JLabel confirmUsernamePrompt;

        JButton deleteButton;
        JFrame frame = new JFrame();
        frame.setTitle("Discussion Forum");
        JPanel headerPanel = new JPanel();
        JPanel textFieldPanel = new JPanel();
        JPanel buttonPanel = new JPanel();
        Container content = frame.getContentPane();
        content.setLayout(new BorderLayout());

        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        };


        deleteAccountPrompt = new JLabel("In order to delete your account, confirm your username.");
        confirmUsernamePrompt = new JLabel("Confirm username:");

        JTextField username = new JTextField(15);

        deleteButton = new JButton("Delete");
        deleteButton.addActionListener(actionListener);


        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                if (username.getText().equals("")) {
                    JOptionPane.showMessageDialog(null, "The username cannot be blank",
                        "Discussion Forum", JOptionPane.ERROR_MESSAGE);
                } else {
                    pw.println("delete account");
                    //pw.flush();
                    pw.println((username.getText()));
                    pw.flush();
                    String deleteResponse = null;
                    try {
                        deleteResponse = bfr.readLine();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    if ("Error, no such user exists".equals(deleteResponse)) {
                        JOptionPane.showMessageDialog(null, deleteResponse,
                            "Discussion Forum", JOptionPane.ERROR_MESSAGE);

                    } else {
                        frame.dispose();
                        JOptionPane.showMessageDialog(null, "Your account has been deleted!",
                            "Discussion Forum", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
                }

            }
        });

        headerPanel.add(deleteAccountPrompt);
        content.add(headerPanel, BorderLayout.NORTH);


        textFieldPanel.add(confirmUsernamePrompt);
        textFieldPanel.add(username);
        content.add(textFieldPanel, BorderLayout.CENTER);

        buttonPanel.add(deleteButton);
        content.add(buttonPanel, BorderLayout.SOUTH);

        frame.setSize(500, 200);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);


    }

    //--------- start of teacher section ---------//

    /**
     * opens to the main teacher menu, where the teacher has the option to view existing courses, add a new course,
     * edit their account, delete their account, or logout
     */

    public static void TeacherGUI(BufferedReader bfr, PrintWriter pw) {
        JLabel welcomeTeacher;
        JButton viewExistingCourses;
        JButton addNewCourse;
        JButton editAccount;
        JButton deleteAccount;
        JButton logout;
        JComboBox listOfCourses;
        JLabel teacherPicksCourse;
        JButton backButton;
        JFrame frame = new JFrame();
        frame.setTitle("Discussion Forum");
        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        };

        Container content = frame.getContentPane();
        content.setLayout(new BorderLayout());

        welcomeTeacher = new JLabel("Welcome Teacher! What would you like to do?");

        viewExistingCourses = new JButton("View Existing Courses");
        viewExistingCourses.addActionListener(actionListener);

        addNewCourse = new JButton("Add New Course");
        addNewCourse.addActionListener(actionListener);

        editAccount = new JButton("Edit Account");
        editAccount.addActionListener(actionListener);

        deleteAccount = new JButton("Delete Account");
        deleteAccount.addActionListener(actionListener);

        logout = new JButton("Logout");
        logout.addActionListener(actionListener);


        viewExistingCourses.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                try {
                    ExistingCoursesFrame(bfr, pw);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        addNewCourse.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                AddNewCourseFrame(bfr, pw);
            }
        });

        editAccount.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                EditAccountFrame(bfr, pw);
            }
        });

        deleteAccount.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                DeleteAccountFrame(bfr, pw);
            }
        });

        logout.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                JOptionPane.showMessageDialog(null, "Thank you for using the Discussion Forum" +
                        " Maker!",
                    "Discussion Forum", JOptionPane.INFORMATION_MESSAGE);

                //go back to welcome screen
            }
        });


        JPanel panel = new JPanel();
        panel.setBorder(new EmptyBorder(2, 3, 2, 3));
        JPanel layout = new JPanel(new GridBagLayout());
        layout.setBorder(new EmptyBorder(5, 5, 5, 5));
        JPanel buttonPanel = new JPanel(new GridLayout(6, 1, 10, 5));

        panel.add(welcomeTeacher);
        content.add(panel, BorderLayout.NORTH);

        buttonPanel.add(viewExistingCourses);
        buttonPanel.add(addNewCourse);
        buttonPanel.add(editAccount);
        buttonPanel.add(deleteAccount);
        buttonPanel.add(logout);
        content.add(buttonPanel, BorderLayout.CENTER);


        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }

    /**
     * Allows teacher to view a drop down menu of the existing courses in the backup.txt file after they click the show
     * courses button. After they click enter, the teacher is sent to the CoursesMenuFrame.
     */

    public static void ExistingCoursesFrame(BufferedReader bfr, PrintWriter pw) throws IOException {
        JComboBox listOfCourses = new JComboBox();
        JLabel teacherPicksCourse;
        JButton backButton;
        JButton enterButton;
        JButton showCourses;
        ArrayList<String> choices = new ArrayList<>();
        JFrame frame = new JFrame();
        frame.setTitle("Discussion Forum");
        JPanel headerPanel = new JPanel();
        JPanel coursesPanel = new JPanel();
        JPanel buttonPanel = new JPanel();
        Container content = frame.getContentPane();
        content.setLayout(new BorderLayout());

        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        };

        teacherPicksCourse = new JLabel("Choose a Course to View! (click view courses in order to generate " +
            "courses)");

        showCourses = new JButton("View Courses");
        showCourses.addActionListener(actionListener);

        backButton = new JButton("Back");
        backButton.addActionListener(actionListener);

        enterButton = new JButton("Enter");
        enterButton.addActionListener(actionListener);

        showCourses.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String[] courseNameJCombo;
                String actualCourseName;

                pw.println("teacher is viewing existing courses");
                pw.flush();
                String courseNames = "";

                try {
                    courseNames = bfr.readLine();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                try {
                    while (!courseNames.equals("done printing existing courses")) {
                        if (courseNames.contains("Course:")) {
                            courseNameJCombo = courseNames.split(":");
                            actualCourseName = courseNameJCombo[1];
                            actualCourseName = actualCourseName.substring(6);
                            choices.add(actualCourseName);
                        }
                        courseNames = bfr.readLine();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                for (String choice : choices) {
                    listOfCourses.addItem(choice);
                }
            }

        });

        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                TeacherGUI(bfr, pw);
            }
        });

        enterButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                pw.println("teacher is selecting a course");
                pw.flush();
                String selectedClass = listOfCourses.getSelectedItem().toString();
                pw.println(selectedClass);
                pw.flush();
                CoursesMenuFrame(bfr, pw);
            }
        });

        headerPanel.add(teacherPicksCourse);
        content.add(headerPanel, BorderLayout.NORTH);

        coursesPanel.add(listOfCourses);
        content.add(coursesPanel, BorderLayout.CENTER);

        buttonPanel.add(showCourses);
        buttonPanel.add(backButton);
        buttonPanel.add(enterButton);
        content.add(buttonPanel, BorderLayout.SOUTH);


        frame.setSize(500, 200);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }

    /**
     * Allows the teacher to view the menu for a specific course. The menu includes the option to add a discussion
     * forum (either manually or by importing a file), edit a discussion forum, delete a discussion forum,
     * view all forums, grade, and a back button that goes to the main teacher menu screen.
     */

    public static void CoursesMenuFrame(BufferedReader bfr, PrintWriter pw) {
        JButton addDiscussionForum;
        JButton editDiscussionForum;
        JButton deleteDiscussionForum;
        JButton viewAllForums;
        JButton gradeButton;
        JButton backButton;
        JLabel header;
        JFrame frame = new JFrame();
        frame.setTitle("Discussion Forum");
        JPanel headerPanel = new JPanel();
        JPanel coursesPanel = new JPanel();
        JPanel buttonPanel = new JPanel();
        Container content = frame.getContentPane();
        content.setLayout(new BorderLayout());

        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        };

        addDiscussionForum = new JButton("Add Discussion Forum");
        addDiscussionForum.addActionListener(actionListener);

        editDiscussionForum = new JButton("Edit Discussion Forum");
        editDiscussionForum.addActionListener(actionListener);

        deleteDiscussionForum = new JButton("Delete Discussion Forum");
        deleteDiscussionForum.addActionListener(actionListener);

        viewAllForums = new JButton("View All Forums");
        viewAllForums.addActionListener(actionListener);

        gradeButton = new JButton("Grade a Forum");
        gradeButton.addActionListener(actionListener);

        backButton = new JButton("Back");
        backButton.addActionListener(actionListener);

        header = new JLabel("What would you like to do?");

        addDiscussionForum.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                AskImportFileFrame(bfr, pw);
            }
        });

        editDiscussionForum.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                EditDiscussionForumFrame(bfr, pw);
            }
        });

        deleteDiscussionForum.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                DeleteDiscussionForumFrame(bfr, pw);
            }
        });

        viewAllForums.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                ViewAllForumsFrame(bfr, pw);
            }
        });

        gradeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                GradeForumFrame(bfr, pw);
            }
        });

        //work on back button
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                TeacherGUI(bfr, pw);
            }
        });

        headerPanel.add(header);
        content.add(headerPanel, BorderLayout.NORTH);

        coursesPanel.add(addDiscussionForum);
        coursesPanel.add(editDiscussionForum);
        coursesPanel.add(deleteDiscussionForum);
        coursesPanel.add(viewAllForums);
        coursesPanel.add(gradeButton);
        content.add(coursesPanel, BorderLayout.CENTER);

        buttonPanel.add(backButton);
        content.add(buttonPanel, BorderLayout.SOUTH);

        frame.setSize(400, 200);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }

    /**
     * Asks the teacher or student if they would like to import a discussion forum file from IntelliJ
     */

    public static void AskImportFileFrame(BufferedReader bfr, PrintWriter pw) {
        JLabel importQuestion;
        JButton yesButton;
        JButton noButton;
        JFrame frame = new JFrame();
        frame.setTitle("Discussion Forum");
        JPanel headerPanel = new JPanel();
        JPanel buttonPanel = new JPanel();
        Container content = frame.getContentPane();
        content.setLayout(new BorderLayout());

        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        };

        importQuestion = new JLabel("Would you like to import a discussion forum?");

        yesButton = new JButton("Yes");
        yesButton.addActionListener(actionListener);

        noButton = new JButton("No");
        noButton.addActionListener(actionListener);

        yesButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                ImportForumFrame(bfr, pw);

            }
        });

        noButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                AddDiscussionForumFrame(bfr, pw);
            }
        });

        headerPanel.add(importQuestion);
        content.add(headerPanel, BorderLayout.NORTH);

        buttonPanel.add(yesButton);
        buttonPanel.add(noButton);
        content.add(buttonPanel, BorderLayout.CENTER);

        frame.setSize(400, 200);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }

    /**
     * allows the teacher to import a discussion forum file by typing in the filepath of the file in IntelliJ
     */

    public static void ImportForumFrame(BufferedReader bfr, PrintWriter pw) {
        JLabel enterFilePathPrompt;
        JLabel filePathPrompt;
        JTextField filePath;
        JButton enterButton;
        JFrame frame = new JFrame();
        frame.setTitle("Discussion Forum");
        JPanel headerPanel = new JPanel();
        JPanel buttonPanel = new JPanel();
        JPanel filePathPanel = new JPanel();
        Container content = frame.getContentPane();
        content.setLayout(new BorderLayout());

        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        };

        enterFilePathPrompt = new JLabel("Enter the filepath of the file you want to import.");
        filePathPrompt = new JLabel("File Path:");
        filePath = new JTextField(20);

        enterButton = new JButton("Enter");
        enterButton.addActionListener(actionListener);

        enterButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (filePath.getText().equals("")) {
                    JOptionPane.showMessageDialog(null, "The filepath cannot be blank",
                        "Discussion Forum", JOptionPane.ERROR_MESSAGE);
                } else {
                    pw.println("teacher is importing forum");
                    pw.flush();

                    String filePathString = filePath.getText();
                    pw.println(filePathString);
                    pw.flush();

                    String wasImported = null;
                    try {
                        wasImported = bfr.readLine();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    if ("forum was imported".equals(wasImported)) {
                        frame.dispose();
                        JOptionPane.showMessageDialog(null, "Your forum has been added!",
                            "Discussion Forum", JOptionPane.INFORMATION_MESSAGE);
                        CoursesMenuFrame(bfr, pw);
                    } else {
                        JOptionPane.showMessageDialog(null, "There was an error importing" +
                            " the file", "Discussion Forum", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        headerPanel.add(enterFilePathPrompt);
        content.add(headerPanel, BorderLayout.NORTH);

        filePathPanel.add(filePathPrompt);
        filePathPanel.add(filePath);
        content.add(filePathPanel, BorderLayout.CENTER);

        buttonPanel.add(enterButton);
        content.add(buttonPanel, BorderLayout.SOUTH);

        frame.setSize(400, 200);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }

    /**
     * allows the teacher to add a discussion forum by self-entering the title and prompt
     */

    public static void AddDiscussionForumFrame(BufferedReader bfr, PrintWriter pw) {
        JLabel addDiscussionForumPrompt;
        JLabel titlePrompt;
        JLabel forumPrompt;
        JLabel spaces;
        JTextField title;
        JTextArea prompt;
        JButton enterButton;
        JFrame frame = new JFrame();
        frame.setTitle("Discussion Forum");
        JPanel headerPanel = new JPanel();
        JPanel textFieldPanel = new JPanel();
        JPanel buttonPanel = new JPanel();
        Container content = frame.getContentPane();
        content.setLayout(new BorderLayout());

        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        };

        addDiscussionForumPrompt = new JLabel("Enter information to add a discussion forum.");
        titlePrompt = new JLabel("Discussion Title:");
        forumPrompt = new JLabel("Discussion Prompt:");
        spaces = new JLabel(" ");
        title = new JTextField(20);
        prompt = new JTextArea(2, 20);
        prompt.setLineWrap(true);
        prompt.setWrapStyleWord(true);

        enterButton = new JButton("Enter");
        enterButton.addActionListener(actionListener);


        enterButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (title.getText().equals("") || prompt.getText().equals("")) {
                    JOptionPane.showMessageDialog(null, "The title and/or prompt cannot be " +
                        "empty", "Discussion Forum", JOptionPane.ERROR_MESSAGE);
                } else {
                    pw.println("teacher is adding a discussion forum");
                    pw.flush();
                    String forumTitle = title.getText();
                    pw.println(forumTitle);
                    pw.flush();
                    String promptOfForum = prompt.getText();
                    pw.println(promptOfForum);
                    pw.flush();

                    String disussionForumAdded = "";
                    try {
                        disussionForumAdded = bfr.readLine();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                    if ("discussion forum has been added".equals(disussionForumAdded)) {
                        frame.dispose();
                        JOptionPane.showMessageDialog(null, "Your forum has been added!",
                            "Discussion Forum", JOptionPane.INFORMATION_MESSAGE);
                        CoursesMenuFrame(bfr, pw);
                    }
                }
            }
        });

        headerPanel.add(addDiscussionForumPrompt);
        content.add(headerPanel, BorderLayout.NORTH);


        textFieldPanel.add(titlePrompt);
        textFieldPanel.add(title);
        textFieldPanel.add(spaces);
        textFieldPanel.add(forumPrompt);
        textFieldPanel.add(prompt);
        content.add(textFieldPanel, BorderLayout.CENTER);

        buttonPanel.add(enterButton);
        content.add(buttonPanel, BorderLayout.SOUTH);

        frame.setSize(500, 200);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }

    /**
     * allows the teacher to edit a discussion forum by typing the old title of the forum, the new title of the forum,
     * and the new prompt for the forum
     */

    public static void EditDiscussionForumFrame(BufferedReader bfr, PrintWriter pw) {
        JLabel editDiscussionForumPrompt;
        JLabel oldTitlePrompt;
        JLabel titlePrompt;
        JLabel forumPrompt;
        JLabel spaces;
        JTextField oldTitle;
        JTextField title;
        JTextArea prompt;
        JButton enterButton;
        JFrame frame = new JFrame();
        frame.setTitle("Discussion Forum");
        JPanel headerPanel = new JPanel();
        JPanel textFieldPanel = new JPanel();
        JPanel buttonPanel = new JPanel();
        Container content = frame.getContentPane();
        content.setLayout(new BorderLayout());

        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        };

        editDiscussionForumPrompt = new JLabel("Enter information to edit a discussion forum.");
        oldTitlePrompt = new JLabel("Title of the Forum you Want to Edit:");
        titlePrompt = new JLabel("Discussion Title:");
        forumPrompt = new JLabel("Discussion Prompt:");
        spaces = new JLabel(" ");
        title = new JTextField(20);
        oldTitle = new JTextField(15);
        prompt = new JTextArea(2, 20);
        prompt.setLineWrap(true);
        prompt.setWrapStyleWord(true);

        enterButton = new JButton("Enter");
        enterButton.addActionListener(actionListener);


        enterButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if ("".equals(title.getText()) || "".equals(prompt.getText())) {
                    JOptionPane.showMessageDialog(null, "The title and/or prompt cannot " +
                        "be empty", "Discussion Forum", JOptionPane.ERROR_MESSAGE);
                } else {
                    pw.println("teacher is editing a discussion forum");
                    pw.flush();
                    String oldForumTitleText = oldTitle.getText();
                    pw.println(oldForumTitleText);
                    pw.flush();
                    String forumTitle = title.getText();
                    pw.println(forumTitle);
                    pw.flush();
                    String promptOfForum = prompt.getText();
                    pw.println(promptOfForum);
                    pw.flush();

                    String disussionForumEdited = "";
                    try {
                        disussionForumEdited = bfr.readLine();

                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                    if ("discussion forum has been edited".equals(disussionForumEdited)) {
                        frame.dispose();
                        JOptionPane.showMessageDialog(null, "Your forum has been edited!",
                            "Discussion Forum", JOptionPane.INFORMATION_MESSAGE);
                        CoursesMenuFrame(bfr, pw);
                    } else if ("forum does not exist".equals(disussionForumEdited)) {
                        JOptionPane.showMessageDialog(null, "That forum does not exist!",
                            "Discussion Forum", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        headerPanel.add(editDiscussionForumPrompt);
        content.add(headerPanel, BorderLayout.NORTH);

        textFieldPanel.add(oldTitlePrompt);
        textFieldPanel.add(oldTitle);
        textFieldPanel.add(titlePrompt);
        textFieldPanel.add(title);
        textFieldPanel.add(spaces);
        textFieldPanel.add(forumPrompt);
        textFieldPanel.add(prompt);
        content.add(textFieldPanel, BorderLayout.CENTER);

        buttonPanel.add(enterButton);
        content.add(buttonPanel, BorderLayout.SOUTH);

        frame.setSize(500, 250);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }

    /**
     * Allows the teacher to delete a discussion forum by choosing the forum they would like to delete from a
     * drop-down menu after clicking the View Forums button.
     */

    public static void DeleteDiscussionForumFrame(BufferedReader bfr, PrintWriter pw) {
        JLabel deleteDiscussionForum;
        JLabel spaces;
        JButton viewForums;
        JButton deleteButton;
        JComboBox listOfForums = new JComboBox();
        ArrayList<String> choices = new ArrayList<>();
        JFrame frame = new JFrame();
        frame.setTitle("Discussion Forum");
        JPanel headerPanel = new JPanel();
        JPanel coursesPanel = new JPanel();
        JPanel buttonPanel = new JPanel();
        Container content = frame.getContentPane();
        content.setLayout(new BorderLayout());

        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        };

        deleteDiscussionForum = new JLabel("Choose the discussion forum you want to delete.");
        spaces = new JLabel(" ");


        viewForums = new JButton("View Forums");
        viewForums.addActionListener(actionListener);

        deleteButton = new JButton("Delete");
        deleteButton.addActionListener(actionListener);

        viewForums.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String[] discussionForumJCombo;
                String actualForumName;

                pw.println("teacher is viewing discussion forums for deletion");
                pw.flush();
                String forumName = "";

                try {
                    forumName = bfr.readLine();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                try {
                    while (!forumName.equals("done printing existing teacher forums")) {
                        if (forumName.contains("Forum:")) {
                            discussionForumJCombo = forumName.split(":");
                            actualForumName = discussionForumJCombo[1];

                            choices.add(actualForumName);
                        }
                        forumName = bfr.readLine();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                for (String choice : choices) {
                    listOfForums.addItem(choice);
                }
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                pw.println("teacher is deleting forum");
                pw.flush();

                String selectedForum = listOfForums.getSelectedItem().toString();
                pw.println(selectedForum);
                pw.flush();


                String isForumDeleted = "";
                try {
                    isForumDeleted = bfr.readLine();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }


                if ("forum is deleted".equals(isForumDeleted)) {
                    frame.dispose();
                    JOptionPane.showMessageDialog(null, "Your forum has been deleted!",
                        "Discussion Forum", JOptionPane.INFORMATION_MESSAGE);
                    CoursesMenuFrame(bfr, pw);
                }
            }
        });

        headerPanel.add(deleteDiscussionForum);
        content.add(headerPanel, BorderLayout.NORTH);

        coursesPanel.add(listOfForums);
        content.add(coursesPanel, BorderLayout.CENTER);

        buttonPanel.add(viewForums);
        buttonPanel.add(deleteButton);
        content.add(buttonPanel, BorderLayout.SOUTH);

        frame.setSize(500, 150);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }


    /**
     * Allows the teacher to view all forums by viewing a drop-down menu that contains the title of each forum after
     * clicking the View Forums button.
     */

    public static void ViewAllForumsFrame(BufferedReader bfr, PrintWriter pw) {
        JComboBox listOfForums = new JComboBox();
        JLabel studentPicksForum;
        JButton backButton;
        JButton viewForums;
        JButton enterButton;
        ArrayList<String> choices = new ArrayList<>();
        JFrame frame = new JFrame();
        frame.setTitle("View Courses");
        JPanel headerPanel = new JPanel();
        JPanel coursesPanel = new JPanel();
        JPanel buttonPanel = new JPanel();
        Container content = frame.getContentPane();
        content.setLayout(new BorderLayout());

        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        };

        studentPicksForum = new JLabel("Click view forums in order to view a forum");

        backButton = new JButton("Back");
        backButton.addActionListener(actionListener);

        viewForums = new JButton("View Forums");
        viewForums.addActionListener(actionListener);

        enterButton = new JButton("Enter");
        enterButton.addActionListener(actionListener);

        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                ExistingCoursesStudentFrame(bfr, pw);
            }
        });

        viewForums.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String[] discussionForumJCombo;
                String actualForumName;

                pw.println("teacher is viewing discussion forums");
                pw.flush();
                String forumName = "";

                try {
                    forumName = bfr.readLine();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }


                try {
                    while (!"done printing existing teacher forums".equals(forumName)) {
                        if (forumName.contains("Forum:")) {
                            discussionForumJCombo = forumName.split(":");
                            actualForumName = discussionForumJCombo[1];

                            choices.add(actualForumName);
                        }
                        forumName = bfr.readLine();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }


                for (String choice : choices) {
                    listOfForums.addItem(choice);
                }

            }
        });

        enterButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pw.println("teacher is selecting a forum");
                pw.flush();
                String selectedForum = listOfForums.getSelectedItem().toString();
                pw.println(selectedForum);
                pw.flush();
                frame.dispose();
                ViewSpecificForumFrame(bfr, pw);

            }
        });

        headerPanel.add(studentPicksForum);
        content.add(headerPanel, BorderLayout.NORTH);

        coursesPanel.add(listOfForums);
        content.add(coursesPanel, BorderLayout.CENTER);

        buttonPanel.add(backButton);
        buttonPanel.add(viewForums);
        buttonPanel.add(enterButton);
        content.add(buttonPanel, BorderLayout.SOUTH);


        frame.setSize(400, 200);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }

    /**
     * Allows the teacher to view the prompt and title of the forum as well as choose between either sorting a forum
     * by its votes or replying to a student
     */

    public static void ViewSpecificForumFrame(BufferedReader bfr, PrintWriter pw) {
        JLabel header;
        JButton sortRepliesByVote;
        JButton replyToAStudent;
        JFrame frame = new JFrame();
        frame.setTitle("Discussion Forum");
        JPanel titlePanel = new JPanel();
        JPanel promptPanel = new JPanel();
        JPanel buttonPanel = new JPanel();
        Container content = frame.getContentPane();
        content.setLayout(new BorderLayout());

        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        };

        header = new JLabel("Click 'View Entire Forum' to view the forum. Then choose what you would like to do.");

        sortRepliesByVote = new JButton("Sort Replies By Vote");
        sortRepliesByVote.addActionListener(actionListener);

        replyToAStudent = new JButton("Reply to A Student");
        replyToAStudent.addActionListener(actionListener);

        sortRepliesByVote.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                SortRepliesByVoteFrame(bfr, pw);
            }
        });

        replyToAStudent.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                viewTeacherPostsFrame(bfr, pw);
            }
        });

        titlePanel.add(header);
        content.add(titlePanel, BorderLayout.NORTH);

        buttonPanel.add(sortRepliesByVote);
        buttonPanel.add(replyToAStudent);
        content.add(buttonPanel, BorderLayout.SOUTH);

        frame.setSize(600, 200);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }

    /**
     * Allows the teacher to generate the posts for a specific forum after clicking the viewPostsButton and allows the
     * teacher to enter the username and post number of the person they would like to reply to.
     */

    public static void viewTeacherPostsFrame(BufferedReader bfr, PrintWriter pw) {
        JFrame frame = new JFrame();
        frame.setTitle("Discussion Forum");
        JPanel headerPanel = new JPanel();
        JPanel textFieldPanel = new JPanel();
        JPanel replyPanel = new JPanel();
        JLabel usernameToReply;
        JLabel postNumberToReply;
        JTextField usernameOfStudentToReply;
        JTextField postNumberOfStudentToReply;
        JTextArea entireForum;
        JPanel buttonPanel = new JPanel();
        JButton viewPostsButton;
        JButton enterButton;
        Container content = frame.getContentPane();
        content.setLayout(new BorderLayout());

        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        };

        usernameToReply = new JLabel("Please enter the username of the student you would like to reply to.");
        postNumberToReply = new JLabel("Please enter the post number of the post you would like to reply to");
        usernameOfStudentToReply = new JTextField(20);
        postNumberOfStudentToReply = new JTextField(5);
        entireForum = new JTextArea(25, 50);
        entireForum.setLineWrap(true);
        entireForum.setWrapStyleWord(true);
        enterButton = new JButton("Enter");
        viewPostsButton = new JButton("View Posts");

        viewPostsButton.addActionListener(actionListener);

        viewPostsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pw.println("teacher is trying to view the posts to reply to");
                pw.flush();
                String discussionPosts = "";
                String replyLineFromServer = null;
                try {

                    replyLineFromServer = discussionPosts = bfr.readLine();

                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                discussionPosts = replyLineFromServer.replaceAll("\\|\\|", "\n");
                entireForum.setText(discussionPosts);
                entireForum.setEditable(false);
            }
        });

        enterButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                // Check user / post exists
                pw.println("check_user_and_post_num");
                pw.println(usernameOfStudentToReply.getText());
                pw.println(postNumberOfStudentToReply.getText());
                pw.flush();
                String serverResponse = null;
                try {
                    serverResponse = bfr.readLine();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                if (serverResponse.equals("post found")) {
                    TeacherReplyStudentFrame(bfr, pw, usernameOfStudentToReply.getText(),
                        postNumberOfStudentToReply.getText());
                } else {
                    JOptionPane.showMessageDialog(null, "Post number or username is not found",
                        "Discussion Post", JOptionPane.ERROR_MESSAGE);
                    ForumMenuStudentFrame(bfr, pw);
                }
            }
        });

        headerPanel.add(entireForum);
        content.add(headerPanel, BorderLayout.NORTH);

        replyPanel.add(usernameToReply);
        replyPanel.add(usernameOfStudentToReply);
        replyPanel.add(postNumberToReply);
        replyPanel.add(postNumberOfStudentToReply);
        content.add(replyPanel, BorderLayout.CENTER);

        buttonPanel.add(viewPostsButton);
        buttonPanel.add(enterButton);
        content.add(buttonPanel, BorderLayout.SOUTH);

        frame.setSize(700, 700);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }

    /**
     * This GUI allows teachers to write a reply to another student
     * asks teachers for the reply they would like to add
     * teachers can enter these in a text area
     * if teachers press enter they are brought back to the TeacherGUI menu frame
     */

    public static void TeacherReplyStudentFrame(BufferedReader bfr, PrintWriter pw,
                                                String userRepliedTo, String replyNumStr) {
        JLabel addDiscussionReply;
        JLabel ReplyPrompt;
        JLabel spaces;
        JTextField topic;
        JTextArea prompt;
        JButton enterButton;
        JFrame frame = new JFrame();
        frame.setTitle("Discussion Reply");
        JPanel headerPanel = new JPanel();
        JPanel textFieldPanel = new JPanel();
        JPanel buttonPanel = new JPanel();
        Container content = frame.getContentPane();
        content.setLayout(new BorderLayout());

        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        };

        addDiscussionReply = new JLabel("Enter information to add a discussion reply.");
        ReplyPrompt = new JLabel("Reply:");
        spaces = new JLabel("                              ");
        topic = new JTextField(20);
        prompt = new JTextArea(2, 15);
        prompt.setLineWrap(true);
        prompt.setWrapStyleWord(true);

        enterButton = new JButton("Enter");
        enterButton.addActionListener(actionListener);

        enterButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();

                pw.println("user is replying to post");
                pw.flush();
                pw.println(userRepliedTo);
                pw.flush();
                pw.println(replyNumStr);
                pw.flush();
                pw.println(prompt.getText());
                pw.flush();
                String responseFromServer = null;
                try {
                    responseFromServer = bfr.readLine();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                if (responseFromServer.equals("found post")) {
                    JOptionPane.showMessageDialog(null, "Your reply has been added!",
                        "Discussion Post", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Post number or username is not found",
                        "Discussion Post", JOptionPane.ERROR_MESSAGE);
                }
                ForumMenuStudentFrame(bfr, pw);
            }
        });

        headerPanel.add(addDiscussionReply);
        content.add(headerPanel, BorderLayout.NORTH);

        textFieldPanel.add(ReplyPrompt);
        textFieldPanel.add(prompt);
        content.add(textFieldPanel, BorderLayout.CENTER);

        buttonPanel.add(enterButton);
        content.add(buttonPanel, BorderLayout.SOUTH);

        frame.setSize(500, 200);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }


    /**
     * allows the teacher to sort a forum by votes
     */

    public static void SortRepliesByVoteFrame(BufferedReader bfr, PrintWriter pw) {
        JFrame frame = new JFrame();
        frame.setTitle("Discussion Forum");
        JPanel titlePanel = new JPanel();
        JPanel promptPanel = new JPanel();
        JPanel buttonPanel = new JPanel();
        Container content = frame.getContentPane();
        content.setLayout(new BorderLayout());

        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        };

        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }


    /**
     * allows the teacher to choose a forum to grade using the drop-down menu with all the forums for the course
     */

    public static void GradeForumFrame(BufferedReader bfr, PrintWriter pw) {
        JLabel viewDiscussionForum;
        JButton enterButton;
        JButton viewForums;
        JComboBox listOfForums = new JComboBox();
        ArrayList<String> choices = new ArrayList<>();
        JFrame frame = new JFrame();
        frame.setTitle("Discussion Forum");
        JPanel headerPanel = new JPanel();
        JPanel coursesPanel = new JPanel();
        JPanel buttonPanel = new JPanel();
        Container content = frame.getContentPane();
        content.setLayout(new BorderLayout());

        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        };

        viewDiscussionForum = new JLabel("Choose the discussion forum you want to view.");

        viewForums = new JButton("View Forums");
        viewForums.addActionListener(actionListener);

        enterButton = new JButton("Enter");
        enterButton.addActionListener(actionListener);

        viewForums.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String[] discussionForumJCombo;
                String actualForumName;

                pw.println("teacher is viewing discussion forums");
                pw.flush();
                String forumName = "";

                try {
                    forumName = bfr.readLine();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }


                try {
                    while (!forumName.equals("done printing existing teacher forums")) {
                        if (forumName.contains("Forum:")) {
                            discussionForumJCombo = forumName.split(":");
                            actualForumName = discussionForumJCombo[1];

                            choices.add(actualForumName);
                        }
                        forumName = bfr.readLine();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }


                for (String choice : choices) {
                    listOfForums.addItem(choice);
                }

            }
        });


        enterButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                StudentUsernamesFrame(bfr, pw);
            }
        });

        headerPanel.add(viewDiscussionForum);
        content.add(headerPanel, BorderLayout.NORTH);

        coursesPanel.add(listOfForums);
        content.add(coursesPanel, BorderLayout.CENTER);

        buttonPanel.add(viewForums);
        buttonPanel.add(enterButton);
        content.add(buttonPanel, BorderLayout.SOUTH);

        frame.setSize(500, 150);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }

    /**
     * allows the teacher to choose a specific student to grade by username
     */

    public static void StudentUsernamesFrame(BufferedReader bfr, PrintWriter pw) {
        JLabel viewDiscussionForum;
        JLabel spaces;
        JButton enterButton;
        JComboBox listOfUsernames;
        JFrame frame = new JFrame();
        frame.setTitle("Discussion Forum");
        JPanel headerPanel = new JPanel();
        JPanel coursesPanel = new JPanel();
        JPanel buttonPanel = new JPanel();
        Container content = frame.getContentPane();
        content.setLayout(new BorderLayout());

        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        };

        viewDiscussionForum = new JLabel("Choose a student username to grade a student.");
        spaces = new JLabel(" ");

        String[] choices = {"aditi123", "donna", "prachi3", "noa5", "enoch9"};
        listOfUsernames = new JComboBox<String>(choices);

        enterButton = new JButton("Enter");
        enterButton.addActionListener(actionListener);


        enterButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                AssignGradeFrame(bfr, pw);
            }
        });

        headerPanel.add(viewDiscussionForum);
        content.add(headerPanel, BorderLayout.NORTH);

        coursesPanel.add(listOfUsernames);
        content.add(coursesPanel, BorderLayout.CENTER);

        buttonPanel.add(enterButton);
        content.add(buttonPanel, BorderLayout.SOUTH);

        frame.setSize(500, 150);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }

    /**
     * allows the teacher to assign a grade to a student
     */

    public static void AssignGradeFrame(BufferedReader bfr, PrintWriter pw) {
        JLabel enterGradePrompt;
        JTextField grade;
        JButton gradeButton;
        JFrame frame = new JFrame();
        frame.setTitle("Discussion Forum");
        JPanel headerPanel = new JPanel();
        JPanel textFieldPanel = new JPanel();
        JPanel buttonPanel = new JPanel();
        Container content = frame.getContentPane();
        content.setLayout(new BorderLayout());

        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        };

        enterGradePrompt = new JLabel("Enter grade");

        grade = new JTextField(20);

        gradeButton = new JButton("Enter");
        gradeButton.addActionListener(actionListener);


        gradeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                if (grade.getText().equals("")) {
                    JOptionPane.showMessageDialog(null, "The grade cannot be blank",
                        "Discussion Forum", JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Your grade has been added!",
                        "Discussion Forum", JOptionPane.INFORMATION_MESSAGE);
                    CoursesMenuFrame(bfr, pw);
                }
            }
        });

        content.add(headerPanel, BorderLayout.NORTH);

        textFieldPanel.add(enterGradePrompt);
        textFieldPanel.add(grade);
        content.add(textFieldPanel, BorderLayout.CENTER);

        buttonPanel.add(gradeButton);
        content.add(buttonPanel, BorderLayout.SOUTH);

        frame.setSize(500, 200);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }

    /**
     * allows the teacher to add a new course
     */

    public static void AddNewCourseFrame(BufferedReader bfr, PrintWriter pw) {
        JLabel addNewCoursePrompt;
        JLabel courseNamePrompt;
        JLabel spaces;
        JTextField courseName;
        JButton backButton;
        JButton enterButton;
        JFrame frame = new JFrame();
        frame.setTitle("Discussion Forum");
        JPanel headerPanel = new JPanel();
        JPanel textFieldPanel = new JPanel();
        JPanel buttonPanel = new JPanel();
        Container content = frame.getContentPane();
        content.setLayout(new BorderLayout());

        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        };

        addNewCoursePrompt = new JLabel("Add a New Course");
        courseNamePrompt = new JLabel("Course Name:");
        spaces = new JLabel(" ");
        courseName = new JTextField(20);

        backButton = new JButton("Back to Main Menu");
        backButton.addActionListener(actionListener);

        enterButton = new JButton("Enter");
        enterButton.addActionListener(actionListener);


        enterButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (courseName.getText().equals("")) {
                    JOptionPane.showMessageDialog(null, "The course name cannot be blank",
                        "Discussion Forum", JOptionPane.ERROR_MESSAGE);
                } else {
                    pw.println("teacher is adding a new course");
                    pw.flush();
                    String newCourseName = courseName.getText();
                    pw.println(newCourseName);
                    pw.flush();

                    String isCourseAdded = "";
                    try {
                        isCourseAdded = bfr.readLine();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                    if ("course was added".equals(isCourseAdded)) {
                        frame.dispose();
                        JOptionPane.showMessageDialog(null, "Your course was added!",
                            "Discussion Forum", JOptionPane.INFORMATION_MESSAGE);
                        TeacherGUI(bfr, pw);
                    } else if ("course already exists".equals(isCourseAdded)) {
                        JOptionPane.showMessageDialog(null, "The course already exists",
                            "Discussion Forum", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                TeacherGUI(bfr, pw);
            }
        });

        headerPanel.add(addNewCoursePrompt);
        content.add(headerPanel, BorderLayout.NORTH);


        textFieldPanel.add(courseNamePrompt);
        textFieldPanel.add(courseName);
        textFieldPanel.add(spaces);
        content.add(textFieldPanel, BorderLayout.CENTER);

        buttonPanel.add(backButton);
        buttonPanel.add(enterButton);
        content.add(buttonPanel, BorderLayout.SOUTH);

        frame.setSize(500, 200);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }


    /**
     * allows the student to view all the posts to a specific forum that they are viewing
     */

    public static void viewPostsFrame(BufferedReader bfr, PrintWriter pw) {
        JFrame frame = new JFrame();
        frame.setTitle("Discussion Forum");
        JPanel headerPanel = new JPanel();
        JPanel textFieldPanel = new JPanel();
        JPanel replyPanel = new JPanel();
        JLabel usernameToReply;
        JLabel postNumberToReply;
        JTextField usernameOfStudentToReply;
        JTextField postNumberOfStudentToReply;
        JTextArea entireForum;
        JPanel buttonPanel = new JPanel();
        JButton viewPostsButton;
        JButton enterButton;
        Container content = frame.getContentPane();
        content.setLayout(new BorderLayout());

        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        };

        usernameToReply = new JLabel("Please enter the username of the student you would like to reply to.");
        postNumberToReply = new JLabel("Please enter the post number of the post you would like to reply to");
        usernameOfStudentToReply = new JTextField(20);
        postNumberOfStudentToReply = new JTextField(5);
        entireForum = new JTextArea(25, 50);
        entireForum.setLineWrap(true);
        entireForum.setWrapStyleWord(true);
        enterButton = new JButton("Enter");
        viewPostsButton = new JButton("View Posts");

        enterButton.addActionListener(actionListener);
        viewPostsButton.addActionListener(actionListener);

        viewPostsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pw.println("student is trying to view the posts to reply to");
                pw.flush();
                String discussionPosts = "";
                String replyLineFromServer = null;
                try {

                    replyLineFromServer = discussionPosts = bfr.readLine();


                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                discussionPosts = replyLineFromServer.replaceAll("\\|\\|", "\n");

                entireForum.setText(discussionPosts);
                entireForum.setEditable(false);
            }
        });

        enterButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                pw.println("check_user_and_post_num");
                pw.println(usernameOfStudentToReply.getText());
                pw.println(postNumberOfStudentToReply.getText());
                pw.flush();
                String serverResponse = null;
                try {
                    serverResponse = bfr.readLine();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                if (serverResponse.equals("post found")) {
                    TeacherReplyStudentFrame(bfr, pw, usernameOfStudentToReply.getText(),
                        postNumberOfStudentToReply.getText());
                } else {
                    JOptionPane.showMessageDialog(null, "Post number or username is not found",
                        "Discussion Post", JOptionPane.ERROR_MESSAGE);
                    ForumMenuStudentFrame(bfr, pw);
                }
            }
        });

        headerPanel.add(entireForum);
        content.add(headerPanel, BorderLayout.NORTH);

        replyPanel.add(usernameToReply);
        replyPanel.add(usernameOfStudentToReply);
        replyPanel.add(postNumberToReply);
        replyPanel.add(postNumberOfStudentToReply);
        content.add(replyPanel, BorderLayout.CENTER);

        buttonPanel.add(viewPostsButton);
        buttonPanel.add(enterButton);
        content.add(buttonPanel, BorderLayout.SOUTH);

        frame.setSize(700, 700);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }

    /**
     * This GUI allows students to write a reply to another student
     * asks students for a topic and prompt
     * students can enter these in a text field
     * if students press enter they are brought back to the forum menu
     */

    public static void ReplyStudentFrame(BufferedReader bfr, PrintWriter pw,
                                         String userRepliedTo, String replyNumStr) {
        JLabel addDiscussionReply;
        JLabel ReplyTopicPrompt;
        JLabel ReplyPrompt;
        JLabel spaces;
        JTextField topic;
        JTextArea prompt;
        JButton enterButton;
        JFrame frame = new JFrame();
        frame.setTitle("Discussion Reply");
        JPanel headerPanel = new JPanel();
        JPanel textFieldPanel = new JPanel();
        JPanel buttonPanel = new JPanel();
        Container content = frame.getContentPane();
        content.setLayout(new BorderLayout());

        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        };

        addDiscussionReply = new JLabel("Enter information to add a discussion reply.");
        ReplyTopicPrompt = new JLabel("Reply Topic:");
        ReplyPrompt = new JLabel("Reply Prompt:");
        spaces = new JLabel("                              ");
        topic = new JTextField(20);
        prompt = new JTextArea(2, 15);
        prompt.setLineWrap(true);
        prompt.setWrapStyleWord(true);

        enterButton = new JButton("Enter");
        enterButton.addActionListener(actionListener);

        enterButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                pw.println("user is replying to post");
                pw.flush();
                pw.println(userRepliedTo);
                pw.flush();
                pw.println(replyNumStr);
                pw.flush();
                pw.println(prompt.getText());
                pw.flush();
                String responseFromServer = null;
                try {
                    responseFromServer = bfr.readLine();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                if (responseFromServer.equals("found post")) {
                    JOptionPane.showMessageDialog(null, "Your reply has been added!",
                        "Discussion Post", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Post number or username is not found",
                        "Discussion Post", JOptionPane.INFORMATION_MESSAGE);
                }
                ForumMenuStudentFrame(bfr, pw);
            }
        });

        headerPanel.add(addDiscussionReply);
        content.add(headerPanel, BorderLayout.NORTH);

        textFieldPanel.add(ReplyTopicPrompt);
        textFieldPanel.add(topic);
        textFieldPanel.add(spaces);
        textFieldPanel.add(ReplyPrompt);
        textFieldPanel.add(prompt);
        content.add(textFieldPanel, BorderLayout.CENTER);

        buttonPanel.add(enterButton);
        content.add(buttonPanel, BorderLayout.SOUTH);

        frame.setSize(500, 200);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }

}
