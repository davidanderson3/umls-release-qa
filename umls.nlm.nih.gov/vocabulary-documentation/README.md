# UMLS Vocabulary Documentation Angular Site

## Installation

Install Node version 18.20.4 (https://nodejs.org/en/about/previous-releases) 

Once installed (Windows only):

Find the npm global installation path: 

Open the command prompt.
 
Run this command to find where npm installs global packages:
 
`npm config get prefix`
 
This will give you a path like C:\Users\<Your-Username>\AppData\Roaming\npm.
 
Add it to the PATH:

* Right-click This PC or My Computer and select Properties.
* Click Advanced system settings on the left.
* In the System Properties window, click the Environment Variables button.
* Under System Variables, find the variable called Path and select it. Click Edit.
* Click New and add the npm global path from step 1, like C:\Users\<Your-Username>\AppData\Roaming\npm.
* Click OK to save the changes, and then restart your terminal.

These commands should return a version number:

`node -v`

`npm -v`

## Installing Angular

Open the command prompt. 

Navigate to the directory where you have cloned this repository:

`cd c:\SOME-DIRECTORY\umls-source-release\umls.nlm.nih.gov\vocabulary-documentation`

Run: 

`npm install`

## Development server

Run `npx ng serve` for a dev server. Navigate to `http://localhost:4200/`. The application will automatically reload if you change any of the source files.

## UMLS Release Documentation

Replace the MRSAB.RRF file in the assets folder of the versioned branch (for example, 2024AB branch)

Once the documentation is finalized for the next release, do a pull request to merge that version branch into master (for example, merge 2024AB branch into master branch). 

QA the site. 

Publish the changes on release day. 







