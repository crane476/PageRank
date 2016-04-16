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
import java.io.PrintStream;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
     * @throws java.io.FileNotFoundException
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
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
        System.setOut(new PrintStream(new File(outDirectory + "\\" + "output.txt")));
        double[][] pMatrix = generateProbabilityMatrix(inDirectory);
        double[] pageRanks = calculatePageRank(pMatrix, alpha, numIterations);
        output(inDirectory, pageRanks);

    }

        /*
     * Function Name: output()
     * Arguments: String, double[]
     * Returns: N/A
     * Description: This method outputs the page ranks for each document sorted
     * in descending order.
     */
    
    public static void output(String inDirectory, double[] pageRanks) {
        File[] pages = new File(inDirectory).listFiles();
        HashMap<File, Double> pRanks = new HashMap<>();
        for (int i = 0; i < pages.length; i++) {
            pRanks.put(pages[i], pageRanks[i]);
        }
        Map<File, Double> sortedMap = sortByValue(pRanks);
        for (File key : sortedMap.keySet()) {
            System.out.println(key.getName() + ": " + sortedMap.get(key));
        }
    }

        /*
     * Function Name: calculatePageRank()
     * Arguments: double[][], double, int
     * Returns: double[]
     * Description: This method uses the link probability matrix to calculate 
     * the page rank for each document in the collection.
     */
    
    public static double[] calculatePageRank(double[][] pMatrix, double alpha, int numIterations) {
        double[] pageRanks = new double[pMatrix.length]; //array to hold page ranks for each document at k iterations
        Arrays.fill(pageRanks, 1.0 / pMatrix.length); //initialize pageRanks to 1/N
        for (int k = 0; k < numIterations; k++) {
            for (int j = 0; j < pageRanks.length; j++) {
                double currentPageRank = pageRanks[j];
                double sum = 0.0; //summation of probability matrix
                for (int i = 0; i < pageRanks.length; i++) {
                    sum += pMatrix[i][j];
                }
                //calculate page rank using alpha, sum of probability matrix, and page rank at previous iteration
                pageRanks[j] = (alpha / pMatrix.length) + (1.0 - alpha) * (sum * currentPageRank);
            }
        }
        return pageRanks; //return page ranks of all documents at iteration k specified by the user
    }

        /*
     * Function Name: generateProbabilityMatrix()
     * Arguments: String
     * Returns: double[][]
     * Description: This method iterates through each file and uses their outgoing
     * links to calculate the link probability matrix and then output the results.
     */
    
    public static double[][] generateProbabilityMatrix(String inDirectory) throws IOException {
        File[] pages = new File(inDirectory).listFiles(); //list of all files
        double[][] probabilityMatrix = new double[pages.length][pages.length];
        for (int i = 0; i < pages.length; i++) {
            for (int j = 0; j < pages.length; j++) {
                probabilityMatrix[i][j] = 0.0;
                ArrayList<String> links = getLinks(pages[i]);
                if (links.contains(pages[j].getName())) {
                    double size = links.size();
                    probabilityMatrix[i][j] = 1.0 / size;
                }
            }
        }
        System.out.print("\t\t");
        for (int k = 0; k < pages.length; k++) {
            System.out.print(pages[k].getName() + "\t");
        }
        System.out.println();
        DecimalFormat df = new DecimalFormat("#.####");
        df.setRoundingMode(RoundingMode.UP);
        for (int i = 0; i < pages.length; i++) {
            System.out.print(pages[i].getName() + ":\t");
            for (int j = 0; j < pages.length; j++) {
                System.out.print(df.format(probabilityMatrix[i][j]) + "\t\t");
            }
            System.out.println();
        }
        System.out.println();

        return probabilityMatrix;
    }

    /*
     * Function Name: getLinks()
     * Arguments: File
     * Returns: ArrayList<String>
     * Description: This method reads through a file and extracts all outgoing
     * links to other files.
     */
    
    public static ArrayList<String> getLinks(File file) throws FileNotFoundException, IOException {
        ArrayList<String> linkList = new ArrayList<>();
        BufferedReader in = new BufferedReader(new FileReader(file));
        String s = null;
        StringBuilder sb = new StringBuilder();
        while ((s = in.readLine()) != null) {
            sb.append(s);
        }
        in.close();
        String fileText = sb.toString();
        Pattern pattern = Pattern.compile("\\d{4}");
        Matcher matcher = pattern.matcher(fileText);
        while (matcher.find()) {
            for (int i = matcher.start() - 1; i > 2; i--) {
                String stringToCheck = sb.substring(i - 3, i + 1);
                if (stringToCheck.equals("COSC") || stringToCheck.equals("MATH") || stringToCheck.equals("MTED")) {
                    String link = stringToCheck + " " + fileText.substring(matcher.start(), matcher.end()) + ".txt";
                    if (!linkList.contains(link) && !link.equals(file.getName())) {
                        linkList.add(link);
                    }
                    break;
                }
            }
        }
        return linkList;
    }

    /*
     * Function Name: sortByValue()
     * Arguments: Map<K, V>
     * Returns: Map<K, V>
     * Description: This method takes a hashmap and sorts it by value in
     * descending order.
     */
    
    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new LinkedList<>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> e1, Map.Entry<K, V> e2) {
                return (e2.getValue()).compareTo(e1.getValue());
            }
        });

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }
}
