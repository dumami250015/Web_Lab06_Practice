# LAB 06 PRACTICE REPORT

## Application Code Flow

### 1. Authentication Flow (Login)
This flow handles user identity verification and session creation.

1.  **User Access**: The user attempts to access a protected page or visits `/login`.
2.  **Filter Check**: `AuthFilter` intercepts the request. If no session exists, it redirects the user to `login.jsp`.
3.  **Submission**: User enters credentials in `login.jsp` and submits (POST) to `LoginController`.
4.  **Verification**: `LoginController` calls `UserDAO.checkLogin(username, password)` to validate the user against the database.
5.  **Session Creation**:
    * **Success**: The controller creates a session and stores the `user` object and `role`. The user is redirected to the `DashboardController`.
    * **Failure**: The user is redirected back to `login.jsp` with an error message.

### 2. Dashboard & Role-Based Access
This flow determines what the user sees based on their role (Admin vs. User).

1.  **Request**: User accesses `/dashboard`.
2.  **Controller Logic**: `DashboardController` retrieves the user's role from the session.
3.  **View Rendering**: The request is forwarded to `dashboard.jsp`.
4.  **Dynamic Content**: Inside `dashboard.jsp`, JSTL tags (`<c:if>`) are used to show or hide buttons (e.g., "Add New Student" is visible only to Admins).

### 3. Student Management Flow (CRUD)
All student operations are managed by the `StudentController`.

* **Read (List Students)**:
    1.  Request to `/student?action=list`.
    2.  `StudentController` calls `StudentDAO.getAllStudents()` to fetch the list.
    3.  The list is attached to the request, and the user is forwarded to `student-list.jsp`.

* **Create (Add Student)**:
    1.  **GET**: `/student?action=new` triggers `StudentController` to forward to the empty `student-form.jsp`.
    2.  **POST**: Submitting the form sends data to `/student?action=insert`. The controller calls `StudentDAO.addStudent()` and redirects to the list.

* **Update (Edit Student)**:
    1.  **GET**: `/student?action=edit&id=X`. The controller calls `StudentDAO.getStudentById(X)` and forwards the existing data to `student-form.jsp` to pre-fill the fields.
    2.  **POST**: Submitting the form sends data to `/student?action=update`. The controller calls `StudentDAO.updateStudent()` and redirects to the list.

* **Delete (Remove Student)**:
    1.  **GET**: `/student?action=delete&id=X`.
    2.  `StudentController` calls `StudentDAO.deleteStudent(X)` and redirects to the list with a success/error message.

### 4. Security Layer
* **`AuthFilter`**: Applied to all pages except login/resources. Ensures no user can access the dashboard or student lists without logging in.
* **`AdminFilter`**: Applied to critical actions (Create, Update, Delete). It checks if `session.getAttribute("role")` equals "admin". If a standard user tries to access these URLs, they are denied access or redirected.
