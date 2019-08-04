package Servidor;

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
import java.util.concurrent.*;

import org.bytedeco.javacv.*;
import org.bytedeco.javacv.Frame;
import org.bytedeco.opencv.opencv_core.IplImage;
import org.json.*;

import javax.imageio.ImageIO;

public class receive_mult implements Runnable{
    BlockingQueue<String>queue = new LinkedBlockingQueue<String>();
    int port = 1;
    String host = "";

    public receive_mult(BlockingQueue<String> queue, int port, String host) {
        this.queue = queue;
        this.port = port;
        this.host = host;
    }

    

    public void run(){
        boolean flag = true;
        String message = "";
        while(flag){
            message = queue.poll();
            if(message != null){
                String[] partes = message.split(" ");
                String filename = partes[1];
                if(partes[0].equals("streaming")){
                    try{
                        Socket socket_mult = new Socket(host,port);
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket_mult.getInputStream()));

                        String video_enc;
                        CanvasFrame canvas = new CanvasFrame("VideoCanvas");
                        canvas.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
                        Java2DFrameConverter bimConverter = new Java2DFrameConverter();
                        while((video_enc = in.readLine()) != null){
                            byte[] decode = Base64.getDecoder( ).decode(video_enc);
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
                    }
                    catch(UnknownHostException e){
                        System.out.println(e);
                    }
                    catch(IOException e){
                        System.out.println(e);
                    }
                }
            }
        }
    }
}
