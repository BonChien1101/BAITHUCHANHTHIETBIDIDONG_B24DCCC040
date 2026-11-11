package com.example.bt;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bt.db.DatabaseHelper;
import com.example.bt.model.Product;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class ProductDetailActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private Product currentProduct;

    private TextView tvName, tvCategory, tvPrice, tvQuantity, tvInventoryValue, tvLowStockWarning, tvDescription;
    private Button btnIncrease, btnDecrease, btnBack, btnDelete;
    private View btnBackHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        dbHelper = new DatabaseHelper(this);

        int productId = getIntent().getIntExtra(MainActivity.EXTRA_PRODUCT_ID, -1);
        if (productId == -1) {
            Toast.makeText(this, "Không tìm thấy sản phẩm", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        currentProduct = dbHelper.getProductById(productId);
        if (currentProduct == null) {
            Toast.makeText(this, "Sản phẩm không tồn tại", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        btnBackHeader = findViewById(R.id.btnBackHeader);
        tvName = findViewById(R.id.tvProductName);
        tvCategory = findViewById(R.id.tvProductCategory);
        tvPrice = findViewById(R.id.tvProductPrice);
        tvQuantity = findViewById(R.id.tvProductQuantity);
        tvInventoryValue = findViewById(R.id.tvInventoryValue);
        tvLowStockWarning = findViewById(R.id.tvLowStockWarning);
        tvDescription = findViewById(R.id.tvProductDescription);

        btnIncrease = findViewById(R.id.btnIncrease);
        btnDecrease = findViewById(R.id.btnDecrease);
        btnBack = findViewById(R.id.btnBack);
        btnDelete = findViewById(R.id.btnDelete);

        updateUI();

        btnBackHeader.setOnClickListener(v -> finish());
        btnBack.setOnClickListener(v -> finish());

        btnIncrease.setOnClickListener(v -> {
            int newQty = currentProduct.getQuantity() + 1;
            dbHelper.updateQuantity(currentProduct.getId(), newQty);
            currentProduct.setQuantity(newQty);
            updateUI();
            Toast.makeText(this, "Đã nhập thêm 1 sản phẩm", Toast.LENGTH_SHORT).show();
        });

        btnDecrease.setOnClickListener(v -> {
            if (currentProduct.getQuantity() <= 0) {
                Toast.makeText(this, "Hết hàng", Toast.LENGTH_SHORT).show();
                return;
            }
            int newQty = currentProduct.getQuantity() - 1;
            dbHelper.updateQuantity(currentProduct.getId(), newQty);
            currentProduct.setQuantity(newQty);
            updateUI();
            if (newQty == 0) {
                Toast.makeText(this, "Hết hàng", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Đã bán 1 sản phẩm", Toast.LENGTH_SHORT).show();
            }
        });

        btnDelete.setOnClickListener(v -> {
            dbHelper.deleteProduct(currentProduct.getId());
            Toast.makeText(this, "Đã xóa sản phẩm", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void updateUI() {
        if (currentProduct == null) return;

        tvName.setText(currentProduct.getName());
        tvCategory.setText(currentProduct.getCategory());

        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setGroupingSeparator('.');
        DecimalFormat df = new DecimalFormat("#,###", symbols);

        tvPrice.setText(df.format(currentProduct.getPrice()) + " đ");
        tvQuantity.setText(String.valueOf(currentProduct.getQuantity()));

        long inventoryValue = (long) currentProduct.getPrice() * currentProduct.getQuantity();
        tvInventoryValue.setText(df.format(inventoryValue) + " đ");

        if (currentProduct.getQuantity() < 5) {
            tvLowStockWarning.setVisibility(View.VISIBLE);
        } else {
            tvLowStockWarning.setVisibility(View.GONE);
        }

        if (TextUtils.isEmpty(currentProduct.getDescription())) {
            tvDescription.setText("Không có mô tả");
        } else {
            tvDescription.setText(currentProduct.getDescription());
        }
    }
}
