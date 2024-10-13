<template>
    <div>
      <el-table :data="examList" style="width: 100%" @row-click="handleRowClick">
        <el-table-column prop="exam.examId" :label="$t('m.Exam_id')" width="150"></el-table-column>
        <el-table-column prop="exam.examName" :label="$t('m.Exam_name')"></el-table-column>
        <el-table-column prop="exam.examDescription" :label="$t('m.Exam_description')"></el-table-column>
        <el-table-column prop="exam.examStartDate" :label="$t('m.Exam_start_time')"></el-table-column>
        <el-table-column prop="exam.examEndDate" :label="$t('m.Exam_end_time')"></el-table-column>
        <el-table-column :label="$t('m.Operate')">
        <template slot-scope="scope">
          <el-button-group>
  <el-tooltip :content="$t('m.add_exam_code_question')" placement="top">
    <el-button type="primary" icon="el-icon-plus" @click.stop="handleAddCodeQuestion(scope.row)" size="mini"></el-button>
  </el-tooltip>
  <el-tooltip :content="$t('m.admin_Exam_Update')" placement="top">
    <el-button type="primary" icon="el-icon-edit" @click.stop="handleEdit(scope.row)" size="mini"></el-button>
  </el-tooltip>
  <el-tooltip :content="$t('m.Delete_The_Exam')" placement="top">
    <el-button type="danger" icon="el-icon-delete" @click.stop="handleDelete(scope.row)" size="mini"></el-button>
  </el-tooltip>
  <el-tooltip :content="$t('m.View_user_submission_records')" placement="top">
    <el-button type="info" icon="el-icon-view" @click.stop="viewExamRecords(scope.row)" size="mini"></el-button>
  </el-tooltip>
  <el-tooltip :content="$t('m.Get_Exam_process_ShotImage')" placement="top">
    <el-button type="success" icon="el-icon-picture" @click.stop="getAndShowExamImageShots(scope.row.exam.examId)" size="mini"></el-button>
  </el-tooltip>
</el-button-group>

           
        </template>
      </el-table-column>
      </el-table>
  
      <el-dialog
        :visible.sync="dialogVisible"
        title="考试详情"
        width="50%">
        <div v-if="selectedExam">
          <p>{{ $t('m.exam_name')}}: {{ selectedExam.exam.examName }}</p>
          <p>{{ $t('m.exam_description')}}: {{ selectedExam.exam.examDescription }}</p>
          <p>{{ $t('m.Exam_start_time')}}: {{ selectedExam.exam.examStartDate }}</p>
          <p>{{ $t('m.Exam_end_time')}}: {{ selectedExam.exam.examEndDate }}</p>
          <p>{{ $t('m.Exam_Total_Score')}}: {{ selectedExam.exam.examScore }}</p>
          <p>{{ $t('m.Exam_score_radio')}}: {{ selectedExam.exam.examScoreRadio }}</p>
          <p>{{ $t('m.Exam_score_check')}}: {{ selectedExam.exam.examScoreCheck }}</p>
          <p>{{ $t('m.Exam_score_judge')}}: {{ selectedExam.exam.examScoreJudge }}</p>
          <p>{{ $t('m.Exam_score_fill')}}: {{ selectedExam.exam.examScoreFill }}</p>
          <p>{{ $t('m.Exam_score_code')}}: {{ selectedExam.exam.examScoreCode }}</p>
          <p>{{ $t('m.Exam_Create_Time')}}: {{ selectedExam.exam.createTime }}</p>
          <p>{{ $t('m.Exam_Update_Time')}}: {{ selectedExam.exam.updateTime }}</p>
          <p>{{ $t('m.Exam_password')}}: {{ selectedExam.password }}</p>
          <h2>{{ $t('m.question_List')}}:</h2>
          
          <ul v-for="(question, index) in selectedExam.questions" :key="index">
            <hr></hr>
            <li>
              <p>{{ $t('m.Opt_questionContent')}}: {{ question.contents[0].questionContent }}</p>
              <p>{{ $t('m.Opt_questionType')}}:
              <span v-if="question.contents[0].questionType.toString() === '1'">{{ $t('m.a_choice_questions')}}</span>
              <span v-else-if="question.contents[0].questionType.toString() === '2'">{{ $t('m.Multiple_choice_questions')}}</span>
              <span v-else-if="question.contents[0].questionType.toString() === '3'">{{ $t('m.Judge_questions')}}</span>
              <span v-else-if="question.contents[0].questionType.toString() === '4'">{{ $t('m.Fill_questions')}}</span>
            </p>
            <p v-if="question.contents[0].questionType.toString() !== '4'">{{ $t('m.Opt_options')}}:</p>
            <ul v-if="question.contents[0].questionType.toString() !== '4'">
              <li v-for="(option, idx) in question.options" :key="idx" style="list-style-type: none;">
                {{ String.fromCharCode(65 + idx) }}. {{ option.optionContent }}
              </li>
            </ul>
            <p>{{ $t('m.Right_answer')}}: {{ question.contents[0].rightAnswer }}</p>
            </li>
            
          </ul>

        
        </div>
        <span slot="footer" class="dialog-footer">
          <el-button @click="dialogVisible = false">{{ $t('m.Close')}}</el-button>
        </span>
      </el-dialog>

      <el-dialog
        :visible.sync="dialogVisible1"
        :title="$t('m.Exam_submission_records')"
        width="50%">
        <div v-if="examRecords">
          <el-table v-if="examRecords" :data="examRecords" style="width: 100%">
          <el-table-column prop="examId" :label="$t('m.Exam_id')"></el-table-column>
          <el-table-column prop="examJoinerId" :label="$t('m.Joiner_Id')"></el-table-column>
          <el-table-column prop="examJoinDate" :label="$t('m.Join_time')"></el-table-column>
          <el-table-column prop="examTimeCost" :label="$t('m.Exam_cost_Time')"></el-table-column>
          <el-table-column prop="examJoinScore" :label="$t('m.Get_score')"></el-table-column>
          <el-table-column prop="answerOptionIds" :label="$t('m.Answer_option')"></el-table-column>
        </el-table>
        <el-button type="success" icon="el-icon-download" @click.stop="generateExcelForRecords">{{ $t('m.Export_Excel')}}</el-button>
        </div>
        <span slot="footer" class="dialog-footer">
          <el-button @click="dialogVisible1 = false">{{ $t('m.Close')}}</el-button>
        </span>
      </el-dialog>
      
      <!-- 独立的图片弹窗 -->
      <el-dialog
      :visible.sync="examImageDialogVisible"
      :title="$t('m.Exam_process_ShotImage')"
      width="50%">
      <div v-for="(examImageShots, examJoinerId) in examImages" :key="examJoinerId">
        <h3>{{ $t('m.user_id')}}: {{ examJoinerId }}</h3>
        <div v-if="examImageShots && examImageShots.length > 0">
          <div v-for="(shot, idx) in examImageShots" :key="idx">
            <img :src="shot.url" style="max-width: 100%; margin-bottom: 10px;">
          </div>
        </div>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="examImageDialogVisible = false">{{ $t('m.Close')}}</el-button>
      </span>
    </el-dialog>

    </div>
  </template>
  
  <script>
  import api from '@/common/api';
  import utils from '@/common/utils';


  export default {
    data() {
      return {
        examList: [],
        dialogVisible: false,
        dialogVisible1: false,
        selectedExam: null,
        examRecords:null,
        SelectExamId:null,
        examImages: {}, // 改为使用对象存放考试截图数据，以 examJoinerId 为键
        examImageDialogVisible: false // 控制考试截图弹窗的显示对象
      };
    },
    mounted() {
      this.fetchExamList();
    },
    methods: {
      fetchExamList() {
        api.getExamList()
          .then(response => {
            this.examList = response.data.data;
          })
          .catch(error => {
            console.error('获取考试列表出错', error);
          });
      },
      handleRowClick(row) {
        const examId = row.exam.examId;
        this.fetchExamDetails(examId);
      },
      handleAddCodeQuestion(row) {
        const examId = row.exam.examId;
        this.$router.push({ name: 'admin-exam-add-problem', params: { examId: examId } });
      },
      fetchExamDetails(examId) {
        api.admin_getExam(examId)
          .then(response => {
            const examDetails = response.data.data.exam;
            const questions = response.data.data.questionVOs;
            const password = response.data.data.password;
            this.selectedExam = {
              exam: examDetails,
              questions: questions,
              password: password
            };
  
            this.dialogVisible = true; // 打开弹窗显示详细信息
          })
          .catch(error => {
            console.error('获取考试详情出错', error);
          });
      },
      handleDelete(row) {
      const examId = row.exam.examId;
      api.admin_examDelete(examId)
        .then(response => {
          // 删除成功后，重新获取考试列表
          this.fetchExamList();
          this.dialogVisible = false; // 关闭弹窗
        })
        .catch(error => {
          console.error('删除考试失败', error);
        });
       },
       handleEdit(row){
        const examId = row.exam.examId;
        this.$router.push({ name: 'admin-examUpdate', params: { examId: examId } });
       },
      viewExamRecords(row) {
      const examId = row.exam.examId;
      this.SelectExamId=examId;
      api.getExamRecordList(examId)
        .then(response => {
          this.examRecords = response.data.data; // 假设API返回的数据结构包含考试记录列表
          this.dialogVisible1 = true; // 打开弹窗显示考试记录列表
        })
        .catch(error => {
          console.error('获取考试记录列表出错', error);
        });
    },

    
    generateExcelForRecords() {
  // 此方法用于生成当前展示的考试提交记录的Excel文件
  if (this.examRecords) {
    let url = '/api/admin/question/get-examRecord-excel?exam_id=' +this.SelectExamId;
            utils.downloadFile(url).then(() => {
              this.$alert("导出成功");
            });
  }
},
    // 获取并显示考试截图
    getAndShowExamImageShots(examId) {
      api.getExamAllImageShot(examId)
        .then(response => {
          const examImageShots = response.data.data;
          if (examImageShots) {
            if(Object.keys(examImageShots).length === 0){
              this.$message.error( $t('m.Screenshots_of_the_exam_were_not_found'));
              return;
            }
            Object.keys(examImageShots).forEach(examJoinerId => {
              if (examImageShots[examJoinerId].length > 0) {
                if (!this.examImages.hasOwnProperty(examJoinerId)) {
                  this.$set(this.examImages, examJoinerId, examImageShots[examJoinerId]);
                } else {
                  this.examImages[examJoinerId] = examImageShots[examJoinerId];
                }
                this.examImageDialogVisible = true; // 打开对应的弹窗
              } else {
                this.$message.error('未找到用户的考试截图');
              }
            });
          } else {
            this.$message.error($t('m.Screenshots_of_the_exam_were_not_found'));
          }
        })
        .catch(error => {
          console.error('获取考试截图失败', error);
          this.$message.error($t('m.Screenshots_of_the_exam_were_not_found'));
        });
    },


    }
  };
  </script>  