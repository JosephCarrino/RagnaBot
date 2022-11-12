package it.unibo.ai.didattica.competition.tablut.improvements;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.Game;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.util.PythonProcessInterface;

import java.util.List;
import java.io.*;
import java.util.Map;

public class GameModel implements aima.core.search.adversarial.Game<State, Action, State.Turn> {
    public Game rules;
    public static final int blackWinIdx = 0;
    public static final int whiteWinIdx = 1;

    private final Map<String, Double> serializedStateToValue;

    private final PythonProcessInterface modelProcessInterface;

    public GameModel(Game rules, String modelToUse){
        this.rules = rules;
        this.serializedStateToValue = new MaxSizeHashMap<>(150000);

        try{
            this.modelProcessInterface = new PythonProcessInterface("python3", "./model/model_communication_with_pipe.py");
            this.modelProcessInterface.startPythonProcess(new String[]{modelToUse});
        } catch(IOException e){
            throw new RuntimeException();
        }



    }

    private Double[] getWinProbability(String serializedState){
        if (this.serializedStateToValue.containsKey(serializedState)){
            Double value = this.serializedStateToValue.get(serializedState);
            return new Double[]{value, 1 - value};
        }


        this.modelProcessInterface.writeToPipe(serializedState);
        String probabilityString = this.modelProcessInterface.readFromPipe();

        //String[] singleProbability = probabilityString.split(",");

        Double[] winningProbability = {0d,0d};
        winningProbability[1] = Double.parseDouble(probabilityString);//1 - winningProbability[0];
        winningProbability[0] = 1 - winningProbability[1];

        this.serializedStateToValue.put(serializedState, winningProbability[0]);

        return winningProbability;
    }

    @Override
    public State getInitialState() {
        return null;
    }

    @Override
    public State.Turn[] getPlayers() {
        return new State.Turn[]{State.Turn.BLACK, State.Turn.WHITE};
    }

    @Override
    public State.Turn getPlayer(State state) {
        return state.getTurn();
    }

    @Override
    public List<Action> getActions(State state) {
        CompleteState completeState = new CompleteState(state, this.rules);
        return completeState.getAllValidActions();
    }

    @Override
    public State getResult(State state, Action action) {
        try {
            return this.rules.checkMove(state.clone(), action);
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean isTerminal(State state) {
        State.Turn turn=state.getTurn();
        return turn.equals(State.Turn.BLACKWIN) || turn.equals(State.Turn.WHITEWIN) || turn.equals(State.Turn.DRAW);
    }

    @Override
    public double getUtility(State state, State.Turn turn) {
        if(this.isTerminal(state)){
            State.Turn winnerPlayer = state.getTurn();
            switch (turn){
                case WHITE -> {
                    if (winnerPlayer.equals(State.Turn.WHITEWIN))
                        return Double.POSITIVE_INFINITY;
                    else if (winnerPlayer.equals(State.Turn.BLACKWIN)){
                        return Double.NEGATIVE_INFINITY;
                    } else {
                        return 0;
                    }
                }
                case BLACK -> {
                    if (winnerPlayer.equals(State.Turn.WHITEWIN))
                        return Double.NEGATIVE_INFINITY;
                    else if (winnerPlayer.equals(State.Turn.BLACKWIN)){
                        return Double.POSITIVE_INFINITY;
                    } else {
                        return 0;
                    }
                }
            }

        }

        CompleteState completeState = new CompleteState(state, this.rules);
        String serializedState = completeState.serializeState();

        Double[] probability = this.getWinProbability(serializedState);

        int myProbIdx = blackWinIdx;
        if (turn.equals(State.Turn.WHITE)){
            myProbIdx = whiteWinIdx;
        }

        return probability[myProbIdx];
    }
}
