package Servidor;

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

    String hostNameV1 = "10.6.40.183"; // Maquina 43
    String hostNameV2 = "10.6.40.184"; // Maquina 44
    boolean connectionVM = true;
    // int portNumberV1 = 4448;
    // int portNumberV2 = 4449;
    int portNumber = 4444;

    private String[] commands = { "ls", "get", "put", "delete" };

    public Object processInput(String theInput, Socket socket) throws IOException {

        JSONObject theOutput = new JSONObject();
        ArrayList<String> directorio;

        Hashtable<String, Boolean> VMs_connected = this.check_VM_connections();
        if (state == validate_p) {
            try {
                theOutput.put("message", "Write Command: ");
                state = sentcommands;
            } catch (JSONException e) {
                e.getCause();
                System.out.println(e);
            }
        } else if (state == sentcommands) {
            ServerSocket DserverSocket_get = new ServerSocket(4445);
            ServerSocket DserverSocket_put = new ServerSocket(4446);
            System.out.println("sockets creados");
            if (theInput.equalsIgnoreCase("ls")) {

                DserverSocket_get.close();
                DserverSocket_put.close();
                // System.out.println("ls here");
                try {
                    
                    theOutput.put("message", disponibilidad + " Write Command: ");
                    directorio = check_index(VMs_connected);
                    JSONArray dir = new JSONArray(directorio);
                    System.out.println(dir);
                    theOutput.put("response", dir);
                    // System.out.println(theOutput);
                    

                } catch (JSONException e) {
                    e.getCause();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                CurrentCommand = 0;
                state = sentcommands;

            } else if (theInput.split(" ")[0].equalsIgnoreCase("get")) {

                directorio = check_index(VMs_connected);
                boolean check = check_file(directorio, theInput.split(" ")[1]);
                if (check) {
                    System.out.println("VM  conectada");
                    disponibilidad = "[VMs activated]";

                    try {
                        DserverSocket_put.close();

                        Socket Dsocket = DserverSocket_get.accept();
                        

                        System.out.println("get here");

                        ArrayList<String> VMs_Sockets = new ArrayList<String>();
                        VMs_Sockets = sockets_hosts(theInput.split(" ")[1]);
                        

                        BufferedReader br = new BufferedReader(new FileReader("./src/Servidor/index_"+theInput.split(" ")[1]));
                        BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream("./src/Servidor/"+theInput.split(" ")[1]));
                        String st;
                        while((st = br.readLine()) != null){
                            String host = st.split(" ")[1];
                            Socket soket_get = new Socket(host,4444);
                            Socket get_get = new Socket(host,4445);
                            PrintWriter outmaq = new PrintWriter(soket_get.getOutputStream(), true);
                            outmaq.println("get "+theInput.split(" ")[1]);
                            BufferedInputStream input = new BufferedInputStream(get_get.getInputStream());


                        }
                        



                        download(theInput.split(" ")[1], Dsocket, theOutput);

                        theOutput.put("message", " Write Command: ");
                        DserverSocket_get.close();

                        
                    } catch (JSONException e) {
                        e.getCause();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    CurrentCommand = 1;
                    state = sentcommands;
                } else {
                    
                    System.out.println("file unable to access, maybe VM no conected");
                    connectionVM = false;
                    disponibilidad = "[file unable to access, maybe VM no conected]";
                    theOutput.put("message", disponibilidad + "\n Write Command: ");
                    CurrentCommand = 1;
                    state = sentcommands;
                    
                }

            } else if (theInput.split(" ")[0].equalsIgnoreCase("put")) {

                
                System.out.println("VM  conectada");
                connectionVM = true;
                disponibilidad = "[VM activated]";
                try {
                    DserverSocket_get.close();
                    
                    Socket Dsocket = DserverSocket_put.accept();
                    byte[] bytearray = new byte[65536];
                    int i;
                    BufferedInputStream input = new BufferedInputStream(Dsocket.getInputStream());
                    DataInputStream dis = new DataInputStream(Dsocket.getInputStream());
                    String file = dis.readUTF();

                    System.out.println("folder = " + System.getProperty("user.dir"));
                    BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream("./src/Servidor/"+file));
                    
                    FileWriter fileWriter = new FileWriter("./src/Servidor/index/index_"+file);


                    File ipes = new File("./src/Servidor/ips.txt");
                    BufferedReader br = new BufferedReader(new FileReader(ipes));

                    String st;
                    ArrayList <Socket> sockets = new ArrayList<Socket>();
                    ArrayList <String> sockets_host = new ArrayList<String>();
                    while((st = br.readLine()) != null){
                        try{
                            sockets.add(new Socket(st,4444));
                            sockets_host.add(st);
                        }
                        catch(IOException e){
                            System.out.println(st + " no pudo conectar");
                        }
                    }
                    
                    int j=0;
                    int k = 1;
                    while ((i = input.read(bytearray)) != -1) {
                        //output.write(bytearray, 0, i);
                        
                        PrintWriter outmaq = new PrintWriter(sockets.get(j).getOutputStream(), true);
                        outmaq.println("put "+file);
                        
                        Socket PSocket = new Socket(sockets_host.get(j),4446);

                        

                        BufferedOutputStream ou2 = new BufferedOutputStream(PSocket.getOutputStream());

                        DataOutputStream output2 = new DataOutputStream(PSocket.getOutputStream());

                        output2.writeUTF(file);
                        
                        //byte[] encoded = Base64.encode(bytearray);
                        ou2.write(bytearray,0,i);

                        ou2.close();
                        PSocket.close();
                        
                        

                        fileWriter.write(""+k+" "+sockets_host.get(j)+"\n");
                        ++j;
                        ++k;
                        if(j>=sockets.size()){
                            j=0;
                        }   
                    } 
                    fileWriter.close();
                    System.out.println("done");

                    int z =0;
                    for(z=0;z<sockets.size();++z){
                        sockets.get(z).close();
                    }
                    

                    
                    theOutput.put("message"," Write Command: ");
                    
                    output.close();
                    dis.close();
                    DserverSocket_put.close();

                }
                catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                CurrentCommand = 2;
                state = sentcommands;

            } else if (theInput.split(" ")[0].equalsIgnoreCase("delete")) {
                directorio = check_index(VMs_connected);
                boolean check = check_file(directorio, theInput.split(" ")[1]);
                ArrayList<String> VMs_Sockets = new ArrayList<String>();
                if (check) {
                    System.out.println("VM  conectada");
                    disponibilidad = "[VMs activated]";
                    VMs_Sockets = sockets_hosts(theInput.split(" ")[1]);
                    try {
                        DserverSocket_get.close();
                        DserverSocket_put.close();
                        
                        String file_name = theInput.split(" ")[1];
                        String path = "./src/Servidor/index/index_" + file_name;
                        File file = new File(path);

                        for (String hostNameVM : VMs_Sockets) {
                            
                            try (Socket socketVM = new Socket(hostNameVM, portNumber)) {
                                System.out.println("Conectado a la maquina de ip " + hostNameVM);
                                PrintWriter out = new PrintWriter(socketVM.getOutputStream(), true);
                                out.println(theInput);
                                if (file.delete()) {
                                   
                                    System.out.println(theInput);
        
                                    theOutput.put("message", file_name + " deleted of root directory" + " Write Command: ");
                                    System.out.println(file_name + " eliminado del directrio raiz");
                                } else{
                                    theOutput.put("message", "Done!, " + file_name + " not found!" + " Write Command: ");
                                }
                            } catch (IOException e) {
                                CurrentCommand = 3;
                                state = sentcommands;
                                System.out.println("No se ha podido conectar a la maquina de ip " + hostNameVM);
                            }

                        }

                    } catch (JSONException e) {
                        e.getCause();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    CurrentCommand = 3;
                    state = sentcommands;
                } else {
                    System.out.println("file unable to access, maybe VM no conected");
                    connectionVM = false;
                    disponibilidad = "[file unable to access, maybe VM no conected]";
                    theOutput.put("message", "file unable to be deleted of root directory" + " Write Command: ");
                    CurrentCommand = 3;
                    state = sentcommands;
                }

            } else {
                try {
                    DserverSocket_get.close();
                    DserverSocket_put.close();
                    theOutput.put("message", "Invalid command " + "Try again. Write Command: ");
                } catch (JSONException e) {
                    e.getCause();
                }
            }
        } else if (state == ANOTHER){
            if (theInput.equalsIgnoreCase("y")) {

                try {
                    // System.out.println("client says: yes");
                    theOutput.put("message", "Write Command: ");
                } catch (JSONException e) {
                    e.getCause();
                }
                CurrentCommand = -1;
                state = sentcommands;
            } else if (theInput.equalsIgnoreCase("n")) {
                try {
                    
                    theOutput.put("message", "Bye.");
                } catch (JSONException e) {
                    e.getCause();
                }
                state = WAITING;
            } else if (theInput.equalsIgnoreCase("gracias")) {
                try {
                    theOutput.put("message", "Want another action? (y/n)");
                } catch (JSONException e) {
                    e.getCause();
                }
                state = WAITING;
            } else {
                try {
                    // System.out.println("aaaaaa: " + theInput);
                    theOutput.put("message", "What do you say? ");
                } catch (JSONException e) {
                    e.getCause();
                }
            }
        }
        return theOutput;
    }
    private ArrayList<String> sockets_hosts(String file) throws FileNotFoundException {
        BufferedReader br = new BufferedReader(new FileReader("./src/Servidor/index/index_" + file));
        String line;
        ArrayList<String> VMs_Sockets = new ArrayList<String>();
        try {
            int cont = 0;
            String hostNameVM_1 = "";
            while ((line = br.readLine()) != null) {
                String hostNameVM = line.split(" ")[1];
                if (cont == 0) {
                    hostNameVM_1 = line.split(" ")[1];
                }else {
                    if ( hostNameVM_1.equalsIgnoreCase(hostNameVM)) {
                        break;
                    }
                }
                cont ++;
                VMs_Sockets.add(hostNameVM);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        };
        return VMs_Sockets;
    }
    private boolean check_file(ArrayList<String>dir, String file) {
        for ( String aux : dir) {
            if (aux.equalsIgnoreCase(file)) {
                return true;
            }
        }
        return false;

    }
    private ArrayList<String> check_index(Hashtable<String, Boolean> dicc) throws FileNotFoundException {
        File dir = new File("./src/Servidor/index");
        String[] ficheros = dir.list();
        ArrayList<String> ficheros_aux  = new ArrayList<String>();
        for (int i = 0; i < ficheros.length; i++) {
            System.out.println(ficheros[i]);
            BufferedReader br = new BufferedReader(new FileReader("./src/Servidor/index/" + ficheros[i]));
            String line;
            boolean aux_bool = true;
            try {
                while ((line = br.readLine()) != null) {
                    aux_bool = aux_bool && dicc.get(line.split(" ")[1]);
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            };
            if (aux_bool) {
                ficheros_aux.add(ficheros[i].split("index_")[1]);
            }
        }
        return ficheros_aux;
    }
    private Hashtable<String, Boolean> check_VM_connections() throws FileNotFoundException {
        Hashtable<String, Boolean> Vms_connected = new Hashtable<String, Boolean>();
        BufferedReader br = new BufferedReader(new FileReader("./src/Servidor/ips.txt"));
        try {
            String hostNameVM;
            while ((hostNameVM = br.readLine()) != null) {
                try (Socket SocketVM = new Socket(hostNameVM, portNumber);) {
                    System.out.println("conectado a la maquina de ip " + hostNameVM);
                    System.out.println(SocketVM.getLocalAddress());
                    Vms_connected.put(hostNameVM, true);
                    // SocketVM.close();
                } catch (IOException e) {
                    Vms_connected.put(hostNameVM, false);
                    System.out.println("No se ha podido conectar a la maquina de ip " + hostNameVM);
                }
            }
        } catch (IOException e) {
            System.out.println(e);
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return Vms_connected;
    }
    private String[] listar_directorio() {
        File dir = new File("./src/Servidor");
        String[] ficheros = dir.list();
        if (ficheros == null)
            System.out.println("No hay ficheros en el directorio especificado");
        return ficheros;
    }

    private void download(String archivo, Socket socket, JSONObject theOutput) {
        try {
            File file = new File("./src/Servidor/" + archivo);
            int i;
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
            BufferedOutputStream ou = new BufferedOutputStream(socket.getOutputStream());

            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
            output.writeUTF(file.getName());

            // PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            byte[] bytearray = new byte[8192];
            while ((i = in.read(bytearray)) != -1) {
                ou.write(bytearray, 0, i);
            }
            // theOutput.put("ready","Descargando.");
            // out.println(theOutput);

            in.close();
            ou.close();
            socket.close();

        } catch (IOException e) {
            System.out.println(e);
            e.getCause();
        }
    }
}