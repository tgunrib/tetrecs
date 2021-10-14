package uk.ac.soton.comp1206.scene;

import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.App;
import uk.ac.soton.comp1206.Utility.Multimedia;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

import java.util.ArrayList;

/**
 * The main menu of the game. Provides a gateway to the rest of the game.
 */
public class MenuScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(MenuScene.class);
    private Multimedia menumusic;
    /**
     * Create a new menu scene
     * @param gameWindow the Game Window this will be displayed in
     */
    public MenuScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating Menu Scene");
    }

    /**
     * Build the menu layout
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());
        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());
        setMenu();
    }

    /**
     * Initialise the menu
     */
    @Override
    public void initialise() {
        menumusic = new Multimedia();
        menumusic.setAudioEnabled(true);
        menumusic.playBackgroudmusic("menu.mp3");
    }

    /**
     * Handle when the Single player button is pressed
     * @param event event
     */
    private void startGame(MouseEvent event) {
        menumusic.playAudio("pling.wav");
        gameWindow.startChallenge();

    }

    /**
     * Hande when the  instructions button is pressed
     * @param event
     */
    private void intstructionpage(MouseEvent event){
        menumusic.playAudio("pling.wav");
        gameWindow.startInstructions();
    }

    /**
     * Handle when the exit button is pressed on the page
     * @param k
     */
    private void closeWindow(MouseEvent k) {
        menumusic.playAudio("pling.wav");
        App.getInstance().shutdown();
    }

    /**
     * Hande when the  instructions button is pressed
     * @param event
     */
    private void multiplayer(MouseEvent event){
        menumusic.playAudio("pling.wav");
        gameWindow.startMultiplayer();}

    /**
     * Method to create the animation that plays in the menu
     * @return
     */
    private ImageView tetrECS(){
        Image tetre = new Image(ScoresScene.class.getResource("/images/TetrECS.png").toExternalForm());
        ImageView tetrecs = new ImageView(tetre);
        tetrecs.setFitWidth(500);
        tetrecs.setFitHeight(150);

        RotateTransition rotate = new RotateTransition();
        rotate.setAxis(Rotate.Z_AXIS);
        rotate.setByAngle(10);
        rotate.setCycleCount(Animation.INDEFINITE);
        rotate.setDuration(Duration.millis(1000));
        rotate.setAutoReverse(true);
        rotate.setNode(tetrecs);
        rotate.play();
        return tetrecs;
    }

    /**
     * Create the visuals for all the buttons needed in the game
     */
    private  void setMenu(){
        var menuPane = new StackPane();
        menuPane.setMaxWidth(gameWindow.getWidth());
        menuPane.setMaxHeight(gameWindow.getHeight());
        menuPane.getStyleClass().add("menu-background");
        root.getChildren().add(menuPane);

        var mainPane = new BorderPane();
        menuPane.getChildren().add(mainPane);

        //For now, let us just add a button that starts the game. I'm sure you'll do something way better.
        var flow = new VBox();
        var label= new Text("Single Player");
        var multi = new Text("Multi Player");
        var instructions = new Text("Instructions");
        var exit = new Text("Exit");
        ArrayList<Text> buttons = new ArrayList<>();
        buttons.add(label);
        buttons.add(multi);
        buttons.add(instructions);
        buttons.add(exit);

        for(Text button : buttons){
            button.getStyleClass().add("menuItem");
            flow.getChildren().add(button);
            button.setOnMouseEntered((m)-> {
                flow.getStyleClass().add("menuItem");
            });
        }
        mainPane.setBottom(flow);
        mainPane.setCenter(tetrECS());
        flow.setAlignment(Pos.BOTTOM_CENTER);


        //Bind the button action to the startGame method in the menu
        label.setOnMouseClicked(this::startGame);
        instructions.setOnMouseClicked(this::intstructionpage);
        exit.setOnMouseClicked(this::closeWindow);
        multi.setOnMouseClicked(this::multiplayer);

    }

}
