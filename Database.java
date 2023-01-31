import java.io.*;
import java.time.LocalTime;
import java.util.ArrayList;

/**
 * DataBase class
 *
 * <p>Purdue University -- CS18000 -- Fall 2021</p>
 *
 * @author Aditi Patel, Prachi Sacheti Purdue CS
 * @version December 12, 2021
 */

public class DataBase {
    ArrayList<Courses> c = new ArrayList<>();
    int latestPostNum = 0;

    public DataBase(String filename) throws IOException {
        readFromFile(filename);
    }

    public int getLatestPostNum() {
        return latestPostNum;
    }

    public ArrayList<String> readFromFile(String fileName) throws FileNotFoundException {
        c = new ArrayList<>();
        Courses courses = null;
        DiscussionForum df = null;
        DiscussionPost dp = null;
        String[] discussionForumArray = new String[3];
        String[] discussionPostsArray = new String[3];
        String[] discussionRepliesArray = new String[5];
        ArrayList<String> input = new ArrayList<>();
        int z = 0;
        try (BufferedReader bfr = new BufferedReader(new FileReader(new File(fileName)))) {
            String line = "";
            while ((line = bfr.readLine()) != null) {
                //System.out.println(line + "Line no" + z);
                input.add(line);
                //z++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

//        int ind=0;
        //System.out.println(input + "ARRAYLIST READ FROM FILE");
        for (int i = 0; i < input.size(); i++) {
            ArrayList<Courses> database = new ArrayList<>();

            if (input.get(i).contains("CCCC%%")) {
                String courseName = input.get(i).substring(6);
                courses = new Courses(courseName);
            } else if (input.get(i).contains("FFFF%%")) {
                discussionForumArray = input.get(i).split("%%");
                String title = discussionForumArray[1];
                String prompt = discussionForumArray[2];
                df = new DiscussionForum(title, prompt);
                courses.addDiscussionForum(df);
            } else if (input.get(i).contains("PPPP%%")) {
                discussionPostsArray = input.get(i).split("%%");
                String prompt = discussionPostsArray[1];
                String username = discussionPostsArray[2];
                String postNumberString = discussionPostsArray[3];

                int postNumber = Integer.parseInt(postNumberString);
                if (postNumber > latestPostNum) {
                    latestPostNum = postNumber;
                }
                /*
                String voteString = discussionPostsArray[4];
                int votes = Integer.parseInt(voteString);
                 */
                dp = new DiscussionPost(prompt, username, postNumber);
                courses.findForum(df.getDiscussionTitle()).addPost(dp);
            } else if (input.get(i).contains("RRRR%%")) {
                discussionRepliesArray = input.get(i).split("%%");
                String voteString = discussionRepliesArray[1];
                int votes = Integer.parseInt(voteString);
                String message = discussionForumArray[2];
                String timeString = discussionRepliesArray[3];
                LocalTime time = LocalTime.parse(timeString);
                String studentString = discussionRepliesArray[4];
                //parse student string to student type??? or just get username
                Reply reply = new Reply(votes, message, time);
                courses.findForum(df.getDiscussionTitle()).findPost(dp.getPostNumber()).addReply(reply);
            }

            if ((i + 1 < input.size() && input.get(i + 1).contains("CCCC%%")) || i + 1 == input.size()) {
                c.add(courses);
            }
        }

        return input;
    }


    //writes information the teacher adds to a course to a txt file
    public void writeToFile(ArrayList<Courses> courses) throws FileNotFoundException {
        try {
            File f = new File("backup.txt");
            FileOutputStream fos = new FileOutputStream(f, false);
            PrintWriter pw = new PrintWriter(fos);

            for (Courses course : courses) {
                pw.print(course.toString());
            }


            pw.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
