QUICK START

    Windows

        The following commands invoke the find-non-ascii.bat file.
        Including the .bat extension is not necessary.
    
            C:\>find-non-ascii path\to\your\data-file.txt

            or

            C:\>path\to\find-non-ascii your-data-file.txt

    UNIX/Linux/BSD/MacOS

        You must make the script executable first:

            $ chmod a+x find-non-ascii

        Then you can run it:

            $ find-non-ascii path/to/your/data-file.txt

            or

            $ path/to/find-non-ascii your-data-file.txt


REQUIREMENTS

    Command-line Only

        This program only works on the command line. You won't be able to
        see the output if you double click it from the desktop.

    Check for Javer

        You must have Java 1.5 or greater installed and on your PATH. On
        Windows, the Java installation automatically puts java in your
        PATH. Check it with this command:

            (Windows)
            C:\>where java
            
            (Linux/UNIX/BSD/MacOS)
            $ which java
        
    Install Java (if the above check failed)

        On Red Hat Linux or Fedora you may have to install Java: 
        
            sudo yum install java-1.8.0-openjdk

        If you're using another Linux distribution, you're just wrong.
        Ha ha. Just kidding. Try:

            apt-cache search java jdk
            apt-get install <whatever you found that looks good>

        Solaris comes with a working java installed and on the PATH
        because they created it.

        I would provide instructions for FreeBSD, NetBSD, OpenBSD, etc.
        but I don't know how to do it. I'm sure they're fine operating
        system though. :-)

        On a Mac you'll have to figure it out because I only have
        PowerPC Macs because they were the last real Macs. Ha ha.
        Actually I just can't bring myself to part with the amount of
        cash it would require to buy a modern Mac.


PROPER INSTALLATION

        Run the install script.
        