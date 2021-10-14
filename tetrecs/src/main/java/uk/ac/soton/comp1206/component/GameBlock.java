package uk.ac.soton.comp1206.component;

import javafx.animation.AnimationTimer;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
/**
 * The Visual User Interface component representing a single block in the grid.
 *
 * Extends Canvas and is responsible for drawing itself.
 *
 * Displays an empty square (when the value is 0) or a coloured square depending on value.
 *
 * The GameBlock value should be bound to a corresponding block in the Grid model.
 */
public class GameBlock extends Canvas {

    private static final Logger logger = LogManager.getLogger(GameBlock.class);

    /**
     * The set of colours for different pieces
     */
    public static final Color[] COLOURS = {
            Color.TRANSPARENT,
            Color.DEEPPINK,
            Color.RED,
            Color.ORANGE,
            Color.YELLOW,
            Color.YELLOWGREEN,
            Color.LIME,
            Color.GREEN,
            Color.DARKGREEN,
            Color.DARKTURQUOISE,
            Color.DEEPSKYBLUE,
            Color.AQUA,
            Color.AQUAMARINE,
            Color.BLUE,
            Color.MEDIUMPURPLE,
            Color.PURPLE
    };

    private final GameBoard gameBoard;

    private final double width;
    private final double height;

    /**
     *  The value of the opacity that changes in the animation timer
     */
    private static double opacity = 1;

    /**
     * The column this block exists as in the grid
     */
    private final int x;

    /**
     * The row this block exists as in t he grid
     */
    private final int y;

    /**
     * The value of this block (0 = empty, otherwise specifies the colour to render as)
     */
    private final IntegerProperty value = new SimpleIntegerProperty(0);

    public boolean hover;

    /**
     * Create a new single Game Block
     *
     * @param gameBoard the board this block belongs to
     * @param x         the column the block exists in
     * @param y         the row the block exists in
     * @param width     the width of the canvas to render
     * @param height    the height of the canvas to render
     */
    public GameBlock(GameBoard gameBoard, int x, int y, double width, double height) {
        this.gameBoard = gameBoard;
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;

        //A canvas needs a fixed width and height
        setWidth(width);
        setHeight(height);

        //Do an initial paint
        paint();

        //When the value property is updated, call the internal updateValue method
        value.addListener(this::updateValue);


    }

    /**
     * When the value of this block is updated,
     *
     * @param observable what was updated
     * @param oldValue   the old value
     * @param newValue   the new value
     */
    private void updateValue(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        paint();
    }

    /**
     * Handle painting of the block canvas
     */
    public void paint() {
        //If the block is empty, paint as empty
        if (value.get() == 0) {
            paintEmpty();
        } else {
            //If the block is not empty, paint with the colour represented by the value
            paintColor();
        }
    }

    /**
     * Paint this canvas empty
     */
    private void paintEmpty() {
        var gc = getGraphicsContext2D();

        //Clear
        gc.clearRect(0, 0, width, height);

        //Fill
        gc.setFill(Color.rgb(0, 0, 0, 0.5));
        gc.fillRect(0, 0, width, height);

        //Border
        gc.setStroke(Color.WHITE);
        gc.strokeRect(0, 0, width, height);

    }

    /**
     * Paint this canvas with the given colour
     *
     */
    private void paintColor() {
        var gc = getGraphicsContext2D();

        //Clear
        gc.clearRect(0, 0, width, height);
        if(value.get() < COLOURS.length-1) {
            Stop[] stops = new Stop[]{new Stop(0, COLOURS[value.get()]), new Stop(1, COLOURS[value.get() + 1])};
            LinearGradient linear = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stops);
            gc.setFill(linear);
        }
        else{
            Stop[] stops = new Stop[]{new Stop(0, COLOURS[value.get()]), new Stop(1, Color.BLACK)};
            LinearGradient linear = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stops);
            gc.setFill(linear);
        }

        //Colour fill
        gc.fillRect(0, 0, width, height);


        //Border
        gc.setStroke(Color.BLACK);
        gc.strokeRect(0, 0, width, height);
    }

    /**
     * Paint the block when it is hovered
     */
    public void hovering() {
        if (hover && value.get() == 0) {
            var gc = getGraphicsContext2D();

            //Clear
            gc.clearRect(0, 0, width, height);

            //Fill
            gc.setFill(Color.rgb(255, 255, 255, 0.5));
            gc.fillRect(0, 0, width, height);

            //Border
            gc.setStroke(Color.BLACK);
            gc.strokeRect(0, 0, width, height);
        } else {
            paint();
        }

    }


    /**
     * Get the column of this block
     *
     * @return column number
     */
    public int getX() {
        return x;
    }

    /**
     * Get the row of this block
     *
     * @return row number
     */
    public int getY() {
        return y;
    }

    /**
     * Get the current value held by this block, representing it's colour
     *
     * @return value
     */
    public int getValue() {
        return this.value.get();
    }

    /**
     * Bind the value of this block to another property. Used to link the visual block to a corresponding block in the Grid.
     *
     * @param input property to bind the value to
     */
    public void bind(ObservableValue<? extends Number> input) {
        value.bind(input);
    }


    /**
     * Create an animation timer that starts when the row or column of the block is cleared
     */
    public void fadeOut() {
        new AnimationTimer() {
            //define the handle method
            @Override
            public void handle(long now) {
                //call the method
                var gc = getGraphicsContext2D();
                handlee(gc);

            }

            //method handlee that changes the colour
            private void handlee(GraphicsContext context) {
                //Clear
                context.clearRect(0, 0, width, height);

                context.setFill(Color.rgb(255,255,255,opacity));

                context.fillRect(0, 0, width, height);

                //Border
                context.setStroke(Color.BLACK);
                context.strokeRect(0, 0, width, height);
                opacity -= 0.0005;

                if (opacity <= 0.2) {
                    stop();
                }
            }
        }.start();
    }



}
