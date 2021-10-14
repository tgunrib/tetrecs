package uk.ac.soton.comp1206.scene;


import javafx.beans.property.*;
import javafx.collections.ListChangeListener;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.util.Pair;

/**
 * The Score List links in with a Scores ArrayList and displays a list of names and points
 */
public class ScoreList extends VBox {

    public final SimpleListProperty<Pair<String,Integer>> scores = new SimpleListProperty<>();
    private final StringProperty username = new SimpleStringProperty();
    public Text heading;

    /**
     * Create a new Score List
     */
    public ScoreList() {

        //Set style
        getStyleClass().add("scorelist");
        setAlignment(Pos.CENTER);
        setSpacing(2);
        heading = new Text();
        heading.getStyleClass().add("heading");


        //Update score list when score array list is updated
        scores.addListener((ListChangeListener<? super Pair<String, Integer>>) (c) -> updateList());
    }

    /**
     * Update the score list from the array list
     */
    public void updateList() {
        //Remove previous children
        getChildren().clear();

        this.getChildren().add(heading);

        //Loop through the top scores
        int counter = 0;
        for(Pair<String,Integer> score : scores) {
            //Only do the top 5 scores
            counter++;
            if(counter > 10) break;

            Text scores = new Text(score.getKey() + ":" + score.getValue().toString());
            this.getChildren().add(scores);
        }
    }

    /**
     * Get the internal score property, for linking to the parent
     * @return score property
     */
    public ListProperty<Pair<String,Integer>> scoreProperty() {
        return scores;
    }

    /**
     * Get the internal username property, for linking to the parent
     * @return username property
     */
    public Property<String> usernameProperty() {
        return username;
    }
}
