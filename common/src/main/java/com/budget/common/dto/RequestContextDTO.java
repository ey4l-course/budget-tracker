package com.budget.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatusCode;

import java.time.Instant;
import java.util.List;

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
        private HttpStatusCode statusCode;
        private Object message;
        private Instant startProcess;
        private Instant endProcess;
        private Exception debug;
        private String payload;
        private String uuid;
        private String source;
        private List<String> cookies;

        public RequestContextDTO(String entryRoute, String method, String userAgent) {
            this.userName = "n/a";
            this.method = method;
            this.entryRoute = entryRoute;
            this.userAgent = userAgent;
            this.startProcess = Instant.now();
        }
}
