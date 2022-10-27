package it.unibo.ai.didattica.competition.tablut.localEmulator;

import it.unibo.ai.didattica.competition.tablut.domain.State;

import java.util.ArrayList;
import java.util.List;

public class GameData {
    public List<State> states;
    public State.Turn finalResult;

    public GameData(){
        this.states = new ArrayList<>();
    }

    public void appendState(State state){
        System.out.println(Thread.currentThread().getName() + " Appending ");
        this.states.add(state);
    }

    private String serializeState(State state){
        String separator = ",";
        int boardLength = state.getBoard().length;

        System.out.println("Ciao");

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

        System.out.println("Finished");


        return blackMatrix + separator + whiteMatrix + separator + kingMatrix;
    }

    public String serialize() {
        if (this.finalResult.equals(State.Turn.DRAW)){
            return "";
        }

        String stateSeparator = ";";
        //String finalSeparator = ".";

        int whiteWin = 1;
        int blackWin = 0;

        StringBuilder result = new StringBuilder();

        System.out.println("Serializing...");
        System.out.println("State length : " + this.states.size());

        for(int i = 0; i < this.states.size(); i++){
            System.out.println(Thread.currentThread().getName() + " Serializing " + i );
            State state = this.states.get(i);
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
