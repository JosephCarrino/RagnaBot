package it.unibo.ai.didattica.competition.tablut.localEmulator;

import it.unibo.ai.didattica.competition.tablut.domain.*;
import it.unibo.ai.didattica.competition.tablut.improvements.GameModel;
import it.unibo.ai.didattica.competition.tablut.improvements.Player;
import it.unibo.ai.didattica.competition.tablut.improvements.PlayerIterativeDeepening;


import java.io.*;

public class TablutLocalAIClient extends TablutLocalClient {
    int gameType;
    int timeout;
    GameModel rules;
    String modelToUse;

    public Player player;


    public TablutLocalAIClient(String player, String name, int timeout, String ipAddress, int gameType, String modelToUse) throws IOException {
        super(player);
        System.out.printf("You are %s player. Timeout = %d. IP address = %s\n", player, timeout, ipAddress);
        this.gameType = gameType;
        this.timeout = timeout;
        this.modelToUse = modelToUse;
        this.rules = this.getRules();
    }

    @Override
    public void run() {
        this.player = new PlayerIterativeDeepening(this.getRules(), -2, 2, this.timeout-2);

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
                return;
            }
            State currentState = this.getCurrentState();
            State.Turn turn = currentState.getTurn();
            State.Turn player = this.getPlayer();

            if(turn.equals(player)) {
                try {
                    Action bestAction = this.player.makeDecision(currentState);
                    this.write(bestAction);
                } catch (ClassNotFoundException | IOException e) {
                    e.printStackTrace();
                    return;
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
            case DRAW, BLACKWIN, WHITEWIN -> {
                return true;
            }
            default -> {
                return false;
            }
        }
    }

    private GameModel getRules(){
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