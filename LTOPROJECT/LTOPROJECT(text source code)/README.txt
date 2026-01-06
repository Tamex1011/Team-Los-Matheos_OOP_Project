LTO PROJECT - TEXT SOURCE CODE BACKUP
========================================

This folder contains a complete text-based backup of the LTO Project.
All source files, data files, and scripts have been converted to .txt format.

STRUCTURE:
----------
data/
  - licenses.csv.txt
  - users.csv.txt
  - violations.csv.txt

scripts/
  - build_and_run.ps1.txt
  - run.bat.txt

src/
  - controller/
    - FileHandler.java.txt
    - LTOSystemController.java.txt
  - model/
    - Admin.java.txt
    - Driver.java.txt
    - DriversLicense.java.txt
    - Officer.java.txt
    - Payable.java.txt
    - Recordable.java.txt
    - Role.java.txt
    - User.java.txt
    - Violation.java.txt
    - ViolationType.java.txt
  - util/
    - DBConnection.java.txt
  - utils/
    - InputValidator.java.txt
  - LicenseDao.java.txt
  - LTOSystem.java.txt
  - LTOSystemMain.java.txt
  - UserDao.java.txt
  - ViolationDao.java.txt

TOTAL FILES: 23 text files

NOTE: To restore these files, simply remove the .txt extension from each file.
For example: FileHandler.java.txt -> FileHandler.java

Generated: $(Get-Date -Format "yyyy-MM-dd HH:mm:ss")

