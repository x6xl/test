package top.hcode.hoj.manager.file;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import top.hcode.hoj.common.exception.StatusFailException;
import top.hcode.hoj.common.exception.StatusForbiddenException;
import top.hcode.hoj.manager.admin.question.AdminQuestionManager;
import top.hcode.hoj.pojo.dto.QuestionDTO;
import top.hcode.hoj.pojo.dto.questionOptionFileDTO;
import top.hcode.hoj.pojo.dto.questionsFileDTO;
import top.hcode.hoj.pojo.entity.question.content;
import top.hcode.hoj.pojo.entity.question.options;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j(topic = "hoj")
public class questionsFileManager {
    @Resource
    AdminQuestionManager adminQuestionManager;
    @Resource
    RabbitTemplate rabbitTemplate;

    public void importQuestion(MultipartFile file) throws IOException, StatusForbiddenException, StatusFailException {
        String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
        if (!"xlsx".toUpperCase().contains(suffix.toUpperCase())) {
            throw new StatusFailException("请上传xlsx格式的excel题目文件！");
        }

        InputStream inputStream = file.getInputStream();

        questionsImportListener questionListener = new questionsImportListener();
        questionOptionImportListener questionOptionImportListener = new questionOptionImportListener();

        // 读取第一个工作表的题目数据
        EasyExcel.read(inputStream, questionsFileDTO.class, questionListener).sheet().doRead();

        // 关闭第一个工作表的输入流
        inputStream.close();

        // 重新打开输入流，读取第二个工作表的选项数据
        inputStream = file.getInputStream();
        EasyExcel.read(inputStream, questionOptionFileDTO.class, questionOptionImportListener).sheet(1).doRead();

        // 关闭第二个工作表的输入流
        inputStream.close();

        // 获取监听器中的数据
        List<questionsFileDTO> questions = questionListener.getData();
        List<questionOptionFileDTO> options = questionOptionImportListener.getData();

        // 处理题目和选项数据的关联
        processQuestionAndOptions(questions, options);
    }

    private List<QuestionDTO> loadQuestionDTOList(List<questionsFileDTO> questions, List<questionOptionFileDTO> options) {
        Map<String, QuestionDTO> questionMap = new HashMap<>();
        for (questionsFileDTO question : questions) {
            QuestionDTO questionDTO = questionMap.computeIfAbsent(question.getQuestionId(), k -> new QuestionDTO());
            if (questionDTO.getContent() == null) {
                System.out.println("+1:" + Thread.currentThread().getName());
                questionDTO.setContent(convertToContent(question));
            }
        }

        for (questionOptionFileDTO option : options) {
            QuestionDTO questionDTO = questionMap.computeIfAbsent(option.getQuestionId(), k -> new QuestionDTO());
            if (questionDTO.getOptions() == null) {
                questionDTO.setOptions(new ArrayList<>());
            }
            questionDTO.getOptions().add(convertToOption(option));
        }
        return new ArrayList<>(questionMap.values());
    }

    private void processQuestionAndOptions(List<questionsFileDTO> questions, List<questionOptionFileDTO> options) throws StatusForbiddenException, StatusFailException {
        List<QuestionDTO> questionDTOList = loadQuestionDTOList(questions, options);
        for (QuestionDTO questionDTO : questionDTOList) {
            rabbitTemplate.convertAndSend("questionImportFile", questionDTO);
        }
    }

    public static content convertToContent(questionsFileDTO dto) {
        content content = new content();
        content.setQuestionId(dto.getQuestionId());
        content.setAuthor(dto.getAuthor());
        content.setAuthorId(dto.getAuthorId());
        content.setQuestionType(dto.getQuestionType());
        content.setQuestionScore(dto.getQuestionScore());
        content.setQuestionContent(dto.getQuestionContent());
        content.setRightAnswer(dto.getRightAnswer());
//        content.setCreateTime(dto.getCreateTime());
        return content;
    }

    public static options convertToOption(questionOptionFileDTO dto) {
        options options = new options();
        options.setQuestionId(dto.getQuestionId());
        options.setAuthor(dto.getAuthor());
        options.setAuthorId(dto.getAuthorId());
        options.setOptionContent(dto.getOptionContent());
        return options;
    }

    public class questionOptionImportListener extends AnalysisEventListener<questionOptionFileDTO> {
        private List<questionOptionFileDTO> list = new ArrayList<>();

        @Override
        public void invoke(questionOptionFileDTO questionOptionFileDTO, AnalysisContext analysisContext) {
            list.add(questionOptionFileDTO);
        }

        @Override
        public void doAfterAllAnalysed(AnalysisContext analysisContext) {

        }

        public List<questionOptionFileDTO> getData() {
            return list;
        }
    }

    public class questionsImportListener extends AnalysisEventListener<questionsFileDTO> {
        private List<questionsFileDTO> list = new ArrayList<>();

        @Override
        public void invoke(questionsFileDTO questionsFileDTO, AnalysisContext analysisContext) {
            list.add(questionsFileDTO);
        }

        @Override
        public void doAfterAllAnalysed(AnalysisContext analysisContext) {
            // 可以在这里进行一些数据处理或清理工作
        }

        public List<questionsFileDTO> getData() {
            return list;
        }
    }
}
