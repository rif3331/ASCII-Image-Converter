package ascii_art;
import ascii_output.ConsoleAsciiOutput;
import ascii_output.HtmlAsciiOutput;
import exceptions.BadCommandExeption;
import exceptions.BadExtraArgsException;
import exceptions.UnKnownCategoryException;
import image.Image;
import image_char_matching.SubImgCharMatcher;
import java.io.IOException;

/**
 * The Shell class is responsible for handling user input, executing various commands, and interacting
 * with other components such as image processing and output generation.
 */
public class Shell {
    // Constants
    private static final String OUTPUT_TYPE_DEFAULT = "console";
    private static final String OUTPUT_NAME_HTML_FILE_DEFAULT = "out.html";
    private static final String OUTPUT_WRITE_STYLE_HTML_FILE_DEFAULT = "Courier New";
    private static final String EMPTY_STRING  = " ";
    private static final String STRING_TO_GET_INPUT_FROM_USER = ">>> ";
    private static final String VALUE_TO_STOP_RUNNING_PROGRAM = "exit";
    private static final String VALUE_TO_PRINT_CHARSET = "chars";
    private static final String VALUE_TO_ADD_VALUE_TO_CHARSET = "add";
    private static final String VALUE_TO_ADD_ALL_VALUES_TO_CHARSET = "all";
    private static final String VALUE_TO_ADD_SPACE_TO_CHARSET = "space";
    private static final String VALUE_TO_REMOVE_VALUE_TO_CHARSET = "remove";
    private static final String VALUE_TO_CHANGE_RESOLUTION = "res";
    private static final String VALUE_TO_IMPROVE_RESOLUTION = "up";
    private static final String VALUE_TO_DECREASE_RESOLUTION = "down";
    private static final String VALUE_TO_CHANGE_ROUND_TYPE = "round";
    private static final String VALUE_TO_CHANGE_OUTPUT_TYPE = "output";
    private static final String VALUE_TO_RUN_ALGORITHM = "asciiArt";
    private static final String MSG_INCORRECT_COMMAND_FORMAT = "Did not execute due to incorrect command.";
    private static final String MSG_INCORRECT_OUTPUT_COMMAND_FORMAT
            = "Did not change output method due to incorrect format.";
    private static final String MSG_INCORRECT_ROUNDING_COMMAND_FORMAT
            = "Did not change rounding method due to incorrect format.";
    private static final String MSG_CHANGE_RESOLUTION_SET = "Resolution set to ";
    private static final String END_MSG_CHANGE_RESOLUTION_SET = ".";
    private static final String MSG_INCORRECT_BOUNDARIES_RESOLUTION_COMMAND_FORMAT
            = "Did not change resolution due to exceeding boundaries.";
    private static final String BEGIN_MSG_INCORRECT_ACTION_UPDATE_SET = "Did not ";
    private static final String END_MSG_INCORRECT_ACTION_UPDATE_SET = " due to incorrect format.";
    private static final String
            MSG_INCORRECT_RESOLUTION_COMMAND_FORMAT = "Did not change resolution due to incorrect format.";
    private static final String BEGIN_MSG_ERR_LOADING_IMAGE = "Error loading image: ";
    private static final String MSG_INVALID_SET = "Did not execute. Charset is too small";
    private static final String OUTPUT_TYPE_2 = "html";
    private static final String STRING_TO_SPLIT = " ";
    private static final char[] CHARSET_VALUES_DEFAULT = {'0','1','2', '3', '4', '5', '6', '7', '8', '9'};
    private static final char SECOND_CHAR_VALUE_ADD_RANGE = '-';
    private static final char SPACE_CHAR_VALUE = ' ';
    private static final int RESOLUTION_DEFAULT = 2;
    private static final int INDEX_OF_FIRST_WORD = 0;
    private static final int COUNT_DIVIDE_PARTS = 3;
    private static final int SECOND_WORD_INDEX = 1;
    private static final int FIRST_LEGAL_CHAR = 32;
    private static final int LAST_LEGAL_CHAR = 126;
    private static final int COUNT_LENGTH_ADD_RANGE = 3;
    private static final int FIRST_CHAR_ADD_RANGE = 0;
    private static final int SECOND_CHAR_ADD_RANGE = 1;
    private static final int THIRD_CHAR_ADD_RANGE = 2;
    private static final int COUNT_OF_UPDATE_RESOLUTION = 2;
    private static final int MIN_CHARS_IN_ROW = 1;
    private static final int CORRECT_NUMBER_OF_ARG = 1;
    private static final int IMG_ARG_INDEX = 0;
    private static final int MIN_CHARS_IN_CHARSET = 2;
    private final SubImgCharMatcher matcher;
    private final ConsoleAsciiOutput console;
    private final HtmlAsciiOutput htmlOutput;
    private Image image;
    private String outputType;
    private int resolution;

    /**
     * Constructs a Shell object with default output type and resolution settings.
     */
    public Shell() {
        this.outputType = OUTPUT_TYPE_DEFAULT;
        this.matcher = new SubImgCharMatcher(CHARSET_VALUES_DEFAULT);
        this.resolution = RESOLUTION_DEFAULT;
        this.htmlOutput = new HtmlAsciiOutput(OUTPUT_NAME_HTML_FILE_DEFAULT,
                OUTPUT_WRITE_STYLE_HTML_FILE_DEFAULT);
        this.console = new ConsoleAsciiOutput();
    }

    /**
     * Runs the shell, prompting for user input and executing corresponding commands.
     *
     * @param imageName the name of the image file to load.
     * @throws IOException if there is an error loading the image.
     */
    public void run(String imageName) throws IOException {
        this.image = new Image(imageName);
        String input;
        while (true) {
            System.out.print(STRING_TO_GET_INPUT_FROM_USER);
            input = KeyboardInput.readLine();
            try {
                String[] parts = input.split(STRING_TO_SPLIT, COUNT_DIVIDE_PARTS);
                String firstWord = parts[INDEX_OF_FIRST_WORD];
                String secondWord = parts.length > SECOND_WORD_INDEX ? parts[SECOND_WORD_INDEX] : EMPTY_STRING;
                if (input.equals(VALUE_TO_STOP_RUNNING_PROGRAM)) {
                    return;
                }
                switch (firstWord) {
                    case VALUE_TO_PRINT_CHARSET -> printCharset();
                    case VALUE_TO_ADD_VALUE_TO_CHARSET ->
                            updateCharset(secondWord, VALUE_TO_ADD_VALUE_TO_CHARSET);
                    case VALUE_TO_REMOVE_VALUE_TO_CHARSET ->
                            updateCharset(secondWord, VALUE_TO_REMOVE_VALUE_TO_CHARSET);
                    case VALUE_TO_RUN_ALGORITHM -> runAsciiArt();
                    case VALUE_TO_CHANGE_RESOLUTION -> updateResolution(secondWord);
                    case VALUE_TO_CHANGE_ROUND_TYPE -> roundBrightnessChar(secondWord);
                    case VALUE_TO_CHANGE_OUTPUT_TYPE -> updateOutputType(secondWord);
                    default -> throw new UnKnownCategoryException(MSG_INCORRECT_COMMAND_FORMAT);
                }
            } catch (BadCommandExeption e) {
                System.out.println(e.getMessage());
            }
        }
    }

    /**
     * Updates the output type based on the given action.
     *
     * @param action the action indicating which output type to set.
     */
    private void updateOutputType(String action) {
        if (action.equals(OUTPUT_TYPE_2) || action.equals(OUTPUT_TYPE_DEFAULT)) {
            this.outputType = action;
            return;
        }
        throw new BadCommandExeption(MSG_INCORRECT_OUTPUT_COMMAND_FORMAT);
    }

    /**
     * Prints the current character set.
     */
    private void printCharset() {
        this.matcher.printHashMap();
    }

    /**
     * Runs the ASCII art algorithm and outputs the result.
     */
    private void runAsciiArt() {
        if (matcher.getNumberOfSet() < MIN_CHARS_IN_CHARSET) {
            throw new BadCommandExeption(MSG_INVALID_SET);
        }
        AsciiArtAlgorithm asciiArtAlgorithm = new AsciiArtAlgorithm(this.matcher,
                this.image, this.resolution);
        char[][] asciiArt = asciiArtAlgorithm.run();
        if (outputType.equals(OUTPUT_TYPE_DEFAULT)) {
            console.out(asciiArt);
            return;
        }
        htmlOutput.out(asciiArt);
    }

    /**
     * Checks if a character is legal (between ASCII codes 32 and 126).
     *
     * @param c the character to check.
     * @return true if the character is legal, false otherwise.
     */
    private boolean isLegalChar(char c) {
        return c >= FIRST_LEGAL_CHAR && c <= LAST_LEGAL_CHAR;
    }

    /**
     * Updates a range of characters in the charset based on the given action.
     *
     * @param first the starting ASCII code.
     * @param last the ending ASCII code.
     * @param action the action to perform (add or remove).
     */
    private void updateRangeChars(int first, int last, String action) {
        for (int i = first; i <= last; i++) {
            char asciiChar = (char) i;
            if (action.equals(VALUE_TO_ADD_VALUE_TO_CHARSET)) {
                matcher.addChar(asciiChar);
            } else {
                matcher.removeChar(asciiChar);
            }
        }
    }

    /**
     * Updates the charset by adding or removing characters based on the action.
     *
     * @param addValue the character(s) to add or remove.
     * @param action the action to perform (add or remove).
     */
    private void updateCharset(String addValue, String action) {
        if (addValue.length() == 1 && isLegalChar(addValue.charAt(FIRST_CHAR_ADD_RANGE))) {
            if (action.equals(VALUE_TO_ADD_VALUE_TO_CHARSET)) {
                matcher.addChar(addValue.charAt(FIRST_CHAR_ADD_RANGE));
            } else {
                matcher.removeChar(addValue.charAt(FIRST_CHAR_ADD_RANGE));
            }
            return;
        }
        if (addValue.equals(VALUE_TO_ADD_ALL_VALUES_TO_CHARSET)) {
            updateRangeChars(FIRST_LEGAL_CHAR, LAST_LEGAL_CHAR, action);
            return;
        }
        if (addValue.equals(VALUE_TO_ADD_SPACE_TO_CHARSET)) {
            if (action.equals(VALUE_TO_ADD_VALUE_TO_CHARSET)) {
                matcher.addChar(SPACE_CHAR_VALUE);
            } else {
                matcher.removeChar(SPACE_CHAR_VALUE);
            }
            return;
        }
        if (addValue.length() == COUNT_LENGTH_ADD_RANGE) {
            char firstChar = addValue.charAt(FIRST_CHAR_ADD_RANGE);
            char secondChar = addValue.charAt(SECOND_CHAR_ADD_RANGE);
            char thirdChar = addValue.charAt(THIRD_CHAR_ADD_RANGE);
            if (secondChar == SECOND_CHAR_VALUE_ADD_RANGE) {
                if (thirdChar < firstChar) {
                    char temp = firstChar;
                    firstChar = thirdChar;
                    thirdChar = temp;
                }
                updateRangeChars(firstChar, thirdChar, action);
                return;
            }
        }
        throw new BadExtraArgsException(BEGIN_MSG_INCORRECT_ACTION_UPDATE_SET
                + action + END_MSG_INCORRECT_ACTION_UPDATE_SET);
    }

    /**
     * Updates the resolution based on the given action.
     *
     * @param action the action to perform (up, down, or set).
     */
    private void updateResolution(String action) {
        int maxCharsInRow = image.getWidth();
        double minCharsInRow = Math.max(MIN_CHARS_IN_ROW, image.getWidth() / image.getHeight());
        if (action.isEmpty()) {
            System.out.println(MSG_CHANGE_RESOLUTION_SET + resolution + END_MSG_CHANGE_RESOLUTION_SET);
        } else if (action.equals(VALUE_TO_IMPROVE_RESOLUTION) &&
                resolution * COUNT_OF_UPDATE_RESOLUTION <= maxCharsInRow) {
            resolution *= COUNT_OF_UPDATE_RESOLUTION;
            System.out.println(MSG_CHANGE_RESOLUTION_SET + resolution + END_MSG_CHANGE_RESOLUTION_SET);
        } else if (action.equals(VALUE_TO_DECREASE_RESOLUTION) && (double)
                resolution / COUNT_OF_UPDATE_RESOLUTION >= minCharsInRow) {
            resolution /= COUNT_OF_UPDATE_RESOLUTION;
            System.out.println(MSG_CHANGE_RESOLUTION_SET + resolution + END_MSG_CHANGE_RESOLUTION_SET);
        } else if (action.equals(VALUE_TO_DECREASE_RESOLUTION) ||
                action.equals(VALUE_TO_IMPROVE_RESOLUTION)) {
            throw new BadCommandExeption(MSG_INCORRECT_BOUNDARIES_RESOLUTION_COMMAND_FORMAT);
        } else {
            throw new BadCommandExeption(MSG_INCORRECT_RESOLUTION_COMMAND_FORMAT);
        }
    }

    /**
     * Changes the rounding method for brightness adjustment.
     *
     * @param action the action indicating which rounding method to set.
     */
    private void roundBrightnessChar(String action) {
        switch (action) {
            case SubImgCharMatcher.ROUND_TYPE_1 -> {
                matcher.setRoundBrightness(SubImgCharMatcher.ROUND_TYPE_1);
                return;
            }
            case SubImgCharMatcher.ROUND_TYPE_2 -> {
                matcher.setRoundBrightness(SubImgCharMatcher.ROUND_TYPE_2);
                return;
            }
            case SubImgCharMatcher.ROUND_TYPE_DEFAULT -> {
                matcher.setRoundBrightness(SubImgCharMatcher.ROUND_TYPE_DEFAULT);
                return;
            }
        }
        throw new BadCommandExeption(MSG_INCORRECT_ROUNDING_COMMAND_FORMAT);
    }

    /**
     * The main entry point for running the shell application.
     *
     * @param args command-line arguments, including the image file name.
     */
    public static void main(String[] args) {
        try {
            if (args.length != CORRECT_NUMBER_OF_ARG) {
                return;
            }
            Shell shell = new Shell();
            shell.run(args[IMG_ARG_INDEX]);
        } catch (IOException e) {
            System.out.println(BEGIN_MSG_ERR_LOADING_IMAGE + e.getMessage());
        }
    }
}
