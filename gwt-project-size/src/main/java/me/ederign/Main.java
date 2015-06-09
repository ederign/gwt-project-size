package me.ederign;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Main {

    public static void main( String args[] ) throws IOException {

        List<String> baseDirectories = Files.readAllLines( Paths.get( ( "src/repos.txt" ) ) );

        GWTLineCounter g = new GWTLineCounter();

        int numberOfLines = g.count( baseDirectories );
        int numberOfLinesCleaned = g.countIgnoringCommentAndSpaces( baseDirectories );
        System.out.println( "Total: " + numberOfLines );
        System.out.println( "Total without comment and spaces: " +numberOfLinesCleaned );
    }

}