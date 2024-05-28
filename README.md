# Training Diary Application

This is a console-based training diary application that allows users to manage their training records. 
Users can register, log in, and perform various operations such as adding, viewing, editing, and deleting training records. 
Admin users have additional privileges like viewing the audit log.

## Features

- User Registration and Login
- Add, View, Edit, and Delete Trainings
- View Statistics
- View Audit Log (Admin only)

## Getting Started

### Prerequisites

- Java 8 or higher
- Maven

### Installation

1. Clone the repository:
   ```sh
    git clone git@github.com:mnv/java-training-diary-console.git
    cd java-training-diary-console
   ```

2. Create environment variables for Docker and fill it as you need:
   ```sh
   cp .env.sample .env
   ```

3. Run the database container:
   ```sh
   docker compose up
   ```

4. Create schemas by running `ru/ylab/migrations/Main.java`;

5. Apply migrations:
   ```shell
   liquibase update
   ```

## Usage

Follow the on-screen instructions to interact with the application. 
The main menu options will be displayed based on the user's authentication status.

## Testing

Unit tests are provided to ensure the application's functionality.
