# İş Portalı Projesi (Job Portal)

Bu proje, bir iş portalı (iş arayanlar ve iş verenler için) backend servisi sunan bir Spring Boot uygulamasıdır.

## 🚀 Kullanılan Teknolojiler

* **Java 21**
* **Spring Boot 3.4.7**
* **Spring Web:** RESTful API endpoint'leri için.
* **Spring Data JPA (Hibernate):** Veritabanı işlemleri ve ORM için.
* **Spring Security:** Endpoint güvenliği ve Cookie tabanlı kimlik doğrulama için.
* **MySQL:** Veritabanı.

## ✨ Temel Özellikler

* **Rol Bazlı Erişim:** `Job Seeker` (İş Arayan) ve `Recruiter` (İş Veren) olmak üzere iki farklı kullanıcı rolü.
* **Kimlik Doğrulama:** Güvenli kayıt (`/register`), giriş (`/login`) ve çıkış (`/logout`) endpoint'leri.
* **İş İlanı Yönetimi:** İş verenler tarafından iş ilanı oluşturma, güncelleme, listeleme.
* **Profil Yönetimi:** Her iki rol için de detaylı profil (metin bilgileri) ve fotoğraf/CV yükleme.
* **İş Arama ve Başvuru:** İş arayanlar için ilan arama, ilana başvurma ve ilanları kaydetme.
* **Başvuru Görüntüleme:** İş verenler için kendi ilanlarına gelen başvuruları listeleme.

---
## 🔐 API Güvenlik Mimarisi: Cookie (Oturum)

Bu API, **JWT (Token)** kullanmaz. Bunun yerine, Spring Security'nin standart `formLogin` mekanizması üzerine kurulu **Cookie (Oturum) tabanlı kimlik doğrulama** kullanır.

Bu, API'yi kullanmak (örn: Postman) için bir akış gerektirir:

1.  **`POST /login`** endpoint'ine `x-www-form-urlencoded` formatında `username` ve `password` gönderin.
2.  Sunucu, yanıt olarak `200 OK` ve bir `JSESSIONID` cookie'si (Çerez) döndürür.
3.  Postman (veya tarayıcınız) bu cookie'yi otomatik olarak saklar.
4.  Artık korumalı endpoint'lere (örn: `/api/v1/profile/seeker/my-profile`) istek attığınızda, Postman bu cookie'yi otomatik olarak isteğe ekler ve Spring Security oturumunuzu tanır.
5.  **`POST /logout`** çağrıldığında bu oturum ve cookie sonlandırılır.

---

## 🗺️ API Endpoint Rehberi

### 🏠 Genel

| Metot | URL | Açıklama |
| :--- | :--- | :--- |
| `GET` | `/` | API'nin çalıştığını belirten hoş geldiniz mesajı. |

### 🔑 Kimlik Doğrulama (Auth) - (Herkese Açık)

| Metot | URL | Body (Request) | Açıklama |
| :--- | :--- | :--- | :--- |
| `POST` | `/login` | `x-www-form-urlencoded` <br> `username` (email) <br> `password` (şifre) | Oturum başlatır ve `JSESSIONID` cookie'si döndürür. |
| `POST` | `/logout` | (Boş) | Mevcut oturumu sonlandırır. |
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

This project is a Spring Boot application that provides a backend service for a job portal (for job seekers and employers).

## 🚀 Technologies Used

* **Java 21**
* **Spring Boot 3.4.7**
* **Spring Web:** For RESTful API endpoints.
* **Spring Data JPA (Hibernate):** For database operations and ORM.
* **Spring Security:** For endpoint security and Cookie-based authentication.
* **MySQL:** Database.

## ✨ Core Features

* **Role-Based Access:** Two distinct user roles: `Job Seeker` and `Recruiter`.
* **Authentication:** Secure registration (`/register`), login (`/login`), and logout (`/logout`) endpoints.
* **Job Post Management:** Creating, updating, and listing job posts by recruiters.
* **Profile Management:** Detailed profiles (text information) and photo/CV uploads for both roles.
* **Job Search and Application:** Searching for listings, applying to jobs, and saving jobs for seekers.
* **Viewing Applications:** Listing applications received for their own job posts (for recruiters).

---
## 🔐 API Security Architecture: Cookie (Session)

This API does **not** use **JWT (Tokens)**. Instead, it uses **Cookie (Session)-based authentication** built on Spring Security's standard `formLogin` mechanism.

This requires a specific flow to use the API (e.g., in Postman):

1.  Send `username` and `password` in `x-www-form-urlencoded` format to the **`POST /login`** endpoint.
2.  The server returns a `200 OK` response and a `JSESSIONID` cookie.
3.  Postman (or your browser) automatically stores this cookie.
4.  Now, when you make requests to protected endpoints (e.g., `/api/v1/profile/seeker/my-profile`), Postman automatically includes this cookie, and Spring Security recognizes your session.
5.  When **`POST /logout`** is called, this session and cookie are terminated.

---

## 🗺️ API Endpoint Guide

### 🏠 General

| Method | URL | Description |
| :--- | :--- | :--- |
| `GET` | `/` | Welcome message indicating the API is running. |

### 🔑 Authentication (Auth) - (Public)

| Method | URL | Body (Request) | Description |
| :--- | :--- | :--- | :--- |
| `POST` | `/login` | `x-www-form-urlencoded` <br> `username` (email) <br> `password` (şifre) | Starts a session and returns a `JSESSIONID` cookie. |
| `POST` | `/logout` | (Empty) | Terminates the current session. |
| `POST` | `/api/v1/auth/register` | `JSON` (RegisterRequestDto) | Creates a new user (Job Seeker or Recruiter) registration. |
| `GET` | `/api/v1/auth/user-types` | (Empty) | Lists user types (`Recruiter`, `Job Seeker`) for use in the registration form. |

### 📄 Job Posts

| Method | URL | Security | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/jobs/search` | Public | Searches for job posts with parameters (job, location, etc.). |
| `GET` | `/api/v1/jobs/{id}` | Protected | Fetches the details of a single job post. |
| `GET` | `/api/v1/dashboard/jobs` | Protected | Fetches dashboard listings based on the logged-in user's role (Seeker/Recruiter). |
| `POST` | `/api/v1/jobs` | Protected (Recruiter) | Creates a new job post. (Body: `JobPostActivity` JSON) |
| `PUT` | `/api/v1/jobs/{id}` | Protected (Recruiter) | Updates an existing job post. (Body: `JobPostActivity` JSON) |
| `GET` | `/api/v1/jobs/{jobId}/applicants`| Protected (Recruiter) | Fetches the list of applicants (`JobSeekerApply` list) for a specific job post. |

### 👤 Job Seeker Profile and Actions - (Protected)

| Method | URL | Body (Request) | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/profile/seeker/my-profile` | (Empty) | Fetches the logged-in job seeker's own profile. |
| `PUT` | `/api/v1/profile/seeker/my-profile` | `JSON` (JobSeekerProfileDto)| Updates the profile's text information (name, city, skills, etc.). |
| `POST` | `/api/v1/profile/seeker/photo` | `form-data` (key: `image`) | Uploads or updates the profile photo. |
| `POST` | `/api/v1/profile/seeker/resume` | `form-data` (key: `resume`)| Uploads or updates the CV (PDF). |
| `GET` | `/api/v1/profile/seeker/{id}` | (Empty) | Fetches any job seeker's (public) profile by ID. |
| `POST` | `/api/v1/jobs/{jobId}/apply` | `JSON` (ApplyJobRequestDto) | Applies to a job post. (Can include an optional `coverLetter`) |
| `POST` | `/api/v1/jobs/{id}/save` | (Empty) | Adds a job post to the "saved" list. |
| `GET` | `/api/v1/profile/seeker/saved-jobs` | (Empty) | Lists all job posts saved by the logged-in user. |

### 👔 Recruiter Profile - (Protected)

| Method | URL | Body (Request) | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/profile/recruiter/my-profile`| (Empty) | Fetches the logged-in recruiter's own profile. |
| `PUT` | `/api/v1/profile/recruiter/my-profile`| `JSON` (RecruiterProfileDto)| Updates the profile's text information (name, company, city, etc.). |
| `POST` | `/api/v1/profile/recruiter/photo` | `form-data` (key: `image`) | Uploads or updates the profile photo. |

### 📁 File Serving

| Method | URL | Description |
| :--- | :--- | :--- |
| `GET` | `/photos/**` | Public | Serves uploaded photos (Recruiter or Seeker). (e.g., `/photos/candidate/1/profile.jpg`) 
| `GET` | `/api/v1/profile/seeker/download-resume`| Protected | Downloads the job seeker's CV. (Parameters: `fileName` and `userID`) |
