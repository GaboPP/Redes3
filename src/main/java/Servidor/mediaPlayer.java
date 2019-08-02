package Servidor;


import java.awt.BorderLayout;
import java.awt.Component;
import java.io.IOException;
import java.net.URL;
import javax.media.CannotRealizeException;
import javax.media.Manager;
import javax.media.NoPlayerException;
import javax.media.Player;
import javax.swing.JPanel;

public class mediaPlayer extends JPanel {
    private static final long serialVersionUID = 1L;
    public mediaPlayer(URL mediaURL) {
        setLayout(new BorderLayout());
        Manager.setHint(Manager.LIGHTWEIGHT_RENDERER, true);

        try {
            Player mediaPlayer_window = Manager.createRealizedPlayer(mediaURL);

            Component video = mediaPlayer_window.getVisualComponent();
            Component controls = mediaPlayer_window.getControlPanelComponent();

            if (video != null)
                add(video, BorderLayout.CENTER);
            if (controls != null)
                add(controls, BorderLayout.SOUTH);
            mediaPlayer_window.start();
        }
        catch (NoPlayerException | CannotRealizeException | IOException e) {
            e.printStackTrace();
        }
    }
}
