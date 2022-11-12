package it.unibo.ai.didattica.competition.tablut.improvements;

import aima.core.search.adversarial.Game;
import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import aima.core.search.adversarial.IterativeDeepeningAlphaBetaSearch;
import it.unibo.ai.didattica.competition.tablut.localEmulator.LocalRunner;


public class PlayerIterativeDeepening extends IterativeDeepeningAlphaBetaSearch<State,Action, State.Turn> implements Player{
    public PlayerIterativeDeepening(Game<State, Action, State.Turn> game, double utilMin, double utilMax, int time) {
        super(game, utilMin, utilMax, time);
    }

    @Override
    public double eval(State state, State.Turn player) {
        super.eval(state, player);
        return this.game.getUtility(state, player);
    }

    @Override
    public Action makeDecision(State state) {
        Action a = super.makeDecision(state);

        LocalRunner.printAndDisablePrint("Explored a total of " + getMetrics().get(METRICS_NODES_EXPANDED) + " nodes, reaching a depth limit of " + getMetrics().get(METRICS_MAX_DEPTH));

        return a;
    }
}
