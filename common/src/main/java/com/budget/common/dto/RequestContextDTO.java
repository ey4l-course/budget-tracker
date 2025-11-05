package com.budget.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestContextDTO {
        private LogCategory category;
        private String ip;
        private String userAgent;
        private String userName;
        private String method;
        private String entryRoute;
        private String outcome;
        private int statusCode;
        private String statusMessage;
        private Instant startProcess;
        private Instant endProcess;
        private Exception debug;
        private String payload;

        public RequestContextDTO(String entryRoute, String method, String userAgent) {
            this.userName = "n/a";
            this.method = method;
            this.entryRoute = entryRoute;
            this.userAgent = userAgent;
            this.startProcess = Instant.now();
        }
}
