package com.growfund.seedtowealth;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.growfund.seedtowealth.adapter.InvestmentAdapter;
import com.growfund.seedtowealth.model.Investment;
import com.growfund.seedtowealth.network.ApiClient;
import com.growfund.seedtowealth.utils.ErrorHandler;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InvestmentActivity extends AppCompatActivity {

    private RecyclerView investmentsRecyclerView;
    private ProgressBar loadingProgress;
    private FloatingActionButton addInvestmentFab;
    private InvestmentAdapter adapter;
    private com.growfund.seedtowealth.repository.InvestmentRepository investmentRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_investment);

        investmentRepository = new com.growfund.seedtowealth.repository.InvestmentRepository(getApplication());

        investmentsRecyclerView = findViewById(R.id.investmentsRecyclerView);
        loadingProgress = findViewById(R.id.loadingProgress);
        addInvestmentFab = findViewById(R.id.addInvestmentFab);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        investmentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new InvestmentAdapter();
        investmentsRecyclerView.setAdapter(adapter);

        addInvestmentFab.setOnClickListener(v -> showCreateInvestmentDialog());

        loadInvestments();
    }

    private void loadInvestments() {
        loadingProgress.setVisibility(View.VISIBLE);
        investmentRepository.getActiveInvestments(
                new com.growfund.seedtowealth.repository.InvestmentRepository.RepositoryCallback<List<Investment>>() {
                    @Override
                    public void onLocalData(List<Investment> data) {
                        runOnUiThread(() -> {
                            adapter.setInvestments(data);
                            loadingProgress.setVisibility(View.GONE);
                        });
                    }

                    @Override
                    public void onSuccess(List<Investment> data) {
                        runOnUiThread(() -> {
                            adapter.setInvestments(data);
                            loadingProgress.setVisibility(View.GONE);
                        });
                    }

                    @Override
                    public void onError(String message) {
                        runOnUiThread(() -> {
                            loadingProgress.setVisibility(View.GONE);
                            Toast.makeText(InvestmentActivity.this, "Error: " + message, Toast.LENGTH_SHORT)
                                    .show();
                        });
                    }
                });
    }

    private void showCreateInvestmentDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("New Investment");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 16, 32, 16);

        final EditText nameInput = new EditText(this);
        nameInput.setHint("Investment Name (e.g. Gold Bond)");
        layout.addView(nameInput);

        final EditText amountInput = new EditText(this);
        amountInput.setHint("Amount (₹)");
        amountInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        layout.addView(amountInput);

        // For simplicity, hardcoding Type and Duration for demo
        // In full app, use Spinner

        builder.setView(layout);

        builder.setPositiveButton("Invest", (dialog, which) -> {
            String name = nameInput.getText().toString();
            String amountStr = amountInput.getText().toString();

            if (name.isEmpty() || amountStr.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                double amountDouble = Double.parseDouble(amountStr);
                long amount = Math.round(amountDouble);
                createInvestment(name, amount);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid amount", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void createInvestment(String name, long amount) {
        loadingProgress.setVisibility(View.VISIBLE);

        // Demo: Fixed Deposit logic
        Investment investment = new Investment(
                name,
                "FIXED_DEPOSIT",
                amount,
                6.5, // 6.5% Interest
                12 // 12 Months
        );

        investmentRepository.createInvestment(investment,
                new com.growfund.seedtowealth.repository.InvestmentRepository.RepositoryCallback<Investment>() {
                    @Override
                    public void onLocalData(Investment data) {
                        // Not used for create operation
                    }

                    @Override
                    public void onSuccess(Investment data) {
                        runOnUiThread(() -> {
                            loadingProgress.setVisibility(View.GONE);
                            Toast.makeText(InvestmentActivity.this, "Investment Created!", Toast.LENGTH_SHORT)
                                    .show();
                            loadInvestments();
                        });
                    }

                    @Override
                    public void onError(String message) {
                        runOnUiThread(() -> {
                            loadingProgress.setVisibility(View.GONE);
                            Toast.makeText(InvestmentActivity.this, "Error: " + message, Toast.LENGTH_SHORT)
                                    .show();
                        });
                    }
                });
    }
}
