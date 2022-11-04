package it.unibo.ai.didattica.competition.tablut.localEmulator;

import java.io.IOException;

/**
 * Clas for running a local emulation of a game of Tablut between two random client
 */
public class LocalRunnerRandomVSRandom extends LocalRunner{
    public static void main(String[] args) throws Exception {
        int gameCount = Integer.parseInt(args[0]);
        String fileOut = args[1];
        boolean guiOn = Boolean.parseBoolean(args[2]);
        boolean startOnRandom = Boolean.parseBoolean(args[3]);
        boolean shufflePlayer = Boolean.parseBoolean(args[4]);
        int timeout = Integer.parseInt(args[5]);

        LocalRunnerRandomVSRandom localRunnerRandomVSRandom = new LocalRunnerRandomVSRandom(gameCount, fileOut, guiOn, startOnRandom, shufflePlayer, timeout);
        localRunnerRandomVSRandom.executeRunner();
    }

    public LocalRunnerRandomVSRandom(int gameCount, String fileOut, boolean guiOn, boolean startOnRandom, boolean shufflePlayer, int timeout) {
        super(gameCount, fileOut, guiOn, startOnRandom, shufflePlayer, timeout);
    }

    @Override
    public TablutLocalClient getFirstClient(String player, int timeout, int gameType) throws IOException {
        return new TablutLocalRandomClient(player ,"", timeout, "", 4);
    }

    @Override
    public TablutLocalClient getSecondClient(String player, int timeout, int gameType) throws IOException {
        return new TablutLocalRandomClient(player ,"", timeout, "", 4);
    }
}
