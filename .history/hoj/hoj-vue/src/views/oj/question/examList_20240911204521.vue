<template>
    <div>
      <el-table :data="examList" style="width: 100%" @row-click="handleRowClick">
        <el-table-column prop="exam.examId" :label="$t('m.Exam_id')"></el-table-column>
        <el-table-column prop="exam.examName" :label="$t('m.Exam_name')"></el-table-column>
        <el-table-column prop="exam.examDescription" :label="$t('m.Exam_description')"></el-table-column>
        <el-table-column prop="exam.examStartDate" :label="$t('m.Exam_start_time')"></el-table-column>
        <el-table-column prop="exam.examEndDate" :label="$t('m.Exam_end_time')"></el-table-column>
        <el-table-column prop="isJoined" :label="$t('m.Is_Join')">
          <template slot-scope="scope">
            <span v-if="scope.row.isJoined" class="joined">√</span>
          <span v-else class="not-joined">×</span>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </template>
  
  <script>
  import api from '@/common/api';
  
  export default {
    data() {
      return {
        examList: []
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
            console.error('Error fetching exam list', error);
          });
      },
      handleRowClick(row) {
      const examId = row.exam.examId;
      if (row.isJoined) {
        // 如果参加过考试，跳转到试卷的记录路由
        this.$router.push({ name: 'examRecord', params: { examId } });
      } else {
        // 如果没有参加过考试，获取上面的考试详情
        this.$router.push({ name: 'examDetail', params: { examId } });
      }
    }
    }
  };
  </script>
  
  <style>
.joined {
  color: rgb(11, 0, 128);
}

.not-joined {
  color: rgb(255, 136, 0);
}
  </style>