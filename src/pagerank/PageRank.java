/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pagerank;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Crane476
 */
public class PageRank {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        // TODO code application logic here
        Scanner input = new Scanner(System.in);
        System.out.print("Input File Directory: ");
        String inDirectory = input.nextLine();
        System.out.print("Output Directory: ");
        String outDirectory = input.nextLine();
        System.out.print("Teleport Probability: ");
        double alpha = input.nextDouble();
        System.out.print("Number of Interations: ");
        int numIterations = input.nextInt();
        double[][] pMatrix = generateProbabilityMatrix(inDirectory);
        for (int i = 0; i < pMatrix.length; i++) {
            for (int j = 0; j < pMatrix.length; j++) {
                System.out.print(pMatrix[i][j] + "\t");
            }
        }

    }

    public static double[] calculatePageRank(double[][] pMatrix, double alpha, int numIterations) {
        double[] pageRanks = new double[pMatrix.length]; //array to hold page ranks for each document at k iterations
        Arrays.fill(pageRanks, 1.0 / pMatrix.length); //initialize pageRanks to 1/N
        for (int k = 0; k < numIterations; k++) {
            for (int j = 0; j < pageRanks.length; j++) {
                double currentPageRank = pageRanks[j];
                int sum = 0; //summation of probability matrix
                for (int i = 0; i < pageRanks.length; i++) {
                    sum += pMatrix[i][j];
                }
                //calculate page rank using alpha, sum of probability matrix, and page rank at previous iteration
                pageRanks[j] = (alpha * 1.0 / pMatrix.length) + (1 - alpha) * (sum * currentPageRank);
            }
        }
        return pageRanks; //return page ranks of all documents at iteration k specified by the user
    }

    public static double[][] generateProbabilityMatrix(String inDirectory) throws IOException {
        File[] pages = new File(inDirectory).listFiles();
        double[][] probabilityMatrix = new double[pages.length][pages.length];
        for (int i = 0; i < pages.length; i++) {
            for (int j = 0; j < pages.length; j++) {
                probabilityMatrix[i][j] = 0;
                ArrayList<String> links = getLinks(pages[i]);
                if (links.contains(pages[j].getName())) {
                    probabilityMatrix[i][j] = 1 / links.size();
                }
            }
        }
        return probabilityMatrix;
    }

    //iterate through file. For each 4 digit number, iterate backwards until you encounter COSC|MATH|MTED
//    public static ArrayList<String> getLinks(File file) throws FileNotFoundException, IOException {
//        ArrayList<String> linkList = new ArrayList<>(); //list containing all outgoing links in document
//        BufferedReader in = new BufferedReader(new FileReader(file));
//        StringBuilder sb = new StringBuilder();
//        String s = null;
//        while ((s = in.readLine()) != null) {
//            sb.append(s);
//        }
//        String fileText = sb.toString();
//        String pattern1 = "(((COSC|MATH|MTED) \\d+))";
//        String pattern2 = "(((COSC|MATH|MTED) \\d+\\/\\d+))";
//        String pattern3 = "(((COSC|MATH|MTED) \\d+ - \\d+))";
//        String pattern4 = "(((COSC|MATH|MTED) \\d+-\\d+))";
//        String pattern5 = "((?<!COSC|MATH|MTED) \\d{4})";
//        ArrayList<String> patterns = new ArrayList<>();
//        patterns.add(pattern1);
//        patterns.add(pattern2);
//        patterns.add(pattern3);
//        patterns.add(pattern4);
//        patterns.add(pattern5);
//        for (int i = 0; i < patterns.size(); i++) {
//            Pattern pattern = Pattern.compile(patterns.get(i));
//            Matcher matcher = pattern.matcher(fileText);
//            while (matcher.find()) {
//                String link = fileText.substring(matcher.start(), matcher.end());
//                if (!linkList.contains(link)) {
//                    linkList.add(link);
//                }
//            }
//        }
//        return linkList;
//    }
    public static ArrayList<String> getLinks(File file) throws FileNotFoundException, IOException {
        ArrayList<String> linkList = new ArrayList<>();
        BufferedReader in = new BufferedReader(new FileReader(file));
        String s = null;
        StringBuilder sb = new StringBuilder();
        while ((s = in.readLine()) != null) {
            sb.append(s);
        }
        String fileText = sb.toString();
        Pattern pattern = Pattern.compile("\\d{4}");
        Matcher matcher = pattern.matcher(fileText);
        while (matcher.find()) {
            for (int i = matcher.start() - 1; i > 2; i--) {
                String stringToCheck = sb.substring(i - 3, i + 1);
                if (stringToCheck.equals("COSC") || stringToCheck.equals("MATH") || stringToCheck.equals("MTED")) {
                    String link = stringToCheck + " " + fileText.substring(matcher.start(), matcher.end()) + ".txt";
                    if (!linkList.contains(link)) {
                        linkList.add(link);
                    }
                    break;
                }
            }
        }
        return linkList;
    }
}
