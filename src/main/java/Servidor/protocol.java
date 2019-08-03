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
                //Canvas para mostrar el video
                CanvasFrame canvas = new CanvasFrame("VideoCanvas");
                //setearlo para cerrar canvas
                canvas.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);

                FrameGrabber grabber = new OpenCVFrameGrabber ("src/main/java/Servidor/media/video4.mp4");

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
                    }


                }
                // Close the video file
                grabber.release();
                //BufferedReader scanner_play_video = new BufferedReader(new InputStreamReader(System.in));
                System.out.println("Escriba nombre de video a reproducir:");
                //String video2play = scanner_play_video.readLine();
                // Abrir archivo y todo el tema para transmitirlo


                //ou.close();
                //DSocket.close();
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
