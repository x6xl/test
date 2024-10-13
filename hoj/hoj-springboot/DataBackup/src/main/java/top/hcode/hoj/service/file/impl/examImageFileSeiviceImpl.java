package top.hcode.hoj.service.file.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.shiro.SecurityUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import top.hcode.hoj.common.exception.StatusSystemErrorException;
import top.hcode.hoj.common.result.CommonResult;
import top.hcode.hoj.manager.file.examImageFileManager;
import top.hcode.hoj.pojo.dto.examImageShotDTO;
import top.hcode.hoj.service.file.examImageFileService;
import top.hcode.hoj.shiro.AccountProfile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class examImageFileSeiviceImpl implements examImageFileService {
    @Resource
    examImageFileManager examImageFileManager;
    @Resource
    RabbitTemplate rabbitTemplate;
    @Override
    public CommonResult<Map<Object, Object>> addExamImageShot(MultipartFile image, String examId) throws StatusSystemErrorException, IOException {


        byte[] imageBytes = image.getBytes(); // 获取文件内容的字节数组
        // 将文件内容转换为 Base64 字符串
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);
        examImageShotDTO examImageShotDTO=new examImageShotDTO();
        examImageShotDTO.setExamId(examId);
        examImageShotDTO.setImageBase64(base64Image);
        AccountProfile userRolesVo = (AccountProfile) SecurityUtils.getSubject().getPrincipal();
        examImageShotDTO.setExamJoinerId(userRolesVo.getUid());
        rabbitTemplate.convertAndSend("examImageShot",examImageShotDTO);
        return CommonResult.successResponse();
    }


    @Override
    public CommonResult<Map<String, List<HashMap<String, Object>>>> getAllExamImageShot(String examId) {
        return CommonResult.successResponse(examImageFileManager.getAllExamImageShot(examId));
    }


}
