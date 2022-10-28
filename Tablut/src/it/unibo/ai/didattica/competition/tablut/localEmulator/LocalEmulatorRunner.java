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

public class LocalEmulatorRunner {

    private static final PrintStream printStream = System.out;
    public static void main(String[] args) throws Exception {
        int gameCount = Integer.parseInt(args[0]);
        String fileOut = args[1];
        boolean guiOn = Boolean.parseBoolean(args[2]);
        boolean startOnRandom = Boolean.parseBoolean(args[3]);

        List<State> randomState = new ArrayList<>();
        if(startOnRandom){
            randomState = generateRandomStates(25);
        }

        StringBuilder serializedGameData = new StringBuilder();
        for(int i = 0; i < gameCount; i++) {
            printAndDisablePrint("Running game " + i);

            State startingRandomState = null;
            if (startOnRandom){
                printAndDisablePrint("Selecting a random starting state ");

                Random randomGenerator = new Random();
                int index = randomGenerator.nextInt(randomState.size());
                startingRandomState = randomState.get(index).clone();
            }

            Emulator emulator = new Emulator(4, guiOn, startingRandomState);

            TablutLocalClient clientWhite = new TablutLocalRandomClient("WHITE" ,"", 60, "", 4);
            TablutLocalClient clientBlack = new TablutLocalRandomClient("BLACK" ,"", 60, "", 4);

            GameData gameData = runEmulator(emulator, clientWhite, clientBlack);
            serializedGameData.append(gameData.serialize()).append("\n");
        }

        Files.writeString(Paths.get(fileOut), serializedGameData.toString(), StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        System.exit(1);
    }

    public static void printAndDisablePrint(String msg){
        System.setOut(printStream);
        System.out.println(msg);
        disablePrint();
    }

    public static void disablePrint(){
        System.setOut(new PrintStream(new OutputStream() {
            @Override
            public void write(int b){

            }
        }));
    }

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

}
