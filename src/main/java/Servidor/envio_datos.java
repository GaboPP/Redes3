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
import org.bytedeco.javacv.FFmpegFrameGrabber;

import javax.imageio.ImageIO;

public class envio_datos extends Thread{
    BlockingQueue<String> queue = new LinkedBlockingQueue<String>();
    private Socket socket = null;

    public envio_datos(Socket socket, BlockingQueue<String> queue) {
        // super("multiserverthread");
        this.socket = socket;
        this.queue = queue;
        //probable haya que incluir mas cosas en lo que recibe
    }

    private static int findFreePort() {
		ServerSocket socket = null;
		try {
			socket = new ServerSocket(0);
			socket.setReuseAddress(true);
			int port = socket.getLocalPort();
			try {
				socket.close();
			} catch (IOException e) {
				// Ignore IOException on close()
			}
			return port;
		} catch (IOException e) { 
		} finally {
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
				}
			}
		}
		throw new IllegalStateException("Could not find a free TCP/IP port to start embedded Jetty HTTP Server on");
	}

    public void video_send(String Name, Socket socket, BlockingQueue<String> queue){
        CanvasFrame canvas = new CanvasFrame("VideoCanvas");
        //setearlo para cerrar canvas
        canvas.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);

        OpenCVFrameGrabber grabber = new OpenCVFrameGrabber ("src/main/java/Servidor/media/" + Name);//video4.mp4

        // Abriendo el vidio
        try{

        
            grabber.start ();
            IplImage imagen;
            Frame frame;
            String message = "";
            boolean flag = true;
            while (flag) {

                if((frame = grabber.grab()) == null){
                    flag = false;
                }
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

                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                out.println(encodedString);

                message = queue.poll();
                if(message != null){
                    if(message.equals("stop")){
                        flag = false;
                    }
                }
            }
            grabber.release();
        }
        
        catch(IOException e){
            System.out.println(e);
        }

        

    }

    public void run(){
        System.out.println("conexion entrante");
        boolean flag = true;
        String message = "";

        int trans_port = findFreePort();
        try{
            ServerSocket transmision = new ServerSocket(trans_port);
       
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(Integer.toString(trans_port));
            out.println("idle");
            while(flag){
                message = queue.poll();
                if(message != null){
                    if(message.contains("play")){
                        String movie = message.split(" ")[1];
                        out.println("streaming " + movie);
                        boolean flag1 = true ;
                        while(flag1){
                            out.println("streaming");

                            video_send(movie, transmision.accept(), queue);
                            message = "stop";
                            /*message = queue.poll();
                            if(message != null){
                                if(message.equals("stop")){
                                    flag1 = false;
                                }
                            }*/
                        }
                    }
                    if(message.equals("stop")){
                        out.println("stop");
                        out.println("idle");
                    }
                    else if(message.equals("close")){
                        out.println("close");
                    }
                }
            }
        }
        catch(IOException e){
            System.out.println(e);
        }
         
    }
}