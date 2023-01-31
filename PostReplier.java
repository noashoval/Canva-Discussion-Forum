import java.util.ArrayList;

/**
 * Class User Post Replier, allows the user to respond to a specific post based on username and post number
 *
 * <p>Purdue University -- CS18000 -- Fall 2021</p>
 *
 * @author Donna Prince Purdue CS
 * @version December 15, 2021
 */

public class PostReplier {
    /*
    method allows a user to respond to a post based on the username and post number, once 
    they have entered that information it will cross-check to ensure that discussion post 
    exists and after that will post a reply below it. In order to signify it is a reply it 
    will have a %%%RRR with an indent underneath the post.
     */
    public String replyToPost(int postNumber, String replyContent, String postedStudentName,
                              String replyingUserName, ArrayList<DiscussionPost> curSelectedPosts,
                              UserInfo userObj) {
        String retString = null;
        boolean foundPost = false;
        for (DiscussionPost post : curSelectedPosts) {
            if (postNumber == post.getPostNumber()) {
                foundPost = true;
                Reply reply = null;
                if (userObj instanceof Teacher) {
                    reply = new Reply(replyContent, (Teacher) userObj);
                } else if (userObj instanceof Student) {
                    reply = new Reply(replyContent, (Student) userObj);
                }
                post.addReply(reply);
                return "found post";
            }
        }
        retString = "There was no post found with the given post number.Try again!";

        return retString;
    }

}
