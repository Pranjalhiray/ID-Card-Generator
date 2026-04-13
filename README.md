# 🪪 ID Card Generator System

A web-based **ID Card Generator Application** built using **Spring Boot, Java, and Thymeleaf**.  
This system allows users to register, log in, and generate ID cards while administrators can manage users through an admin panel.

---

## 🚀 Features

- 🔐 User Registration and Login System
- 👤 User Dashboard
- 🪪 ID Card Generation
- 🛠 Admin Panel for Management
- 📄 Profile Page for Users
- 💾 Database Storage for User and ID Card Data

---

## 🛠 Technologies Used

### Backend
- Java
- Spring Boot
- Spring MVC
- Spring Security

### Frontend
- HTML
- CSS
- Thymeleaf

### Database
- MySQL / H2 Database

### Build Tool
- Maven

---

## 📂 Project Structure

```
src
 ├── main
 │   ├── java/com/idcard
 │   │   ├── config
 │   │   ├── controller
 │   │   ├── entity
 │   │   ├── repository
 │   │   └── service
 │   │
 │   └── resources
 │       ├── templates
 │       │   ├── login.html
 │       │   ├── register.html
 │       │   ├── dashboard.html
 │       │   ├── admin-login.html
 │       │   ├── admin-panel.html
 │       │   └── profile.html
 │       │
 │       └── application.properties
 │
 └── pom.xml
```

---

## ⚙️ Installation and Setup

### 1️⃣ Clone the Repository

```
git clone https://github.com/Pranjalhiray/ID-Card-Generator.git
```

### 2️⃣ Open the Project

```
cd ID-Card-Generator
```

### 3️⃣ Build the Project

```
mvn clean install
```

### 4️⃣ Run the Application

```
mvn spring-boot:run
```

Application will run at:

```
http://localhost:8080
```

---

## 📌 Pages

| Page | URL |
|-----|-----|
Login | `/login` |
Register | `/register` |
Dashboard | `/dashboard` |
Admin Login | `/admin-login` |
Admin Panel | `/admin-panel` |

---

## 🔮 Future Improvements

- QR Code on ID Cards
- PDF Download for ID Cards
- Image Upload for Profile
- Email Verification
- Role-based Access Control

---

## 👨‍💻 Author

**Pranjal Hiray**

GitHub:  
https://github.com/Pranjalhiray

---

## 📜 License

This project is open source and available under the **MIT License**.