<template>
    <div class="exam-list">
      <el-card v-for="examItem in examList" :key="examItem.exam.examId" class="exam-card" :body-style="{ padding: '20px' }">
        <div slot="header" class="exam-header">
          <span class="exam-name">{{ examItem.exam.examName }}</span>
          <span class="exam-time-limit">限时：{{ examItem.exam.examTimeLimit }} 分钟</span>
        </div>
        <div class="exam-description">
          {{ examItem.exam.examDescription }}
        </div>
        <div class="exam-details">
          <p><b>考试ID：</b>{{ examItem.examId }}</p>
          <p><b>满分：</b>{{ examItem.exam.examScore }} 分</p>
          <p><b>单选题分值：</b>{{ examItem.exam.examScoreRadio }}</p>
          <p><b>多选题分值：</b>{{ examItem.exam.examScoreCheck }}</p>
          <p><b>判断题分值：</b>{{ examItem.exam.examScoreJudge }}</p>
          <p><b>考试开始时间：</b>{{ examItem.exam.examStartDate }}</p>
          <p><b>考试结束时间：</b>{{ examItem.exam.examEndDate }}</p>
          <p><b>创建时间：</b>{{ examItem.exam.createTime }}</p>
          <p><b>更新时间：</b>{{ examItem.exam.updateTime }}</p>
        </div>
        <div class="exam-record" v-if="examItem.examRecord">
          <el-tag type="success">您已参加过此考试</el-tag>
          <p><b>参加记录ID：</b>{{ examItem.examRecord.examRecordId }}</p>
          <p><b>参加者ID：</b>{{ examItem.examRecord.examJoinerId }}</p>
          <p><b>参加时间：</b>{{ examItem.examRecord.examJoinDate }}</p>
          <p><b>答题耗时：</b>{{ examItem.examRecord.examTimeCost }} 分钟</p>
          <p><b>获得分数：</b>{{ examItem.examRecord.examJoinScore }} 分</p>
          <p><b>答题选项ID：</b>{{ examItem.examRecord.answerOptionIds }}</p>
        </div>
        <el-button type="primary" @click="joinExam(examItem.exam.examId)">开始考试</el-button>
      </el-card>
    </div>
  </template>
  
  <script>
  import api from '@/common/api';
  
  export default {
    name: 'ExamList',
    data() {
      return {
        examList: [] // 存放从后端获取的考试列表数据
      };
    },
    methods: {
      getExamList() {
        api.getExamList() // 替换成你实际的 API 端点
          .then(response => {
            this.examList = response.data.data; // 将 API 返回的考试列表数据存储到 examList 中
            console.log('66666', this.examList.examId);
          })
          .catch(error => {
            console.error('获取考试列表失败', error);
          });
      },
      joinExam(examId) {
        // 根据路由跳转至对应的考试页面
        this.$router.push({ path: `/exam/${examId}` });
      }
    },
    mounted() {
      this.getExamList(); // 组件挂载时获取考试列表数据
    }
  };
  </script>
  
  <style scoped>
  .exam-list {
    display: flex;
    flex-wrap: wrap;
    justify-content: space-around;
  }
  
  .exam-card {
    width: 400px;
    margin-bottom: 20px;
  }
  
  .exam-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  
  .exam-name {
    font-size: 18px;
    font-weight: bold;
  }
  
  .exam-time-limit {
    font-size: 14px;
    color: #999;
  }
  
  .exam-description {
    margin: 10px 0;
  }
  
  .exam-details p {
    margin-bottom: 5px;
  }
  </style>
  