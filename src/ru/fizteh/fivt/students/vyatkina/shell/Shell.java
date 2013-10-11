package ru.fizteh.fivt.students.vyatkina.shell;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Pattern;

public class Shell {

    FileManager fileManager = new FileManager ();
    private HashMap<String, Command> COMMAND_MAP = new HashMap<> ();
    Mode mode;

    enum Mode {
        PACKET,
        INTERACTIVE;
    }

    ;

    Shell (Collection<Command> commands, Mode mode) {
        if (commands == null) {
            commands = standardCommands ();
        }

        for (Command c : commands) {
            COMMAND_MAP.put (c.getName (), c);
        }

        this.mode = mode;
    }

    Set<Command> standardCommands () {
        Set<Command> commands = new HashSet ();
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


    public static void main (String[] args) {
        Shell shell;
        if (args.length == 0) {
            shell = new Shell (null, Mode.INTERACTIVE);
        } else {
            shell = new Shell (null, Mode.PACKET);
        }
         shell.startWork (args);
     }

    void startWork (String [] args) {
        switch (mode) {
            case INTERACTIVE: {
                startInteractiveMode ();
                break;
            }
            case PACKET: {
                startPacketMode (args);
            }
        }
    }

    private class CommandToExecute {
        Command command;
        String [] args;

        CommandToExecute (Command command, String [] args) {
            this.command = command;
            this.args = args;
        }
    }

    ArrayList <CommandToExecute>  parseArguments (String input) throws IllegalArgumentException {
        ArrayList <CommandToExecute> commandsToExecute = new ArrayList<> ();
        String[] commandsWithArgs = input.trim ().split ("\\s*;\\s*");
        for (String command : commandsWithArgs) {
            String[] splitted = command.split ("\\s+");
            if (COMMAND_MAP.get (splitted [0]) != null) {
                Command cmd = COMMAND_MAP.get (splitted[0]);
                int argsNumber = cmd.getArgumentCount ();
                if (splitted.length - 1 == argsNumber) {
                    String[] args = new String[argsNumber];
                    for (int i = 0; i < argsNumber; i++) {
                        args[i] = splitted[i + 1];
                    }
                    commandsToExecute.add (new CommandToExecute (cmd,args));
                } else {
                    throw new RuntimeException ("Wrong number of arguments in " + cmd.getName () + ": needed: " + argsNumber
                            + " have: " + (splitted.length - 1));
                }
            } else {
                throw new RuntimeException ("Unknown command: [" + splitted[0] + "]");
            }
        }
    return commandsToExecute;
    }

    void startPacketMode (String [] args) {
      StringBuilder sb = new StringBuilder ();
        for (String arg : args) {
            sb.append (arg);
            sb.append (" ");
        }
        try {
            ArrayList <CommandToExecute> commandsToExecute = parseArguments (sb.toString ());
            for (CommandToExecute cmd: commandsToExecute) {
                cmd.command.execute (cmd.args);
            }

        } catch (RuntimeException e) {
            System.out.println (e.getMessage ());
            System.exit (-1);
        }


    }

    void startInteractiveMode () {
       Scanner scanner = new Scanner (System.in);
        while (!Thread.currentThread ().isInterrupted ()) {
            System.out.print (fileManager.getCurrentDirectoryString () + "$ ");
            String line = scanner.nextLine ();
            try {
            ArrayList <CommandToExecute> commandsToExecute = parseArguments (line);
            for (CommandToExecute cmd: commandsToExecute) {
                 cmd.command.execute (cmd.args);
            }
            } catch (RuntimeException e) {
                System.out.println(e.getMessage ());
            }
        }

    }


}
