package com.alteredmechanism.apps.find_non_ascii;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * find-special-chars.jar
 *
 * @author bill
 */
public class FindNonAsciiCharacters {

    public static final int MAX_ASCII = 127;

    private Charset charset = Charset.defaultCharset();
    private boolean searchedForCharacterGetNameMethod = false;
    private Method characterGetNameMethod = null;

    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            FindNonAsciiCharacters finder = new FindNonAsciiCharacters();
            List<String> fileNames = finder.processSwitches(args);
            finder.find(fileNames);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<String> processSwitches(String[] args) throws IOException {
        List<String> fileNames = new ArrayList<String>();
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.equals("-l")) {
                listCharsets();
                return fileNames;
            }
            else if (arg.equals("-c")) {
                charset = Charset.forName(args[++i]);
                //System.setOut(new PrintStream(System.out, true, charset.name()));
            }
            else if (arg.equals("-h") || arg.equals("-help") || arg.equals("--help") || arg.equals("/?")) {
                usage();
                return fileNames;
            }
            else if (arg.equals("-i")) {
                int nextArgIndex = i + 1;
                if (nextArgIndex < args.length) {
                    String nextArg = args[nextArgIndex];
                    Installer installer = new Installer();
                    installer.install(new File(nextArg));
                    System.exit(0);
                }
                else {
                    System.err.println("The -i install switch requires a parameter that specifies ");
                    System.err.println("the location of the install files.");
                    System.exit(1);
                }
            }
            else {
                fileNames.add(arg);
            }
        }
        if (! charset.name().equals("UTF-8")) {
            System.out.printf("WARNING: Expecting the input to be encoded as %s.%n", charset.name());
            System.out.printf("WARNING: If this does not match the actual character set of the input,%n");
            System.out.printf("WARNING: re-run with the -c switch to specify the correct character set.%n");
        }
        return fileNames;
    }

    private void usage() {
        System.out.println("find-non-ascii -h | -l | [ -c Charset ] [ file1 file2 ... ] ");
        System.out.println("  -c Charset Assume the input is encoded in the given Charset");
        System.out.println("  -h         Show help");
        System.out.println("  -l         List the names of all Charsets that can be specified by -c");
        System.out.println("  files      A list of files to examine. Use a dash to read stdin");
    }

    private void listCharsets() {
        for (String name : Charset.availableCharsets().keySet()) {
            System.out.println(name);
        }
    }

    protected void find(List<String> fileNames) throws IOException, UnsupportedEncodingException,
    IllegalAccessException, InvocationTargetException {
        if (fileNames.size() == 0) {
            usage();
            return;
        }
        else {
            for (String fileName : fileNames) {
                if (fileName.equals("-")) {
                    find(System.in, fileName);
                }
                else {
                    find(new FileInputStream(fileName), fileName);
                }
            }
        }
    }

    protected void find(InputStream in, String fileName) throws IOException,
    UnsupportedEncodingException, IllegalAccessException, InvocationTargetException {
        InputStreamReader reader = new InputStreamReader(in, charset);
        find(reader, fileName);
    }

    protected void find(Reader reader, String fileName) throws IOException,
    UnsupportedEncodingException, IllegalAccessException, InvocationTargetException {
        System.out.print(" LINE  COLUMN  CHAR\tCODEPOINT  BYTES");
        if (characterGetNameMethodExists()) {
            System.out.println("                NAME");
        }
        else {
            System.out.println();
        }
        BufferedReader lineReader = null;
        try {
            lineReader = new BufferedReader(reader);
            int lineNumber = 0;
            String line;
            while ((line = lineReader.readLine()) != null) {
                lineNumber++;
                char[] lineAsCharArray = line.toCharArray();
                int physicalCharCountForLogicalChar = 0;
                for (int i = 0; i < lineAsCharArray.length; i += physicalCharCountForLogicalChar) {
                    // This handles surrogate pairs automatically which is why it is being used.
                    int logicalCharCodePoint = Character.codePointAt(lineAsCharArray, i);
                    physicalCharCountForLogicalChar = Character.charCount(logicalCharCodePoint);
                    if (! isASCII(logicalCharCodePoint)) {
                        reportCharacter(logicalCharCodePoint, lineNumber, i, line, fileName);
                    }
                }
            }
        }
        finally {
            if (! fileName.equals("-")) {
                close(lineReader);
            }
        }
    }

    public boolean isASCII(int codePoint) {
        return codePoint <= MAX_ASCII;
    }

    protected void reportCharacter(int codePoint, int lineNumber,
                                   int columnNumber, String line, String fileName) throws UnsupportedEncodingException,
    IllegalAccessException, InvocationTargetException {

        // UTF-16 representation including surrogates
        char[] physicalChars = Character.toChars(codePoint);
        // One logical char for possibly multiple physical chars
        String logicalChar = String.valueOf(physicalChars);
        // An explanation of what it is
        String logicalCharUnicodeName = getUnicodeName(codePoint);
        // The byte representation for the requested charset.
        byte[] physicalBytes = logicalChar.getBytes(charset.name());

        String codePointString = makePrintable(codePoint);
        //String physicalCharsString = makePrintable(physicalChars);
        String physicalBytesString = makePrintable(physicalBytes);

        // %s with a field width is right-justified by default. A minus
        // left-justified. If a field width is not specified it appears
        // left-justified but since there's no width, it's really just
        // printed left to right.
        //
        // The max number of bytes in a UTF-8 char is 4 hence the %-19s.
        //
        // The position of the first 3 fields and the tab are critical.
        // The problem is that the logicalChar can take up 0 to 2 cells
        // on the console/terminal screen. There is no way to know
        // in the code how long it is going to be as it is always just
        // 1 character regardless of how the output looks. I believe it
        // depends on the font that is being used for the console/terminal.
        // For a terminal app, there is no way to get the font. So, to
        // compensate, a tab is used to provide a variable width space
        // between fields 3 and 4. But that only works if for spaces less
        // than 8 characters because a tab is 8 or less characters.
        System.out.printf("%5d  %5d    %s\t%8s   %-19s", lineNumber,
        columnNumber, logicalChar, codePointString,
        physicalBytesString);
        if (logicalCharUnicodeName == null) {
            System.out.println();
        }
        else {
            System.out.printf("  %s%n", logicalCharUnicodeName);
        }
    }

    /**
     * Only way to get formatting right.
     */
    public String makePrintable(int codePoint) {
        StringBuilder s = new StringBuilder();
        s.append("U+");
        s.append(String.format("%04X", codePoint));
        return s.toString();
    }

    /**
     * Arrays.toString makes the individual values negative in some versions
     * of Java. So, this is done instead.
     */
    public String makePrintable(byte[] bytes) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            s.append("0x");
            // Append the byte value as a 0-padded hex number with 2 digits.
            s.append(String.format("%02X", bytes[i]));
            s.append(',');
        }
        s.deleteCharAt(s.length() - 1);
        return s.toString();
    }

    /**
     * Arrays.toString makes string with the actual character glyph instead of
     * a numeric value. So, this is done instead.
     */
    public String makePrintable(char[] chars) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < chars.length; i++) {
            // Append the byte value as a 0-padded hex number with 4 digits.
            s.append("0x");
            s.append(String.format("%04X", (int) chars[i]));
            s.append(',');
        }
        s.deleteCharAt(s.length() - 1);
        return s.toString();
    }

    public boolean characterGetNameMethodExists() {
        return getCharacterGetNameMethod() != null;
    }

    public Method getCharacterGetNameMethod() {
        if (! searchedForCharacterGetNameMethod) {
            try {
                characterGetNameMethod = Character.class.getMethod("getName", int.class);
            }
            catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            searchedForCharacterGetNameMethod = true;
        }
        return characterGetNameMethod;
    }

    public String getUnicodeName(int codePoint) throws IllegalAccessException,
    InvocationTargetException {
        String name = null;
        Method method = getCharacterGetNameMethod();
        if (method != null) {
            // The null parameter to invoke is for a static method.
            name = (String) method.invoke(null, Integer.valueOf(codePoint));
        }
        return name;
    }

    public void close(Reader r) {
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
