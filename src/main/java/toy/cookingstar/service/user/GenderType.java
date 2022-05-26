package toy.cookingstar.service.user;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum GenderType {

    MALE("남성"), FEMALE("여성"), NOT_OPEN("비공개");

    private final String description;

    public String getDescription() {
        return description;
    }
}
