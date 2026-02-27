package com.budget.auth.controller;

import com.budget.auth.config.SecurityProperties;
import com.budget.auth.service.IdentityUtil;
import com.budget.auth.util.CustomAccessDeniedException;
import com.budget.common.dto.SecretServiceDTO;
import com.budget.common.dto.SecurityLogDto;
import com.budget.common.exceptions.CriticalIncidentException;
import com.budget.common.utilities.LogUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
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
    private Map<String, String> subnets;

    public InternalSignatures (IdentityUtil idUtil,
                               LogUtil logger,
                               SecurityProperties properties){
        this.idUtil = idUtil;
        this.logger = logger;
        this.secrets = properties.getClusterKeys();
        this.subnets = properties.getAllowedSubnets();
    }

    @PostMapping ("/sign-me")
    public ResponseEntity<String> secretService (@RequestHeader ("X-bootstrap-secret") String providedSecret,
                                                 @RequestParam String whoIsIt,
                                                 HttpServletRequest request){
        try {
            String expected = secrets.get(whoIsIt);
            String ip = request.getRemoteAddr();

            boolean isAllowedIp = subnets
                    .values()
                    .stream()
                    .anyMatch(cidr -> new IpAddressMatcher(cidr).matches(ip));
            if (!isAllowedIp)
                throw new CustomAccessDeniedException(new SecurityLogDto(whoIsIt, idUtil.passwordFP(providedSecret), "Unknown host"));

            if (expected != null && expected.equals(providedSecret)){
                String sig = idUtil.internalSignatureHandler(whoIsIt);
                logger.secretService(
                        new SecretServiceDTO(
                                whoIsIt,
                                ip,
                                idUtil.passwordFP(sig)));
                return ResponseEntity.ok(sig);
            }
            throw new  CustomAccessDeniedException(new SecurityLogDto(whoIsIt, idUtil.passwordFP(providedSecret), "Invalid secret"));
        }catch (CustomAccessDeniedException e){
            throw new CriticalIncidentException("Violation at secret service", e);
        }

    }
}
