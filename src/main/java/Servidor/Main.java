package Servidor;


import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class Main {

    public static void main(String[] args) throws MalformedURLException {
        String media = "src/com/company/media/video4.mp4";
        // System.out.println("Working Directory = " + System.getProperty("user.dir"));
        URL url = null;
        try {
            url = new URL("file:///" +  new File(media).getCanonicalPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer window = new mediaPlayer(url);
    }
}
