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

public class hear_conn implements Runnable{
    BlockingQueue<String> queue = new LinkedBlockingQueue<String>();
    int port = 0;

    public hear_conn(BlockingQueue<String> queue, int port) {
        this.queue = queue;
        this.port = port;
    }

    public void run(){
        boolean flag = true;
        String message;
        try{
            ServerSocket conn_socket = new ServerSocket(port);
            while(flag){
                //message = queue.poll();
                message = "a";
                if(message != null){
                    if(message.equals("close")){
                        flag = false;
                    }
                }
                new envio_datos(conn_socket.accept(), queue).start();
            }
            System.out.println("salio del while");
        }
        catch(IOException e){
            System.out.println(e);
        }
    }
}