package Servidor;

import java.net.*;
import java.io.*;

public class multiserverthread extends Thread {
    private Socket socket = null;
    

    //int portNumberV1 = 4448;
    //int portNumberV2 = 4449;
    int portNumber = 6666;

    public multiserverthread(Socket socket) {
        // super("multiserverthread");
        this.socket = socket;
        System.out.println(this.socket.getRemoteSocketAddress().toString() + " Conexion entrante");
    }

    public void run() {

        try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));) {
            String inputLine;
            Object outputLine;
            protocol protocolo = new protocol();


            //falta hacer la condicion en caso que uno de los sockets no conecte

            outputLine = protocolo.processInput(null, null);
            out.println(outputLine);
            // System.out.println("Esto llega: " + in.readLine());
            
            while ((inputLine = in.readLine()) != null) {
                System.out.println(inputLine.split(" ")[0]);
                outputLine = protocolo.processInput(inputLine, socket);
                out.println(outputLine);


                if (outputLine.equals("Bye"))
                    break;
            }
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}