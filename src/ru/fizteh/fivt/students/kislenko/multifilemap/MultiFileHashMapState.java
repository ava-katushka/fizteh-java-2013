package ru.fizteh.fivt.students.kislenko.multifilemap;

import ru.fizteh.fivt.students.kislenko.filemap.CommandUtils;
import ru.fizteh.fivt.students.kislenko.filemap.FatherState;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicReference;

public class MultiFileHashMapState extends FatherState {
    private Path databasePath;
    private MyTable currentTable;
    private MyTableProvider tables;

    public MultiFileHashMapState(Path p) {
        databasePath = p;
        MyTableProviderFactory factory = new MyTableProviderFactory();
        tables = factory.create(p.toString());
        currentTable = null;
    }

    public Path getPath() {
        return databasePath;
    }

    public void deleteTable(String tableName) {
        tables.removeTable(tableName);
    }

    public void createTable(String tableName) {
        tables.createTable(databasePath.resolve(tableName).toString());
    }

    public MyTable getCurrentTable() {
        return currentTable;
    }

    public Path getWorkingPath() {
        if (currentTable == null) {
            return databasePath;
        } else {
            return databasePath.resolve(currentTable.getName());
        }
    }

    public void setCurrentTable(String name) {
        if (name == null) {
            currentTable = null;
        } else {
            currentTable = tables.getTable(databasePath.resolve(name).toString());
        }
    }

    @Override
    public boolean alright(AtomicReference<Exception> checkingException, AtomicReference<String> message) {
        return CommandUtils.multiTablePutGetRemoveAlright(currentTable, checkingException, message);
    }

    @Override
    public String get(String key, AtomicReference<Exception> exception) {
        TwoLayeredString twoLayeredKey = new TwoLayeredString(key);
        try {
            Utils.loadFile(currentTable, twoLayeredKey);
        } catch (IOException e) {
            exception.set(e);
            return null;
        }
        return currentTable.get(key);
    }

    @Override
    public void put(String key, String value, AtomicReference<Exception> exception) {
        TwoLayeredString twoLayeredKey = new TwoLayeredString(key);
        try {
            Utils.loadFile(currentTable, twoLayeredKey);
        } catch (IOException e) {
            exception.set(e);
        }
        currentTable.put(key, value);
    }

    @Override
    public void remove(String key, AtomicReference<Exception> exception) {
        TwoLayeredString twoLayeredKey = new TwoLayeredString(key);
        try {
            Utils.loadFile(currentTable, twoLayeredKey);
        } catch (IOException e) {
            exception.set(e);
        }
        currentTable.remove(key);
    }
}
