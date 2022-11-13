package it.unibo.ai.didattica.competition.tablut.localEmulator;

import java.io.IOException;

public class LocalRunnerAIVSAI extends LocalRunner{
    String firstModelPath;
    String secondModelPath;

    public static void main(String[] args) throws Exception {
        int gameCount = Integer.parseInt(args[0]);
        String fileOut = args[1];
        boolean guiOn = Boolean.parseBoolean(args[2]);
        boolean startOnRandom = Boolean.parseBoolean(args[3]);
        boolean shufflePlayer = Boolean.parseBoolean(args[4]);
        int timeout = Integer.parseInt(args[5]);

        String firstModelPath = args[6];
        String secondModelPath = args[7];

        LocalRunnerAIVSAI localRunnerAIVSAI = new LocalRunnerAIVSAI(gameCount, fileOut, guiOn, startOnRandom, shufflePlayer, timeout, firstModelPath, secondModelPath);
        localRunnerAIVSAI.executeRunner();
    }

    public LocalRunnerAIVSAI(int gameCount, String fileOut, boolean guiOn, boolean startOnRandom, boolean shufflePlayer, int timeout, String firstModelPath, String secondModelPath) {
        super(gameCount, fileOut, guiOn, startOnRandom, shufflePlayer, timeout);

        this.firstModelPath = firstModelPath;
        this.secondModelPath = secondModelPath;
    }

    @Override
    public TablutLocalClient getFirstClient(String player, int timeout, int gameType) throws IOException {
        return new TablutLocalAIClient(player, "", timeout, "", gameType, this.firstModelPath);
    }

    @Override
    public TablutLocalClient getSecondClient(String player, int timeout, int gameType) throws IOException {
        return new TablutLocalAIClient(player, "", timeout, "", gameType, this.secondModelPath);
    }
}
