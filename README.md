# User Stories

## 👩‍🎓 As a Student, I want to:

- **Browse available courses**  
  So that I can choose which courses to take.

- **Enroll in a course**  
  So that I can start learning.

- **Access educational materials such as readings and videos**  
  So that I can study and understand the content.

- **Have a personal account to see all my enrolled courses, progress, grades, and feedback in one place**  
  So that I can easily track my learning journey.

- **Receive assignments**  
  So that I can test my knowledge and apply what I have learned.

- **Get feedback from the instructor**  
  So that I can understand my progress and improve.

- **Track my grades**  
  So that I can monitor my performance and stay motivated.


## 🧑‍🏫 As an Instructor, I want to:

- **Create a new course**  
  So that I can provide educational content and manage enrolled students.

- **Add lessons and assignments to a course**  
  So that I can structure learning material and evaluate student progress.

- **Upload and remove materials for lessons**  
  So that I can enhance lessons with documents, videos, or presentations.

- **View all courses, lessons, and assignments I have created**  
  So that I can track and update my content effectively.

- **Grade student submissions**  
  So that I can provide feedback and performance metrics.

- **Give personalized feedback to student submissions**  
  So that students can learn from their mistakes and improve.

- **Send announcements to students enrolled in a course**  
  So that I can communicate updates or reminders directly.

- **Register, update, or delete my instructor account**  
  So that I can manage my profile and credentials.







# Local PostgreSQL Setup using Docker Compose

The manifest is the `docker-compose.yaml` configuration file that describes the multi-container setup of my project. It enables us to start all the necessary services with one command:

```bash
docker-compose up -d
```

For this task, it was required to use PostgreSQL via the Docker Compose file.

---

## 📦 What is a `.env` File?

Before diving into the manifest, it’s important to understand the `.env` file.

The `.env` file is a safe way to store environment variables, config settings, and other sensitive information so they don’t appear directly in your code.

In my `.env` file, I stored database-related info such as:

* Host name (e.g., `localhost`)
* Port number (e.g., `5432`)
* DB name
* Username and password

Using environment variables keeps the `docker-compose.yaml` clean and secure.

---

## 🐳 Services in `docker-compose.yaml`

### `app` – My Application Service

* `build: .` – Tells Docker to build the image from the Dockerfile in the current directory.
* `container_name:` – Sets a name for the container.
* `environment:` – Loads variables from `.env`.
* `depends_on:` – Ensures the database starts before the app.

### `db` – PostgreSQL Database Service

* `image:` – Uses a pre-built PostgreSQL image from DockerHub. (Downloaded manually beforehand in my case.)
* `container_name:` – Sets a name for the database container.
* `restart:` – Ensures automatic restarts if the container crashes or Docker restarts.
* `environment:` – Uses PostgreSQL-specific environment variables from `.env`.
* `volumes:` – Maps a Docker volume to persist data even after container removal.

---

## 💪 How to Use the Manifest to Create a Local Database

### ✅ Prerequisites

Make sure you have the following installed on your system:

* [Docker](https://docs.docker.com/get-docker/)
* [Docker Compose](https://docs.docker.com/compose/install/)
* *(Optional)* [DBeaver](https://dbeaver.io/download/) for visual database management

---

### 📄 Step 1: Create a `.env` File

Create a `.env` file in the same folder as `docker-compose.yaml` with the following content:

```env
DB_HOST=localhost
DB_PORT=5432
DB_NAME=your-db-name
DB_USER=your-username
DB_PASSWORD=your-password
```

---

### ▶️ Step 2: Run Docker Compose

Start the database and application services using:

```bash
docker-compose up -d
```

This will build and start the containers in detached mode.

---

### 🔍 Step 3: Verify That It's Running

Check container status using:

```bash
docker ps
```

You can also use DBeaver or IntelliJ Database Tool to connect to:

* Host: `localhost`
* Port: `5432`
* Username, password, and DB name as specified in `.env`

---

### ⛔ Step 4: Stop or Remove the Containers

To **stop** the running containers:

```bash
docker-compose stop
```

To **stop and remove** the containers completely:

```bash
docker-compose down
```

---

This setup ensures your database runs inside a container, is easy to spin up, and keeps your host system clean and consistent across environments.

