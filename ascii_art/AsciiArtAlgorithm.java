package ascii_art;

import image.Image;
import image.SubImage;
import image_char_matching.SubImgCharMatcher;
import java.awt.*;

/**
 * The AsciiArtAlgorithm class is responsible for generating ASCII art representation from a given image.
 * It processes the image by dividing it into smaller sub-images,
 * calculates the brightness for each sub-image,
 * and then maps the brightness to corresponding characters to create an ASCII art representation.
 */
public class AsciiArtAlgorithm {

    private static Image lastImage;
    private static int lastResolution;
    private static SubImgCharMatcher lastSubImageCharMatcher;
    private static SubImage[][] lastSubImageArray;

    private final SubImgCharMatcher subImageCharMatcher;
    private Image image;
    private final int resolution;

    /**
     * Constructor for creating an AsciiArtAlgorithm object.
     *
     * @param subImageCharMatcher the character matcher responsible for converting
     *                           brightness values to ASCII characters.
     * @param image               the input image to be converted to ASCII art.
     * @param resolution          the resolution to divide the image into sub-images.
     */
    public AsciiArtAlgorithm(SubImgCharMatcher subImageCharMatcher, Image image, int resolution) {
        this.subImageCharMatcher = subImageCharMatcher;
        this.image = image;
        this.resolution = resolution;
    }

    /**
     * Generates an ASCII art representation of the image by dividing it into sub-images,
     * calculating the brightness of each sub-image, and mapping the brightness to corresponding characters.
     *
     * @return a 2D char array representing the ASCII art image.
     */
    public char[][] run() {
        // Check if the image, resolution, or character matcher has changed since the last run.
        if (!(lastSubImageCharMatcher == subImageCharMatcher
                && lastResolution == resolution && lastImage == image)) {
            lastSubImageCharMatcher = subImageCharMatcher;
            lastResolution = resolution;
            lastImage = image;

            // Create a padded image with dimensions that are a power of two.
            image = image.createPaddingImageToNextPowerOfTwo();

            // Divide the image into smaller sub-images based on the resolution.
            SubImage[][] divideImage = image.divideImage(resolution);

            // Create a deep copy of the sub-image array for further processing.
            lastSubImageArray = deepCopySubImageArray(divideImage);
        }

        // Initialize a 2D char array to store the resulting ASCII art.
        char[][] tableCharImage = new char[lastSubImageArray.length][lastSubImageArray[0].length];

        // Process each sub-image, calculate its brightness, and map it to a corresponding ASCII character.
        for (int i = 0; i < lastSubImageArray.length; i++) {
            for (int j = 0; j < lastSubImageArray[0].length; j++) {
                double subImageBrightness = lastSubImageArray[i][j].calculateBrightnessByImage();
                tableCharImage[i][j] = subImageCharMatcher.getCharByImageBrightness(subImageBrightness);
            }
        }

        // Return the 2D char array representing the ASCII art image.
        return tableCharImage;
    }

    /**
     * Creates a deep copy of a 2D array of SubImage objects.
     *
     * @param original the original 2D array of SubImage objects.
     * @return a deep copy of the SubImage array.
     */
    private SubImage[][] deepCopySubImageArray(SubImage[][] original) {
        // Create a new array of the same dimensions as the original.
        SubImage[][] copy = new SubImage[original.length][];

        // Iterate through each sub-image and copy its pixel data.
        for (int i = 0; i < original.length; i++) {
            copy[i] = new SubImage[original[i].length];
            for (int j = 0; j < original[i].length; j++) {
                SubImage originalSubImage = original[i][j];

                // Create a copy of the pixel data for the current sub-image.
                Color[][] pixelsCopy = new Color[originalSubImage.getHeight()][originalSubImage.getWidth()];
                for (int x = 0; x < originalSubImage.getHeight(); x++) {
                    for (int y = 0; y < originalSubImage.getWidth(); y++) {
                        pixelsCopy[x][y] = new Color(originalSubImage.getPixel(x, y).getRGB());
                    }
                }

                // Create a new SubImage with the copied pixel data.
                copy[i][j] = new SubImage(pixelsCopy, originalSubImage.getWidth(),
                        originalSubImage.getHeight());
            }
        }
        return copy;
    }
}
