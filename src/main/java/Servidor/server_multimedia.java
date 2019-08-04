package Servidor;

import java.net.*;
import java.io.*;
import java.util.concurrent.*;

public class server_multimedia implements Runnable{
    private Socket socket = null;

    BlockingQueue<String> queue = new LinkedBlockingQueue<String>();

    server_multimedia(BlockingQueue<String> queue) {
        this.queue = queue;
    }


    //aqui hay que hacer el trabajo de sockets para que se mantenga escuchando por conexiones
    //idealmente usar todo este codigo para la implementacion del cliente como servidor

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

    public void run(){
        int puerto = findFreePort();
        //System.out.println("El puerto libre encontrado es:" + puerto);
        //BlockingQueue<String> queue = new LinkedBlockingQueue<String>();
        try{
            queue.put(Integer.toString(puerto));
        }
        catch(InterruptedException e){
            System.out.println(e);
        }
        synchronized (this) {
            notifyAll(); 
        } 
        int portmult = puerto;
        try (ServerSocket serverSocket = new ServerSocket(portmult)) {
            boolean flag = true;
            String comando;
            
            while(flag){
                new envio_datos(serverSocket.accept(),queue);
                Socket serv = new Socket();
                PrintWriter out = new PrintWriter(serv.getOutputStream(), true);
                System.out.println("entrando a espera de comando");
                if((comando = queue.poll()) != null){
                    if(comando.equals("play")){
                        System.out.println("esperando comando");
                        String video = queue.poll();
                        //iniciar reproduccion de video
                        System.out.println("Se recibio un play");
                    }
                    else if(comando.equals("stop")){
                        //parar reproduccion
                        out.println("stop");
                        System.out.println("se recibio un stop");
                        
                    }
                    else if(comando.equals("salir")){
                        //detener servidor, mensajes a clientes y cerrar
                        out.println("close");
                        System.out.println("Se recibio un close");
                    }
                }
                int i = 0;
                
                //PrintWriter out = new PrintWriter(serv.getOutputStream(), true);
                out.println("idle");
            }
        }
        catch (IOException e) {
            System.out.println(e);
        }
    }

}