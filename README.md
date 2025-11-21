# LAB 05 HOMEWORK REPORT

---

## 1. Search Functionality

The search feature allows users to filter the student list based on a keyword that matches either the **Student Code** or **Full Name**.

### Code Flow & Logic

1.  **User Interaction (View)**
    * The user enters a keyword (e.g., "John" or "SV001") into the search input on `student-list.jsp`.
    * They click the "Search" button.
    * The form submits a `GET` request to the server: `/student?action=list&keyword=John`.

2.  **Controller Processing (`StudentController.java`)**
    * The `doGet` method receives the request. It identifies the `action` is `list` (default).
    * It calls the `listStudents` method.
    * `listStudents` retrieves the `keyword` parameter using `request.getParameter("keyword")`.
    * This `keyword` is then passed to two DAO methods: `countStudents(keyword)` (to count only matching records for pagination) and `getAllStudents(keyword, ...)` (to fetch the actual data).

3.  **Data Access (`StudentDAO.java`)**
    * The `getAllStudents` method constructs a SQL query with a `WHERE` clause:
        ```sql
        SELECT * FROM students WHERE full_name LIKE ? OR student_code LIKE ? ...
        ```
    * It sets the parameters using `%keyword%` to enable partial matching.
    * The query executes, returning only the matching students.

4.  **Response (View)**
    * The `StudentController` forwards the filtered list back to `student-list.jsp`.
    * The JSP displays only the students found. If the keyword is "John", only students with "John" in their name are shown.

    <img width="1299" height="617" alt="image" src="https://github.com/user-attachments/assets/497f9a14-ab3f-4c7a-bb0f-cb2228e37765" />

## 2. Server-Side Validation

Validation ensures data integrity by checking input on the server before it touches the database.

### Code Flow & Logic

1.  **Submission**
    * User submits the "Add Student" form.
    * Request goes to `StudentController.insertStudent`.

2.  **Validation Check**
    * Before creating the `Student` object, the controller calls a helper method `validateStudent(code, email)`.
    * Regex Rules:
        ** Student Code: `[A-Z]{2}[0-9]{3,}` (2 uppercase letters + 3+ digits).
        ** Email: `^[A-Za-z0-9+_.-]+@(.+)$` (Standard email format).

3.  **Error Handling**
    * If Invalid: The method returns an error string. The controller immediately redirects back to the form (`student-form.jsp`), passing the error message in the URL (URL encoded). The database is never touched.
    * If Valid: The method returns `null`. The controller proceeds to call `studentDAO.addStudent()`.

    <img width="1301" height="621" alt="image" src="https://github.com/user-attachments/assets/483376c8-c64c-4baf-b72a-8e901072426b" />
    <img width="1290" height="625" alt="image" src="https://github.com/user-attachments/assets/e1cff214-8907-4c8c-adad-1faf3aafa3a4" />
    <img width="1302" height="621" alt="image" src="https://github.com/user-attachments/assets/af40abc7-9857-4538-bdc8-52132a1d90d5" />
    <img width="1301" height="625" alt="image" src="https://github.com/user-attachments/assets/b49948d5-06ca-4b19-914b-37d9c62fd613" />
    <img width="1302" height="622" alt="image" src="https://github.com/user-attachments/assets/0aaccaa4-910a-4444-a9e2-f229b529d653" />
    <img width="1301" height="622" alt="image" src="https://github.com/user-attachments/assets/d60aa3ed-b35e-4ed2-a91a-2ccdca495de0" />

## 3. Sorting

Sorting allows users to order the data by clicking on column headers.

### Code Flow & Logic

1.  **User Interaction (`student-list.jsp`)**
    * The user clicks a column header (e.g., "Full Name").
    * The link triggers a request: `/student?action=list&sortBy=fullName&sortOrder=ASC`.

2.  **Control Logic (`studentController.java`)**
    * The `listStudents` method reads `sortBy` and `sortOrder` from the request.
    * If they are null, it defaults to `id` and `DESC`.

3.  **Dynamic SQL (`StudentDAO.java`)**
    * The `getAllStudents` method in `StudentDAO` builds the SQL query dynamically.
    * Security Measure: To prevent SQL Injection, we do not paste the `sortBy` string directly into the query. Instead, we use a `switch` statement (whitelisting) to map the safe parameter `"fullName"` to the actual database column "full_name".
    * Final SQL looks like: `ORDER BY full_name ASC`.

4.  **View Logic (`student-list.jsp`)**
    * The JSP receives the sorted list.
    * It also calculates the next sort order for the links. If the current order is `ASC`, the link for the header becomes `DESC`, allowing the user to toggle the order.
    
    <img width="1286" height="625" alt="image" src="https://github.com/user-attachments/assets/39a837e1-a8b1-4404-8b67-802ccab228bf" />
    <img width="1288" height="623" alt="image" src="https://github.com/user-attachments/assets/d58a6039-eb9e-495c-9711-f8def1368f2f" />
    <img width="1291" height="626" alt="image" src="https://github.com/user-attachments/assets/ba7eb940-21ee-45f6-95a2-75100c84f60a" />
    <img width="1288" height="628" alt="image" src="https://github.com/user-attachments/assets/217caee2-73b0-4b76-8649-14075a67e257" />
    <img width="1287" height="626" alt="image" src="https://github.com/user-attachments/assets/822f5375-6156-4cd1-915b-a4eb54db7486" />

## 4. Pagination

Pagination handles large datasets by breaking them into smaller chunks (pages), improving performance and user experience.

### Code Flow & Logic

1.  **Initialization (`StudentController.java`)**
    * In `StudentController.listStudents`, we define `recordsPerPage = 5`.
    * We check for a `page` parameter in the URL (e.g., `/student?page=2`). If missing, it defaults to page 1.

2.  **Calculation**
    * `offset`: Calculated as `(currentPage - 1) * recordsPerPage`. For page 2, offset is `(2-1)*5 = 5` (skip the first 5 records).
    * `totalRecords`: Fetched from `studentDAO.countStudents(keyword)`.
    * `totalPages`: Calculated as `Math.ceil(totalRecords / recordsPerPage)`.
3.  **Data Access (`StudentDAO.java`)**
    * The SQL query uses LIMIT and OFFSET to fetch only the required slice of data:
      
    ```sql
    SELECT * FROM ... LIMIT 5 OFFSET 5
    ```
4.  **Rendering (`student-list.jsp`)**
    * The JSP receives `currentPage` and `totalPages`.
    * It generates a pagination bar.
    * Crucial Detail: Every page link includes the current `keyword`, `sortBy`, and `sortOrder`. This ensures that if a user searches for "Smith" and goes to page 2, they see page 2 of the search results for "Smith", not page 2 of the entire list.

    <img width="1285" height="624" alt="image" src="https://github.com/user-attachments/assets/e173445e-362f-4b14-bab4-f5399114c6b1" />
    <img width="1289" height="625" alt="image" src="https://github.com/user-attachments/assets/8e37c33e-fc91-440a-b579-79485f11471c" />
    <img width="1300" height="622" alt="image" src="https://github.com/user-attachments/assets/9c3ca760-c19c-48a8-bd82-b3316b874d46" />
