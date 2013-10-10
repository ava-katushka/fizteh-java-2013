package ru.fizteh.fivt.students.vyatkina.shell;

/**
 * Created with IntelliJ IDEA.
 * User: ava_katushka
 * Date: 10.10.13
 * Time: 21:02
 * To change this template use File | Settings | File Templates.
 */
public class RmCommand implements Command {

    @Override
    public void execute (String[] args) throws IllegalArgumentException {
        //To change body of implemented methods use File | Settings | File Templates.
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
