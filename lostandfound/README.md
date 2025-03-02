# Lost and Found Application

A simple Spring Boot web application that allows users to:

- Upload files (images or descriptions of lost items).
- View a list of all lost items.
- Claim lost items by providing a quantity and item identifier.
- View a list of all claimed items.

The application uses Spring Security for authentication and includes basic endpoints to demonstrate file storage, retrieval, and user claims.

## Table of Contents

1. [Features](#features)  
2. [Prerequisites](#prerequisites)  
3. [Setup & Installation](#setup--installation)  
4. [Running the Application](#running-the-application)  
5. [Logging in](#logging-in)  
6. [Endpoints Overview](#endpoints-overview)  
7. [Usage](#usage)  

## Features

- **User Login**: Spring Security-based login flow (`/login` endpoint).  
- **File Upload**: Upload files (e.g., images for lost items) via a POST request.  
- **Lost Items Management**:  
  - Display all lost items in the main page.  
  - Return JSON of all lost items via `/lostItems`.  
- **Claiming Items**:  
  - Allows users to claim lost items by specifying an item ID and quantity.  
  - Return JSON of all claimed items via `/claimedItems`.  

## Prerequisites

- **Java**: Make sure you have Java 21 installed.
- **Maven**: Ensure you have Maven 3.x installed 

## Setup & Installation

1. **Clone the repository**:
   ```bash
   git clone https://github.com/KevVoncken/Rabobankv2.git
   ```
2. **Configure application properties**:
   - Open `src/main/resources/application.properties`.
   - Here you can see that the database used, is an SQLite database named lostandfound.db, this db will automatically be recreated if you delete it
---

## Running the Application

After building, you can run the application:

**Using the JAR**:
   
   The jar can be found in the target folder. (\Rabobankv2\lostandfound\target)
   
   ```bash
   java -jar target/lostandfound-0.0.1-SNAPSHOT.jar
   ```

By default, the application will start on [http://localhost:8080](http://localhost:8080).

# Logging in

You can chose to login in as admin or as user, for this demo I created three users and a single admin.

- A user can claim a quantity of a lost item
- An admin can add new lost items and see who has claimed which item

## Logins:
- Username: admin & Password: admin
- Username: user & Password: password
- Username: userTwo & Password: passwordTwo
- Username: userThree & Password: passwordThree


## Endpoints Overview

| HTTP Method | Path                  | Description                                                                                  |
|-------------|-----------------------|----------------------------------------------------------------------------------------------|
| **GET**     | `/`                   | Displays the main page with uploaded files, lost items, and claimed items.                  |
| **GET**     | `/login`             | Displays the login page (handled by Spring Security).                                       |
| **GET**     | `/files/{filename}`  | Serves a specific uploaded file as a downloadable resource.                                 |
| **GET**     | `/claimedItems`      | Returns a JSON list of all claimed items (admin only)(`UserClaimState`).                                |
| **GET**     | `/lostItems`         | Returns a JSON list of all lost items(`LostItemState`).                                    |
| **POST**    | `/upload`            | Handles file upload. Expects a `MultipartFile` named `"file"`.                              |
| **POST**    | `/claimItem`         | Creates a new claim for a lost item. Expects `UserClaimState` data (lostItemGuid, quantity).|
| **GET**     | `/user`              | Returns the authenticated user details                      |

---