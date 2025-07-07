Tweetchat - Web Technology Project

This project is a full-stack microblogging application developed for the Web Technology Project module at OTH Regensburg. It allows users to register, post short messages with optional images, follow other users, and interact with posts by liking them.
Core Technologies

    Backend: Spring Boot 3 with Java 21

    Frontend: React 18 with Vite

    Database: MariaDB

    Authentication: JWT (JSON Web Tokens) via Spring Security

    Containerization: Docker & Docker Compose

    Testing: JUnit 5 & Mockito

Features
Functional Requirements Met

    User Management (F-U1, F-U2, F-U3): Full user registration with unique usernames, login, and logout functionality.

    Following System (F-F1, F-F2, F-F3): Users can search for other users by username, follow them, and unfollow them. Search results indicate the current follow status.

    Post Creation (F-P1): Authenticated users can create new posts containing text.

    Liking Posts (F-L1): Users can like posts created by other users. It is not possible to like one's own post or to like the same post multiple times.

    Timelines (F-V1, F-V2, F-V3):

        User Profile: Displays all posts from a specific user in reverse chronological order.

        Aggregated Feed: The homepage displays a timeline of the 20 most recent posts from all users the current user follows.

Implemented Extra Features

This project implements the following extra features for grade improvement, as suggested in the project specification:

    Un-liking Posts: The "like" button functions as a toggle, allowing users to unlike a post they have previously liked.

    Deleting One's Own Posts: Users have the ability to delete their own posts, which are then removed from all timelines and the database.

    Displaying the User Profile of a Different User: All usernames are clickable links that navigate to a dedicated profile page for that user, showing their posts and follower/following information.

    Managing One's Own User Profile (Partial): The profile page displays follower and following counts, providing more social context.

    Image Uploads with Posts: Users can optionally attach an image when creating a new post. These images are stored on a persistent volume and displayed in the timelines.

    Pagination on Timelines: A "Load More" button is implemented on user profiles and the main feed, allowing users to load older posts in pages of 20.

Technical Requirements Met

    Project Structure (T-S): The project is structured into tweetchat-backend and tweetchat-frontend modules as specified.

    Backend (T-B): The Spring Boot backend connects to an external MariaDB container named db and is accessible on port 8080.

    Testing (T-T): The backend includes a comprehensive suite of JUnit 5 unit and integration tests, achieving over 80% line and method coverage for the src/main/java directory. Tests are executed automatically with Maven and use an H2 in-memory database.

    Frontend (T-F): The React frontend is built using Vite and npm. It uses environment variables to manage the backend API URL.

    Code Quality (T-Q): The application follows standard architecture patterns for Spring Boot (Controller-Service-Repository) and React (components, pages, services). A clear distinction is made between DTOs and JPA Entities.

    Dockerization (T-D): The entire application stack (backend, frontend, database) is containerized and can be built and started with a single command: docker compose up --build.

How to Run the Application

    Ensure Docker Desktop is installed and running on your machine.

    Clone this repository to your local machine.

    Navigate to the root directory of the project (ics-wtp-tweetchat) in a terminal.

    Run the following command:   ( docker compose up --build)
    Wait for all containers (db, tweetchat-backend, tweetchat-frontend) to build and start.

    Access the application in your web browser at:
    http://localhost
