# Resume Builder JavaFX App

A styled desktop resume builder created with JavaFX and Maven.

## Features

- form fields for personal details, summary, education, experience, skills, and projects
- live resume preview in the same window
- sample data loader
- clear button to reset the form
- export to `.txt`
- export to PDF through the JavaFX print dialog

## Requirements

- Java 17 or later
- Maven 3.9 or later

## Run the App

```bash
mvn clean javafx:run
```

## How PDF Export Works

1. Click `Export PDF`
2. In the print dialog, choose a PDF printer such as `Microsoft Print to PDF`
3. Save the file

## Project Structure

- `src/main/java/com/resumebuilder/ResumeBuilderApp.java`: main JavaFX application and export logic
- `src/main/java/com/resumebuilder/Launcher.java`: launcher entry point
- `src/main/java/module-info.java`: module definition
- `src/main/resources/styles/app.css`: CSS styling for the JavaFX interface

## Branch Note

This README matches the `css-version` branch of the project.
