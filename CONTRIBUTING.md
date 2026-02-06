# Contributing to GrowFund

Thank you for your interest in contributing to GrowFund - "Seed to Wealth"! This project aims to empower Indian farmers with financial literacy through gamified learning.

## Code of Conduct

By participating in this project, you agree to:
- Be respectful and inclusive
- Focus on what's best for the community and farmers
- Show empathy towards other contributors
- Accept constructive criticism gracefully

## How Can I Contribute?

### Reporting Bugs

Before creating bug reports, please check existing issues. When creating a bug report, include:

- **Clear title and description**
- **Steps to reproduce** the issue
- **Expected behavior** vs actual behavior
- **Screenshots** if applicable
- **Environment details** (Android version, device, backend version)

### Suggesting Enhancements

Enhancement suggestions are welcome! Please include:

- **Clear use case** for farmers
- **Detailed description** of the feature
- **Mockups or examples** if applicable
- **Benefits** to target users

### Pull Requests

1. **Fork the repository** and create your branch from `main`
2. **Follow coding standards**:
   - Use Java naming conventions
   - Add Javadoc comments for public methods
   - Follow existing code style
3. **Write or update tests** for your changes
4. **Update documentation** if needed
5. **Ensure all tests pass**
6. **Submit the pull request** with a clear description

## Development Process

### Setting Up Development Environment

1. Follow [SETUP.md](docs/SETUP.md) to configure your environment
2. Create a new branch for your feature:
   ```bash
   git checkout -b feature/your-feature-name
   ```

### Coding Standards

#### Java (Backend & Android)
- Use camelCase for variables and methods
- Use PascalCase for classes
- Maximum line length: 120 characters
- Use meaningful variable names
- Add comments for complex logic

#### Example:
```java
/**
 * Calculate compound interest for an investment
 * @param principal Initial investment amount
 * @param rate Annual interest rate (percentage)
 * @param months Investment duration in months
 * @return Final amount after interest
 */
public double calculateCompoundInterest(double principal, double rate, int months) {
    // Implementation
}
```

### Commit Messages

Follow conventional commits format:
- `feat:` New feature
- `fix:` Bug fix
- `docs:` Documentation changes
- `style:` Code style changes (formatting)
- `refactor:` Code refactoring
- `test:` Adding or updating tests
- `chore:` Maintenance tasks

Example: `feat: add mutual fund investment calculator`

### Testing

#### Backend Tests
```bash
cd backend
mvn test
```

#### Android Tests
```bash
cd android-app
./gradlew test
./gradlew connectedAndroidTest
```

Ensure all tests pass before submitting PR.

## Project Structure Guidelines

### Backend
- **Controllers**: Handle HTTP requests, minimal logic
- **Services**: Business logic and calculations
- **Repositories**: Database access only
- **Models**: Entity definitions with JPA annotations
- **DTOs**: Data transfer objects for API responses

### Android
- **Activities**: UI controllers
- **Fragments**: Reusable UI components
- **ViewModels**: UI state management
- **Repositories**: Data source abstraction
- **Models**: Data classes

## Special Considerations

### Localization
When adding new strings:
1. Add to `values/strings.xml` (English)
2. Add translations to language-specific folders
3. Use placeholders for dynamic content
4. Keep text simple for low-literacy users

### Accessibility
- Use large, clear fonts
- Provide audio alternatives
- Use icons with text labels
- Test with screen readers
- Support gesture navigation

### Performance
- Optimize for low-end Android devices (2GB RAM)
- Minimize network calls
- Implement caching
- Use background threads for heavy operations
- Optimize images and resources

### Offline Support
- Critical features must work offline
- Implement sync mechanisms
- Handle network errors gracefully
- Cache essential data locally

## Financial Content Guidelines

When adding investment-related content:

1. **Accuracy**: Verify information with reliable sources
2. **Simplicity**: Use farmer-friendly language
3. **Examples**: Provide real-world scenarios
4. **Visual**: Include charts and illustrations
5. **Regional**: Consider state-specific schemes

### Approved Sources
- Reserve Bank of India (RBI)
- Securities and Exchange Board of India (SEBI)
- National Bank for Agriculture and Rural Development (NABARD)
- Ministry of Agriculture & Farmers Welfare
- Post Office savings schemes

## Review Process

1. **Automated checks**: Linting, tests, build
2. **Code review**: At least one maintainer approval
3. **Testing**: Manual testing for UI changes
4. **Documentation**: Verify docs are updated
5. **Merge**: Squash and merge to main

## Getting Help

- **Technical questions**: Create a Discussion
- **Bug reports**: Create an Issue
- **Feature ideas**: Create an Issue with "enhancement" label
- **Documentation**: Check [/docs](docs/) folder

## Recognition

Contributors will be:
- Listed in CONTRIBUTORS.md
- Mentioned in release notes
- Credited in the app (for significant contributions)

## License

By contributing, you agree that your contributions will be licensed under the MIT License.

---

Thank you for helping make financial literacy accessible to farmers! 🌾
