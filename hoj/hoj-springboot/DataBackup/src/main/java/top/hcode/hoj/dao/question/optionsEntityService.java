package top.hcode.hoj.dao.question;

import com.baomidou.mybatisplus.extension.service.IService;
import org.thymeleaf.model.ICDATASection;
import top.hcode.hoj.pojo.entity.question.options;

import java.util.List;

public interface optionsEntityService extends IService<options> {

    List<options> getOptionsByQid(String questionId);

}
