package com.resumebuilder;

import java.io.IOException;
import java.nio.file.Files;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class ResumeBuilderApp extends Application {
    private TextField fullNameField;
    private TextField emailField;
    private TextField phoneField;
    private TextField addressField;
    private TextArea summaryArea;
    private TextArea educationArea;
    private TextArea experienceArea;
    private TextArea skillsArea;
    private TextArea previewArea;

    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        Label title = new Label("Simple Resume Builder");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        BorderPane.setMargin(title, new Insets(0, 0, 10, 0));
        root.setTop(title);

        SplitPane splitPane = new SplitPane(createFormPane(stage), createPreviewPane());
        splitPane.setDividerPositions(0.55);
        root.setCenter(splitPane);

        loadSampleData();
        updatePreview();

        Scene scene = new Scene(root, 900, 600);

        stage.setTitle("Resume Builder");
        stage.setScene(scene);
        stage.show();
    }

    private VBox createFormPane(Stage stage) {
        VBox formPane = new VBox(10);
        formPane.setPadding(new Insets(10));

        GridPane personalDetails = new GridPane();
        personalDetails.setHgap(10);
        personalDetails.setVgap(10);

        fullNameField = new TextField();
        emailField = new TextField();
        phoneField = new TextField();
        addressField = new TextField();
        summaryArea = new TextArea();
        educationArea = new TextArea();
        experienceArea = new TextArea();
        skillsArea = new TextArea();

        summaryArea.setPrefRowCount(3);
        educationArea.setPrefRowCount(4);
        experienceArea.setPrefRowCount(5);
        skillsArea.setPrefRowCount(3);

        personalDetails.add(new Label("Full Name:"), 0, 0);
        personalDetails.add(fullNameField, 1, 0);
        personalDetails.add(new Label("Email:"), 0, 1);
        personalDetails.add(emailField, 1, 1);
        personalDetails.add(new Label("Phone:"), 0, 2);
        personalDetails.add(phoneField, 1, 2);
        personalDetails.add(new Label("Address:"), 0, 3);
        personalDetails.add(addressField, 1, 3);
        GridPane.setHgrow(fullNameField, Priority.ALWAYS);
        GridPane.setHgrow(emailField, Priority.ALWAYS);
        GridPane.setHgrow(phoneField, Priority.ALWAYS);
        GridPane.setHgrow(addressField, Priority.ALWAYS);

        Button updateButton = new Button("Update Preview");
        updateButton.setOnAction(event -> updatePreview());

        Button clearButton = new Button("Clear");
        clearButton.setOnAction(event -> clearFields());

        Button exportButton = new Button("Export");
        exportButton.setOnAction(event -> exportResume(stage));

        VBox.setVgrow(summaryArea, Priority.NEVER);

        formPane.getChildren().addAll(
            new Label("Personal Details"),
            personalDetails,
            new Label("Summary"),
            summaryArea,
            new Label("Education"),
            educationArea,
            new Label("Experience"),
            experienceArea,
            new Label("Skills"),
            skillsArea,
            updateButton,
            clearButton,
            exportButton
        );

        return formPane;
    }

    private VBox createPreviewPane() {
        VBox previewPane = new VBox(10);
        previewPane.setPadding(new Insets(10));

        Label previewLabel = new Label("Resume Preview");
        previewArea = new TextArea();
        previewArea.setEditable(false);
        previewArea.setWrapText(true);

        VBox.setVgrow(previewArea, Priority.ALWAYS);
        previewPane.getChildren().addAll(previewLabel, previewArea);
        return previewPane;
    }

    private void updatePreview() {
        StringBuilder builder = new StringBuilder();

        appendLine(builder, fullNameField.getText());
        appendLine(builder, emailField.getText());
        appendLine(builder, phoneField.getText());
        appendLine(builder, addressField.getText());

        appendSection(builder, "SUMMARY", summaryArea.getText());
        appendSection(builder, "EDUCATION", educationArea.getText());
        appendSection(builder, "EXPERIENCE", experienceArea.getText());
        appendSection(builder, "SKILLS", skillsArea.getText());

        previewArea.setText(builder.toString());
    }

    private void clearFields() {
        fullNameField.clear();
        emailField.clear();
        phoneField.clear();
        addressField.clear();
        summaryArea.clear();
        educationArea.clear();
        experienceArea.clear();
        skillsArea.clear();
        updatePreview();
    }

    private void exportResume(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Resume");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        fileChooser.setInitialFileName(safeFileName(fullNameField.getText()) + "-resume.txt");

        var file = fileChooser.showSaveDialog(stage);
        if (file == null) {
            return;
        }

        try {
            Files.writeString(file.toPath(), previewArea.getText());
            showMessage(Alert.AlertType.INFORMATION, "Export Complete", "Resume exported to:\n" + file.getAbsolutePath());
        } catch (IOException exception) {
            showMessage(Alert.AlertType.ERROR, "Export Failed", "Could not save the resume file.\n" + exception.getMessage());
        }
    }

    private void showMessage(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void loadSampleData() {
        fullNameField.setText("Asmit verma");
        emailField.setText("asmit.verma@example.com");
        phoneField.setText("+91 1111111111");
        addressField.setText("Bengaluru, India");
        summaryArea.setText("Computer science student with strong interest in Java and software development.");
        educationArea.setText("B.E. in Computer Science\nABC Institute of Technology\n2023 - 2027");
        experienceArea.setText("Student Project Developer\n- Built Java applications for academic projects.");
        skillsArea.setText("Java, JavaFX, OOP, MySQL");
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

    private void appendSection(StringBuilder builder, String title, String content) {
        if (content == null || content.isBlank()) {
            return;
        }
        builder.append(System.lineSeparator());
        builder.append(title).append(System.lineSeparator());
        builder.append(content.trim()).append(System.lineSeparator());
    }

    private void appendLine(StringBuilder builder, String value) {
        if (value == null || value.isBlank()) {
            return;
        }
        builder.append(value.trim()).append(System.lineSeparator());
    }
}
