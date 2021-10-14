package uk.ac.soton.comp1206.event;

/**
 * The Right Clicked listener is used to handle the event when a board has been clicked
 * and the next gamepiece to be played needs to be swapped with the one after.
 * It passes the Gampepiece to the method.
 */
public interface RightClickedListener {
    /**
     * Method to swap current piece to folllowing piece.
     */
    void swapCurrentPiece();
}
