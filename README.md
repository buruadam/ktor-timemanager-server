# 🌐 Timemanager Server - Backend API

## 📖 Overview

The **Timemanager Server** is my server-side logic for my [Timemanager Android Application](https://github.com/buruadam/Timemanager), developed as part of my Thesis Project. It provides a robust REST API for managing schedules, user data, and synchronization.

## 🛠️ Tech Stack

*   **Language:** Kotlin
*   **Framework:** Ktor (Netty engine)
*   **Database:** H2 (Embedded)
*   **ORM:** Exposed
*   **Serialization:** Kotlinx Serialization (JSON)
*   **Testing:** MockK & JUnit 5 (unit tests)

## ✨ Key Features

- **User Management:** Secure handling of authentication and safely stored user data.
- **Task & Schedule API:** Full CRUD operations to the tasks created by the users.
- **Persistent Storage:** Efficient data management using the Exposed ORM with a relational database.
- **Android Integration:** Optimized JSON responses designed specifically for mobile client.

## 🚀 Getting Started

### 📦 Prerequisites
*   JDK 11 or higher
*   [Git](https://git-scm.com)
*   [IntelliJ IDEA](https://www.jetbrains.com/idea/) (Recommended IDE)

### ⚙️ Installation & Running
1. Clone the repository:
   ```
   git clone https://github.com/buruadam/ktor-timemanager-server.git
   ```

2.  Open the project in **IntelliJ IDEA** and wait for the **Gradle Sync** to complete.

3. **Run** the server using **Gradle**:
   ```
   ./gradlew run
   ```

The server will start locally at <http://localhost:8081>.

**Note:** Once the server is running, you can set up the mobile client by following the instructions in the [Timemanager Android Application](https://github.com/buruadam/Timemanager) repository.

## 📜 License

This project is licensed under the **MIT License**. See the [LICENSE](LICENSE) file for details.
