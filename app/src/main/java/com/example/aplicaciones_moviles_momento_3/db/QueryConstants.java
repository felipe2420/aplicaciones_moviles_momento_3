package com.example.aplicaciones_moviles_momento_3.db;

import com.example.aplicaciones_moviles_momento_3.db.tables.ProductsTable;

public class QueryConstants {
    private static final String SQL_CREATE_PRODUCTS_TABLE =
            "CREATE TABLE " + ProductsTable.TABLE_NAME + " (" +
                    ProductsTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    ProductsTable.COLUMN_NAME_REFERENCE + " INTEGER UNIQUE," +
                    ProductsTable.COLUMN_NAME_DESCRIPTION + " TEXT," +
                    ProductsTable.COLUMN_NAME_COST + " REAL," +
                    ProductsTable.COLUMN_NAME_STOCK + " INTEGER)";

    public static  final String SQL_CREATE_TABLES = SQL_CREATE_PRODUCTS_TABLE;

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + ProductsTable.TABLE_NAME;
}
