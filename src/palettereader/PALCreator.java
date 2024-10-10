/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package palettereader;

/**
 *
 * @author colli
 */
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Utilizing output from PALReader or custom-written txt format, creates a valid .pal file
 * Valid input is `r    g   b` per line or `r   g   b   a` (if no alpha exists, 0 is used)
 */
public class PALCreator
{

    private final String inputFilename;

    PALCreator(String inputFilename) {
        this.inputFilename = inputFilename;
    }
    public void run()
    {
        //String inputFilename = "your_input_file.txt"; // Replace with your actual input filename
        
        Scanner scanner = null;
        FileOutputStream outputStream = null;
        try
        {
            File input = new File(inputFilename);
            scanner = new Scanner(input);
            String extension = "";
            int i = input.toString().lastIndexOf('.');
            if (i > 0) {
                extension = input.toString().substring(i+1);
            }
            else
            {
                System.out.println("Invalid input file? " + input.toString());
                System.exit(123);
            }
            String outputFilename = inputFilename.replaceAll("\\." + extension + "$", ".pal");
            outputStream = new FileOutputStream(outputFilename);
            writePAL(scanner, outputStream);
            System.out.println("PAL file created: " + outputFilename);
        }
        catch (IOException | StringIndexOutOfBoundsException ex)
        {
            System.out.println("ERROR " + ex.getMessage());
            System.exit(0);
        }

        try {
            outputStream.close();
            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writePAL(Scanner scanner, OutputStream outputStream) throws IOException
    {
        ArrayList<String> RGBValues = new ArrayList<>();
        String line;
        while (scanner.hasNextLine())
        {
            line = scanner.nextLine();
            String[] split = line.split("\t");
            RGBValues.add(split[0]);
            RGBValues.add(split[1]);
            RGBValues.add(split[2]);
            if(split.length > 3) //we have an alpha attached, use it
            {
                RGBValues.add(split[3]);
            }
            else
            {
                RGBValues.add("0");
            }
        }
        int numEntries = RGBValues.size() / 4;
        System.out.println("Num entries " + numEntries);
        int palDataSize =  RGBValues.size() + 4;
        System.out.println("palDataSize " + palDataSize);
        int RIFFSize = palDataSize + 12;
        System.out.println("RIFFSize " + RIFFSize);
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        outputStream.write("RIFF".getBytes());
        buffer.putInt(RIFFSize);
        outputStream.write(buffer.array(), 0, 4);
        buffer.position(0);
        outputStream.write("PAL data".getBytes());
        buffer.putInt(palDataSize);
        outputStream.write(buffer.array(), 0, 4);
        buffer.position(0);
        outputStream.write(0);
        outputStream.write(3);
        buffer.putInt(numEntries);
        outputStream.write(buffer.array(), 0, 2);
        for(String value : RGBValues)
        {
            outputStream.write(Integer.parseInt(value));
        }
    }

}

