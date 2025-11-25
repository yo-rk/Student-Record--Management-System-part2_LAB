import java.io.*;
import java.util.*;

/**
 * StudentRecordApp.java
 * Single-file Student Record Management System (final corrected version)
 *
 * - Uses: BufferedReader/BufferedWriter, RandomAccessFile
 * - Uses: ArrayList<Student> (with generics)
 * - Sorting: by marks (desc) and by name (asc)
 * - Displaying: using Iterator
 * - RandomAccessFile demo using recorded offsets
 *
 * Save as StudentRecordApp.java and run:
 *   javac StudentRecordApp.java
 *   java StudentRecordApp
 */
public class StudentRecordApp {

    private static final String DATA_FILE = "students.txt";

    // --------------- Student class ---------------
    static class Student {
        int roll;
        String name;
        String email;
        String course;
        double marks;

        Student(int roll, String name, String email, String course, double marks) {
            this.roll = roll;
            this.name = name;
            this.email = email;
            this.course = course;
            this.marks = marks;
        }

        static Student fromCSV(String line) {
            if (line == null) return null;
            String[] parts = line.split(",", -1);
            if (parts.length < 5) return null;
            try {
                int r = Integer.parseInt(parts[0].trim());
                String n = parts[1].trim();
                String e = parts[2].trim();
                String c = parts[3].trim();
                double m = Double.parseDouble(parts[4].trim());
                return new Student(r, n, e, c, m);
            } catch (Exception ex) {
                return null;
            }
        }

        String toCSV() {
            return roll + "," + name + "," + email + "," + course + "," + marks;
        }

        @Override
        public String toString() {
            return "Roll No: " + roll +
                    "\nName: " + name +
                    "\nEmail: " + email +
                    "\nCourse: " + course +
                    "\nMarks: " + marks;
        }
    }

    // --------------- File utilities ---------------
    static class FileUtil {

        static class LoadResult {
            ArrayList<Student> students;
            ArrayList<Long> offsets;
            LoadResult(ArrayList<Student> s, ArrayList<Long> o) { students = s; offsets = o; }
        }

        // Load students and record offsets for each line (for RandomAccessFile demo)
        static LoadResult load(String filename) {
            ArrayList<Student> list = new ArrayList<>();
            ArrayList<Long> offsets = new ArrayList<>();
            try (RandomAccessFile raf = new RandomAccessFile(filename, "r")) {
                long pointer = raf.getFilePointer();
                String line;
                while ((line = raf.readLine()) != null) {
                    offsets.add(pointer);
                    // raf.readLine returns bytes interpreted as ISO-8859-1; usually ok for ASCII CSV
                    Student s = Student.fromCSV(line.trim());
                    if (s != null) list.add(s);
                    pointer = raf.getFilePointer();
                }
            } catch (FileNotFoundException fnf) {
                // file may not exist yet — that's fine
            } catch (IOException ignored) {}
            return new LoadResult(list, offsets);
        }

        static boolean saveAll(String filename, ArrayList<Student> list) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
                for (Student s : list) {
                    bw.write(s.toCSV());
                    bw.newLine();
                }
                return true;
            } catch (IOException ex) {
                System.out.println("Error saving file: " + ex.getMessage());
                return false;
            }
        }

        static boolean append(String filename, Student s) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename, true))) {
                bw.write(s.toCSV());
                bw.newLine();
                return true;
            } catch (IOException ex) {
                System.out.println("Error appending file: " + ex.getMessage());
                return false;
            }
        }

        static Student readRandom(String filename, ArrayList<Long> offsets, int index) {
            if (offsets == null || offsets.isEmpty() || index < 0 || index >= offsets.size()) return null;
            try (RandomAccessFile raf = new RandomAccessFile(filename, "r")) {
                raf.seek(offsets.get(index));
                String line = raf.readLine();
                return Student.fromCSV(line == null ? null : line.trim());
            } catch (IOException ex) {
                System.out.println("Random read error: " + ex.getMessage());
                return null;
            }
        }
    }

    // --------------- Manager ---------------
    static class StudentManager {
        ArrayList<Student> students = new ArrayList<>();
        ArrayList<Long> offsets = new ArrayList<>();

        void loadFromFile() {
            FileUtil.LoadResult r = FileUtil.load(DATA_FILE);
            students = r.students;
            offsets = r.offsets;
            if (students.isEmpty()) {
                System.out.println("No students loaded (file missing or empty).");
            } else {
                System.out.println("Loaded students from file:\n");
                for (Student s : students) {
                    System.out.println(s + "\n");
                }
            }
        }

        void addStudent(Student s) {
            students.add(s);
            boolean ok = FileUtil.append(DATA_FILE, s);
            if (!ok) System.out.println("Warning: could not append to file.");
            // refresh offsets
            offsets = FileUtil.load(DATA_FILE).offsets;
        }

        void viewAll() {
            if (students.isEmpty()) {
                System.out.println("No students to display.");
                return;
            }
            Iterator<Student> it = students.iterator();
            while (it.hasNext()) {
                System.out.println(it.next() + "\n");
            }
        }

        Student searchByName(String name) {
            for (Student s : students) {
                if (s.name.equalsIgnoreCase(name.trim())) return s;
            }
            return null;
        }

        boolean deleteByName(String name) {
            boolean removed = students.removeIf(s -> s.name.equalsIgnoreCase(name.trim()));
            if (removed) {
                FileUtil.saveAll(DATA_FILE, students);
                offsets = FileUtil.load(DATA_FILE).offsets;
            }
            return removed;
        }

        void sortByMarksDescending() {
            // uses generics; safe to access s.marks
            students.sort((a, b) -> Double.compare(b.marks, a.marks));
        }

        void sortByNameAscending() {
            students.sort(Comparator.comparing(s -> s.name.toLowerCase()));
        }

        void demoRandomRead(Scanner sc) {
            if (offsets.isEmpty()) {
                System.out.println("No records available for random read.");
                return;
            }
            System.out.println("Enter record index (1.." + offsets.size() + ", 0 to cancel): ");
            int idx = -1;
            try {
                idx = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) { idx = -1; }
            if (idx <= 0) {
                System.out.println("Cancelled.");
                return;
            }
            Student s = FileUtil.readRandom(DATA_FILE, offsets, idx - 1);
            if (s != null) System.out.println("Random record:\n" + s);
            else System.out.println("Could not read record at that index.");
        }

        void saveAll() {
            boolean ok = FileUtil.saveAll(DATA_FILE, students);
            if (ok) System.out.println("All records saved to " + DATA_FILE);
            else System.out.println("Error saving records.");
        }
    }

    // --------------- Main ---------------
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        StudentManager manager = new StudentManager();

        // ensure file exists
        try { new File(DATA_FILE).createNewFile(); } catch (IOException ignored) {}

        manager.loadFromFile();

        while (true) {
            System.out.println("===== Capstone Student Menu =====");
            System.out.println("1. Add Student");
            System.out.println("2. View All Students");
            System.out.println("3. Search by Name");
            System.out.println("4. Delete by Name");
            System.out.println("5. Sort by Marks");
            System.out.println("6. Sort by Name");
            System.out.println("7. Demo Random Read (RandomAccessFile)");
            System.out.println("8. Save and Exit");
            System.out.print("Enter choice: ");

            String choiceLine = sc.nextLine().trim();
            int choice;
            try { choice = Integer.parseInt(choiceLine); } catch (NumberFormatException e) { choice = -1; }

            switch (choice) {
                case 1:
                    try {
                        System.out.print("Enter Roll No: ");
                        int roll = Integer.parseInt(sc.nextLine().trim());
                        System.out.print("Enter Name: ");
                        String name = sc.nextLine().trim();
                        System.out.print("Enter Email: ");
                        String email = sc.nextLine().trim();
                        System.out.print("Enter Course: ");
                        String course = sc.nextLine().trim();
                        System.out.print("Enter Marks: ");
                        double marks = Double.parseDouble(sc.nextLine().trim());
                        Student s = new Student(roll, name, email, course, marks);
                        manager.addStudent(s);
                        System.out.println("Student added.");
                    } catch (Exception e) {
                        System.out.println("Invalid input. Student not added.");
                    }
                    break;

                case 2:
                    manager.viewAll();
                    break;

                case 3:
                    System.out.print("Enter name to search: ");
                    String q = sc.nextLine().trim();
                    Student found = manager.searchByName(q);
                    if (found != null) System.out.println("Found:\n" + found);
                    else System.out.println("No student found with name: " + q);
                    break;

                case 4:
                    System.out.print("Enter name to delete: ");
                    String d = sc.nextLine().trim();
                    boolean removed = manager.deleteByName(d);
                    System.out.println(removed ? "Student(s) deleted." : "No matching student found.");
                    break;

                case 5:
                    manager.sortByMarksDescending();
                    System.out.println("Sorted by marks (descending):");
                    manager.viewAll();
                    break;

                case 6:
                    manager.sortByNameAscending();
                    System.out.println("Sorted by name (ascending):");
                    manager.viewAll();
                    break;

                case 7:
                    manager.demoRandomRead(sc);
                    break;

                case 8:
                    manager.saveAll();
                    System.out.println("Exiting. Goodbye!");
                    sc.close();
                    return;

                default:
                    System.out.println("Invalid choice. Try again.");
            }

            System.out.println(); // spacing before next menu
        }
    }
}
