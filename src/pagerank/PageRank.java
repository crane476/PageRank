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
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 *
 * @author Crane476
 */
public class PageRank {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
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

    }

    public static double[][] calculateProbabilityMatrix(String inDirectory) {
        File[] pages = new File(inDirectory).listFiles();
        double[][] probabilityMatrix = new double[pages.length][pages.length];

        return probabilityMatrix;
    }

    public static ArrayList<String> getLinks(File file) throws FileNotFoundException, IOException {
        ArrayList<String> linkList = new ArrayList<>(); //list containing all outgoing links in document
        BufferedReader in = new BufferedReader(new FileReader(file));
        StringBuilder sb = new StringBuilder();
        String s = null;
        while ((s = in.readLine()) != null) {
            sb.append(s);
        }
        String[] terms = sb.toString().split("\\W+"); //array containing each term in the document
        String courseName = file.getName();
        for (int i = 0; i < terms.length; i++) {
            String term = terms[i];
            if (term.equals("COSC") || term.equals("MATH") || term.equals("MTED")) {
                String courseID = terms[i + 1].replace(":].", "");
                if (courseID.contains("/")) { //COSC 1136/1336
                    String[] IDs = courseID.split("/");
                    for (int j = 0; j < IDs.length; j++) {
                        if (isNumeric(IDs[j])) {
                            String link = term + " " + IDs[j] + ".txt";
                            if (!link.equals(courseName)) {
                                linkList.add(link);
                            }
                        }
                    }
                } else if (courseID.contains("=")) { //COSC 1137=TCCN: COSC 1425
                    String[] IDs = courseID.split("=");
                    for (int j = 0; j < IDs.length; j++) {
                        if (isNumeric(IDs[j])) {
                            String link = term + " " + IDs[j] + ".txt";
                            if (!link.equals(courseName)) {
                                linkList.add(link);
                            }
                        }
                    }
                } else if(courseID.charAt(courseID.length() - 1) == ','){ //COSC 1301, 1325, 1337, ...
                    int index = i+2;
                    String ID = terms[index].replace(",", "");
                    while(isNumeric(ID)){
                        String link = term + " " + ID + ".txt";
                        linkList.add(link);
                        index++;
                    }
                    courseID = courseID.replace(",", "");
                    String link = term + " " + courseID + ".txt";
                    linkList.add(link);
                } 
                
                else { //COSC 4385
                    if (isNumeric(courseID)) {
                        String link = term + " " + courseID + ".txt";
                        if (!link.equals(courseName)) {
                            linkList.add(link);
                        }
                    }
                }
            } 
        }
        return linkList;
    }

    public static boolean isNumeric(String term) {
        char[] chars = term.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (!Character.isDigit(chars[i])) {
                return false;
            }
        }
        return true;
    }

}
