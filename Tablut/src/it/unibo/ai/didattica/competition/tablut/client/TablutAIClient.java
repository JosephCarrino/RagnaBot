

package it.unibo.ai.didattica.competition.tablut.client;
import it.unibo.ai.didattica.competition.tablut.domain.*;
import it.unibo.ai.didattica.competition.tablut.improvements.GameModel;
import it.unibo.ai.didattica.competition.tablut.improvements.Player;
import it.unibo.ai.didattica.competition.tablut.improvements.PlayerIterativeDeepening;

import java.io.*;

public class TablutAIClient extends TablutClient{
    int gameType;
    int timeout;
    GameModel rules;
    String modelToUse;

    public Player player;


    public TablutAIClient(String player, String name, int timeout, String ipAddress, int gameType, String modelToUse) throws IOException {
        super(player, name, timeout, ipAddress);
        System.out.printf("You are %s player. Timeout = %d. IP address = %s\n", player, timeout, ipAddress);
        this.gameType = gameType;
        this.timeout = timeout;
        this.modelToUse = modelToUse;
        this.rules = this.getGameModel();

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

        if (args.length >= 4) {
            ipAddress = args[3];
        }

        String modelToUse = "./Tablut/model/regressor.sav";
        if (args.length >= 5){
            modelToUse = args[4];
            System.out.println("Ciao");
        }

        TablutAIClient client = new TablutAIClient(player, name, timeout, ipAddress, 4, modelToUse);
        client.run();
    }

    @Override
    public void run() {
        this.player = new PlayerIterativeDeepening(this.getGameModel(), -2, 2, this.timeout-2);

        try {
            this.declareName();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Running...");

        while (true){
            try {
                this.read();
            } catch (ClassNotFoundException | IOException e1) {
                e1.printStackTrace();
                System.exit(1);
            }
            System.out.println("Current state: ");
            State currentState = this.getCurrentState();
            State.Turn turn = currentState.getTurn();
            State.Turn player = this.getPlayer();

            if(turn.equals(player)) {
                Action bestAction = this.player.makeDecision(currentState);
                try {
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

    private GameModel getGameModel(){
        switch (this.gameType) {
            case 1, 3 -> {
                System.out.println("Using standard tablut rules");
                return new GameModel(new GameTablut(), this.modelToUse);

            }
            case 2 -> {
                System.out.println("Using modern tablut rules");
                return new GameModel(new GameModernTablut(), this.modelToUse);
            }
            case 4 -> {
                System.out.println("Using Ashton tablut rules");
                return new GameModel(new GameAshtonTablut(99, 0, "garbage", "fake", "fake"), this.modelToUse);
            }
        }
        throw new IllegalArgumentException(String.format("Game type %d is not a valid game type", this.gameType));
    }


}