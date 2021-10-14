package uk.ac.soton.comp1206.scene;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;
import uk.ac.soton.comp1206.Utility.Multimedia;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

public class IntroductionScene extends BaseScene{

    private Multimedia introductionMusic;
    /**
     * Create a new scene, passing in the GameWindow the scene will be displayed in
     *
     * @param gameWindow the game window
     */
    public IntroductionScene(GameWindow gameWindow) {
        super(gameWindow);
    }

    @Override
    public void initialise() {
        var mainPane = new BorderPane();
        Image ecslogo = new Image(ScoresScene.class.getResource("/images/ECSGames.png").toExternalForm());
        ImageView Ecs = new ImageView(ecslogo);
        Ecs.setFitHeight(200);
        Ecs.setFitWidth(200);
        mainPane.setCenter(Ecs);
        root.getChildren().add(mainPane);
        introductionMusic.playAudio("intro.mp3");
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(Ecs.opacityProperty(), 0.0)),
                new KeyFrame(Duration.seconds(8), new KeyValue(Ecs.opacityProperty(), 1.0)),
                new KeyFrame(Duration.seconds(9), e->{
                    gameWindow.startMenu();
                })
        );
        timeline.play();

    }

    @Override
    public void build() {
        //logger.info("Building " + this.getClass().getName());
        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

    }
}
