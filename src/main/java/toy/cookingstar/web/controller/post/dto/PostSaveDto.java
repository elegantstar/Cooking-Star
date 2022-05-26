package toy.cookingstar.web.controller.post.dto;

import lombok.Data;
import toy.cookingstar.service.post.StatusType;

import java.util.List;

@Data
public class PostSaveDto {
    private String content;
    private List<String> postImageUris;
    private StatusType status;
}
