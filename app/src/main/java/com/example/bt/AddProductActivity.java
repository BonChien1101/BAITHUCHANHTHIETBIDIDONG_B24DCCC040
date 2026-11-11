package com.example.bt;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bt.db.DatabaseHelper;
import com.example.bt.model.Product;

public class AddProductActivity extends AppCompatActivity {

    private EditText etName, etPrice, etQuantity, etDescription;
    private Spinner spCategory;
    private Switch swActive;
    private Button btnSave;
    private View btnBackHeader;
    private DatabaseHelper dbHelper;

    private static final String[] CATEGORIES = {"Điện tử", "Thực phẩm", "Văn phòng phẩm", "Gia dụng", "Khác"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        dbHelper = new DatabaseHelper(this);

        btnBackHeader = findViewById(R.id.btnBackHeader);
        etName = findViewById(R.id.etProductName);
        etPrice = findViewById(R.id.etProductPrice);
        etQuantity = findViewById(R.id.etProductQuantity);
        spCategory = findViewById(R.id.spProductCategory);
        swActive = findViewById(R.id.swActive);
        etDescription = findViewById(R.id.etProductDescription);
        btnSave = findViewById(R.id.btnSaveProduct);

        // Setup Spinner
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                CATEGORIES
        );
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(categoryAdapter);

        btnBackHeader.setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> saveProduct());
    }

    private void saveProduct() {
        String name = etName.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String quantityStr = etQuantity.getText().toString().trim();
        String category = spCategory.getSelectedItem().toString();
        int active = swActive.isChecked() ? 1 : 0;
        String description = etDescription.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            etName.setError("Vui lòng nhập tên sản phẩm");
            etName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(priceStr)) {
            etPrice.setError("Vui lòng nhập giá bán");
            etPrice.requestFocus();
            return;
        }

        int price;
        try {
            price = Integer.parseInt(priceStr);
            if (price <= 0) {
                etPrice.setError("Giá bán phải lớn hơn 0");
                etPrice.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            etPrice.setError("Giá bán không hợp lệ");
            etPrice.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(quantityStr)) {
            etQuantity.setError("Vui lòng nhập số lượng");
            etQuantity.requestFocus();
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(quantityStr);
            if (quantity < 0) {
                etQuantity.setError("Số lượng không được âm");
                etQuantity.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            etQuantity.setError("Số lượng không hợp lệ");
            etQuantity.requestFocus();
            return;
        }

        Product product = new Product(name, price, quantity, category, active, description);
        long result = dbHelper.insertProduct(product);

        if (result > 0) {
            Toast.makeText(this, "Thêm sản phẩm thành công", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Thêm sản phẩm thất bại", Toast.LENGTH_SHORT).show();
        }
    }
}
