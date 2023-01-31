/**
 * UserInfo class
 *
 * <p>Purdue University -- CS18000 -- Fall 2021</p>
 *
 * @author Prachi Sacheti Purdue CS
 * @version December 12, 2021
 */


public class UserInfo {
    private String username;
    private String password;
    private String userType;

    public UserInfo(String username, String password, String userType) {
        this.username = username;
        this.password = password;
        this.userType = userType;
    }

    public UserInfo(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public String toString() {
        return username + ", " + password + ", " + userType;
    }
}
