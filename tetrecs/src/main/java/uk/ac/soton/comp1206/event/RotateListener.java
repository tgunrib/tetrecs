package uk.ac.soton.comp1206.event;

/**
 * The Rotate listener is used to handle the event when a board has been clicked
 * and the next gamepiece to be played needs to be rotated.
 * It passes the Gampepiece to the method
 */
public interface RotateListener {
    /**
     * method to rotate piece
     * @param a number of times to rotate piece
     */
    void rotateCurrentPiece(int a);
}
