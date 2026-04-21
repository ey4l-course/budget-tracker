package com.budget.common.client;

import com.budget.common.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient (name = "internalTxnClient", url = "${routes.transactions}", configuration = FeignConfig.class)
public interface InternalTxnClient {

}
