package uk.ac.soton.comp1206.scene;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.Utility.Multimedia;
import uk.ac.soton.comp1206.event.CommunicationsListener;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;

public class LobbyScene extends BaseScene{

    private static final Logger logger = LogManager.getLogger(LobbyScene.class);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
    private BorderPane pane;
    private CommunicationsListener multilistener;
    private Timer channelTimer;
    private VBox channelflow;
    private final ObservableList<String> channelList = FXCollections.observableArrayList();
    private TextFlow messageflow;
    private SimpleStringProperty username = new SimpleStringProperty();
    private HBox textfield;
    private BorderPane mainField;

    /**
     * Create a new scene, passing in the GameWindow the scene will be displayed in
     *
     * @param gameWindow the game window
     */
    public LobbyScene(GameWindow gameWindow) {
        super(gameWindow);
    }

    @Override
    public void initialise() {
        logger.info("Initialising lobbyscene");
        Multimedia lobbymusic = new Multimedia();
        lobbymusic.setAudioEnabled(true);
        lobbymusic.playBackgroudmusic("menu.mp3");
        getScene().setOnKeyPressed((m)->{
            if(m.getCode() == KeyCode.ESCAPE){
                lobbymusic.setAudioEnabled(false);
                gameWindow.cleanup();
                gameWindow.startMenu();
            }
        });

    }

    /**
     * Build the visual for the lobbyscene
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());

        root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());
        var challengePane = new StackPane();
        challengePane.setMaxWidth(gameWindow.getWidth());
        challengePane.setMaxHeight(gameWindow.getHeight());
        challengePane.getStyleClass().add("menu-background");
        root.getChildren().add(challengePane);

        pane = new BorderPane();
        var channelPane = new VBox();
        channelflow = new VBox();
        var currentGames = new Text("Current Games");
        currentGames.getStyleClass().add("title");
        var host = new Text("Host New Game");
        host.getStyleClass().add("title");
        host.setOnMouseClicked((m)->{
           hostChannel();
        });
        channelPane.getChildren().addAll(currentGames,host, channelflow);
        pane.setLeft(channelPane);
        challengePane.getChildren().add(pane);
        timer();

    }


    /**
     *  Add a textchannel that also allows you to start or end game when needed
     */
    public void textfield(){
        mainField = new BorderPane();
        mainField.getStyleClass().add("gameBox");
        mainField.setMaxSize(500,400);
        pane.setCenter(mainField);
        textfield = new HBox();
        messageflow = new TextFlow();
        var username = new Text();
        messageflow.getChildren().add(username);
        messageflow.getStyleClass().add("messages");
        Button leave = new Button("End Game");
        leave.setOnMouseClicked((e)->{
            gameWindow.getCommunicator().send("PART");
            pane.getChildren().remove(mainField);
        });
        TextField messagebox = new TextField();
        messagebox.setOnKeyPressed((n->{
            if(n.getCode()==KeyCode.ENTER) {
                sendMessage(messagebox.getText());
                messagebox.clear();
            }
        }));
        textfield.getChildren().addAll(messagebox,leave);
        HBox.setHgrow(messagebox, Priority.ALWAYS);
        mainField.setBottom(textfield);
        mainField.setCenter(messageflow);

    }


    /**
     * Add a timer to regularly call the list of channels in the server
     * @return
     */
    public Timer timer(){
        logger.info("new timer created");
        if(channelTimer != null)
            channelTimer.cancel();
        channelTimer = new Timer("Timer");
        TimerTask task = new TimerTask() {
            public void run() {
                Platform.runLater(()->{
                  gameWindow.getCommunicator().send("LIST");
                  gameWindow.getCommunicator().addListener((multilistener)->Platform.runLater(()->receiveMessage(multilistener)));
                });
            }};
        channelTimer.schedule(task,0,10000);
        return channelTimer;
    }


    /**
     * Method to deal with the different types of message recieved from the server by the communicator
     * @param Channel
     */
    public void receiveMessage(String Channel){
       if(Channel.startsWith("CHANNELS")) {
           channelflow.getChildren().clear();
           channelList.clear();
           Channel = Channel.replace("CHANNELS","");
           String[] channels = Channel.split("\\n");
           for(String channel: channels) {
               channelList.add(channel);
           }
           for(String channel: channelList){
                   Text channelLabel = new Text(channel);
                   channelLabel.getStyleClass().add("channelItem");
                   channelflow.getChildren().add(channelLabel);
                   channelLabel.setOnMouseClicked((m) -> {
                       gameWindow.getCommunicator().send("JOIN" + channel);
                   });
           }
       }

        if(Channel.startsWith("NICK")){
            Channel = Channel.replace("NICK", "");
            if(Channel.contains(":")){
                String[] name = Channel.split(":", 2);
                Channel = name[1];
            }
            setUsername(Channel);
        }

        if(Channel.startsWith("START")){
            startGame();
        }

        if (Channel.startsWith("MSG") && messageflow.isVisible()){
            messageflow.getChildren().add(new Text(Channel));
        }
        if(Channel.startsWith("ERROR"))
            handleError(Channel);

        if (Channel.startsWith("JOIN"))
            textfield();

        if(Channel.startsWith("HOST")){
            textfield();
            Button start = new Button("Start Game");
            textfield.getChildren().add(0,start);
            start.setOnMouseClicked((m) -> {
                startGame();
            });
        }
    }

    /**
     * Method to create your own host channel as well as start a game
     */
    public void hostChannel(){
        var hostName = new TextField();
        channelflow.getChildren().add(hostName);
        hostName.setOnKeyPressed((k)->{
            if(k.getCode() == KeyCode.ENTER){
                hostName.setVisible(false);

                gameWindow.getCommunicator().send("CREATE "+hostName.getText());
            }
        });

    }

    /**
     * Method that allows to send a message to the server
     * @param message
     */
    public void sendMessage(String message) {
        gameWindow.getCommunicator().send("MSG " + message);
        if(getUsername() == null){
            setUsername("Tosin");
        }
        messageflow.getChildren().add(new Text("<" + getUsername() + ">" + message));
        messageflow.getChildren().add(new Text("\n"));
    }

    /**
     * Method to change the username
     * @param name
     */
    private void setUsername(String name){
        username.set(name);
    }

    /**
     * Method that return the username when called
     * @return username
     */
    private String getUsername(){
        return username.get();
    }

    /**
     * Method that starts the game when the start button is pressed or when the server sends a start message
     */
    private void startGame(){
        gameWindow.getCommunicator().send("START");
        gameWindow.cleanup();
        gameWindow.startMultiplayerGame();
        gameWindow.startMultiplayerGame().getUsernameprop().bind(username);
    }

    /**
     * Method to handle the error received by the communicator
     * @param message
     */
    private void handleError(String message){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error Dialog");
        alert.setHeaderText("Error Message");
        message = message.replace("ERROR","");
        alert.setContentText(message);
        alert.showAndWait();

    }




}
