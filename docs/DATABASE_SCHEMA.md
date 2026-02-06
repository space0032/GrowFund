# Database Schema

## Overview
GrowFund uses PostgreSQL as the primary relational database. The schema is designed to support the gamified financial literacy features while maintaining data integrity and performance.

## Entity Relationship Diagram

```
┌─────────────┐
│    users    │
├─────────────┤
│ id (PK)     │
│ username    │◄────┐
│ password    │     │
│ full_name   │     │
│ phone       │     │
│ email       │     │
│ language    │     │
│ state       │     │
│ level       │     │
│ total_coins │     │
│ experience  │     │
└─────────────┘     │
      │             │
      │             │
      ├─────────────┼─────────────┬─────────────┐
      │             │             │             │
      ▼             │             │             │
┌─────────────┐     │             │             │
│    farms    │     │             │             │
├─────────────┤     │             │             │
│ id (PK)     │     │             │             │
│ user_id(FK) │─────┘             │             │
│ farm_name   │                   │             │
│ land_size   │                   │             │
│ savings     │                   │             │
│ emerg_fund  │                   │             │
└─────────────┘                   │             │
      │                           │             │
      │                           │             │
      ├───────────┬───────────────┘             │
      │           │                             │
      ▼           ▼                             │
┌─────────────┐ ┌─────────────┐                │
│    crops    │ │  equipment  │                │
├─────────────┤ ├─────────────┤                │
│ id (PK)     │ │ id (PK)     │                │
│ farm_id(FK) │ │ farm_id(FK) │                │
│ crop_type   │ │ equip_type  │                │
│ area        │ │ name        │                │
│ investment  │ │ price       │                │
│ yield       │ │ value       │                │
│ season      │ │ durability  │                │
│ status      │ │ unlocked    │                │
└─────────────┘ └─────────────┘                │
                                                │
                                                │
      ┌─────────────────────────────────────────┤
      │                                         │
      ▼                                         ▼
┌──────────────┐                        ┌──────────────┐
│ investments  │                        │ achievements │
├──────────────┤                        ├──────────────┤
│ id (PK)      │                        │ id (PK)      │
│ user_id (FK) │                        │ user_id (FK) │
│ inv_type     │                        │ achv_type    │
│ scheme_name  │                        │ title        │
│ principal    │                        │ description  │
│ interest_rate│                        │ reward_coins │
│ duration     │                        │ reward_exp   │
│ current_value│                        │ unlocked_at  │
│ status       │                        └──────────────┘
└──────────────┘
```

## Table Definitions

### users
Stores user profile and authentication information.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique user identifier |
| username | VARCHAR(50) | UNIQUE, NOT NULL | Login username |
| password | VARCHAR(255) | NOT NULL | Encrypted password |
| full_name | VARCHAR(100) | NOT NULL | User's full name |
| phone_number | VARCHAR(15) | UNIQUE | Mobile number |
| email | VARCHAR(100) | UNIQUE | Email address |
| preferred_language | VARCHAR(5) | NOT NULL, DEFAULT 'en' | UI language (en, hi, ta, etc.) |
| state | VARCHAR(50) | NOT NULL | Indian state |
| district | VARCHAR(50) | | District name |
| current_level | INTEGER | NOT NULL, DEFAULT 1 | Game level |
| total_coins | BIGINT | NOT NULL, DEFAULT 10000 | Virtual currency |
| experience | BIGINT | NOT NULL, DEFAULT 0 | XP points |
| created_at | TIMESTAMP | NOT NULL | Account creation |
| last_login_at | TIMESTAMP | | Last login time |

**Indexes:**
- `idx_users_username` ON username
- `idx_users_phone` ON phone_number

### farms
Represents a user's virtual farm.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique farm identifier |
| user_id | BIGINT | FOREIGN KEY, NOT NULL, UNIQUE | Owner user ID |
| farm_name | VARCHAR(100) | NOT NULL | Farm name |
| land_size | DECIMAL(10,2) | NOT NULL, DEFAULT 1.0 | Land in acres |
| savings | BIGINT | NOT NULL, DEFAULT 0 | Saved coins |
| emergency_fund | BIGINT | NOT NULL, DEFAULT 0 | Emergency reserve |

**Foreign Keys:**
- `user_id` REFERENCES users(id) ON DELETE CASCADE

### crops
Tracks crops planted on farms.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique crop identifier |
| farm_id | BIGINT | FOREIGN KEY, NOT NULL | Farm ID |
| crop_type | VARCHAR(50) | NOT NULL | Wheat, Rice, Cotton, etc. |
| area_planted | DECIMAL(10,2) | NOT NULL | Area in acres |
| investment_amount | BIGINT | NOT NULL | Money invested |
| expected_yield | BIGINT | | Expected harvest value |
| actual_yield | BIGINT | | Actual harvest value |
| season | VARCHAR(20) | NOT NULL | KHARIF, RABI, ZAID |
| planted_date | TIMESTAMP | NOT NULL | Planting date |
| harvest_date | TIMESTAMP | | Harvest date |
| status | VARCHAR(20) | NOT NULL | PLANTED, GROWING, HARVESTED, FAILED |
| weather_impact | VARCHAR(50) | | NORMAL, DROUGHT, FLOOD, PEST |

**Foreign Keys:**
- `farm_id` REFERENCES farms(id) ON DELETE CASCADE

**Indexes:**
- `idx_crops_farm_status` ON (farm_id, status)

### investments
Tracks user investment activities.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique investment ID |
| user_id | BIGINT | FOREIGN KEY, NOT NULL | Investor user ID |
| investment_type | VARCHAR(50) | NOT NULL | Type of investment |
| scheme_name | VARCHAR(100) | NOT NULL | Scheme/product name |
| principal_amount | BIGINT | NOT NULL | Initial investment |
| interest_rate | DECIMAL(5,2) | NOT NULL | Annual interest rate % |
| duration_months | INTEGER | NOT NULL | Investment duration |
| current_value | BIGINT | | Current value |
| start_date | TIMESTAMP | NOT NULL | Investment start |
| maturity_date | TIMESTAMP | | Maturity date |
| status | VARCHAR(20) | NOT NULL | ACTIVE, MATURED, WITHDRAWN |
| completed_at | TIMESTAMP | | Completion date |

**Foreign Keys:**
- `user_id` REFERENCES users(id) ON DELETE CASCADE

**Indexes:**
- `idx_inv_user_status` ON (user_id, status)

### equipment
Farm equipment and tools.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique equipment ID |
| farm_id | BIGINT | FOREIGN KEY, NOT NULL | Farm ID |
| equipment_type | VARCHAR(50) | NOT NULL | TRACTOR, PUMP, SPRAYER |
| name | VARCHAR(100) | NOT NULL | Equipment name |
| purchase_price | BIGINT | NOT NULL | Purchase cost |
| current_value | BIGINT | | Depreciated value |
| durability | INTEGER | NOT NULL, DEFAULT 100 | Condition % |
| is_unlocked | BOOLEAN | NOT NULL, DEFAULT false | Unlocked status |

**Foreign Keys:**
- `farm_id` REFERENCES farms(id) ON DELETE CASCADE

### achievements
Gamification achievements and rewards.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique achievement ID |
| user_id | BIGINT | FOREIGN KEY, NOT NULL | User ID |
| achievement_type | VARCHAR(50) | NOT NULL | Achievement category |
| title | VARCHAR(100) | NOT NULL | Achievement title |
| description | TEXT | | Description |
| reward_coins | BIGINT | NOT NULL | Coin reward |
| reward_experience | BIGINT | NOT NULL | XP reward |
| unlocked_at | TIMESTAMP | NOT NULL | Unlock timestamp |

**Foreign Keys:**
- `user_id` REFERENCES users(id) ON DELETE CASCADE

**Indexes:**
- `idx_achv_user_type` ON (user_id, achievement_type)

## Investment Types

Supported investment schemes:
- `MUTUAL_FUND` - Agricultural mutual funds
- `FIXED_DEPOSIT` - Bank fixed deposits
- `KISAN_VIKAS_PATRA` - Post office savings
- `PM_KISAN` - PM-Kisan Maan Dhan Yojana
- `NABARD_SCHEME` - NABARD subsidies
- `INSURANCE` - Crop/life insurance

## Crop Types

Common Indian crops:
- `WHEAT` - Rabi season
- `RICE` - Kharif season
- `COTTON` - Kharif season
- `SUGARCANE` - Year-round
- `MAIZE` - Kharif/Rabi
- `PULSES` - Various seasons
- `VEGETABLES` - Various seasons

## Seasons

Agricultural seasons in India:
- `KHARIF` - Monsoon season (Jun-Oct)
- `RABI` - Winter season (Oct-Mar)
- `ZAID` - Summer season (Mar-Jun)

## Data Constraints

### Business Rules
1. Users start with 10,000 coins and level 1
2. Minimum farm size is 1 acre
3. Crop investment cannot exceed available coins
4. Equipment durability decreases with use (0-100%)
5. Investments have minimum duration of 1 month
6. Interest rates are positive decimals (0.1% - 50%)

### Data Integrity
- All foreign keys use CASCADE on delete
- Username, phone, and email are unique
- Timestamps use UTC timezone
- Monetary values stored as BIGINT (in paise/smallest unit)
- Percentages stored as DECIMAL with 2 decimal places

## Migration Notes

For production deployment:
1. Use `ddl-auto: validate` instead of `update`
2. Create database indexes as shown
3. Set up backup strategy
4. Enable query logging for optimization
5. Configure connection pooling (HikariCP)
6. Consider partitioning for large tables (investments, crops)

## Sample Queries

### Get user's total investment value
```sql
SELECT SUM(current_value) as total_investments
FROM investments
WHERE user_id = ? AND status = 'ACTIVE';
```

### Get active crops by season
```sql
SELECT c.*, f.farm_name
FROM crops c
JOIN farms f ON c.farm_id = f.id
WHERE c.status IN ('PLANTED', 'GROWING')
  AND c.season = 'KHARIF';
```

### User leaderboard by level and coins
```sql
SELECT username, current_level, total_coins, experience
FROM users
ORDER BY current_level DESC, total_coins DESC
LIMIT 100;
```
