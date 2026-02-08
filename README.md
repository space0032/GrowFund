# GrowFund - "Seed to Wealth"

## 🌾 Financial Empowerment for Farmers

GrowFund is a gamified financial literacy application designed specifically for Indian farmers to educate them about investments and wealth management. The app uses farm-specific scenarios and engaging gameplay to teach financial planning and investment strategies in a simple, easy-to-understand, and interactive way.

## 📖 Table of Contents

- [Vision & Goals](#vision--goals)
- [Features](#features)
- [Target Users](#target-users)
- [Technology Stack](#technology-stack)
- [Getting Started](#getting-started)
- [Project Structure](#project-structure)
- [Game Mechanics](#game-mechanics)
- [Investment Education](#investment-education)
- [Development Roadmap](#development-roadmap)
- [Contributing](#contributing)
- [License](#license)

## 🎯 Vision & Goals

### Purpose
Financial empowerment of farmers by teaching investment concepts through an engaging gamified experience.

### Primary Goal
Teach farmers to understand and start applying basic investment principles (saving, investing, and wealth management) through interactive simulations and games.

### Secondary Goal
Build confidence in using financial tools like savings accounts, insurance, mutual funds, and credit mechanisms.

## 👥 Target Users

- **Small and marginal farmers in India**, focused on rural areas
- Farmers with **limited digital exposure** and varying literacy levels
- **Smartphone users** (low to mid-range Android devices)
- Primary regions: Rural India across multiple states
- Language support: Hindi, Tamil, Telugu, Marathi, Bengali, Gujarati, and English

## ✨ Features

### 🎮 Interactive Gameplay
- **Role-Playing**: Manage a virtual farm and make financial decisions
- **Real-Life Scenarios**: Deal with challenges like natural disasters, market volatility, and pest infestations
- **Decision Making**: Choose between investing in better seeds, saving for emergencies, or expanding the farm
- **Progress Tracking**: Earn coins, gain experience, and level up

### 💰 Investment Simulations
Learn about practical investment options:
- **Government Schemes**: PM Kisan Maan Dhan Yojana, Kisan Vikas Patra
- **Banking Products**: Fixed Deposits, Savings Accounts
- **Investment Vehicles**: Agricultural Mutual Funds
- **Digital Payments**: UPI integration understanding
- **Insurance**: Crop and life insurance basics
- **NABARD Initiatives**: Subsidies and support programs

### 📊 Financial Tools
- **Investment Analytics**: ROI calculators and portfolio value tracking
- **Recommendation Engine**: Personalized investment advice based on risk profile
- **Interest Calculators**: Compound and simple interest calculations
- **Savings Tracker**: Monitor emergency funds and savings goals
- **Budget Planning**: Allocate resources effectively

### 🏆 Gamification & Social
- **Leaderboards**: Compete with other farmers based on net worth
- **Achievements**: Unlock badges for milestones
- **Quizzes**: Test financial knowledge with interactive quizzes
- **Equipment Shop**: Buy tools (Tractors, Sprinklers) to improve efficiency
- **Random Events**: Dynamic weather and market events (Droughts, Market Surges)

### 🌍 Localization
- **Multi-language Support**: Content in 7 Indian languages
- **Audio Support**: Voice guidance for low-literacy users
- **Simple Visuals**: Icon-based navigation with minimal text
- **Cultural Relevance**: Region-specific content and examples

### 📴 Offline Capability
- Play and learn without constant internet connection
- Local data storage with Room Database
- Sync when online

## 🛠 Technology Stack

### Frontend (Android App)
- **Language**: Java
- **Platform**: Android (API 24+)
- **UI Framework**: Material Design
- **Build Tool**: Gradle
- **Database**: Room Database (SQLite)
- **Networking**: Retrofit 2, OkHttp
- **Authentication**: Firebase Auth
- **Database**: Room Database (Offline), Firestore (Feedback)
- **Analytics**: Firebase Analytics, Crashlytics, MPAndroidChart
- **Updates**: Google Play In-App Updates
- **Image Loading**: Glide

### Backend (API Server)
- **Framework**: Spring Boot 3.2
- **Language**: Java 17
- **Database**: PostgreSQL
- **ORM**: Hibernate/JPA
- **Security**: Spring Security
- **Cloud**: Firebase Admin SDK
- **Financial Calculations**: Apache Commons Math
- **Build Tool**: Maven

### Infrastructure
- **Database**: PostgreSQL (Backend), Firestore (Feedback)
- **Cloud Services**: Firebase (Auth, Firestore, Crashlytics, Analytics)
- **API Protocol**: RESTful APIs with JSON
- **Version Control**: Git/GitHub

## 🚀 Getting Started

### Prerequisites
- Java Development Kit (JDK) 17+
- Android Studio Hedgehog (2023.1.1) or later
- PostgreSQL 14+
- Maven 3.6+
- Git

### Quick Start

#### Backend Setup
```bash
# Clone the repository
git clone https://github.com/space0032/GrowFund.git
cd GrowFund/backend

# Configure database in application.yml
# Create PostgreSQL database
createdb growfund

# Build and run
mvn clean install
mvn spring-boot:run

# API will be available at http://localhost:8080/api
```

#### Android App Setup
```bash
# Open android-app folder in Android Studio
cd GrowFund/android-app

# Sync Gradle dependencies
# Configure Firebase (download google-services.json)
# Run on emulator or device
```

For detailed setup instructions, see [SETUP.md](docs/SETUP.md).

## 📁 Project Structure

```
GrowFund/
├── backend/                    # Spring Boot Backend
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/growfund/
│   │   │   │   ├── controller/     # REST Controllers
│   │   │   │   ├── service/        # Business Logic
│   │   │   │   ├── model/          # Entity Models
│   │   │   │   ├── repository/     # Data Access Layer
│   │   │   │   ├── config/         # Configuration
│   │   │   │   └── dto/            # Data Transfer Objects
│   │   │   └── resources/
│   │   │       └── application.yml # Configuration
│   │   └── test/                   # Backend Tests
│   └── pom.xml                     # Maven Dependencies
│
├── android-app/                # Android Application
│   ├── app/
│   │   ├── src/
│   │   │   ├── main/
│   │   │   │   ├── java/          # Java Source Code
│   │   │   │   ├── res/           # Resources
│   │   │   │   │   ├── layout/    # UI Layouts
│   │   │   │   │   ├── values/    # Strings, Colors, Themes
│   │   │   │   │   ├── values-hi/ # Hindi Localization
│   │   │   │   │   └── drawable/  # Images and Icons
│   │   │   │   └── AndroidManifest.xml
│   │   │   └── test/              # App Tests
│   │   └── build.gradle           # App Dependencies
│   └── build.gradle               # Project Build Config
│
├── docs/                       # Documentation
│   ├── ARCHITECTURE.md         # System Architecture
│   ├── SETUP.md                # Setup Guide
│   └── DATABASE_SCHEMA.md      # Database Design
│
└── README.md                   # This file
```

## 🎮 Game Mechanics

### Core Gameplay Loop

1. **Planning Phase**
   - Choose crops to plant
   - Allocate budget for seeds, fertilizers, equipment
   - Set aside emergency funds

2. **Growing Phase**
   - Monitor crop growth
   - Handle random events (weather, pests, market changes)
   - Make investment decisions with surplus funds

3. **Harvest Phase**
   - Collect harvest and profits
   - Calculate returns on investments
   - Unlock new features and equipment

4. **Progress & Rewards**
   - Earn experience points and coins
   - Level up to unlock advanced features
   - Achieve milestones and badges
   - Compete on leaderboards

### Random Events
- **Weather**: Droughts, floods, favorable conditions
- **Market**: Price fluctuations, demand changes
- **Challenges**: Pest infestations, equipment breakdowns
- **Opportunities**: Government subsidies, bonus harvests

### Resource Management
- **Coins**: Virtual currency for transactions
- **Land**: Expandable farm size
- **Equipment**: Tools that improve efficiency
- **Savings**: Emergency fund and investment capital

## 📚 Investment Education

### Covered Topics

#### 1. **Savings**
- Importance of emergency funds
- Regular savings habits
- Bank savings accounts
- Interest calculation basics

#### 2. **Government Schemes**
- **PM Kisan Maan Dhan Yojana**: Pension scheme for farmers
- **Kisan Vikas Patra**: Post office savings certificate
- **NABARD Programs**: Agricultural development schemes
- **Crop Insurance**: Pradhan Mantri Fasal Bima Yojana

#### 3. **Banking Products**
- **Fixed Deposits**: Guaranteed returns
- **Recurring Deposits**: Regular savings
- **Savings Accounts**: Liquidity and safety
- **UPI/Digital Payments**: Modern banking

#### 4. **Investment Vehicles**
- **Mutual Funds**: Agricultural and equity funds
- **Gold**: Traditional investment
- **Land Improvements**: Long-term value addition

#### 5. **Risk Management**
- **Insurance**: Crop and life insurance
- **Diversification**: Multiple income sources
- **Emergency Planning**: Financial safety nets

#### 6. **Credit & Loans**
- **Kisan Credit Card**: Agricultural credit
- **Loan Management**: Responsible borrowing
- **Interest Understanding**: Cost of credit

### Learning Methodology
- **Learning by Doing**: Practical simulations
- **Visual Learning**: Charts and graphs
- **Immediate Feedback**: See results of decisions
- **Progressive Difficulty**: Start simple, grow complex
- **Culturally Relevant**: Real-world farmer scenarios

## 🗓 Development Roadmap

### Phase 1-22: Development & Polish (COMPLETED) ✅
- [x] **Core Systems**: Auth, Harvest, Farm Growth
- [x] **Game Mechanics**: Weather, Random Events, Equipment, Leaderboards
- [x] **Financial Features**: Investment System, Analytics, Quizzes
- [x] **UI/UX**: Localization (7 langs), Profiles, Settings, Dark Mode support
- [x] **Offline Support**: Room Database synchronization
- [x] **Production Readiness**: Crashlytics, Analytics, In-App Updates, Feedback Form

### Phase 23: Launch & Testing (Current) 🚀
- [ ] User Acceptance Testing (UAT)
- [ ] Performance Testing
- [ ] Google Play Store Release

## 👨‍💻 Team Roles

### Required Roles
1. **Project Manager**: Task coordination, timeline management
2. **Backend Developer**: Spring Boot API development
3. **Android Developer**: Mobile app development
4. **Game Developer** (Optional): LibGDX game mechanics
5. **UI/UX Designer**: App visuals and user experience
6. **Content Writer**: Educational content creation
7. **Financial Advisor**: Investment scheme expertise
8. **QA Engineer**: Testing and quality assurance

## 🤝 Contributing

We welcome contributions from developers, designers, and domain experts!

### How to Contribute
1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### Development Guidelines
- Follow Java coding standards
- Write unit tests for new features
- Update documentation
- Use meaningful commit messages
- Ensure backward compatibility

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🙏 Acknowledgments

- Indian farmers who inspired this project
- Government of India for agricultural schemes
- Open source community for tools and libraries
- Agricultural organizations and NGOs

## 📞 Contact & Support

- **Issues**: [GitHub Issues](https://github.com/space0032/GrowFund/issues)
- **Discussions**: [GitHub Discussions](https://github.com/space0032/GrowFund/discussions)
- **Documentation**: [/docs](docs/)

## 🌟 Vision

> "Empowering every farmer with the knowledge and tools to build financial security and prosperity through technology and education."

---

**Made with ❤️ for Indian Farmers**
