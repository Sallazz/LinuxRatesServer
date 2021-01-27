/**
 * Description: Interprets client request to determine if it's a request for all
 * rates for a given date or a request for a single rate; then applies the
 * appropriate SQL query in the database.
 */

import data.DataSource;
import data.Rate;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.List;

public class ResponseBody implements Runnable{

    // Remove this comment.
    //Fields.
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private String name;
    public Thread t;

    // Remove this comment.
    //Constructor.
    public ResponseBody(Socket s){
        clientSocket = s;
        //Increases static threadCount field so it's unique.
        Listener.threadCount++;
        name = "thread" + Listener.threadCount;
        t = new Thread(this, name);
    }

    @Override
    public void run() {
        try{
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String inputLine = in.readLine();
            System.out.println("Client request received at " + LocalDateTime.now() + " -> " + inputLine); // in gerenal there is a large amount of strings created. Consider String Builder. Also use loggers for these, they log time on their own.
            //If inputLine contains "-" then client is requesting all rates for a given date.
            if (inputLine.contains("-")){
                List<Rate> rates = DataSource.getInstance()
                                             .getRates(inputLine); // One statement per line and alligned to the "."
                
                String response = generateResponse(rates);
                out.println(response);
                System.out.println("Response sent to client at " + LocalDateTime.now() + " -> " + response + "\n"); // Same.
            }else{ //If inputLine doesn't contain "-" then client is requesting a single rate for a given date.
                String rateResponse = DataSource.getInstance()
                                                .getRate(inputLine); // One statement per line and alligned to the "."
                
                out.println(rateResponse);
                System.out.println("Response sent to client at " + LocalDateTime.now() + " -> " + rateResponse + "\n");
            }
            //Closes reader, writer and socket to prevent blocking.
            in.close();
            out.close();
            clientSocket.close();
        } catch (IOException e) {
            System.out.println("IOException caught in ResponseBody -> run()");
        }
    }

   
    // How do these responces look like? date:country:rate? Is that FX rates e.g.: EUR/GBP: 1.12345 ?
    private String generateResponse(List<Rate> r){
        try {
            //Combine rate objects from query result into a string which is sent to client.
            StringBuilder sb = new StringBuilder();
            for (Rate rate : r) {
                sb.append(rate.getDate() + ":" + rate.getCountry() + ":" + rate.getRate() + "|");
            }
            // Leave an empty string before the return statement.
            return sb.toString();
        } catch (NullPointerException e) {
            return "No table with that date"; // That might not be the case correct? Any one of those 3 fields might throw a NPE so different bits might be wrong. Also at that point your ll return null right? Returning "" might be safer but it depends on your overall strategy of handling nulls.
        }
    }
}
