package com.github.vgaj.plainlanguageencoder;

import java.io.*;

public class TestApplication
{
    // TODO: Separate this out to it's own project
    public static void main(String[] args) throws IOException {
        boolean isEncode = (args.length > 0 && args[0].equals("e"));
        boolean isDecode = (args.length > 0 && args[0].equals("d"));
        String inputFile = args[1];
        String outputFile = args[2];
        if (args.length == 3 && (isEncode || isDecode)) {
            if (isEncode) {
                try (InputStream is = new BufferedInputStream( new FileInputStream( inputFile));
                     OutputStream os = new LanguageEncodedOutputStream( new BufferedOutputStream( new FileOutputStream( outputFile)))) {
                    int inputData;
                    while ((inputData = is.read()) != -1) {
                        os.write(inputData);
                    }
                }
            } else if (isDecode) {
                try (InputStream is = new LanguageEncodedInputStream( new BufferedInputStream( new FileInputStream( inputFile)));
                     OutputStream os = new BufferedOutputStream( new FileOutputStream( outputFile))) {
                    int inputData;
                    while ((inputData = is.read()) != -1) {
                        os.write(inputData);
                    }
                }
            }

        } else {
            System.out.println("Usage: java -jar xxx.jar [e|d] [Input File] [Output File]");
        }
    }
}
