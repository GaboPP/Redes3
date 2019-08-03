package Servidor;


import org.bytedeco.javacv.*;
import org.bytedeco.javacv.Frame;
import org.bytedeco.opencv.opencv_core.IplImage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;

public class Main {

    public static void main(String[] args) throws IOException {
        //Create canvas frame for displaying video.
       CanvasFrame canvas = new CanvasFrame("VideoCanvas");
        //Set Canvas frame to close on exit
       canvas.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);

        FrameGrabber grabber = new OpenCVFrameGrabber ("src/main/java/Servidor/media/video4.mp4");

        // Open video video file
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
    }
}
