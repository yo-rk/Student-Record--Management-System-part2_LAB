# 🎓 **Student Record Management System (File Handling)**

# 📘 *Java Programming Project*

**Name:** Yashieta Sethi
**Roll No.:** 2401010187
**Course Name:** Java Programming
**Programme:** B.Tech CSE CORE
**Session:** 2025–26

---

## 📝 **Description**

This project is a **file-based Student Record Management System** implemented in a **single Java file (`StudentRecordApp.java`)**.

It demonstrates:

* **File Handling** using `BufferedWriter`, `FileWriter`, and `RandomAccessFile`
* **ArrayList with Generics** (`ArrayList<Student>`)
* **Sorting with Comparators** (by marks and by name)
* **Iteration using `Iterator<Student>`**
* **Random-access reading** of records from a file using stored byte offsets

All student records are stored in a text file named **`students.txt`**, allowing the data to persist across program runs.

---

## ✨ **Features**

* ➕ Add New Student

  * Roll No
  * Name
  * Email
  * Course
  * Marks
* 📄 View All Students (using `Iterator`)
* 🔍 Search Student by **Name** (case-insensitive)
* 🗑️ Delete Student(s) by Name (with file update)
* 📊 Sort Students:

  * By **Marks (Descending)**
  * By **Name (Ascending)**
* 📂 Persistent Storage in `students.txt` (CSV format)
* 🎯 **Random Record Access** using `RandomAccessFile` and stored offsets
* 🎛️ Simple, menu-driven console interface

---

## 🧠 **Concepts Used**

### 🔹 Custom `Student` Class

* Fields: `roll`, `name`, `email`, `course`, `marks`
* Methods:

  * `toCSV()` → converts record to CSV format
  * `fromCSV(String line)` → parses a CSV line into a `Student` object
  * `toString()` → formatted display of student details

### 🔹 File Handling

* `BufferedWriter` + `FileWriter` →

  * `append()` → add a single student record
  * `saveAll()` → rewrite the entire file after deletions
* `RandomAccessFile` →

  * Reads file line-by-line
  * Records **byte offsets** of each student record
  * Enables **random access** to a specific student by index

### 🔹 Collections & Generics

* `ArrayList<Student>` → in-memory list of all students
* `ArrayList<Long>` → list of byte offsets for each record in the file
* `Iterator<Student>` → used in `viewAll()` to display all students

### 🔹 Sorting

* `sortByMarksDescending()`

  * Uses lambda: `students.sort((a, b) -> Double.compare(b.marks, a.marks));`
* `sortByNameAscending()`

  * Uses: `students.sort(Comparator.comparing(s -> s.name.toLowerCase()));`

### 🔹 Search & Delete

* `searchByName(String name)` → returns the first matching student (case-insensitive).
* `deleteByName(String name)` → removes matching students and rewrites `students.txt`.

### 🔹 Random Read Demo

* `demoRandomRead(Scanner sc)`:

  * Asks user for a record index (1…N)
  * Uses `RandomAccessFile` with stored offset to read that specific record directly from the file.

---

## ▶️ **How to Run**

### 1️⃣ Save the file as:

```bash
StudentRecordApp.java
```

### 2️⃣ Compile:

```bash
javac StudentRecordApp.java
```

### 3️⃣ Run:

```bash
java StudentRecordApp
```

The program will ensure **`students.txt`** exists in the current directory and then load any existing records.

---

## 📂 **File Used**

* **students.txt**

  * Stores each student in **CSV** format:

    ```text
    roll,name,email,course,marks
    ```
  * Example:

    ```text
    101,Ankit,ankit@mail.com,B.Tech,78.5
    ```

This file is automatically created and updated by the program. You don’t need to make it manually.

---

## 🧾 **Menu Options**

After running, you’ll see:

1. Add Student
2. View All Students
3. Search by Name
4. Delete by Name
5. Sort by Marks
6. Sort by Name
7. Demo Random Read (RandomAccessFile)
8. Save and Exit

---

## ✅ **Conclusion**

This project demonstrates:

* **Java File Handling (BufferedWriter, FileWriter, RandomAccessFile)**
* **ArrayList with Generics + Iterator**
* **Sorting with Comparators**
* **Search, Delete, and Random Access on File Records**
* **Menu-driven Student Record Management**

It is well-suited for **file handling–based Java lab work, internal assessments, and practical demonstrations**.


