package top.hcode.hoj.pojo.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class examImageShotDTO {

    private String examJoinerId;

    private String examId;

    private String imageBase64;

}
