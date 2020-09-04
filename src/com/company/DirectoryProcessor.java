package com.company;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

public class DirectoryProcessor implements FileVisitor {

    Path path_to_files;

    public DirectoryProcessor(Path path_to_files) {
        this.path_to_files = path_to_files;
    }

    @Override
    public FileVisitResult preVisitDirectory(Object o, BasicFileAttributes basicFileAttributes) throws IOException {

        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Object o, BasicFileAttributes basicFileAttributes) throws IOException {

        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Object o, IOException e) throws IOException {
        return null;
    }

    @Override
    public FileVisitResult postVisitDirectory(Object o, IOException e) throws IOException {


        String przetworzone = o.toString().replace('\\',' ');
        String nazwa_jezyka;
        String[] nazwa = przetworzone.split(" ");
        nazwa_jezyka = nazwa[nazwa.length-1];

        if(!nazwa_jezyka.equals("TRAIN"))
        Main.mapa_processorow.put(nazwa_jezyka, new FileProcessor(Path.of(o.toString())));

        return FileVisitResult.CONTINUE;
    }
}
