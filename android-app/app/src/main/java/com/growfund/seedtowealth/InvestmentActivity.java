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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_investment);

        investmentsRecyclerView = findViewById(R.id.investmentsRecyclerView);
        loadingProgress = findViewById(R.id.loadingProgress);
        addInvestmentFab = findViewById(R.id.addInvestmentFab);

        investmentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new InvestmentAdapter();
        investmentsRecyclerView.setAdapter(adapter);

        addInvestmentFab.setOnClickListener(v -> showCreateInvestmentDialog());

        loadInvestments();
    }

    private void loadInvestments() {
        loadingProgress.setVisibility(View.VISIBLE);
        ApiClient.getApiService().getMyActiveInvestments().enqueue(new Callback<List<Investment>>() {
            @Override
            public void onResponse(Call<List<Investment>> call, Response<List<Investment>> response) {
                loadingProgress.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    adapter.setInvestments(response.body());
                } else {
                    ErrorHandler.handleApiError(InvestmentActivity.this, response);
                }
            }

            @Override
            public void onFailure(Call<List<Investment>> call, Throwable t) {
                loadingProgress.setVisibility(View.GONE);
                ErrorHandler.handleError(InvestmentActivity.this, t);
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
                double amount = Double.parseDouble(amountStr);
                createInvestment(name, amount);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid amount", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void createInvestment(String name, double amount) {
        loadingProgress.setVisibility(View.VISIBLE);

        // Demo: Fixed Deposit logic
        Investment investment = new Investment(
                name,
                "FIXED_DEPOSIT",
                amount,
                6.5, // 6.5% Interest
                12 // 12 Months
        );

        ApiClient.getApiService().createInvestment(investment).enqueue(new Callback<Investment>() {
            @Override
            public void onResponse(Call<Investment> call, Response<Investment> response) {
                loadingProgress.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    Toast.makeText(InvestmentActivity.this, "Investment Created!", Toast.LENGTH_SHORT).show();
                    loadInvestments();
                } else {
                    ErrorHandler.handleApiError(InvestmentActivity.this, response);
                }
            }

            @Override
            public void onFailure(Call<Investment> call, Throwable t) {
                loadingProgress.setVisibility(View.GONE);
                ErrorHandler.handleError(InvestmentActivity.this, t);
            }
        });
    }
}
