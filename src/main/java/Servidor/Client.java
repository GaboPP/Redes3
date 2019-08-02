package Servidor;
import java.io.*;
import java.net.*;
import org.json.*;
import java.util.concurrent.TimeUnit;

public class Client {
    public void run() throws IOException {


        String hostName =  "localhost";
        int portNumber = 6666; //Integer.parseInt(args[1]);

        try (
                Socket kkSocket = new Socket(hostName, portNumber);
                PrintWriter out = new PrintWriter(kkSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(kkSocket.getInputStream()))
        ) {
           // mediaPlayer media = new mediaPlayer("../../media/videp4.mp4");
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            JSONObject fromServer;
            String fromUser;
            JSONArray response;
            String message;
            String file_line;
            String some;
            try {
                fromServer = new JSONObject(in.readLine());
                while (!fromServer.getString("message").equals("Bye.")) {
                    if (fromServer.opt("response") != null) {
                        response = fromServer.getJSONArray("response");
                        System.out.println("Server: " + response);
                    }
                    if (fromServer.opt("message").equals("Done!")){
                        out.println("Arigato!");
                        message = fromServer.getString("message");
                        System.out.println("Server: " + message);
                        fromServer = new JSONObject(in.readLine());
                    }
                    message = fromServer.getString("message");
                    System.out.println("Server: " + message);
                    // System.out.println("empezamos denuevo");

                    fromUser = stdIn.readLine();
                    boolean flag = true;

                    if (fromUser != null) {
                        out.println(fromUser);
                        TimeUnit.SECONDS.sleep(1);
                    }

                    some = in.readLine();
                    fromServer = new JSONObject(some);
                    // System.out.println("some = "+ some);
                    // System.out.println("FromServer1: " + fromServer);
                }
            }
            catch(JSONException e) {
                System.out.println((e));
                e.getCause();
            }
        }
        catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        }
        catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                    hostName);
            e.printStackTrace();
            System.exit(1);
        }
        catch(InterruptedException e){
            e.printStackTrace();
        }
    }
}