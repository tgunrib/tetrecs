package uk.ac.soton.comp1206.component;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import uk.ac.soton.comp1206.game.GamePiece;
public class PieceBoard extends GameBoard {

    /**
     * Pieceboard which extends the Gameboard to hold currentpiece and  the nextpiece to be placed.
     * @param cols
     * @param rows
     * @param width
     * @param height
     */
    public PieceBoard(int cols, int rows, double width, double height) {
        super(cols, rows, width, height);
    }

    /**
     * Method to add pieces to the gameboard as they change
     * @param piece
     */
    public void addPiece(GamePiece piece){
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
               this.grid.set(i,j,piece.getBlocks()[i][j]);
            }
        }
    }

    /**
     * Override the method in the gameboard class to change the events when the mouse is clicked
     * To either rotate the block or swap the block
     * @param block
     */
    @Override
    public void mouseEvent(GameBlock block){
        block.setOnMouseClicked((e) -> {
            rightClicked(e);
            rotate(e, 1);
        });
    }


    /**
     * Method to create a circle at the centre of the current pieceboard
     */
    public void addCircle(){
        BorderPane pane = new BorderPane();
        Circle circle = new Circle();
        circle.setFill(Color.rgb(0,0,0,0.5));
        circle.setRadius(height/(rows*2));
        pane.setCenter(circle);
        add(pane, 1, 1);
    }


}
