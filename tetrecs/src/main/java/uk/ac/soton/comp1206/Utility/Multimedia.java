package uk.ac.soton.comp1206.Utility;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Multimedia {

    private static final Logger logger = LogManager.getLogger(Multimedia.class);
    /**
     * Boolean property that tells if music can be played or not
     */
    public static final BooleanProperty audioEnabledProperty = new SimpleBooleanProperty(true);
    private static MediaPlayer audio;
    public static MediaPlayer media;


    /**
     * Method to play all other audio except the background music
     */
    public Multimedia(){
        audioEnabledProperty.addListener((observableValue, aBoolean, t1) -> stopMusic());
    }
    public static void playAudio(String file){
        if(!getAudioEnabled()) return;
            String toPlay = Multimedia.class.getResource("/sounds/" + file).toExternalForm();
            logger.info("Playing audio: " + toPlay);
                try {
                    Media play = new Media(toPlay);
                    audio = new MediaPlayer(play);
                    audio.play();

                } catch (Exception e) {
                    setAudioEnabled(false);
                    e.printStackTrace();
                    logger.error("Unable to play audio file, disabling audio");
                }


    }

    /**
     * Method to play the background music from a file on a continuous loop
     */
    public static void playBackgroudmusic(String file){
        if(!getAudioEnabled()) return;
        String toPlay = Multimedia.class.getResource("/music/" + file).toExternalForm();
        logger.info("Playing audio: " + toPlay);

            try {
                Media play = new Media(toPlay);
                media = new MediaPlayer(play);
                media.play();
                media.setCycleCount(MediaPlayer.INDEFINITE);

                if(!getAudioEnabled()){
                    logger.info("music stopped");
                    media.stop();
                }

            } catch (Exception e) {
                setAudioEnabled(false);
                e.printStackTrace();
                logger.error("Unable to play audio file, disabling audio");
            }


    }

    /**
     * Method to change audioenabledproperty from another method
     * @return
     */
    public static BooleanProperty audioEnabledProperty(){
        return audioEnabledProperty;
    }

    /**
     * Method to set the boolean of  audioenabled
     * @param enabled
     */
    public static void setAudioEnabled(boolean enabled) {
        logger.info("Audio enabled set to: " + enabled);
        audioEnabledProperty().set(enabled);
    }

    /**
     * Method to get the audionenabled boolean
     * @return
     */
    public static boolean getAudioEnabled() {
        return audioEnabledProperty().get();
    }

    /**
     * Method to stop the music
     */
    public void stopMusic(){
        if(!getAudioEnabled() && media!=null){
            logger.info("stopping music");
            media.stop();
            media.dispose();
        }
    }

}
