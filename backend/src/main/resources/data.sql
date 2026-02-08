-- Equipment Catalog Seed Data

-- IRRIGATION Equipment
INSERT INTO equipment (name, description, equipment_type, tier, cost, yield_bonus, cost_reduction, max_durability, icon, created_at) VALUES
('Basic Drip System', 'Simple drip irrigation for small plots', 'IRRIGATION', 'BASIC', 5000, 0.10, 0.05, 50, '💧', NOW()),
('Advanced Sprinkler', 'Automated sprinkler system with timer', 'IRRIGATION', 'ADVANCED', 15000, 0.20, 0.10, 100, '🚿', NOW()),
('Smart Irrigation', 'IoT-enabled precision irrigation system', 'IRRIGATION', 'PREMIUM', 30000, 0.30, 0.15, 200, '💦', NOW());

-- FERTILIZER Equipment
INSERT INTO equipment (name, description, equipment_type, tier, cost, yield_bonus, cost_reduction, max_durability, icon, created_at) VALUES
('Organic Compost', 'Natural organic fertilizer mix', 'FERTILIZER', 'BASIC', 3000, 0.12, 0.00, 40, '🌱', NOW()),
('NPK Fertilizer', 'Balanced NPK chemical fertilizer', 'FERTILIZER', 'ADVANCED', 10000, 0.22, 0.08, 80, '🧪', NOW()),
('Bio-Enhancer Pro', 'Advanced bio-fertilizer with micronutrients', 'FERTILIZER', 'PREMIUM', 25000, 0.35, 0.12, 150, '🔬', NOW());

-- TOOLS Equipment
INSERT INTO equipment (name, description, equipment_type, tier, cost, yield_bonus, cost_reduction, max_durability, icon, created_at) VALUES
('Hand Tools Set', 'Basic farming hand tools', 'TOOLS', 'BASIC', 2000, 0.05, 0.10, 100, '🔨', NOW()),
('Power Tiller', 'Motorized tilling equipment', 'TOOLS', 'ADVANCED', 12000, 0.15, 0.20, 120, '⚙️', NOW()),
('Smart Farm Kit', 'Complete automated farming toolkit', 'TOOLS', 'PREMIUM', 35000, 0.25, 0.25, 250, '🛠️', NOW());

-- SEEDS Equipment
INSERT INTO equipment (name, description, equipment_type, tier, cost, yield_bonus, cost_reduction, max_durability, icon, created_at) VALUES
('Hybrid Seeds', 'High-yield hybrid seed varieties', 'SEEDS', 'BASIC', 4000, 0.15, 0.00, 30, '🌾', NOW()),
('GMO Seeds Plus', 'Genetically modified disease-resistant seeds', 'SEEDS', 'ADVANCED', 18000, 0.28, 0.05, 60, '🧬', NOW()),
('Premium Genetics', 'Premium seed genetics with maximum yield', 'SEEDS', 'PREMIUM', 40000, 0.40, 0.10, 100, '⭐', NOW());
