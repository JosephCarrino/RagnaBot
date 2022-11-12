package it.unibo.ai.didattica.competition.tablut.localEmulator;

import it.unibo.ai.didattica.competition.tablut.domain.State;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Base class for running a local emulation of a game of Tablut between two differnt client
 */
public abstract class LocalRunner {
    // Used for disabling printing to console
    private static final PrintStream printStream = System.out;

    // Game one wants to emulate
    int gameCount;

    // The output file for the gamedata
    String fileOut;

    // Whether the gui should be on
    boolean guiOn;

    // Whether to start on random starting state each game
    boolean startOnRandom;

    // Whether to shuffle the role of the two clients
    boolean shufflePlayer;

    // The timeout given to the clients
    int timeout;

    public LocalRunner(int gameCount, String fileOut, boolean guiOn, boolean startOnRandom, boolean shufflePlayer, int timeout){
        this.gameCount = gameCount;
        this.fileOut = fileOut;
        this.guiOn = guiOn;
        this.startOnRandom = startOnRandom;
        this.shufflePlayer = shufflePlayer;
        this.timeout = timeout;
    }

    /**
     * Execute the runner
     */
    public void executeRunner() throws IOException {
        List<State> randomState = new ArrayList<>();
        if(startOnRandom){
            randomState = generateRandomStates(25);
        }


        Random randomGeneratorForState = new Random();
        Random randomGeneratorForShuffle = new Random();

        int firstPlayerWin = 0;
        int secondPlayerWin = 0;
        int draw = 0;

        StringBuilder serializedGameData = new StringBuilder();
        for(int i = 0; i < gameCount; i++) {
            printAndDisablePrint("\nRunning game " + i);

            State startingRandomState = null;
            if (startOnRandom){
                int index = randomGeneratorForState.nextInt(randomState.size());
                startingRandomState = randomState.get(index).clone();
            }

            Emulator emulator = new Emulator(4, guiOn, startingRandomState);

            String firstPlayer = "WHITE";
            String secondPlayer = "BLACK";

            if (shufflePlayer){
                if(randomGeneratorForShuffle.nextBoolean()){
                    firstPlayer = "WHITE";
                    secondPlayer = "BLACK";
                } else{
                    firstPlayer = "BLACK";
                    secondPlayer = "WHITE";
                }
            }

            printAndDisablePrint("First player is " + firstPlayer + " Second player is " + secondPlayer);

            TablutLocalClient firstClient = this.getFirstClient(firstPlayer, timeout, 4);
            TablutLocalClient secondClient = this.getSecondClient(secondPlayer, timeout, 4);

            GameData gameData = runEmulator(emulator, firstClient, secondClient);

            switch (gameData.finalResult){
                case BLACKWIN -> {
                    if (firstPlayer.equals("BLACK")){
                        printAndDisablePrint("First player has won");
                        firstPlayerWin++;
                    } else{
                        printAndDisablePrint("Second player has won");
                        secondPlayerWin++;
                    }
                }
                case WHITEWIN -> {
                    if (firstPlayer.equals("WHITE")){
                        firstPlayerWin++;
                        printAndDisablePrint("First player has won");
                    } else{
                        printAndDisablePrint("Second player has won");
                        secondPlayerWin++;
                    }
                }
                case DRAW -> {
                    printAndDisablePrint("draw");
                    draw++;
                }
                default -> {
                }
            }

            serializedGameData.append(gameData.serialize()).append("\n");
        }

        printAndDisablePrint("\nNumber of first player wins " + firstPlayerWin + ". Number of second player wins " + secondPlayerWin + ". Number of draw " + draw);

        Files.writeString(Paths.get(fileOut), serializedGameData.toString(), StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        // System.exit(1);
    }

    /**
     * Get the first client. This can be overridden in sub-class to change the clients
     */
    public abstract TablutLocalClient getFirstClient(String player, int timeout, int gameType) throws IOException;


    /**
     * Get the second client. This can be overridden in sub-class to change the clients
     */
    public abstract TablutLocalClient getSecondClient(String player, int timeout, int gameType) throws IOException;

    /**
     * Run an emulator for a single game between two client
     */
    public static GameData runEmulator(Emulator emulator, TablutLocalClient clientWhite, TablutLocalClient clientBlack){
        clientWhite.setEmulator(emulator);
        clientBlack.setEmulator(emulator);

        emulator.start();

        Logger logger = Logger.getLogger("GameLog");
        logger.setLevel(Level.OFF);

        Thread whiteThread = new Thread(clientWhite);
        Thread blackThread = new Thread(clientBlack);
        whiteThread.start();
        blackThread.start();

        try{
            whiteThread.join();
            blackThread.join();
        } catch (Exception e){
            e.printStackTrace();
        }

        return emulator.gameData;
    }

    /**
     * Generate a list of random state
     */
    public static List<State> generateRandomStates(int randomGameToPlay) throws IOException {
        List<State> randomState = new ArrayList<>();
        disablePrint();
        for(int i = 0; i < randomGameToPlay; i++){
            Emulator emulator = new Emulator(4, false);
            TablutLocalClient clientWhite = new TablutLocalRandomClient("WHITE" ,"", 60, "", 4);
            TablutLocalClient clientBlack = new TablutLocalRandomClient("BLACK","", 60, "", 4);

            GameData gameData = runEmulator(emulator, clientWhite, clientBlack);

            // We remove the last state since is a winning state
            gameData.states.remove(gameData.states.size() - 1);

            randomState.addAll(gameData.states);
        }
        return randomState;
    }


    /**
     * Used to print and then disable printing
     */
    public static void printAndDisablePrint(String msg){
        enablePrint();
        System.out.println(msg);
        disablePrint();
    }

    /**
     * Enable printing to console
     */
    public static void enablePrint(){
        System.setOut(printStream);
    }

    /**
     * Disable printing to console
     */
    public static void disablePrint(){
        System.setOut(new PrintStream(new OutputStream() {
            @Override
            public void write(int b){

            }
        }));
    }

}
