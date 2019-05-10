# car-rental-app
Application for car rentals. Technologies: Java, Spring Boot, Spring Data, Spring Security, Thymeleaf, Bootstrap

1. open DATABASE\create-user.sql
2. execute script
3. open DATABASE\car-rental-app.sql
3. execute script

default admin:

email: admin@gmail.com
password: password

if you want to create new admin account:
1. open src\main\java\k\dawid\loginuserspringboot\service\UserServiceImpl.java
2. comment method: saveUser(User user)
3. uncomment method saveUser placed below
4. run application
5. register new user
