# İş Portalı Projesi (Job Portal)

Bu proje, bir iş portalı (iş arayanlar ve iş verenler için) backend servisi sunan bir Spring Boot uygulamasıdır.

## 🚀 Kullanılan Teknolojiler

* **Java 21**
* **Spring Boot 3.4.7**
* **Spring Web:** RESTful API endpoint'leri için.
* **Spring Data JPA (Hibernate):** Veritabanı işlemleri ve ORM için.
* **Spring Security:** Endpoint güvenliği ve JWT (JSON Web Token) tabanlı kimlik doğrulama için.
* **MySQL:** Veritabanı.

## ✨ Temel Özellikler

* **Rol Bazlı Erişim:** `Job Seeker` (İş Arayan) ve `Recruiter` (İş Veren) olmak üzere iki farklı kullanıcı rolü.
* **Kimlik Doğrulama:** Güvenli kayıt (`/register`), giriş (`/login`) ve çıkış (`/logout`) endpoint'leri.
* **İş İlanı Yönetimi:** İş verenler tarafından iş ilanı oluşturma, güncelleme, listeleme.
* **Profil Yönetimi:** Her iki rol için de detaylı profil (metin bilgileri) ve fotoğraf/CV yükleme.
* **İş Arama ve Başvuru:** İş arayanlar için ilan arama, ilana başvurma ve ilanları kaydetme.
* **Başvuru Görüntüleme:** İş verenler için kendi ilanlarına gelen başvuruları listeleme.

---
## 🔐 API Güvenlik Mimarisi: JWT (Stateless)

Bu API, modern ve ölçeklenebilir JWT (JSON Web Token) tabanlı Stateless (Durumsuz) kimlik doğrulama yapısı kullanır.

API'yi kullanmak (örn: Postman veya Frontend) için aşağıdaki akış takip edilmelidir:

1.  **`POST /api/v1/auth/login`** endpoint'ine `JSON` formatında `email` ve `password` gönderin.
2.  Sunucu, yanıt olarak başarılı girişte `200 OK` ve bir `Access Token (JWT)` döndürür.
3.  Korumalı endpoint'lere (örn: /api/v1/profile/seeker/my-profile) istek atarken, bu token'ı isteğin Header kısmına eklemeniz gerekir.
4.  Sunucu tarafında oturum tutulmadığı için /logout endpoint'ine gerek yoktur. İstemci tarafında (Tarayıcı/Mobil) token'ın silinmesi çıkış işlemi için yeterlidir.

---

## 🗺️ API Endpoint Rehberi

### 🏠 Genel

| Metot | URL | Açıklama |
| :--- | :--- | :--- |
| `GET` | `/` | API'nin çalıştığını belirten hoş geldiniz mesajı. |

### 🔑 Kimlik Doğrulama (Auth) - (Herkese Açık)

| Metot | URL | Body (Request) | Açıklama |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/auth/login` | `JSON` <br> `email` (email) <br> `password` (şifre) | Giriş yapar ve `JWT` Token döndürür. |
| `POST` | `/logout` | (Boş) | Çıkış işlemi için istemci tarafında Token'ın silinmesi yeterlidir. |
| `POST` | `/api/v1/auth/register` | `JSON` (RegisterRequestDto) | Yeni kullanıcı (Job Seeker veya Recruiter) kaydı oluşturur. |
| `GET` | `/api/v1/auth/user-types` | (Boş) | Kayıt formunda kullanılmak üzere kullanıcı tiplerini (`Recruiter`, `Job Seeker`) listeler. |

### 📄 İş İlanları (Job Posts)

| Metot | URL | Güvenlik | Açıklama |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/jobs/search` | Herkese Açık | Parametreler ile (iş, lokasyon vb.) iş ilanı arar. |
| `GET` | `/api/v1/jobs/{id}` | Korumalı | Tek bir iş ilanının detaylarını getirir. |
| `GET` | `/api/v1/dashboard/jobs` | Korumalı | Giriş yapan kullanıcının rolüne göre (Seeker/Recruiter) dashboard ilanlarını getirir. |
| `POST` | `/api/v1/jobs` | Korumalı (Recruiter) | Yeni bir iş ilanı oluşturur. (Body: `JobPostActivity` JSON) |
| `PUT` | `/api/v1/jobs/{id}` | Korumalı (Recruiter) | Mevcut bir iş ilanını günceller. (Body: `JobPostActivity` JSON) |
| `GET` | `/api/v1/jobs/{jobId}/applicants`| Korumalı (Recruiter) | Belirli bir ilana başvuranların listesini (`JobSeekerApply` listesi) getirir. |

### 👤 İş Arayan (Job Seeker) Profili ve Aksiyonları - (Korumalı)

| Metot | URL | Body (Request) | Açıklama |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/profile/seeker/my-profile` | (Boş) | Giriş yapmış iş arayanın kendi profilini getirir. |
| `PUT` | `/api/v1/profile/seeker/my-profile` | `JSON` (JobSeekerProfileDto)| Profilin metin bilgilerini (isim, şehir, yetenekler vb.) günceller. |
| `POST` | `/api/v1/profile/seeker/photo` | `form-data` (key: `image`) | Profil fotoğrafı yükler veya günceller. |
| `POST` | `/api/v1/profile/seeker/resume` | `form-data` (key: `resume`)| CV (PDF) yükler veya günceller. |
| `GET` | `/api/v1/profile/seeker/{id}` | (Boş) | ID ile herhangi bir iş arayanın (public) profilini getirir. |
| `POST` | `/api/v1/jobs/{jobId}/apply` | `JSON` (ApplyJobRequestDto) | Bir iş ilanına başvuru yapar. (Opsiyonel `coverLetter` içerebilir) |
| `POST` | `/api/v1/jobs/{id}/save` | (Boş) | Bir iş ilanını "kaydedilenler" listesine ekler. |
| `GET` | `/api/v1/profile/seeker/saved-jobs` | (Boş) | Giriş yapmış kullanıcının kaydettiği tüm iş ilanlarını listeler. |

### 👔 İş Veren (Recruiter) Profili - (Korumalı)

| Metot | URL | Body (Request) | Açıklama |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/profile/recruiter/my-profile`| (Boş) | Giriş yapmış iş verenin kendi profilini getirir. |
| `PUT` | `/api/v1/profile/recruiter/my-profile`| `JSON` (RecruiterProfileDto)| Profilin metin bilgilerini (isim, şirket, şehir vb.) günceller. |
| `POST` | `/api/v1/profile/recruiter/photo` | `form-data` (key: `image`) | Profil fotoğrafı yükler veya günceller. |

### 📁 Dosya İndirme (File Serving)

| Metot | URL | Açıklama |
| :--- | :--- | :--- |
| `GET` | `/photos/**` | Herkese Açık | Yüklenen fotoğrafları (Recruiter veya Seeker) sunar. (Örn: `/photos/candidate/1/profil.jpg`) 
| `GET` | `/api/v1/profile/seeker/download-resume`| Korumalı | İş arayanın CV'sini indirir. (Parametre: `fileName` ve `userID`) |

---------------------------------

# Job Portal Project

This project is a Spring Boot application providing a backend service for a job portal (for job seekers and recruiters).

## 🚀 Technologies Used

* **Java 21**
* **Spring Boot 3.4.7**
* **Spring Web:** For RESTful API endpoints.
* **Spring Data JPA (Hibernate):** For database operations and ORM.
* **Spring Security:** For endpoint security and JWT (JSON Web Token) based authentication.
* **MySQL:** Database.

## ✨ Core Features

* **Role-Based Access:** Two distinct user roles: **`Job Seeker`** and **`Recruiter`**.
* **Authentication:** Secure registration (`/register`), login (`/login`), and logout (`/logout`) endpoints.
* **Job Posting Management:** Creating, updating, and listing job postings by recruiters.
* **Profile Management:** Detailed profile (text info) and photo/CV upload for both roles.
* **Job Search and Application:** Job search, applying for a job, and saving job postings for job seekers.
* **Application Viewing:** Listing applications received for their own job postings for recruiters.

---
## 🔐 API Security Architecture: JWT (Stateless)

This API uses a modern and scalable **JWT (JSON Web Token)** based **Stateless** authentication structure.

To use the API (e.g., Postman or Frontend), the following flow must be followed:

1.  **`POST /api/v1/auth/login`** endpoint is sent `email` and `password` in **`JSON`** format.
2.  The server returns **`200 OK`** and an **`Access Token (JWT)`** upon successful login.
3.  When sending requests to protected endpoints (e.g., /api/v1/profile/seeker/my-profile), this token must be added to the request's **Header**.
4.  Since no session is maintained on the server side, a **`/logout`** endpoint is not necessary. Deleting the token on the client side (Browser/Mobile) is sufficient for the logout operation.

---

## 🗺️ API Endpoint Guide

### 🏠 General

| Method | URL | Description |
| :--- | :--- | :--- |
| **`GET`** | **`/`** | Welcome message indicating the API is running. |

### 🔑 Authentication (Auth) - (Public Access)

| Method | URL | Body (Request) | Description |
| :--- | :--- | :--- | :--- |
| **`POST`** | **`/api/v1/auth/login`** | **`JSON`** <br> **`email`** <br> **`password`** | Logs in and returns **`JWT`** Token. |
| **`POST`** | **`/logout`** | (Empty) | Deleting the Token on the client side is sufficient for logout. |
| **`POST`** | **`/api/v1/auth/register`** | **`JSON`** (RegisterRequestDto) | Creates a new user registration (Job Seeker or Recruiter). |
| **`GET`** | **`/api/v1/auth/user-types`** | (Empty) | Lists user types (`Recruiter`, `Job Seeker`) to be used in the registration form. |

### 📄 Job Posts

| Method | URL | Security | Description |
| :--- | :--- | :--- | :--- |
| **`GET`** | **`/api/v1/jobs/search`** | Public Access | Searches for job postings with parameters (job title, location, etc.). |
| **`GET`** | **`/api/v1/jobs/{id}`** | Protected | Retrieves the details of a single job posting. |
| **`GET`** | **`/api/v1/dashboard/jobs`** | Protected | Retrieves dashboard job postings based on the logged-in user's role (Seeker/Recruiter). |
| **`POST`** | **`/api/v1/jobs`** | Protected (Recruiter) | Creates a new job posting. (Body: `JobPostActivity` JSON) |
| **`PUT`** | **`/api/v1/jobs/{id}`** | Protected (Recruiter) | Updates an existing job posting. (Body: `JobPostActivity` JSON) |
| **`GET`** | **`/api/v1/jobs/{jobId}/applicants`**| Protected (Recruiter) | Retrieves the list of applicants (`JobSeekerApply` list) for a specific job posting. |

### 👤 Job Seeker Profile and Actions - (Protected)

| Method | URL | Body (Request) | Description |
| :--- | :--- | :--- | :--- |
| **`GET`** | **`/api/v1/profile/seeker/my-profile`** | (Empty) | Retrieves the logged-in job seeker's own profile. |
| **`PUT`** | **`/api/v1/profile/seeker/my-profile`** | **`JSON`** (JobSeekerProfileDto)| Updates the profile's text information (name, city, skills, etc.). |
| **`POST`** | **`/api/v1/profile/seeker/photo`** | **`form-data`** (key: `image`) | Uploads or updates the profile photo. |
| **`POST`** | **`/api/v1/profile/seeker/resume`** | **`form-data`** (key: `resume`)| Uploads or updates the CV (PDF). |
| **`GET`** | **`/api/v1/profile/seeker/{id}`** | (Empty) | Retrieves the (public) profile of any job seeker by ID. |
| **`POST`** | **`/api/v1/jobs/{jobId}/apply`** | **`JSON`** (ApplyJobRequestDto) | Applies to a job posting. (Can optionally include `coverLetter`) |
| **`POST`** | **`/api/v1/jobs/{id}/save`** | (Empty) | Adds a job posting to the "saved" list. |
| **`GET`** | **`/api/v1/profile/seeker/saved-jobs`** | (Empty) | Lists all saved job postings for the logged-in user. |

### 👔 Recruiter Profile - (Protected)

| Method | URL | Body (Request) | Description |
| :--- | :--- | :--- | :--- |
| **`GET`** | **`/api/v1/profile/recruiter/my-profile`**| (Empty) | Retrieves the logged-in recruiter's own profile. |
| **`PUT`** | **`/api/v1/profile/recruiter/my-profile`**| **`JSON`** (RecruiterProfileDto)| Updates the profile's text information (name, company, city, etc.). |
| **`POST`** | **`/api/v1/profile/recruiter/photo`** | **`form-data`** (key: `image`) | Uploads or updates the profile photo. |

### 📁 File Serving

| Method | URL | Description |
| :--- | :--- | :--- |
| **`GET`** | **`/photos/**`** | Public Access | Serves uploaded photos (Recruiter or Seeker). (E.g.: `/photos/candidate/1/profil.jpg`) |
| **`GET`** | **`/api/v1/profile/seeker/download-resume`**| Protected | Downloads the job seeker's CV. (Parameters: `fileName` and `userID`) |
