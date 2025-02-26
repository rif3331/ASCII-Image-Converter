package image_char_matching;

import java.util.HashMap;
import java.util.Map;

/**
 * The SubImgCharMatcher class is responsible for managing a character set,
 * where each character is associated with a brightness value. The class provides methods to add, remove,
 * and normalize characters based on their brightness. It also allows for selecting the closest matching
 * character based on a given brightness value.
 */
public class SubImgCharMatcher {

    /**
     * The default rounding method used for brightness comparison.
     * This method calculates the absolute difference between brightness values
     * and selects the character whose brightness is closest to the given value.
     */
    public static final String ROUND_TYPE_DEFAULT = "abs";

    /**
     * The rounding method that rounds brightness values up.
     * This method selects the character whose brightness is greater than or equal
     * to the given brightness value but as close as possible to it.
     */
    public static final String ROUND_TYPE_1 = "up";

    /**
     * The rounding method that rounds brightness values down.
     * This method selects the character whose brightness is less than or equal
     * to the given brightness value but as close as possible to it.
     */
    public static final String ROUND_TYPE_2 = "down";


    // Constants for brightness rounding methods
    private static final String STRING_TO_SPLIT = " ";
    private static final char SPACE_CHAR_VALUE = ' ';
    private static final double DEFAULT_VALUE_CHARSET = 0.0;
    private static final int SIZE_MIN_MAX_ARRAY = 2;

    // Maps to store the character set and normalized brightness values
    private final Map<Character, Double> charset;
    private final Map<Character, Double> charsetNormal;
    private String roundBrightness;

    /**
     * Constructor for SubImgCharMatcher, initializing the charset with characters
     * and setting the rounding method for brightness comparisons.
     *
     * @param charset an array of characters to initialize the character set.
     */
    public SubImgCharMatcher(char[] charset) {
        roundBrightness = ROUND_TYPE_DEFAULT;
        this.charset = new HashMap<>();
        this.charsetNormal = new HashMap<>();
        for (char c : charset) {
            this.charset.put(c, calculateBrightnessByChar(c));
            this.charsetNormal.put(c, DEFAULT_VALUE_CHARSET);
        }
        this.normalCharBrightness();
    }

    /**
     * Calculates the brightness of a character by converting it to a black-and-white matrix
     * and calculating the ratio of 'true' values in the matrix.
     *
     * @param c the character whose brightness is to be calculated.
     * @return the calculated brightness of the character.
     */
    private static double calculateBrightnessByChar(char c) {
        boolean[][] blackWhitTable = CharConverter.convertToBoolArray(c);
        int trueCount = 0;
        int totalCount = 0;
        for (boolean[] row : blackWhitTable) {
            for (boolean cell : row) {
                if (cell) { trueCount++; }
                totalCount++;
            }
        }
        return (double) trueCount / totalCount;
    }

    /**
     * Finds the minimum and maximum brightness values in the charset.
     *
     * @return an array where index 0 is the minimum brightness and index 1 is the maximum brightness.
     */
    private double[] findMaxAndMin() {
        double[] result = new double[SIZE_MIN_MAX_ARRAY];
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        for (double value : charset.values()) {
            if (value < min) { min = value; }
            if (value > max) { max = value; }
        }
        result[0] = min;
        result[1] = max;
        return result;
    }

    /**
     * Normalizes the brightness of characters in the charset to be between 0 and 1.
     */
    public void normalCharBrightness() {
        double minCharBrightness = findMaxAndMin()[0];
        double maxCharBrightness = findMaxAndMin()[1];
        for (Map.Entry<Character, Double> entry : charset.entrySet()) {
            char key = entry.getKey();
            double value = entry.getValue();
            double newCharBrightness = (value - minCharBrightness) / (maxCharBrightness - minCharBrightness);
            charsetNormal.put(key, newCharBrightness);
        }
    }

    /**
     * Adds a new character to the charset and recalculates the normalized brightness values.
     *
     * @param c the character to be added.
     */
    public void addChar(char c) {
        this.charset.put(c, calculateBrightnessByChar(c));
        this.charsetNormal.put(c, calculateBrightnessByChar(c));
        this.normalCharBrightness();
    }

    /**
     * Removes a character from the charset and recalculates the normalized brightness values.
     *
     * @param c the character to be removed.
     */
    public void removeChar(char c) {
        this.charset.remove(c);
        this.charsetNormal.remove(c);
        this.normalCharBrightness();
    }

    /**
     * Returns the character that corresponds to the given brightness value based on
     * the selected rounding method.
     *
     * @param brightness the brightness value for which to find the closest matching character.
     * @return the character closest to the given brightness value.
     */
    public char getCharByImageBrightness(double brightness) {
        if (ROUND_TYPE_DEFAULT.equals(roundBrightness)) {
            return findClosest(brightness, ROUND_TYPE_DEFAULT);
        } else if (ROUND_TYPE_1.equals(roundBrightness)) {
            return findClosest(brightness, ROUND_TYPE_1);
        } else if (ROUND_TYPE_2.equals(roundBrightness)) {
            return findClosest(brightness, ROUND_TYPE_2);
        }
        return SPACE_CHAR_VALUE;
    }

    /**
     * Sets the rounding method for brightness value comparison.
     *
     * @param newType the new rounding method (default, up, down).
     */
    public void setRoundBrightness(String newType) {
        this.roundBrightness = newType;
    }

    /**
     * Finds the character whose normalized brightness is closest to the given brightness value
     * based on the selected mode (absolute, up, or down rounding).
     *
     * @param brightness the brightness value to compare.
     * @param mode the rounding mode (default, up, down).
     * @return the character closest to the brightness value.
     */
    private char findClosest(double brightness, String mode) {
        char closestKey = SPACE_CHAR_VALUE;
        double comparisonValue = mode.equals(ROUND_TYPE_DEFAULT)
                ? Double.MAX_VALUE : (mode.equals(ROUND_TYPE_1) ? Double.MAX_VALUE : Double.MIN_VALUE);
        for (Map.Entry<Character, Double> entry : charsetNormal.entrySet()) {
            double value = entry.getValue();
            if (mode.equals(ROUND_TYPE_DEFAULT)) {
                double difference = Math.abs(value - brightness);
                if (difference < comparisonValue || (difference == comparisonValue &&
                        entry.getKey() < closestKey)) {
                    closestKey = entry.getKey();
                    comparisonValue = difference;
                }
            } else if (mode.equals(ROUND_TYPE_1) && value >= brightness && value < comparisonValue) {
                closestKey = entry.getKey();
                comparisonValue = value;
            } else if (mode.equals(ROUND_TYPE_2) && value <= brightness && value > comparisonValue) {
                closestKey = entry.getKey();
                comparisonValue = value;
            }
        }
        return closestKey;
    }

    /**
     * Prints the characters in the charset, separated by spaces.
     */
    public void printHashMap() {
        for (Map.Entry<Character, Double> entry : charset.entrySet()) {
            System.out.print(entry.getKey() + STRING_TO_SPLIT);
        }
        System.out.println();
    }

    /**
     * Returns the number of characters in the charset.
     *
     * @return the size of the charset.
     */
    public int getNumberOfSet() {
        return charset.size();
    }
}
