/**
 * Description: Confirms with user if they'd like to start the server. If so, creates a DataSource
 *  object, CheckLastFive object and Listener object.
 *  User can also select to quit the application.
 */

import data.DataSource;
import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        boolean quit = false;
        //Loops until a valid response is given or the application is terminated.
        while (!quit) {
            System.out.println("Would you like to start the Currency Server? (y - yes | q - quit):");
            String response = scanner.nextLine();
            if (response.contains("y")) {
                System.out.println("Currency Server started. (ctrl-c to stop Currency Server.)");
                DataSource d = new DataSource();
                CheckLastFive c = new CheckLastFive();
                c.executeCheck();
                Listener listener = new Listener(5000);
            }else if (response.contains("q")){
                System.out.println("Exiting Currency Server.");
                System.exit(0);
            }else{
                System.out.println("Invalid response! Must be y or q.");
            }
        }

    }

}
