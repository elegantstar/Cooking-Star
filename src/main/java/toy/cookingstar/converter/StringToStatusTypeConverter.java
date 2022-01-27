package toy.cookingstar.converter;

import org.springframework.core.convert.converter.Converter;

import toy.cookingstar.service.post.StatusType;

public class StringToStatusTypeConverter implements Converter<String, StatusType> {

    @Override
    public StatusType convert(String source) {
        return StatusType.valueOf(source);
    }
}
