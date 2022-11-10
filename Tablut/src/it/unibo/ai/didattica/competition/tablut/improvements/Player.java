package it.unibo.ai.didattica.competition.tablut.improvements;
import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;

public interface Player {
    public double eval(State state, State.Turn player);
    public Action makeDecision(State state);
}
