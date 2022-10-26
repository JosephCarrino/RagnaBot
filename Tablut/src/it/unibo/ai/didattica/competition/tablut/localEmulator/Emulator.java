package it.unibo.ai.didattica.competition.tablut.localEmulator;

import it.unibo.ai.didattica.competition.tablut.domain.*;
import it.unibo.ai.didattica.competition.tablut.gui.Gui;

public class Emulator{
    private final Thread threadWhite;
    private final Thread threadBlack;

    private State state;
    private final Game rules;
    private Gui gui;
    private boolean guiOn;
    private final int gameType;

    public Emulator(TablutLocalClient clientWhite, TablutLocalClient clientBlack, int gameType, boolean guiOn){
        this.gameType = gameType;

        switch (this.gameType) {
            case 1 -> {
                this.state = new StateTablut();
                this.rules = new GameTablut();
            }
            case 2 -> {
                this.state = new StateTablut();
                this.rules = new GameModernTablut();
                System.out.println("Using modern tablut rules");
            }
            case 3 -> {
                this.state = new StateBrandub();
                this.rules = new GameTablut();
                System.out.println("Using standard tablut rules");
            }
            case 4 -> {
                this.state = new StateTablut();
                this.state.setTurn(State.Turn.WHITE);
                this.rules = new GameAshtonTablut(99, 0, "garbage", "fake", "fake");
            }
            default -> {
                System.out.println("Error in game selection");
                throw new IllegalArgumentException("GameType not valid");
            }
        }

        this.guiOn = guiOn;
        if(this.guiOn)
            this.initializeGUI(this.state);

        this.threadWhite = new Thread(clientWhite);
        this.threadBlack = new Thread(clientBlack);
    }

    public void start() {
        this.threadWhite.start();
        this.threadBlack.start();
    }

    public void initializeGUI(State state) {
        // TODO Make it work with other game types
        this.gui = new Gui(this.gameType);
        this.gui.update(state);
    }

    public void declareName(){
        // Do nothing
    }

    public void write(Action action) {
        try{
            this.state = this.rules.checkMove(this.state, action);

            if (this.guiOn)
                this.gui.update(this.state);
        } catch (Exception ignored) {
            System.out.println("Wrong move");
        }
    }

    public State read(){
        this.checkFinish();
        return this.state;
    }

    private void checkFinish(){
        switch (this.state.getTurn()) {
            case BLACKWIN, WHITEWIN, DRAW -> {
                this.threadWhite.interrupt();
                this.threadBlack.interrupt();
            }
            default -> {
            }
        }
    }

}
