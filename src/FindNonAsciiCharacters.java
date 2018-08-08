import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class FindNonAsciiCharacters {

    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
	        FindNonAsciiCharacters finder = new FindNonAsciiCharacters();
	        finder.find(args);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void find(String[] fileNames) throws IOException {
        for (String fileName : fileNames) {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String line;
            int lineCount = 0;
            while ((line = reader.readLine()) != null) {
                lineCount++;
                char[] bytes = line.toCharArray();
                int columnCount = 0;
                for (char c : bytes) {
                    columnCount++;
                    if (c > 127) {
                        System.out.printf("%s:%d character='%c' code=%d%n", fileName, lineCount, c, (int) c);
                    }
                }
            }
        }
    }

}
