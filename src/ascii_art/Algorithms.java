package ascii_art;

import java.util.HashSet;
import java.util.Set;

public class Algorithms {
    /**
     * this program finds the single duplice element in the array
     * @param numList - the array to look for duplicate
     * @return - the duplicate number
     */
    public static int findDuplicate(int[] numList){
        int slow=numList[0],fast=numList[0];

        do
        {
            slow =numList[slow];
            fast =numList[numList[fast]];
        } while(slow != fast);

        int first =numList[0];
        while(first != slow)
        {
            first = numList[first];
            slow = numList[slow];
        }
        return first;

    }

    /**
     * in this met
     * in order to solve the problem, i made a hash set that maps every char to its morse code.
     * in order to return the uniqe number of stings, i chose to use the hash set.
     * because in the hase set counts only one reception (as we are asked for)
     * @param words - a list of english words
     * @return - the number of unique morse code in the given list
     */
    public static int uniqueMorseRepresentations(String[] words){
        String[] arr = new String[]{".-", "-...", "-.-.", "-..",
                ".", "..-.", "--.", "....", "..", ".---", "-.-", ".-..", "--", "-.", "---",
                ".--.", "--.-", ".-.", "...", "-", "..-", "...-", ".--", "-..-", "-.--", "--.."};
        Set<String> setOfMorseUniqueCode = new HashSet<>();
        for (String s : words) {
            StringBuilder stringBuilder = new StringBuilder();
            for (char ch : s.toCharArray()) {
                stringBuilder.append(arr[ch - 'a']);
            }
            setOfMorseUniqueCode.add(stringBuilder.toString());
        }

        return setOfMorseUniqueCode.size();
    }


}
