package uk.ac.soton.comp1206.scene;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Pos;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.Utility.Multimedia;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBlockCoordinate;
import uk.ac.soton.comp1206.component.GameBoard;
import uk.ac.soton.comp1206.component.PieceBoard;
import uk.ac.soton.comp1206.event.CommunicationsListener;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

import java.io.*;
import java.util.Queue;
import java.util.Set;

/**
 * The Single Player challenge scene. Holds the UI for the single player challenge mode in the game.
 */
public class ChallengeScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(ChallengeScene.class);
    private BooleanProperty audio = new SimpleBooleanProperty();
    protected Game game;
    protected PieceBoard nextboard;
    protected PieceBoard followingboard;
    protected GameBoard board;
    private BorderPane mainPane;
    private Text highscore;
    protected Text name;
    protected VBox centre;
    protected Multimedia gamemusic;
    protected StackPane challengePane;
    protected VBox tracking;
    protected BooleanProperty playing = new SimpleBooleanProperty(true);
    /**
     * Create a new Single Player challenge scene
     *
     * @param gameWindow the Game Window
     */
    public ChallengeScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating Challenge Scene");
        gamemusic = new Multimedia();
    }

    /**
     * Build the Challenge window
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());

        setupGame();
        gamemusic.setAudioEnabled(true);
        gamemusic.playBackgroudmusic("game.wav");

        //creating rootpane
        root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());
        challengePane = new StackPane();
        challengePane.setMaxWidth(gameWindow.getWidth());
        challengePane.setMaxHeight(gameWindow.getHeight());
        challengePane.getStyleClass().add("menu-background");
        root.getChildren().add(challengePane);

        //creating borderpane for game and properties to add to the  stackpane
        mainPane = new BorderPane();
        challengePane.getChildren().add(mainPane);

        //creating the gameboard for the game
        board = new GameBoard(game.getGrid(), gameWindow.getWidth() /2, gameWindow.getWidth() / 2);
        centre.getChildren().add(0,board);
        centre.setAlignment(Pos.CENTER);
        mainPane.setCenter(centre);
        var boxtracker = new VBox();
        heading();

        //creating the pieceboards for the current and next piece display
        nextboard = new PieceBoard(3, 3, 100, 100);
        nextboard.addCircle();
        followingboard = new PieceBoard(3, 3, 90, 90);

        //Adding listeners for the game and board
        game.setNextpieceListener(this::Nextpiece);
        game.setOnLineCleared(this::LineCleared);
        game.setGameLoopListener(this::gameloop);

        scoreTracker();
        boxtracker.setSpacing(20);
        boxtracker.getChildren().addAll(tracking, nextboard, followingboard);

        //Handle block on gameboard grid being clicked
        board.setOnBlockClick(this::blockClicked);
        board.setOnRightClicked(this::rightClicked);
        board.setOnRotate(this::rotateCurrentPiece);

        //Handle when any of the pieceboard is being clicked
        followingboard.setOnRightClicked(this::rightClicked);
        nextboard.setOnRotate(this::rotateCurrentPiece);

        mainPane.setAlignment(tracking, Pos.CENTER_RIGHT);
        mainPane.setRight(boxtracker);

    }

    /**
     * Handle when a block is clicked
     *
     * @param gameBlock the Game Block that was clocked
     */
    protected void blockClicked(GameBlock gameBlock) {
        game.blockClicked(gameBlock);
    }

    /**
     * Handle when a board is clicked to rotate piece
     */
    private void rightClicked() {
        Multimedia.playAudio("rotate.wav");
        game.swapCurrentpiece();
    }

    /**
     * Handle when the nextboard containing the current piece is to be rotated
     * @param m
     */
    private void rotateCurrentPiece(int m) {
        logger.info("piece rotating");
        Multimedia.playAudio("rotate.wav");
        game.rotateCurrentpiece(m);
    }

    private void LineCleared(Set<GameBlockCoordinate> clear) {
        board.fadeOut(clear);
    }

    /**
     * Create timeline for a progress bar such that it is continuous and resets when the timer is reset
     * @param delay
     */
    private void gameloop(double delay){
        logger.info("progress bar reset" );
        ProgressBar timer = new ProgressBar();
        timer.setPrefWidth(root.getWidth());
        mainPane.setBottom(timer);
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(timer.progressProperty(), 1)),
                new KeyFrame(Duration.millis(delay), e-> {
                }, new KeyValue(timer.progressProperty(), 0))
        );
        timeline.play();
        if(game.getLives().get() < 0){
            sendScore("Tosin", new File("C:\\Users\\tosin\\OneDrive\\Documents\\Programming\\semester 2\\coursework\\scores.txt"));
            Platform.runLater(()->{
                openScores();
                gamemusic.setAudioEnabled(false);
            });
        }

    }

    /**
     * Setup the game object and model
     */
    public void setupGame() {
        logger.info("Starting a new challenge");

        //Start new game
        game = new Game(5, 5);
        name = new Text("Challlenge Mode");
        centre = new VBox();
        game.playGame.bind(playing);
        playing.set(true);

    }

    /**
     * Initialise the scene and start the game
     */
    @Override
    public void initialise() {
        logger.info("Initialising Challenge");
        game.start();

        //Bind the keypressed to the gameboard as well as allow reversion back to menuscene when escape is pressed
        getScene().onKeyPressedProperty().bind(board.onKeyPressedProperty());
        getScene().addEventHandler(KeyEvent.KEY_PRESSED, this::handle);
    }

    /**
     * Create a vbox that tracks the scores,levels and highest local score
     * @return
     */
    protected void scoreTracker() {
        tracking = new VBox();
        var highscores = new VBox();
        var highscorelabel = new Text("High Score");
        highscorelabel.getStyleClass().add("title");
        tracking.getChildren().add(highscorelabel);
        highscore = new Text();
        highscore.getStyleClass().add("score");
        getHighScore("C:\\Users\\tosin\\OneDrive\\Documents\\Programming\\semester 2\\coursework\\scores.txt");
        highscores.getChildren().add(highscore);
        tracking.getChildren().add(highscores);

        var levellabel = new Text("level");
        levellabel.getStyleClass().add("title");
        var leveltext = new Text();
        leveltext.getStyleClass().add("level");
        leveltext.textProperty().bind(game.getLevel().asString());
        tracking.getChildren().addAll(levellabel, leveltext);

        var multiplier = new Text("Multiplier");
        multiplier.getStyleClass().add("title");
        var multipliertext = new Text();
        multipliertext.getStyleClass().add("score");
        multipliertext.textProperty().bind(game.getMultiplier().asString());
        tracking.getChildren().addAll(multiplier, multipliertext);


    }

    /**
     * Create VBox to store the score,lives and mode of the game
     */
    protected void heading(){
        HBox scorelives = new HBox();
        VBox scores = new VBox();
        var scorelabel = new Text("Score");
        scorelabel.getStyleClass().add("title");
        var scoretext = new Text();
        scoretext.getStyleClass().add("score");
        scoretext.textProperty().bind(game.getScore().asString());
        scores.getChildren().addAll(scorelabel, scoretext);

        VBox lives = new VBox();
        var livesLabel = new Text("Lives");
        livesLabel.getStyleClass().add("title");
        var livestext = new Text();
        livestext.getStyleClass().add("lives");
        livestext.textProperty().bind(game.getLives().asString());
        lives .getChildren().addAll(livesLabel, livestext);

        name.getStyleClass().add("bigtitle");
        scorelives.getChildren().addAll(scores,name,lives);
        scorelives.setSpacing(40);
        mainPane.setTop(scorelives);
    }

    /**
     * method to add the nextpiece and the piece after to their respective pieceboard
     * @param piece
     * @param follow
     */
    public void Nextpiece(GamePiece piece, GamePiece follow) {
        logger.info("Current piece:" + piece.toString());
        nextboard.addPiece(piece);
        followingboard.addPiece(follow);
    }


    /**
     * Opens the scorescene when the game ends
     */
    protected void openScores(){
        playing.set(false);
        gamemusic.audioEnabledProperty.set(false);
        gameWindow.cleanup();
        gameWindow.startScores();
    }

    /**
     * Method to getHighScore
     */
    protected void sendScore(String username,File file){
        logger.info("Sending score");
        try {
            FileWriter scorewriter = new FileWriter(file,true);
            BufferedWriter filewriter= new BufferedWriter(scorewriter);
            filewriter.newLine();
            filewriter.append(username + ":" + game.getScore().get());
            filewriter.close();
        }
        catch (IOException a){
            logger.info("there is an exception3");

        }

    }

    /**
     * Method  to get the highscore and add to the localfile
     * @param filename
     */
    private void getHighScore(String filename) {
        try {
            BufferedReader filereader = new BufferedReader(new FileReader(filename));
            String score = filereader.readLine();
            String[] high = score.split(":");
            highscore.setText(high[1]);
            if(Integer.valueOf(high[1])  < game.getScore().get()){
                highscore.textProperty().bind(game.getScore().asString());
            }
        } catch (FileNotFoundException e) {
        } catch (IOException a) {
        }

    }

    /**
     * Method to handle when the escape button is pressed
     * @param evt
     */
    protected void handle(KeyEvent evt) {
        if (evt.getCode() == KeyCode.ESCAPE){
            playing.set(false);
            gamemusic.audioEnabledProperty.set(false);
            gameWindow.cleanup();
            gameWindow.startMenu();
        }
    }
}
