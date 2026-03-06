package com.budget.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignatureVerificationDTO {
    String service; //Used for services only
    Long timeStamp;
    byte[] payload;
    byte[] signature;
}
