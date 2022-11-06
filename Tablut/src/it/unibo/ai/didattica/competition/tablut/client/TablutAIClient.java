

package it.unibo.ai.didattica.competition.tablut.client;
import it.unibo.ai.didattica.competition.tablut.domain.*;
import it.unibo.ai.didattica.competition.tablut.improvements.CompleteState;

import java.io.*;
import java.util.List;
import java.util.Random;

public class TablutAIClient extends TablutClient {
    int gameType;
    Game rules;

    int blackWinIdx = 0;
    int whiteWinIdx = 1;

    public TablutAIClient(String player, String name, int timeout, String ipAddress, int gameType) throws IOException {
        super(player, name, timeout, ipAddress);
        System.out.printf("You are %s player. Timeout = %d. IP address = %s\n", player, timeout, ipAddress);
        this.gameType = gameType;
        this.rules = this.getRules();
    }

    /***
     *
     * @param args They are passed using '-Dargs="{color} {playername} {timeout} {ipaddress}"', only 'color' is required
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        String player = "";
        //String player = "WHITE";
        String name = "random";
        String ipAddress = "localhost";
        int timeout = 60;
        System.out.println(args.length);
        if (args.length < 1) {
            System.out.println("You must specify which player you are (black or white)");
            System.exit(-1);
        } else {
            player = args[0];
        }

        if (args.length >= 2)
            name = args[1];

        if (args.length >= 3){
            try {
                timeout = Integer.parseInt(args[2]);
            } catch (Exception e) {
                System.out.println("Timeout must be an integer value");
                System.exit(-1);
            }
        }

        if (args.length == 4) {
            ipAddress = args[3];
        }

        TablutAIClient client = new TablutAIClient(player, name, timeout, ipAddress, 4);
        client.run();
    }

    /**
     * A util function that thanks to a Python3 model calculates the winning probability for both players
     * @param serializedState The state you want to evaluate
     * @return An array of winning probabilities
     */
    private Double[] getWinProbs(String serializedState){
        String s = null;
        String toRet = "";
        Double[] winProbs = {0.0, 0.0};
        try{
            String myDir = "python3";
            String myCmd = "./model/model_builder.py";
            String[] cmdarray = {myDir, myCmd, serializedState}; 
            Process p = Runtime.getRuntime().exec(cmdarray);
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
            // BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

            while ((s = stdInput.readLine()) != null) {
                toRet = toRet.concat(s);
            }

            String[] winStringProbs = toRet.split(",");
            winProbs[0] = Double.parseDouble(winStringProbs[0]);
            winProbs[1] = Double.parseDouble(winStringProbs[1]);
        } catch ( IOException e ){
            e.printStackTrace();
        }
        return winProbs;
    }

    /**
     * Util function that calculates all next possible states from the current one
     * @param possibleActions All legal actions that can be performed from current state
     * @return An array of next possible serialized states
     */
    private String[] getPossibleSerializedStates( List<Action> possibleActions){
        String[] toRet = new String[possibleActions.size()];
        for (int i=0; i < possibleActions.size(); i++){
            try{
                Game dummyGame = this.getRules();
                State currState = this.getCurrentState().clone();
                State newState = dummyGame.checkMove(currState, possibleActions.get(i));
                CompleteState serializer = new CompleteState(newState, dummyGame);
                toRet[i] = serializer.serializeState();
            } catch ( Exception e ){
                e.printStackTrace();
            }
        }
        return toRet;
    }

    @Override
    public void run() {
        try {
            this.declareName();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Running...");

        int myProbIdx = blackWinIdx;
        if (this.getPlayer().equals("W")){
            myProbIdx = whiteWinIdx;
        }

        while (true){
            try {
                this.read();
            } catch (ClassNotFoundException | IOException e1) {
                e1.printStackTrace();
                System.exit(1);
            }
            System.out.println("Current state: ");
            CompleteState completeState = new CompleteState(this.getCurrentState(), this.rules);
            State.Turn turn = completeState.getTurn();
            State.Turn player = this.getPlayer();

            if(turn.equals(player)) {
                List<Action> actionList = completeState.getAllValidActions();
                // String serializedState = completeState.serializeState();
                String[] allNextState = getPossibleSerializedStates(actionList);
                Double bestRate = 0.0;
                Action bestAction = actionList.get(0);
                for(int i=0; i < allNextState.length; i++){
                    Double[] winningRate = getWinProbs(allNextState[i]);
                    if (winningRate[myProbIdx] >= bestRate){
                        bestRate = winningRate[myProbIdx];
                        bestAction = actionList.get(i);
                    }
                }
                System.out.println(String.valueOf(bestRate));


                // Random randomizer = new Random();
                // Action randomAction = actionList.get(randomizer.nextInt(actionList.size()));

                try {
                    // this.write(randomAction);
                    this.write(bestAction);
                } catch (ClassNotFoundException | IOException e) {
                    e.printStackTrace();
                }

            } else {
                this.checkWin(turn, player);
            }

        }

    }

    private void checkWin(State.Turn turn, State.Turn player){
        State.Turn winner = null;
        switch (turn) {
            case WHITEWIN -> winner = State.Turn.WHITE;
            case BLACKWIN -> winner = State.Turn.BLACK;
            case DRAW -> {
                System.out.println("DRAW!");
                System.exit(0);
            }
            default -> {
                return;
            }
        }

        if (winner.equals(player)) {
            System.out.println("YOU WIN!");
            System.exit(0);
        } else{
            System.out.println("YOU LOSE!");
            System.exit(0);
        }
    }
    private Game getRules(){
        switch (this.gameType) {
            case 1, 3 -> {
                System.out.println("Using standard tablut rules");
                return new GameTablut();
            }
            case 2 -> {
                System.out.println("Using modern tablut rules");
                return new GameModernTablut();
            }
            case 4 -> {
                System.out.println("Using Ashton tablut rules");
                return new GameAshtonTablut(99, 0, "garbage", "fake", "fake");
            }
        }
        throw new IllegalArgumentException(String.format("Game type %d is not a valid game type", this.gameType));
    }
}