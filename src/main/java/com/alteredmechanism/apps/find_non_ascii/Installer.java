package com.alteredmechanism.apps.find_non_ascii;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.FileFilter;
import java.io.File;
import java.io.IOException;

public class Installer {

    private BufferedReader in;

    public void install(File sourceDir) throws IOException, InterruptedException {
        in = new BufferedReader(new InputStreamReader(System.in));
        File installDir;
        System.out.println("FIND-NON-ASCII INSTALLER");
        boolean systemInstall = choice("Install for all users? You must have admin rights.");
        installDir = new File(choiceString("Enter install directory", 
            getDefaultInstallDir(systemInstall).getAbsolutePath()));
        copyFiles(selectSourceFiles(sourceDir), installDir);
    }

    public File getDefaultInstallDir(boolean systemInstall) {
        return systemInstall ? getDefaultSystemInstallDir() : getDefaultUserInstallDir();
    }
    
    public int menu(String title, String[] choices) throws IOException {
    	String userResponse;
    	int selectedIndex;
    	do {
    		System.out.println(title);
    		for (int i = 0; i < choices.length; i++) {
    			System.out.printf("%d. %s%n", i, choices[i]);
    		}
    		userResponse = in.readLine();
    		selectedIndex = Integer.parseInt(userResponse);
    	} while (selectedIndex < 0 && choices.length <= selectedIndex);
    	return selectedIndex;
    }

    public String choiceString(String prompt, String defaultResponse) throws IOException {
        String userResponse;
        String choice;
        System.out.print(prompt);
        System.out.print(" [");
        System.out.print(defaultResponse);
        System.out.print("] ");
        userResponse = in.readLine();
        if (userResponse == null || userResponse.equalsIgnoreCase("q")) {
            System.exit(0);
        }
        choice = userResponse.equals("") ? defaultResponse : userResponse;
        return choice;
    }

    public boolean choice(String prompt) throws IOException {
        String userResponse;
        do {
            System.out.printf("%s (y/n/q) ", prompt);
            userResponse = in.readLine();
            if (userResponse == null || userResponse.equalsIgnoreCase("q")) {
                System.exit(0);
            }
            userResponse = userResponse.toLowerCase();
        } while (! userResponse.equals("y") && ! userResponse.equals("n"));
        return userResponse.equals("y");
    }

    public boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().startsWith("windows");
    }

    public File getDefaultSystemInstallDir() {
        return new File(isWindows() ? "C:\\Program Files\\Find Non-ASCII" : "/usr/local");
    }

    public File getDefaultUserInstallDir() {
        String home = System.getProperty("user.home");
        return new File(isWindows() ? home + "\\Program Files\\Find Non-ASCII" : home + "/.local");
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
            System.err.println("Could not find jar in directory: " + sourceDir.getAbsolutePath());
            System.exit(1);
        }
        else {
            sourceFiles.add(jar);
        }
        return sourceFiles;
    }

    class JarFileFilter implements FileFilter {
        public boolean accept(File pathname) {
            boolean accept = pathname.getName().toLowerCase().endsWith(".jar");
            System.out.printf("JarFileFilter.accept: pathname=%s returning=%b%n", pathname, accept);
            return accept;
        }
    }

    public File findFirstJar(File dir) {
        FileFilter jarFilter = new JarFileFilter();
        File[] jars = dir.listFiles(jarFilter);
        System.out.printf("findFirstJar: jars=%s%n", Arrays.asList(jars));
        System.out.printf("findFirstJar: jars.length=%d%n", jars.length);
        if (jars.length > 0) {
            System.out.printf("findFirstJar: jars[0]=%s%n", jars[0].getAbsolutePath());
            return jars[0];
        }
        else {
            return null;
        }
    }

    public void copyFiles(List<File> sourceFiles, File targetDir) throws IOException, InterruptedException {
        File jarDestinationDir = new File(targetDir, "share/java");
        File scriptDestinationDir = new File(targetDir, "bin");
        for (int i = 0; i < sourceFiles.size(); i++) {
            File f = sourceFiles.get(i);
            if (f.getName().endsWith(".jar")) {
                copyFile(f, jarDestinationDir);
            }
            else {
                copyFile(f, scriptDestinationDir);
                makeExecutable(new File(scriptDestinationDir, f.getName()));
            }
        }
    }

    public void copyFile(File f, File destDir) throws IOException, InterruptedException {
        mkdir(destDir);
        String copy = isWindows() ? "copy" : "cp";
        System.out.printf("Copying %s to %s%n", f.getName(), destDir.getAbsolutePath());
        Executor.exec(new String[] {copy, f.getAbsolutePath(), destDir.getAbsolutePath()});
    }

    public void mkdir(File dir) {
        if (! dir.exists()) {
            if (! dir.mkdirs()) {
                System.err.printf("Failed to create directory: %s%n", dir.getAbsolutePath());
                System.exit(1);
            }
        }
        else if (! dir.isDirectory()) {
            System.err.printf("Required directory already exists as a regular file: %s%n", 
                dir.getAbsolutePath());
            System.exit(1);
        }
    }

    public void makeExecutable(File f) throws IOException, InterruptedException {
        if (! isWindows()) {
            Executor.exec(new String[] {"chmod", "a+x", f.getAbsolutePath()});
        }
    }

}
