import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * MyThread class
 *
 * <p>Purdue University -- CS18000 -- Fall 2021</p>
 *
 * @author Aditi Patel, Prachi Sacheti Purdue CS
 * @version December 12, 2021
 */

public class MyThread implements Runnable {
    Socket socket;

    public MyThread(Socket socket) {
        this.socket = socket;
    }

    public void run() {

        try {
            BufferedReader bfr = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter pw = new PrintWriter(socket.getOutputStream());
        } catch(IOException e) {
            e.printStackTrace();
        }

    }


}
