# Tweetchat - Full-Stack Microblogging Application

 https://imgur.com/1x7xlKS

Tweetchat is a complete, full-stack microblogging web application built with a modern Java and JavaScript technology stack. It demonstrates a comprehensive understanding of backend REST API development, frontend single-page application (SPA) design, authentication, database management, and containerization with Docker.

This project was developed as part of the Web Technology Project module at OTH Regensburg.

---

## ✨ Features

*   **User Authentication:** Secure user registration and login using JWT (JSON Web Tokens).
*   **Content Creation:** Authenticated users can create posts with text and optional image uploads.
*   **Social Networking:** A complete follow/unfollow system allows users to build their own social graph.
*   **Interactive Timelines:**
    *   A personalized homepage feed showing the latest posts from followed users.
    *   User profile pages displaying a specific user's posts, follower count, and following count.
*   **Post Interaction:** Users can "like" and "unlike" posts, and delete their own posts.
*   **Dynamic UI:** The interface updates in real-time without page reloads (e.g., new posts appear instantly, like counts update on click).
*   **Discoverability:** Users can search for other users to connect with.
*   **Pagination:** Timelines automatically load more posts as the user scrolls or clicks a "Load More" button, ensuring excellent performance.

---

## 🛠️ Technology Stack

This project is a demonstration of a modern, containerized, full-stack architecture.

| Layer         | Technology / Framework                                                                                             |
|---------------|--------------------------------------------------------------------------------------------------------------------|
| **Frontend**  | **React 18** (with Hooks), **Vite**, **React Router**, **Axios**                                                     |
| **Backend**   | **Java 21**, **Spring Boot 3**, **Spring Security**, **Spring Data JPA** (Hibernate)                                |
| **Database**  | **MariaDB**                                                                                                        |
| **API**       | **RESTful API** with JWT-based stateless authentication                                                            |
| **Testing**   | **JUnit 5**, **Mockito** (Unit Tests), **Spring Boot Test** (Integration Tests) - achieving over 80% code coverage. |
| **Deployment**| **Docker** & **Docker Compose** for full application containerization (Frontend, Backend, Database).               |

---

## 🚀 Getting Started

### Prerequisites

*   [Docker Desktop](https://www.docker.com/products/docker-desktop/) must be installed and running on your machine.

### Running the Application

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/Jha45415/tweetchat.git
    cd tweetchat
    ```

2.  **Create a local environment file:**
    At the root of the project, create a file named `.env` and add the following content. This file is ignored by Git and is used by Docker Compose to configure the services.
    ```env
    MARIADB_ROOT_PASSWORD=asdf1234
    MARIADB_DATABASE=tweetchat
    APP_JWT_SECRET=YourSuperSecretKeyForJWTGenerationAndValidationMustBeLongAndComplexEnoughForHS256
    ```

3.  **Build and run the containers:**
    This single command will build the frontend and backend images and start all three containers (frontend, backend, database).
    ```bash
    docker compose up --build
    ```

4.  **Access the application:**
    Once the containers are running, open your web browser and navigate to:
    **`http://localhost`**

---

## 📸 Screenshots


**Login Page:**
https://imgur.com/1x7xlKS


**User Profile Page:**
https://imgur.com/1bzB4gp
https://imgur.com/PZIwgTr
https://imgur.com/g1TWXFm

---

## Project Structure

```
.
├── tweetchat-backend/    # Spring Boot Application
│   ├── src/
│   └── pom.xml
│   └── Dockerfile
├── tweetchat-frontend/   # React Application
│   ├── src/
│   └── package.json
│   └── Dockerfile
└── docker-compose.yml    # Master file to run all services
```
