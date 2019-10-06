package util;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Utilities {
    public static String loadResource(String fileName) throws Exception {
        String result;
        try (InputStream in = Class.forName(Utilities.class.getName()).getResourceAsStream(fileName);
             Scanner scanner = new Scanner(in, StandardCharsets.UTF_8)) {
            result = scanner.useDelimiter("\\A").next();
        }
        return result;
    }
}
