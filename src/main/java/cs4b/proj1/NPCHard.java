package cs4b.proj1;

public class NPCHard implements PlayerBehavior {
    // Player 1 = +10
    // Player 2 = -10

    private char player1Char = '\0';
    private char player2Char = '\0';
    private Integer MAX_SCORE;
    private Integer MIN_SCORE;

    //***************************************************************************
    NPCHard(char p1, char p2){
//        player1Char = p1.getSymbol();
//        player2Char = p2.getSymbol();
        player1Char = p1;
        player2Char = p2;

        Board b = new Board();
        MAX_SCORE =  ((b.BOARD_SIZE_X*b.BOARD_SIZE_Y) + 1);
        MIN_SCORE = -((b.BOARD_SIZE_X*b.BOARD_SIZE_Y) + 1);
    }
    //***************************************************************************


    //***************************************************************************
    @Override
    public void getMove(Board b, char token) {
        int score = Integer.MIN_VALUE;
        int xPos = -1;
        int yPos = -1;

        boolean isPlayer1sTurn;

        if(token == player1Char) {
            score = Integer.MAX_VALUE;
            isPlayer1sTurn = true;
            score = Integer.MIN_VALUE;
        }
        else if(token == player2Char){
            score = Integer.MIN_VALUE;
            isPlayer1sTurn = false;
            score = Integer.MAX_VALUE;
        }
        else {
            throw new IllegalStateException("Token does not belong to Player1 or Player2");
        }

        for(int i = 0; i < b.BOARD_SIZE_X; i++) {
            for(int j = 0; j < b.BOARD_SIZE_Y; j++) {
                if(b.getPos(i,j) == b.DEFAULT_VALUE ) {
                    char tempChar;
                    if(isPlayer1sTurn) {
                        tempChar = player1Char;


                    }
                    else {
                        tempChar = player2Char;
                       
                    }
                    //System.out.println("Output " + i +" :\t" + i + " " + j );
                    b.setPos(i,j,tempChar);

                   // System.out.println("Output " + i +" :\t" + xPos + " " + yPos );
                    int tempScore = minimax(b,0,isPlayer1sTurn);
                    b.setPos(i,j,b.DEFAULT_VALUE);
                   // System.out.println("Output " + i +" :\t" + xPos + " " + yPos );


                    if(isPlayer1sTurn && tempScore > score) {
                        xPos = i;
                        yPos = j;
                        score = tempScore;
                    }
                    else if(!isPlayer1sTurn && tempScore < score){
                        xPos = i;
                        yPos = j;
                        score = tempScore;
                    }
                }
            }
        }

        System.out.println("Best Move:\t" + xPos + " " + yPos );

    }
    //***************************************************************************

//    // idk if i like this. I might make this makeMove(Board b)
//    @Override
//    public Pair<Integer,Integer> getMove(Board b) {
//
//
//        return new Pair<>(0,0);
//    }


    //***************************************************************************
    public int minimax(Board b, int depth, boolean isMax) {

        // Check if someone has won the game
        Integer score = evalBoard(b);
        if (score != null) return score;

        // If no one has one, check to see if the game is over.
        if(isBoardFull(b)) {
            return 0; // The Game was a tie
        }

        if(isMax) {
            return maximize(b, depth, isMax);

        }
        else {
            return minimize(b, depth, isMax);

        }

    }
    //***************************************************************************


    //***************************************************************************
    private int minimize(Board b, int depth, boolean isMax) {
        // minimize
        int worst = Integer.MAX_VALUE;
//        Board temp = new Board(b.getBoardArray()); //TODO make sure this is a deep copy
        
        for(int i = 0; i < b.BOARD_SIZE_X; i++) {
            for(int j = 0; j < b.BOARD_SIZE_Y; j++) {
                if(b.getPos(i,j) == b.DEFAULT_VALUE){
                    b.setPos(i,j,player2Char);
//                    System.out.println("***************");
//                    System.out.print(temp.toString());
//                    System.out.println("***************\n");
                    worst = Integer.min(worst, minimax(b,depth+1,!isMax));
                    b.setPos(i,j,b.DEFAULT_VALUE);
                }
            }
        }
        return worst;
    }
    //***************************************************************************


    //***************************************************************************
    private int maximize(Board b, int depth, boolean isMax) {
        // maximize
        int best = Integer.MIN_VALUE;
        //Board temp = new Board(b.getBoardArray()); //TODO make sure this is a deep copy
        for(int i = 0; i < b.BOARD_SIZE_X; i++) {
            for(int j = 0; j < b.BOARD_SIZE_Y; j++) {
                if(b.getPos(i,j) == b.DEFAULT_VALUE){
                    b.setPos(i,j,player1Char);
//                    System.out.println("***************");
//                    System.out.print(temp.toString());
//                    System.out.println("***************\n");
                    best = Integer.max(best, minimax(b,depth+1,!isMax));
                    b.setPos(i,j,b.DEFAULT_VALUE);
                }
            }
        }
        return best;
    }
    //***************************************************************************


    //***************************************************************************
    boolean isBoardFull(Board b) {
        int defaultSpaceCount = 0;
        for(int i = 0; i < b.BOARD_SIZE_X; i++) {
            for(int j = 0; j < b.BOARD_SIZE_Y; j++) {
                if(b.getPos(i,j) == b.DEFAULT_VALUE) {
                    defaultSpaceCount++;
                }
            }
        }
        if(defaultSpaceCount > 0) {
            return false;
        }
        else {
            return true;
        }
    }
    //***************************************************************************

    //***************************************************************************
    Integer evalBoard(Board b) {
        Integer rowWim = checkRowWin(b);
        if (rowWim != null) return rowWim;
        Integer colWin = checkColWin(b);
        if (colWin != null) return colWin;
        Integer digWin = checkDig(b);
        if (digWin != null) return digWin;
        return 0;
    }
    //***************************************************************************



    //***************************************************************************
    Integer checkDig(Board b) {
        // Check dig's
        char[][] array = b.getBoardArray();
        if(array[0][0] == array[1][1] && array[1][1] == array[2][2]) {
            if(array[0][0] == player1Char) {
                return MAX_SCORE;
            }
            else if(array[0][0] == player2Char) {
                return MIN_SCORE;
            }
        }
        if(array[0][2]==array[1][1] && array[1][1] == array[2][0]) {
            if(array[0][2] == player1Char) {
                return MAX_SCORE;
            }
            else if(array[0][2] == player2Char) {
                return MIN_SCORE;
            }
        }
        return null;
    }
    //***************************************************************************


    //***************************************************************************
    private Integer checkColWin(Board b) {
        // CheckWinCol
        char[][] array = b.getBoardArray();
        for(int j = 0; j < b.BOARD_SIZE_Y ;j++) {
            if(array[0][j] == array[1][j] && array[1][j] == array[2][j]) {
                if(array[0][j] == player1Char) {
                    return MAX_SCORE;
                }
                else if(array[0][j] == player2Char) {
                    return MIN_SCORE;
                }
            }
        }
        return null;
    }
    //***************************************************************************


    //***************************************************************************
    private Integer checkRowWin(Board b) {
        // CheckWin Rows
        char[][] array = b.getBoardArray();
        for(int i = 0; i < b.BOARD_SIZE_X; i++) {
            if(array[i][0] == array[i][1] && array[i][1] ==array [i][2]) {
                if(array[i][0] == player1Char) {
                    return MAX_SCORE;
                }
                else if(array[i][0] == player2Char){
                    return MIN_SCORE;
                }
            }
        }
        return null;
    }
    //***************************************************************************

}
