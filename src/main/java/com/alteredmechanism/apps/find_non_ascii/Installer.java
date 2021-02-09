package com.alteredmechanism.apps.find_non_ascii;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import java.io.FileFilter;
import java.io.File;
import java.io.IOException;

public class Installer {

    private BufferedReader in;

    public void install(File sourceDir) throws IOException, InterruptedException {
        in = new BufferedReader(new InputStreamReader(System.in));
        File installDir;
        File defaultInstallDir;
        System.out.println("FIND-NON-ASCII INSTALLER");
        boolean systemInstall = choice("Install for all users? You must have admin rights.");
        installDir = new File(choiceString("Enter install directory", 
            getDefaultInstallDir(systemInstall).getAbsolutePath()));
        copyFiles(selectSourceFiles(sourceDir), installDir);
    }

    public File getDefaultInstallDir(boolean systemInstall) {
        File dir;
        if (systemInstall) {
            dir = getDefaultSystemInstallDir();
        }
        else {
            dir = getDefaultUserInstallDir();
        }
        return dir;
    }

    public String choiceString(String prompt, String defaultResponse) throws IOException {
        String userResponse;
        String choice;
        System.out.print(prompt);
        System.out.print(" [");
        System.out.print(defaultResponse);
        System.out.print("] ");
        userResponse = in.readLine();
        if (userResponse == null) {
            System.exit(0);
        }
        if (userResponse.equals("q")) {
            System.exit(0);
        }
        if (userResponse.equals("")) {
            choice = defaultResponse;
        }
        else {
            choice = userResponse;
        }
        return choice;
    }

    public boolean choice(String prompt) throws IOException {
        String userResponse = "";
        boolean choice = false;
        while (! userResponse.equals("y") && ! userResponse.equals("n")) {
            System.out.print(prompt);
            System.out.print(" (y/n/q) ");
            userResponse = in.readLine();
            if (userResponse == null) {
                System.exit(0);
            }
            userResponse = userResponse.toLowerCase();
            if (userResponse.equals("q")) {
                System.exit(0);
            }
        }
        if (userResponse.equals("y")) {
            choice = true;
        }
        return choice;
    }

    public boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().startsWith("windows");
    }

    public File getDefaultSystemInstallDir() {
        String dir;
        if (isWindows()) {
            dir = "C:\\Program Files\\Find Non-ASCII";
        }
        else {
            dir = "/usr/local";
        }
        return new File(dir);
    }

    public File getDefaultUserInstallDir() {
        String dir;
        String home = System.getProperty("user.home");
        if (isWindows()) {
            dir = home + "\\Program Files\\Find Non-ASCII";
        }
        else {
            dir = home + "/.local";
        }
        return new File(dir);
    }

    public List<File> selectSourceFiles(File sourceDir) {
        List<File> sourceFiles = new ArrayList<File>();
        if (isWindows()) {
            sourceFiles.add(new File(sourceDir, "find-non-ascii.bat"));
        }
        else {
            sourceFiles.add(new File(sourceDir, "find-non-ascii"));
        }
        File jar = findFirstJar(sourceDir);
        if (jar == null) {
            System.err.println("Could not find jar in directory " 
                + sourceDir.getAbsolutePath());
            System.exit(1);
        }
        else {
            sourceFiles.add(jar);
        }
        return sourceFiles;
    }

    class JarFileFilter implements FileFilter {
        public boolean accept(File pathname) {
            return pathname.getName().toLowerCase().endsWith(".jar");
        }
    }

    public File findFirstJar(File dir) {
        FileFilter jarFilter = new JarFileFilter();
        File[] jars = dir.listFiles(jarFilter);
        if (jars.length > 1) {
            return jars[0];
        }
        else {
            return null;
        }
    }

    public void copyFiles(List<File> sourceFiles, File targetDir) throws IOException, InterruptedException {
        File jarDestination = new File(targetDir, "share/java");
        File scriptDestination = new File(targetDir, "bin");
        for (int i = 0; i < sourceFiles.size(); i++) {
            File f = sourceFiles.get(i);
            if (f.getName().endsWith(".jar")) {
                copyFile(f, jarDestination);
            }
            else {
                copyFile(f, scriptDestination);
            }
        }
    }

    public void copyFile(File f, File destination) throws IOException, InterruptedException {
        String copy;
        if (isWindows()) {
            copy = "copy";
        }
        else {
            copy = "cp";
        }
        String[] cmd = new String[3];
        cmd[0] = copy;
        cmd[1] = f.getAbsolutePath();
        cmd[3] = destination.getAbsolutePath();
        System.out.printf("Copying %s to %s%n", f.getName(), destination.getAbsolutePath());
        Executor.exec(cmd);
    }

}
