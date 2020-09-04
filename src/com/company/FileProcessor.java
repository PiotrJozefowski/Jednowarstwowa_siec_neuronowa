package com.company;

import java.io.*;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;

public class FileProcessor implements FileVisitor {

    Path path;

    FileReader reader;
    String result = "";
    ArrayList<double[]> lista_wektorow = new ArrayList<>();
    double[] counted;


    public FileProcessor(Path path) {
        this.path = path;
    }




    void clear(Path path){

        result = "";
        File file = new File(path.toString());
        int letter;
        try { reader = new FileReader(file);
        } catch (FileNotFoundException e) { e.printStackTrace();
        }

        try {
            while ((letter = reader.read())!= -1) {

                if((letter > 96 && letter < 123) || (letter > 64 && letter < 91)){
                    if(letter>64 && letter < 91){
                        result = result +(char)(letter+32);
                    }else {
                        result = result + (char) letter;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        calcutale();
    }

    void calcutale(){

        counted = new double[26];

        for (int i = 0; i < result.length(); i++) {
            counted[result.charAt(i)-97] = counted[result.charAt(i)-97] + 1;
        }

        lista_wektorow.add(counted);                    /// brak normalizacji podczas obliczania ilosci wystapien
    }

    double[] normalizuj(double[] wektor){

        double ilosc = 0;
        for (int i = 0; i < wektor.length; i++) {
            ilosc = ilosc + wektor[i];
        }
        for (int i = 0; i < wektor.length; i++) {
            wektor[i] = wektor[i]/ilosc;
        }
        return wektor;
    }

    void wyswietl_liste(){
        lista_wektorow.forEach((wektor)->{

            for (int i = 0; i < wektor.length; i++) {
                System.out.print((char)(i+97)+" ");
            }
            System.out.println();
            for (int i = 0; i < wektor.length; i++) {
                System.out.print(wektor[i] + " ");
            }
            System.out.println();
            System.out.println();

        });
    }




    @Override
    public FileVisitResult preVisitDirectory(Object o, BasicFileAttributes basicFileAttributes) throws IOException {
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Object o, BasicFileAttributes basicFileAttributes) throws IOException {
        clear((Path)o);
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Object o, IOException e) throws IOException {
        return null;
    }

    @Override
    public FileVisitResult postVisitDirectory(Object o, IOException e) throws IOException {

        return FileVisitResult.TERMINATE;
    }
}





