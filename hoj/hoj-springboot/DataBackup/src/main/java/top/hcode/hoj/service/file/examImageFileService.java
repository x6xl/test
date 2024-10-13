package top.hcode.hoj.service.file;

import org.springframework.web.multipart.MultipartFile;
import top.hcode.hoj.common.exception.StatusSystemErrorException;
import top.hcode.hoj.common.result.CommonResult;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface examImageFileService {

    CommonResult<Map<Object, Object>> addExamImageShot(MultipartFile image, String examId) throws StatusSystemErrorException, IOException;


    CommonResult<Map<String, List<HashMap<String, Object>>>> getAllExamImageShot(String examId);
}
