import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class PlagiarismDetector {

    public static int[] computeLPSArray(String pattern) {
        int[] lps = new int[pattern.length()];
        int j = 0;

        for (int i = 1; i < pattern.length(); ) {
            if (pattern.charAt(i) == pattern.charAt(j)) {
                lps[i] = j + 1;
                i++;
                j++;
            } else {
                if (j != 0) {
                    j = lps[j - 1];
                } else {
                    lps[i] = 0;
                    i++;
                }

            }
        }
        return lps;
    }

    public static int kmp(String text, String pattern) {
        int[] lps = computeLPSArray(pattern);
        int i = 0, j = 0;

        while (i < text.length() && j < pattern.length()) {
            if (text.charAt(i) == pattern.charAt(j)) {
                i++;
                j++;
            } else {
                if (j != 0) {
                    j = lps[j - 1];
                } else {
                    i++;
                }
            }
        }

        if (j == pattern.length()) {
            return i - j;
        }

        return -1;
    }

    public static class TextFileCreator {
        public static void createTextFiles() {
            try {
                // Creating the first text file
                String file1Content = "This is the content of the first text file.";
                createTextFile("file1.txt", file1Content);

                // Creating the second text file
                String file2Content = "This is the content of the second text file.";
                createTextFile("file2.txt", file2Content);

                // Creating the third text file
                String file3Content = "This is the content of the third text file.";
                createTextFile("file3.txt", file3Content);

                System.out.println("Text files created successfully.");
            } catch (IOException e) {
                System.out.println("An error occurred while creating the text files: " + e.getMessage());
            }
        }

        public static void createTextFile(String fileName, String fileContent) throws IOException {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
            writer.write(fileContent);
            writer.close();
        }
    }

    public static void main(String[] args) {
        TextFileCreator.createTextFiles();

        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to the Plagiarism Detector!");

        System.out.println("Please choose an option:");
        System.out.println("1. Folder Comparison");
        System.out.println("2. File Comparison");
        int choice = scanner.nextInt();

        if (choice == 1) {
            System.out.println("Enter the path to the folder:");
            String folderPath = scanner.next();
            System.out.println("Enter the path to the master file:");
            String masterFilePath = scanner.next();

            try {
                BufferedReader masterFileReader = new BufferedReader(new FileReader(masterFilePath));
                StringBuilder masterFileContent = new StringBuilder();
                String line;
                while ((line = masterFileReader.readLine()) != null) {
                    masterFileContent.append(line);
                }

                masterFileReader.close();
                String masterFileText = masterFileContent.toString().replaceAll("\\s+", "");

                String[] files = new File(folderPath).list();

                if (files != null) {
                    for (String file : files) {
                        String filePath = folderPath + File.separator + file;
                        BufferedReader currentFileReader = new BufferedReader(new FileReader(filePath));
                        StringBuilder currentFileContent = new StringBuilder();

                        while ((line = currentFileReader.readLine()) != null) {
                            currentFileContent.append(line);
                        }

                        currentFileReader.close();
                        String currentFileText = currentFileContent.toString().replaceAll("\\s+", "");

                        int index = kmp(masterFileText, currentFileText);

                        if (index != -1) {
                            double similarity = 100 - ((double) (masterFileText.length() - index) / masterFileText.length()) * 100;

                            System.out.println("Similarity between " + filePath + " and " + masterFilePath + ": " + similarity + "%");

                            // Create a result file for each comparison
                            String resultFilePath = filePath + "_result.txt";
                            try (BufferedWriter writer = new BufferedWriter(new FileWriter(resultFilePath))) {
                                writer.write("Similarity between " + filePath + " and " + masterFilePath + ": " + similarity + "%");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } else {
                    System.out.println("No files found in the specified folder.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (choice == 2) {
            System.out.println("Enter the path to the first file:");
            String filePath1 = scanner.next();
            System.out.println("Enter the path to the second file:");
            String filePath2 = scanner.next();

            try {
                BufferedReader fileReader1 = new BufferedReader(new FileReader(filePath1));
                StringBuilder fileContent1 = new StringBuilder();
                String line;

                while ((line = fileReader1.readLine()) != null) {
                    fileContent1.append(line);
                }

                fileReader1.close();

                BufferedReader fileReader2 = new BufferedReader(new FileReader(filePath2));
                StringBuilder fileContent2 = new StringBuilder();

                while ((line = fileReader2.readLine()) != null) {
                    fileContent2.append(line);
                }

                fileReader2.close();

                String fileText1 = fileContent1.toString().replaceAll("\\s+", "");
                String fileText2 = fileContent2.toString().replaceAll("\\s+", "");

                int index = kmp(fileText1, fileText2);

                if (index != -1) {
                    double similarity = 100 - ((double) (fileText1.length() - index) / fileText1.length()) * 100;

                    System.out.println("Similarity between " + filePath1 + " and " + filePath2 + ": " + similarity + "%");

                    // Create a result file for the comparison
                    String resultFilePath = filePath1 + "_" + filePath2 + "_result.txt";
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(resultFilePath))) {
                        writer.write("Similarity between " + filePath1 + " and " + filePath2 + ": " + similarity + "%");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("The two files are not similar.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Invalid choice. Please select either 1 or 2.");
        }
    }
}
