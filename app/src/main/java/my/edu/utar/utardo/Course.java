package my.edu.utar.utardo;

public class Course {
    private String documentId;
    private String courseName;

    public Course(String documentId, String courseName) {
        this.documentId = documentId;
        this.courseName = courseName;
    }

    public String getDocumentId() {
        return documentId;
    }

    public String getCourseName() {
        return courseName;
    }
}
