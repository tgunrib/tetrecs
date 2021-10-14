package uk.ac.soton.comp1206.scene;

import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.event.CommunicationsListener;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

import java.io.*;
import java.util.Comparator;

public class ScoresScene extends BaseScene {

    private final ObservableList<Pair<String, Integer>> scoreList = FXCollections.observableArrayList();
    private final ListProperty<Pair<String, Integer>> scoreProperty = new SimpleListProperty<>(scoreList);
    private final ObservableList<Pair<String, Integer>> remoteList= FXCollections.observableArrayList();
    private final ListProperty<Pair<String, Integer>> remoteScores= new SimpleListProperty<>(remoteList);
    private CommunicationsListener scorelistener;

    private static final Logger logger = LogManager.getLogger(MenuScene.class);
    protected ScoreList localscore;
    private ScoreList onlinescore;

    /**
     * Create a new scene, passing in the GameWindow the scene will be displayed in
     *
     * @param gameWindow the game window
     */
    public ScoresScene(GameWindow gameWindow) {
        super(gameWindow);
    }

    @Override
    public void initialise() {

        logger.info("Initialising Score scene");
        getScene().setOnKeyPressed((evt)->{
        if(evt.getCode() == KeyCode.ESCAPE) {
            gameWindow.getCommunicator().send("QUIT");
            gameWindow.startMenu();
        }});
    }

    @Override
    public void build() {

        root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());
        var pane = new BorderPane();
        root.getChildren().add(pane);
        pane.getStyleClass().add("menu-background");
        loadOnlineScores();

        Image tetre = new Image(ScoresScene.class.getResource("/images/TetrECS.png").toExternalForm());
        ImageView tetrecs = new ImageView(tetre);
        tetrecs.setFitWidth(400);
        tetrecs.setFitHeight(100);

        pane.setTop(tetrecs);
        var box = new HBox();

        setScoreList();
        onlinescore = new ScoreList();
        onlinescore.heading.setText("Online High Scores");
        onlinescore.scoreProperty().bind(remoteScores);

        box.getChildren().addAll(localscore,onlinescore);
        pane.setCenter(box);
        box.setAlignment(Pos.CENTER);

    }

    /**
     * Method to get scores from a file and add it to a list
     * @filename being the name of the file
     */
    private void loadScores(String filename) {
        try {
            logger.info("files");
            File file = new File(filename);
            if (!file.exists()) {
             file = new File("score.txt");
             writeScores(file);
             }

            BufferedReader filereader = new BufferedReader(new FileReader(filename));
            String score = filereader.readLine();
            logger.info("reading lines");
            while (filereader.ready()) {
            String[] scoresplit = score.split(":");
            Pair<String, Integer> userscore = new Pair<>(scoresplit[0], Integer.valueOf(scoresplit[1])) ;
            if(!scoreList.contains(userscore))
            scoreList.add(userscore);
            score = filereader.readLine();
             }
                filereader.close();
                scoreList.sort(Comparator.<Pair<String, Integer>>comparingInt(Pair::getValue).reversed());

        }catch(FileNotFoundException a){
            logger.info("there is an exception");
        }
        catch(IOException b){
            logger.info("there is an exception2");
        }

    }


    /**
     * Method to write scores to a file if required
     * @param file
     */
    private void writeScores(File file){
        try {
            if(!file.exists()) {
                file = new File("score.txt");
            }

            FileWriter scorewriter = new FileWriter(file);
            BufferedWriter filewriter= new BufferedWriter(scorewriter);
            for(Pair<String, Integer> towrite: scoreList){
                filewriter.write(towrite.getKey()+":"+towrite.getValue());
                filewriter.newLine();
            }
            filewriter.close();
        }
        catch (IOException a){
            logger.info("there is an exception3");

        }

    }

    /**
     * Method to get online scores from the communicator and add it to list
     */
    private void loadOnlineScores(){
         gameWindow.getCommunicator().send("HISCORES UNIQUE");
        gameWindow.getCommunicator().addListener((scorelistener)-> Platform.runLater(()->this.receiveCommunication(scorelistener)));
    }

    /**
     * Method that sorts out the highscore received from the communicator
     * @param communicate
     */
    private void receiveCommunication(String communicate) {
        if (communicate.startsWith("HISCORES")) {
            communicate = communicate.replace("HISCORES", "");
            String[] highScores = communicate.split("\\n");
            for (int i = 0; i < 10; i++) {
                String[] eachscore = highScores[i].split(":");
                remoteList.add(new Pair<String, Integer>(eachscore[0],Integer.parseInt(eachscore[1])));
            }
        }
    }

    /**
     * Method to set the scorelist in the
     */
    protected void setScoreList(){
        localscore = new ScoreList();
        localscore.heading.setText("Local High Scores");
        localscore.scoreProperty().bind(scoreProperty);
        loadScores("C:\\Users\\tosin\\OneDrive\\Documents\\Programming\\semester 2\\coursework\\scores.txt");
    }


}
