package it.unibo.ai.didattica.competition.tablut.localEmulator;

import java.io.IOException;

public class LocalTester {
    public static void main(String[] args) throws IOException, NoSuchMethodException {

        TablutLocalClient clientWhite = new TablutAILocalClient("WHITE" ,"", 60, "", 4);
        TablutLocalClient clientBlack = new TablutAILocalClient("BLACK","", 60, "", 4);

        Emulator emulator = new Emulator(clientWhite, clientBlack, 4, true);
        clientWhite.setEmulator(emulator);
        clientBlack.setEmulator(emulator);

        emulator.start();

    }
}
