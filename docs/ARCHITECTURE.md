# GrowFund Architecture

## System Overview

GrowFund "Seed to Wealth" is a gamified financial literacy application designed for Indian farmers. The system follows a client-server architecture with the following components:

### Components

1. **Android Mobile App (Frontend)**
   - Target Platform: Android (API 24+)
   - Language: Java
   - Framework: Android SDK with Material Design
   - Offline Support: Room Database
   - Localization: Multiple Indian languages (Hindi, Tamil, Telugu, Marathi, Bengali, Gujarati)

2. **Backend API Server**
   - Framework: Spring Boot 3.2
   - Language: Java 17
   - Database: PostgreSQL
   - Cloud Integration: Firebase (Authentication, Cloud Firestore)
   - Financial Calculations: Apache Commons Math

3. **Database**
   - Primary: PostgreSQL for structured data
   - Cache/Offline: Room Database (SQLite on Android)
   - Cloud: Firebase Firestore for real-time sync

## Architecture Diagram

```
┌─────────────────────────────────────────────────────────────┐
│                    Android Mobile App                        │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │   UI Layer   │  │  Game Engine │  │ Room Database│      │
│  │  (Activities)│  │   (LibGDX)   │  │  (Offline)   │      │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘      │
│         │                 │                  │               │
│  ┌──────┴─────────────────┴──────────────────┴───────┐      │
│  │           ViewModel & Repository Layer             │      │
│  └──────────────────────┬─────────────────────────────┘      │
└─────────────────────────┼──────────────────────────────────┘
                          │ REST API / Firebase
                          │
┌─────────────────────────┼──────────────────────────────────┐
│                         │    Spring Boot Backend            │
│  ┌──────────────────────┴─────────────────────────────┐    │
│  │              REST Controllers                       │    │
│  └──────────────────────┬─────────────────────────────┘    │
│  ┌──────────────────────┴─────────────────────────────┐    │
│  │          Service Layer (Business Logic)             │    │
│  │  - Investment Calculations (Commons Math)           │    │
│  │  - Game Mechanics                                   │    │
│  │  - User Management                                  │    │
│  └──────────────────────┬─────────────────────────────┘    │
│  ┌──────────────────────┴─────────────────────────────┐    │
│  │           Repository Layer (JPA)                    │    │
│  └──────────────────────┬─────────────────────────────┘    │
└─────────────────────────┼──────────────────────────────────┘
                          │
                ┌─────────┴──────────┐
                │                    │
        ┌───────┴────────┐  ┌────────┴────────┐
        │   PostgreSQL   │  │    Firebase     │
        │    Database    │  │  (Auth/Firestore)│
        └────────────────┘  └─────────────────┘
```

## Data Models

### Core Entities

1. **User**
   - User profile and authentication
   - Progress tracking (level, coins, experience)
   - Language preference
   - Location (state, district)

2. **Farm**
   - Virtual farm representation
   - Land size and resources
   - Savings and emergency fund

3. **Crop**
   - Planted crops with investment tracking
   - Growth simulation
   - Weather impact modeling

4. **Investment**
   - Investment portfolio tracking
   - Multiple scheme support (Mutual Funds, FD, KVP, etc.)
   - Interest calculation and returns

5. **Equipment**
   - Farm equipment and tools
   - Progressive unlocking system

6. **Achievement**
   - Gamification rewards
   - Progress milestones

## API Endpoints

### Investment API
- `POST /api/investments` - Create new investment
- `GET /api/investments/user/{userId}` - Get user investments
- `GET /api/investments/user/{userId}/active` - Get active investments
- `PUT /api/investments/{id}/update-value` - Update investment value
- `GET /api/investments/calculate/compound` - Calculate compound interest
- `GET /api/investments/calculate/simple` - Calculate simple interest

### User API (To be implemented)
- `POST /api/users/register` - Register new user
- `POST /api/users/login` - User login
- `GET /api/users/{id}` - Get user profile
- `PUT /api/users/{id}` - Update user profile

### Farm API (To be implemented)
- `GET /api/farms/user/{userId}` - Get user's farm
- `POST /api/farms/{id}/crops` - Plant new crop
- `PUT /api/farms/{id}/crops/{cropId}/harvest` - Harvest crop

## Security

- Spring Security for authentication and authorization
- Firebase Authentication integration
- JWT token-based API access
- Role-based access control (RBAC)

## Offline Capability

- Room Database for local data persistence
- Sync strategy for online/offline modes
- Conflict resolution for data sync

## Scalability Considerations

- Stateless API design
- Database connection pooling
- Caching strategies (Redis - future)
- CDN for static assets (future)
- Load balancing (future)

## Technology Stack Summary

| Layer | Technology |
|-------|------------|
| Frontend | Android SDK, Java, Material Design |
| Game Engine | LibGDX (optional) |
| Backend | Spring Boot 3.2, Java 17 |
| Database | PostgreSQL, Firebase Firestore |
| Local Storage | Room Database (SQLite) |
| Authentication | Firebase Auth, Spring Security |
| API Protocol | REST with JSON |
| Build Tools | Gradle (Android), Maven (Backend) |
| Financial Lib | Apache Commons Math |
