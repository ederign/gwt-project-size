package me.ederign;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Main {

    public static void main( String args[] ) throws IOException {

        List<String> baseDirectories = Files.readAllLines( Paths.get( ( "src/repos.txt" ) ) );

        GWTLineCounter g = new GWTLineCounter();

        System.out.println( g.count( baseDirectories ));;
    }

}