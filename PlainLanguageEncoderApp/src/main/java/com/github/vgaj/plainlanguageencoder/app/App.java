package com.github.vgaj.plainlanguageencoder.app;

import java.io.*;
import com.github.vgaj.plainlanguageencoder.*;
import org.apache.logging.log4j.*;

public class App
{
    private static final Logger logger = LogManager.getLogger(LanguageEncodedOutputStream.class);

    public static void main(String[] args) throws IOException {
        boolean isEncode = (args.length > 0 && args[0].equals("e"));
        boolean isDecode = (args.length > 0 && args[0].equals("d"));
        if (args.length == 3 && (isEncode || isDecode)) {
            if (isEncode) {
                try (InputStream is = new BufferedInputStream( new FileInputStream( args[1]));
                     OutputStream os = new LanguageEncodedOutputStream( new BufferedOutputStream( new FileOutputStream( args[2])))) {
                    logger.info("Encoding {} to {}", args[1], args[2]);
                    int inputData;
                    while ((inputData = is.read()) != -1) {
                        os.write(inputData);
                    }
                }
            } else if (isDecode) {
                try (InputStream is = new LanguageEncodedInputStream( new BufferedInputStream( new FileInputStream( args[1])));
                     OutputStream os = new BufferedOutputStream( new FileOutputStream( args[2]))) {
                    logger.info("Decoding {} to {}", args[2], args[1]);
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
