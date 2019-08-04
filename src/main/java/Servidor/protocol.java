package Servidor;
;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.*;
import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Hashtable;
import java.net.Socket;
import java.util.List;

import org.bytedeco.javacv.*;
import org.bytedeco.javacv.Frame;
import org.bytedeco.opencv.opencv_core.IplImage;
import org.json.*;

import javax.imageio.ImageIO;

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
    String host =  "localhost"; //CAMBIAR SOLO USADO PARA TESTIN'!!!!!!!!!!!!!!
    public void descargar(String filename, Socket socket){
        String path = System.getProperty("user.dir");
        Socket dataSocket = null;
        BufferedReader dataIn = null;

        List<Socket> listDataSockets = new ArrayList<Socket>();
        List<BufferedReader> dataBuffers = new ArrayList<BufferedReader>();

        byte[] byteArray = new byte[64*1024*1024];
        String linea = null;
        String confirmacion = null;
        Base64.Decoder decoder = Base64.getDecoder();

        List<String> stringsBase64 = new ArrayList<String>();

        try{
            System.out.println ("FGSFDS");
            respuesta(socket, "COMIENZA LA DESCARGA");
            ServerSocket serverSocket = new ServerSocket(5900);
            Socket socketCliente = serverSocket.accept();

            String line = null;
            String[] parts = null;
            int totalSize = 0;
            String response = null;
            String[] address = null;
//            while ((line = br.readLine()) != null) {
//                parts = line.split(" ");
//                if (parts[0].equals(filename)){
//                    totalSize = Integer.parseInt(parts[1]);
//                } else {
//                    response = "El archivo no existe";
//                }
//            }
//            br.close();

            ByteArrayOutputStream byteWriter = new ByteArrayOutputStream();
            int index = 0;
            int counter = 0;

            respuesta(socketCliente, "DONE");

            for(int i = 0; i < listDataSockets.size(); ++i){
                if(listDataSockets.get(i).isConnected())
                    respuesta(listDataSockets.get(i), "DONE");
            }

            byteArray = byteWriter.toByteArray();

            OutputStream output = socketCliente.getOutputStream();
            output.write(byteArray, 0, totalSize);
            output.flush();

            for(int i = 0; i < listDataSockets.size(); ++i){
                if(listDataSockets.get(i).isConnected()){
                    (listDataSockets.get(i)).close();
                    (dataBuffers.get(i)).close();
                }
            }

            output.close();
            serverSocket.close();
            socketCliente.close();
            System.out.println("Done");

        } catch(IOException e){
            e.printStackTrace();
        }
    }

    public void respuesta(Socket socket, String mensaje){
        PrintStream response = null;

        try{
            response = new PrintStream(socket.getOutputStream());
            response.println(mensaje);
            response.flush();
        } catch(IOException e){
            e.printStackTrace();
        }
    }
    public void  show_video(String Name) throws IOException {
        //Canvas para mostrar el video
        CanvasFrame canvas = new CanvasFrame("VideoCanvas");
        //setearlo para cerrar canvas
        canvas.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);

        FrameGrabber grabber = new OpenCVFrameGrabber ("src/main/java/Servidor/media/" + Name);//video4.mp4

        // Abriendo el vidio
        grabber.start ();
        IplImage imagen;
        Frame frame;
        while ((frame = grabber.grab()) != null) {

            // frame se pueden convertir a bufferedimage
            Java2DFrameConverter bimConverter = new Java2DFrameConverter();
            BufferedImage img = bimConverter.convert(frame);
            BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
            Graphics2D bGr = bimage.createGraphics();
            bGr.drawImage(img, 0, 0, null);
            bGr.dispose();

            img.flush();
            //la buffered image a bytearrayoutputstream
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            // eso se mete en un imageIO
            ImageIO.write(bimage, "png", outputStream);
            //imageIO se puede pasar a bytearray
            String encodedString = Base64.getEncoder().encodeToString(outputStream.toByteArray());
            System.out.println (encodedString.length ());

            // MANDAR DATAGRAMAS
            // output.writeUTF(encodedString);

            byte[] decode = Base64.getDecoder( ).decode(encodedString);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(decode);
            BufferedImage Bimage = ImageIO.read (inputStream);
            Frame frame2 = bimConverter.convert(Bimage);

            // Set canvas size as per dimentions of video frame.
            // canvas.setCanvasSize(grabber.getImageWidth(), grabber.getImageHeight());
            if (frame2 != null) {
                //Show video frame in canvas
                canvas.showImage(frame2);
            }        }
        // Close the video file
        grabber.release();
    }
    private String options_message = "\nEscoja una acción:\n" + "1- Mostrar videos disponibles\n" + "2- Reproducir Video\n" + "3- Salir de la App\n"+
            "4- Detener reproduccion actual";

    public Object processInput(String theInput, Socket socket) throws IOException {
        System.out.println (theInput);
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
                    System.out.println("working on =" + System.getProperty("user.dir"));

                    File folder = new File("./src/main/java/Servidor/media");
                    File[] listOfFiles = folder.listFiles();
                    ArrayList<String> listOfFilesNames = new ArrayList<>();

                    for (int i = 0; i < listOfFiles.length; i++) {
                        listOfFilesNames.add(listOfFiles[i].getName());
                    }

                    theOutput.put("message", disponibilidad + options_message);
                    System.out.println(listOfFilesNames);
                    theOutput.put("response", listOfFilesNames);


                } catch (JSONException e) {
                    e.getCause();
                }
                CurrentCommand = 0;
                state = sentcommands;
            }
            else if (theInput.split (" ")[0].equals("2")) { // Reproducir Video
                String Name = theInput.split (" ")[1];
                descargar (Name, socket);
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
