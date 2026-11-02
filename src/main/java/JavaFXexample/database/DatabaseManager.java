package JavaFXexample.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseManager {
    private static final Logger log = LoggerFactory.getLogger(DatabaseManager.class);
    private static final String URL = "jdbc:sqlite:students.db";
    private static final int SCHEMA_VERSION = 4;

    public static Connection connect() throws SQLException {
        Connection conn = DriverManager.getConnection(URL);
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON");
            try (ResultSet rs = stmt.executeQuery("PRAGMA journal_mode = WAL")) {
                rs.next();
            }
        }
        return conn;
    }

    public static void initialize() {
        try (Connection conn = connect();
                Statement stmt = conn.createStatement()) {
            int version = getUserVersion(stmt);

            conn.setAutoCommit(false);
            try {
                if (version < 1) {
                    migrateTo1(stmt);
                }
                if (version < 2) {
                    migrateTo2(stmt);
                }
                if (version < 3) {
                    migrateTo3(stmt);
                }
                if (version < 4 || !tableExists(stmt, "curriculum")) {
                    migrateTo4(stmt);
                }
                setUserVersion(stmt, SCHEMA_VERSION);
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            log.error("Database initialization failed", e);
        }
    }

    private static boolean tableExists(Statement stmt, String table) throws SQLException {
        try (ResultSet rs = stmt.executeQuery("SELECT count(*) FROM sqlite_master WHERE type='table' AND name='" + table + "'")) {
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    private static void migrateTo1(Statement stmt) throws SQLException {
        stmt.execute("""
                CREATE TABLE IF NOT EXISTS students(
                    id INTEGER PRIMARY KEY,
                    name TEXT NOT NULL,
                    gpa REAL,
                    department TEXT
                )
                """);
        stmt.execute("""
                CREATE TABLE IF NOT EXISTS users(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT NOT NULL UNIQUE,
                    password_hash TEXT NOT NULL,
                    salt TEXT NOT NULL
                )
                """);
    }

    private static void migrateTo2(Statement stmt) throws SQLException {
        addColumnIfMissing(stmt, "students", "photo", "BLOB");
        addColumnIfMissing(stmt, "users", "photo", "BLOB");
    }

    private static void migrateTo3(Statement stmt) throws SQLException {
        addColumnIfMissing(stmt, "students", "advisor", "TEXT");
        addColumnIfMissing(stmt, "students", "phone", "TEXT");
        addColumnIfMissing(stmt, "students", "address", "TEXT");
        addColumnIfMissing(stmt, "students", "email", "TEXT");
    }

    private static void migrateTo4(Statement stmt) throws SQLException {
        stmt.execute("""
                CREATE TABLE IF NOT EXISTS curriculum(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    department TEXT NOT NULL,
                    academic_year INTEGER NOT NULL,
                    semester INTEGER NOT NULL,
                    course_code TEXT NOT NULL,
                    course_name TEXT NOT NULL,
                    credits INTEGER NOT NULL DEFAULT 3,
                    ects INTEGER NOT NULL DEFAULT 5,
                    course_type TEXT NOT NULL DEFAULT 'Compulsory'
                )
                """);
        stmt.execute("CREATE INDEX IF NOT EXISTS idx_curriculum_dept ON curriculum(department, academic_year, semester)");

        seedCurriculumData(stmt);
    }

    private static void seedCurriculumData(Statement stmt) throws SQLException {
        try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM curriculum")) {
            if (rs.next() && rs.getInt(1) > 0) {
                return;
            }
        }

        String sql = """
                INSERT INTO curriculum (department, academic_year, semester, course_code, course_name, credits, ects, course_type)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (java.sql.PreparedStatement pstmt = stmt.getConnection().prepareStatement(sql)) {

            // Computer Science
            addSeedCourse(pstmt, "Computer Science", 1, 1, "CS 101", "Introduction to Computer Science", 4, 6, "Compulsory");
            addSeedCourse(pstmt, "Computer Science", 1, 1, "MATH 101", "Calculus I", 4, 6, "Compulsory");
            addSeedCourse(pstmt, "Computer Science", 1, 1, "PHYS 101", "General Physics I", 4, 6, "Compulsory");
            addSeedCourse(pstmt, "Computer Science", 1, 1, "ENG 101", "Academic English I", 3, 4, "Compulsory");
            addSeedCourse(pstmt, "Computer Science", 1, 1, "GE 100", "Orientation Program", 1, 2, "Compulsory");
            addSeedCourse(pstmt, "Computer Science", 1, 1, "TURK 101", "Turkish Language I", 2, 2, "Compulsory");
            addSeedCourse(pstmt, "Computer Science", 1, 1, "HIST 101", "History of Modern Turkey I", 2, 4, "Compulsory");

            addSeedCourse(pstmt, "Computer Science", 1, 2, "CS 102", "Object-Oriented Programming", 4, 6, "Compulsory");
            addSeedCourse(pstmt, "Computer Science", 1, 2, "MATH 102", "Calculus II", 4, 6, "Compulsory");
            addSeedCourse(pstmt, "Computer Science", 1, 2, "MATH 106", "Discrete Mathematics", 3, 5, "Compulsory");
            addSeedCourse(pstmt, "Computer Science", 1, 2, "PHYS 102", "General Physics II", 4, 6, "Compulsory");
            addSeedCourse(pstmt, "Computer Science", 1, 2, "ENG 102", "Academic English II", 3, 4, "Compulsory");
            addSeedCourse(pstmt, "Computer Science", 1, 2, "TURK 102", "Turkish Language II", 2, 3, "Compulsory");

            addSeedCourse(pstmt, "Computer Science", 2, 1, "CS 201", "Data Structures", 4, 6, "Compulsory");
            addSeedCourse(pstmt, "Computer Science", 2, 1, "MATH 201", "Linear Algebra", 3, 5, "Compulsory");
            addSeedCourse(pstmt, "Computer Science", 2, 1, "CS 204", "Digital Logic Design", 4, 6, "Compulsory");
            addSeedCourse(pstmt, "Computer Science", 2, 1, "STAT 205", "Probability and Statistics", 3, 5, "Compulsory");
            addSeedCourse(pstmt, "Computer Science", 2, 1, "HUM 101", "Cultural Studies", 3, 5, "Elective");
            addSeedCourse(pstmt, "Computer Science", 2, 1, "ECO 101", "Principles of Economics", 3, 3, "Elective");

            addSeedCourse(pstmt, "Computer Science", 2, 2, "CS 202", "Analysis of Algorithms", 4, 6, "Compulsory");
            addSeedCourse(pstmt, "Computer Science", 2, 2, "CS 206", "Computer Organization", 4, 6, "Compulsory");
            addSeedCourse(pstmt, "Computer Science", 2, 2, "MATH 202", "Differential Equations", 3, 5, "Compulsory");
            addSeedCourse(pstmt, "Computer Science", 2, 2, "CS 210", "Principles of Software Development", 3, 5, "Compulsory");
            addSeedCourse(pstmt, "Computer Science", 2, 2, "TE 201", "Technical Elective I", 3, 5, "Elective");
            addSeedCourse(pstmt, "Computer Science", 2, 2, "COM 102", "Public Speaking", 2, 3, "Elective");

            addSeedCourse(pstmt, "Computer Science", 3, 1, "CS 301", "Database Systems", 4, 6, "Compulsory");
            addSeedCourse(pstmt, "Computer Science", 3, 1, "CS 303", "Operating Systems", 4, 6, "Compulsory");
            addSeedCourse(pstmt, "Computer Science", 3, 1, "CS 305", "Theory of Computation", 3, 5, "Compulsory");
            addSeedCourse(pstmt, "Computer Science", 3, 1, "TE 301", "Web Application Development", 3, 5, "Elective");
            addSeedCourse(pstmt, "Computer Science", 3, 1, "TE 302", "Artificial Intelligence", 3, 5, "Elective");
            addSeedCourse(pstmt, "Computer Science", 3, 1, "ETH 301", "Engineering Ethics", 2, 3, "Compulsory");

            addSeedCourse(pstmt, "Computer Science", 3, 2, "CS 302", "Software Engineering Principles", 4, 6, "Compulsory");
            addSeedCourse(pstmt, "Computer Science", 3, 2, "CS 304", "Computer Networks", 4, 6, "Compulsory");
            addSeedCourse(pstmt, "Computer Science", 3, 2, "CS 306", "Microprocessors & Embedded Systems", 4, 6, "Compulsory");
            addSeedCourse(pstmt, "Computer Science", 3, 2, "TE 303", "Machine Learning", 3, 5, "Elective");
            addSeedCourse(pstmt, "Computer Science", 3, 2, "TE 304", "Mobile Application Development", 3, 5, "Elective");
            addSeedCourse(pstmt, "Computer Science", 3, 2, "CS 399", "Summer Training", 1, 2, "Compulsory");

            addSeedCourse(pstmt, "Computer Science", 4, 1, "CS 401", "Senior Capstone Project I", 3, 7, "Compulsory");
            addSeedCourse(pstmt, "Computer Science", 4, 1, "CS 403", "Information Security & Cryptography", 3, 5, "Compulsory");
            addSeedCourse(pstmt, "Computer Science", 4, 1, "TE 401", "Cloud Computing & DevOps", 3, 6, "Elective");
            addSeedCourse(pstmt, "Computer Science", 4, 1, "TE 402", "Deep Learning & Neural Networks", 3, 6, "Elective");
            addSeedCourse(pstmt, "Computer Science", 4, 1, "GE 401", "Entrepreneurship & Innovation", 3, 6, "Elective");

            addSeedCourse(pstmt, "Computer Science", 4, 2, "CS 402", "Senior Capstone Project II", 3, 8, "Compulsory");
            addSeedCourse(pstmt, "Computer Science", 4, 2, "CS 404", "Parallel & Distributed Systems", 3, 6, "Compulsory");
            addSeedCourse(pstmt, "Computer Science", 4, 2, "TE 403", "Big Data Analytics", 3, 6, "Elective");
            addSeedCourse(pstmt, "Computer Science", 4, 2, "TE 404", "Natural Language Processing", 3, 5, "Elective");
            addSeedCourse(pstmt, "Computer Science", 4, 2, "FREE 402", "Free Elective", 3, 5, "Elective");

            // Software Engineering
            addSeedCourse(pstmt, "Software Engineering", 1, 1, "SE 101", "Introduction to Software Engineering", 4, 6, "Compulsory");
            addSeedCourse(pstmt, "Software Engineering", 1, 1, "MATH 101", "Calculus I", 4, 6, "Compulsory");
            addSeedCourse(pstmt, "Software Engineering", 1, 1, "PHYS 101", "General Physics I", 4, 6, "Compulsory");
            addSeedCourse(pstmt, "Software Engineering", 1, 1, "ENG 101", "Academic English I", 3, 4, "Compulsory");
            addSeedCourse(pstmt, "Software Engineering", 1, 1, "TURK 101", "Turkish Language I", 2, 4, "Compulsory");
            addSeedCourse(pstmt, "Software Engineering", 1, 1, "HIST 101", "History of Modern Turkey I", 2, 4, "Compulsory");

            addSeedCourse(pstmt, "Software Engineering", 1, 2, "SE 102", "Object Oriented Programming & Design", 4, 6, "Compulsory");
            addSeedCourse(pstmt, "Software Engineering", 1, 2, "MATH 102", "Calculus II", 4, 6, "Compulsory");
            addSeedCourse(pstmt, "Software Engineering", 1, 2, "MATH 106", "Discrete Mathematics", 3, 5, "Compulsory");
            addSeedCourse(pstmt, "Software Engineering", 1, 2, "PHYS 102", "General Physics II", 4, 6, "Compulsory");
            addSeedCourse(pstmt, "Software Engineering", 1, 2, "ENG 102", "Academic English II", 3, 4, "Compulsory");
            addSeedCourse(pstmt, "Software Engineering", 1, 2, "TURK 102", "Turkish Language II", 2, 3, "Compulsory");

            addSeedCourse(pstmt, "Software Engineering", 2, 1, "SE 201", "Data Structures & Algorithms", 4, 6, "Compulsory");
            addSeedCourse(pstmt, "Software Engineering", 2, 1, "SE 203", "Software Requirements Engineering", 3, 6, "Compulsory");
            addSeedCourse(pstmt, "Software Engineering", 2, 1, "MATH 201", "Linear Algebra", 3, 5, "Compulsory");
            addSeedCourse(pstmt, "Software Engineering", 2, 1, "STAT 205", "Probability and Statistics", 3, 5, "Compulsory");
            addSeedCourse(pstmt, "Software Engineering", 2, 1, "SE 205", "Human-Computer Interaction", 3, 5, "Elective");

            addSeedCourse(pstmt, "Software Engineering", 2, 2, "SE 202", "Software Architecture & Design Patterns", 4, 6, "Compulsory");
            addSeedCourse(pstmt, "Software Engineering", 2, 2, "SE 204", "Database Systems Design", 4, 6, "Compulsory");
            addSeedCourse(pstmt, "Software Engineering", 2, 2, "CS 206", "Computer Systems & Architecture", 3, 5, "Compulsory");
            addSeedCourse(pstmt, "Software Engineering", 2, 2, "SE 208", "Web Technologies & Architecture", 3, 5, "Compulsory");
            addSeedCourse(pstmt, "Software Engineering", 2, 2, "TE 202", "Technical Elective I", 3, 5, "Elective");

            addSeedCourse(pstmt, "Software Engineering", 3, 1, "SE 301", "Software Testing, Verification & QA", 4, 6, "Compulsory");
            addSeedCourse(pstmt, "Software Engineering", 3, 1, "SE 303", "Operating Systems", 4, 6, "Compulsory");
            addSeedCourse(pstmt, "Software Engineering", 3, 1, "SE 305", "Agile Project Management & DevOps", 3, 5, "Compulsory");
            addSeedCourse(pstmt, "Software Engineering", 3, 1, "TE 301", "Cloud Native Application Design", 3, 5, "Elective");
            addSeedCourse(pstmt, "Software Engineering", 3, 1, "ETH 301", "Engineering Ethics", 2, 3, "Compulsory");

            addSeedCourse(pstmt, "Software Engineering", 3, 2, "SE 302", "Software Security & Secure Coding", 3, 5, "Compulsory");
            addSeedCourse(pstmt, "Software Engineering", 3, 2, "CS 304", "Computer Networks & Protocols", 4, 6, "Compulsory");
            addSeedCourse(pstmt, "Software Engineering", 3, 2, "SE 306", "Distributed & Service-Oriented Systems", 4, 6, "Compulsory");
            addSeedCourse(pstmt, "Software Engineering", 3, 2, "TE 304", "Full Stack Development", 3, 5, "Elective");
            addSeedCourse(pstmt, "Software Engineering", 3, 2, "SE 399", "Summer Internship", 1, 2, "Compulsory");

            addSeedCourse(pstmt, "Software Engineering", 4, 1, "SE 401", "Software Engineering Capstone I", 3, 8, "Compulsory");
            addSeedCourse(pstmt, "Software Engineering", 4, 1, "TE 401", "Microservices Architecture", 3, 6, "Elective");
            addSeedCourse(pstmt, "Software Engineering", 4, 1, "TE 403", "Machine Learning for Engineers", 3, 6, "Elective");
            addSeedCourse(pstmt, "Software Engineering", 4, 1, "GE 401", "Technology Entrepreneurship", 3, 5, "Elective");

            addSeedCourse(pstmt, "Software Engineering", 4, 2, "SE 402", "Software Engineering Capstone II", 3, 8, "Compulsory");
            addSeedCourse(pstmt, "Software Engineering", 4, 2, "TE 402", "Mobile Application Systems", 3, 6, "Elective");
            addSeedCourse(pstmt, "Software Engineering", 4, 2, "TE 404", "Enterprise Application Integration", 3, 6, "Elective");
            addSeedCourse(pstmt, "Software Engineering", 4, 2, "FREE 402", "Free Elective", 3, 5, "Elective");

            // Business Administration
            addSeedCourse(pstmt, "Business Administration", 1, 1, "BUS 101", "Introduction to Business Management", 3, 6, "Compulsory");
            addSeedCourse(pstmt, "Business Administration", 1, 1, "ECON 101", "Microeconomics", 3, 6, "Compulsory");
            addSeedCourse(pstmt, "Business Administration", 1, 1, "MATH 105", "Business Mathematics", 3, 6, "Compulsory");
            addSeedCourse(pstmt, "Business Administration", 1, 1, "ENG 101", "Business English I", 3, 4, "Compulsory");
            addSeedCourse(pstmt, "Business Administration", 1, 1, "TURK 101", "Turkish Language I", 2, 4, "Compulsory");
            addSeedCourse(pstmt, "Business Administration", 1, 1, "HIST 101", "History of Modern Turkey I", 2, 4, "Compulsory");

            addSeedCourse(pstmt, "Business Administration", 1, 2, "BUS 102", "Organizational Behavior", 3, 6, "Compulsory");
            addSeedCourse(pstmt, "Business Administration", 1, 2, "ECON 102", "Macroeconomics", 3, 6, "Compulsory");
            addSeedCourse(pstmt, "Business Administration", 1, 2, "ACC 101", "Financial Accounting I", 3, 6, "Compulsory");
            addSeedCourse(pstmt, "Business Administration", 1, 2, "STAT 102", "Business Statistics", 3, 5, "Compulsory");
            addSeedCourse(pstmt, "Business Administration", 1, 2, "ENG 102", "Business English II", 3, 4, "Compulsory");
            addSeedCourse(pstmt, "Business Administration", 1, 2, "TURK 102", "Turkish Language II", 2, 3, "Compulsory");

            addSeedCourse(pstmt, "Business Administration", 2, 1, "ACC 201", "Financial Accounting II", 3, 6, "Compulsory");
            addSeedCourse(pstmt, "Business Administration", 2, 1, "MKTG 201", "Principles of Marketing", 3, 6, "Compulsory");
            addSeedCourse(pstmt, "Business Administration", 2, 1, "FIN 201", "Business Finance", 3, 6, "Compulsory");
            addSeedCourse(pstmt, "Business Administration", 2, 1, "LAW 201", "Business Law", 3, 5, "Compulsory");
            addSeedCourse(pstmt, "Business Administration", 2, 1, "ELECT 201", "Management Information Systems", 3, 5, "Elective");

            addSeedCourse(pstmt, "Business Administration", 2, 2, "MKTG 202", "Marketing Management & Strategy", 3, 6, "Compulsory");
            addSeedCourse(pstmt, "Business Administration", 2, 2, "FIN 202", "Corporate Financial Management", 3, 6, "Compulsory");
            addSeedCourse(pstmt, "Business Administration", 2, 2, "OPS 202", "Operations & Supply Chain Management", 3, 6, "Compulsory");
            addSeedCourse(pstmt, "Business Administration", 2, 2, "HR 202", "Human Resource Management", 3, 5, "Compulsory");
            addSeedCourse(pstmt, "Business Administration", 2, 2, "ELECT 202", "International Business", 3, 5, "Elective");

            addSeedCourse(pstmt, "Business Administration", 3, 1, "STRAT 301", "Strategic Management", 3, 6, "Compulsory");
            addSeedCourse(pstmt, "Business Administration", 3, 1, "RES 301", "Business Research Methods", 3, 6, "Compulsory");
            addSeedCourse(pstmt, "Business Administration", 3, 1, "FIN 301", "Investment Analysis & Portfolio", 3, 6, "Elective");
            addSeedCourse(pstmt, "Business Administration", 3, 1, "MKTG 301", "Digital Marketing & Analytics", 3, 6, "Elective");
            addSeedCourse(pstmt, "Business Administration", 3, 1, "ETH 301", "Business Ethics & CSR", 2, 4, "Compulsory");

            addSeedCourse(pstmt, "Business Administration", 3, 2, "BUS 302", "Managerial Economics", 3, 6, "Compulsory");
            addSeedCourse(pstmt, "Business Administration", 3, 2, "OPS 302", "Quality Management & Six Sigma", 3, 6, "Compulsory");
            addSeedCourse(pstmt, "Business Administration", 3, 2, "ENT 302", "Entrepreneurship & Small Business", 3, 6, "Elective");
            addSeedCourse(pstmt, "Business Administration", 3, 2, "FIN 302", "Financial Markets & Banking", 3, 6, "Elective");
            addSeedCourse(pstmt, "Business Administration", 3, 2, "BUS 399", "Summer Internship", 1, 2, "Compulsory");

            addSeedCourse(pstmt, "Business Administration", 4, 1, "CAP 401", "Strategic Consulting Project I", 3, 8, "Compulsory");
            addSeedCourse(pstmt, "Business Administration", 4, 1, "ELECT 401", "Global Marketing Strategy", 3, 6, "Elective");
            addSeedCourse(pstmt, "Business Administration", 4, 1, "ELECT 403", "Corporate Governance & Leadership", 3, 6, "Elective");
            addSeedCourse(pstmt, "Business Administration", 4, 1, "ELECT 405", "Risk Management & Derivatives", 3, 6, "Elective");

            addSeedCourse(pstmt, "Business Administration", 4, 2, "CAP 402", "Strategic Consulting Project II", 3, 8, "Compulsory");
            addSeedCourse(pstmt, "Business Administration", 4, 2, "ELECT 402", "Innovation Management & Design", 3, 6, "Elective");
            addSeedCourse(pstmt, "Business Administration", 4, 2, "ELECT 404", "Negotiation & Conflict Resolution", 3, 6, "Elective");
            addSeedCourse(pstmt, "Business Administration", 4, 2, "FREE 402", "Free Elective", 3, 5, "Elective");

            // Architecture
            addSeedCourse(pstmt, "Architecture", 1, 1, "ARCH 101", "Basic Design Studio I", 6, 10, "Compulsory");
            addSeedCourse(pstmt, "Architecture", 1, 1, "ARCH 111", "Architectural Drawing & Representation I", 3, 5, "Compulsory");
            addSeedCourse(pstmt, "Architecture", 1, 1, "ARCH 121", "Introduction to Architectural History", 3, 5, "Compulsory");
            addSeedCourse(pstmt, "Architecture", 1, 1, "MATH 101", "Calculus for Architects", 3, 4, "Compulsory");
            addSeedCourse(pstmt, "Architecture", 1, 1, "ENG 101", "Academic English I", 3, 3, "Compulsory");
            addSeedCourse(pstmt, "Architecture", 1, 1, "TURK 101", "Turkish Language I", 2, 3, "Compulsory");

            addSeedCourse(pstmt, "Architecture", 1, 2, "ARCH 102", "Basic Design Studio II", 6, 10, "Compulsory");
            addSeedCourse(pstmt, "Architecture", 1, 2, "ARCH 112", "Architectural Drawing & Representation II", 3, 5, "Compulsory");
            addSeedCourse(pstmt, "Architecture", 1, 2, "ARCH 122", "History of Architecture: Antiquity", 3, 5, "Compulsory");
            addSeedCourse(pstmt, "Architecture", 1, 2, "PHYS 101", "Physics for Architects", 3, 4, "Compulsory");
            addSeedCourse(pstmt, "Architecture", 1, 2, "ENG 102", "Academic English II", 3, 3, "Compulsory");
            addSeedCourse(pstmt, "Architecture", 1, 2, "TURK 102", "Turkish Language II", 2, 3, "Compulsory");

            addSeedCourse(pstmt, "Architecture", 2, 1, "ARCH 201", "Architectural Design Studio I", 6, 10, "Compulsory");
            addSeedCourse(pstmt, "Architecture", 2, 1, "ARCH 231", "Building Materials & Construction", 3, 5, "Compulsory");
            addSeedCourse(pstmt, "Architecture", 2, 1, "ARCH 221", "Medieval & Renaissance Architecture", 3, 5, "Compulsory");
            addSeedCourse(pstmt, "Architecture", 2, 1, "ARCH 241", "Structural Principles in Architecture", 3, 5, "Compulsory");
            addSeedCourse(pstmt, "Architecture", 2, 1, "ELECT 201", "Digital Modeling & BIM I", 3, 5, "Elective");

            addSeedCourse(pstmt, "Architecture", 2, 2, "ARCH 202", "Architectural Design Studio II", 6, 10, "Compulsory");
            addSeedCourse(pstmt, "Architecture", 2, 2, "ARCH 232", "Building Technologies & Systems", 3, 5, "Compulsory");
            addSeedCourse(pstmt, "Architecture", 2, 2, "ARCH 222", "Modern Architecture History", 3, 5, "Compulsory");
            addSeedCourse(pstmt, "Architecture", 2, 2, "ARCH 242", "Environmental Control Systems", 3, 5, "Compulsory");
            addSeedCourse(pstmt, "Architecture", 2, 2, "ELECT 202", "Computational Design & Parametric", 3, 5, "Elective");

            addSeedCourse(pstmt, "Architecture", 3, 1, "ARCH 301", "Architectural Design Studio III", 6, 10, "Compulsory");
            addSeedCourse(pstmt, "Architecture", 3, 1, "ARCH 351", "Urban Design & City Planning", 3, 5, "Compulsory");
            addSeedCourse(pstmt, "Architecture", 3, 1, "ARCH 331", "Advanced Structural Systems", 3, 5, "Compulsory");
            addSeedCourse(pstmt, "Architecture", 3, 1, "ELECT 301", "Sustainable Architecture & Energy", 3, 5, "Elective");
            addSeedCourse(pstmt, "Architecture", 3, 1, "ELECT 303", "Architectural Photography", 2, 3, "Elective");

            addSeedCourse(pstmt, "Architecture", 3, 2, "ARCH 302", "Architectural Design Studio IV", 6, 10, "Compulsory");
            addSeedCourse(pstmt, "Architecture", 3, 2, "ARCH 352", "Restoration & Heritage Conservation", 3, 5, "Compulsory");
            addSeedCourse(pstmt, "Architecture", 3, 2, "ARCH 332", "Building Acoustics & Lighting", 3, 5, "Compulsory");
            addSeedCourse(pstmt, "Architecture", 3, 2, "ELECT 302", "Interior Architectural Systems", 3, 5, "Elective");
            addSeedCourse(pstmt, "Architecture", 3, 2, "ARCH 399", "Office & Site Practice", 1, 2, "Compulsory");

            addSeedCourse(pstmt, "Architecture", 4, 1, "ARCH 401", "Architectural Design Studio V", 6, 12, "Compulsory");
            addSeedCourse(pstmt, "Architecture", 4, 1, "ARCH 461", "Professional Practice & Project Mgmt", 3, 5, "Compulsory");
            addSeedCourse(pstmt, "Architecture", 4, 1, "ELECT 401", "Contemporary Architectural Theory", 3, 5, "Elective");
            addSeedCourse(pstmt, "Architecture", 4, 1, "ELECT 403", "Housing Studies & Policy", 3, 5, "Elective");

            addSeedCourse(pstmt, "Architecture", 4, 2, "ARCH 402", "Diploma Graduation Project", 8, 15, "Compulsory");
            addSeedCourse(pstmt, "Architecture", 4, 2, "ELECT 402", "Portfolio & Exhibition Design", 3, 6, "Elective");
            addSeedCourse(pstmt, "Architecture", 4, 2, "ELECT 404", "Building Economics & Feasibility", 3, 5, "Elective");
            addSeedCourse(pstmt, "Architecture", 4, 2, "FREE 402", "Free Elective", 2, 4, "Elective");

            // Seed foundation courses for the rest of departments
            String[] otherDepts = {
                "Civil Engineering", "Communication Studies", "Dentistry", "Economics",
                "Electrical and Electronics Engineering", "English Language and Literature",
                "Fine Arts", "Industrial Engineering", "International Relations", "Law",
                "Mechanical Engineering", "Medicine", "Molecular Biology and Genetics",
                "Nursing", "Pharmacy", "Psychology"
            };

            for (String dept : otherDepts) {
                String prefix = dept.replaceAll("[^A-Z]", "");
                if (prefix.length() < 2) prefix = "DEPT";
                if (prefix.length() > 4) prefix = prefix.substring(0, 4);

                addSeedCourse(pstmt, dept, 1, 1, prefix + " 101", "Introduction to " + dept, 3, 6, "Compulsory");
                addSeedCourse(pstmt, dept, 1, 1, "MATH 101", "General Mathematics I", 3, 5, "Compulsory");
                addSeedCourse(pstmt, dept, 1, 1, "ENG 101", "Academic English I", 3, 4, "Compulsory");
                addSeedCourse(pstmt, dept, 1, 1, "TURK 101", "Turkish Language I", 2, 3, "Compulsory");
                addSeedCourse(pstmt, dept, 1, 1, "HIST 101", "History of Modern Turkey I", 2, 4, "Compulsory");
                addSeedCourse(pstmt, dept, 1, 1, "INFO 101", "Computer Literacy & Skills", 2, 3, "Compulsory");

                addSeedCourse(pstmt, dept, 1, 2, prefix + " 102", "Foundations of " + dept, 3, 6, "Compulsory");
                addSeedCourse(pstmt, dept, 1, 2, prefix + " 104", "Research Methodologies", 3, 6, "Compulsory");
                addSeedCourse(pstmt, dept, 1, 2, "STAT 102", "Basic Statistics", 3, 5, "Compulsory");
                addSeedCourse(pstmt, dept, 1, 2, "ENG 102", "Academic English II", 3, 4, "Compulsory");
                addSeedCourse(pstmt, dept, 1, 2, "TURK 102", "Turkish Language II", 2, 3, "Compulsory");

                addSeedCourse(pstmt, dept, 2, 1, prefix + " 201", "Core Concepts in " + dept + " I", 4, 6, "Compulsory");
                addSeedCourse(pstmt, dept, 2, 1, prefix + " 203", "Applied Theory I", 3, 6, "Compulsory");
                addSeedCourse(pstmt, dept, 2, 1, prefix + " 205", "Departmental Elective I", 3, 5, "Elective");
                addSeedCourse(pstmt, dept, 2, 1, "HUM 201", "Ethics and Professional Responsibility", 2, 4, "Compulsory");

                addSeedCourse(pstmt, dept, 2, 2, prefix + " 202", "Core Concepts in " + dept + " II", 4, 6, "Compulsory");
                addSeedCourse(pstmt, dept, 2, 2, prefix + " 204", "Applied Theory II", 3, 6, "Compulsory");
                addSeedCourse(pstmt, dept, 2, 2, prefix + " 206", "Departmental Elective II", 3, 5, "Elective");
                addSeedCourse(pstmt, dept, 2, 2, "SCI 202", "Science, Technology & Society", 3, 5, "Elective");

                addSeedCourse(pstmt, dept, 3, 1, prefix + " 301", "Advanced Studies in " + dept + " I", 4, 7, "Compulsory");
                addSeedCourse(pstmt, dept, 3, 1, prefix + " 303", "Special Topics I", 3, 6, "Elective");
                addSeedCourse(pstmt, dept, 3, 1, prefix + " 305", "Field Research & Practice", 3, 6, "Compulsory");
                addSeedCourse(pstmt, dept, 3, 1, "FREE 301", "University Free Elective I", 3, 5, "Elective");

                addSeedCourse(pstmt, dept, 3, 2, prefix + " 302", "Advanced Studies in " + dept + " II", 4, 7, "Compulsory");
                addSeedCourse(pstmt, dept, 3, 2, prefix + " 304", "Special Topics II", 3, 6, "Elective");
                addSeedCourse(pstmt, dept, 3, 2, prefix + " 399", "Professional Internship", 1, 3, "Compulsory");
                addSeedCourse(pstmt, dept, 3, 2, "FREE 302", "University Free Elective II", 3, 5, "Elective");

                addSeedCourse(pstmt, dept, 4, 1, prefix + " 401", "Senior Graduation Project I", 4, 10, "Compulsory");
                addSeedCourse(pstmt, dept, 4, 1, prefix + " 403", "Contemporary Issues in " + dept, 3, 6, "Elective");
                addSeedCourse(pstmt, dept, 4, 1, prefix + " 405", "Advanced Seminar I", 3, 6, "Elective");

                addSeedCourse(pstmt, dept, 4, 2, prefix + " 402", "Senior Graduation Project II", 4, 12, "Compulsory");
                addSeedCourse(pstmt, dept, 4, 2, prefix + " 404", "Advanced Seminar II", 3, 6, "Elective");
                addSeedCourse(pstmt, dept, 4, 2, prefix + " 406", "Professional Portfolio", 2, 4, "Compulsory");
            }

            pstmt.executeBatch();
        }
    }

    private static void addSeedCourse(java.sql.PreparedStatement pstmt, String dept, int year, int sem,
                                      String code, String name, int credits, int ects, String type) throws SQLException {
        pstmt.setString(1, dept);
        pstmt.setInt(2, year);
        pstmt.setInt(3, sem);
        pstmt.setString(4, code);
        pstmt.setString(5, name);
        pstmt.setInt(6, credits);
        pstmt.setInt(7, ects);
        pstmt.setString(8, type);
        pstmt.addBatch();
    }

    private static void addColumnIfMissing(Statement stmt, String table, String column, String type)
            throws SQLException {
        if (columnExists(stmt, table, column)) {
            return;
        }
        stmt.execute("ALTER TABLE " + table + " ADD COLUMN " + column + " " + type);
    }

    private static boolean columnExists(Statement stmt, String table, String column) throws SQLException {
        try (ResultSet rs = stmt.executeQuery("PRAGMA table_info(" + table + ")")) {
            while (rs.next()) {
                if (column.equalsIgnoreCase(rs.getString("name"))) {
                    return true;
                }
            }
        }
        return false;
    }

    private static int getUserVersion(Statement stmt) throws SQLException {
        try (ResultSet rs = stmt.executeQuery("PRAGMA user_version")) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    private static void setUserVersion(Statement stmt, int version) throws SQLException {
        stmt.execute("PRAGMA user_version = " + version);
    }
}
