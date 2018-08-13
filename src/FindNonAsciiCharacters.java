import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * find-special-chars.jar
 * 
 * @author bill
 */
public class FindNonAsciiCharacters {

    private Charset charset = Charset.defaultCharset();
    private PrintWriter out;

    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            FindNonAsciiCharacters finder = new FindNonAsciiCharacters();
            finder.findCommandLineArguments(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void findCommandLineArguments(String[] args) throws IOException {
		List<String> fileNames = new ArrayList<String>();
		for (int i = 0; i < args.length; i++) {
			String arg = args[i];
			if (arg.equals("-l")) {
				listCharsets();
				return;
			}
			else if (arg.equals("-c")) {
				charset = Charset.forName(args[++i]);
				//System.setOut(new PrintStream(System.out, true, charset.name()));
			}
			else if (arg.equals("-h") || arg.equals("-help") || arg.equals("--help") || arg.equals("/?")) {
				usage();
				return;
			}
			else {
				fileNames.add(arg);
			}
		}
		find(fileNames);
	}

	private void usage() {
		System.out.println("find-non-ascii -h | -l | [ -c Charset ] [ file1 file2 ... ] ");
		System.out.println("\t-h	Show help");
		System.out.println("\t-l	List all character set names");
		System.out.println("\tfiles	A list of files to examine, can be empty to read stdin");
	}

	private void listCharsets() {
    	for (String name : Charset.availableCharsets().keySet()) {
    		System.out.println(name);
    	}
	}

	protected void find(List<String> fileNames) throws IOException {
		if (fileNames.size() == 0) {
			try (InputStreamReader reader = new InputStreamReader(System.in, charset)) {
				find(reader, "stdin");
			}
		}
		else {
	        for (String fileName : fileNames) {
	        	File f = new File(fileName);
	            try (InputStreamReader reader = new InputStreamReader(new FileInputStream(f), charset)) {
	                find(reader, f.getName());
	            }
	        }
		}
    }

	protected void printf(String format, Object ...stuff) {
		if (out == null) {
			if (charset == Charset.defaultCharset()) {
				out = new PrintWriter(System.out);
			}
			else {
				out = new PrintWriter(new OutputStreamWriter(System.out, charset));
			}
		}
		out.printf(format, stuff);
	}
	
    protected void find(InputStreamReader reader, String fileName) throws IOException {
    	if (! reader.getEncoding().equalsIgnoreCase("UTF8")) {
    		System.out.printf("WARNING: Input character set is %s, not UTF-8. Use the command line arguments '-c UTF-8' to set it to UTF-8.%n", reader.getEncoding());
    	}
        int lineNum = 0;
        int columnNum = 0;
        int code;
        while ((code = reader.read()) != -1) {
            columnNum++;
            if (code == '\n') {
                columnNum = 0;
                lineNum++;
            }
            if (code > 127) {
                byte[] utf8bytes = String.valueOf(code).getBytes(charset);
                Character.isValidCodePoint(code);
                if (code > Character.MAX_VALUE) {
                    String message = "The character value is too big to store in a char: ";
                    message += code;
                    message += " at " + lineNum + ":" + columnNum;
                    throw new IOException(message);
                }
                // Now it is safe to cast c to char because of the above check.
                char c = (char) code;
                int codePoint = code;
                // Check for surrogate.
                if (Character.isSurrogate(c)) {
                    char c2 = (char) reader.read();
                    if (Character.isSurrogatePair(c2, c)) {
                        codePoint = Character.toCodePoint(c2, c);
                    } else if (Character.isSurrogatePair(c, c2)) {
                        codePoint = Character.toCodePoint(c, c2);
                    } else {
                        throw new IOException("Bad surrogate pair: firstRead=" + c + ", secondRead=" + c2);
                    }
                }
                System.out.printf("%s:%d:%d character='%c' codePoint=%d bytes=%s%n", fileName, lineNum, columnNum, c, codePoint, Arrays.toString(utf8bytes));
            }
        }
    }

}
