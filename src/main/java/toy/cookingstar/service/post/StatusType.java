package toy.cookingstar.service.post;

import org.apache.ibatis.type.MappedTypes;

import toy.cookingstar.handler.StatusEnum;
import toy.cookingstar.handler.StatusEnumTypeHandler;

public enum StatusType implements StatusEnum {

    POSTING("posting"),
    TEMPORARY_STORAGE("temporaryStorage"),
    PRIVATE("private");

    private final String status;

    StatusType(String status) {
        this.status = status;
    }

    @MappedTypes(StatusType.class)
    public static class TypeHandler extends StatusEnumTypeHandler<StatusType> {
        public TypeHandler() {
            super(StatusType.class);
        }
    }

    @Override
    public String getStatus() {
        return status;
    }
}
