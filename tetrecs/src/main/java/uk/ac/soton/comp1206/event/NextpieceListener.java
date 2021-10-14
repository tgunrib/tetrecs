package uk.ac.soton.comp1206.event;

import uk.ac.soton.comp1206.game.GamePiece;

/**
 * The Next piece listener is used to handle the event when a block has been clicked and a next piece is to be created. It passes the
 * Gamepiece in the message.
 */
public interface NextpieceListener {
    /**
     * Method to listen when the next piece is called so that it can add it to the grid
     *
     */
    public void Nextpiece(GamePiece piece, GamePiece follow);

}
