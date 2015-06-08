package me.ederign;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;

public class GWTLineCounter {

    private int numberOfLines;

    public int count( List<String> baseDirectories ) {
        for ( String baseDirectory : baseDirectories ) {
            process( baseDirectory );
        }
        return numberOfLines;
    }

    private void process( String baseDirectory ) {
        File baseDir = new File( baseDirectory );
        System.out.println( "----------------------------------------" );
        System.out.println( "Base Directory " + baseDirectory );
        List<File> gwtProject = searchForGwtProjects( baseDir );

        for ( File file : gwtProject ) {
            File targetDir = prepareTargetDir( file );
            if ( targetDir.isDirectory() ) {
                System.out.println( "Processing " + targetDir );
                Collection<File> gwtClientFiles = FileUtils.listFiles( targetDir, new String[]{ "java" }, true );
                gwtClientFiles.forEach( lineCounter() );
            }
        }

    }

    private Consumer<File> lineCounter() {
        return new Consumer<File>() {
            @Override
            public void accept( File file ) {
                try {
//                    System.out.println( "Reading File: " + file );
                    List<String> lines = Files.readAllLines( file.toPath() );
//                    System.out.println( "Number of lines:" + lines.size() );
                    numberOfLines += lines.size();
                } catch ( IOException e ) {
                    e.printStackTrace();
                }
            }
        };
    }

    private File prepareTargetDir( File baseDir ) {
        String baseDirString = baseDir.getParent();
        if ( baseDirString.contains( "resources" ) ) {
            baseDirString = baseDirString.replace( "resources", "java" );
        }
        return new File( baseDirString );
    }

    private List<File> searchForGwtProjects( File targetDir ) {
        Collection<File> gwtProjects = FileUtils.listFiles( targetDir,
                                                            new String[]{ "gwt.xml" }, true );
        return gwtProjects.stream().filter( cleanupGWTProjectsDir() ).collect( Collectors.toList() );
    }

    private static Predicate<File> cleanupGWTProjectsDir() {
        return f -> ( !f.getAbsolutePath().contains( "classes" ) &&
                !f.getAbsolutePath().contains( "target" ) &&
                !f.getAbsolutePath().contains( "Fast" ) &&
                !f.getAbsolutePath().contains( "WEB-INF" ) );
    }
}
