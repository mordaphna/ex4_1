package ascii_art;

import ascii_art.img_to_char.BrightnessImgCharMatcher;
import ascii_output.AsciiOutput;
import ascii_output.ConsoleAsciiOutput;
import ascii_output.HtmlAsciiOutput;
import image.Image;

import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Stream;

/**
 * this class is responsible for all the interface operations and will be controlled by
 * the driver class.
 */
public class Shell {

    // massages
    private static final String ERROR_MAX_MESSAGE = "You are using the maximal resolution";
    private static final String ERROR_MIN_MESSAGE = "You are using the minimal resolution";
    private static final String MESSAGE_SET = "Width set to %d";
    public static final String ERROR_NOT_VALID_COMMAND = "Error - you have not entered a valid command";

    private static final String FONT_NAME = "Courier New";
    private static final String OUTPUT_FILENAME = "out.html";

    //given parameters
    private static final int INITIAL_CHARS_IN_ROW = 64;
    private static final int MIN_PIXELS_PER_CHAR = 2 ;
    public static final int PARAMETER_RESOLUTION_RESIZE = 2;
    public static final int RANGE_TO_ITERATE = 1;
    public static final int FIRST_INDEX_IN_RANGE = 0;
    public static final int SECOND_INDEX_IN_RANGE = 2;
    public static final int MAXIMUM_LENGTH = 3;

    //symbols
    public static final String SHELL_STRING = ">>> ";
    public static final char HYPHEN = '-';
    public static final String STRING_SPACE = "";
    public static final char TILDA = '~';
    public static final char SPACE = ' ';

    // inputs
    public static final String SPACE1 = "space";
    public static final String ALL = "all";
    public static final String CHARS = "chars";
    public static final String RENDER = "render";
    public static final String CONSOLE="console";
    public static final String ADD = "add";
    public static final String REMOVE = "remove";
    public static final String UP = "up";
    public static final String DOWN = "down";
    public static final String RES = "res";
    private static final String CMD_EXIT = "exit";
    private static final String INITIAL_CHARS_RANGE = "0-9";

    // numbers
    public static final int ONE = 1;
    public static final int ZERO = 0;
    public static final int TWO = 2;

    //fields
    private final BrightnessImgCharMatcher charMatcher;
    private final ConsoleAsciiOutput outputConsole;
    private final AsciiOutput output;
    private final Set<Character> charSet = new HashSet<>();
    private final int minCharsInRow;
    private final int maxCharsInRow;
    private int charsInRow;
    private boolean outPutHTML = true;

    /**
     * the shell contractor.
     * @param img - the img to convert to ASCII
     */
    public Shell(Image img) {
        minCharsInRow = Math.max(1, img.getWidth()/img.getHeight());
        maxCharsInRow = img.getWidth() / MIN_PIXELS_PER_CHAR;
        charsInRow = Math.max(Math.min(INITIAL_CHARS_IN_ROW, maxCharsInRow), minCharsInRow);
        charMatcher = new BrightnessImgCharMatcher(img, FONT_NAME);
        outputConsole = new ConsoleAsciiOutput();
        output = new HtmlAsciiOutput(OUTPUT_FILENAME, FONT_NAME);
        addChars(INITIAL_CHARS_RANGE);

    }

    /**
     * this function translates the commands given by the user to the function
     * of the shell class.
     */
    public void run() {
        Scanner scanner = new Scanner(System.in);
        System.out.print(SHELL_STRING);
        String cmd = scanner.nextLine().trim();
        String[] words = cmd.split("\\s+");
        while (!words[ZERO].toLowerCase().equals(CMD_EXIT) || words.length != 1) {
            if (!words[ZERO].equals(STRING_SPACE)) {
                if (words.length > ONE) {
                    handleCommandsCasesBiggerThenOne(words);
                }
                else{
                    handleCommandCasesLessThenOne(words);
                }
            }
            System.out.print(SHELL_STRING);
            cmd = scanner.nextLine().trim();
            words = cmd.split("\\s+");
        }
    }

    /**
     * deals with the cases that the commands' length are less than one
     * @param words - the words array to handle
     */
    private void handleCommandCasesLessThenOne(String[] words) {
        switch (words[0]) {
            case CHARS:
                showChars();
                break;
            case RENDER:
                render();
                break;
            case CONSOLE:
                outPutHTML = false;
                break;
            default:
                System.out.println(ERROR_NOT_VALID_COMMAND);
        }
    }

    /**
     * handles the commands with length bigger than one
     * @param words - the command to handle
     */
    private void handleCommandsCasesBiggerThenOne(String[] words) {
        String param = SPACE1;
        param = words[ONE];
        switch (words[ZERO]){
            case ADD:
                addChars(param);
                break;
            case REMOVE:
                removeChars(param);
                break;
            case RES:
                resChange(param);
                break;
            default:
                System.out.println(ERROR_NOT_VALID_COMMAND);
        }
    }

    /**
     * This functions prints the output to the console
     */
    private void console() {
        Character [] chars = new Character [charSet.size()];
        char[][] asciiArtConsole = charMatcher.chooseChars(charsInRow,charSet.toArray(chars));
        outputConsole.output(asciiArtConsole);
    }

    /**
     * this functions prints the characters we can use to construct the img
     */
    private void showChars(){
        charSet.stream().sorted().forEach(c-> System.out.print(c + " "));
        System.out.println();

    }

    /**
     * this function appends characters to the character buffer
     * @param s - the char to add to the buffer
     */
    private void addChars(String s) {
        char[] range = parseCharRange(s);
        if(range != null){
            Stream.iterate(range[0], c -> c <= range[1], c -> (char)((int)c+1)).forEach(charSet::add);
        }
    }

    /**
     * this function takes care of three possible input cases of chars addition or remove
     * @param param - the chars to append or remove
     * @return - a 2 places array explains what to append or remove
     */
    private static char[] parseCharRange(String param){
        if (param.length() == ONE){
            return new char[]{param.charAt(FIRST_INDEX_IN_RANGE), param.charAt(FIRST_INDEX_IN_RANGE)};
        }
        if (param.equals(SPACE1)){
            return new char[]{SPACE,SPACE};
        }
        if (param.equals(ALL)){
            return new char[]{SPACE, TILDA};
        }
        if((param.charAt(ONE) == HYPHEN) && (param.length() == MAXIMUM_LENGTH)) {
            return makeHyphenCharsArray(param);
        }
        System.out.println(ERROR_NOT_VALID_COMMAND);
        return null;

    }

    /**
     * this function handles the case where we need to add a limit of charter.
     * deals with the case that the limit is from bigger to lower
     * @param param - the range
     * @return - the wanted array that displays the limit of the chars
     */
    private static char[] makeHyphenCharsArray(String param) {
        int firstChar = param.charAt(FIRST_INDEX_IN_RANGE);
        int secondChar = param.charAt(SECOND_INDEX_IN_RANGE);
        if (firstChar > secondChar) {
            int temp = firstChar;
            firstChar = secondChar;
            secondChar = temp;
        }
        return new char[]{(char)firstChar,(char)secondChar};
    }

    /**
     * this function removes characters from the character buffer
     * @param s - the char to remove to the buffer
     */
    private void removeChars(String s) {
        char[] range = parseCharRange(s);
        if (range != null) {
            Stream.iterate(range[FIRST_INDEX_IN_RANGE], c -> c <= range[RANGE_TO_ITERATE], c -> (char)
                    ((int) c + 1)).forEach(charSet::remove);
        }
    }

    /**
     * this functions controls the image resolution
     * @param s - the parameters that decides if to increase or decrease the resolution
     */
    private void resChange(String s){
        switch (s){
            case UP:
                increaseRes();
                break;
            case DOWN:
                decreaseRes();
                break;
            default:
                System.out.println(ERROR_NOT_VALID_COMMAND);
        }
    }

    /**
     * increase the resolution if the user asks. Takes care of the case where the use wants to change res
     * more than limit
     */
    private void increaseRes() {
        if(charsInRow* PARAMETER_RESOLUTION_RESIZE <= maxCharsInRow) {
            charsInRow *= PARAMETER_RESOLUTION_RESIZE;
            System.out.printf((MESSAGE_SET) + "%n", charsInRow);
        } else{
            System.out.println(ERROR_MAX_MESSAGE);
        }
    }

    /**
     * decrease the resolution if the user asks. takes care of the case where he wants to change res
     * lass than limit
     */
    private void decreaseRes() {
        if(minCharsInRow <= charsInRow/ TWO) {
            charsInRow /= PARAMETER_RESOLUTION_RESIZE;
            System.out.printf((MESSAGE_SET) + "%n", charsInRow);
        }else{
            System.out.println(ERROR_MIN_MESSAGE);
        }
    }

    /**
     * this function prints the output to the HTML file or to the console
     */
    private void render(){
        if (outPutHTML) {
            Character [] chars = new Character [charSet.size()];
            char[][] asciiArt = charMatcher.chooseChars(charsInRow,charSet.toArray(chars));
            output.output(asciiArt);
        }
        else{
            console();
        }
    }

}

