package it.unibo.ai.didattica.competition.tablut.localEmulator;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;

import java.io.IOException;

public abstract class TablutLocalClient implements  Runnable {
    private State currentState;
    private State.Turn player;
    private String name;
    private Emulator emulator;

    protected TablutLocalClient(String player) {
        // super(player, "localClient", 60);
        this.player = State.Turn.valueOf(player);
    }

    public Emulator getEmulator() {
        return emulator;
    }

    public void setEmulator(Emulator emulator) {
        this.emulator = emulator;
    }

    public State getCurrentState() {
        return currentState;
    }

    public State.Turn getPlayer() {
        return player;
    }

    public void setPlayer(State.Turn player) {
        this.player = player;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void write(Action action) throws IOException, ClassNotFoundException {
        this.emulator.write(action);
    }

    public void declareName() throws IOException, ClassNotFoundException {
        this.emulator.declareName();
    }

    public void read() throws ClassNotFoundException, IOException {
        this.currentState = this.emulator.read();
    }
    public void run(){

    }
}
