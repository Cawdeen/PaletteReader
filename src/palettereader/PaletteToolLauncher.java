/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package palettereader;


import java.util.Scanner;
/**
 *
 * @author colli
 */
public class PaletteToolLauncher {
    
    public static void main(String[] args){
        Scanner scanner = new Scanner(System.in);

        System.out.println("Palette Tools");
        System.out.println("1. Palette Reader");
        System.out.println("2. Palette Creator");
        System.out.println("3. Palette Brightness Adjuster");
        System.out.print("Please select an option (1-3): ");

        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        switch (choice) {
            case 1:
                System.out.print("Enter the path to the .pal file to read: ");
                String readerFilename = scanner.nextLine();
                PaletteReader reader = new PaletteReader(readerFilename);
                reader.run();
                break;
            case 2:
                System.out.print("Enter the path to the input text file: ");
                String creatorInputFilename = scanner.nextLine();
                PALCreator creator = new PALCreator(creatorInputFilename);
                creator.run();
                break;
            case 3:
                System.out.print("Enter the path to the input .txt file: ");
                String brightnessInputFilename = scanner.nextLine();
                System.out.print("Enter the path for the output .txt file: ");
                String brightnessOutputFilename = scanner.nextLine();
                System.out.print("Enter the brightness factor (e.g., 1.2 for 20% increase): ");
                double brightnessFactor = scanner.nextDouble();
                ColorBrightnessAdjuster adjuster = new ColorBrightnessAdjuster(brightnessInputFilename, brightnessOutputFilename, brightnessFactor);
                adjuster.run();
                break;
            default:
                System.out.println("Invalid option selected.");
                break;
        }

        scanner.close();
        System.out.println("Program completed.");
    }
    
}
