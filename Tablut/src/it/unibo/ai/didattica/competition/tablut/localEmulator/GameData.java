package it.unibo.ai.didattica.competition.tablut.localEmulator;

import it.unibo.ai.didattica.competition.tablut.domain.State;

import java.util.ArrayList;
import java.util.List;

public class GameData {
    public final static String stateSeparator = ";";
    public final static String matrixSeparator = ",";
    public final static int whiteWin = 1;
    public final static int blackWin = 0;

    public List<State> states;
    public State.Turn finalResult;

    public GameData(){
        this.states = new ArrayList<>();
    }

    public void appendState(State state){
        this.states.add(state);
    }

    private String serializeState(State state){
        int boardLength = state.getBoard().length;

        StringBuilder blackMatrix = new StringBuilder();
        StringBuilder whiteMatrix = new StringBuilder();
        StringBuilder kingMatrix = new StringBuilder();

        for (int i = 0; i < boardLength; i++) {
            for (int j = 0; j < boardLength; j++) {
                State.Pawn pawn = state.getBoard()[i][j];
                switch (pawn){
                    case KING -> {
                        kingMatrix.append(1);
                        whiteMatrix.append(0);
                        blackMatrix.append(0);
                    }
                    case WHITE -> {
                        kingMatrix.append(0);
                        whiteMatrix.append(1);
                        blackMatrix.append(0);
                    }
                    case BLACK -> {
                        kingMatrix.append(0);
                        whiteMatrix.append(0);
                        blackMatrix.append(1);
                    }
                    default -> {
                        kingMatrix.append(0);
                        whiteMatrix.append(0);
                        blackMatrix.append(0);
                    }
                }

            }
        }

        return blackMatrix + matrixSeparator + whiteMatrix + matrixSeparator + kingMatrix;
    }

    public String serialize() {
        if (this.finalResult.equals(State.Turn.DRAW)){
            return "";
        }
        StringBuilder result = new StringBuilder();
        for (State state : this.states) {
            result.append(this.serializeState(state)).append(stateSeparator);
        }

        int win = blackWin;
        if (this.finalResult.equals(State.Turn.WHITEWIN)){
            win = whiteWin;
        }
        result.append(win);
        return result.toString();
    }
}
