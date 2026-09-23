# BudgetBuddy SA

## Personal Financial Management Mobile Application

BudgetBuddy SA is a Kotlin-based Android application designed to help users manage their personal finances.

## Features

- User registration and login
- Expense tracking
- Expense categories
- Budget management
- Savings goals
- Achievements and gamification
- Dashboard with financial summaries
- Offline data storage
- REST API integration
- Room Database
- Unit testing
- GitHub Actions CI

## Technologies Used

- Kotlin
- Android Studio
- Android SDK
- Room Database
- REST API
- Spring Boot
- Gradle
- GitHub
- GitHub Actions

## Database

Room Database is used for local storage of:

- Expenses
- Budgets
- Savings goals

The application can continue storing financial information when the REST API is unavailable.

## Testing

The project includes unit tests for:

- Budget
- Expenses
- Savings

GitHub Actions automatically builds and tests the project.

## API

The backend was developed using Spring Boot and provides REST endpoints for:

- Expenses
- Budgets
- Savings
- Health checking

## Build

The Android APK can be generated from Android Studio using:

Build → Generate App Bundles or APKs → Generate APKs
