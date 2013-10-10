package ru.fizteh.fivt.students.vyatkina.shell;

import java.nio.file.Path;
import java.nio.file.Paths;

public class RmCommand implements Command {
    FileManager fileManager;

    RmCommand (FileManager fileManager) {
      this.fileManager = fileManager;
    }

    @Override
    public void execute (String[] args) throws RuntimeException {
        Path file = Paths.get (args[0]);
        fileManager.deleteFile (file);
    }

    @Override
    public String getName () {
        return "rm";
    }

    @Override
    public int getArgumentCount () {
        return 1;
    }
}
