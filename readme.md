# Home libraries management system
Application allows users to manage their home libraries.
They can add books (by creating new one or by choosing book
which already exists in the system),
mark book as being read, manage borrowed and lent books, view statistics about their
readings, comment books and more.

# Technologies:
- Java 8
- Spring
- HTML, CSS, Bootstrap
- Thymeleaf
- Maven
- H2 database


# Lunch
In order to run the application just type in:
````
mvn spring-boot:run
````
App runs on port 8080 by default.

By default there exists an user with username `a` and password `a`.

For email account confirmations and invitations to work properly,
you should specify credentials in `application.properties`, I
do not guarantee that at the moment you will try to lunch the app
credentials provided by me will be still valid.