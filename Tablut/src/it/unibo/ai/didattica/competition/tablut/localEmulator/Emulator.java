package it.unibo.ai.didattica.competition.tablut.localEmulator;

import it.unibo.ai.didattica.competition.tablut.domain.*;
import it.unibo.ai.didattica.competition.tablut.gui.Gui;

import java.io.*;

public class Emulator {
    public State state;
    public final Game rules;

    public Gui gui;
    public boolean guiOn;
    public final int gameType;
    public boolean isGameRunning;
    public final GameData gameData = new GameData();

    public Emulator(int gameType, boolean guiOn, State startingState){
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

        if(startingState != null)
            this.state = startingState;

        this.guiOn = guiOn;
    }

    public Emulator(int gameType, boolean guiOn){
        this(gameType, guiOn, null);
    }

    public void start() {
        if(this.guiOn){
            this.gui = new Gui(this.gameType);
            this.gui.update(state);
        }
        this.isGameRunning = true;
    }

    public void declareName(){
        // do nothing
    }

    public void write(Action action) throws IOException {
        if(!this.isGameRunning){
            System.out.println("Tried to write action while game is not running");
            return;
        }

        if (!action.getTurn().equals(this.state.getTurn())){
            System.out.println("Tried to write action while not your turn");
            return;
        }

        try{
            this.state = this.rules.checkMove(this.state, action);

            if(this.guiOn) {
                this.gui.update(this.state);
            }

            this.gameData.appendState(this.state.clone());

            this.checkFinish();
        } catch (Exception exception){
            exception.printStackTrace();
        }
    }

    public State read() throws IOException{
        return this.state;
    }

    private void checkFinish(){
        switch (this.state.getTurn()) {
            case BLACKWIN, WHITEWIN, DRAW -> {
                this.gameData.finalResult = this.state.getTurn();
                this.isGameRunning = false;
            }
            default -> {
            }
        }
    }
}