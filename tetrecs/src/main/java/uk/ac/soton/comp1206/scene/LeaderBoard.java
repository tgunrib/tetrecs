package uk.ac.soton.comp1206.scene;

import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LeaderBoard extends ScoreList{

    private static final Logger logger = LogManager.getLogger(LeaderBoard.class);


    public LeaderBoard(SimpleListProperty<Pair<String, Integer>> playerscores){
        scores.bind(playerscores);
    }

}
