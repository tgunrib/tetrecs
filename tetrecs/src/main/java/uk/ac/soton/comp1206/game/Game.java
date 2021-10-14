package uk.ac.soton.comp1206.game;

import javafx.application.Platform;
import javafx.beans.property.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.Utility.Multimedia;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBlockCoordinate;
import uk.ac.soton.comp1206.event.*;
import java.util.*;

/**
 * The Game class handles the main logic, state and properties of the TetrECS game. Methods to manipulate the game state
 * and to handle actions made by the player should take place inside this class.
 */
public class Game {

    private static final Logger logger = LogManager.getLogger(Game.class);

    //create integer properties for  lives, level, score and  multiplier
    protected final IntegerProperty score = new SimpleIntegerProperty(0);
    protected final IntegerProperty multiplier = new SimpleIntegerProperty(1);
    protected final IntegerProperty level = new  SimpleIntegerProperty(0);
    protected final IntegerProperty lives = new SimpleIntegerProperty(3);

    /**
     * Create sounds for blocks
     */
    private Multimedia sounds;

    /**
     * Number of rows
     */
    protected final int rows;

    /**
     * Number of columns
     */
    protected final int cols;

     //The grid model linked to the game
    protected final Grid grid;

    /**
     * Generating a random number between 0 and 14
     */
      Random random = new Random();


    /**
     *Gamepiece field to keep track of the gamepiece
     */
      protected  GamePiece currentPiece;
      protected GamePiece followingpiece;

      private int multiply;

      private ArrayList<NextpieceListener> listeners = new ArrayList<NextpieceListener>();
      private GameLoopListener list;
      private LineClearedListener lineClearedListener;
      private Timer timer;
      public BooleanProperty playGame = new SimpleBooleanProperty(true);


    /**
     * Create a new game with the specified rows and columns. Creates a corresponding grid model.
     * @param cols number of columns
     * @param rows number of rows
     */
    public Game(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;

        //Create a new grid model to represent the game state
        this.grid = new Grid(cols,rows);
    }


    /**
     * Start the game
     */
    public void start() {
        logger.info("Starting game");
        initialiseGame();
        followingpiece = spawnPiece();
        nextPiece();
        nextLoop();
        //creating timer
        score.set(0);
        setTimer();
    }

    /**
     * Initialise a new game and set up anything that needs to be done at the start
     */
    public void initialiseGame() {
        logger.info("Initialising game");
    }

    /**
     * Handle what should happen when a particular block is clicked
     * @param gameBlock the block that was clicked
     */
    public void blockClicked(GameBlock gameBlock) {
        //Get the position of this block
        int x = gameBlock.getX();
        int y = gameBlock.getY();

        if (!grid.canPlayPiece(currentPiece, gameBlock.getX(), gameBlock.getY())){
            logger.info("Cannot play piece");
            sounds.playAudio("fail.wav");
        }
        else {
            //play the piece and spawn a new piece
            setTimer();
            nextLoop();
            grid.playPiece(currentPiece, gameBlock.getX(), gameBlock.getY());
            sounds.playAudio("place.wav");
            nextPiece();
            setMultiplier();

       }
    }

    /**
     * Get the grid model inside this game representing the game state of the board
     * @return game grid model
     */
    public Grid getGrid() {
        return grid;
    }

    /**
     * Get the number of columns in this game
     * @return number of columns
     */
    public int getCols() {
        return cols;
    }

    /**
     * Get the number of rows in this game
     * @return number of rows
     */
    public int getRows() {
        return rows;
    }

    /**
     * return the gamepiece being created;
     */
    public GamePiece spawnPiece(){
          int gamepiecenumber = random.nextInt(15);
         return GamePiece.createPiece(gamepiecenumber);
    }

    /**
     * crete a new gamepiece
     */
    public void nextPiece() {
        currentPiece = followingpiece;
        followingpiece = spawnPiece();
        Nextpiece(this.currentPiece,this.followingpiece);
        logger.info("piece name:"  + currentPiece.toString());
    }

    /**
     * create a boolean method that checks the value of each block in a row
     * and return false if one of the blocks is not coloured
     * @param a
     * @return
     */
    public boolean checkRow(int a){
        for (int i = 0; i < getCols(); i++) {
            if (grid.get(i, a) == 0)
                return false;

        }
        return true;

    }
    public boolean checkColumn(int a){
       // logger.info("Checking row 0");
        for (int i = 0; i < getRows(); i++) {
            if (grid.get(a,i) == 0)
                return false;
        }
        return true;
    }


    /**
     * Clear rows and columns after playing if they are full
     */
    public boolean afterPiece(){
        HashSet<GameBlockCoordinate> toclear = new HashSet<GameBlockCoordinate>();
        boolean multiply = false;
        int lines = 0;
        for (int a = 0; a < getRows(); a++) {
            //clear only the rows
            if(checkRow(a)) {
                logger.info("row cleared");
                for (int i = 0; i < getCols(); i++) {
                    toclear.add(new GameBlockCoordinate(i,a));
                }
                sounds.playAudio("clear.wav");
                lines++;
                multiply = true;
            }

            //clear only the columns
            if(checkColumn(a)) {
                logger.info("column cleared");
                for (int i = 0; i < getCols(); i++) {
                    toclear.add(new GameBlockCoordinate(a,i));
                }
                sounds.playAudio("clear.wav");
                lines++;
                multiply = true;
            }
            LineCleared(toclear);
        }
        //setting the score,level and multiplier after each play
        setScore(lines);
        setLevel();
        return multiply;
    }


    public IntegerProperty getScore(){
        return score;
    }
    public IntegerProperty getLives(){
        return lives;
    }
    public IntegerProperty getLevel(){
        return level;
    }
    public IntegerProperty getMultiplier(){
        return multiplier;
    }

    /**
     * Change the score depending on the number of lines
     * @param addLines
     */
    public void setScore(int addLines){
        score.set(score.get() + (10 * multiplier.get() * addLines));
    }

    /**
     * Change the level depending on the score
      */
    public void setLevel(){
        if(score.get() >= 1000){
            level.set(level.get() + 1);
            sounds.playAudio("level.wav");
        }
    }

    /**
     * Increase the multipier or set it to one when called
     */
    public void setMultiplier(){
        if(afterPiece() == true){
            multiplier.set(multiplier.get()+1);
        }
        else{
            multiplier.set(1);
        }
    }

    /**
     * Set listener for the nextpiece
     * @param listener
     */
    public void setNextpieceListener(NextpieceListener listener){
        listeners.add(listener);
    }

    /**
     * Triggered when a nextpiece is to be placed. Call the attached listener.
     * @param piece current piece
     * @param follow piece that follows after
     */
    public void Nextpiece(GamePiece piece, GamePiece follow){
        // Notify everybody that may be interested.
            for (NextpieceListener hl : listeners) {
                hl.Nextpiece(piece, follow);
            }

    }

    /**
     * rotates the pieces when the rotatelistener is triggered
     * @param m
     */
    public void rotateCurrentpiece(int m){
        currentPiece.rotate(m);
       Nextpiece(currentPiece,followingpiece);
    }


    /**
     * Swap the currentpiece when the right clicked listener is called
     * by storing currentpiece in a temporary location a
     */
    public void swapCurrentpiece(){
        GamePiece a  = currentPiece;
        currentPiece = followingpiece;
        followingpiece = a;
        Nextpiece(currentPiece,followingpiece);
    }

    /**
     * Triggered when a row or column is ful. Call the attached listener.
     * @param clear block of coordinates to clear
     */
    public void LineCleared(Set<GameBlockCoordinate> clear) {

        if (lineClearedListener != null) {
            lineClearedListener.fadeOut(clear);
        }
    }

    public void setOnLineCleared(LineClearedListener listener) {
        this.lineClearedListener = listener;
    }

    /**
     * Method to create the time delay for every timer at a certain level
     * With the minimum time being 2.5 seconds
     */
    public int getTimerDelay(){
       return Math.max(2500, (12000-(500*level.get())));
    }

    /**
     * Method that is called when the block has been clicked or the timer has been reset
     */
    public void gameLoop(){
        nextPiece();
        setMultiplier();
        lives.set(lives.get()-1);
        sounds.playAudio("lifelose.wav");
    }

    /**
     *
     * @param listener
     */
    public void setGameLoopListener(GameLoopListener listener){
        this.list = listener;
    }

    /**
     * Method when the gameloop listener is called
     */
    protected void nextLoop(){
        if(list != null)
            list.gameloop(getTimerDelay());
    }

    /**
     * Timer for the time given to play the next piece
     */
    public void setTimer(){
        logger.info("new timer created");
        if(timer != null)
            timer.cancel();
        timer = new Timer("Timer");
        TimerTask task = new TimerTask() {
            public void run() {
                Platform.runLater(()->{
                    if(!playGame.get()){
                        timer.cancel();
                        timer.purge();
                    }else{
                        gameLoop();
                        nextLoop();
                    }
                });
            }};
        timer.schedule(task,getTimerDelay(),getTimerDelay());
    }




}










