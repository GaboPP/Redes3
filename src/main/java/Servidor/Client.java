package Servidor;
import java.io.*;
import java.net.*;
import org.json.*;
import java.util.concurrent.TimeUnit;

public class Client {
    public static void descargar(String filename, String host){
        String path = System.getProperty("user.dir");
        //File file = new File(path + "/" + filename);
        int fileSize = 64*1024*1024; //16 mega bytes
        int bytesRead;
        int current = 0;
        int puerto = 5900;
        Socket socketCliente = null;

        try {
            socketCliente = new Socket(host, puerto);
            System.out.println (host);
            BufferedReader br = new BufferedReader(new InputStreamReader(socketCliente.getInputStream()));
            String confirmacion = br.readLine();

            if((confirmacion.equals("ERROR"))){
                System.out.println("El archivo no esta disponible en estos momentos, intentelo mas tarde");
                socketCliente.close();
                return;
            }

            System.out.println(confirmacion);
            byte []byteArray = new byte [fileSize];
            InputStream inputStream = socketCliente.getInputStream();
            FileOutputStream fileOutput = new FileOutputStream(path + "/" + filename);
            BufferedOutputStream bufferedOutput = new BufferedOutputStream(fileOutput);
            bytesRead = inputStream.read(byteArray, 0, byteArray.length);
            current = bytesRead;

            do{
                bytesRead = inputStream.read(byteArray, current, (byteArray.length - current));
                if (bytesRead >= 0) current += bytesRead;
            }while(bytesRead > -1);

            bufferedOutput.write(byteArray, 0, current);
            bufferedOutput.flush();
            System.out.println("DESCARGA COMPLETA");

            inputStream.close();
            fileOutput.close();
            bufferedOutput.close();
            socketCliente.close();

        } catch (IOException e){
            e.printStackTrace();
        }
    }

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
                message = fromServer.getString("message");
                while (!message.equals("Bye.")) {
                    message = fromServer.getString("message");
                    if (fromServer.opt("response") != null) {
                        response = fromServer.getJSONArray("response");
                        System.out.println("Server: " + response);
                    }
                    if (fromServer.opt("message").equals("Done!")){
                        out.println("ack");
                        message = fromServer.getString("message");
                        System.out.println("Server: " + message);
                        fromServer = new JSONObject(in.readLine());
                    }
                    System.out.println("Server: " + message);
                    // System.out.println("empezamos denuevo");

                    fromUser = stdIn.readLine();
                    boolean flag = true;
                    if (fromUser.equals("2")){
                        fromUser = stdIn.readLine();
                        out.println("2 " + fromUser);
                        descargar(fromUser, hostName);
                        message = fromServer.getString("message");
                        System.out.println("Server: " + message);
                    }
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