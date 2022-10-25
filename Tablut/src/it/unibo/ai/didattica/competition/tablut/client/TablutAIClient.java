

package it.unibo.ai.didattica.competition.tablut.client;
import it.unibo.ai.didattica.competition.tablut.domain.*;
import it.unibo.ai.didattica.competition.tablut.improvements.CompleteState;

import java.io.IOException;
import java.util.List;
import java.util.Random;

public class TablutAIClient extends TablutClient {
    int gameType;
    Game rules;

    public TablutAIClient(String player, String name, int timeout, String ipAddress, int gameType) throws IOException {
        super(player, name, timeout, ipAddress);
        System.out.printf("You are %s player. Timeout = %d. IP address = %s\n", player, timeout, ipAddress);
        this.gameType = gameType;
        this.rules = this.getRules();
    }

    public static void main(String[] args) throws IOException {
        String player = "";
        String name = "random";
        String ipAddress = "localhost";
        int timeout = 60;

        if (args.length < 1) {
            System.out.println("You must specify which player you are (white or black)");
            System.exit(-1);
        } else {
            player = args[0];
        }

        if (args.length >= 2) {
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

    @Override
    public void run() {
        System.out.println("Running...");

        while (true){
            System.out.println("Loop");
            try {
                this.read();
            } catch (ClassNotFoundException | IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                System.exit(1);
            }

            CompleteState completeState = new CompleteState(this.getCurrentState(), this.rules);
            State.Turn turn = completeState.getTurn();
            State.Turn player = this.getPlayer();

            if(turn.equals(player)) {
                List<Action> actionList = completeState.getAllValidActions();

                Random randomizer = new Random();
                Action randomAction = actionList.get(randomizer.nextInt(actionList.size()));

                try {
                    this.write(randomAction);
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