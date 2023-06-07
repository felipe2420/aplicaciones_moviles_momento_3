package com.example.aplicaciones_moviles_momento_3.db.tables;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.example.aplicaciones_moviles_momento_3.db.Query;
import com.example.aplicaciones_moviles_momento_3.models.Product;

import java.util.Optional;

public class ProductsTable implements BaseColumns {
    public static final String TABLE_NAME = "products";
    public static final String COLUMN_NAME_REFERENCE = "reference";
    public static final String COLUMN_NAME_DESCRIPTION = "description";
    public static final String COLUMN_NAME_COST = "cost";
    public static final String COLUMN_NAME_STOCK = "stock";

    private final SQLiteOpenHelper dbHelper;

    public ProductsTable(SQLiteOpenHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public boolean create(Product product) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = createContentValue(product);

        long insertedRow = db.insert(TABLE_NAME, null, values);
        return insertedRow != -1;
    }

    public Optional<Product> get(int reference) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = { _ID, COLUMN_NAME_REFERENCE, COLUMN_NAME_DESCRIPTION, COLUMN_NAME_COST, COLUMN_NAME_STOCK };

        Query query = createSelectionQuery(reference, true);

        Cursor cursor = db.query(TABLE_NAME, projection, query.getSelection(), query.getArgs(), null, null, null);

        Optional<Product> product = Optional.empty();
        if (cursor.moveToFirst()) {
            product = Optional.of(mapProduct(cursor));
        }

        cursor.close();
        return product;
    }

    public boolean delete(int reference) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Query query = createSelectionQuery(reference, true);

        int deletedRows = db.delete(TABLE_NAME, query.getSelection(), query.getArgs());

        return deletedRows != 0;
    }

    public boolean update(Product product, int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = createContentValue(product);
        Query query = createSelectionQuery(id, false);

        int count = db.update(TABLE_NAME, values, query.getSelection(), query.getArgs());

        return count != 0;
    }

    // UTILS
    private Product mapProduct(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndexOrThrow(_ID));
        int reference = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NAME_REFERENCE));
        String description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_DESCRIPTION));
        double cost = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_NAME_COST));
        int stock = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NAME_STOCK));

        return new Product(id, reference, description, cost, stock);
    }

    private ContentValues createContentValue(Product product) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME_REFERENCE, product.getReference());
        values.put(COLUMN_NAME_DESCRIPTION, product.getDescription());
        values.put(COLUMN_NAME_COST, product.getCost());
        values.put(COLUMN_NAME_STOCK, product.getStock());

        return values;
    }

    private Query createSelectionQuery(int arg, boolean searchByReference) {
        String columnName = searchByReference ? COLUMN_NAME_REFERENCE : _ID;
        String selection = columnName + " = ?";
        String[] selectionArgs = { Integer.toString(arg) };

        return new Query(selection, selectionArgs);
    }
}
