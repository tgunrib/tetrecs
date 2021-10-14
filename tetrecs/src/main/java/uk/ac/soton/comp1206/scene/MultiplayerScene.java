package uk.ac.soton.comp1206.scene;

import javafx.application.Platform;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.game.MultiplayerGame;
import uk.ac.soton.comp1206.ui.GameWindow;
import java.util.*;


public class MultiplayerScene extends ChallengeScene{
    private static final Logger logger = LogManager.getLogger(ChallengeScene.class);
    /**
     * Observable list and SimpleList property for Gamepieces
     */
    private final ObservableList<Integer> pieceList = FXCollections.observableArrayList();
    private final SimpleListProperty<Integer> pieceProperty = new SimpleListProperty<>(pieceList);


    private final SimpleStringProperty usernameprop = new SimpleStringProperty();
    /**
     * HashMap to store scores and username while the game is playing
     */
    private final HashMap<String, Integer> scorestore = new HashMap();
    /**
     * Observable list and SimpleListproperty to bind to the scorescene when the game ends
     */
    private final ObservableList<Pair<String, Integer>> scoreList = FXCollections.observableArrayList();
    private final SimpleListProperty<Pair<String, Integer>> scores = new SimpleListProperty<>(scoreList);


    private Timer pieceTimer = new Timer();
    /**
     * Leaderboard that is created to be passed to the scorescene when the game ends
     */
    private LeaderBoard leaderBoard;

    /**
     * Text to store the name of the person your playing against
     */
    private Text rivalname ;
    /**
     * Create a new Single Player challenge scene
     *
     * @param gameWindow the Game Window
     */
    public MultiplayerScene(GameWindow gameWindow) {

        super(gameWindow);
    }

    /**
     * Override the game setup in the super class so it plays a multiplayer game instead of a singleplayer game
     */
    @Override
    public void setupGame(){
        logger.info("Setting up multiplier game");
        pieceTimer();
        gameWindow.getCommunicator().addListener((multilistener)->Platform.runLater(()->{
            if(multilistener.startsWith("PIECE")){
                listPiece(multilistener);
            }
            if (multilistener.startsWith("SCORES"))
                arrangeSCORES(multilistener);

        }));
        super.game = new MultiplayerGame(5,5);
        ((MultiplayerGame)super.game).pieceProperty().bind(pieceProperty);
        name = new Text("Multiplayer Mode");
        super.centre = new VBox();
        super.game.playGame.bind(super.playing);
        sendSCORE();
        addTextbox();
        Platform.runLater(()->{
            createRival();
        });
    }

    /**
     * Handles the string sent back when the communicator ask for a gamepiece
     * @param message
     */
    private void listPiece(String message){
        message = message.replace("PIECE","");
        message = message.trim();
        pieceList.add(Integer.valueOf(message));
    }

    /**
     * Method to add a listener for when the value of the score changes so the score can be sent to the communicator as well as
     * when the lives change a listener is also updated
     */
    private void sendSCORE(){
        super.game.getScore().addListener((observableValue, number, t1) -> gameWindow.getCommunicator().send("SCORE "+ t1));

        super.game.getLives().addListener((observableValue, number, t2) -> {
            if(t2.intValue() >= 0) {
                gameWindow.getCommunicator().send("LIVES " + t2);
                gameWindow.getCommunicator().send("PIECE");
            }

        });

    }

    /**
     * Method to call for pieces for the game when the game is first called
     */
    private void pieceTimer(){
        TimerTask task = new TimerTask() {
            public void run() {
                Platform.runLater(()->{
                    gameWindow.getCommunicator().send("SCORES");
                    gameWindow.getCommunicator().send("PIECE");
                    gameWindow.getCommunicator().send("PIECE");
                    gameWindow.getCommunicator().send("PIECE");
                    gameWindow.getCommunicator().send("PIECE");
                    gameWindow.getCommunicator().send("PIECE");
                });
            }};
        pieceTimer.schedule(task,0);

    }

    @Override
    protected void blockClicked(GameBlock gameBlock){
        super.game.blockClicked(gameBlock);
        StringBuilder block = new StringBuilder("BOARD ");
        for (int i = 0; i < board.getRowCount(); i++) {
            for (int j = 0; j < board.getColumnCount(); j++) {
                block.append(board.getBlock(i, j).getValue()).append(" ");
            }
        }
        gameWindow.getCommunicator().send(block.toString());
        gameWindow.getCommunicator().send("PIECE");
    }


    /**
     * Textfield to send messages during the game
     */
    public void addTextbox(){
        logger.info("creating message box");
        Text message = new Text("Click on this to send message");
        message.getStyleClass().add("messagetext");
        super.centre.getChildren().add(message);
        message.setOnMouseClicked((r)->{
                var messagebox = new TextField();
                super.centre.getChildren().add(messagebox);
                messagebox.setOnKeyPressed((n)->{
                    if(n.getCode() == KeyCode.ENTER){
                        gameWindow.getCommunicator().send("MSG " + messagebox.getText());
                        message.setText(usernameprop.get() + ":" + messagebox.getText());
                        super.centre.getChildren().remove(messagebox);
                    }
                });
        });
    }


    /**
     * Initialise the scene and start the game
     */
    @Override
    public void initialise() {
        logger.info("Initialising MultiChallenge");
        super.game.start();
        super.getScene().onKeyPressedProperty().bind(board.onKeyPressedProperty());
        getScene().addEventHandler(KeyEvent.KEY_PRESSED, evt -> {
            if(evt.getCode() == KeyCode.ESCAPE) {
                gameWindow.getCommunicator().send("QUIT");
                super.handle(evt);
            }
        });
    }

    public SimpleStringProperty getUsernameprop(){
        return usernameprop;
    }


    /**
     * Method to open the scores when the game ends
     */
    @Override
    protected void openScores(){
        for(Map.Entry<String, Integer> score: scorestore.entrySet()){
            scoreList.add(new Pair<String, Integer>(score.getKey(), score.getValue()));
        }
        super.playing.set(false);
        gameWindow.cleanup();
        gameWindow.startMenu();
        leaderBoard = new LeaderBoard(scores);
        gameWindow.startMultiplayerScoreScene(leaderBoard);
   }

    /**
     * Method to add scores to a hashmap which can be regualarly changed and later transformed into an observable arraylist for
     * the leaderboard
      * @param message
     */
   private void arrangeSCORES(String message){
        message = message.replace("SCORES", "");
        String[] individual = message.split("\\n");
        for(String indi: individual) {
            String[] score = indi.split(":", 3);
            if (Integer.parseInt(score[2]) < 0) {
                gameWindow.getCommunicator().send("DIE " + score[1]);
            }
            if (scorestore.containsKey(score[0])) {
                scorestore.replace(score[0], Integer.parseInt(score[1]));
            } else {
                scorestore.put(score[0], Integer.parseInt(score[1]));
            }
        }
        /**if(scorestore.size() > 0){
            rivalname.setText(String.valueOf(scorestore.get(0)));
        }
         */

    }

    private void createRival(){
       logger.info("creating rival name");
        var rivaltracker = new Text("Versus:");
        rivaltracker.getStyleClass().add("title");
        rivalname = new Text();
        rivalname.getStyleClass().add("rivalname");
        if(scorestore.size() == 0)
            rivalname.setText(usernameprop.get());
        tracking.getChildren().remove(0,6);
        tracking.getChildren().addAll(rivaltracker,rivalname);
    }





}
