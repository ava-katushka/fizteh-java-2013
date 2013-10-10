package ru.fizteh.fivt.students.vyatkina.shell;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.Comparator;

public class DirCommand implements Command {

    FileManager fileManager;

    DirCommand (FileManager fileManager) {
        this.fileManager = fileManager;
    }

    @Override
    public void execute (String[] args) {
      String [] files = fileManager.getSortedCurrentDirectoryFiles ();
      for (String file: files) {
          System.out.println (file);
      }

    }

    @Override
    public String getName () {
        return "dir";
    }

    @Override
    public int getArgumentCount () {
        return 0;
    }
}

