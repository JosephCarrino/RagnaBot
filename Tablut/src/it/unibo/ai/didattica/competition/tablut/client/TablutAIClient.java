package it.unibo.ai.didattica.competition.tablut.client;
import it.unibo.ai.didattica.competition.tablut.domain.*;
import it.unibo.ai.didattica.competition.tablut.improvements.CompleteState;

import java.io.IOException;

public class TablutAIClient extends TablutClient {
    int gameType;

    public TablutAIClient(String player, String name, int timeout, String ipAddress, int gameType) throws IOException {
        super(player, name, timeout, ipAddress);
        this.gameType = gameType;
    }

    public static void main(String[] args) throws IOException {
        State state = new StateTablut();
        Game rules = new GameTablut();
        CompleteState completeState = new CompleteState(rules, state);

        System.out.println(completeState.getPlayerPieces(State.Turn.WHITE).size());
        System.out.println(completeState.getPlayerPieces(State.Turn.BLACK).size());
        System.out.println(completeState.getAllValidActions());
        System.out.println(completeState.getPlayerPieces(State.Turn.WHITE).get(0));

        // TablutAIClient client = new TablutAIClient("white", "name", 60, "localhost", 4);
        // client.run();
    }

    @Override
    public void run() {
        State state = new StateTablut();
        Game rules = new GameTablut();
        CompleteState completeState = new CompleteState(rules, state);

    }
}
