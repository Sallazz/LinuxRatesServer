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


    //Fields.
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private String name;
    public Thread t;

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
            System.out.println("Client request received at " + LocalDateTime.now() + " -> " + inputLine);
            //If inputLine contains "-" then client is requesting all rates for a given date.
            if (inputLine.contains("-")){
                List<Rate> rates = DataSource.getInstance().getRates(inputLine);
                String response = generateResponse(rates);
                out.println(response);
                System.out.println("Response sent to client at " + LocalDateTime.now() + " -> " + response + "\n");
            }else{ //If inputLine doesn't contain "-" then client is requesting a single rate for a given date.
                String rateResponse = DataSource.getInstance().getRate(inputLine);
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

    private String generateResponse(List<Rate> r){
        try {
            //Combine rate objects from query result into a string which is sent to client.
            StringBuilder sb = new StringBuilder();
            for (Rate rate : r) {
                sb.append(rate.getDate() + ":" + rate.getCountry() + ":" + rate.getRate() + "|");
            }
            return sb.toString();
        } catch (NullPointerException e) {
            return "No table with that date";
        }
    }
}
