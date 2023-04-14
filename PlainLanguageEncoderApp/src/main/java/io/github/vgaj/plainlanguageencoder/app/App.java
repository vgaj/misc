package io.github.vgaj.plainlanguageencoder.app;

import java.io.*;
import java.time.Duration;
import java.time.Instant;

import io.github.vgaj.plainlanguageencoder.*;
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
                    Instant start = Instant.now();
                    int inputData;
                    while ((inputData = is.read()) != -1) {
                        os.write(inputData);
                    }
                    logger.info("Encoding completed in {} seconds", Duration.between(start,Instant.now()).getSeconds());
                }
            } else if (isDecode) {
                try (InputStream is = new LanguageEncodedInputStream( new BufferedInputStream( new FileInputStream( args[1])));
                     OutputStream os = new BufferedOutputStream( new FileOutputStream( args[2]))) {
                    logger.info("Decoding {} to {}", args[1], args[2]);
                    Instant start = Instant.now();
                    int inputData;
                    while ((inputData = is.read()) != -1) {
                        os.write(inputData);
                    }
                    logger.info("Decoding completed in {} seconds", Duration.between(start,Instant.now()).getSeconds());
                }
            }

        } else {
            System.out.println("Usage: java -jar xxx.jar [e|d] <input file> <output file>");
        }
    }
}
