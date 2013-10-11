package ru.fizteh.fivt.students.vyatkina.shell;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created with IntelliJ IDEA.
 * User: ava_katushka
 * Date: 08.10.13
 * Time: 18:19
 * To change this template use File | Settings | File Templates.
 */
public class CdCommand implements Command {

    private final FileManager fileManager;

    CdCommand (FileManager fileManager) {
        this.fileManager = fileManager;
    }

    @Override
    public void execute (String[] args) throws IllegalArgumentException {
        Path newDirectory = Paths.get (args [0]);
        fileManager.changeCurrentDirectory (newDirectory);
    }

    @Override
    public String getName () {
        return "cd";
    }

    @Override
    public int getArgumentCount () {
        return 1;
    }
}
