import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ReadabilityScore {
    private int averageAge = 0; //used for calculating average age should the user chooses all readability tests
    private String[] sentencesInText;
    private String[] wordsInText;
    private String[] charactersInText;
    private int numOfSyllables;
    private int numOfPollysyllables;

    /**
     * Takes the file location of a text file and displays it and provides additional information about that text
     * @param fileLocation - location where the text file is located
     */
    public ReadabilityScore(String fileLocation) { //pass args[0] for command line
        try {
            String text = readFile(fileLocation);
            System.out.println("The text is:");
            System.out.println(text);

            sentencesInText = text.split("[.?!]+");
            wordsInText = text.split("\\pZ");
            charactersInText = text.replaceAll("[\\s]","").split("");
            numOfSyllables = sylCounter(wordsInText, "S");
            numOfPollysyllables = sylCounter(wordsInText, "P");

            System.out.printf("Words: %d \n", wordsInText.length);
            System.out.printf("Sentences: %d \n", sentencesInText.length);
            System.out.printf("Characters: %d \n", charactersInText.length);
            System.out.printf("Syllables: %d \n", numOfSyllables);
            System.out.printf("Polysyllables: %d \n", numOfPollysyllables);

        } catch (IOException e) {
            System.out.println("Exception occurred:" + e.getMessage());
        }
    }

    /*
    Displays the readability scores based on what the user chooses
     */
    public void scoreCalculate(String score) {
        switch (score) {
            case "ARI":
                double ARI = getReadIndex(charactersInText.length, wordsInText.length, sentencesInText.length);
                int ageARI = whoCanRead((int) Math.round(ARI));
                System.out.printf("Automated Readability Index: %.2f (about %d year olds).\n", ARI, ageARI);
                averageAge += ageARI;
                break;

            case "FK":
                double flechIndex = fleschTest(numOfSyllables);
                int ageFK = whoCanRead((int) Math.round(flechIndex));
                System.out.printf("Flesch–Kincaid readability tests: %.2f (about %d year olds).\n", flechIndex, ageFK);
                averageAge += ageFK;
                break;

            case "SMOG":
                double smogInex = smogTest(numOfPollysyllables);
                int ageSmog = whoCanRead((int) Math.round(smogInex));
                System.out.printf("Simple Measure of Gobbledygook: %.2f (about %d year olds).\n", smogInex, ageSmog);
                averageAge += ageSmog;
                break;

            case "CL":
                double colemanIndex = coleManIndex();
                int ageCole = whoCanRead((int) Math.round(colemanIndex));
                System.out.printf("Coleman–Liau index: %.2f (about %d year olds).\n", colemanIndex, ageCole);
                averageAge += ageCole;
                break;

            case "all":
                String[] all = {"ARI", "FK", "SMOG", "CL"};
                for (String s : all) {
                    scoreCalculate(s);
                }
                System.out.printf("This text should be understood in average by %.2f year olds.", (double)averageAge/4);
                break;

            default:
                System.out.println("Unrecognized input, try again!");
                break;
        }
    }

    //=========================4 readability tests=============================================

    /*
    Automated readability Index
     */
    public double getReadIndex(double numOfCharacters, double numOfWords, double numOfSentences) {
        return 4.71 * (numOfCharacters / numOfWords) + 0.5 * (numOfWords / numOfSentences) - 21.43;
    }

    /*
    Flesch-Kincaid Readability Test
     */
    public double fleschTest(int numOfSyllables) {
        return 0.39 * wordsInText.length / sentencesInText.length + 11.8 * numOfSyllables / wordsInText.length - 15.59;
    }

    /*
    SMOG Test
     */
    public double smogTest(int polysyllables) {
        double temp = polysyllables * 30.0 / sentencesInText.length;
        return 1.043 * Math.sqrt(temp) + 3.1291;
    }

    /*
    Coleman-Liau Index:
     */
    public double coleManIndex() {
        double L = ((double) charactersInText.length / (double) wordsInText.length) * 100;
        double S = ((double) sentencesInText.length / (double) wordsInText.length) * 100;
        return 0.0588 * L - 0.296 * S - 15.8;
    }

    //========================================================================================

    /**
     * Returns an age based on the table here https://en.wikipedia.org/wiki/Automated_readability_index
     * @param index - index from either of the 4 readability scores
     * @return - age based on the readability score
     */
    public int whoCanRead(int index) {
        final int[] ages = {6, 7, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 24, 25};
        int age = 0;
        age = ages[index - 1];
        return age;
    }


    /*
    Reads a file as a single string
     */
    public String readFile(String fileName) throws IOException {
        return new String(Files.readAllBytes(Paths.get(fileName)));
    }

    /**
     * Counts the number of syllables or polysyllables
     * @param words - array of words in the given text
     * @param polyOrSyl - whether 'S' - syllables or 'P' - polysyllables should be counted
     * @return - number of syllables or polysyllables
     * */
    public static int sylCounter(String[] words, String polyOrSyl) {
        int counter = 0;
        String vowels = "[aeiouy]";

        for (String aWord : words) {
            int vowelCounter = 0;
            String tempLook = aWord;
            String[] charactersInWord = new String[aWord.length()];
            if (aWord.matches(".+e[?!,.]")) { //if ends with e and followed by . or ,
                aWord = aWord.substring(0, aWord.length() - 2);
            } else if (aWord.matches(".+e")) {  //if ends with e
                aWord = aWord.substring(0, aWord.length() - 1);
            }

            charactersInWord = aWord.split("");

            for (int i = 0; i < charactersInWord.length; i++) {
                if (charactersInWord[i].toLowerCase().matches(vowels)) { //if the current character is a vowel
                    if (i == charactersInWord.length - 1) { //if i is the last character
                        vowelCounter++;
                    } else { //check if the next character is a vowel
                        if (!charactersInWord[i + 1].toLowerCase().matches(vowels)) {
                            vowelCounter++;
                        }
                    }
                }
            }

            switch (polyOrSyl) {
                case "S":
                    if (vowelCounter > 0) {
                        counter += vowelCounter;
                    } else {
                        counter++; //word contains 0 vowels, means 1-syllable
                    }
                    break;

                case "P":
                    if (vowelCounter > 2) {
                        counter++;
                    }
                    break;
            }
        }
        return counter;
    }
}
