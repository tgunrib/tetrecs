package uk.ac.soton.comp1206.game;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.event.CommunicationsListener;
public class MultiplayerGame extends Game {

    private static final Logger logger = LogManager.getLogger(MultiplayerGame.class);
    private CommunicationsListener multigamelistener;
    public final ListProperty<Integer> pieces = new SimpleListProperty<>();
    private static int piecenum = 0;

    /**
     * Create a new game with the specified rows and columns. Creates a corresponding grid model.
     *
     * @param cols number of columns
     * @param rows number of rows
     */
    public MultiplayerGame(int cols, int rows) {
        super(cols, rows);

    }

    /**
     * Get piece from the list pieces given by the communicator and spawn the nextpiece
     * @return
     */
    @Override
    public GamePiece spawnPiece(){
        int piecenumber = pieces.get(piecenum);
        GamePiece play = GamePiece.createPiece(piecenumber);
        piecenum++;
        return play;

    }

    /**
     * Method to start game only after the pieces are added
     */
    @Override
    public void start(){
        logger.info("Starting game");
        initialiseGame();
        nextLoop();

        //creating timer
        score.set(0);
        setTimer();

        pieces.addListener((observableValue, integers, t1) -> {
            if(pieces.size() == 5){
                followingpiece = spawnPiece();
                nextPiece();
            }

        });


    }

    /**
     * Method to get the piecelist in the game that can be used for binding
     * @return
     */
    public ListProperty<Integer> pieceProperty() {
        return pieces;
    }


}
