package com.example.storageminiprojectcodebase2.data.entity;


import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "orders",
        foreignKeys = @ForeignKey(
                entity = User.class,
                parentColumns = "id",
                childColumns = "userId",
                onDelete = ForeignKey.CASCADE
        )
)
public class Order {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int userId;
    public String status;   // "PENDING" | "PAID"
    public long createdAt;
    public double totalAmount;
}
