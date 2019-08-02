package Servidor;
;
import java.net.*;
import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Hashtable;

import org.json.*;

public class protocol {
    private static final int WAITING = 0;
    private static final int validate_u = 1;
    private static final int validate_p = 2;
    private static final int sentcommands = 3;
    private static final int ANOTHER = 4;

    private String pass = "";
    private int state = validate_p;
    private int CurrentCommand = -1;
    private String disponibilidad = "";
    int portNumber = 6666;

    private String options_message = "\nEscoja una acción:\n" + "1- Mostrar videos disponibles\n" + "2- Reproducir Video\n" + "3- Salir de la App\n"+
            "4- Detener reproduccion actual";

    public Object processInput(String theInput, Socket socket) throws IOException {

        JSONObject theOutput = new JSONObject();
        ArrayList<String> directorio;
        if (state == validate_p) {
            try {
                theOutput.put("message", options_message);
                state = sentcommands;
            } catch (JSONException e) {
                e.getCause();
                System.out.println(e);
            }
        } else if (state == sentcommands) {

            if (theInput.equals("1")) {
                try {// Mostrar videos disponibles

                    System.out.println("Videos Disponibles:");

                    // System.out.println("working on =" + System.getProperty("user.dir"));

                    File folder = new File("./media");
                    File[] listOfFiles = folder.listFiles();
                    ArrayList<String> listOfFilesNames = new ArrayList<>();

                    for (int i = 0; i < listOfFiles.length; i++) {
                        System.out.println(listOfFiles[i].getName());
                        listOfFilesNames.add(listOfFiles[i].getName());

                    }

                    theOutput.put("message", disponibilidad + options_message);
                    System.out.println(listOfFilesNames);
                    theOutput.put("response", listOfFilesNames);
                    // System.out.println(theOutput);


                } catch (JSONException e) {
                    e.getCause();
                }
                CurrentCommand = 0;
                state = sentcommands;
            }
            else if (theInput.equals("2")) { // Reproducir Video
                BufferedReader scanner_play_video = new BufferedReader(new InputStreamReader(System.in));
                System.out.println("Escriba nombre de video a reproducir:");
                String video2play = scanner_play_video.readLine();
                // Abrir archivo y todo el tema para transmitirlo
            }

            else if (theInput.equals("3")) { // Salir de la App

            }

            else if (theInput.equals("4")) { // Detener reproduccion actual

            } else {
                System.out.println("Ingrese una accion valida");
                try {
                    theOutput.put("message", "Invalid command " + "Try again.\n" + options_message);
                } catch (JSONException e) {
                    e.getCause();
                }
            }
        }
        return theOutput;
    }
}