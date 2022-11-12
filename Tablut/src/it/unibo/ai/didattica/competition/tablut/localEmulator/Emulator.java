package it.unibo.ai.didattica.competition.tablut.localEmulator;

import it.unibo.ai.didattica.competition.tablut.domain.*;
import it.unibo.ai.didattica.competition.tablut.gui.Gui;

import java.io.*;

/**
 * Class used for running locally a game of Tablut. It tracks the evolution of the game so that they can be used later.
 * The game can start from a particular state or from the correct initial state.
 */
public class Emulator {

    // Current state of the game
    public State state;

    //Rules selected of the game
    public final Game rules;

    // Gui in use for display the game
    public Gui gui;

    //Whether the gui should be displayed or not
    public boolean guiOn;

    //The game type in use
    public final int gameType;

    //Whether the game is currently running
    public boolean isGameRunning;

    //Gamedata object storing information about the evolution of the game
    public final GameData gameData = new GameData();

    public Emulator(int gameType, boolean guiOn, State startingState) {
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
                this.rules = new GameAshtonTablut(2, 1000, "garbage", "fake", "fake");
            }
            default -> {
                System.out.println("Error in game selection");
                throw new IllegalArgumentException("GameType not valid");
            }
        }

        if (startingState != null)
            this.state = startingState.clone();

        this.guiOn = guiOn;
    }

    public Emulator(int gameType, boolean guiOn) {
        this(gameType, guiOn, null);
    }

    /**
     * Start the emulator.
     */
    public void start() {
        if (this.guiOn) {
            this.gui = new Gui(this.gameType);
            this.gui.update(this.state);
        }
        this.isGameRunning = true;
    }

    /**
     * Declare name to the emulator, in practice doesn't do anything
     */
    public void declareName() {
        // do nothing
    }

    /**
     * Write an action to the emulator. The action is first checked then applied to the internal state.
     * The gui is updated and also the Gamedata
     */
    public void write(Action action) throws IOException {
        if (!this.isGameRunning) {
            System.out.println("Tried to write action while game is not running");
            return;
        }

        if (!action.getTurn().equals(this.state.getTurn())) {
            System.out.println("Tried to write action while not your turn");
            return;
        }

        try {
            this.state = this.rules.checkMove(this.state, action);

            if (this.guiOn) {
                this.gui.update(this.state);
            }

            this.gameData.appendState(this.state.clone());

            this.checkFinish();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Read the current state of the emulator.
     */
    public State read() throws IOException {
        return this.state;
    }

    /**
     * Check whether the game is in a finished state.
     */
    private void checkFinish() {
        switch (this.state.getTurn()) {
            case BLACKWIN, WHITEWIN, DRAW -> {
                this.gameData.finalResult = this.state.getTurn();
                this.isGameRunning = false;
                if (this.guiOn) {
                    this.gui.close();
                }
            }
            default -> {
            }
        }
    }
}