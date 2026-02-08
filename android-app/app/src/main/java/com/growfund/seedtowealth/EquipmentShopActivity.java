package com.growfund.seedtowealth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.growfund.seedtowealth.adapter.EquipmentAdapter;
import com.growfund.seedtowealth.model.Equipment;
import com.growfund.seedtowealth.repository.EquipmentRepository;
import com.growfund.seedtowealth.repository.FarmRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EquipmentShopActivity extends AppCompatActivity implements EquipmentAdapter.OnEquipmentClickListener {

    private RecyclerView recyclerViewEquipment;
    private EquipmentAdapter adapter;
    private TabLayout tabLayout;
    private TextView textViewBalance;
    private ProgressBar progressBar;
    private View emptyState;

    private EquipmentRepository equipmentRepository;
    private FarmRepository farmRepository;

    private List<Equipment> allEquipment = new ArrayList<>();
    private List<Long> ownedEquipmentIds = new ArrayList<>();
    private Long currentFarmId;
    private Long currentBalance = 0L;

    private static final String[] EQUIPMENT_TYPES = { "ALL", "IRRIGATION", "FERTILIZER", "TOOLS", "SEEDS" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipment_shop);

        // Initialize repositories
        equipmentRepository = new EquipmentRepository(getApplication());
        farmRepository = new FarmRepository(getApplication());

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Equipment Shop");
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white, null));
        toolbar.setNavigationOnClickListener(v -> finish());

        // Initialize views
        recyclerViewEquipment = findViewById(R.id.recyclerViewEquipment);
        tabLayout = findViewById(R.id.tabLayout);
        textViewBalance = findViewById(R.id.textViewBalance);
        progressBar = findViewById(R.id.progressBar);
        emptyState = findViewById(R.id.emptyState);

        // Setup RecyclerView
        adapter = new EquipmentAdapter(this);
        recyclerViewEquipment.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewEquipment.setAdapter(adapter);

        // Setup tabs
        setupTabs();

        // Load data
        loadFarmData();
        loadEquipment();

        // Fetch equipment from API
        fetchEquipmentFromAPI();
    }

    private void fetchEquipmentFromAPI() {
        equipmentRepository.fetchEquipmentCatalog(new EquipmentRepository.RepositoryCallback<List<Equipment>>() {
            @Override
            public void onSuccess(List<Equipment> result) {
                // Equipment cached in database, will be loaded by LiveData observer
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> Toast.makeText(EquipmentShopActivity.this,
                        "Failed to load equipment: " + error,
                        Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void setupTabs() {
        for (String type : EQUIPMENT_TYPES) {
            tabLayout.addTab(tabLayout.newTab().setText(type));
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                filterEquipment(EQUIPMENT_TYPES[tab.getPosition()]);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void loadFarmData() {
        farmRepository.getUserFarm().observe(this, farm -> {
            if (farm != null) {
                currentFarmId = farm.getId();
                currentBalance = farm.getSavings();
                textViewBalance.setText("₹" + String.format("%,d", currentBalance));

                // Load owned equipment for this farm
                loadOwnedEquipment();
            }
        });
    }

    private void loadEquipment() {
        showLoading(true);
        equipmentRepository.getAllEquipment().observe(this, equipmentList -> {
            showLoading(false);
            if (equipmentList != null && !equipmentList.isEmpty()) {
                allEquipment = equipmentList;
                filterEquipment("ALL");
                emptyState.setVisibility(View.GONE);
            } else {
                // Show test data if no equipment in database
                allEquipment = createTestEquipment();
                if (!allEquipment.isEmpty()) {
                    filterEquipment("ALL");
                    emptyState.setVisibility(View.GONE);
                } else {
                    adapter.setEquipmentList(new ArrayList<>());
                    emptyState.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private List<Equipment> createTestEquipment() {
        List<Equipment> testEquipment = new ArrayList<>();

        // Irrigation
        Equipment e1 = new Equipment();
        e1.setId(1L);
        e1.setName("Basic Drip System");
        e1.setDescription("Simple drip irrigation for small plots");
        e1.setEquipmentType("IRRIGATION");
        e1.setTier("BASIC");
        e1.setCost(5000L);
        e1.setYieldBonus(0.10);
        e1.setCostReduction(0.05);
        e1.setMaxDurability(50);
        e1.setIcon("💧");
        testEquipment.add(e1);

        Equipment e2 = new Equipment();
        e2.setId(2L);
        e2.setName("Smart Irrigation");
        e2.setDescription("IoT-enabled precision irrigation system");
        e2.setEquipmentType("IRRIGATION");
        e2.setTier("PREMIUM");
        e2.setCost(30000L);
        e2.setYieldBonus(0.30);
        e2.setCostReduction(0.15);
        e2.setMaxDurability(200);
        e2.setIcon("💦");
        testEquipment.add(e2);

        // Fertilizer
        Equipment e3 = new Equipment();
        e3.setId(3L);
        e3.setName("Organic Compost");
        e3.setDescription("Natural organic fertilizer mix");
        e3.setEquipmentType("FERTILIZER");
        e3.setTier("BASIC");
        e3.setCost(3000L);
        e3.setYieldBonus(0.12);
        e3.setCostReduction(0.0);
        e3.setMaxDurability(40);
        e3.setIcon("🌱");
        testEquipment.add(e3);

        Equipment e4 = new Equipment();
        e4.setId(4L);
        e4.setName("Bio-Enhancer Pro");
        e4.setDescription("Advanced bio-fertilizer with micronutrients");
        e4.setEquipmentType("FERTILIZER");
        e4.setTier("PREMIUM");
        e4.setCost(25000L);
        e4.setYieldBonus(0.35);
        e4.setCostReduction(0.12);
        e4.setMaxDurability(150);
        e4.setIcon("🔬");
        testEquipment.add(e4);

        // Tools
        Equipment e5 = new Equipment();
        e5.setId(5L);
        e5.setName("Hand Tools Set");
        e5.setDescription("Basic farming hand tools");
        e5.setEquipmentType("TOOLS");
        e5.setTier("BASIC");
        e5.setCost(2000L);
        e5.setYieldBonus(0.05);
        e5.setCostReduction(0.10);
        e5.setMaxDurability(100);
        e5.setIcon("🔨");
        testEquipment.add(e5);

        Equipment e6 = new Equipment();
        e6.setId(6L);
        e6.setName("Smart Farm Kit");
        e6.setDescription("Complete automated farming toolkit");
        e6.setEquipmentType("TOOLS");
        e6.setTier("PREMIUM");
        e6.setCost(35000L);
        e6.setYieldBonus(0.25);
        e6.setCostReduction(0.25);
        e6.setMaxDurability(250);
        e6.setIcon("🛠️");
        testEquipment.add(e6);

        return testEquipment;
    }

    private void loadOwnedEquipment() {
        if (currentFarmId != null) {
            equipmentRepository.getFarmEquipment(currentFarmId).observe(this, farmEquipmentList -> {
                if (farmEquipmentList != null) {
                    ownedEquipmentIds = farmEquipmentList.stream()
                            .map(fe -> fe.getEquipmentId())
                            .collect(Collectors.toList());
                    adapter.setOwnedEquipmentIds(ownedEquipmentIds);
                }
            });
        }
    }

    private void filterEquipment(String type) {
        List<Equipment> filtered;
        if ("ALL".equals(type)) {
            filtered = allEquipment;
        } else {
            filtered = allEquipment.stream()
                    .filter(e -> type.equalsIgnoreCase(e.getEquipmentType()))
                    .collect(Collectors.toList());
        }
        adapter.setEquipmentList(filtered);

        if (filtered.isEmpty()) {
            emptyState.setVisibility(View.VISIBLE);
        } else {
            emptyState.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPurchaseClick(Equipment equipment) {
        if (currentFarmId == null) {
            Toast.makeText(this, "Farm not found", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if user has enough funds
        if (currentBalance < equipment.getCost()) {
            Toast.makeText(this, "Insufficient funds! Need ₹" +
                    String.format("%,d", equipment.getCost() - currentBalance) + " more",
                    Toast.LENGTH_LONG).show();
            return;
        }

        // Purchase equipment
        showLoading(true);
        equipmentRepository.purchaseEquipment(currentFarmId, equipment.getId(),
                new EquipmentRepository.PurchaseCallback() {
                    @Override
                    public void onSuccess() {
                        runOnUiThread(() -> {
                            showLoading(false);
                            Toast.makeText(EquipmentShopActivity.this,
                                    "Successfully purchased " + equipment.getName() + "!",
                                    Toast.LENGTH_SHORT).show();

                            // Reload data
                            loadFarmData();
                            loadOwnedEquipment();
                        });
                    }

                    @Override
                    public void onError(String error) {
                        runOnUiThread(() -> {
                            showLoading(false);
                            Toast.makeText(EquipmentShopActivity.this,
                                    "Purchase failed: " + error,
                                    Toast.LENGTH_LONG).show();
                        });
                    }
                });
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerViewEquipment.setVisibility(show ? View.GONE : View.VISIBLE);
    }
}
