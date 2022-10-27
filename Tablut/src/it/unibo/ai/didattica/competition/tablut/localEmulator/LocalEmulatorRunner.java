package it.unibo.ai.didattica.competition.tablut.localEmulator;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LocalEmulatorRunner {
    public static void main(String[] args) throws Exception {

        PrintStream printStream = System.out;

        int gameCount = Integer.parseInt(args[0]);
        String fileOut = args[1];
        boolean guiOn = Boolean.parseBoolean(args[2]);

        StringBuilder gameData = new StringBuilder();

        for(int i = 0; i < gameCount; i++) {
            System.setOut(printStream);
            System.out.println("Running game " + i);
            System.setOut(new PrintStream(new OutputStream() {
                @Override
                public void write(int b){

                }
            }));

            Emulator emulator = new Emulator(4, guiOn);

            TablutLocalClient clientWhite = new TablutAILocalClient("WHITE" ,"", 60, "", 4);
            TablutLocalClient clientBlack = new TablutAILocalClient("BLACK","", 60, "", 4);

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

            gameData.append(emulator.gameData.serialize()).append("\n\n");
        }

        Files.writeString(Paths.get(fileOut), gameData.toString(), StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        System.exit(1);
    }
}
