package com.example.client;

import com.example.dto.FineDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
@FeignClient(name = "overdue-fines", url = "http://localhost:8088")
public interface OverdueFinesClient {
    @PostMapping("/fines/generate/{transactionId}")
    int generateFine(@PathVariable int transactionId);  // ✅ Returns only Fine ID
    
    @GetMapping("/fines/fine-details/{transactionId}")
    FineDTO getFineDetails(@PathVariable int transactionId);

    @PatchMapping("/fines/pay/{fineId}")
    String payFine(@PathVariable int fineId);
}
