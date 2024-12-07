# Syntax Checker SaaS

This project is a **Syntax Checker SaaS** built with **Spring Boot**, providing a REST API to validate Java assignment statements. It includes a user-friendly UI and robust backend validation with exception handling.

---

## Features
- **Syntax Validation**: Checks the validity of Java assignment statements.
- **Rate Limiting**: Limits API calls to 10 per minute for reliable usage.
- **Error Handling**: Provides clear error messages for invalid syntax.
- **User-Friendly UI**: A simple and responsive interface to test the syntax checker.
- **Postman API Testing**: Allows direct testing of the backend using Postman.

---

## Team
- **Lyle**: Systems Analyst
- **Joaquin**: Development Lead
- **Ansel**: Quality Assurance Lead and Developer

---

## Setup Instructions

### **1. Spring Initializer**
The project was created using **Spring Initializer** with the following configuration:
- **Project**: Maven
- **Language**: Java
- **Spring Boot Version**: 3.x
- **Packaging**: Jar
- **Java Version**: 17
- **Dependencies**:
  - Spring Web
  - Spring Boot DevTools
  - Spring Boot Actuator

---

### **2. Import into IDE**
1. Extract the downloaded project from Spring Initializer.
2. Open your IDE (e.g., Eclipse or IntelliJ IDEA).
3. Import the project as a Maven project.

---

### **3. Backend Implementation**
The backend contains the following key components:
- **SyntaxChecker.java**: Handles syntax validation logic with added exception handling for seamless error management.
- **SyntaxCheckerApplication.java**: The main entry point for the Spring Boot application.
- **SyntaxCheckerController.java**: Exposes REST API endpoints for syntax validation and API stats.
- **SyntaxCheckerRequest.java**: Represents incoming request payloads.
- **SyntaxCheckerService.java**: Implements business logic, including rate-limiting and API call tracking.

---

### **4. Frontend Implementation**
To interact with the syntax checker, the project includes:
- **index.html**: Provides a user interface to input and test code.
- **style.css**: Adds styling for a clean and modern look.
- **script.js**: Handles API requests and dynamically updates the UI with results.

---

### **5. Run the Application**
1. Open a terminal or IDE terminal and navigate to the project directory.
2. Build the project using Maven:
   ```bash
   mvn clean install
