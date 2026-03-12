package com.budget.transactions.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDTO {
    private String name;
    private BigDecimal subtotal;
    private List<TransactionResponseDTO> content;

    public void updateSubtotal (BigDecimal addition) {
        if (subtotal != null)
            this.subtotal = subtotal.add(addition);}
}
