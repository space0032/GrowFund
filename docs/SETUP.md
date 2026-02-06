# Development Setup Guide

## Prerequisites

### For Backend Development
- Java Development Kit (JDK) 17 or higher
- Maven 3.6+
- PostgreSQL 14+
- IDE: IntelliJ IDEA or Eclipse
- Git

### For Android App Development
- Android Studio Hedgehog (2023.1.1) or later
- Android SDK (API level 24-34)
- Java Development Kit (JDK) 17
- Android device or emulator
- Git

## Backend Setup

### 1. Clone the Repository
```bash
git clone https://github.com/space0032/GrowFund.git
cd GrowFund/backend
```

### 2. Configure PostgreSQL Database
```bash
# Create database
createdb growfund

# Or using psql
psql -U postgres
CREATE DATABASE growfund;
```

### 3. Configure Application Properties
Create/edit `backend/src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/growfund
    username: your_username
    password: your_password
```

### 4. Build and Run
```bash
# Using Maven
cd backend
mvn clean install
mvn spring-boot:run

# The API will be available at http://localhost:8080/api
```

### 5. Test API Endpoints
```bash
# Test compound interest calculation
curl "http://localhost:8080/api/investments/calculate/compound?principal=10000&annualRate=8.5&months=12"
```

## Android App Setup

### 1. Open Project in Android Studio
1. Launch Android Studio
2. Select "Open an Existing Project"
3. Navigate to `GrowFund/android-app`
4. Wait for Gradle sync to complete

### 2. Configure Firebase (Optional)
1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Create a new project or use existing
3. Add Android app with package name: `com.growfund.seedtowealth`
4. Download `google-services.json`
5. Place it in `android-app/app/` directory

### 3. Update API Configuration
Edit `android-app/app/src/main/java/com/growfund/seedtowealth/config/ApiConfig.java`:
```java
public static final String BASE_URL = "http://10.0.2.2:8080/api/"; // For emulator
// or
public static final String BASE_URL = "http://YOUR_IP:8080/api/"; // For physical device
```

### 4. Build and Run
1. Connect Android device or start emulator
2. Click Run button (Shift+F10)
3. Select target device
4. App will install and launch

## Database Schema Setup

The backend uses Hibernate with `ddl-auto: update`, so tables will be created automatically on first run.

For manual schema creation, refer to:
```bash
# Generate schema SQL
cd backend
mvn clean compile
# Check target/generated-sources for schema
```

## Environment Variables

### Backend
```bash
export DB_USERNAME=postgres
export DB_PASSWORD=yourpassword
export FIREBASE_CONFIG_PATH=/path/to/firebase-config.json
```

### Android
Configure in `local.properties`:
```properties
sdk.dir=/path/to/Android/Sdk
```

## Development Workflow

### Backend Development
1. Make changes to Java files
2. Run tests: `mvn test`
3. Build: `mvn clean install`
4. Run: `mvn spring-boot:run`

### Android Development
1. Make changes to Java/XML files
2. Sync Gradle
3. Run on emulator/device
4. Check Logcat for debugging

## Testing

### Backend Tests
```bash
cd backend
mvn test
```

### Android Tests
```bash
cd android-app
./gradlew test           # Unit tests
./gradlew connectedAndroidTest  # Instrumented tests
```

## Troubleshooting

### Common Issues

**Backend won't start:**
- Check PostgreSQL is running: `pg_isready`
- Verify database credentials in application.yml
- Check port 8080 is not in use: `lsof -i :8080`

**Android build fails:**
- Clean build: Build → Clean Project
- Invalidate caches: File → Invalidate Caches / Restart
- Check Gradle version compatibility

**Cannot connect to backend from Android:**
- For emulator, use `10.0.2.2` instead of `localhost`
- For physical device, ensure same WiFi network
- Check firewall settings

## IDE Configuration

### IntelliJ IDEA (Backend)
1. Import as Maven project
2. Set JDK to 17: File → Project Structure → Project SDK
3. Enable annotation processing for Lombok
4. Install Spring Boot plugin

### Android Studio
1. Install recommended plugins when prompted
2. Configure code style: Settings → Editor → Code Style
3. Enable auto-import for Java

## Next Steps

After setup:
1. Review [ARCHITECTURE.md](ARCHITECTURE.md) for system design
2. Check [API.md](API.md) for endpoint documentation
3. Read [CONTRIBUTING.md](CONTRIBUTING.md) for contribution guidelines
4. Explore code in `backend/src` and `android-app/app/src`

## Support

For issues or questions:
- Create an issue on GitHub
- Check existing documentation in `/docs`
- Review code comments
