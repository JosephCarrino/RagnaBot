package it.unibo.ai.didattica.competition.tablut.improvements;

import aima.core.search.adversarial.Game;
import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import aima.core.search.adversarial.IterativeDeepeningAlphaBetaSearch;


public class PlayerIterativeDeepening extends IterativeDeepeningAlphaBetaSearch<State,Action, State.Turn> implements Player{
    public PlayerIterativeDeepening(Game<State, Action, State.Turn> game, double utilMin, double utilMax, int time) {
        super(game, utilMin, utilMax, time);
    }

    @Override
    public double eval(State state, State.Turn player) {
        super.eval(state, player);


        Double toRet = this.game.getUtility(state, player);
        System.out.println(toRet);
        return toRet;
    }

    @Override
    public Action makeDecision(State state) {
        return super.makeDecision(state);
    }
}
