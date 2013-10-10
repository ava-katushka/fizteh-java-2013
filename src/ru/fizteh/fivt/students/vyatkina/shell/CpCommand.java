package ru.fizteh.fivt.students.vyatkina.shell;

import java.nio.file.Path;
import java.nio.file.Paths;


public class CpCommand implements Command {
    FileManager fileManager;

    CpCommand (FileManager fileManager) {
        this.fileManager = fileManager;
    }

    @Override
    public void execute (String[] args) throws IllegalArgumentException {
        Path fromPath = Paths.get (args[0]);
        Path toPath = Paths.get (args [1]);
        fileManager.copyFile (fromPath, toPath);
    }

    @Override
    public String getName () {
        return "cp";
    }

    @Override
    public int getArgumentCount () {
        return 2;
    }
}
