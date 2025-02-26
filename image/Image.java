package image;

import image_char_matching.SubImgCharMatcher;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * The Image class represents an image in memory using a 2D array of pixels.
 * It allows for loading an image from a file, dividing it into smaller sub-images,
 * saving the image to a file, and performing other image manipulations such as padding.
 * <p>
 * The image is represented by a 2D array of {@link Color} objects, which hold the color
 * values of each pixel. This class supports functionality for dividing the image into
 * sub-images based on a specified resolution and padding the image to the next power of two.
 * </p>
 */
public class Image {

    private final Color[][] pixelArray;
    private final int width;
    private final int height;
    private static final int OFFSET = 1;
    private static final int MAX_RGB = 255;
    private static final int PADDING_FROM_BORDER = 2;
    private static final double RED_VALUE = 0.2126;
    private static final double BLUE_VALUE = 0.0722;
    private static final double GREEN_VALUE = 0.7152;

    /**
     * Constructor for creating an image from a file.
     *
     * @param filename the path to the image file to load.
     * @throws IOException if an error occurs while reading the file.
     */
    public Image(String filename) throws IOException {
        BufferedImage im = ImageIO.read(new File(filename));
        width = im.getWidth();
        height = im.getHeight();

        pixelArray = new Color[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                pixelArray[i][j] = new Color(im.getRGB(j, i));
            }
        }
    }

    /**
     * Constructor for creating an image from an existing 2D array of pixels.
     *
     * @param pixelArray the 2D array of {@link Color} representing the pixels of the image.
     * @param width      the width of the image.
     * @param height     the height of the image.
     */
    public Image(Color[][] pixelArray, int width, int height) {
        this.pixelArray = pixelArray;
        this.width = width;
        this.height = height;
    }

    /**
     * Gets the width of the image.
     *
     * @return the width of the image.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Gets the height of the image.
     *
     * @return the height of the image.
     */
    public int getHeight() {
        return height;
    }

    /**
     * Gets the color of a pixel at a specific coordinate.
     *
     * @param x the x-coordinate of the pixel.
     * @param y the y-coordinate of the pixel.
     * @return the {@link Color} of the pixel at the specified coordinates.
     */
    public Color getPixel(int x, int y) {
        return pixelArray[x][y];
    }

    /**
     * Saves the current image to a file in JPEG format.
     *
     * @param fileName the name of the file to save the image as.
     */
    public void saveImage(String fileName) {
        BufferedImage bufferedImage = new BufferedImage(pixelArray[0].length, pixelArray.length,
                BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < pixelArray.length; x++) {
            for (int y = 0; y < pixelArray[x].length; y++) {
                bufferedImage.setRGB(y, x, pixelArray[x][y].getRGB());
            }
        }
        File outputfile = new File(fileName + ".jpeg");
        try {
            ImageIO.write(bufferedImage, "jpeg", outputfile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Divides the image into smaller sub-images of the specified resolution.
     *
     * <p>
     * This method splits the image into smaller blocks (sub-images) based on the resolution parameter, which
     * represents the number of divisions along each dimension (width and height). Each sub-image will be of
     * approximately equal size.
     * </p>
     *
     * @param resolution the number of divisions along the width and height.
     * @return a 2D array of {@link SubImage} objects representing the divided sub-images.
     */
    public SubImage[][] divideImage(int resolution) {
        int newWidth = width / resolution;
        int newHeight = height / resolution;
        SubImage[][] subImages = new SubImage[resolution][resolution];

        // Iterate over each block and extract the sub-image
        for (int i = 0; i < resolution; i++) {
            for (int j = 0; j < resolution; j++) {
                int xStart = i * newWidth;
                int yStart = j * newHeight;
                int xEnd = (i + OFFSET) * newWidth;
                int yEnd = (j + OFFSET) * newHeight;

                Color[][] subImagePixels = new Color[newHeight][newWidth];
                for (int x = xStart; x < xEnd; x++) {
                    for (int y = yStart; y < yEnd; y++) {
                        subImagePixels[y - yStart][x - xStart] = pixelArray[y][x];
                    }
                }
                subImages[j][i] = new SubImage(subImagePixels, newWidth, newHeight);
            }
        }
        return subImages;
    }

    /**
     * Creates a new image by padding the original image to the next power of two in both width and height.
     * <p>
     * This method increases the width and height of the image by adding padding of white pixels
     * to make the dimensions a power of two, which is useful for certain image processing algorithms
     * (such as fast Fourier transform).
     * </p>
     *
     * @return a new {@link Image} instance with padded dimensions.
     */
    public Image createPaddingImageToNextPowerOfTwo() {
        int newWidth = nextPowerOfTwo(width);
        int newHeight = nextPowerOfTwo(height);
        int paddingTop = (newHeight - height) / PADDING_FROM_BORDER;
        int paddingLeft = (newWidth - width) / PADDING_FROM_BORDER;

        Color[][] newPixelArray = new Color[newHeight][newWidth];
        for (int i = 0; i < newHeight; i++) {
            for (int j = 0; j < newWidth; j++) {
                if (i >= paddingTop && i < paddingTop + height &&
                        j >= paddingLeft && j < paddingLeft + width) {
                    newPixelArray[i][j] = pixelArray[i - paddingTop][j - paddingLeft];
                } else {
                    newPixelArray[i][j] = Color.WHITE;
                }
            }
        }
        return new Image(newPixelArray, newWidth, newHeight);
    }

    /**
     * Calculates the brightness of the image as a weighted average of the pixel values.
     * <p>
     * The brightness is calculated using the standard luminance formula, which gives more weight to the
     * green channel, followed by the red channel, and then the blue channel.
     * </p>
     *
     * @return the calculated brightness of the image, between 0 and 1.
     */
    public double calculateBrightnessByImage() {
        double sum = 0;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Color curruntPixelColor = pixelArray[i][j];
                sum += (curruntPixelColor.getRed() * RED_VALUE) +
                        (curruntPixelColor.getGreen() * GREEN_VALUE) +
                        (curruntPixelColor.getBlue() * BLUE_VALUE);
            }
        }
        return sum / (height * width * MAX_RGB);
    }

    /**
     * Returns the next power of two that is greater than or equal to the given number.
     *
     * @param num the number to find the next power of two for.
     * @return the next power of two greater than or equal to the input number.
     */
    public static int nextPowerOfTwo(int num) {
        if (num <= 0) {
            return 1;
        }
        if ((num & (num - 1)) == 0) {
            return num;
        }
        int power = 1;
        while (power < num) {
            power <<= 1;
        }
        return power;
    }
}
