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
  
    <img width="1302" height="629" alt="image" src="https://github.com/user-attachments/assets/7c83f9f9-e731-4d3a-83b8-47a5b5af7ea0" />

    <img width="1302" height="627" alt="image" src="https://github.com/user-attachments/assets/cb95e3cf-e356-4e72-b90a-5f13c0b8dafe" />

    <img width="1302" height="625" alt="image" src="https://github.com/user-attachments/assets/07a1cad4-fae3-4632-b6fc-20f467e07b0b" />

### 2. Dashboard & Role-Based Access
This flow determines what the user sees based on their role (Admin vs. User).

1.  **Request**: User accesses `/dashboard`.
2.  **Controller Logic**: `DashboardController` retrieves the user's role from the session.
3.  **View Rendering**: The request is forwarded to `dashboard.jsp`.
4.  **Dynamic Content**: Inside `dashboard.jsp`, JSTL tags (`<c:if>`) are used to show or hide buttons (e.g., "Add New Student" is visible only to Admins).

    <img width="1307" height="627" alt="image" src="https://github.com/user-attachments/assets/5dd99fb5-2c3e-4a6a-a759-85d2b099536b" />

### 3. Student Management Flow (CRUD)
All student operations are managed by the `StudentController`.

* **Read (List Students)**:
    1.  Request to `/student?action=list`.
    2.  `StudentController` calls `StudentDAO.getAllStudents()` to fetch the list.
    3.  The list is attached to the request, and the user is forwarded to `student-list.jsp`.

    <img width="1288" height="625" alt="image" src="https://github.com/user-attachments/assets/91319dbc-683e-4143-a6e7-313598ae0933" />

* **Create (Add Student)**:
    1.  **GET**: `/student?action=new` triggers `StudentController` to forward to the empty `student-form.jsp`.
    2.  **POST**: Submitting the form sends data to `/student?action=insert`. The controller calls `StudentDAO.addStudent()` and redirects to the list.

    <img width="1303" height="624" alt="image" src="https://github.com/user-attachments/assets/e4e6a518-704c-412b-9885-a3f79cc538a7" />

    <img width="1288" height="627" alt="image" src="https://github.com/user-attachments/assets/d293c38a-95cb-4c4e-868d-aa6a89780c82" />

* **Update (Edit Student)**:
    1.  **GET**: `/student?action=edit&id=X`. The controller calls `StudentDAO.getStudentById(X)` and forwards the existing data to `student-form.jsp` to pre-fill the fields.
    2.  **POST**: Submitting the form sends data to `/student?action=update`. The controller calls `StudentDAO.updateStudent()` and redirects to the list.

    <img width="1303" height="629" alt="image" src="https://github.com/user-attachments/assets/c64884c7-ad79-40f6-bb39-208eabf8ad92" />

    <img width="1292" height="628" alt="image" src="https://github.com/user-attachments/assets/baf2423b-1fc8-432f-a2e8-37251a060c0b" />

* **Delete (Remove Student)**:
    1.  **GET**: `/student?action=delete&id=X`.
    2.  `StudentController` calls `StudentDAO.deleteStudent(X)` and redirects to the list with a success/error message.

    <img width="443" height="121" alt="image" src="https://github.com/user-attachments/assets/1ee17b3e-9caf-4f2e-839f-e2df34f2970d" />

    <img width="1289" height="629" alt="image" src="https://github.com/user-attachments/assets/e2d1921f-0c15-46af-a04e-e8d1f8ec1fe7" />

### 4. Security Layer
* **`AuthFilter`**: Applied to all pages except login/resources. Ensures no user can access the dashboard or student lists without logging in.
* **`AdminFilter`**: Applied to critical actions (Create, Update, Delete). It checks if `session.getAttribute("role")` equals "admin". If a standard user tries to access these URLs, they are denied access or redirected.

    <img width="1308" height="628" alt="image" src="https://github.com/user-attachments/assets/588c142f-0d23-4ee2-9710-516be7307f7f" />

    <img width="1306" height="628" alt="image" src="https://github.com/user-attachments/assets/90db0ad6-ee4c-4561-92ae-c52038a8705d" />
