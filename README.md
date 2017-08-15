# csv-diff
Diff two CSV files with the differences output to a new CSV file.

Eclipse Maven project

```
usage: Diff [-d | -diffsOnly] [-m | --showMissing] [-z | --zeroNotNull]
            <idColumnName> <CSV File A> <CSV File B> <Output CSV file>
            
CSV Diff Tool
 -d,--diffsOnly     Only output cells with differing values
 -h,--help          Show this help message
 -m,--showMissing   Output rows missing between files
 -z,--zeroNotNull   Zero does not match null or empty string (default is
                    to match)
```                    

Run this application from the command line.
If there are differences between the two input CSV files then "Output CSV file" will be created.
The command line output will list rows and columns missing between the two input files.
