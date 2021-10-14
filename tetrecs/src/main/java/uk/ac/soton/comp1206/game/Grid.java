package uk.ac.soton.comp1206.game;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import uk.ac.soton.comp1206.Utility.Multimedia;
import uk.ac.soton.comp1206.component.GameBlock;

/**
 * The Grid is a model which holds the state of a game board. It is made up of a set of Integer values arranged in a 2D
 * arrow, with rows and columns.
 *
 * Each value inside the Grid is an IntegerProperty can be bound to enable modification and display of the contents of
 * the grid.
 *
 * The Grid contains functions related to modifying the model, for example, placing a piece inside the grid.
 *
 * The Grid should be linked to a GameBoard for it's display.
 */
public class Grid {

    /**
     * The number of columns in this grid
     */
    private final int cols;

    /**
     * The number of rows in this grid
     */
    private final int rows;

    /**
     * The grid is a 2D arrow with rows and columns of SimpleIntegerProperties.
     */
    private final SimpleIntegerProperty[][] grid;

    /**
     * creating a boolean property to return true or false when a piece is called
     */
    private boolean play = true;


    /**
     * Create a new Grid with the specified number of columns and rows and initialise them
     *
     * @param cols number of columns
     * @param rows number of rows
     */
    public Grid(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;

        //Create the grid itself
        grid = new SimpleIntegerProperty[cols][rows];

        //Add a SimpleIntegerProperty to every block in the grid
        for (var y = 0; y < rows; y++) {
            for (var x = 0; x < cols; x++) {
                grid[x][y] = new SimpleIntegerProperty(0);
            }
        }

    }

    /**
     * Get the Integer property contained inside the grid at a given row and column index. Can be used for binding.
     *
     * @param x column
     * @param y row
     * @return the IntegerProperty at the given x and y in this grid
     */
    public IntegerProperty getGridProperty(int x, int y) {
        return grid[x][y];
    }

    /**
     * Update the value at the given x and y index within the grid
     *
     * @param x     column
     * @param y     row
     * @param value the new value
     */
    public void set(int x, int y, int value) {
        grid[x][y].set(value);
    }

    /**
     * Get the value represented at the given x and y index within the grid
     *
     * @param x column
     * @param y row
     * @return the value
     */
    public int get(int x, int y) {
        try {
            //Get the value held in the property at the x and y index provided
            return grid[x][y].get();
        } catch (ArrayIndexOutOfBoundsException e) {
            //No such index
            return -1;
        }
    }

    /**
     * Get the number of columns in this game
     *
     * @return number of columns
     */
    public int getCols() {
        return cols;
    }

    /**
     * Get the number of rows in this game
     *
     * @return number of rows
     */
    public int getRows() {
        return rows;
    }


    /**return the whether a gamepiece can be played or not
     * @param piece piece to be placed on the grid
     * @param x the x  coordinate of the block clicked
     * @param y the y coordinate of the block clikced
     */
     public boolean canPlayPiece(GamePiece piece, int x, int y) {
         for (int i = 0; i < piece.getBlocks().length; i++) {
             for (int j = 0; j < piece.getBlocks().length; j++) {
                 if (piece.getBlocks()[i][j] == 0) continue;
                 if(get(x + i - 1, y + j - 1) != 0) return false;
             }
         }
         return true;
    }

    /**
     * play the piece to be placed
     * @param piece piece to be placed
     * @param x x coordinate of block clicked
     * @param y y coordinate of block clicked
     */
    public void playPiece(GamePiece piece, int x, int y) {
            for (int i = 0; i < piece.getBlocks().length; i++) {
                for (int j = 0; j < piece.getBlocks().length; j++) {
                    if (piece.getBlocks()[i][j] == 0) continue;
                    set( x+i - 1,  y+j - 1, piece.getBlocks()[i][j]);
                }
            }
    }
}











