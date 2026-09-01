package com.resumebuilder;

import org.openjdk.jmh.annotations.*;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class ResumeBenchmark {

    private ResumeBuilderApp.ResumeData resumeData;

    @Setup
    public void setup() {
        resumeData = new ResumeBuilderApp.ResumeData();
        resumeData.fullNameProperty().set("Asmit Verma");
        resumeData.headlineProperty().set("Software Engineer");
        resumeData.emailProperty().set("asmit.verma@example.com");
        resumeData.phoneProperty().set("+91 98765 43210");
        resumeData.locationProperty().set("Bengaluru, India");
        resumeData.websiteProperty().set("linkedin.com/in/asmitverma");
        resumeData.summaryProperty().set("Detail-oriented Java developer with experience building desktop and web applications.");
        resumeData.educationProperty().set("B.E. in Computer Science, ABC Institute of Technology, 2023 - 2027");
        resumeData.experienceProperty().set("Student Developer Intern - BrightCode Labs | Summer 2025");
        resumeData.skillsProperty().set("Java, JavaFX, OOP, MySQL, Git, HTML, CSS, Problem Solving, UI Design");
        resumeData.projectsProperty().set("Resume Builder Application");
    }

    @Benchmark
    public String benchmarkToFormattedResume() {
        return resumeData.toFormattedResume();
    }

    @Benchmark
    public String benchmarkSafeFileName() {
        return ResumeBuilderApp.safeFileName("Asmit Verma (Software Engineer)");
    }

    @Benchmark
    public String benchmarkJoinNonBlank() {
        return ResumeBuilderApp.joinNonBlank(" | ", 
            resumeData.emailProperty().get(), 
            resumeData.phoneProperty().get(), 
            resumeData.locationProperty().get(), 
            resumeData.websiteProperty().get());
    }
}
