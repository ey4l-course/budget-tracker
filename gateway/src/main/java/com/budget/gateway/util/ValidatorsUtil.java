package com.budget.gateway.util;

import com.budget.common.dto.RegisterDto;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class ValidatorsUtil {
    final private Pattern validEmail = Pattern.compile("^[a-zA-Z0-9-_~+.]{2,30}@[a-zA-Z0-9]{2,15}(\\.[a-zA-Z]{2,3}){1,2}$");
    final private Pattern validName = Pattern.compile("^(?=.{3,20}$)[a-zA-Z]{2,}(?: [a-zA-Z]{2,})*$");
    final private Pattern validMobile = Pattern.compile("^\\d{10,15}$");
    final private Pattern validUserName = Pattern.compile("^(?!.*( )\1)[a-zA-Z0-9._\\-$^~]{5,20}$");
    final private Pattern validPassword = Pattern.compile("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[-!@#$%^&*()_./]).{8,}$");

    public void validateRegistrationData (RegisterDto user) {
        if (user.getId() == null || user.getId().isEmpty() || !idChecksum(user.getId()))
            throw new IllegalArgumentException("Invalid or missing ID");
        if (isUsernameInvalid(user.getUsername()))
            throw new IllegalArgumentException("User name must contain letters, digits or ._-$^~");
        if (user.getPassword() == null || user.getPassword().isEmpty() || !validPassword.matcher(user.getPassword()).matches())
            throw new IllegalArgumentException("Password must be 8-20 characters long and contain at least 1 upper case, 1 lower case, 1 digit and 1 symbol (-!@#$%^&*()_./)");
        if (user.getGivenName() == null || user.getGivenName().isEmpty() || !validName.matcher(user.getGivenName()).matches())
            throw new IllegalArgumentException("Given name must be 3-20 character length, may include additional name separated by single space and cannot be blank");
        if (user.getSurname() == null || user.getSurname().isEmpty() || !validName.matcher(user.getSurname()).matches())
            throw new IllegalArgumentException("Surname must be 3-20 character length, may include additional name separated by single space and cannot be blank");
        if (user.getEmail() == null || user.getEmail().isEmpty() || !validEmail.matcher(user.getEmail()).matches())
            throw new IllegalArgumentException("Invalid E-mail address");
        if (user.getMobile() == null || user.getMobile().isEmpty() || !validMobile.matcher(user.getMobile()).matches())
            throw new IllegalArgumentException("Mobile must be 10-15 digit long, may include state prefix without + or separators");
    }

    public boolean isUsernameInvalid(String username){
        return username == null || username.isEmpty() || !validUserName.matcher(username).matches();
    }

    private boolean idChecksum(String id) {
        if (id.length() != 9)
            return false;
        int sum = 0;
        int validation = Character.getNumericValue(id.charAt(8));
        for (int i = 0; i < 8; i++){
            int digit = Character.getNumericValue(id.charAt(i));
            if (i % 2 != 0){
                int newDigit = digit * 2;
                if (newDigit > 9) {
                    sum += (newDigit % 10) + (newDigit / 10);
                }else {
                    sum += newDigit;
                }
            }else {
                sum += digit;
            }
        }
        return (sum + validation) % 10 == 0;
    }
}
