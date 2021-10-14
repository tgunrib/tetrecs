package uk.ac.soton.comp1206.component;

import javafx.event.Event;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.event.BlockClickedListener;
import uk.ac.soton.comp1206.event.RightClickedListener;
import uk.ac.soton.comp1206.event.RotateListener;
import uk.ac.soton.comp1206.game.Grid;

import java.util.Set;

/**
 * A GameBoard is a visual component to represent the visual GameBoard.
 * It extends a GridPane to hold a grid of GameBlocks.
 *
 * The GameBoard can hold an internal grid of it's own, for example, for displaying an upcoming block. It also be
 * linked to an external grid, for the main game board.
 *
 * The GameBoard is only a visual representation and should not contain game logic or model logic in it, which should
 * take place in the Grid.
 */
public class GameBoard extends GridPane {

    protected static final Logger logger = LogManager.getLogger(GameBoard.class);

    /**
     * Number of columns in the board
     */
    protected final int cols;

    /**
     * Number of rows in the board
     */
    protected final int rows;

    /**
     * The visual width of the board - has to be specified due to being a Canvas
     */
    protected final double width;

    /**
     * The visual height of the board - has to be specified due to being a Canvas
     */
    protected final double height;

    /**
     * The grid this GameBoard represents
     */
    final Grid grid;
    /**
     * The blocks inside the grid
     */
    GameBlock[][] blocks;

    /**
     * The listener to call when
     * (blockClickedListener) a specific block is clicked
     * (rightClickedListener) the gameblock is rightclicked or pieceboard is leftclicked
     * (rotateListener) the boards a clicked to  rotatepiece
     */
    protected BlockClickedListener blockClickedListener;
    protected RightClickedListener rightClickedListener;
    protected RotateListener rotateListener;

    /**
     * Static integer that change to help hover around the board
     * rowkey = row number
     * columnkey = column number
     */
    private static int rowkey;
    private static int columnkey;


    /**
     * Create a new GameBoard, based off a given grid, with a visual width and height.
     *
     * @param grid   linked grid
     * @param width  the visual width
     * @param height the visual height
     */
    public GameBoard(Grid grid, double width, double height) {
        this.cols = grid.getCols();
        this.rows = grid.getRows();
        this.width = width;
        this.height = height;
        this.grid = grid;

        //Build the GameBoard
        build();
    }

    /**
     * Create a new GameBoard with it's own internal grid, specifying the number of columns and rows, along with the
     * visual width and height.
     *
     * @param cols   number of columns for internal grid
     * @param rows   number of rows for internal grid
     * @param width  the visual width
     * @param height the visual height
     */
    public GameBoard(int cols, int rows, double width, double height) {
        this.cols = cols;
        this.rows = rows;
        this.width = width;
        this.height = height;
        this.grid = new Grid(cols, rows);

        //Build the GameBoard
        build();

    }

    /**
     * Get a specific block from the GameBoard, specified by it's row and column
     *
     * @param x column
     * @param y row
     * @return game block at the given column and row
     */
    public GameBlock getBlock(int x, int y) {
        return blocks[x][y];
    }

    /**
     * Build the GameBoard by creating a block at every x and y column and row
     */
    protected void build() {
        logger.info("Building grid: {} x {}", cols, rows);

        setMaxWidth(width);
        setMaxHeight(height);
        setGridLinesVisible(true);

        blocks = new GameBlock[cols][rows];

        for (var y = 0; y < rows; y++) {
            for (var x = 0; x < cols; x++) {
                createBlock(x, y);

            }
        }
    }

    /**
     * Create a block at the given x and y position in the GameBoard
     * @param x column
     * @param y row
     */
    protected GameBlock createBlock(int x, int y) {
        var blockWidth = width / cols;
        var blockHeight = height / rows;

        //Create a new GameBlock UI component
        GameBlock block = new GameBlock(this, x, y, blockWidth, blockHeight);

        //Add to the GridPane
        add(block, x, y);


        //Add to our block directory
        blocks[x][y] = block;

        //Link the GameBlock component to the corresponding value in the Grid
        block.bind(grid.getGridProperty(x, y));

        //Methods to monitor mousevents and keyevents of blocks
        mouseEvent(block);
        setOnKeyPressed((k) -> {
            boardKeys(k, block);
            pieceKeys(k);
        });

        return block;
    }

    /**
     * Set the listener to handle an event when a block is clicked
     *
     * @param listener listener to add
     */
    public void setOnBlockClick(BlockClickedListener listener) {
        this.blockClickedListener = listener;
    }

    /**
     * Triggered when a block is clicked. Call the attached listener.
     *
     * @param event mouse event
     * @param block block clicked on
     */
    public void blockClicked(Event event, GameBlock block) {
        logger.info("Block clicked: {}", block);

        if (blockClickedListener != null) {
            blockClickedListener.blockClicked(block);
        }
    }

    public void setOnRightClicked(RightClickedListener listener) {
        this.rightClickedListener = listener;
    }

    /**
     * Triggered when a block is clicked. Call the attached listener.
     *
     * @param event mouse event
     */
    public void rightClicked(Event event) {
        logger.info("Gamepiece swapped");

        if (rightClickedListener != null) {
            rightClickedListener.swapCurrentPiece();
        }
    }

    public void setOnRotate(RotateListener listener) {
        this.rotateListener = listener;
    }

    /**
     * Triggered when a block is clicked. Call the attached listener.
     *
     * @param event mouse event
     */
    public void rotate(Event event, int m) {
        logger.info("Gamepiece rotated");

        if (rotateListener != null) {
            rotateListener.rotateCurrentPiece(m);
        }
    }

    /**
     * Triggered when a key is pressed in the scene that affects the gameboard
     *
     * @param m
     * @param block
     */

    public void boardKeys(KeyEvent m, GameBlock block) {
        //Key pressed to go up
        if (m.getCode() == KeyCode.UP || m.getCode() == KeyCode.W) {
            checkHover();
            try {
                columnkey--;
                getBlock(rowkey, columnkey).hover = true;
                getBlock(rowkey, columnkey).hovering();
            } catch (ArrayIndexOutOfBoundsException nr) {
                columnkey++;
                getBlock(rowkey, columnkey).hover = true;
                getBlock(rowkey, columnkey).hovering();
            }
        }
        //key pressed to go down
        if (m.getCode() == KeyCode.DOWN || m.getCode() == KeyCode.S) {
            checkHover();
            try {
                columnkey++;
                getBlock(rowkey, columnkey).hover = true;
                getBlock(rowkey, columnkey).hovering();
            } catch (ArrayIndexOutOfBoundsException ar) {
                columnkey--;
                getBlock(rowkey, columnkey).hover = true;
                getBlock(rowkey, columnkey).hovering();
            }
        }
        //Keypressed to go left
        if (m.getCode() == KeyCode.LEFT || m.getCode() == KeyCode.D) {
            checkHover();
            try {
                rowkey--;
                getBlock(rowkey, columnkey).hover = true;
                getBlock(rowkey, columnkey).hovering();
            } catch (ArrayIndexOutOfBoundsException bn) {
                rowkey++;
                getBlock(rowkey, columnkey).hover = true;
                getBlock(rowkey, columnkey).hovering();
            }
        }
        //Keypressed to move right
        if (m.getCode() == KeyCode.RIGHT || m.getCode() == KeyCode.R) {
            checkHover();
            try {
                rowkey++;
                getBlock(rowkey, columnkey).hover = true;
                getBlock(rowkey, columnkey).hovering();
            } catch (ArrayIndexOutOfBoundsException b) {
                rowkey--;
                getBlock(rowkey, columnkey).hover = true;
                getBlock(rowkey, columnkey).hovering();
            }
        }
        //Keypressed to place block
        if (m.getCode() == KeyCode.ENTER) {
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    if (getBlock(i, j).hover)
                        blockClicked(m, getBlock(i, j));
                }
            }
        }

    }

    /**
     * Triggered when the key pressed affects the pieceboards and
     * swaps and rotates pieces
     *
     * @param m
     */
    public void pieceKeys(KeyEvent m) {
        if (m.getCode() == KeyCode.SPACE || m.getCode() == KeyCode.R)
            rightClicked(m);

        else if (m.getCode() == KeyCode.Q || m.getCode() == KeyCode.Z || m.getCode() == KeyCode.OPEN_BRACKET)
            rotate(m, 3);

        else if (m.getCode() == KeyCode.E || m.getCode() == KeyCode.C || m.getCode() == KeyCode.CLOSE_BRACKET)
            rotate(m, 1);
    }

    /**
     * Check if any block is hovered and clear it
     */
    public void checkHover() {
        for (int i = 0; i < getRowCount(); i++) {
            for (int j = 0; j < getRowCount(); j++) {
                getBlock(i, j).hover = false;
                getBlock(i, j).hovering();
            }
        }
    }

    /**
     * Triggered when a mouse is clicked
     *
     * @param block
     */
    public void mouseEvent(GameBlock block) {

        //Add a mouse click handler to the block to trigger GameBoard blockClicked method
        block.setOnMouseClicked((e) -> {
            if (e.getButton() == MouseButton.PRIMARY) blockClicked(e, block);

            if (e.getButton() == MouseButton.SECONDARY) rightClicked(e);
        });

        block.setOnMouseEntered((k) -> {
            checkHover();
            block.hover = true;
            block.hovering();
        });

        block.setOnMouseExited((k) -> {
            block.hover = false;
            block.hovering();
        });
    }



    /**
     * Fade all blocks in a set of gameblock cooordinates that are in the gameboard
     * @param clear
     */
    public void fadeOut(Set<GameBlockCoordinate> clear){
        for(GameBlockCoordinate a : clear){
            grid.set(a.getX(),a.getY(),0);
            this.getBlock(a.getX(),a.getY()).fadeOut();
        }

    }

}
