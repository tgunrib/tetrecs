package uk.ac.soton.comp1206.scene;

import uk.ac.soton.comp1206.ui.GameWindow;

public class MultiplayerScoreScene extends ScoresScene{
    private LeaderBoard leaderBoard;
    /**
     * Create a new scene, passing in the GameWindow the scene will be displayed in
     *
     * @param gameWindow the game window
     */
    public MultiplayerScoreScene(GameWindow gameWindow, LeaderBoard board) {
        super(gameWindow);

        this.leaderBoard = board;
        setScoreList();
    }

    /**
     * Override score scene method
     * to change localscore to a leaderboard instead of scorelist
     */
    @Override
    protected void setScoreList(){
        super.localscore =  leaderBoard;
        super.localscore.heading.setText("Local High Scores");
    }


}
