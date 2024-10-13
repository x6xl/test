package top.hcode.hoj.pojo.dto;


import cn.hutool.core.date.DateTime;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.List;

@Data
@Accessors(chain = true)
public class examFinishDTO {
    private  HashMap<String, List<String>> answersMap;

    private DateTime examJoinDate;

    private int examTimeCost;
}
