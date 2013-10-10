package ru.fizteh.fivt.students.vyatkina.shell;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Pattern;

public class Shell {

   FileManager fileManager = new FileManager ();
   private HashMap <String,Command> COMMAND_MAP = new HashMap<> ();
   String possibleCommands = "pwd|dir|exit|cd|mkdir|cp|rm|mv";

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
          StringBuilder sb = new StringBuilder ();
          for (String arg: args) {
            sb.append (arg);
            sb.append (" ");
          }
          shell.startPacketMode (sb.toString ());
        }
    }

    void startPacketMode (String input) {
      String [] commandsWithArgs = input.trim ().split ("\\s*;\\s*");
        for (String command: commandsWithArgs) {
            String [] splitted = command.split ("\\s+");
            if (Pattern.matches (possibleCommands , splitted [0]) ) {
              String cmd = splitted [0];
              int argsNumber = COMMAND_MAP.get (cmd).getArgumentCount ();
              if (splitted.length - 1 == argsNumber) {
                  String [] args = new String [argsNumber];
                  for (int i = 0; i < argsNumber; i++) {
                      args [i] = splitted [i + 1];
                  }
                  executeCommandPacketMode (COMMAND_MAP.get (cmd),args);
              } else {
                  throw new RuntimeException ("Wrong number of arguments in " + cmd +": needed: " + argsNumber
                          + " have: " + (splitted.length - 1));
              }
            } else {
                throw new RuntimeException ("Unknown command: [" + splitted[0] + "]");
            }
        }


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


}
