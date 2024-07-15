import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class ExamScheduler {

    public static void main(String[] args) {
        String filePath = "C:/Users/vik7n/Downloads/iiitg/StudentData.csv";

        try {
            List<Exam> schedule = scheduleExams(filePath);
            // Print out the schedule
            for (Exam exam : schedule) {
                System.out.println("Exam scheduled: Subject " + exam.getSubjectCode() +
                        " on Day " + exam.getTimeslot().getDay() +
                        " Slot " + exam.getTimeslot().getSlot());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Exam> scheduleExams(String filePath) throws IOException {
        List<Student> students = parseStudents(filePath);
        Map<String, Integer> subjectEnrollments = countEnrollments(students);
        return createSchedule(subjectEnrollments);
    }

    private static List<Student> parseStudents(String filePath) throws IOException {
        List<Student> students = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isFirstLine = true; // Skip header
            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                String[] parts = line.split(",");
                int rollNumber = Integer.parseInt(parts[0].trim());
                Set<String> courses = new HashSet<>();
                for (int i = 1; i < parts.length; i++) {
                    if (!parts[i].trim().isEmpty()) {
                        courses.add(parts[i].trim());
                    }
                }
                students.add(new Student(rollNumber, courses));
            }
        }

        return students;
    }

    private static Map<String, Integer> countEnrollments(List<Student> students) {
        Map<String, Integer> enrollments = new HashMap<>();
        for (Student student : students) {
            for (String course : student.getCourses()) {
                enrollments.put(course, enrollments.getOrDefault(course, 0) + 1);
            }
        }
        return enrollments;
    }

    private static List<Exam> createSchedule(Map<String, Integer> enrollments) {
        List<Exam> schedule = new ArrayList<>();
        int day = 1;
        int slot = 1;

        // Create exams based on enrollments
        for (Map.Entry<String, Integer> entry : enrollments.entrySet()) {
            String subject = entry.getKey();
            schedule.add(new Exam(subject, new Timeslot(day, slot)));
            // Move to the next slot or day as per your scheduling logic
            slot++;
            if (slot > 3) {
                slot = 1;
                day++;
            }
        }

        return schedule;
    }
}

class Student {
    private final int rollNumber;
    private final Set<String> courses; // Use Set to avoid duplicates

    public Student(int rollNumber, Set<String> courses) {
        this.rollNumber = rollNumber;
        this.courses = courses;
    }

    public int getRollNumber() {
        return rollNumber;
    }

    public Set<String> getCourses() {
        return courses;
    }
}

class Exam {
    private final String subjectCode;
    private final Timeslot timeslot;

    public Exam(String subjectCode, Timeslot timeslot) {
        this.subjectCode = subjectCode;
        this.timeslot =
