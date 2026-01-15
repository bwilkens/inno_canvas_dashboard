```mermaid
classDiagram
    class CourseRole {
        <<enumeration>>
        STUDENT
        TEACHER
    }
    class Privilege {
        <<enumeration>>
        USER
        ADMIN
        SUPERADMIN
    }
    class Users {
        -String email
        -String name
    }
    class Course {
        -String canvasCourseId
        -String courseName
        -String courseCode
        -String instanceName
        -LocalDate startDate
        -LocalDate endDate
    }
    class UserCourse

    note for Privilege "Defaults to `USER`"

    Users "1" -- "0..*" UserCourse
    Users --> "1" Privilege
    UserCourse --> "1" CourseRole
    UserCourse "0..*" -- "1" Course
    ```