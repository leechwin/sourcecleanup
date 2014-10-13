package leechwin;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.io.FilenameUtils;

/**
 * Source Clean Up Rules
 * - Tab to space
 * - Remove tailing space
 * - Remove tailing tab
 * - Remove "\r" of "\r\n" in windows env.
 *
 * TODO:: ignore folder?
 *        input for root dir
 *
 *  @author leechwin1@gmail.com
 */
public class SourceCleanUp {

    // you can choice the target file
    public static final String SOURCE_FILE_EXTENSION = "java";

    public static final String TAB = "	";
    public static final String SPACE = "    ";

    public static final String TEMP_FILE_NAME = "temp.text";

    public static final String USAGE = "Usage: java -jar SourceCleanUp.jar <absolutely root folder path>\n"
                                     + "Example: java -jar SourceCleanUp.jar /home/user/targetRootFolder\n";

    public static void main( String[] args ) {
        if ( null == args || 0 == args.length ) {
            System.out.println(USAGE);
            return;
        }
        // input for root folder
        // test
        //String rootFolder = System.getProperty("user.dir") + "/src/leechwin/test";
        String rootFolder = args[0];

        ArrayList<File> files = new ArrayList<File>();
        try {
            getFileLists( rootFolder, files );
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
    }

    public static String readFile( File file ) {
        StringBuffer contents = new StringBuffer();
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(file));
            String text = null;

            // repeat until all lines are read
            while ((text = reader.readLine()) != null) {
                // replace tabs with 4 whitespaces
                String text_wo_tabs = text.replace(TAB, SPACE);
                // send it off to have trailing whitespace trimmed
                text_wo_tabs = trimEnd(text_wo_tabs);
                contents.append(text_wo_tabs);
                contents.append(System.getProperty("line.separator"));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) { reader.close(); }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return contents.toString();
    }

    public static String trimEnd( String str ) {
        int len = str.length();
        char[] val = str.toCharArray();
        while ((0 < len) && (val[len - 1] <= ' ')) {
            len--;
        }

        return (len < str.length()) ? str.substring(0, len) : str;
    }

    public static boolean writeFile( String contents ) throws IOException {
        File file = new File(TEMP_FILE_NAME);
        boolean exist = file.createNewFile();
        if ( !exist ) {
            System.out.println("File already exists. Removing it");
            file.delete();
        }
        FileWriter fstream = new FileWriter(file);
        BufferedWriter out = new BufferedWriter(fstream);
        out.write( contents );
        out.close();
        // System.out.println("File created successfully.");
        return true;
    }

    public static void getFileLists( String directoryName, ArrayList<File> files ) {
        File directory = new File(directoryName);

        // get all the files from a directory
        File[] fList = directory.listFiles();
        for ( File file : fList ) {
            if ( file.isFile() ) {
                if ( SOURCE_FILE_EXTENSION.equalsIgnoreCase( FilenameUtils.getExtension(file.getName()) ) ) {
                    files.add(file);
                    // System.out.println(file.getAbsolutePath());
                }
            } else if ( file.isDirectory() ) {
                getFileLists( file.getAbsolutePath(), files );
            }
        }
    }

}
