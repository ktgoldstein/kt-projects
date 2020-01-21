import java.io.File;

import components.set.Set;
import components.set.Set1L;
import components.simplereader.SimpleReader;
import components.simplereader.SimpleReader1L;
import components.simplewriter.SimpleWriter;
import components.simplewriter.SimpleWriter1L;

/**
 * Program takes the name/location of a folder as input. Folder must contain a
 * series of .txt files. This program does a number of things as a result:<br>
 *
 * --> Asks user whether they would like to see the number of words in each
 * individual document (in addition to the sum of words in all documents), and
 * reports the individual word counts of each document if so <br>
 * --> reports the total number of documents,<br>
 * --> reports the total number of words in all documents combined,<br>
 * --> reports a few basic statistics regarding the sums and the averages of the
 * number of words in the documents when it is done reading.<br>
 *
 *
 * @author KT Goldstein
 * @version 20191005
 *
 */
public final class WordCounting {

    /**
     * Private constructor so this utility class cannot be instantiated.
     */
    private WordCounting() {
    }

    /**
     * Creates a set that contains separators/whitespace characters, i.e. any
     * character that is not included in the definition of a "word."
     *
     * @return A set containing all separator characters.
     * @ensures createSeparators = [a set containing separator characters]
     */
    private static Set<Character> createSeparators() {
        Set<Character> separators = new Set1L<>();
        final char[] arrayOfSeps = { '!', ',', '.', ' ', '"', ';', ':', '(',
                ')', '?', '/', '-' };
        //Above: array of chosen separators to be read into a Set<Character>

        for (int i = 0; i < arrayOfSeps.length; i++) {
            separators.add(arrayOfSeps[i]);
        }
        return separators;
    }

    /**
     * Counts and returns the number of words in a file using a given reader for
     * a file.
     *
     * @param fileReader
     *            reader for the file/input stream
     * @return number of words contained in the text file
     */
    private static int numberOfWordsInFile(SimpleReader fileReader) {
        int numberOfWordsInFile = 0;
        Set<Character> separators = createSeparators();
        /*
         * Above: creates the set of characters used to determine what is and is
         * not a word
         */
        /*
         * Below: begin to read through the file and count the words
         */
        while (!fileReader.atEOS()) {
            String toRead = fileReader.nextLine();
            int position = 0;

            while (position < toRead.length()) {
                String nextWordOrSep = nextWordOrSeparator(toRead, position,
                        separators);
                position += nextWordOrSep.length();
                if (!separators.contains(nextWordOrSep.charAt(0))
                        && !nextWordOrSep.equals("")) {
                    numberOfWordsInFile++;
                }
                /*
                 * if the word contains a separator then it's not a word, so
                 * don't add to the word count.
                 */
            }

        }
        return numberOfWordsInFile;
    }

    /**
     * Returns the first "word" (maximal length string of characters not in
     * {@code separators}) or "separator string" (maximal length string of
     * characters in {@code separators}) in the given {@code text} starting at
     * the given {@code position}.
     *
     * @param text
     *            the {@code String} from which to get the word or separator
     *            string
     * @param position
     *            the starting index
     * @param separators
     *            the {@code Set} of separator characters
     * @return the first word or separator string found in {@code text} starting
     *         at index {@code position}
     * @requires 0 <= position < |text|
     * @ensures <pre>
     * nextWordOrSeparator =
     *   text[position, position + |nextWordOrSeparator|)  and
     * if entries(text[position, position + 1)) intersection separators = {}
     * then
     *   entries(nextWordOrSeparator) intersection separators = {}  and
     *   (position + |nextWordOrSeparator| = |text|  or
     *    entries(text[position, position + |nextWordOrSeparator| + 1))
     *      intersection separators /= {})
     * else
     *   entries(nextWordOrSeparator) is subset of separators  and
     *   (position + |nextWordOrSeparator| = |text|  or
     *    entries(text[position, position + |nextWordOrSeparator| + 1))
     *      is not subset of separators)
     * </pre>
     */
    private static String nextWordOrSeparator(String text, int position,
            Set<Character> separators) {
        String wordOrSep = "";
        //Below: code for creating a "separator" word
        if (separators.contains(text.charAt(position))) {
            for (int i = position; i < text.length()
                    && separators.contains(text.charAt(i)); i++) {
                wordOrSep += text.charAt(i);
            }
            //Below: code for creating a word without separators
        } else {
            for (int i = position; i < text.length()
                    && !separators.contains(text.charAt(i)); i++) {
                wordOrSep += text.charAt(i);
            }
        }
        return wordOrSep;
    }

    /**
     * Method to get "yes" or "no" input from a user; repeats asking for an
     * answer if one is not given.
     *
     * @param in
     *            the input stream
     * @param out
     *            the output stream
     * @return a boolean reporting whether the user has said yes
     */
    private static boolean getUserYesOrNo(SimpleReader in, SimpleWriter out) {
        String answer = in.nextLine();
        boolean answerIsYes = true;
        while (!answer.equalsIgnoreCase("y") && !answer.equalsIgnoreCase("n")) {
            out.print("Error: please enter 'y' or 'n' as a response: ");
            answer = in.nextLine();
        }
        if (answer.equalsIgnoreCase("n")) {
            answerIsYes = false;
        }
        return answerIsYes;
    }

    /**
     * Main method.
     *
     * @param args
     *            the command line arguments
     */
    public static void main(String[] args) {
        SimpleReader in = new SimpleReader1L();
        SimpleWriter out = new SimpleWriter1L();
        out.print("Please enter the name and/or path to a folder "
                + "containing .txt files: ");
        String folderName = in.nextLine();
        File folder = new File(folderName);
        File[] allTxtFiles = folder.listFiles();
        /*
         * Above: reads all text files into an array.
         */
        out.print("Would you like to write to a file instead of printing the "
                + "results to the console? (Please type 'y' or 'n' to respond): ");
        boolean writeToFile = getUserYesOrNo(in, out);
        if (writeToFile) {
            out.print(
                    "Please enter the name of a file you'd like to write to: ");
            String toWriteTo = in.nextLine();
            out.close();
            out = new SimpleWriter1L(toWriteTo);
        }

        /*
         * Get input from the user about whether to change the default word
         * count number.
         */
        out.print(
                "\nThis program will automatically keep track of the number of files "
                        + "that fall above or below 1000 words. \n"
                        + "However, you have the option to keep track of a"
                        + " different word count instead. \nWould you like to "
                        + "keep track of a different word count? "
                        + "(Please type 'y' or 'n' to respond):");
        boolean keepTrackOfNumber = getUserYesOrNo(in, out);
        int comparisonNumber = 1000;
        if (keepTrackOfNumber) {
            out.print("Please enter the word count (natural numbers only): ");
            comparisonNumber = Integer.parseInt(in.nextLine());
        }

        out.print("\nFinally, would you like every file name to be "
                + "printed with its respective word count \n"
                + "(as opposed to just the summary printing)? \n"
                + "(Please type 'y' or 'n' to respond: ");
        boolean userWantsIndividualFiles = getUserYesOrNo(in, out);

        int fileCount = 0;
        int totalWords = 0;
        int belowComparison = 0;
        int totalBelow = 0;
        int atOrAboveComparison = 0;
        int totalAtOrAbove = 0;

        /*
         * Below: turns entire pathname of the File into a String, so the
         * SimpleReader will be able to open this file.
         */

        for (fileCount = 0; fileCount < allTxtFiles.length; fileCount++) {
            File toCountWords = allTxtFiles[fileCount];
            String nameOfFile = toCountWords.toString();

            /*
             * Below: Keeps count of each file's word count in a separate
             * category depending whether it's above or below the comparison
             * number
             */

            SimpleReader fileReader = new SimpleReader1L(nameOfFile);
            int wordsInFile = numberOfWordsInFile(fileReader);
            totalWords += wordsInFile;
            if (wordsInFile < comparisonNumber) {
                belowComparison++;
                totalBelow += wordsInFile;
            } else {
                atOrAboveComparison++;
                totalAtOrAbove += wordsInFile;
            }

            /*
             * Below: will print out the name of the file each time if the user
             * asked for it previously
             */
            if (userWantsIndividualFiles) {
                String actualFileNameWithoutPath = nameOfFile
                        .substring(folderName.length() + 1);
                out.println(actualFileNameWithoutPath + ": " + wordsInFile);
            }

        }

        double averageNumberOfWords = 0;
        if (fileCount > 0) {
            averageNumberOfWords = (double) totalWords / (double) fileCount;
        }

        out.println();
        out.println("************************************STATS************"
                + "************************");
        out.println();

        out.println("Total number of words in folder: " + totalWords);
        out.println("Total number of files: " + fileCount);

        out.println(
                "Average number of words per file: " + averageNumberOfWords);

        out.println();

        out.println("Number of files " + comparisonNumber
                + " words in length or above: " + atOrAboveComparison);
        out.println("Number of files below " + comparisonNumber
                + " words in length: " + belowComparison);

        out.println();

        out.println("Total words from files " + comparisonNumber
                + " words in length or above: " + totalAtOrAbove);
        out.println("Total words from files below " + comparisonNumber
                + " words in length: " + totalBelow);

        /*
         * Close input and output streams
         */
        System.out.print("Done!");
        in.close();
        out.close();
    }

}
