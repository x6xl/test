<template>
    <div>
      <el-table :data="examList" style="width: 100%" @row-click="handleRowClick">
        <el-table-column prop="exam.examId" label="考试id"></el-table-column>
        <el-table-column prop="exam.examName" label="名字"></el-table-column>
        <el-table-column prop="exam.examDescription" label="描述"></el-table-column>
        <el-table-column prop="exam.examStartDate" label="开始时间"></el-table-column>
        <el-table-column prop="exam.examEndDate" label="结束时间"></el-table-column>
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
      
        this.$router.push({ name: 'examDetail', params: { examId } });
    }
    }
  };
  </script>
  
  <style>

  </style>