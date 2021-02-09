package com.alteredmechanism.apps.find_non_ascii;

import java.io.*;

public class Executor {

    public static void copyLines(InputStream in, PrintStream out) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
        while (true) {
            String line = bufferedReader.readLine();
            if (line == null) {
                break;
            }
            else {
                out.println(line);
            }
        }
        out.flush();
    }

    public static int exec(String[] cmd) throws IOException, InterruptedException {
        Process proc = Runtime.getRuntime().exec(cmd);
        copyLines(proc.getInputStream(), System.out);
        copyLines(proc.getErrorStream(), System.err);
        return proc.waitFor();
    }
}
