package it.unibo.ai.didattica.competition.tablut.improvements;

import aima.core.search.adversarial.Game;
import aima.core.search.adversarial.IterativeDeepeningAlphaBetaSearch;
import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.localEmulator.LocalRunner;


public class PlayerIterativeDeepeningDist extends IterativeDeepeningAlphaBetaSearch<State,Action, State.Turn> implements Player{
    public PlayerIterativeDeepeningDist(Game<State, Action, State.Turn> game, double utilMin, double utilMax, int time) {
        super(game, utilMin, utilMax, time);
    }

    @Override
    public double eval(State state, State.Turn player) {
        super.eval(state, player);
        double util = this.game.getUtility(state, player)/3;
        if (util <= 0){
            System.out.println(util);
        }

        return util;
    }

    @Override
    public Action makeDecision(State state) {
        Action a = super.makeDecision(state);

        LocalRunner.printAndDisablePrint("Explored a total of " + getMetrics().get(METRICS_NODES_EXPANDED) + " nodes, reaching a depth limit of " + getMetrics().get(METRICS_MAX_DEPTH));

        return a;
    }
}
