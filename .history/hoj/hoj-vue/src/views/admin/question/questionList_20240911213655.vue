<template>
  <el-row :gutter="18">
    <el-card shadow>
      <div slot="header">
        <el-row :gutter="20" style="margin-bottom: 0.5em;">
          <el-col :xs="8" :sm="6">
            <el-input v-model="searchKeyword" :placeholder="$t('m.Please_enter_a_keyword')"></el-input>
          </el-col>
          <el-col :xs="4" :sm="6" class="text-right">
            <el-tooltip content="$t('m.Opt_id')" placement="top">
              <el-button type="primary" icon="el-icon-search" @click="searchQuestions"></el-button>
            </el-tooltip>
            <el-tooltip :content="$t('m.Add_question')" placement="top">
            <el-button type="primary" icon="el-icon-circle-plus-outline" @click="addquestions"></el-button>
            </el-tooltip>
            <el-tooltip :content="$t('m.Import_questions_files')" placement="top">
              <el-upload
              class="importQuestions"
              action="/api/file/importQuestion"
              multiple
              :limit="1"
              style="display: inline-block; width: auto;">
                <el-button icon="el-icon-upload" type="primary"></el-button>
              </el-upload>
            </el-tooltip>

            <el-tooltip :content=" examActionText " placement="top">
          <el-button type="primary" icon="el-icon-circle-plus" @click="handleExamAction"></el-button>
            </el-tooltip>
            <el-select v-model="selectedQuestionType" placeholder="选择题目类型" @change="searchQuestions" style="width: 150px; margin-left: 10px;">
              <el-option
                v-for="item in questionTypes"
                :key="item.value"
                :label="item.label"
                :value="item.value">
              </el-option>
            </el-select>
          </el-col>
        </el-row>
        <el-row :gutter="20" style="margin-bottom: 0.5em;">
          <el-col :xs="24" :sm="6">
            <span class="problem-list-title">{{ $t('m.Problem_List') }}</span>
          </el-col>
        </el-row>
      </div>
      <el-table :data="questionList" stripe style="width: 100%" @row-click="handleRowClick">
        <el-table-column fixed prop="id" :label="$t('m.Opt_id')" width="50"></el-table-column>
        <el-table-column prop="questionId" :label="$t('m.Opt_questionId')" width="100"></el-table-column>
        <el-table-column prop="author" :label="$t('m.Opt_author')" width="100"></el-table-column>
        <el-table-column prop="authorId" :label="$t('m.Opt_authorId')" width="100"></el-table-column>
        <el-table-column prop="questionType" :label="$t('m.Opt_questionType')" width="200" :formatter="getQuestionTypeText"></el-table-column>
        <el-table-column prop="questionScore" :label="$t('m.Opt_score')" width="200"></el-table-column>
        <el-table-column prop="questionContent" :label="$t('m.Opt_questionContent')" width="200"
          :show-overflow-tooltip="true"></el-table-column>
        <el-table-column prop="rightAnswer" :label="$t('m.Right_answer')" width="200"></el-table-column>
        <el-table-column prop="createTime" :label="$t('m.Opt_createTime')" width="250"></el-table-column>
        <el-table-column fixed="right" :label="$t('m.Operate')" width="200">
          <template slot-scope="scope">
            <el-tooltip :content="$t('m.Selected')" placement="top">
              <el-checkbox v-model="scope.row.selectedForExam" @change="toggleQuestionSelection(scope.row)"></el-checkbox>
            </el-tooltip> 

            <el-tooltip :content="$t('m.Review_The_Question_Option')" placement="top">
           <el-button v-if="scope.row.questionType != 4" icon="el-icon-more" @click.stop="showOptions(scope.row)" size="mini"></el-button>
           </el-tooltip>
            <el-tooltip :content="$t('m.Delete_The_Question')" placement="top">
               <el-button type="danger" icon="el-icon-delete" @click.stop="handleDelete(scope.row)" size="mini"></el-button>
           </el-tooltip>
          

           <el-tooltip :content="$t('m.Edit_The_Question')" placement="top">
            <el-button type="primary" icon="el-icon-edit" @click.stop="handleEdit(scope.row)" size="mini"></el-button>
          </el-tooltip>

            
          </template>
        </el-table-column>
      </el-table>
      <el-row type="flex" justify="space-between">
  <el-col :span="18">
    <el-pagination
      v-if="!showAll"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      :current-page="currentPage"
      :page-size="pageSize"
      layout="total, sizes, prev, pager, next, jumper"
      :total="totalQuestions"
      :page-sizes="[10, 20, 30, 50]">
    </el-pagination>
  </el-col>
  <el-col :span="6" class="text-right">
    <el-button @click="toggleShowAll">{{ showAll ? $t('m.Page_data') :$t('m.All_data')  }}</el-button>
  </el-col>
</el-row>


      <el-dialog :visible.sync="dialogVisible" title="问题选项">
        <el-table :data="currentQuestionOptions" style="width: 100%">
          <el-table-column prop="id" label="ID" width="80"></el-table-column>
          <el-table-column prop="questionId" label="问题ID" width="120"></el-table-column>
          <el-table-column prop="optionContent" label="选项内容" width="300"></el-table-column>
          <el-table-column prop="author" label="作者" width="80"></el-table-column>
          <el-table-column prop="authorId" label="作者ID" width="80"></el-table-column>
        </el-table>
        <span slot="footer" class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="dialogVisible = false">确定</el-button>
        </span>
      </el-dialog>
      
      <!-- 创建/编辑考试的对话框 -->
      <el-dialog :visible.sync="showExamCreation" :title="examDialogTitle">
        <el-form :model="examDetails" label-width="80px">
          <el-form-item label="考试ID">
            <el-input v-model="examDetails.examId"></el-input>
          </el-form-item>
          <el-form-item label="考试名称">
            <el-input v-model="examDetails.examName"></el-input>
          </el-form-item>
          <el-form-item label="考试描述">
            <el-input v-model="examDetails.examDescription"></el-input>
          </el-form-item>
          <el-form-item label="单选题分数">
            <el-input v-model="examDetails.examScoreRadio"></el-input>
          </el-form-item>
          <el-form-item label="多选题分数">
            <el-input v-model="examDetails.examScoreCheck"></el-input>
          </el-form-item>
          <el-form-item label="判断题分数">
            <el-input v-model="examDetails.examScoreJudge"></el-input>
          </el-form-item>
          <el-form-item label="填空题分数">
            <el-input v-model="examDetails.examScoreFill"></el-input>
          </el-form-item>
          <el-form-item label="代码题分数">
            <el-input v-model="examDetails.examScoreCode"></el-input>
          </el-form-item>
          <el-form-item label="考试密码">
            <el-input v-model="examDetails.password"></el-input>
          </el-form-item>
          <el-form-item label="开始时间">
            <el-date-picker v-model="examDetails.startDate" type="datetime" value-format="yyyy-MM-ddTHH:mm:ss"></el-date-picker>
          </el-form-item>
          <el-form-item label="结束日期">
            <el-date-picker v-model="examDetails.endDate" type="datetime" value-format="yyyy-MM-ddTHH:mm:ss"></el-date-picker>
          </el-form-item>
        </el-form>
        <span slot="footer" class="dialog-footer">
          <el-button @click="showExamCreation = false">取消</el-button>
          <el-button type="primary" @click="submitExam">{{ submitButtonText }}</el-button>
        </span>
      </el-dialog>
    </el-card>
  </el-row>
</template>

<script>
import api from '@/common/api';

export default {
  data() {
    return {
      searchKeyword: '',
      questionList: [],
      currentPage: 1,
      pageSize: 10, // 每页显示条数
      totalQuestions: 0,
      dialogVisible: false,
      currentQuestionOptions: [],
      showExamCreation: false,
      showAll: false, // 控制显示所有数据的状态
      examDetails: {
        examId: '',
        examName: '',
        examDescription: '',  
        examTimeLimit: '',
        examScoreRadio: '',
        examScoreCheck: '',
        examScoreJudge: '',
        examScoreFill: '',
        examScoreCode: '',
        password: null,
        startDate: '',
        endDate: ''
      },
      examId: '', 
      selectedQuestions: {}, // 用于存储选择状态的对象
      selectedQuestionType: -1, // 当前选择的题目类型
    questionTypes: [ 
      { value: -1,label: this.$t('m.All_questions') },
      { value: 2, label: this.$t('m.Multiple_choice_questions') },
      { value: 1, label: this.$t('m.a_choice_questions') },
      { value: 4, label: this.$t('m.Fill_questions') }
    ]
    };
  },
  created() {
    this.examId = this.$route.params.examId; // 获取路由参数中的examId
    if (this.examId) {
      // 如果存在examId，则加载考试信息和关联的题目
      this.getQuestionsForExam(this.examId);
    } else {
      // 如果没有examId，可能是在创建新考试，此时加载普通题目列表
      this.getQuestions();
    }
  },
  computed: {
    examActionText() {
      // 根据路由判断按钮文本是“创建考试”还是“编辑考试”
      return this.$route.name === 'admin-examUpdate' ? this.$t('m.admin_Exam_Update') : this.$t('m.Add_Exam');
    },
    examDialogTitle() {
      // 根据路由判断对话框标题是“创建考试信息”还是“编辑考试信息”
      return this.$route.name === 'admin-examUpdate' ? '编辑考试信息' : '创建考试信息';
    },
    submitButtonText() {
      // 根据路由判断按钮文本是“确定”还是“更新”
      return this.$route.name === 'admin-examUpdate' ? '更新' : '确定';
    }
  },
  methods: {
  getQuestionTypeText(row) {
    console.log("类型：", row.questionType);
    switch (row.questionType.toString()) {
      case '1':
        return '单选题';
      case '2':
        return '多选题';
      case '3':
        return '选择题';
      case '4':
        return '填空题';
      default:
        return '未知题型';
    }
  },

  toggleShowAll() {
    this.showAll = !this.showAll;
    this.pageSize = this.showAll ? -1 : 10;
    this.currentPage = 1;
    this.getQuestions();
  },

  getQuestions() {
    if (this.pageSize === -1) {
      // 显示所有数据
      api.admin_getQuestionList()
        .then(response => {
          this.questionList = response.data.data.map(question => ({
            ...question,
            options: [],
            selectedForExam: !!this.selectedQuestions[question.questionId],
          }));
          this.totalQuestions = this.questionList.length;
        })
        .catch(error => {
          console.error('获取问题列表出错:', error);
        });
    } else {
      // 分页获取数据
      api.admin_getQuestionListPage(this.pageSize, this.currentPage, this.searchKeyword,this.selectedQuestionType)
        .then(response => {
          this.questionList = response.data.data.content.map(question => ({
            ...question,
            options: [],
            selectedForExam: !!this.selectedQuestions[question.questionId],
          }));
          this.totalQuestions = response.data.data.totalElements;
        })
        .catch(error => {
          console.error('获取问题列表出错:', error);
        });
    }
  },

  // 处理选择题目的按钮点击
  toggleQuestionSelection(question) {
    const isSelected = !!this.selectedQuestions[question.questionId];
    if (isSelected) {
      this.$delete(this.selectedQuestions, question.questionId); // 取消选择
    } else {
      this.$set(this.selectedQuestions, question.questionId, true); // 添加选择
    }
    question.selectedForExam = !isSelected; // 更新行内状态
  },
  getQuestionsForExam(examId) {
    // 获取考试信息和已选择的题目
    api.admin_getExam(examId).then(response => {
      const exam = response.data.data.exam;
      const selectQuestionVOs = response.data.data.questionVOs;

      // 设置考试信息
      this.examDetails.examId = exam.examId;
      this.examDetails.examName = exam.examName;
      this.examDetails.examDescription = exam.examDescription;
      this.examDetails.examTimeLimit = exam.examTimeLimit.toString(); // 考试时长可能需要转换成字符串显示
      this.examDetails.examScoreRadio = exam.examScoreRadio.toString();
      this.examDetails.examScoreCheck = exam.examScoreCheck.toString();
      this.examDetails.examScoreJudge = exam.examScoreJudge.toString();
      this.examDetails.examScoreFill = exam.examScoreFill.toString();
      this.examDetails.examScoreCode = exam.examScoreCode.toString();
      this.examDetails.password = response.data.data.password;
      this.examDetails.startDate = exam.examStartDate; // 已经是完整的日期时间格式
      this.examDetails.endDate = exam.examEndDate; // 已经是完整的日期时间格式

      // 获取所有题目列表
      api.admin_getQuestionList().then(response => {

        const allQuestions = response.data.data.map(question => ({
          ...question,
          options: [],
          selectedForExam: selectQuestionVOs.some(selectedQuestion => selectedQuestion.questionId === question.questionId),
        }));

        // 设置题目列表
        this.questionList = allQuestions.map(question => ({
          questionId: question.questionId,
          author: question.author,
          authorId: question.authorId,
          questionType: question.questionType,
          questionScore: question.questionScore,
          questionContent: question.questionContent,
          rightAnswer: question.rightAnswer, // 根据你的数据结构，需要从选项中找到正确答案
          createTime: question.createTime,
          selectedForExam: question.selectedForExam, // 设置选中状态
        }));

        // 更新 `selectedQuestions` 对象
        this.selectedQuestions = this.questionList
          .filter(question => question.selectedForExam)
          .reduce((acc, question) => {
            acc[question.questionId] = true;
            return acc;
          }, {});
      }).catch(error => {
        console.error('获取问题列表出错:', error);
      });
    }).catch(error => {
      console.error('获取考试和题目信息出错:', error);
    });
  },

    searchQuestions() {
      this.currentPage = 1; // 重置当前页为第一页
      this.getQuestions();
    },
    handleCurrentChange(page) {
      this.currentPage = page;
      this.getQuestions();
    },
    handleSizeChange(size) {
      this.pageSize = size;
      this.currentPage = 1; // 切换每页条数时，重置当前页为第一页
      this.getQuestions();
    },

    addquestions() {
      this.$router.push({ name: 'admin-add-question' });
    },

    handleEdit(row) {
      this.$router.push({ name: 'admin-edit-question', params: { questionId: row.questionId } });
    },

    handleDelete(row) {
  if (row.questionType === 4) {
    api.admin_deleteFillQuestion(row.questionId).then(() => {
      this.getQuestions();
    }).catch(error => {
      console.error('删除填空题出错:', error);
    });
  } else {
    api.admin_deleteQuestion(row.questionId)
      .then(() => {
        this.getQuestions();
      }).catch(error => {
        console.error(error);
      });
  }
},

    handleRowClick(row, column, event) {
      // 点击行事件的处理，根据需要添加逻辑
    },

    showOptions(question) {
      api.admin_getOptions(question.questionId)
        .then(response => {
          this.currentQuestionOptions = response.data.data;
          this.dialogVisible = true;
        });
    },

    handleExamAction() {
    const selectedQuestions = this.questionList.filter(question => this.selectedQuestions[question.questionId]);
    
    if (selectedQuestions.length === 0) {
      alert('请至少选择一个问题用于考试！');
      return;
    }
    this.showExamCreation = true;
  },

    submitExam() {
      const selectedQuestions = this.questionList.filter(question => this.selectedQuestions[question.questionId]);
  // 计算考试时长（单位为分钟）
  const startDate = new Date(this.examDetails.startDate).getTime(); // 转换为时间戳
  const endDate = new Date(this.examDetails.endDate).getTime(); // 转换为时间戳
  const examTimeLimitMilliseconds = Math.abs(endDate - startDate); // 计算毫秒差
  const examTimeLimitMinutes = Math.ceil(examTimeLimitMilliseconds / (1000 * 60)); // 转换为分钟并向上取整
  this.examDetails.startDate = new Date(this.examDetails.startDate);
  this.examDetails.endDate = new Date(this.examDetails.endDate);
   // 转换到 GMT+8
   const timezoneOffset = 8 * 60; // GMT+8 时区偏移量 (分钟)
  const startDateGMT8 = new Date(startDate + timezoneOffset * 60000).toISOString();
  const endDateGMT8 = new Date(endDate + timezoneOffset * 60000).toISOString();
  // 构建提交的考试信息对象
  const examData = {
    examId: this.examDetails.examId,
    examName: this.examDetails.examName,
    examDescription: this.examDetails.examDescription,
    questions: selectedQuestions.map(question => ({
        questionId: question.questionId,
        checked: true
      })),
    examTimeLimit: examTimeLimitMinutes, // 设置考试时长为分钟
    examScoreRadio: this.examDetails.examScoreRadio,
    examScoreCheck: this.examDetails.examScoreCheck,
    examScoreJudge: this.examDetails.examScoreJudge,
    examScoreFill: this.examDetails.examScoreFill,
    examScoreCode: this.examDetails.examScoreCode,
    examStartDate: startDateGMT8,
    examEndDate: endDateGMT8,
    password: this.examDetails.password,
  };
console.log("提交的考试开始时间：", this.examDetails.startDate);
  // 调用API提交考试信息
  if (this.examId) {
    api.admin_updateExam(examData)
    
      .then(response => {
        
        console.log('考试信息更新成功:', response);
        alert('更新考试成功！');
        this.$router.push('/admin/questions');
        this.showExamCreation = false;
      })
      .catch(error => {
        console.error('更新考试信息失败:', error);
        alert('更新考试信息失败，请稍后重试！');
      });
  } else {
    api.admin_createExam(examData)
      .then(response => {
        console.log('考试信息提交成功:', response);
        alert('创建考试成功！');
        this.$router.push('/admin/questions');
        this.showExamCreation = false;
      })
      .catch(error => {
        console.error('提交考试信息失败:', error);
        alert('提交考试信息失败，请稍后重试！');
      });
  }
}

  }
};
</script>

<style scoped>
.upload-tip {
  color: red;
  text-align: left;
}
</style>
