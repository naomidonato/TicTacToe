package cs4b.proj1;

import cs4b.proj1.observer.*;
import javafx.util.Pair;
import org.w3c.dom.ranges.RangeException;

import java.util.*;

/**
 * The Game Engine
 */
public class Game implements ISubject {

    // HEY, YOU!
    //
    // If you're viewing this with IntelliJ, you should be able to fold
    // region below this comment called "Event info containers".
    // I typically put type definitions near the top of my code, but
    // it they've gotten long this time around. So do yourself a favor,
    // and fold this region.


    //region ISubject *************************************************************

    /**
     * Contains info on the current turn, including the previous move.
     * @see TurnInfo
     * @author Daniel Edwards
     */
    static public class MoveInfo {
        private int x;
        private int y;
        private Player nextPlayer;
        private Player previousPlayer;
        private Board currentBoard;

        public MoveInfo() {
        }

        public MoveInfo(int x, int y, Player nextPlayer, Player previousPlayer, Board currentBoard) {
            this.x = x;
            this.y = y;
            this.nextPlayer = nextPlayer;
            this.previousPlayer = previousPlayer;
            this.currentBoard = currentBoard;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        /**
         * Gets the player who will take the next turn.
         * If null, there is no turn after this one.
         * @return The next player or null.
         * @author Daniel Edwards
         */
        public Player getNextPlayer() {
            return nextPlayer;
        }

        /**
         * Gets the player who took the previous turn.
         * Logically, this shouldn't be null, but there is no
         * strong garuntee that it is not.
         * @return The previous player.
         * @author Daniel Edwards
         */
        public Player getPreviousPlayer() {
            return previousPlayer;
        }

        public Board getCurrentBoard() {
            return currentBoard;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            MoveInfo that = (MoveInfo) o;
            return getX() == that.getX() &&
                    getY() == that.getY() &&
                    getPreviousPlayer() == that.getPreviousPlayer() &&
                    Objects.equals(getNextPlayer(), that.getNextPlayer()) &&
                    Objects.equals(getCurrentBoard(), that.getCurrentBoard());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getX(), getY(), getNextPlayer(), getPreviousPlayer(), getCurrentBoard());
        }
    }

    /**
     * Tracks just info on who's turn it is and was. Doesn't keep track of
     * moves which have been made.
     * @see MoveInfo
     * @author Daniel Edwards
     */
    static public class TurnInfo {
        private Player nextPlayer;
        private Player previousPlayer;
        private Board currentBoard;

        public TurnInfo() {};

        public TurnInfo(Player nextPlayer, Player previousPlayer, Board currentBoard) {
            this.nextPlayer = nextPlayer;
            this.previousPlayer = previousPlayer;
            this.currentBoard = currentBoard;
        }

        /**
         * Returns the player who'll take the next turn. If this is the
         * last turn, null is returned instead.
         * @return The player who'll take the next turn or null.
         * @author Daniel Edwards
         */
        public Player getNextPlayer() {
            return nextPlayer;
        }

        /**
         * Returns the player who took the previous turn. If this is
         * the first turn, this will be null.
         * @return The player who took the previous turn or null.
         * @author Daniel Edwards
         */
        public Player getPreviousPlayer() {
            return previousPlayer;
        }

        public Board getCurrentBoard() {
            return currentBoard;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TurnInfo turnInfo = (TurnInfo) o;
            return Objects.equals(getNextPlayer(), turnInfo.getNextPlayer()) &&
                    Objects.equals(getPreviousPlayer(), turnInfo.getPreviousPlayer()) &&
                    Objects.equals(getCurrentBoard(), turnInfo.getCurrentBoard());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getNextPlayer(), getPreviousPlayer(), getCurrentBoard());
        }
    }

    /**
     * Contains info on the end result of a game.
     * @author Daniel Edwards
     */
    static public class ResultInfo {
        private Player winner;

        public ResultInfo() {
            winner = null;
        }

        public ResultInfo(Player winner) {
            this.winner = winner;
        }

        /**
         * Gets the player who won. If no players won
         * (i.e. the game ended in a draw), then null is returned.
         * @return The winner or null.
         * @author Daniel Edwards
         */
        public Player getWinner() {
            return winner;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ResultInfo that = (ResultInfo) o;
            return Objects.equals(winner, that.winner);
        }

        @Override
        public int hashCode() {
            return Objects.hash(winner);
        }
    }


    /**
     * Subscribes the given observer, causing its update function to be called
     * for the given event. As there can be a variety of modes, subjects are
     * expected to implement some kind of object (e.g. an enum) to allow
     * subscribers to select what kind of events they are interested in.
     * <p>
     * If an observer attempts to subscribe itself more than once, the first
     * subscription should be replaced. (Unless they are with differenct
     * modes, of course.)
     *
     * @param observer The observer which will be subscribed.
     * @author Daniel Edwards
     */
    @Override
    public void subscribe(IObserver observer) {
        // TODO Maybe needed for serializable?
        if(subjAssist == null) {
            subjAssist = new SubjectAssistant();
        }

        subjAssist.subscribe(observer);
    }

    /**
     * Unsubscribes the given observer so that they will no longer receive
     * updates for the given event. Nothing should happen if the observer
     * isn't subscribed.
     *
     * @param observer Observer to be unsubscribed.
     * @author Daniel Edwards
     */
    @Override
    public void unsubscribe(IObserver observer) {
        // TODO Maybe needed for serializable?
        if(subjAssist == null) {
            subjAssist = new SubjectAssistant();
        }

        subjAssist.unsubscribe(observer);
    }

    //endregion ISubject ***********************************************************


    // TODO There needs to be some concrete way of tracking the current player.
    //      Otherwise, when we go to serialize/deserialize, there won't be any
    //      way of checking who's turn it is.


    private Player player1;
    private Player player2;
    Board board;

    private Player currentPlayer;       // Used to track who's turn it is.

    private SubjectAssistant subjAssist;



    public Game(PlayerBehavior player1Behavior, PlayerBehavior player2Behavior) {
        // Init Player Behaviors
        this.player1 = new Player(player1Behavior);
        this.player2 = new Player(player2Behavior);

        this.board = new Board();
    }

    /**
     * Call this once everything is set up. This may get deprecated/deleted
     * if we force everything to be properly configured in the constructor.
     * @author Daniel Edwards
     */
    public void startGame() {
        currentPlayer = player1;

        subjAssist.triggerUpdate(new TurnInfo(currentPlayer, null, board));
    }

    @Deprecated
    void makePlay(Player player) {
        // For AI Plays.  Calls the same makePlay(Player player, int x, int y)
        makePlay(player,0, 0);
    }

    /**
     * Puts down the given player's symbol.
     *
     * @param movingPlayer
     * @param x
     * @param y
     */
    void makePlay(Player movingPlayer, int x, int y) {

        // TODO This needs some tests.
        //      However, none have been written because this method
        //      is in a super nebulous state right now.

        // TODO This should check for game over.
        //      When the game does end, SubjectMode.GameEnd should be triggered.

        // TODO This should probably safeguard against overriding other spaces.

        // TODO Either call a function on nextPlayer or make sure that
        //      the players are hooked up correctly with the events.

        if(movingPlayer == null) {
            throw new NullPointerException("movingPlayer must not be null!");
        }
        else if(movingPlayer != currentPlayer) {
            throw new IllegalArgumentException(
                    movingPlayer.toString() + " isn't the same as " + currentPlayer.toString()
                    + ", yet " + movingPlayer.toString() + " attempted to make a move."
            );
        }

        board.setPos(x, y, movingPlayer.getSymbol());

        // TODO If the game is over, nextPlayer should become null.
        Player nextPlayer = (movingPlayer != player1 ? player1 : player2);
        currentPlayer = nextPlayer;

        subjAssist.triggerUpdate(
                new MoveInfo(x, y, nextPlayer, movingPlayer, board)
        );
        subjAssist.triggerUpdate(
                new TurnInfo(nextPlayer, movingPlayer, board)
        );



        /*
        // If we use this implamentation, we have to do it this way because board is not in the scope of PlayerBehavior
        // If the Type is NPC, the xy values will be discarded.
        if(player.pb instanceof HPC) {
            board.setPos(x,y,player.getSymbol());
        }
        else if(player.pb instanceof NPCEasy) {
            // Some random choice function
            Pair<Integer,Integer> pair = random();
            board.setPos(pair.getKey(),pair.getValue(),player.getSymbol());
        }
        else if(player.pb instanceof NPCHard) {
            // Use AI
            Pair<Integer,Integer> pair= minimax_helper(board);
            board.setPos(pair.getKey(),pair.getValue(),player.getSymbol());

        }
        */

    }

    Pair<Integer,Integer> minimax_helper (Board b) {

        minimax();
        return new Pair<>(1,1);
    }

    private Pair<Integer,Integer> minimax(){
        //TODO
        // write the minimax
        return new Pair<>(1,1);
    }
//    private void staticEvaluator() {
//
//    }

    Pair<Integer,Integer> random (/*Board b*/) {
        Random rand = new Random();
        char p1 = player1.getSymbol();
        char p2 = player2.getSymbol();
        int x;
        int y;
        do {
            x = rand.nextInt(3);
            y = rand.nextInt(3);
        }while(board.getPos(x,y) != board.DEFAULT_VALUE/*board.getPos(x,y) == p1 || board.getPos(x,y) == p2*/);

        return new Pair<>(x,y);
    }

    @Override
    public String toString() {
        return "Game{" +
                "player1=" + player1 +
                ", player2=" + player2 +
                ", board=" + board +
                ", currentPlayer=" + currentPlayer +
                ", subjAssist=" + subjAssist +
                '}';
    }



}
