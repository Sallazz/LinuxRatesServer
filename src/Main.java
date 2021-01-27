/**
 * Description: Confirms with user if they'd like to start the server. If so, creates a DataSource
 *  object, CheckLastFive object and Listener object.
 *  User can also select to quit the application.
 */

// Comments in top of file should be less technical usually and describe what the file is doing from a higher point of view. E.g. here allows the user to start or stop the server
// These class comments can be used to extract automatic documentation so must be considered as such.

import data.DataSource;
import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        boolean quit = false;
        //Loops until a valid response is given or the application is terminated.
        // Do you need the "quit" here? You are not using it below. (see my comment on system.exit as well). If you dont need it remove it, or use it instead.
        // Also a double negative is harder to read than a possitive (consider renaming to while(running)).
        while (!quit) {
            System.out.println("Would you like to start the Currency Server? (y - yes | q - quit):");
            String response = scanner.nextLine();
            if (response.contains("y")) {
                System.out.println("Currency Server started. (ctrl-c to stop Currency Server.)");
                DataSource d = new DataSource();
                CheckLastFive c = new CheckLastFive();
                c.executeCheck();                       // executeCheck is very criptic. Needs renaming.
                Listener listener = new Listener(5000);
            }else if (response.contains("q")){
                System.out.println("Exiting Currency Server."); // Consider just exiting the loop here and letting the main return naturally. Is there a reason why you System.exit here?
                System.exit(0);
            }else{
                System.out.println("Invalid response! Must be y or q.");
            }
        }

    }

}
