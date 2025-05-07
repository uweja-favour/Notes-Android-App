# 📓 JetNotes – Modern Notes App with Jetpack Compose

Notes-App is a sleek, minimalistic notes application built entirely with **Jetpack Compose**. It provides a modern, responsive UI and intuitive user experience following the latest Android development best practices. This project demonstrates the application of **clean architecture**, **state management**, **MVVM design pattern**, and **Jetpack libraries** in a real-world Android app.

---

## 🛠️ Tech Stack

- **Kotlin** – Modern, concise programming language for Android.
- **Jetpack Compose** – Declarative UI toolkit for native Android.
- **MVVM Architecture** – Separation of concerns and reactive UI.
- **Room** – Local database persistence.
- **Hilt** – Dependency Injection.
- **Coroutines & Flow** – Asynchronous and reactive data handling.
- **Material 3** – Google’s latest design system.

---

## ✨ Features

- Create, edit, and delete notes
- Real-time search functionality
- Sort notes by date or title
- Dark & Light theme support
- Responsive UI for various screen sizes
- Local data storage using Room
- Smooth animations and transitions using Compose

---

## 📱 UI Screens

- Home Screen (List of notes)
- Add/Edit Note Screen
- Search bar with dynamic results
- Empty state illustration
- Snackbar feedback

---

## 📐 Architecture

Notes-App follows the **MVVM pattern** with a **unidirectional data flow**, ensuring separation of concerns, testability, and scalability.

```text
| UI Layer     | Jetpack Compose, ViewModel (State Management) |
|--------------|-----------------------------------------------|
| Domain Layer | Use Cases                                     |
| Data Layer   | Repository, Room DAO, Local DB                |
