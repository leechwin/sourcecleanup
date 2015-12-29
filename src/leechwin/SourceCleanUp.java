package leechwin;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FilenameUtils;

/**
 * Source cleanup<br>
 * <h4>Rules</h4>
 * <ul>
 *     <li>Tab to space></li>
 *     <li>Remove tailing space</li>
 *     <li>Remove tailing tab</li>
 *     <li>Remove "\r" of "\r\n" in windows env.</li>
 * </ul>
 *  @author leechwin1@gmail.com
 */
public class SourceCleanUp {

    public static final String DEFAULT_FILE_EXTENSION = "java";
    public static final String TEMP_FILE_NAME = "temp.text";

    public static Options options = null;
    public static String SOURCE_FILE_EXTENSION = DEFAULT_FILE_EXTENSION;

    public static void main( String[] args ) {
        CommandLineParser parser = new DefaultParser();
        options = new Options();
        options.addOption("h", "help", false, Message.HELP_DESCRIPTION);
        options.addOption("p", "path", true, Message.PATH_DESCRIPTION);
        options.addOption("e", "extension", true, Message.EXTENSION_DESCRIPTION);

        String startPath = null;
        try {
            CommandLine cmd = parser.parse(options, args);
            if (cmd.hasOption("help")) {
                printUsage();
                return;
            }
            if (cmd.hasOption("path")) {
                startPath = cmd.getOptionValue("p");
            } else {
                printUsage();
                return;
            }
            if (cmd.hasOption("extension")) {
                SOURCE_FILE_EXTENSION = cmd.getOptionValue("e");
            }

            ArrayList<File> files = new ArrayList<File>();
            try {
                getFileLists(startPath, files);
            } catch (Exception e) {
                System.out.println("Error: Invalid root folder!!");
                return;
            }

            for ( File file : files ) {
                try {
                    // all methods throw IOException this is important
                    // because if one part fails we don't want to move on
                    writeFile( readFile(file) );
                    // delete orign file
                    if ( file.exists() ) {
                        file.delete();
                    }
                    // tmp file move to origin file
                    File tempFile = new File(TEMP_FILE_NAME);
                    if (!tempFile.renameTo(file)) {
                        System.out.println("rename failed");
                    } else {
                        System.out.println("Success: " + file.getAbsolutePath() );
                    }
                } catch (IOException e) {
                    System.out.println("Failed IOException");
                }
            }
        } catch (ParseException e1) {
            System.out.println(e1.getMessage());
        }
    }

    public static String readFile(File file) {
        StringBuffer contents = new StringBuffer();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String text = null;
            // repeat until all lines are read
            while ((text = reader.readLine()) != null) {
                // replace tabs with 4 whitespaces
                String text_wo_tabs = text.replace(Message.TAB, Message.SPACE);
                // send it off to have trailing whitespace trimmed
                text_wo_tabs = trimEnd(text_wo_tabs);
                contents.append(text_wo_tabs);
                contents.append('\n');
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (reader != null) { reader.close(); }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
        return contents.toString();
    }

    public static String trimEnd(String str) {
        int len = str.length();
        char[] val = str.toCharArray();
        while ((0 < len) && (val[len - 1] <= ' ')) {
            len--;
        }
        return (len < str.length()) ? str.substring(0, len) : str;
    }

    public static boolean writeFile(String contents) throws IOException {
        File file = new File(TEMP_FILE_NAME);
        boolean exist = file.createNewFile();
        if (!exist) {
            System.out.println("File already exists. Removing it");
            file.delete();
        }
        FileWriter fstream = new FileWriter(file);
        BufferedWriter out = new BufferedWriter(fstream);
        out.write(contents);
        out.close();
        return true;
    }

    public static void getFileLists(String directoryName, ArrayList<File> files) {
        File directory = new File(directoryName);
        // get all the files from a directory
        File[] fList = directory.listFiles();
        for (File file : fList) {
            if (file.isFile()) {
                if (SOURCE_FILE_EXTENSION.equalsIgnoreCase(FilenameUtils.getExtension(file.getName()))) {
                    files.add(file);
                }
            } else if (file.isDirectory()) {
                getFileLists(file.getAbsolutePath(), files);
            }
        }
    }

    /**
     * Print usage
     */
    public static void printUsage() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(Message.USAGE_DESCRIPTION, options);
    }

}
