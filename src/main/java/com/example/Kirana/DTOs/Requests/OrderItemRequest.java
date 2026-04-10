package com.example.Kirana.DTOs.Requests;

import lombok.Data;

@Data
public class OrderItemRequest {
    private String productId;    // required — must exist in products table
    private String quantity;     // still a number now, not free text
    private String note;         // optional — "less spicy" etc
}
