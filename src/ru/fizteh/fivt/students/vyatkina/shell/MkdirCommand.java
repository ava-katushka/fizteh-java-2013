package ru.fizteh.fivt.students.vyatkina.shell;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MkdirCommand implements Command {

    private final FileManager fileManager;

    MkdirCommand (FileManager fileManager) {
        this.fileManager = fileManager;
    }

    @Override
    public void execute (String[] args)  {
        Path newDir = Paths.get (args[0]);
        try {
        fileManager.makeDirectory (newDir);
        } catch (IOException e) {
            throw new IllegalArgumentException ("Failed to make a directory [" + newDir + "]");
        }

    }

    @Override
    public String getName () {
        return "mkdir";
    }

    @Override
    public int getArgumentCount () {
        return 1;
    }
}
