# Memoir

Memoir is a personal expression app for Android that allows users to create, update, delete posts,
and share media. The app provides a platform for users to express themselves through posts, images,
and other content. It also incorporates enhanced security features like Two-Factor Authentication (
2FA) for secure login and Firebase for user management and storage.

## Features

- **Create Posts**: Users can create and share posts with text, images, and other media.
- **Update and Delete Posts**: Edit or remove your posts with ease.
- **Image Uploads**: Upload and display images as part of your posts, stored in Firebase Storage.
- **Two-Factor Authentication (2FA)**: Enhanced security using a 2FA system that requires users to
  verify their identity via email before accessing their account.
- **Firebase Authentication**: Seamless login and registration with Firebase Authentication.
- **Firebase Storage**: Store and retrieve images, videos, and documents for user-generated content.
- **Email Integration**: Secure SMTP configuration for sending verification and other emails
  programmatically.
- **Modern UI/UX**: Built with modern Android UI components for a smooth and intuitive user
  experience.

## Getting Started

These instructions will help you set up **Memoir** on your local development machine for testing and
development purposes.

### Prerequisites

- **Android Studio**: Make sure you have Android Studio installed. Download it
  from [here](https://developer.android.com/studio).
- **Java 11**: Ensure you have Java 11 installed. You can download it
  from [AdoptOpenJDK](https://adoptopenjdk.net/).
- **Gradle**: Memoir uses Gradle for dependency management and build automation.

### Setup

1. Clone the repository:

    ```bash
    git clone https://github.com/ArditZubaku/memoir.git
    cd memoir
    ```

2. Open the project in Android Studio.

3. Add your secret configuration to `env.properties` in the project root:

    ```properties
    smtp_sender_email=your-email@example.com
    smtp_sender_password=your-email-password
    ```

4. Sync the project to download dependencies.

5. Build and run the project:

    ```bash
    ./gradlew clean build
    ```

### Configuration

- **SMTP Configuration**:
    - Email and password values are fetched securely from the `env.properties` file and injected
      into the `BuildConfig` for use during the build process.
    - Make sure your `env.properties` file is not committed to version control. Consider adding it
      to `.gitignore`.

- **Two-Factor Authentication (2FA)**:
    - The app integrates 2FA for enhanced security during login.
    - Implemented through Firebase Authentication and custom email sending for token verification.

- **Post and Image Management**:
    - Users can create posts, update, and delete them.
    - Image uploads are handled through Firebase Storage and are linked to posts.

### Environment Variables

Make sure to securely store sensitive information, such as API keys and passwords, outside of
version control by using the `env.properties` file or equivalent method.

## Technologies Used

- **Firebase Authentication**: For handling user authentication.
- **Firebase Firestore**: For storing user posts, data, and app information.
- **Firebase Storage**: For storing user-uploaded images, videos, and documents.
- **Android SDK**: Native Android development environment.
- **Gradle**: Build automation tool.
- **SMTP Email Integration**: For email services.

## Contributing

1. Fork the repository.
2. Create a new branch (`git checkout -b feature-branch`).
3. Commit your changes (`git commit -am 'Add new feature'`).
4. Push to the branch (`git push origin feature-branch`).
5. Open a pull request to merge changes into the `main` branch.