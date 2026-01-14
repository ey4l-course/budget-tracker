package com.budget.common.utilities;

import com.budget.common.config.ObjectMapperConfig;
import com.budget.common.dto.LogCategory;
import com.budget.common.dto.RequestContextDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.Instant;

@SpringJUnitConfig
@ContextConfiguration(classes = {LogUtil.class, ObjectMapperConfig.class})
class LogUtilTest {

    private final LogUtil logUtil;

    @Autowired
    LogUtilTest(LogUtil logUtil) {
        this.logUtil = logUtil;
    }

    @Test
    void testLogRequest() {
        RequestContextDTO request = new RequestContextDTO("/register", "POST", "JUnit-Agent");
        request.setCategory(LogCategory.USER_ERROR);
        request.setIp("127.0.0.1");
        request.setUserName("test_user");
        request.setOutcome("Test outcome");
        request.setStatusCode(HttpStatus.BAD_REQUEST);
        request.setStatusMessage("Bad Request");
        request.setPayload("{\"username\":\"test_user\"}");
        request.setStartProcess(Instant.now());
        request.setEndProcess(Instant.now());

        String uuid = logUtil.logRequest(request);
        System.out.println("Returned UUID: " + uuid);
    }
}
