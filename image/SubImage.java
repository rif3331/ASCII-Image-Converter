package image;

import java.awt.*;

/**
 * The SubImage class represents a sub-image, which is a smaller section of an image.
 * This class extends the {@link Image} class and inherits its functionality.
 * <p>
 * A sub-image is typically a smaller portion of an original image, extracted based on
 * a given resolution. It is represented by a 2D array of {@link Color} objects,
 * and this class allows for accessing the pixel data of the sub-image as well as
 * performing image operations.
 * </p>
 */
public class SubImage extends Image {

    /**
     * Constructor for creating a sub-image from a 2D array of pixels.
     *
     * @param pixelArray the 2D array of {@link Color} representing the pixels of the sub-image.
     * @param width      the width of the sub-image.
     * @param height     the height of the sub-image.
     */
    public SubImage(Color[][] pixelArray, int width, int height) {
        super(pixelArray, width, height);
    }
}
