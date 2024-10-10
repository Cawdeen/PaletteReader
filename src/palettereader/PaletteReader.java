/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package palettereader;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author colli
 */
public class PaletteReader {

    private final String filename;

    PaletteReader(String filename) {
        this.filename = filename;
    }

    /**
     * @param args the command line arguments
     */
    public void run()
    {
        System.out.println("Started");
        //String filename = "new_palette.pal"; // Replace with your actual filename
        
        FileInputStream inputStream = null;
        FileOutputStream outputStream = null;
        
        try
        {
            inputStream = new FileInputStream(filename);
            String outputFilename = removeExtension(filename) + ".txt";
            outputStream = new FileOutputStream(outputFilename);
            Writer writer = new BufferedWriter(new OutputStreamWriter(outputStream));
            writeTXT(inputStream, writer);
        }
        catch (StringIndexOutOfBoundsException | IOException ex)
        {
            System.out.println("ERROR " + ex.getMessage());
            System.exit(0);
        }
        try
        {
            outputStream.close();
            inputStream.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Writes the FileInputStream (.pal file) to a Writer (.pal.txt file)
     * @param inputStream A palette file
     * @param writer Normally a text file
     * @throws IOException err0r
     */
    public static void writeTXT(FileInputStream inputStream, Writer writer) throws IOException
    {
        System.out.println("Entered WriteTXT");
        while (inputStream.available() > 0)
        {
            //RIFF
            //RIFF LENGTH
            //PAL
            //DATA
            //PAL DATA LENGTH
            //a zero
            //a 3 (version?)
            //2 byte short: number of entries
            //int r g b a
            String formquestionmark = readString(inputStream, 4); //RIFF
            if (!formquestionmark.equalsIgnoreCase("RIFF"))
            {
                System.exit(123);
            }
            long form_length = readRUint(inputStream);
            System.out.println("RIFF Length: " + form_length);
            formquestionmark = readString(inputStream, 4); //PAL
            if (!formquestionmark.equalsIgnoreCase("PAL "))
            {
                System.exit(123);
            }
            formquestionmark = readString(inputStream, 4); //data
            if (!formquestionmark.equalsIgnoreCase("data"))
            {
                System.exit(123);
            }
            form_length = readRUint(inputStream);
            System.out.println("PAL data Length: " + form_length);
            int aZero = inputStream.read();
            System.out.println("a zero is " + aZero);
            int aThree = inputStream.read();
            System.out.println("a three is " + aThree);
            int numEntries = readShort(inputStream);
            System.out.println("Number of Entries " + numEntries);
            for(int i = 0; i < numEntries; i++)
            {
                int red = inputStream.read();
                int blue = inputStream.read();
                int green = inputStream.read();
                int alpha = inputStream.read();
                System.out.println("Entry " + i + " is: red;" + red + ";blue;" + blue + ";green;" + green + ";alpha;" + alpha);
                writer.write("" + red + "\t" + blue + "\t" + green + "\t" + alpha);
                writer.write("\r\n");
            }
            writer.close();
        }
    }
    
    /**
     * Removes the extension from a filename.
     * @param filename The filename from which to remove the extension.
     * @return The filename without its extension.
     */
    private String removeExtension(String filename) {
        int index = filename.lastIndexOf('.');
        if (index > 0) {
            return filename.substring(0, index);
        } else {
            return filename;
        }
    }
	
	/**
     * Reads a reverse unsigned integer (4 bytes)
     * @param toSkip FileInputStream to perform reads on
     */
    static long readRUint(InputStream toSkip) {
        try {
            return ((long) (toSkip.read()) & 0xff | (long) (toSkip.read() << 8) & 0xff00
                    | (long) (toSkip.read() << 16) & 0xff0000 | (long) (toSkip.read() << 24) & 0xff000000);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return -1;
    }
	/**
     * Reads a string from IFF files
     * @param toSkip FileInputStream to perform reads on
     * @param length Length of String to read
     */
    static String readString(InputStream toSkip, int length) {
        String baseObject = "";
        try {
            byte[] baseObjectAr = new byte[length]; // create a byte array of
            // length
            toSkip.read(baseObjectAr, 0, length);
            baseObject = new String(baseObjectAr, StandardCharsets.UTF_8); // turn the byte
            // array into a
            // string
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } // read the bytes

        return baseObject;
    }
	/**
     * Reads a short (2 bytes)
     * @param toSkip FileInputStream to perform reads on
     */
    static int readShort(InputStream toSkip) {
        try {
            return ((toSkip.read()) | (toSkip.read() << 8));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return -1;
    }
    
}
