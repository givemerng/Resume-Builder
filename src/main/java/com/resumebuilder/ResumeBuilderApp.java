package com.resumebuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Scale;
import javafx.print.PageLayout;
import javafx.print.PrinterJob;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class ResumeBuilderApp extends Application {
    private final ResumeData resumeData = new ResumeData();
    private VBox previewCard;

    @Override
    public void start(Stage stage) {
        seedSampleData();

        BorderPane root = new BorderPane();
        root.getStyleClass().add("app-shell");

        Label title = new Label("Resume Builder");
        title.getStyleClass().add("app-title");

        Label subtitle = new Label("Create a professional resume and export it as plain text.");
        subtitle.getStyleClass().add("app-subtitle");

        VBox header = new VBox(4, title, subtitle);
        header.setPadding(new Insets(24, 28, 16, 28));

        SplitPane splitPane = new SplitPane(buildEditorPane(stage), buildPreviewPane());
        splitPane.setOrientation(Orientation.HORIZONTAL);
        splitPane.setDividerPositions(0.47);

        root.setTop(header);
        root.setCenter(splitPane);

        Scene scene = new Scene(root, 1180, 760);
        scene.getStylesheets().add(Objects.requireNonNull(
            getClass().getResource("/styles/app.css")).toExternalForm());

        stage.setTitle("JavaFX Resume Builder");
        stage.setMinWidth(980);
        stage.setMinHeight(680);
        stage.setScene(scene);
        stage.show();
    }

    private ScrollPane buildEditorPane(Stage stage) {
        VBox content = new VBox(18);
        content.setPadding(new Insets(24));
        content.getStyleClass().add("panel");

        content.getChildren().addAll(
            sectionTitle("Personal Details"),
            createField("Full Name", resumeData.fullNameProperty(), "Asmit Verma"),
            createField("Role / Headline", resumeData.headlineProperty(), "Software Developer"),
            createField("Email", resumeData.emailProperty(), "asmit.verma@example.com"),
            createField("Phone", resumeData.phoneProperty(), "+91 98765 43210"),
            createField("Location", resumeData.locationProperty(), "Bengaluru, India"),
            createField("LinkedIn / Portfolio", resumeData.websiteProperty(), "linkedin.com/in/ava"),
            sectionTitle("Professional Summary"),
            createArea("Summary", resumeData.summaryProperty(), 5),
            sectionTitle("Education"),
            createArea("Education", resumeData.educationProperty(), 5),
            sectionTitle("Experience"),
            createArea("Experience", resumeData.experienceProperty(), 8),
            sectionTitle("Skills"),
            createArea("Skills", resumeData.skillsProperty(), 4),
            sectionTitle("Projects"),
            createArea("Projects", resumeData.projectsProperty(), 6),
            buildActions(stage)
        );

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("scroll-pane");
        return scrollPane;
    }

    private ScrollPane buildPreviewPane() {
        previewCard = new VBox(14);
        previewCard.setPadding(new Insets(28));
        previewCard.getStyleClass().add("preview-card");

        Label name = new Label();
        name.getStyleClass().add("resume-name");
        name.textProperty().bind(resumeData.fullNameProperty());

        Label headline = new Label();
        headline.getStyleClass().add("resume-headline");
        headline.textProperty().bind(resumeData.headlineProperty());

        Label contact = new Label();
        contact.getStyleClass().add("resume-contact");
        contact.textProperty().bind(Bindings.createStringBinding(
            () -> joinNonBlank(" | ",
                resumeData.emailProperty().get(),
                resumeData.phoneProperty().get(),
                resumeData.locationProperty().get(),
                resumeData.websiteProperty().get()),
            resumeData.emailProperty(),
            resumeData.phoneProperty(),
            resumeData.locationProperty(),
            resumeData.websiteProperty()
        ));
        contact.setWrapText(true);

        previewCard.getChildren().addAll(name, headline, contact, new Separator());
        previewCard.getChildren().add(createPreviewSection("Professional Summary", resumeData.summaryProperty()));
        previewCard.getChildren().add(createPreviewSection("Education", resumeData.educationProperty()));
        previewCard.getChildren().add(createPreviewSection("Experience", resumeData.experienceProperty()));
        previewCard.getChildren().add(createPreviewSection("Skills", resumeData.skillsProperty()));
        previewCard.getChildren().add(createPreviewSection("Projects", resumeData.projectsProperty()));

        ScrollPane scrollPane = new ScrollPane(previewCard);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("scroll-pane");
        return scrollPane;
    }

    private VBox createPreviewSection(String heading, StringProperty content) {
        Label title = new Label(heading);
        title.getStyleClass().add("section-heading");

        Label body = new Label();
        body.getStyleClass().add("section-body");
        body.textProperty().bind(Bindings.createStringBinding(
            () -> {
                String value = content.get();
                return value == null || value.isBlank() ? "Add details in the editor panel." : value;
            },
            content
        ));
        body.setWrapText(true);

        return new VBox(8, title, body);
    }

    private VBox buildActions(Stage stage) {
        Button fillSampleButton = new Button("Load Sample");
        fillSampleButton.getStyleClass().add("secondary-button");
        fillSampleButton.setOnAction(event -> seedSampleData());

        Button clearButton = new Button("Clear");
        clearButton.getStyleClass().add("secondary-button");
        clearButton.setOnAction(event -> resumeData.clear());

        Button exportButton = new Button("Export Resume");
        exportButton.getStyleClass().add("primary-button");
        exportButton.setOnAction(event -> exportResume(stage));

        Button pdfButton = new Button("Export PDF");
        pdfButton.getStyleClass().add("secondary-button");
        pdfButton.setOnAction(event -> exportPdf(stage));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox actionBar = new HBox(10, fillSampleButton, clearButton, spacer, pdfButton, exportButton);
        actionBar.setAlignment(Pos.CENTER_LEFT);
        return new VBox(new Separator(), actionBar);
    }

    private VBox createField(String labelText, StringProperty property, String promptText) {
        Label label = new Label(labelText);
        label.getStyleClass().add("field-label");

        TextField field = new TextField();
        field.setPromptText(promptText);
        field.textProperty().bindBidirectional(property);

        VBox box = new VBox(6, label, field);
        VBox.setVgrow(field, Priority.NEVER);
        return box;
    }

    private VBox createArea(String labelText, StringProperty property, int preferredRows) {
        Label label = new Label(labelText);
        label.getStyleClass().add("field-label");

        TextArea area = new TextArea();
        area.setPromptText("Write each point on a new line");
        area.setPrefRowCount(preferredRows);
        area.setWrapText(true);
        area.textProperty().bindBidirectional(property);

        VBox box = new VBox(6, label, area);
        VBox.setVgrow(area, Priority.NEVER);
        return box;
    }

    private Label sectionTitle(String text) {
        Label title = new Label(text);
        title.getStyleClass().add("editor-section-title");
        return title;
    }

    private void exportResume(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Resume");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        fileChooser.setInitialFileName(safeFileName(resumeData.fullNameProperty().get()) + "-resume.txt");

        var file = fileChooser.showSaveDialog(stage);
        if (file == null) {
            return;
        }

        try {
            Files.writeString(file.toPath(), resumeData.toFormattedResume());
            showMessage(Alert.AlertType.INFORMATION, "Export Complete", "Resume exported to:\n" + file.getAbsolutePath());
        } catch (IOException exception) {
            showMessage(Alert.AlertType.ERROR, "Export Failed", "Could not save the resume file.\n" + exception.getMessage());
        }
    }

    private void exportPdf(Stage stage) {
        if (previewCard == null) {
            showMessage(AlertType.ERROR, "Export Failed", "Resume preview is not ready yet.");
            return;
        }

        PrinterJob job = PrinterJob.createPrinterJob();
        if (job == null) {
            showMessage(AlertType.ERROR, "Export Failed", "No printer service is available.");
            return;
        }

        boolean accepted = job.showPrintDialog(stage);
        if (!accepted) {
            return;
        }

        PageLayout pageLayout = job.getJobSettings().getPageLayout();
        double printableWidth = pageLayout.getPrintableWidth();
        double printableHeight = pageLayout.getPrintableHeight();

        double scaleX = printableWidth / previewCard.getBoundsInParent().getWidth();
        double scaleY = printableHeight / previewCard.getBoundsInParent().getHeight();
        double scaleValue = Math.min(scaleX, scaleY);

        Scale scale = new Scale(scaleValue, scaleValue);
        previewCard.getTransforms().add(scale);

        boolean printed = job.printPage(previewCard);
        previewCard.getTransforms().remove(scale);

        if (printed) {
            job.endJob();
            showMessage(
                AlertType.INFORMATION,
                "PDF Export",
                "In the print dialog, choose your system's 'Save as PDF' option to export the resume."
            );
        } else {
            job.cancelJob();
            showMessage(AlertType.ERROR, "Export Failed", "The resume could not be sent to the printer.");
        }
    }

    private void showMessage(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void seedSampleData() {
        resumeData.fullNameProperty().set("Asmit Verma");
        resumeData.headlineProperty().set("Java Developer | UI Enthusiast");
        resumeData.emailProperty().set("asmit.verma@example.com");
        resumeData.phoneProperty().set("+91 98765 43210");
        resumeData.locationProperty().set("Bengaluru, India");
        resumeData.websiteProperty().set("linkedin.com/in/asmitverma");
        resumeData.summaryProperty().set("""
            Detail-oriented Java developer with experience building desktop and web applications.
            Strong foundation in object-oriented design, clean UI creation, and problem solving.
            Interested in creating useful products with simple, thoughtful user experiences.
            """.strip());
        resumeData.educationProperty().set("""
            B.E. in Computer Science, ABC Institute of Technology, 2023 - 2027
            CGPA: 8.9/10
            Relevant coursework: OOP, Data Structures, Database Systems, Software Engineering
            """.strip());
        resumeData.experienceProperty().set("""
            Student Developer Intern - BrightCode Labs | Summer 2025
            - Built a JavaFX dashboard to monitor student records and attendance.
            - Improved form validation and reduced manual data entry issues.

            Freelance Project - Library Management Tool
            - Designed modular Java classes for book tracking and issue/return workflows.
            - Prepared user-friendly reports for librarians and staff members.
            """.strip());
        resumeData.skillsProperty().set("""
            Java, JavaFX, OOP, MySQL, Git, HTML, CSS, Problem Solving, UI Design
            """.strip());
        resumeData.projectsProperty().set("""
            Resume Builder Application
            - Created a live-preview desktop tool in JavaFX for building resumes quickly.

            Student Task Planner
            - Developed a productivity app with task categories, due dates, and progress tracking.
            """.strip());
    }

    private static String joinNonBlank(String delimiter, String... values) {
        StringBuilder builder = new StringBuilder();
        for (String value : values) {
            if (value == null || value.isBlank()) {
                continue;
            }
            if (builder.length() > 0) {
                builder.append(delimiter);
            }
            builder.append(value.trim());
        }
        return builder.toString();
    }

    private static String safeFileName(String value) {
        if (value == null || value.isBlank()) {
            return "resume";
        }
        return value.trim().toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", "");
    }

    public static void main(String[] args) {
        launch(args);
    }

    private static final class ResumeData {
        private final StringProperty fullName = new SimpleStringProperty("");
        private final StringProperty headline = new SimpleStringProperty("");
        private final StringProperty email = new SimpleStringProperty("");
        private final StringProperty phone = new SimpleStringProperty("");
        private final StringProperty location = new SimpleStringProperty("");
        private final StringProperty website = new SimpleStringProperty("");
        private final StringProperty summary = new SimpleStringProperty("");
        private final StringProperty education = new SimpleStringProperty("");
        private final StringProperty experience = new SimpleStringProperty("");
        private final StringProperty skills = new SimpleStringProperty("");
        private final StringProperty projects = new SimpleStringProperty("");

        public StringProperty fullNameProperty() {
            return fullName;
        }

        public StringProperty headlineProperty() {
            return headline;
        }

        public StringProperty emailProperty() {
            return email;
        }

        public StringProperty phoneProperty() {
            return phone;
        }

        public StringProperty locationProperty() {
            return location;
        }

        public StringProperty websiteProperty() {
            return website;
        }

        public StringProperty summaryProperty() {
            return summary;
        }

        public StringProperty educationProperty() {
            return education;
        }

        public StringProperty experienceProperty() {
            return experience;
        }

        public StringProperty skillsProperty() {
            return skills;
        }

        public StringProperty projectsProperty() {
            return projects;
        }

        public void clear() {
            fullName.set("");
            headline.set("");
            email.set("");
            phone.set("");
            location.set("");
            website.set("");
            summary.set("");
            education.set("");
            experience.set("");
            skills.set("");
            projects.set("");
        }

        public String toFormattedResume() {
            StringBuilder builder = new StringBuilder();

            appendLine(builder, fullName.get());
            appendLine(builder, headline.get());
            appendLine(builder, joinNonBlank(" | ", email.get(), phone.get(), location.get(), website.get()));

            appendSection(builder, "PROFESSIONAL SUMMARY", summary.get());
            appendSection(builder, "EDUCATION", education.get());
            appendSection(builder, "EXPERIENCE", experience.get());
            appendSection(builder, "SKILLS", skills.get());
            appendSection(builder, "PROJECTS", projects.get());

            return builder.toString().trim() + System.lineSeparator();
        }

        private void appendSection(StringBuilder builder, String title, String content) {
            if (content == null || content.isBlank()) {
                return;
            }
            builder.append(System.lineSeparator())
                .append(title)
                .append(System.lineSeparator())
                .append(content.trim())
                .append(System.lineSeparator());
        }

        private void appendLine(StringBuilder builder, String value) {
            if (value == null || value.isBlank()) {
                return;
            }
            builder.append(value.trim()).append(System.lineSeparator());
        }
    }
}
