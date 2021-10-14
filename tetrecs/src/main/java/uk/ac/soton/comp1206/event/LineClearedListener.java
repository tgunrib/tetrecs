package uk.ac.soton.comp1206.event;

import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBlockCoordinate;

import java.util.Set;

/**
 * The Line Cleared listener is used to handle the event when a row or column is filled in the gameboard. It passes the
 * GameBlocks that have been filled in the message
 */
public interface LineClearedListener {

    /**
     * Handle when all blocks in a column and row are filled
     * @param clear the set of block that need to be cleared
     */
    void fadeOut(Set<GameBlockCoordinate> clear);
}
