package palettereader;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * PALBrightnessAdjuster reads a .pal file, adjusts the brightness of each color,
 * and writes the result to a new .pal file.
 */
public class PALBrightnessAdjuster {

    private String inputFilename;
    private String outputFilename;
    private double brightnessFactor;

    public PALBrightnessAdjuster(String inputFilename, String outputFilename, double brightnessFactor) {
        this.inputFilename = inputFilename;
        this.outputFilename = outputFilename;
        this.brightnessFactor = brightnessFactor;
    }

    public void run() {
        try {
            // Read colors from input file
            List<int[]> colors = readPAL(inputFilename);

            // Adjust brightness
            List<int[]> adjustedColors = adjustBrightness(colors, brightnessFactor);

            // Write colors to output file
            writePAL(adjustedColors, outputFilename);

            System.out.println("Brightness adjusted by a factor of " + brightnessFactor + " and saved to " + outputFilename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads a .pal file and extracts the color entries.
     * @param fileName The input .pal file.
     * @return A list of color entries, each represented as an array of [red, blue, green, alpha].
     * @throws IOException If an I/O error occurs.
     */
    public List<int[]> readPAL(String fileName) throws IOException {
        List<int[]> colors = new ArrayList<>();
        FileInputStream inputStream = new FileInputStream(fileName);
        try {
            // Read the header
            String formquestionmark = readString(inputStream, 4); // RIFF
            if (!formquestionmark.equalsIgnoreCase("RIFF")) {
                System.err.println("Not a valid RIFF file");
                System.exit(123);
            }
            long form_length = readRUint(inputStream);
            System.out.println("RIFF Length: " + form_length);
            formquestionmark = readString(inputStream, 4); // PAL
            if (!formquestionmark.equalsIgnoreCase("PAL ")) {
                System.err.println("Not a valid PAL file");
                System.exit(123);
            }
            formquestionmark = readString(inputStream, 4); // data
            if (!formquestionmark.equalsIgnoreCase("data")) {
                System.err.println("No data chunk");
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
            for (int i = 0; i < numEntries; i++) {
                int red = inputStream.read();
                int blue = inputStream.read();
                int green = inputStream.read();
                int alpha = inputStream.read();
                System.out.println("Entry " + i + " is: red;" + red + ";blue;" + blue + ";green;" + green + ";alpha;" + alpha);
                int[] color = new int[]{red, blue, green, alpha};
                colors.add(color);
            }
        } finally {
            inputStream.close();
        }
        return colors;
    }

    /**
     * Adjusts the brightness of each color in the list.
     * @param colors The list of colors to adjust.
     * @param brightnessFactor The factor by which to adjust the brightness.
     * @return A new list of adjusted colors.
     */
    public List<int[]> adjustBrightness(List<int[]> colors, double brightnessFactor) {
        List<int[]> adjustedColors = new ArrayList<>();
        for (int[] color : colors) {
            int red = (int) Math.min(255, color[0] * brightnessFactor);
            int blue = (int) Math.min(255, color[1] * brightnessFactor);
            int green = (int) Math.min(255, color[2] * brightnessFactor);
            int alpha = color[3]; // Keep alpha the same
            int[] adjustedColor = new int[]{red, blue, green, alpha};
            adjustedColors.add(adjustedColor);
        }
        return adjustedColors;
    }

    /**
     * Writes the list of colors to a .pal file.
     * @param colors The list of colors to write.
     * @param fileName The output .pal file.
     * @throws IOException If an I/O error occurs.
     */
    public void writePAL(List<int[]> colors, String fileName) throws IOException {
        FileOutputStream outputStream = new FileOutputStream(fileName);
        try {
            int numEntries = colors.size();
            System.out.println("Num entries " + numEntries);
            int palDataSize = numEntries * 4 + 4; // Each color is 4 bytes, plus 4 bytes for the header data
            System.out.println("palDataSize " + palDataSize);
            int RIFFSize = palDataSize + 12; // palDataSize + 12 bytes for 'PAL ' and 'data' headers
            System.out.println("RIFFSize " + RIFFSize);
            ByteBuffer buffer = ByteBuffer.allocate(4);
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            outputStream.write("RIFF".getBytes(StandardCharsets.US_ASCII));
            buffer.putInt(RIFFSize);
            outputStream.write(buffer.array(), 0, 4);
            buffer.clear();
            outputStream.write("PAL ".getBytes(StandardCharsets.US_ASCII));
            outputStream.write("data".getBytes(StandardCharsets.US_ASCII));
            buffer.putInt(palDataSize);
            outputStream.write(buffer.array(), 0, 4);
            buffer.clear();
            outputStream.write(0); // aZero
            outputStream.write(3); // aThree
            buffer = ByteBuffer.allocate(2);
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            buffer.putShort((short) numEntries);
            outputStream.write(buffer.array(), 0, 2);
            for (int[] color : colors) {
                outputStream.write(color[0]); // red
                outputStream.write(color[1]); // blue
                outputStream.write(color[2]); // green
                outputStream.write(color[3]); // alpha
            }
        } finally {
            outputStream.close();
        }
    }

    /**
     * Reads a string from the input stream.
     * @param inputStream The input stream to read from.
     * @param length The number of bytes to read.
     * @return The string read from the input stream.
     * @throws IOException If an I/O error occurs.
     */
    public String readString(InputStream inputStream, int length) throws IOException {
        byte[] bytes = new byte[length];
        int bytesRead = inputStream.read(bytes, 0, length);
        if (bytesRead != length) {
            throw new IOException("Failed to read " + length + " bytes for String");
        }
        return new String(bytes, StandardCharsets.US_ASCII);
    }

    /**
     * Reads a little-endian unsigned integer from the input stream.
     * @param inputStream The input stream to read from.
     * @return The unsigned integer read from the input stream.
     * @throws IOException If an I/O error occurs.
     */
    public long readRUint(InputStream inputStream) throws IOException {
        long b1 = inputStream.read() & 0xff;
        long b2 = inputStream.read() & 0xff;
        long b3 = inputStream.read() & 0xff;
        long b4 = inputStream.read() & 0xff;
        return b1 | (b2 << 8) | (b3 << 16) | (b4 << 24);
    }

    /**
     * Reads a little-endian short integer from the input stream.
     * @param inputStream The input stream to read from.
     * @return The short integer read from the input stream.
     * @throws IOException If an I/O error occurs.
     */
    public int readShort(InputStream inputStream) throws IOException {
        int b1 = inputStream.read() & 0xff;
        int b2 = inputStream.read() & 0xff;
        return b1 | (b2 << 8);
    }
}

