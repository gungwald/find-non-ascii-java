import java.io.BufferedReader;

public class Installer {
    BufferedReader in;
    public void install(String sourceDir) {
        in = new BufferedReader(new InputStreamReader(System.in));
        String installDir;
        String defaultInstallDir;
        System.out.println("FIND-NON-ASCII INSTALLER");
        if (choice("Install for all users? You must have admin rights.")) {
            defaultInstallDir = getDefaultSystemInstallDir();
        }
        else {
            defaultInstallDir = getDefaultUserInstallDir();
        }
        installDir = choiceString("Enter install directory", defaultInstallDir);
        copyFiles(sourceDir, installDir);
    }
    public String choiceString(String prompt, String defaultResponse) {
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
    public boolean choice(String prompt) {
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
        return System.getProperty("os.name").startsWith("Win");
    }
    public String getDefaultSystemInstallDir() {
        if (isWindows()) {
            return "C:\\Program Files\\Altered Mechanism\\Find Non-ASCII";
        }
        else {
            return "/usr/local";
        }
    }

}
