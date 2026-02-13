package de.cosyfeat;

import de.cosyfeat.tool.*;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.*;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Main {

    public static String inputFilepath;

    public static void main(String[] args) {

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-i":
                case "--input":
                    i++;
                    inputFilepath = args[i];
                    break;
                case "-h":
                case "--help":
                    printhelp();
                    break;
                default:
                    printhelp();
            }
        }

        if (inputFilepath == null) {
            System.out.println("Please specify input file! Enter -h for help.");
            return;
        }

        System.out.println("Start reading in the feature models from the file:   " + inputFilepath);

        final long timeStart = System.currentTimeMillis();

        FileReader file;
        StringBuilder sb = new StringBuilder();
        try {
            file = new FileReader(inputFilepath);
            BufferedReader reader = new BufferedReader(file);
            String currentLine;
            while ((currentLine = reader.readLine()) != null) {
                sb.append(currentLine).append("\n");
            }
        } catch (IOException e) {
            System.out.println("The path of the XML does not exist!");
            e.printStackTrace();
        }


        String stringInput = sb.toString();

        CharStream input = CharStreams.fromString(stringInput);

        System.out.println("Feature models are being translated ...");
        CoSyFeATLexer lexer = new CoSyFeATLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        CoSyFeATParser parser = new CoSyFeATParser(tokens);
        parser.setBuildParseTree(true);
        ParseTree tree = parser.fmodel();

        ParseTreeWalker walker = new ParseTreeWalker();
        Translator translator = new Translator();
        walker.walk(translator, tree);

        System.out.println("\nCreating instances of the feature models ...");
        for (Featuremodel featuremodel : translator.storage.featuremodels) {
            if (featuremodel.type.equals("type")) {
                for (int i = 0; i < featuremodel.numberOfInstances; i++) {
                    int instanceid = (i + 1);
                    featuremodel.instances.add(new Instance(instanceid, featuremodel.systemroot.clone()));
                }
            }
        }

        System.out.println("Modelchecker is starting ...");
        ModelChecker modelChecker = new ModelChecker();

        for (Featuremodel featuremodel : translator.storage.featuremodels) {
            if (featuremodel.type.equals("type")) {
                modelChecker.runInstances(featuremodel.instances);
            } else if (featuremodel.type.equals("group")) {
                modelChecker.runGroupmodel(featuremodel);
            }

        }
        for (Constraint constraint : translator.storage.constraints) {
            modelChecker.runConstraints(constraint, translator.storage.featuremodels);
        }

        modelChecker.printResult();

        final long timeEnd = System.currentTimeMillis();
        System.out.println("Models and constraints have been successfully verified in " + (timeEnd - timeStart) + " ms!");
    }

    public static void printhelp() {
        System.out.println("Please enter the file to be checked as follows:  \n --input, -i <filename> \t Path to the XML file \n");
        System.out.println("--help, -h  \t\t\t Help");
    }
}

