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
import java.util.ArrayList;
import java.util.List;

/**
 * Reads a text file containing RGB[A] values, adjusts the brightness, and writes the result to a new text file.
 */
public class ColorBrightnessAdjuster {

    private String inputFilename;
    private String outputFilename;
    private double brightnessFactor;

    public ColorBrightnessAdjuster(String inputFilename, String outputFilename, double brightnessFactor) {
        this.inputFilename = inputFilename;
        this.outputFilename = outputFilename;
        this.brightnessFactor = brightnessFactor;
    }

    public void run() {
        try {
            // Read colors from input text file
            List<int[]> colors = readColorsFromTextFile(inputFilename);

            // Adjust brightness
            List<int[]> adjustedColors = adjustBrightness(colors, brightnessFactor);

            // Write adjusted colors to output text file
            writeColorsToTextFile(adjustedColors, outputFilename);

            System.out.println("Brightness adjusted by a factor of " + brightnessFactor + " and saved to " + outputFilename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads color values from a text file.
     * @param filename The input text file.
     * @return A list of color entries, each represented as an array of [red, green, blue, alpha].
     * @throws IOException If an I/O error occurs.
     */
    public List<int[]> readColorsFromTextFile(String filename) throws IOException {
        List<int[]> colors = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line;
        int lineNumber = 1;
        while ((line = reader.readLine()) != null) {
            if (line.trim().isEmpty()) {
                lineNumber++;
                continue; // Skip empty lines
            }
            String[] tokens = line.split("\\s+"); // Split by whitespace
            if (tokens.length < 3) {
                System.out.println("Invalid line at " + lineNumber + ": " + line);
                lineNumber++;
                continue;
            }
            try {
                int red = Integer.parseInt(tokens[0]);
                int green = Integer.parseInt(tokens[1]);
                int blue = Integer.parseInt(tokens[2]);
                int alpha = (tokens.length > 3) ? Integer.parseInt(tokens[3]) : 0;
                int[] color = new int[]{red, green, blue, alpha};
                colors.add(color);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number format at line " + lineNumber + ": " + line);
            }
            lineNumber++;
        }
        reader.close();
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
            int green = (int) Math.min(255, color[1] * brightnessFactor);
            int blue = (int) Math.min(255, color[2] * brightnessFactor);
            int alpha = color[3]; // Keep alpha the same
            int[] adjustedColor = new int[]{red, green, blue, alpha};
            adjustedColors.add(adjustedColor);
        }
        return adjustedColors;
    }

    /**
     * Writes the list of colors to a text file.
     * @param colors The list of colors to write.
     * @param filename The output text file.
     * @throws IOException If an I/O error occurs.
     */
    public void writeColorsToTextFile(List<int[]> colors, String filename) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
        for (int[] color : colors) {
            writer.write(color[0] + "\t" + color[1] + "\t" + color[2] + "\t" + color[3]);
            writer.newLine();
        }
        writer.close();
    }

    // Main method for testing purposes
    public static void main(String[] args) {
        // Example usage:
        String inputFile = "input_colors.txt"; // Replace with your input file
        String outputFile = "brightened_colors.txt"; // Replace with your desired output file
        double brightnessFactor = 1.2; // Increase brightness by 20%

        ColorBrightnessAdjuster adjuster = new ColorBrightnessAdjuster(inputFile, outputFile, brightnessFactor);
        adjuster.run();
    }
}

