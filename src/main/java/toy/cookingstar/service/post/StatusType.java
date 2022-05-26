package toy.cookingstar.service.post;

import org.apache.ibatis.type.MappedTypes;

import toy.cookingstar.handler.StatusEnum;
import toy.cookingstar.handler.StatusEnumTypeHandler;

public enum StatusType implements StatusEnum {

    POSTING("POSTING"),
    TEMPORARY_STORAGE("TEMPORARY_STORAGE"),
    PRIVATE("PRIVATE"),
    DELETED("DELETED");

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
