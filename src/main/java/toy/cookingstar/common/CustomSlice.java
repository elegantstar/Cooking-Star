package toy.cookingstar.common;

import lombok.Data;
import org.springframework.data.domain.Slice;

import java.util.List;

@Data
public class CustomSlice<T> {
    private List<T> content;
    private boolean hasNext;
    private boolean last;

    public static <T> CustomSlice<T> convertFromSlice(Slice<T> slice) {
        CustomSlice<T> converted = new CustomSlice<>();
        converted.content = slice.getContent();
        converted.hasNext = slice.hasNext();
        converted.last = slice.isLast();
        return converted;
    }
}
