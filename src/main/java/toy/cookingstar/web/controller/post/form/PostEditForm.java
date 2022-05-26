package toy.cookingstar.web.controller.post.form;

import org.hibernate.validator.constraints.Length;

import lombok.Data;
import toy.cookingstar.service.post.StatusType;

@Data
public class PostEditForm {

    //이미지 수정 불가
    @Length(max = 2200)
    private String content;

    private StatusType status;

}
