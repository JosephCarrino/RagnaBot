package it.unibo.ai.didattica.competition.tablut.localEmulator;

import it.unibo.ai.didattica.competition.tablut.domain.*;
import it.unibo.ai.didattica.competition.tablut.improvements.CompleteState;

import java.io.IOException;
import java.util.List;
import java.util.Random;

public class TablutLocalRandomClient extends TablutLocalClient {
    int gameType;
    Game rules;

    public TablutLocalRandomClient(String player, String name, int timeout, String ipAddress, int gameType) throws IOException {
        super(player);
        System.out.printf("You are %s player. Timeout = %d. IP address = %s\n", player, timeout, ipAddress);
        this.gameType = gameType;
        this.rules = this.getRules();
    }

    @Override
    public void run() {
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
            }
            CompleteState completeState = new CompleteState(this.getCurrentState(), this.rules);
            State.Turn turn = completeState.getTurn();
            State.Turn player = this.getPlayer();

            System.out.println(this.getPlayer());

            if(turn.equals(player)) {
                System.out.println("Getting next move");
                List<Action> actionList = completeState.getAllValidActions();

                Random randomizer = new Random();
                Action randomAction = actionList.get(randomizer.nextInt(actionList.size()));

                try {
                    this.write(randomAction);
                } catch (ClassNotFoundException | IOException e) {
                    e.printStackTrace();
                }

            } else {
                if(this.checkWin(turn, player)){
                    return;
                }
            }

        }

    }

    private boolean checkWin(State.Turn turn, State.Turn player){
        State.Turn winner = null;
        switch (turn) {
            case WHITEWIN -> winner = State.Turn.WHITE;
            case BLACKWIN -> winner = State.Turn.BLACK;
            case DRAW -> {
                System.out.println("DRAW!");
            }
            default -> {
                return false;
            }
        }

        if (winner.equals(player)) {
            System.out.println("YOU WIN!" + player);
        } else{
            System.out.println("YOU LOSE!" + player);
        }
        return true;
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