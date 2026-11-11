# BudgetBuddy SA 🇿🇦

### Personal Budget and Expense Management Android Application

BudgetBuddy SA is an Android-based personal finance management application designed to help users manage their money, monitor expenses, create budgets and track savings goals.

The application aims to encourage responsible financial habits by providing users with tools to monitor their spending and understand their financial progress.

## 📱 Application Features

- **User Authentication:** Register and log in using email and password or Google Single Sign-On.
- **Dashboard:** View financial information and access the application's main features.
- **Expense Management:** Add and manage personal expenses.
- **Budget Management:** Create budgets and monitor spending.
- **Budget Notifications:** Receive alerts when spending reaches 80% and 100% of the allocated budget.
- **Savings Goals:** Set savings targets and monitor progress.
- **Categories:** Organise expenses into different categories.
- **Achievements:** Track financial progress and achievements.
- **Offline Support:** Store application data locally using Room Database.
- **Cloud Synchronisation:** Synchronise expense information with Firebase Firestore when an internet connection is available.
- **Push Notifications:** Receive notifications through Firebase Cloud Messaging.
- **Multilingual Support:** English and Afrikaans language options.

## 🛠️ Technologies Used

| Technology | Purpose |
|---|---|
| Kotlin | Main programming language |
| Android Studio | Android development environment |
| XML | User interface design |
| Room Database | Local data storage |
| Firebase Authentication | User authentication |
| Google Sign-In | Single Sign-On |
| Firebase Firestore | Cloud database and synchronisation |
| Firebase Cloud Messaging | Push notifications |
| WorkManager | Background synchronisation |
| GitHub | Version control and source code management |
| GitHub Actions | Automated build workflow |

## 🏗️ Application Architecture

BudgetBuddy SA uses an Android application structure with separate components for:

- User interface and activities.
- Local database operations.
- Firebase authentication.
- Cloud data management.
- Background synchronisation.
- Notification management.

Room Database provides local storage, while Firebase Firestore supports cloud synchronisation.

## 📋 Requirements

To run the project, you need:

- Android Studio.
- Android SDK 36.1.
- Kotlin support.
- JDK 21 or a compatible configured JDK.
- An Android device or emulator running Android 8.0 or later.
- Internet connection for Firebase services.

## 🚀 Installation and Setup

1. Clone the repository:

   ```bash
   git clone https://github.com/Ndzali-sudo/BudgetBuddySA.git
   ```

2. Open Android Studio.

3. Select **Open** and choose the cloned BudgetBuddySA project folder.

4. Allow Gradle to synchronise the project.

5. Configure your own Firebase project and register the Android application using:

   ```text
   com.budgetbuddysa.app
   ```

6. Download the Firebase configuration file (`google-services.json`) and place it inside the `app` directory.

7. Enable the required Firebase services:
    - Authentication
    - Google Sign-In
    - Cloud Firestore
    - Cloud Messaging

8. Run the application on an Android device or emulator.

## 🧪 Testing

The application is developed and tested using Android Studio.

Testing includes:

- User registration and login.
- Google Single Sign-On.
- Expense management.
- Budget creation and tracking.
- Budget threshold notifications.
- Savings goal functionality.
- English and Afrikaans language switching.
- Local database functionality.
- Firebase notification delivery.
- Application build verification.

The project also includes a GitHub Actions workflow for automated build checks.

## 📦 Release Build

A signed release APK has been generated for BudgetBuddy SA.

The application can be built through Android Studio using:

**Build → Generate Signed Bundle / APK**

Select the release build variant to generate the release APK.

## 👩‍💻 Developer

**Name:** Ndzalama Nkanyana  
**Student Number:** ST10450463  
**Institution:** Rosebank International University  
**Qualification:** Diploma in Information Technology  
**Project:** BudgetBuddy SA

## 🎯 Project Purpose

BudgetBuddy SA was developed as part of an Information Technology practical project to demonstrate Android application development, database management, authentication, cloud integration, notifications, offline functionality and multilingual support

## Application Screenshots

1. Login Screen
2. Registration Screen
3. Dashboard
4.  4. Expenses
5. Add Expense
6. Budget Management
7. Savings
8. Categories
9. Achievements
10. Settings
11. Notifications


YOUTUBE DEMONSTRATION VIDEO LINK▶️
https://youtube.com/shorts/xZvpvSC3krY?si=HMN2hGl8rcMbAkG4





