# BookStore Project

A comprehensive web-based book store application built using the Spring MVC framework, designed for scalability and ease of use.

## Project Overview

- **Purpose:** An online bookstore platform featuring book browsing, purchasing, customer support (chat), and administrative management.
- **Main Technologies:**
    - **Backend:** Java 1.8, Spring MVC 5.3.39, Spring Security 5.8.16, Spring JDBC.
    - **Database:** H2 (local development) / Oracle (ojdbc11).
    - **Frontend:** JSP, JSTL, HTML, CSS, JavaScript.
    - **Build System:** Maven.
    - **Other Libraries:** Lombok, WebSocket, OpenCSV, Jackson, Apache Commons.
- **Architecture:** Standard Spring MVC architecture (Controller -> Service -> DAO -> VO).

## Key Features

- **Book Management:** Browsing, searching, and detailed viewing of books with ratings and likes.
- **Member System:** Registration, login (standard and Kakao Social Login), and profile management.
- **Cart & Order:** Adding items to cart and processing orders.
- **Customer Support:** Real-time chat system using WebSockets and a QNA section.
- **Admin Dashboard:** Tools for managing sales data (daily, weekly, monthly, yearly summaries) and system administration.

## Database & Initialization

- **Database:** Uses H2 database by default for development. Connection details are in `dispatcher-servlet.xml` (jdbc:h2:tcp://localhost/~/test).
- **Auto-Initialization:**
    - `WebBookStore.common.DatabaseInitializer`: Automatically creates tables (member, book, cart, orders, etc.) and inserts default users (`admin/1234`, `user/1234`).
    - `WebBookStore.book.BookCsvInitializer`: Populates the `book` table from `src/main/resources/data/books.csv` on first run.

## Building and Running

### Build Commands
```bash
# Clean and package the application into a WAR file
mvn clean package
```

### Running the Project
- The project is packaged as a `.war` file and requires a Servlet Container like Apache Tomcat (version 9+ recommended).
- Ensure an H2 database server is running or configure the connection string in `dispatcher-servlet.xml` to use embedded mode if preferred.

## Development Conventions

- **Packages:** Organized by feature (e.g., `WebBookStore.admin`, `WebBookStore.book`, `WebBookStore.member`).
- **Views:** Located in `/WEB-INF/views/` using JSP.
- **Security:** Managed by Spring Security. Configuration is integrated within `dispatcher-servlet.xml`.
- **Styling:** Main CSS file is located at `src/main/webapp/css/style.css`.
- **Encoding:** The project uses `UTF-8` throughout.

## Important Files

- `pom.xml`: Maven configuration and dependencies.
- `src/main/webapp/WEB-INF/web.xml`: Web application configuration.
- `src/main/webapp/WEB-INF/dispatcher-servlet.xml`: Spring MVC and Security configuration.
- `DB_patch.sql`: Supplemental SQL scripts for database updates or complex queries.
- `src/main/resources/data/books.csv`: Initial book data.
