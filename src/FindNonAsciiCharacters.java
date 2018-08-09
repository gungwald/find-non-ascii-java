import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Arrays;


public class FindNonAsciiCharacters {
    
    protected Charset UTF8 = Charset.forName("UTF-8");

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

    protected void find(String[] fileNames) throws IOException {
        for (String fileName : fileNames) {
            Reader reader = null;
            try {
                reader = new InputStreamReader(new FileInputStream(fileName), UTF8);
                find(reader, fileName);
            }
            finally {
                close(reader);
            }
        }
    }

    protected void find(Reader reader, String fileName) throws IOException {
        int lineNum = 0;
        int columnNum = 0;
        int c;
        while ((c = reader.read()) != -1) {
            columnNum++;
            if (c == '\n') {
                columnNum = 0;
                lineNum++;
            }
            if (c > 127) {
                byte[] utf8bytes = String.valueOf(c).getBytes(UTF8);
                Character.isValidCodePoint(c);
                if (c > Character.MAX_VALUE) {
                    throw new IOException("The character value read is too big to store in a char: " + c);
                }
                // Now it is safe to cast c to char because of the above check.
                // Check for surrogate.
                System.out.printf("%s:%d:%d character='%c' code=%d bytes=%s%n", fileName, lineNum, columnNum, c, (int) c, Arrays.toString(utf8bytes));
            }
        }
    }

    protected byte[] decodeUtf8(char c) {
        String s = String.valueOf(c);
        byte[] utf8Bytes = s.getBytes(UTF8);
        return utf8Bytes;
    }
    protected void close(Reader r) {
        if (r != null) {
            try {
                r.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
