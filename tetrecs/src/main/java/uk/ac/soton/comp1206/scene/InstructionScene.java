package uk.ac.soton.comp1206.scene;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.Utility.Multimedia;
import uk.ac.soton.comp1206.component.GameBoard;
import uk.ac.soton.comp1206.component.PieceBoard;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * Instructionscene class which extends the basescene
 * It displays the different gamepieces as well as the key that play  the game
 */
public class InstructionScene extends BaseScene{

    private static final Logger logger = LogManager.getLogger(MenuScene.class);
    private Multimedia elevatormusic = new Multimedia() ;


    public InstructionScene(GameWindow window){
        super(window);
    }

    /**
     * Initialise the game and setup action to revert back to the menuscene when the
     * Escape key is pressed
     */
    @Override
    public void initialise() {
        elevatormusic.playBackgroudmusic("game.wav");
        getScene().setOnKeyPressed((m)->{
            if(m.getCode() == KeyCode.ESCAPE){
                elevatormusic.setAudioEnabled(false);
                gameWindow.cleanup();
                gameWindow.startMenu();
            }
        });


    }

    /**
     * method to build the instruction page
     */
    @Override
    public void build() {

        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());
        var pane = new ScrollPane();
        var instructionbox = new VBox();
         instructionbox.getStyleClass().add("instruction");

        instructionbox.getChildren().addAll(setPieces(), setImage());
        pane.setContent(instructionbox);
        root.getChildren().add(pane);
    }

    /**
     * Adds the instruction images to the instruction page
     * @return
     */
    private ImageView setImage(){
        String toPlay = InstructionScene.class.getResource("/images/instructions.png").toExternalForm();
        Image img = new Image(toPlay);
        ImageView tetre= new ImageView(img);
        tetre.setFitWidth(gameWindow.getWidth());
        tetre.getStyleClass().add("instruction");
        return tetre;
    }


    /**
     * Method to add all Gamepiece to the instructionpage
     * @return
     */
    private GridPane setPieces(){
        int a = 0;

        GridPane pieces = new GridPane();

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 3; j++) {
                PieceBoard piece = new PieceBoard(3,3,gameWindow.getWidth()/10,gameWindow.getHeight()/6);
                VBox piecebx = new VBox();
                piecebx.setPrefHeight(gameWindow.getWidth()/5);
                piecebx.setPrefSize(gameWindow.getWidth()/5, gameWindow.getHeight()/3);
                Label label = new Label(GamePiece.createPiece(a).toString());
                label.getStyleClass().add("instruction");
                piece.addPiece(GamePiece.createPiece(a));
                piecebx.getChildren().addAll(piece,label);
                piece.setAlignment(Pos.CENTER);
                pieces.add(piecebx, i,j);
                a++;
            }

        }
        return pieces;
    }

}


