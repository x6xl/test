<template>
    <div>
      <h2>{{ $t('m.Exam_name')}}：{{ examName1 }}</h2>
      <p>{{ $t('m.Exam_description')}}：{{ examDescription1 }}</p>
      <p>{{ $t('m.Exam_Time')}}：{{ examStartDate1 }} 至 {{ examEndDate1 }}</p>
      <p>{{ $t('m.Get_score')}}: {{ examJoinScore }}</p>

      <el-form ref="examForm" :model="examForm" label-width="80px">
        <el-row v-for="(question, index) in exam.selectQuestionVOs" :key="question.questionId">
          <el-col :span="24">
            <h3>{{ $t('m.The_Question_Number1')}}{{(index + 1) }}{{ $t('m.The_Question_Number2')}}：</h3>
            <p>{{ question.questionContentVOs[0].questionContent }}</p>
  
            <div v-for="(option, optIndex) in question.options" :key="option.id">
              {{ String.fromCharCode(65 + optIndex) }}. {{ option.optionContent }}
            </div>
  
            <template v-if="answersMap && resultsMap && answersRightMap">
              <div>
                <p>{{ $t('m.Right_answer')}}: {{ answersRightMap[question.questionId].join(', ') }}</p>
                <p>{{ $t('m.User_Answer')}}: {{ answersMap[question.questionId] ? answersMap[question.questionId].join(', ') : $t('m.Not_selected') }}</p>
                <p :style="{ color: getTextColor(resultsMap[question.questionId]) }">{{ $t('m.Result')}}: {{ resultsMap[question.questionId] === 'True' ? $t('m.right') : $t('m.Error') }}</p>
              </div>
            </template>
  
          </el-col>
        </el-row>
      </el-form>
      <div style="margin-top: 20px;">
      <h3>{{ $t('m.Code_questions')}}:</h3>
      <el-row>
        <el-col v-for="(id, index) in problemIds" :key="index" :span="24">
          <el-button
            type="text"
            @click="navigateToProblem(id)"
            style="display: block; margin-bottom: 10px;"
          >
            {{ index + 1 }}. {{ id }}
          </el-button>
        </el-col>
      </el-row>
    </div>

    </div>
  </template>
  
  <script>
  import api from '@/common/api';
  
  export default {
    data() {
      return {
        exam: {},
        examJoinScore: null,
        examForm: {}, // Form data for storing selected options
        answersMap: {}, // 用户答案映射
        resultsMap: {}, // 每题结果映射
        answersRightMap: {}, // 正确答案映射
        examEndDate1: '',
        examStartDate1: '',
        examDescription1: '',
        examName1: '',
        problemIds: [],
      };
    },
    mounted() {
      this.fetchExamRecord();
    },
    methods: {
      fetchExamRecord() {
        const examId = this.$route.params.examId;
        api.getExamRecord(examId)
          .then(response => {
            this.examJoinScore = response.data.data.examRecord.examJoinScore;
            this.exam = response.data.data.examVO; // 直接从考试记录中获取考试信息
            this.examEndDate1=this.exam.exam.examEndDate;
            this.examStartDate1=this.exam.exam.examStartDate;
            this.examDescription1=this.exam.exam.examDescription;
            this.examName1=this.exam.exam.examName;
            this.answersMap = response.data.data.answersMap;
            this.resultsMap = response.data.data.resultsMap;
            this.answersRightMap = response.data.data.answersRightMap;
            this.problemIds = this.exam.problemId;
            console.log(this.problemIds);
            // 初始化 examForm
            this.exam.selectQuestionVOs.forEach(question => {
              this.$set(this.examForm, question.questionId, '');
            });
          })
          .catch(error => {
            console.error('获取考试记录时出错', error);
            this.$message.error(this.$t('m.failed_to_get_exam_record'));
          });
      },
  
      getTextColor(result) {
        return result === 'True' ? 'green' : 'red';
      },

      navigateToProblem(problemId){
        this.$router.push({
        name: 'ProblemDetails',
        params: {
          problemID: problemId,
        },
      });
      }


    }
  };
  </script>
  
  <style scoped>
  /* Add any custom styles here */
  </style>
  