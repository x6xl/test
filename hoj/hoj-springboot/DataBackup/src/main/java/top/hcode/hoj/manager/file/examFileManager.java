package top.hcode.hoj.manager.file;


import cn.hutool.core.date.DateTime;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.IdUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import top.hcode.hoj.common.exception.StatusSystemErrorException;
import top.hcode.hoj.dao.question.examImageShotEntityService;
import top.hcode.hoj.dao.question.examRecordEntityService;
import top.hcode.hoj.pojo.entity.question.examImageShot;
import top.hcode.hoj.pojo.entity.question.examRecord;
import top.hcode.hoj.pojo.vo.ExamRecordExcelVO;
import top.hcode.hoj.shiro.AccountProfile;
import top.hcode.hoj.utils.Constants;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j(topic = "hoj")
public class examFileManager {

    @Resource
    examRecordEntityService examRecordEntityService;


    public List<ExamRecordExcelVO> examRecordExcelList(String examId) {
        QueryWrapper<examRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("exam_joiner_id", "exam_join_date", "exam_time_cost", "exam_join_score", "answer_option_ids", "exam_id")
                .eq("exam_id", examId);

        List<examRecord> examRecords = examRecordEntityService.list(queryWrapper);

        List<ExamRecordExcelVO> examRecordExcelVOS = new ArrayList<>();
        for (examRecord examRecord : examRecords) {
            ExamRecordExcelVO excelVO = new ExamRecordExcelVO();
            BeanUtils.copyProperties(examRecord, excelVO);
            examRecordExcelVOS.add(excelVO);
        }
        return examRecordExcelVOS;
    }

    public void getExamRecordExcel(String examId, HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码
        String fileName = URLEncoder.encode(examId, "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
        response.setHeader("Content-Type", "application/xlsx");
        // 创建居中样式的WriteCellStyle
        WriteCellStyle headWriteCellStyle = new WriteCellStyle();
        headWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER); // 头部居中
        WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
        contentWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER); // 内容居中

        // 应用样式到所有列
        HorizontalCellStyleStrategy horizontalCellStyleStrategy =
                new HorizontalCellStyleStrategy(headWriteCellStyle, contentWriteCellStyle);
        EasyExcel.write(response.getOutputStream(), ExamRecordExcelVO.class)
                .registerWriteHandler(horizontalCellStyleStrategy) // 应用样式
                .sheet("考试记录")
                .doWrite(examRecordExcelList(examId));
    }

}
