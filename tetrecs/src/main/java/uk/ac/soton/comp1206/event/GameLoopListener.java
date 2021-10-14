package uk.ac.soton.comp1206.event;

import java.util.Timer;

/**
 * The Game loop listener is used to handle the event when a block has been clicked and the timer is to be reset. It passes the
 * time to be delayed in the message.
 */
public interface GameLoopListener {
    /**
     * method to reset the timer
     * @param delay stores the time to be delayed
     */
    void gameloop(double delay);

}
