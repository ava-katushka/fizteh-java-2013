package ru.fizteh.fivt.students.vyatkina.shell;

/**
 * Created with IntelliJ IDEA.
 * User: ava_katushka
 * Date: 08.10.13
 * Time: 18:10
 * To change this template use File | Settings | File Templates.
 */
public class ExitCommand implements Command
{
    @Override
    public void execute (String[] args)  {
        System.exit (0);
    }

    @Override
    public String getName () {
        return "exit";
    }

    @Override
    public int getArgumentCount () {
        return 0;
    }
}
