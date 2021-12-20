package ascii_art.img_to_char;

import image.Image;

import java.awt.*;
import java.util.HashMap;

import static java.lang.Math.abs;

/**
 * this class is responsible to Converts image files to Ascii art
 */
public class BrightnessImgCharMatcher {
    private final HashMap<Image, Double> cache = new HashMap<>();
    private static final int NUMBER_OF_PIXELS = 16;
    private static final int WHITE = 255;
    private final Image image;
    private final String font;

    /**
     * Constructor
     * @param image - the image to convert to ASCII
     * @param font - the font name
     */
    public BrightnessImgCharMatcher(Image image, String font) {
        this.image = image;
        this.font = font;
    }

    /**
     * To calculate the clarity of a character, we will convert the same character into a Boolean 2D array
     * We will count the cell values where the value is true and then divide  the sum in the sum of the pixels
     * in order to get a number between 1-0 that represents the brightness level of the character.
     * @param charSet -  The set of characters through which we want to draw our picture.
     * @return - 2D array that represent brightness level of the every character.
     */
    private double[] LevelOfBrightness(Character[] charSet) {
        double[] levelOfBrightness = new double[charSet.length];
        for (int i = 0; i < levelOfBrightness.length; i++) {
            int numberOfTruesValues = 0;
            boolean[][] mapping = CharRenderer.getImg(charSet[i], NUMBER_OF_PIXELS, font);
            for (boolean[] rows_of_pixel : mapping) {
                for (boolean pixel : rows_of_pixel) {
                    if (pixel) {
                        numberOfTruesValues += 1;
                    }
                }
            }
            levelOfBrightness[i] = (double) numberOfTruesValues / (mapping.length * mapping.length);
        }
        return levelOfBrightness;
    }

    /**
     * We will perform a linear stretch on our array in order to normalize the brightness values of
     * Characters by stretching the edge values. This is in order to avoid a situation in which we get a picture
     * with similar shades.The stretch sharpens for us the differences in clarity between the different character
     * And so we avoid such a situation.
     * @param levelOfBrightness -  2D array that represents the brightness level of the every character.
     * @return - the 2D array after linear stretch
     */
    private static double[] makingLinearStretching(double[] levelOfBrightness) {
        double[] afterLinearStretch = new double[levelOfBrightness.length];
        double max = getMax(levelOfBrightness);
        double min = getMin(levelOfBrightness);
        for (int i=0; i<levelOfBrightness.length; i++) {
                afterLinearStretch[i] = (levelOfBrightness[i] - min)/(max-min);
            }
        return afterLinearStretch;
    }

    /**
     * This method goes through all pixels of the image, calculates the average value of
     * The pixel and returns this value. (after normalization)
     * @param image - the image to go threw all her pixels
     * @return  - calculates the average value of the pixel
     */
    private static double CalculateAverage(Image image) {
        int numberOfPixels = 0;
        double sum = 0;
        for (Color pixel : image.pixels()) {
            numberOfPixels += 1;
            double greyPixel = pixel.getRed() * 0.2126 + pixel.getGreen() * 0.7152 + pixel.getBlue() * 0.0722;
            sum += greyPixel;
        }
        return  (sum / WHITE)/numberOfPixels ;
    }

    /**
     * the method will divide the picture into a number of Sub-images required, calculate the brightness level for
     * sub-image and match the ASCII character with the closest brightness level.This character will be kept in the
     * appropriate position in the array.
     * @param afterStretch -the 2D array after stretch
     * @param numCharsInRow -the number of characters that will be drawn in each line in the ASCI image
     * @param charSet - The set of characters through which we want to draw our picture.
     * @return - The method returns the 2D array of ASCII characters after conversion.
     */
    private char[][] convertImageToAscii(double[] afterStretch, int numCharsInRow, Character[] charSet) {
        int pixels = image.getWidth() / numCharsInRow;
        char[][] asciiArt = new char[image.getHeight() / pixels][image.getWidth() / pixels];
        int i = 0;
        double brightNess;
        for (Image subImage : image.squareSubImagesOfSize(pixels)) {
            if (cache.containsKey(subImage)) {
                //System.out.println("From Cache");
                brightNess = cache.get(subImage);
            }
            brightNess = CalculateAverage(subImage);
            cache.put(subImage, brightNess);
            int indexOfClose = checkCloseCharIndex(afterStretch, brightNess);
            asciiArt[i / (image.getWidth() / pixels)][i % (image.getWidth() / pixels)] = charSet[indexOfClose];
            i++;
        }
        return asciiArt;
    }

    /**
     *
     * @param afterStretch - the 2D array after Linear stretch
     * @param brightNess - the average value of a pixel
     * @return - The closest index of the list to the average value
     */
    private static int checkCloseCharIndex(double[] afterStretch, double brightNess) {
        double closest = abs(afterStretch[0] - brightNess);
        int indexOfClosest = 0;
        for (int i = 1; i < afterStretch.length; i++) {
            if (abs(afterStretch[i] - brightNess) < closest) {
                closest = abs(afterStretch[i] - brightNess);
                indexOfClosest = i;
            }
        }
        return indexOfClosest;
    }

    /**
     *
     * @param numCharsInRow - the number of characters that will be drawn in each line in the ASCI image
     * @param charSet - The set of characters through which we want to draw our picture.
     * @return - The method returns a two-dimensional array of characters (ASCII) representing an image
     * (the same image obtained In the builder
     */
    public char[][] chooseChars(int numCharsInRow, Character[] charSet){
        double[] afterBrightness = LevelOfBrightness(charSet);
        double[] afterStretch = makingLinearStretching(afterBrightness);
        return convertImageToAscii(afterStretch,numCharsInRow,charSet);
    }


    /**
     * finds the minimum value of the linear stretch
     * @param arr - array of doubles
     * @return - the maximal element in the array
     */
    private static double getMax(double[] arr) {
        double max = arr[0];
        for (double element : arr) {
            if (element > max)
                max = element;
        }
        return max;
    }

    /**
     * finds the maximum value of the linear stretch
     * @param arr - array of doubles
     * @return - the min element in the array
     */
    private static double getMin(double[] arr) {
        double min = arr[0];
        for (double element : arr) {
            if (element < min)
                min = element;
        }
        return min;
    }
}





