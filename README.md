# 📱 Mobile Calorie Management App

An Android application that allows users to log meals, track daily calorie intake, and retrieve nutritional information using external food APIs. The app is designed to help users monitor their dietary habits through a simple and intuitive mobile interface.

---

## 🚀 Features

* 🍽️ Log meals with detailed food information
* 🔢 Automatically calculate daily calorie intake
* 🌐 Retrieve nutritional data using external food APIs
* 📷 Upload and store meal images using Firebase Cloud Storage
* 📊 View daily calorie summaries
* 📜 Access meal history for tracking progress
* 🎨 Simple and user-friendly interface

---

## ☁️ Firebase Cloud Storage Integration

The application integrates Firebase Cloud Storage to handle meal image uploads and storage efficiently.

* Uploads meal photos to Firebase Cloud Storage
* Generates unique filenames using Meal ID and timestamp
* Retrieves download URLs after successful upload
* Stores image URLs in the local database linked to meals
* Displays real-time upload progress using a progress bar
* Implements error handling for upload and retrieval failures

This integration improves scalability, reliability, and user experience by ensuring secure and efficient image handling.

---

## 🛠️ Technologies Used

* **Android Development:** Java
* **API Integration:** REST APIs (Food/Nutrition APIs)
* **Cloud Storage:** Firebase Cloud Storage
* **Database:** Local database with DAO-based data management
* **Development Tools:** Android Studio
* **Version Control:** Git & GitHub

---

## 📱 Screenshots

### 🏠 Home Screen

![Home Screen][(screenshots/App Face.png)](https://github.com/Keneesha-Perera/Calorie-Management-Mobile-Application-/blob/0dc47bbbfd3325b9c4deb7306c15bb04bd9c82b5/screenshots/App%20Face.png)

### 🏠 Menu Screen

![Menu Screen][(screenshots/Option Menu.png)](https://github.com/Keneesha-Perera/Calorie-Management-Mobile-Application-/blob/1e27fe5a1b71ff48f0f12772d325d31d0070a495/screenshots/Option%20Menu.png)

### ➕ Add Meal Screen

![Add Meal][(screenshots/Photo Upload.png)](https://github.com/Keneesha-Perera/Calorie-Management-Mobile-Application-/blob/6ae740e8f6db71f69cf5a55f83ec7ea4b79de864/screenshots/Photo%20Upload.png)

### 📊 Nutrition Search

![Summary][screenshots/API Nutrition Search.png](https://github.com/Keneesha-Perera/Calorie-Management-Mobile-Application-/blob/8656991a1fba3dce52ed780ef5645cef5e3d4589/screenshots/API%20Nutrition%20Search.png)

### 📜 Meal History

![History][(screenshots/Calorie Summary.png)](https://github.com/Keneesha-Perera/Calorie-Management-Mobile-Application-/blob/69df73195c04a5c027b940d247da6fed3d76bba1/screenshots/Calorie%20Summary.png)

---

## ⚙️ How to Run the Project

1. Clone the repository:

   ```bash
   git clone https://github.com/Keneesha-Perera/Calorie-Management-Mobile-Application-.git
   ```

2. Open the project in **Android Studio**

3. Sync Gradle files

4. Connect an Android device or start an emulator

5. Click ▶️ Run to launch the application

---

## 💡 Project Purpose

This project was developed as part of a university assignment to demonstrate:

* Mobile application development
* API integration
* Cloud-based storage (Firebase)
* User interface design
* Real-world problem solving

---

## 🚧 Future Improvements

* User authentication (login/signup)
* Personalized diet recommendations
* Offline data storage support
* Advanced analytics and charts
* Improved UI/UX design

---

## 👤 Author

**Keneesha Perera**
📧 [keneeshayehaly@gmail.com](mailto:keneeshayehaly@gmail.com)
🔗 www.linkedin.com/in/keneesha-perera-14104b268

---

## ⭐ Acknowledgements

* Nutrition data provided by external food APIs
* Firebase for cloud storage services
* Developed using Android Studio and open-source tools
