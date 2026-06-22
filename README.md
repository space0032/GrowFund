# 🌾 GrowFund - "Seed to Wealth"

> **Empowering Indian farmers with financial literacy through gamified learning**
>
> A comprehensive gamified financial literacy platform designed specifically for rural farmers to understand investments, wealth management, and government schemes through engaging gameplay and real-world agricultural scenarios.

[![Java](https://img.shields.io/badge/Java-17+-orange?style=flat-square&logo=java)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-6DB33F?style=flat-square&logo=springboot)](https://spring.io/projects/spring-boot)
[![Android](https://img.shields.io/badge/Android-API%2024+-3DDC84?style=flat-square&logo=android)](https://www.android.com/)
[![Firebase](https://img.shields.io/badge/Firebase-Integrated-FFCA28?style=flat-square&logo=firebase)](https://firebase.google.com/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-14+-336791?style=flat-square&logo=postgresql)](https://www.postgresql.org/)
[![License](https://img.shields.io/badge/License-MIT-green?style=flat-square)](LICENSE)

---

## 📋 Table of Contents

- [Overview](#overview)
- [Problem & Solution](#problem--solution)
- [Key Features](#key-features)
- [Tech Stack](#tech-stack)
- [Quick Start](#quick-start)
- [Game Mechanics](#game-mechanics)
- [Educational Content](#educational-content)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [Contributing](#contributing)

---

## 🎯 Overview

**GrowFund** is a social-impact fintech application addressing financial literacy gaps in rural India. By combining gamification with agricultural scenarios, it teaches farmers about:

- 💰 Savings & Emergency funds
- 📈 Investment vehicles (Mutual Funds, FDs, Government schemes)
- 🏦 Banking products & Digital payments
- 🌾 Government agricultural schemes (PM Kisan, Kisan Vikas Patra)
- 🛡️ Insurance basics (Crop, Life, Health)
- 📊 Risk management & Diversification

**Target Users**: Small-to-marginal farmers in rural India with limited digital exposure and varying literacy levels.

**Impact**: Make financial empowerment accessible to 10M+ farmers through their smartphones.

---

## 🔥 Problem & Solution

### The Problem
- ❌ ~87% of Indian farmers lack basic financial literacy
- ❌ Low awareness of government schemes and banking products
- ❌ Limited access to financial advisors in rural areas
- ❌ Language barriers & digital illiteracy
- ❌ Complex financial concepts presented in formal language

### Our Solution
- ✅ **Gamified Learning**: Farm management gameplay makes learning fun
- ✅ **Localized Content**: 7 Indian languages with audio support
- ✅ **Real-World Scenarios**: Agriculture-specific financial situations
- ✅ **Offline Capability**: Works in low-connectivity regions
- ✅ **Voice-Driven**: Audio guidance for low-literacy users

---

## ✨ Key Features

### 🎮 Interactive Gameplay

| Feature | Description |
|---------|-------------|
| **Farm Management** | Manage a virtual farm, make financial decisions, face real-world challenges |
| **Role-Playing** | Experience financial scenarios as a farmer (planting, harvesting, investing) |
| **Dynamic Events** | Random weather events, market volatility, pest infestations, government subsidies |
| **Progression System** | Level up, earn coins, unlock equipment, track wealth growth |

### 💰 Investment Education Hub

**Government Schemes**
- PM Kisan Maan Dhan Yojana (Pension scheme)
- Kisan Vikas Patra (Savings certificates)
- NABARD initiatives & subsidies
- Pradhan Mantri Fasal Bima Yojana (Crop insurance)

**Banking Products**
- Fixed Deposits (FDs) with ROI calculators
- Savings Accounts & Recurring Deposits
- Digital Payment systems (UPI, Mobile Banking)
- Loan products & Credit understanding

**Investment Vehicles**
- Agricultural Mutual Funds
- Gold savings
- Land improvements as investments
- Diversification strategies

### 🏆 Gamification & Social Features

- **Leaderboards**: Compete with other farmers based on virtual net worth
- **Achievements**: Unlock badges for milestones (First Investment, 1M coins, etc.)
- **Interactive Quizzes**: Test financial knowledge with rewards
- **Equipment Shop**: Buy virtual tools (Tractors, Sprinklers) to improve farm efficiency
- **Progressive Unlocking**: Advanced features unlock as you progress

### 🌍 Localization & Accessibility

| Feature | Details |
|---------|---------|
| **Multi-Language** | English, Hindi, Tamil, Telugu, Marathi, Bengali, Gujarati |
| **Audio Support** | Voice guidance & text-to-speech for low-literacy users |
| **Offline Mode** | Complete gameplay without internet, sync when online |
| **Simple UI** | Icon-based navigation with minimal text complexity |
| **Voice Queries** | Ask questions in your language using speech-to-text |

### 📊 Financial Tools & Analytics

- **ROI Calculator**: Compute returns on various investments
- **Portfolio Tracker**: Monitor virtual investments
- **Savings Goal Tracker**: Set and track financial targets
- **Budget Planner**: Allocate farm resources effectively
- **Recommendation Engine**: AI-powered investment advice based on risk profile

---

## 🛠️ Tech Stack

### Mobile Application (Android)
| Component | Technology |
|-----------|-----------|
| **Language** | Java |
| **Platform** | Android 24+ (Material Design) |
| **Build Tool** | Gradle |
| **UI Framework** | Material Design Components |
| **Local DB** | Room Database (SQLite) |
| **Networking** | Retrofit 2 + OkHttp |
| **Auth** | Firebase Authentication |
| **Analytics** | Firebase Analytics & Crashlytics |
| **Notifications** | Firebase Cloud Messaging (FCM) |
| **Charts** | MPAndroidChart for analytics |
| **Image Loading** | Glide |

### Backend Server
| Component | Technology |
|-----------|-----------|
| **Framework** | Spring Boot 3.2 |
| **Language** | Java 17+ |
| **Build Tool** | Maven |
| **Database** | PostgreSQL 14+ |
| **ORM** | Hibernate/JPA |
| **Security** | Spring Security, JWT tokens |
| **APIs** | RESTful JSON APIs |
| **Cloud** | Firebase Admin SDK |
| **Libraries** | Apache Commons Math (financial calculations) |

### Infrastructure & Services
| Service | Purpose |
|---------|---------|
| **Firebase** | Auth, Firestore (feedback), Analytics, Crashlytics |
| **PostgreSQL** | Persistent user data, game state, analytics |
| **SMTP** | Transactional emails (OTP, notifications) |
| **Google Play** | Distribution, In-app updates, Review system |

---

## 🚀 Quick Start

### Prerequisites

```
✓ Java Development Kit (JDK) 17+
✓ Android Studio Hedgehog (2023.1.1) or later
✓ PostgreSQL 14+
✓ Maven 3.6+
✓ Git
✓ Firebase Project (for development/production)
```

### Backend Setup

**1. Clone & Navigate**
```bash
git clone https://github.com/space0032/GrowFund.git
cd GrowFund/backend
```

**2. Configure Database**
```bash
# Create PostgreSQL database
createdb growfund

# Create .env file with credentials
cat > .env << EOF
DB_HOST=localhost
DB_PORT=5432
DB_NAME=growfund
DB_USER=postgres
DB_PASSWORD=your_password
JWT_SECRET=your_secret_key_here
FIREBASE_API_KEY=your_firebase_key
EOF
```

**3. Build & Run**
```bash
# Build the application
mvn clean install

# Run the Spring Boot server
mvn spring-boot:run

# API available at http://localhost:8080/api
```

### Android App Setup

**1. Open in Android Studio**
```bash
cd GrowFund/android-app
# Open this folder in Android Studio
```

**2. Configure Firebase**
- Download `google-services.json` from Firebase Console
- Place it in `android-app/app/`

**3. Build & Run**
```bash
# Sync Gradle dependencies
./gradlew sync

# Run on emulator
./gradlew installDebug

# Run on device
./gradlew installDebug
```

---

## 🎮 Game Mechanics

### Core Gameplay Loop

```
┌─────────────────────────────────────────┐
│         1. PLANNING PHASE               │
│  - Choose crops to plant               │
│  - Allocate budget (seeds, tools)     │
│  - Set aside emergency funds          │
└────────────────┬────────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────────┐
│       2. GROWING PHASE (7-14 days)     │
│  - Monitor crop growth                 │
│  - Handle random events                │
│  - Make investment decisions           │
│  - Complete mini-games/quizzes        │
└────────────────┬────────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────────┐
│       3. HARVEST PHASE                  │
│  - Collect harvest & profits           │
│  - Calculate investment returns        │
│  - Earn experience & coins             │
│  - Unlock new features                 │
└────────────────┬────────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────────┐
│   4. PROGRESS & REWARDS                 │
│  - Level up (unlock advanced features) │
│  - Achieve milestones (badges)        │
│  - Compete on leaderboards            │
│  - Gain wealth (virtual net worth)    │
└─────────────────────────────────────────┘
```

### Random Events System

| Event Type | Examples | Impact |
|-----------|----------|--------|
| **Weather** | Droughts, Floods, Favorable conditions | Crop yield changes |
| **Market** | Price surges, Demand drops | Revenue increases/decreases |
| **Challenges** | Pest infestations, Equipment breakdown | Cost unexpected expenses |
| **Opportunities** | Government subsidies, Bonus harvests | Extra income |

### Resource Management

- **Coins**: Virtual currency for all in-game transactions
- **Experience (XP)**: Earned through gameplay, unlocks features
- **Farm Land**: Expandable by investing coins
- **Equipment**: Tools that improve farm efficiency
- **Savings**: Emergency fund + investment capital

---

## 📚 Educational Content

### Module 1: Savings Fundamentals
- Importance of emergency funds (3-6 month buffer)
- Regular savings habits and discipline
- Bank savings accounts & interest calculation
- Compound interest power demonstration

### Module 2: Government Schemes
- **PM Kisan Scheme**: ₹6000 yearly, eligibility, application
- **Kisan Vikas Patra**: Post office savings with guaranteed returns
- **NABARD Programs**: Agricultural development & credit
- **Crop Insurance**: Risk protection for farmers

### Module 3: Banking Products
- **Fixed Deposits (FDs)**: Security, returns, lock-in periods
- **Recurring Deposits (RDs)**: Automatic savings mechanism
- **Savings Accounts**: Liquidity and safety
- **Digital Payments**: UPI, Mobile Banking, Online transfers

### Module 4: Investment Strategies
- **Mutual Funds**: Agricultural + equity funds
- **Diversification**: Not putting all eggs in one basket
- **Risk vs. Return**: Understanding investment profiles
- **Long-term vs. Short-term**: Time-based strategies

### Module 5: Risk Management
- **Insurance Types**: Crop, Life, Health insurance
- **Emergency Planning**: Financial safety nets
- **Debt Management**: Understanding loans & interest
- **Loan Repayment**: Responsible borrowing practices

### Learning Methodology

✅ **Learning by Doing**: Practical simulations with real consequences  
✅ **Visual Learning**: Charts, graphs, progress indicators  
✅ **Immediate Feedback**: See results of financial decisions  
✅ **Progressive Difficulty**: Start simple, advance gradually  
✅ **Culturally Relevant**: Real-world farmer scenarios  

---

## 📁 Project Structure

```
GrowFund/
├── backend/                           # Spring Boot Backend Server
│   ├── src/main/java/com/growfund/
│   │   ├── controller/                # REST API endpoints
│   │   │   ├── AuthController.java
│   │   │   ├── GameController.java
│   │   │   ├── InvestmentController.java
│   │   │   └── SchemeController.java
│   │   ├── service/                   # Business logic
│   │   │   ├── GameService.java
│   │   │   ├── InvestmentService.java
│   │   │   └── UserService.java
│   │   ├── model/                     # Entity models (JPA)
│   │   │   ├── User.java
│   │   │   ├── GameState.java
│   │   │   ├── Investment.java
│   │   │   └── Scheme.java
│   │   ├── repository/                # Data access layer
│   │   ├── config/                    # Configuration classes
│   │   └── dto/                       # Data Transfer Objects
│   ├── src/main/resources/
│   │   ├── application.yml            # Configuration
│   │   └── schema.sql                 # Database schema
│   ├── pom.xml                        # Maven dependencies
│   └── Dockerfile
│
├── android-app/                       # Android Mobile Application
│   ├── app/
│   │   ├── src/main/
│   │   │   ├── java/com/growfund/
│   │   │   │   ├── activity/          # UI Activities
│   │   │   │   │   ├── MainActivity.java
│   │   │   │   │   ├── GameActivity.java
│   │   │   │   │   └── LoginActivity.java
│   │   │   │   ├── fragment/          # Fragments
│   │   │   │   │   ├── GameFragment.java
│   │   │   │   │   ├── InvestmentFragment.java
│   │   │   │   │   └── LeaderboardFragment.java
│   │   │   │   ├── database/          # Room Database
│   │   │   │   ├── service/           # Business logic
│   │   │   │   ├── api/               # Retrofit API client
│   │   │   │   ├── model/             # Data models
│   │   │   │   └── util/              # Utility classes
│   │   │   ├── res/
│   │   │   │   ├── layout/            # XML layouts
│   │   │   │   ├── drawable/          # Images & icons
│   │   │   │   ├── values/            # Colors, strings, themes
│   │   │   │   ├── values-hi/         # Hindi strings
│   │   │   │   └── values-ta/         # Tamil strings (etc.)
│   │   │   └── AndroidManifest.xml
│   │   └── build.gradle
│   ├── build.gradle                   # Project build config
│   └── settings.gradle
│
├── docs/                              # Documentation
│   ├── ARCHITECTURE.md                # System design
│   ├── API_REFERENCE.md               # Backend API docs
│   ├── SETUP.md                       # Detailed setup guide
│   ├── GAME_DESIGN.md                 # Game mechanics doc
│   └── DATABASE_SCHEMA.md             # DB design
│
└── README.md                          # This file
```

---

## 🔑 API Reference

### Authentication

```http
POST /api/auth/register
Content-Type: application/json

{
  "phone": "+919876543210",
  "password": "secure_password",
  "name": "Farmer Name"
}
```

```http
POST /api/auth/login
Content-Type: application/json

{
  "phone": "+919876543210",
  "password": "secure_password"
}

Response:
{
  "token": "jwt_token_here",
  "user": { ... }
}
```

### Game API

```http
GET /api/game/state
Authorization: Bearer {token}

Response:
{
  "farmSize": 5,
  "coins": 50000,
  "level": 3,
  "crops": [
    { "name": "Rice", "status": "growing", "days": 7 }
  ],
  "investments": [
    { "type": "FD", "amount": 10000, "roi": 5.5 }
  ]
}
```

### Investment API

```http
POST /api/investments/create
Authorization: Bearer {token}
Content-Type: application/json

{
  "investmentType": "MUTUAL_FUND",
  "amount": 5000,
  "scheme": "Agricultural Fund"
}
```

```http
GET /api/schemes
Response:
[
  {
    "id": 1,
    "name": "PM Kisan Maan Dhan Yojana",
    "description": "Pension scheme for farmers",
    "minAge": 18,
    "maxAge": 40
  }
]
```

---

## 🎯 Development Roadmap

### ✅ Completed (Phase 1-22)
- [x] Core game systems (farm, harvest, progression)
- [x] Investment simulation engine
- [x] Government schemes database
- [x] Multi-language support (7 languages)
- [x] Offline mode with Room Database
- [x] Firebase integration (Auth, Analytics, Crashlytics)
- [x] Interactive quizzes
- [x] Leaderboards & achievements
- [x] Admin dashboard
- [x] In-app updates support

### 🚀 Current (Phase 23: Launch)
- [ ] User Acceptance Testing (UAT)
- [ ] Performance optimization
- [ ] Google Play Store release
- [ ] Marketing campaign

### 🔮 Future (Phase 24+)
- [ ] Web version for accessibility
- [ ] Advanced ML-based recommendations
- [ ] Integration with actual banking APIs
- [ ] Real investment portfolio tracking
- [ ] Government scheme auto-application
- [ ] Community features (social farming)
- [ ] Rewards redemption (real incentives)

---

## 🤝 Contributing

We welcome contributions from developers, designers, and domain experts!

### How to Contribute

1. **Fork** the repository
2. **Create** a feature branch: `git checkout -b feature/YourFeature`
3. **Commit** changes: `git commit -m 'Add YourFeature'`
4. **Push** to branch: `git push origin feature/YourFeature`
5. **Open** a Pull Request

### Development Guidelines

- ✅ Follow Java naming conventions (camelCase for variables, PascalCase for classes)
- ✅ Write meaningful commit messages
- ✅ Add comments for complex logic
- ✅ Test thoroughly before submitting
- ✅ Update documentation if needed
- ✅ Maintain backward compatibility

---

## 📊 Impact Metrics

**Target Impact:**
- 🌾 Reach 100K+ farmers in Year 1
- 💰 Help farmers increase savings by 20%
- 📈 Increase investment awareness from 20% to 70%
- 🎓 Complete financial literacy program completion rate: 80%+

---

## 🔒 Security & Privacy

- ✅ End-to-end encrypted communications
- ✅ Secure authentication with JWT tokens
- ✅ OWASP-compliant API design
- ✅ Data encryption at rest
- ✅ Regular security audits
- ✅ GDPR-compliant data handling
- ✅ User data never shared without consent

---

## 📄 License

This project is licensed under the **MIT License** - see [LICENSE](LICENSE) file for details.

---

## 🙏 Acknowledgments

- Indian farmers who inspire this mission
- Government of India for agricultural schemes
- Open-source community for tools and libraries
- NGOs and agricultural organizations supporting rural development
- Firebase for robust cloud infrastructure
- Spring Boot team for the excellent framework

---

## 📞 Support & Contact

- **Issues**: [GitHub Issues](https://github.com/space0032/GrowFund/issues)
- **Discussions**: [GitHub Discussions](https://github.com/space0032/GrowFund/discussions)
- **Email**: reach out via GitHub profile
- **Documentation**: See `/docs` folder

---

## 🌟 Vision

> **"Empowering every farmer with the knowledge and tools to build financial security and prosperity through technology and education."**

---

<div align="center">

**Made with ❤️ for Indian Farmers**

⭐ **Star this repo if you believe in financial empowerment for farmers!**

</div>
