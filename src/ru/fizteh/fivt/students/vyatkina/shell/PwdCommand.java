package ru.fizteh.fivt.students.vyatkina.shell;


import java.io.PrintStream;

public class PwdCommand implements Command {

    private final FileManager fileManager;

    PwdCommand (FileManager fileManager) {
       this.fileManager = fileManager;
    }

    @Override
    public void execute (String[] args) {
       System.out.println (fileManager.getCurrentDirectoryString ());
    }

    @Override
    public String getName () {
        return "pwd";
    }

    @Override
    public int getArgumentCount () {
        return 0;
    }
}
