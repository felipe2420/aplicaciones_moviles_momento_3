package com.example.aplicaciones_moviles_momento_3;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aplicaciones_moviles_momento_3.db.StoreDbHelper;
import com.example.aplicaciones_moviles_momento_3.db.tables.ProductsTable;
import com.example.aplicaciones_moviles_momento_3.models.Product;
import com.example.aplicaciones_moviles_momento_3.utils.ValidationException;

import java.util.Optional;

public class MainActivity extends AppCompatActivity {
    StoreDbHelper dbHelper;
    ProductsTable productsTable;
    EditText txtReference, txtDescription, txtCost, txtStock;
    TextView txtIVA;
    Button btnCreate, btnSearch, btnUpdate, btnDelete, btnClean;

    private Product lastSearchedProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new StoreDbHelper(getApplicationContext());
        productsTable = new ProductsTable(dbHelper);

        setViewComponents();
        setListeners();
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }

    private void setViewComponents() {
        txtReference = findViewById(R.id.txtReference);
        txtDescription = findViewById(R.id.txtDescription);
        txtCost = findViewById(R.id.txtCost);
        txtStock = findViewById(R.id.txtStock);

        txtIVA = findViewById(R.id.txtIVA);

        btnCreate = findViewById(R.id.btnSave);
        btnSearch = findViewById(R.id.btnSearch);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);
        btnClean = findViewById(R.id.btnClean);
    }

    private void setListeners() {
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                tryCreateProduct();
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                tryGetProduct();
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                tryUpdateProduct();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                tryDeleteProduct();
            }
        });

        btnClean.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                clean();
            }
        });
    }

    private  void tryCreateProduct() {
        try {
            int reference = tryToGetReference();
            String description = tryToGetDescription();
            Double cost = tryToGetCost();
            int stock = tryToGetStock(true);

            validateNotExists(reference);

            Product product = new Product(reference, description, cost, stock);
            boolean result = productsTable.create(product);

            String message = result ? "Producto creado con exito" : "No se pudo crear el producto";
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

            if(result) { clean(); }

        } catch (ValidationException exception) {
            Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
        } catch (Exception exception) {
            Toast.makeText(this, "Ocurrió un error inesperado, asegurese de ingresar todos los campos correctamente", Toast.LENGTH_SHORT).show();
        }
    }

    private void tryGetProduct() {
        try {
            int reference = tryToGetReference();
            Optional<Product> product = productsTable.get(reference);

            if(product.isPresent()) {
                lastSearchedProduct = product.get();
                setFields(product.get());
                return;
            }

            Toast.makeText(this, "No se encontró ningún producto con la referencia seleccionada", Toast.LENGTH_SHORT).show();
        } catch (ValidationException exception) {
            Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
        } catch (Exception exception) {
            Toast.makeText(this, "Ocurrió un error inesperado", Toast.LENGTH_SHORT).show();
        }
    }

    private void tryUpdateProduct() {
        try {
            int reference = tryToGetReference();
            String description = tryToGetDescription();
            Double cost = tryToGetCost();
            int stock = tryToGetStock(false);

            validateIfCanBeUpdated(reference);

            Product product = new Product(reference, description, cost, stock);
            boolean result = productsTable.update(product, lastSearchedProduct.getId());

            String message = result ? "Producto actualizado con exito" : "No se pudo actualizar el producto";
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

            if(result) { clean(); }

        } catch (ValidationException exception) {
            Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
        } catch (Exception exception) {
            Toast.makeText(this, "Ocurrió un error inesperado al actualizar el producto", Toast.LENGTH_SHORT).show();
        }
    }

    private void tryDeleteProduct() {
        try {
            int reference = tryToGetReference();

            validateIfCanBeDeleted(reference);
            boolean result = productsTable.delete(reference);

            String message = result ? "Producto eliminado con exito" : "No se pudo eliminar el producto";
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

            if(result) { clean(); }

        } catch (ValidationException exception) {
            Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
        } catch (Exception exception) {
            Toast.makeText(this, "Ocurrió un error inesperado al eliminar el producto", Toast.LENGTH_SHORT).show();
        }
    }

    // VALIDATIONS
    private int tryToGetReference() throws ValidationException {
        String referenceString = txtReference.getText().toString();

        if (referenceString.isEmpty()) {
            throw new ValidationException("La referencia no puede estar vacía");
        }

        try {
            return Integer.parseInt(referenceString);
        } catch (Exception e) {
            throw new ValidationException("La referencia solo admite números");
        }
    }

    private String tryToGetDescription() throws ValidationException {
        String description = txtDescription.getText().toString();

        if (description.isEmpty()) {
            throw new ValidationException("La descripción no puede estar vacía");
        }

        return description;
    }

    private Double tryToGetCost() throws ValidationException {
        Double cost = getCost();

        if(cost <= 20_000) {
            throw new ValidationException("El costo del producto debe ser superior a $20.000");
        }

        return cost;
    }

    private Double getCost() throws ValidationException {
        String costString = txtCost.getText().toString();

        if (costString.isEmpty()) {
            throw new ValidationException("El costo no puede estar vacío");
        }

        try {
            return Double.parseDouble(costString);
        } catch (Exception e) {
            throw new ValidationException("El costo solo admite números");
        }
    }

    private int tryToGetStock(boolean hasToValidateUnitLimit) throws ValidationException {
        int stock = getStock();

        if (!hasToValidateUnitLimit) {
            return stock;
        }

        if(stock >= 5 && stock <= 20) {
            return stock;
        }

        throw new ValidationException("La existencia debe estar entre 5 y 20 unidades");
    }

    private int getStock() throws ValidationException {
        String stockString = txtStock.getText().toString();

        if (stockString.isEmpty()) {
            throw new ValidationException("La existencia del producto no puede estar vacía");
        }

        try {
            return Integer.parseInt(stockString);
        } catch (Exception e) {
            throw new ValidationException("La existencia solo admite números");
        }
    }

    private void validateNotExists(int reference) throws ValidationException {
        Optional<Product> result = productsTable.get(reference);

        if (result.isPresent()) {
            throw new ValidationException("Ya existe un producto con la misma referencia");
        }
    }

    private void validateIfCanBeUpdated(int reference) throws ValidationException {
        if(lastSearchedProduct == null) {
            throw new ValidationException("Primero debes buscar un producto para poder actualizarlo");
        }

        if (reference != lastSearchedProduct.getReference()) {
            validateNotExists(reference);
        }
    }

    private void validateIfCanBeDeleted(int reference) throws ValidationException {
        Optional<Product> result = productsTable.get(reference);

        if(!result.isPresent()) {
            throw new ValidationException("No existe ningún producto con la referencia seleccionada");
        }

        int stock = result.get().getStock();
        if(stock != 0) {
            throw new ValidationException("No se puede eliminar el producto. Existencia de " + stock + " unidades");
        }
    }

    //UTILS

    private void setFields(Product product) {
        String reference = Integer.toString(product.getReference());
        txtReference.setText(reference);

        txtDescription.setText(product.getDescription());

        String cost = Double.toString(product.getCost());
        txtCost.setText(cost);

        String stock = Integer.toString(product.getStock());
        txtStock.setText(stock);

        txtIVA.setVisibility(View.VISIBLE);
        String iva = "IVA: $" + Double.toString(product.getIVA());
        txtIVA.setText(iva);
    }

    private void clean() {
        txtReference.getText().clear();
        txtDescription.getText().clear();
        txtCost.getText().clear();
        txtStock.getText().clear();

        txtIVA.setText("");
        txtIVA.setVisibility(View.GONE);

        lastSearchedProduct = null;
    }
}