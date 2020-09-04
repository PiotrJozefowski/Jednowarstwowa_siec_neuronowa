package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Flow;

public class Main {

    public static HashMap<String,FileProcessor> mapa_processorow = new HashMap<>();

    public static void main(String[] args) {

        JFrame okno = new JFrame();
        okno.setSize(600,800);
        okno.setLocationRelativeTo(null);
        okno.setDefaultCloseOperation(3);
        okno.setLayout(new BorderLayout());

        JPanel west = new JPanel();
        west.setLayout(new FlowLayout());

        JPanel lewy = new JPanel();
        GridLayout gl = new GridLayout(10,1,10,10);
        lewy.setLayout(gl);

        JPanel prawy = new JPanel();
        BoxLayout ley_prawy = new BoxLayout(prawy,BoxLayout.Y_AXIS);
        prawy.setLayout(ley_prawy);
        prawy.setAlignmentX(Component.RIGHT_ALIGNMENT);


        TextArea text = new TextArea();
        text.setPreferredSize(new Dimension(400,600));
        prawy.add(text);

        JButton zatwierdz = new JButton("OK");
        JLabel wykryty_jezyk = new JLabel();



        TextArea txt_field_ilosc_nauki = new TextArea();
        txt_field_ilosc_nauki.setPreferredSize(new Dimension(100,25));

        Checkbox check_normalizacja = new Checkbox("normalizacja");
        Checkbox check_ciagla = new Checkbox("wyliczenie f ciaglej");
        JLabel ilosc_nauki = new JLabel("Ilosc powtorzeń uczenia");

        Checkbox c1 = new Checkbox("1");
        Checkbox c2 = new Checkbox("5");
        Checkbox c3 = new Checkbox("10");
        Checkbox c4 = new Checkbox("15");


        lewy.add(check_normalizacja);
        lewy.add(check_ciagla);
        lewy.add(ilosc_nauki);
        lewy.add(c1);
        lewy.add(c2);
        lewy.add(c3);
        lewy.add(c4);

        lewy.add(zatwierdz);
        lewy.add(wykryty_jezyk);

        west.add(lewy);

        okno.add(west,BorderLayout.WEST);
        okno.add(prawy,BorderLayout.EAST);

        okno.setVisible(true);

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// GUI

        DirectoryProcessor dp = new DirectoryProcessor(Path.of("TRAIN"));
        try {
            Files.walkFileTree(dp.path_to_files,dp);
        } catch (IOException e) {
            e.printStackTrace();
        }

        mapa_processorow.forEach((jezyk,procesor)->{
            try {
                Files.walkFileTree(procesor.path,procesor);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        ////////////////////////////////////////////////////////////////////

        HashMap<String, Perceptron> mapa_perceptronow = new HashMap<>();
        mapa_processorow.forEach((jezyk,processor)->{
            mapa_perceptronow.put(jezyk,new Perceptron(26,0.25));
        });

        ////////////////////////////////////////////////////////////////// nauka
        for (int i = 0; i < 10; i++) {

            mapa_processorow.forEach((jezyk, processor) -> {

                processor.lista_wektorow.forEach((wektor) -> {
                    mapa_perceptronow.forEach((jezyk_perceptronu, perceptron) -> {

                        if (perceptron.klasyfikuj(wektor) == 0 && jezyk_perceptronu == jezyk) {
                            //System.out.println("UCZE na 1: " + jezyk_perceptronu + " " + jezyk );
                            perceptron.ucz_w(wektor, 1);
                        }
                        if (perceptron.klasyfikuj(wektor) == 1 && jezyk_perceptronu != jezyk) {
                            //System.out.println("UCZE na 0: " + jezyk_perceptronu + " " + jezyk );
                            perceptron.ucz_w(wektor, 0);
                        }
                    });
                });
            });



        }
        //////////////////////////////////////////////////////////////////

        zatwierdz.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {


                ArrayList<String> odpowiedz = new ArrayList<>();

                double[] wektor = przetworz_tekst(text.getText());

                if(check_normalizacja.getState()){
                     wektor = normalizuj(wektor);
                                mapa_perceptronow.forEach((jezyk, perceptron) -> {
                                    perceptron.normalizuj();
                                });
                }

                double[] f_wektor = wektor;


                if(check_ciagla.getState()) {
                     mapa_perceptronow.forEach((jezyk, perceptron)->{
                        System.out.println(jezyk + " : " + perceptron.klasyfikuj_fa(f_wektor));
                        //odpowiedz.add(jezyk + " : " + perceptron.klasyfikuj_fa(f_wektor));
                     });
                }


                mapa_perceptronow.forEach((jezyk, perceptron)->{
                    int wynik = perceptron.klasyfikuj(f_wektor);
                    System.out.println(jezyk + " : " + wynik);
                    if(wynik == 1){
                        odpowiedz.add(jezyk);
                    }
                });

                String response = "<HTML>";
                for (int i = 0; i < odpowiedz.size(); i++) {
                    response = response + odpowiedz.get(i) + "<BR>";
                }
                response = response + "</HTML>";
                wykryty_jezyk.setText(response);

            }
        });




    }

    static double[] calcutale(String result){

        double[] counted = new double[26];
        counted = new double[26];

        for (int i = 0; i < result.length(); i++) {
            counted[result.charAt(i)-97] = counted[result.charAt(i)-97] + 1;
        }

        return counted;
    }


    static double[] przetworz_tekst(String tekst){

        String result = "";
        for (int i = 0; i < tekst.length(); i++) {

            if((tekst.charAt(i) > 96 && tekst.charAt(i) < 123) || (tekst.charAt(i) > 64 && tekst.charAt(i) < 91)){
                if(tekst.charAt(i)>64 && tekst.charAt(i) < 91){
                    result = result +(char)(tekst.charAt(i) + 32);
                }else {
                    result = result + (char) tekst.charAt(i);
                }
            }

        }
        return calcutale(result);
    }

    static double[] normalizuj(double[] wektor){

        double dlugosc = 0;
        for (int i = 0; i < wektor.length; i++) {
            dlugosc = dlugosc + Math.pow(wektor[i],2);
        }
        dlugosc = Math.sqrt(dlugosc);

//        double ilosc = 0;
//        for (int i = 0; i < wektor.length; i++) {
//            ilosc = ilosc + wektor[i];
//        }

        for (int i = 0; i < wektor.length; i++) {
            wektor[i] = wektor[i]/dlugosc;
        }

        return wektor;
    }
}
