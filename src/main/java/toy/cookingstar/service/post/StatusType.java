package toy.cookingstar.service.post;

import lombok.Getter;

@Getter
public enum StatusType {

    POSTING("posting"), TEMPORARY_STORAGE("temporaryStorage"), PRIVATE("private");

    private final String status;

    StatusType(String status) {
        this.status = status;
    }


}
