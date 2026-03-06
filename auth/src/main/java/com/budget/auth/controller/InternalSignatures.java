package com.budget.auth.controller;

import com.budget.auth.config.SecurityProperties;
import com.budget.auth.service.IdentityUtil;
import com.budget.auth.util.CustomAccessDeniedException;
import com.budget.common.dto.SecretServiceDTO;
import com.budget.common.dto.SecurityLogDto;
import com.budget.common.exceptions.CriticalIncidentException;
import com.budget.common.utilities.LogUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.util.matcher.IpAddressMatcher;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/internal")
public class InternalSignatures {
    private final IdentityUtil idUtil;
    private final LogUtil logger;
    private final Map<String, String> secrets;
    private final Map<String, String> subnets;

    public InternalSignatures (IdentityUtil idUtil,
                               LogUtil logger,
                               SecurityProperties properties){
        this.idUtil = idUtil;
        this.logger = logger;
        this.subnets = properties.getAllowedSubnets();
        this.secrets = properties.getClusterKeys();
    }

    @PostMapping ("/sign-me")
    public ResponseEntity<String> secretService (@RequestHeader ("X-bootstrap-secret") String providedSecret,
                                                 @RequestParam ("service") String whoIsIt,
                                                 HttpServletRequest request){
        try {
            String expectedSecret = secrets.get(whoIsIt);
            String ip = request.getRemoteAddr();

            boolean isAllowedIp = subnets
                    .values()
                    .stream()
                    .anyMatch(cidr -> new IpAddressMatcher(cidr).matches(ip));
            if (!isAllowedIp)
                throw new CustomAccessDeniedException(new SecurityLogDto(whoIsIt, idUtil.passwordFP(providedSecret), "Unknown host"));

            if (expectedSecret != null && expectedSecret.equals(providedSecret)){
                String sig = idUtil.internalSignatureHandler(whoIsIt);
                logger.secretService(
                        new SecretServiceDTO(
                                whoIsIt,
                                ip,
                                idUtil.passwordFP(sig)));
                return ResponseEntity.ok(sig);
            }
            throw new  CustomAccessDeniedException(new SecurityLogDto(whoIsIt, idUtil.passwordFP(providedSecret), "Invalid or missing secret"));
        }catch (CustomAccessDeniedException e){
            throw new CriticalIncidentException("Violation at secret service", e);
        }
    }
}
