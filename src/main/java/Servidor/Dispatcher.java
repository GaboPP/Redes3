package Servidor;
import java.io.*;

public class Dispatcher {
    public static void main(String[] args) throws Exception {

        String currentDirectory = System.getProperty("user.dir");
        System.out.println("The current working directory is " + currentDirectory);

        BufferedReader scanner = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Escoja una opcion:");
        System.out.println("1- Servidor");
        System.out.println("2- Cliente");

        String opcion_ejec = scanner.readLine();
        if (opcion_ejec.equals("1")){
            System.out.println("servidor");
            multiserver Servidor = new multiserver();
            multiserver.run();
        }else if(opcion_ejec.equals("2")){
            Client cliente = new Client();
            cliente.run();
        }

    }
}