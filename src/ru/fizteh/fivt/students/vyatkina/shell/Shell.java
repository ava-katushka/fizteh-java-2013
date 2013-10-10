package ru.fizteh.fivt.students.vyatkina.shell;

import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;

public class Shell {

   FileManager fileManager = new FileManager ();
   private HashMap <String,Command> COMMAND_MAP = new HashMap<> ();
   String possibleCommands = "pwd|dir|exit|cd|mkdir|cp|rm|mv";
   String commandSeparator = ";";

   final static String INVALID_NUMBER_OF_ARGUMENTS = "Invalid number of arguments";
   Mode mode;

    enum Mode {
        PACKET,
        INTERACTIVE;
    };

    Shell (Collection <Command> commands, Mode mode) {
        if (commands == null) {
            commands = standardCommands ();
        }

        for (Command c: commands) {
            COMMAND_MAP.put (c.getName (), c);
        }

        this.mode = mode;
    }

    Set <Command> standardCommands () {
        System.out.println ("Shell.standardCommands");
        Set <Command> commands = new HashSet ();
        commands.add (new DirCommand (fileManager));
        commands.add (new PwdCommand (fileManager));
        commands.add (new ExitCommand ());
        commands.add (new CdCommand (fileManager));
        commands.add (new MkdirCommand (fileManager));
        commands.add (new CpCommand (fileManager));
        commands.add (new RmCommand (fileManager));
        commands.add (new MvCommand (fileManager));

        return commands;
    }

    void executeCommandInteractiveMode (Command cmd, String [] args) {
        try {
            cmd.execute (args);
        } catch (Exception e) {
            System.err.println (e.getMessage ());
        }
    }

    void executeCommandPacketMode (Command cmd, String [] args) {
        try {
            cmd.execute (args);
        } catch (Exception e) {
            System.out.println (e.getMessage ());
            System.exit (-1);
        }
    }

    public static void main (String[] args) {
        Shell shell;
        if (args.length == 0) {
           shell = new Shell (null,Mode.INTERACTIVE);
           shell.startInteractiveMode ();
        }
        else {
          shell = new Shell (null,Mode.PACKET);
          shell.startPacketMode (shell.splitPacketModeArgs (args));
        }
    }

    void startPacketMode (ArrayList<String> args) {


    }

    void startInteractiveMode () {
        Scanner scanner = new Scanner (System.in);
        while (!Thread.currentThread ().isInterrupted ()) {
            System.out.print (fileManager.getCurrentDirectoryString () + "$ ");
            if (scanner.hasNext (possibleCommands))  {
                 String cmd = scanner.next (possibleCommands);
                 int argsNumber = COMMAND_MAP.get (cmd).getArgumentCount ();
                 String [] args = new String [argsNumber];
                for (int i = 0; i < argsNumber ; i++) {
                   args [i] = scanner.next ();
                }
                executeCommandInteractiveMode (COMMAND_MAP.get (cmd),args);
               }
            else {
                System.out.println ("Unknown command: " + scanner.next ());
            }
        }

    }

    private ArrayList <String> splitPacketModeArgs (String [] args) {
        ArrayList <String> result = new ArrayList<> ();
        return result;
    }


}
