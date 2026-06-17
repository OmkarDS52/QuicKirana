package com.example.Kirana.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.example.Kirana.DTOs.Requests.ProductRequest;
import com.example.Kirana.DTOs.Response.ProductResponse;
import com.example.Kirana.Service.ProductService;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // Shop owner adds product
    @PostMapping
    public ResponseEntity<ProductResponse> addProduct(
            Authentication auth,
            @RequestBody ProductRequest request) {
        String ownerId = auth.getPrincipal().toString();
        return ResponseEntity.ok(productService.addProduct(ownerId, request));
    }

    // Shop owner updates product
    @PutMapping("/{productId}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable String productId,
            @RequestBody ProductRequest request) {
        return ResponseEntity.ok(productService.updateProduct(productId, request));
    }

    // Shop owner toggles product visibility
    @PatchMapping("/{productId}/toggle")
    public ResponseEntity<ProductResponse> toggleProduct(@PathVariable String productId) {
        return ResponseEntity.ok(productService.toggleProduct(productId));
    }

    // Shop owner updates stock status
    @PatchMapping("/{productId}/stock/{status}")
    public ResponseEntity<ProductResponse> updateStock(
            @PathVariable String productId,
            @PathVariable String status) {
        return ResponseEntity.ok(productService.updateStock(productId, status));
    }

    // Customer browses shop products
    @GetMapping("/shop/{shopId}")
    public ResponseEntity<List<ProductResponse>> getByShop(@PathVariable String shopId) {
        return ResponseEntity.ok(productService.getProductsByShop(shopId));
    }

    // Customer filters by category
    @GetMapping("/shop/{shopId}/category/{category}")
    public ResponseEntity<List<ProductResponse>> getByCategory(
            @PathVariable String shopId,
            @PathVariable String category) {
        return ResponseEntity.ok(productService.getProductsByCategory(shopId, category));
    }
}
