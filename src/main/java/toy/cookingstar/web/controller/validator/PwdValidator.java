package toy.cookingstar.web.controller.validator;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

@Component
public class PwdValidator {

    public void validEquality(String password1, String password2, BindingResult bindingResult) {

        if (!StringUtils.equals(password1, password2)) {
            bindingResult.reject("password.inconsistency");
        }
    }
}
