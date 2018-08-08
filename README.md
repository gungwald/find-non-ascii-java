# FEATURES

* Install.bat script for easy setup
* Simple to use, works just like other commands
* Finds location of non-ASCII characters
* Shows the non-ASCII characters that are found
* Shows the UNICODE code point of non-ASCII characters
* Shows the byte encoding of non-ASCII characters


# USAGE EXAMPLE

```
C:\Users\fred>find-non-ascii test-data.txt
test-data.txt:222,132: char='‎' code=8206 bytes=[e2 80 8e]
test-data.txt:222,203: char='‎' code=8206 bytes=[e2 80 8e]
test-data.txt:491,5: char='‐' code=8208 bytes=[e2 80 90]
test-data.txt:529,313: char='ñ' code=241 bytes=[c3 b1]
test-data.txt:529,475: char='ç' code=231 bytes=[c3 a7]
test-data.txt:529,797: char='日' code=26085 bytes=[e6 97 a5]
test-data.txt:529,798: char='本' code=26412 bytes=[e6 9c ac]
test-data.txt:529,799: char='語' code=35486 bytes=[e8 aa 9e]
test-data.txt:529,962: char='ê' code=234 bytes=[c3 aa]
test-data.txt:529,1119: char='Р' code=1056 bytes=[d0 a0]
test-data.txt:529,1120: char='у' code=1091 bytes=[d1 83]
test-data.txt:529,1121: char='с' code=1089 bytes=[d1 81]
test-data.txt:529,1122: char='с' code=1089 bytes=[d1 81]
test-data.txt:529,1123: char='к' code=1082 bytes=[d0 ba]
test-data.txt:529,1124: char='и' code=1080 bytes=[d0 b8]
test-data.txt:529,1125: char='й' code=1081 bytes=[d0 b9]
test-data.txt:529,1281: char='中' code=20013 bytes=[e4 b8 ad]
test-data.txt:529,1282: char='文' code=25991 bytes=[e6 96 87]
```


# PROPER INSTALLATION

* Download the latest release from the above `release` tab.
* Unzip the archive on your machine.
* Run the install.bat script.
* You're now ready to run `find-non-ascii`.


# QUICK START

## Windows

The following commands invoke the find-non-ascii.bat file.
Including the .bat extension is not necessary. Because the
`find-non-ascii` command is installed into your PATH, you
can just type its name, regardless of what your current
directory is:

    C:\my\data>find-non-ascii data-file.txt


## UNIX/Linux/BSD/MacOS

You must make the script executable first:

    $ chmod a+x find-non-ascii

Then you can run it:

    $ find-non-ascii path/to/your/data-file.txt

or

    $ path/to/find-non-ascii your-data-file.txt


# REQUIREMENTS

## Command-line Only

This program only works on the command line. You won't be able to
see the output if you double click it from the desktop.

## Check for Javer

You must have Java 1.5 or greater installed and on your PATH. On
Windows, the Java installation automatically puts java in your
PATH. Check it with this command:

    (Windows)
    C:\>where java

    (Linux/UNIX/BSD/MacOS)
    $ which java
        
## Install Java (if the above check failed)

On Red Hat Linux or Fedora you may have to install Java: 

    sudo yum install java-1.8.0-openjdk

If you're using another Linux distribution try:

    apt-cache search java jdk
    apt-get install <whatever you found that looks good>

Solaris comes with a working java installed and on the PATH
because they created it.

TODO - Mac & BSD
