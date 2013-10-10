package ru.fizteh.fivt.students.vyatkina.shell;

import java.nio.file.Path;
import java.nio.file.Paths;

public class MvCommand implements Command {
    FileManager fileManager;

    MvCommand (FileManager fileManager) {
        this.fileManager = fileManager;
    }

    @Override
    public void execute (String[] args) throws RuntimeException {
    Path fromPath = Paths.get (args[0]);
    Path toPath = Paths.get (args[1]);
    fileManager.moveFile (fromPath,toPath);
    }

    @Override
    public String getName () {
        return "mv";
    }

    @Override
    public int getArgumentCount () {
        return 2;
    }
}
