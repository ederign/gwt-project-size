package me.ederign;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;

public class GWTLineCounter {

    private int numberOfLines;
    private boolean ignoreCommentAndSpaces;

    public int count( List<String> baseDirectories ) {
        numberOfLines = 0;
        for ( String baseDirectory : baseDirectories ) {
            process( baseDirectory );
        }
        return numberOfLines;
    }

    public int countIgnoringCommentAndSpaces( List<String> baseDirectories ) {
        ignoreCommentAndSpaces = true;
        return count( baseDirectories );
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
                    List<String> lines = Files.readAllLines( file.toPath() );
                    if ( ignoreCommentAndSpaces ) {
                        List<String> cleanedLines = filterLines( lines );
                        numberOfLines += cleanedLines.size();
                    } else {
                        numberOfLines += lines.size();
                    }

                } catch ( IOException e ) {
                    e.printStackTrace();
                }
            }
        };
    }

    private List<String> filterLines( List<String> lines ) {
        boolean inABlockComment = false;

        List<String> cleanedLines = new ArrayList<>();
        for ( String line : lines ) {
            if ( !inABlockComment ) {
                if ( !isABlockComment( line ) ) {
                    String parsedLine = parse(line);
                    if ( !isAEmptyLine( parsedLine ) ) {
                        if ( !isALineComment( parsedLine ) ) {
                            cleanedLines.add( parsedLine );
                        }
                    }
                } else {
                    inABlockComment = true;
                }
            } else {
                if ( endOfBlockComment( line ) ) {
                    inABlockComment = false;
                }
            }

        }
        return cleanedLines;
    }

    private boolean isAEmptyLine( String parsedLine ) {
        return parsedLine.isEmpty();
    }

    private boolean endOfBlockComment( String line ) {
        return line.contains( "*/" );
    }

    private boolean isABlockComment( String line ) {
        return line.contains( "/*" );
    }

    private boolean isALineComment( String line ) {
        return line.startsWith( "//" );
    }

    private String parse( String line ) {
        return line.replaceAll( "\\s", "" );
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
