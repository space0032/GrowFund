# GrowFund - Investment Schemes Reference

## Government Schemes for Farmers

### 1. PM-Kisan Maan Dhan Yojana
**Type**: Pension Scheme  
**Target**: Small and Marginal Farmers  
**Features**:
- Monthly pension of ₹3,000 after 60 years
- Entry age: 18-40 years
- Contribution: ₹55 to ₹200 per month (age-dependent)
- Voluntary and contributory scheme

**In-Game Implementation**:
- Long-term investment option
- Shows monthly contribution and future returns
- Educational content about retirement planning

### 2. Kisan Vikas Patra (KVP)
**Type**: Savings Certificate  
**Issuer**: Post Office  
**Features**:
- Money doubles in 115 months (approx. 9.5 years)
- Current interest rate: ~7.5% per annum (compounded annually)
- Minimum investment: ₹1,000
- No maximum limit
- Can be transferred

**In-Game Implementation**:
- Medium to long-term investment
- Shows money doubling visualization
- Safe investment option for risk-averse players

### 3. Pradhan Mantri Fasal Bima Yojana (PMFBY)
**Type**: Crop Insurance  
**Coverage**:
- Pre-sowing to post-harvest losses
- Natural calamities, pests, diseases
- Premium: 1.5-5% of sum insured

**In-Game Implementation**:
- Risk management tool
- Protects against random negative events
- Shows premium vs. coverage comparison

### 4. Kisan Credit Card (KCC)
**Type**: Agricultural Credit  
**Features**:
- Credit limit based on land holding
- Interest rate: 7% per annum
- 3% interest subvention (effective 4%)
- Additional 3% for prompt repayment (effective 1%)
- Withdrawal and repayment flexibility

**In-Game Implementation**:
- Emergency credit option
- Teaches responsible borrowing
- Shows interest calculation

### 5. NABARD Schemes
**Type**: Development and Subsidy Programs  
**Coverage**:
- Farm mechanization
- Irrigation development
- Dairy farming
- Horticulture

**In-Game Implementation**:
- Equipment upgrade subsidies
- Infrastructure development
- Diversification opportunities

## Banking Products

### Fixed Deposits (FD)
**Features**:
- Tenure: 7 days to 10 years
- Interest: 5-7.5% per annum
- Safe and guaranteed returns
- Premature withdrawal with penalty

**Game Parameters**:
```
Min Investment: ₹1,000
Durations: 6, 12, 24, 36, 60 months
Interest Rates: 5.5%, 6.0%, 6.5%, 7.0%, 7.5%
Compounding: Quarterly
```

### Recurring Deposits (RD)
**Features**:
- Monthly fixed deposits
- Similar interest as FD
- Builds savings discipline

**Game Parameters**:
```
Min Monthly: ₹100
Durations: 12, 24, 36, 60 months
Interest: Similar to FD
Compounding: Quarterly
```

### Savings Account
**Features**:
- Liquidity
- Interest: 2.7-4% per annum
- Minimum balance requirements

**Game Parameters**:
```
Opening Balance: ₹500
Interest Rate: 3.5% per annum
Min Balance: ₹500
```

## Investment Products

### Agricultural Mutual Funds
**Examples**:
- HDFC Agri Saver Fund
- Quantum India ESG Equity Fund

**Features**:
- Market-linked returns
- Professional management
- Higher risk, higher potential returns
- SIP (Systematic Investment Plan) options

**Game Parameters**:
```
Min Investment: ₹500
Expected Returns: 8-15% per annum (variable)
Lock-in: None (recommended 3+ years)
Risk Level: Medium to High
```

### Gold
**Features**:
- Traditional investment
- Hedge against inflation
- Liquid asset

**Game Parameters**:
```
Unit: Grams
Price: Variable (market-linked)
Expected Returns: 8-10% per annum
Storage: Digital gold (no storage cost)
```

## Game-Specific Investment Options

### Land Expansion
**Type**: Infrastructure Investment  
**Returns**: Increased production capacity  
**Cost**: Variable based on level
```
1 acre = ₹50,000 (game currency)
Returns: 20% more crop area
Payback: 5-6 seasons
```

### Equipment Upgrades
**Type**: Productivity Investment  
**Options**:
- Tractor: Reduces farming time, increases efficiency
- Irrigation: Protects against drought
- Storage: Reduces post-harvest losses

**Example**:
```
Tractor:
  Cost: ₹200,000
  Benefit: 30% faster farming
  Durability: 100 cycles
  Maintenance: ₹1,000 per season
```

### Soil Improvement
**Type**: Long-term Value Addition  
**Returns**: Better yields
```
Cost: ₹10,000 per acre
Benefit: 15% higher yields
Duration: 10 seasons
```

## Comparison Matrix

| Investment | Risk | Returns | Liquidity | Min Amount | Lock-in |
|------------|------|---------|-----------|------------|---------|
| Savings Account | Very Low | 3.5% | High | ₹500 | None |
| Fixed Deposit | Very Low | 5.5-7.5% | Low | ₹1,000 | Varies |
| KVP | Very Low | 7.5% | Medium | ₹1,000 | 115 months |
| Mutual Funds | Medium-High | 8-15% | Medium | ₹500 | None* |
| PM-Kisan Pension | Low | Fixed ₹3k/mo | Very Low | Age-based | Till 60 |
| Land Expansion | Low | 20%+ | Very Low | ₹50,000 | Long-term |
| Equipment | Low | Efficiency | Medium | ₹50,000+ | Medium |

*Recommended minimum 3 years

## Educational Messages

### Risk & Return
> "Higher returns come with higher risk. Diversify your investments!"

### Emergency Fund
> "Always keep 6 months of expenses in liquid savings before investing."

### Long-term Thinking
> "Good investments grow like trees - they need time and patience."

### Diversification
> "Don't put all your eggs in one basket. Spread your investments."

### Compound Interest
> "Start early! Even small savings grow significantly over time."

## Calculation Formulas

### Simple Interest
```
Amount = Principal × (1 + Rate × Time)
```

### Compound Interest
```
Amount = Principal × (1 + Rate/n)^(n×Time)
where n = compounding frequency
```

### SIP Returns
```
FV = P × [(1 + r)^n - 1] / r × (1 + r)
where P = monthly investment, r = monthly rate, n = months
```

## Regional Variations

Different states may have specific schemes:
- **Maharashtra**: Additional subsidy on drip irrigation
- **Punjab**: Wheat procurement bonuses
- **Tamil Nadu**: Special horticulture schemes
- **Kerala**: Coconut development programs

**Note**: Game should allow region-specific content customization.

## Sources & References

- Reserve Bank of India (rbi.org.in)
- India Post (indiapost.gov.in)
- NABARD (nabard.org)
- Ministry of Agriculture (agriculture.gov.in)
- PMFBY Portal (pmfby.gov.in)

---

*This is a reference document for game design. Actual interest rates and scheme details should be updated regularly to reflect current government policies.*
