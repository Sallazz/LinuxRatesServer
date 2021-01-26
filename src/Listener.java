/**
 * Description: Listens for client requests and creates new ResponseBody object on
 * a separate thread to prevent blocking.
 */

import java.io.IOException;
import java.net.ServerSocket;

public class Listener {

    //Fields.
    private ServerSocket serverSocket;
    //Static field so each thread has a unique number.
    public static int threadCount;

    //Listens for requests from client and creates a new thread for each request to avoid blocking.
    public Listener(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        threadCount = 1;
        while (true){
            new Thread(new ResponseBody(serverSocket.accept())).start();
        }
    }

}
