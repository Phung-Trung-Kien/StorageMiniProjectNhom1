package com.example.storageminiprojectcodebase2.data.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "products",
        foreignKeys = @ForeignKey(
                entity = Category.class,
                parentColumns = "id",
                childColumns = "categoryId",
                onDelete = ForeignKey.SET_NULL
        )
)
public class Product {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String name;
    public String description;
    public double price;
    public int stock;
    public Integer categoryId;
    public String imageUrl;
}
