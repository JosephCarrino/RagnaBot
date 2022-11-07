package it.unibo.ai.didattica.competition.tablut.util;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class PythonProcessInterface {
    private static final String quitCommand = "QUIT";
    private final String pythonCmd;
    private final String pythonSourcePath;
    private BufferedReader input;
    private BufferedWriter output;

    public PythonProcessInterface(String pythonCmd, String pythonSourcePath){
        this.pythonCmd = pythonCmd;
        this.pythonSourcePath = pythonSourcePath;
    }

    public void startPythonProcess() throws IOException {
        this.startPythonProcess(new String[]{});
    }

    public void startPythonProcess(String[] args) throws IOException {
        List<String> commands = new ArrayList<>(List.of(args));
        commands.add(0, this.pythonCmd);
        commands.add(1, this.pythonSourcePath);

        System.out.println(Arrays.toString(commands.toArray(new String[0])));

        Process process = Runtime.getRuntime().exec(commands.toArray(new String[0]));

        this.input = new BufferedReader(new InputStreamReader(process.getInputStream()));
        this.output = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));

        System.out.println("Python process started");
    }

    public void writeToPipe(String msg){
        try {
            this.output.write(msg + "\n");
            this.output.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String readFromPipe(){
        try {
            return this.input.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void close(){
        try{
            this.writeToPipe(PythonProcessInterface.quitCommand);
            this.input.close();
            this.output.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
