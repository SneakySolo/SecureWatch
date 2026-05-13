# 🛡️ SecureWatch

> A backend-heavy security monitoring system built with Spring Boot — goes far beyond basic auth.

SecureWatch is a REST API with an admin dashboard that doesn't just handle login and registration. It **watches what users do**, scores their behavior, and automatically blocks threats. Honeypot endpoints, brute force detection, new IP alerts, and rapid request tracking — all running silently on every request.

---

## What it actually does

Most auth projects stop at "user logs in, gets a token." SecureWatch keeps going.

Every login attempt is recorded. Every API request passes through a detection layer. Risk scores climb. When a threshold is crossed, the system blocks automatically — no manual intervention needed. An admin can review everything through a dashboard and unblock users when appropriate.

---

## Screenshot

![image alt](https://github.com/SneakySolo/SecureWatch/blob/f22aa04e4e91f20c72a3688933d73465807ba687/Capture.PNG)

---

## Features

**Detection Engine**
- Brute force detection — flags 5+ failed logins within 5 minutes
- New IP login alerts — notifies when a user logs in from an unrecognized IP
- Rapid request detection — in-memory sliding window tracking, triggers at 30 req/min
- Honeypot endpoints — fake routes that look real, silently log anyone who touches them

**Risk Scoring**
| Event | Points |
|---|---|
| Failed login (×n) | +2 per attempt |
| New IP detected | +3 |
| Rapid requests | +8 |
| Honeypot triggered | +15 |

Once a user hits **15 points**, they're auto-blocked. Only an admin can reverse it.

**Security**
- Stateless JWT authentication (access token only, stored client-side)
- Custom `JwtAuthFilter` — validates tokens and checks blocked status on every request
- Role-based access control — `USER` and `ADMIN` roles enforced at route level
- IP-level blocking — blocked IPs are rejected before token validation even runs

**Admin Dashboard**
- View all active users sorted by risk score
- Click any user to see their full suspicious event history
- One-click block and unblock
- Blocked entities page for managing restricted users and IPs

---

## Tech Stack

| Layer | Technology |
|---|---|
| Framework | Spring Boot 3.x |
| Security | Spring Security + JWT (jjwt) |
| Database | PostgreSQL + Spring Data JPA |
| Frontend | Thymeleaf + plain CSS |
| Build | Maven |
| Java | 21 |

---

## Project Structure

```
src/main/java/com/SneakySolo/SecureWatch/
│
├── Controller/        REST controllers + Dashboard controller
├── Service/           Business logic, DetectionService, AdminService
├── Repository/        JPA repositories with custom queries
├── Entity/            JPA entities (User, LoginAttempt, SuspiciousEvent, ...)
├── Dto/               Request and response DTOs — no raw entities exposed
├── Filter/            JwtAuthFilter — runs on every request
├── Security/          UserDetailsServiceImpl
├── Config/            SecurityConfig, GlobalExceptionHandler
├── Util/              JwtUtil
└── Exception/         Custom exception classes
```

---

## Database Schema

```
users               → id, username, email, password, role, risk_score, is_blocked
login_attempts      → id, username, ip_address, success, timestamp
suspicious_events   → id, user_id, ip_address, event_type, description, metadata, timestamp
decoy_access_logs   → id, user_id, ip_address, endpoint, timestamp
blocked_entities    → id, entity_type, entity_value, reason, blocked_by, blocked_at
```

---

## API Endpoints

**Auth**
```
POST  /api/auth/register
POST  /api/auth/login
```

**Honeypot** *(public — intentionally)*
```
GET   /api/decoy/admin-access
POST  /api/decoy/export-users
GET   /api/decoy/system-config
```

**Admin** *(requires ADMIN role)*
```
GET    /api/admin/suspicious-events
GET    /api/admin/blocked-entities
GET    /api/admin/decoy-logs
GET    /api/admin/summary
POST   /api/admin/block
DELETE /api/admin/unblock/{id}
```

**Dashboard** *(session-based UI)*
```
GET/POST  /dashboard/login
GET       /dashboard/users
GET       /dashboard/users/{username}/events
POST      /dashboard/users/{username}/block
GET       /dashboard/blocked
POST      /dashboard/unblock/{id}
GET       /dashboard/logout
```

---

## Getting Started

**1. Clone the repo**
```bash
git clone https://github.com/yourusername/SecureWatch.git
cd SecureWatch
```

**2. Create the database**
```sql
CREATE DATABASE security_monitor_db;
```

**3. Configure `application.properties`**
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/security_monitor_db
spring.datasource.username=your_db_user
spring.datasource.password=your_db_password

spring.jpa.hibernate.ddl-auto=update

jwt.secret=your_base64_encoded_secret_min_32_chars
jwt.duration=3600000
```

**4. Seed an admin user**

Run the app once, then insert directly:
```sql
INSERT INTO users (username, email, password, role, risk_score, is_blocked, created_at)
VALUES ('admin', 'admin@securewatch.com', '<bcrypt_hash>', 'ADMIN', 0, false, NOW());
```

**5. Run**
```bash
mvn spring-boot:run
```

Dashboard available at `http://localhost:8080/dashboard/login`

---

## How the Attack Flow Works

```
Attacker hits /api/decoy/admin-access
        ↓
DecoyController logs the access
        ↓
DetectionService creates SuspiciousEvent (HONEYPOT_TRIGGERED)
        ↓
addRisk() adds +15 to risk score
        ↓
Score ≥ 15 → user auto-blocked, BlockedEntity created
        ↓
Next request → JwtAuthFilter checks blocked status → 403
```

---

## Risk Threshold Logic

```
register → login (success) → risk: 0
failed login x5            → risk: 10  (+2 per attempt, triggered at 5)
login from new IP          → risk: 13  (+3)
hit a decoy endpoint       → risk: 28  (+15) → AUTO BLOCKED ✓
```

---

## Author

**Kumar Aditya**
[LinkedIn](https://www.linkedin.com/in/kumar-aditya-567403278/)
