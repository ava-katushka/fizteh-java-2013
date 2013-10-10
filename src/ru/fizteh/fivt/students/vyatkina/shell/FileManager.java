package ru.fizteh.fivt.students.vyatkina.shell;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystemLoopException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Arrays;
import java.util.Comparator;
import java.net.URI;
import java.util.EnumSet;

public class FileManager {

    Path currentDirectory = Paths.get ("").toAbsolutePath ();

    public void changeCurrentDirectory (Path newDirectory) throws IllegalArgumentException {
        Path oldDirectory = currentDirectory;

        if (newDirectory.isAbsolute ()) {
            currentDirectory = newDirectory.toAbsolutePath ();
        } else {
            currentDirectory = normalizePath (currentDirectory.toAbsolutePath ().resolve (newDirectory));
        }
        if (!currentDirectory.toFile ().exists () || !currentDirectory.toFile ().isDirectory ()) {
            currentDirectory = oldDirectory;
            throw new IllegalArgumentException ("Unable to enter directory [" + newDirectory + "] it doesn't exists or is a file");
        }
    }

    public Path normalizePath (Path path) throws IllegalArgumentException {
        try {
            String normalized = new URI (path.toString ()).normalize ().getPath ();
            return Paths.get (normalized);
        }
        catch (URISyntaxException e) {
            throw new IllegalArgumentException ("Unable to enter directory [" + path.toString () + "]");
        }
    }

    public String getCurrentDirectoryString () {
        return currentDirectory.toAbsolutePath ().toString ();
    }

    public String[] getSortedCurrentDirectoryFiles () {
        File[] files = currentDirectory.toFile ().listFiles ();
        Arrays.sort (files, new Comparator<File> () {
            @Override
            public int compare (File o1, File o2) {
                if (o1.isDirectory () ^ o2.isDirectory ()) {
                    if (o1.isDirectory ()) {
                        return -1;
                    } else {
                        return 1;
                    }
                } else {
                    return o1.compareTo (o2);
                }
            }
        });

        final String[] res = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            res[i] = files[i].getName ();
        }
        return res;
    }

    public void makeDirectory (Path dir) throws IOException {
        Path newDirectory = currentDirectory.resolve (dir);
        if (!Files.exists (newDirectory)) {
            Files.createDirectory (newDirectory);
        } else {
            throw new IllegalArgumentException ("Fail to create a directory: [" + newDirectory + "]: this directory already exists");
        }
    }

    public void copyFile (Path fromPath, Path toPath) {
        fromPath = currentDirectory.resolve (fromPath);
        toPath = currentDirectory.resolve (toPath);

        if (fromPath.equals (toPath)) {
            throw new IllegalArgumentException ("Try to copy file [" + fromPath + "] to itself: don't do it");
        }

        if (Files.exists (fromPath) && Files.exists (toPath)) {
            try {
                Path destination = Files.isDirectory (toPath) ? toPath.resolve (fromPath.getFileName ()) : toPath;
                Files.walkFileTree (fromPath, EnumSet.of (FileVisitOption.FOLLOW_LINKS),
                        Integer.MAX_VALUE, new CopyDirectoryVisitor (fromPath, destination));

            }
            catch (IOException io) {
                throw new IllegalArgumentException ("Fail to copy [" + fromPath + "] to [" + toPath + "] " + io);
            }
        } else {
            throw new IllegalArgumentException ("Fail to copy [" + fromPath + "] to [" + toPath + "] one of files doesn't exist");
        }
    }

    private class CopyDirectoryVisitor extends SimpleFileVisitor<Path> {

        private Path fromPath;
        private Path toPath;
        private StandardCopyOption copyOption = StandardCopyOption.REPLACE_EXISTING;

        CopyDirectoryVisitor (Path fromPath, Path toPath) {
            this.fromPath = fromPath;
            this.toPath = toPath;
        }

        @Override
        public FileVisitResult preVisitDirectory (Path dir, BasicFileAttributes attr) {
            Path newDir = toPath.resolve (fromPath.relativize (dir));
            try {
                Files.copy (dir, newDir, copyOption);
            }
            catch (FileAlreadyExistsException e) {
                // ignore
            }
            catch (IOException e) {
                throw new RuntimeException ("Unable to create: [" + newDir + "] " + e);
            }
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile (Path file, BasicFileAttributes attrs) {
            try {
                Files.copy (file, toPath.resolve (fromPath.relativize (file)),
                        copyOption);
            }
            catch (IOException e) {
                throw new RuntimeException ("Unable to copy file [" + file + "] " + e);
            }

            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory (Path dir, IOException exc) {
            if (exc == null) {
                Path newDir = toPath.resolve (fromPath.relativize (dir));
                try {
                    FileTime time = Files.getLastModifiedTime (dir);
                    Files.setLastModifiedTime (newDir, time);
                }
                catch (IOException e) {
                    throw new RuntimeException ("Unable to copy all attributes to: [" + newDir + "] " + e);
                }
            }
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed (Path file, IOException exc) {
            if (exc instanceof FileSystemLoopException) {
                throw new RuntimeException ("Cycle detected: " + file);
            } else {
                throw new RuntimeException ("Unable to copy: [" + file + "] " + exc);
            }
        }
    }

    void deleteFile (Path file) throws RuntimeException {
        file = currentDirectory.resolve (file);
        if (Files.notExists (file)) {
            throw new RuntimeException ("Try to delete file [" + file + "] which doesn't exist");
        } else {
            try {
                Files.walkFileTree (file, new DeleteDirectoryVisitor ());
            }
            catch (IOException e) {
                throw new RuntimeException ("Unable to delete file [" + file + "] " + e);
            }
        }
    }

    private class DeleteDirectoryVisitor extends SimpleFileVisitor<Path> {

        @Override
        public FileVisitResult visitFile (Path file, BasicFileAttributes attrs) {
            try {
                Files.delete (file);
            }
            catch (IOException e) {
                throw new RuntimeException ("Unable to delete file [" + file + "] " + e);
            }
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory (Path dir, IOException exc) {
            if (exc == null) {
                try {
                    Files.delete (dir);
                }
                catch (IOException e) {
                    throw new RuntimeException ("Unable to delete directory [" + dir + "] " + e);
                }
            }
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed (Path file, IOException exc) {
            if (exc instanceof FileSystemLoopException) {
                throw new RuntimeException ("Cycle detected: " + file);
            } else {
                throw new RuntimeException ("Unable to delete: [" + file + "] " + exc);
            }
        }
    }

    public void moveFile (Path fromPath, Path toPath) throws RuntimeException {
        fromPath = currentDirectory.resolve (fromPath);
        toPath = currentDirectory.resolve (toPath);

        if (Files.notExists (fromPath) || (Files.isDirectory (toPath) && Files.notExists (toPath))) {
            throw new RuntimeException ("Fail to move [" + fromPath + "] to [" + toPath + "] one of files doesn't exist");
        }
        try {
            Path destination = Files.isDirectory (toPath) ? toPath.resolve (fromPath.getFileName ()) : toPath;
            Files.move (fromPath, destination, StandardCopyOption.REPLACE_EXISTING);
        }
        catch (IOException e) {
            throw new RuntimeException ("Fail to move [" + fromPath + "] to [" + toPath + "] " + e);
        }
    }
}





