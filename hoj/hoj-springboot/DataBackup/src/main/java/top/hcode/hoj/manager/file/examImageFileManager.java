package top.hcode.hoj.manager.file;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import top.hcode.hoj.common.exception.StatusFailException;
import top.hcode.hoj.common.exception.StatusSystemErrorException;
import top.hcode.hoj.common.result.CommonResult;
import top.hcode.hoj.dao.question.examImageShotEntityService;
import top.hcode.hoj.pojo.dto.examImageShotDTO;
import top.hcode.hoj.pojo.entity.question.examImageShot;
import top.hcode.hoj.repository.examImageShotRepository;
import top.hcode.hoj.shiro.AccountProfile;
import top.hcode.hoj.utils.Constants;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.bouncycastle.crypto.tls.ConnectionEnd.client;

@Component
@RefreshScope
@Slf4j(topic = "hoj")
public class examImageFileManager {
    @Resource
    examImageShotEntityService examImageShotEntityService;

    @Resource
    examImageShotRepository examImageShotRepository;

//    public Map<Object, Object> addExamImageShot(MultipartFile image, String examId) throws StatusSystemErrorException {
//        //获取文件后缀
//        String suffix = image.getOriginalFilename().substring(image.getOriginalFilename().lastIndexOf(".") + 1);
//        //通过UUID生成唯一文件名
//        String filename = IdUtil.simpleUUID() + "." + suffix;
//
//        FileUtil.mkdir(Constants.File.EXAM_IMAGE_SHOT.getPath());
//
//        try {
//            image.transferTo(FileUtil.file(Constants.File.EXAM_IMAGE_SHOT.getPath() + File.separator + filename));
//        } catch (Exception e) {
//            log.error("图片文件上传异常-------------->{}", e.getMessage());
//            throw new StatusSystemErrorException("服务器异常：图片上传失败！");
//        }
//
//        String filepath=Constants.File.EXAM_IMAGE_SHOT.getPath() + File.separator + filename;
//        String folderPath=Constants.File.EXAM_IMAGE_SHOT.getPath();
////        AccountProfile userRolesVo = (AccountProfile) SecurityUtils.getSubject().getPrincipal();
//
//        examImageShot examImageShot=new examImageShot();
//        examImageShot.setExamId(examId);
//        examImageShot.setFolderPath(folderPath);
//        examImageShot.setShotTime(LocalDateTime.now());
////        examImageShot.setExamJoinerId(userRolesVo.getUid());
//        examImageShot.setExamJoinerId("xjl66");
//        examImageShot.setFilePath(filepath);
//        examImageShot.setName(filename);
//
//        examImageShotEntityService.saveOrUpdate(examImageShot);
//
//        return MapUtil.builder()
//                .put("id", examImageShot.getId())
//                .put("url", Constants.File.IMG_API.getPath() + filename)
//                .map();
//    }

        @RabbitListener(queuesToDeclare = @Queue("examImageShot"))
        public void processMessage(examImageShotDTO examImageShotDTO) {
            try{
                addExamImageShot(examImageShotDTO);
            }catch (StatusFailException e) {
                log.error("Failed to process message: {}", e.getMessage());
               } catch (Exception e) {
                log.error("Unexpected error: {}", e.getMessage());
            }
        }

        @RabbitHandler
        public void addExamImageShot(examImageShotDTO examImageShotDTO) throws StatusFailException {
                String examId = examImageShotDTO.getExamId();
                String base64Image = examImageShotDTO.getImageBase64();

                byte[] imageBytes = Base64.getDecoder().decode(base64Image.getBytes());

                String suffix = "jpg";
                String filename = IdUtil.simpleUUID() + "." + suffix;

                FileUtil.mkdir(Constants.File.EXAM_IMAGE_SHOT.getPath());

                try (OutputStream stream = new FileOutputStream(Constants.File.EXAM_IMAGE_SHOT.getPath() + File.separator + filename)) {
                    stream.write(imageBytes);
                } catch (Exception e) {
                    log.error("图片文件上传异常-------------->{}", e.getMessage());
                    throw new StatusFailException("服务器异常：图片上传失败！");
                }

                String filepath = Constants.File.EXAM_IMAGE_SHOT.getPath() + File.separator + filename;
                String folderPath = Constants.File.EXAM_IMAGE_SHOT.getPath();
                String examJoinId=examImageShotDTO.getExamJoinerId();


                examImageShot examImageShot = new examImageShot();
                examImageShot.setExamId(examId);
                examImageShot.setFolderPath(folderPath);
                examImageShot.setShotTime(LocalDateTime.now());
                examImageShot.setExamJoinerId(examJoinId);
                examImageShot.setFilePath(filepath);
                examImageShot.setName(filename);

                examImageShotEntityService.save(examImageShot);
                examImageShotRepository.save(examImageShot);
        }

    public Map<String, List<HashMap<String, Object>>> getAllExamImageShot1(String examId) {
        QueryWrapper<examImageShot> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("exam_id", examId);
        List<examImageShot> fileList = examImageShotEntityService.list(queryWrapper);

        Map<String, List<examImageShot>> groupedByExamJoiner = fileList.stream()
                .collect(Collectors.groupingBy(examImageShot::getExamJoinerId));

        Map<String, List<HashMap<String, Object>>> resultMap = new HashMap<>();
        groupedByExamJoiner.forEach((examJoiner, shots) -> {
            List<HashMap<String, Object>> list = shots.stream().map(f -> {
                HashMap<String, Object> map = new HashMap<>();
                map.put("id", f.getId());
                map.put("url", Constants.File.IMG_API.getPath() + f.getName());
                return map;
            }).collect(Collectors.toList());
            resultMap.put(examJoiner, list);
        });
        return resultMap;
    }


    public Map<String, List<HashMap<String, Object>>> getAllExamImageShot(String examId) {
//        QueryWrapper<examImageShot> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("exam_id", examId);
//        List<examImageShot> fileList = examImageShotEntityService.list(queryWrapper);

        MatchQueryBuilder matchQueryBuilder=QueryBuilders.matchQuery("examId",examId);
        Iterable<examImageShot> search = examImageShotRepository.search(matchQueryBuilder);
        List<examImageShot> fileList = StreamSupport.stream(search.spliterator(),false)
                .collect(Collectors.toList());

        Map<String, List<examImageShot>> groupedByExamJoiner = fileList.stream()
                .collect(Collectors.groupingBy(examImageShot::getExamJoinerId));

        Map<String, List<HashMap<String, Object>>> resultMap = new HashMap<>();
        groupedByExamJoiner.forEach((examJoiner, shots) -> {
            List<HashMap<String, Object>> list = shots.stream().map(f -> {
                HashMap<String, Object> map = new HashMap<>();
                map.put("id", f.getId());
                map.put("url", Constants.File.IMG_API.getPath() + f.getName());
                return map;
            }).collect(Collectors.toList());
            resultMap.put(examJoiner, list);
        });

        return resultMap;
    }
}
