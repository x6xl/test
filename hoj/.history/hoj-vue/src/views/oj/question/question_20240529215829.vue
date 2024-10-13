<template>  
    <div>  
      <el-form :model="form" label-width="120px">  
        <el-form-item label="题目">  
          <span>{{ question.text }}</span>  
        </el-form-item>  
    
         
        <el-form-item label="选项" v-if="question.options.length">  
          <el-radio-group v-model="form.selectedAnswer">  
            <el-radio v-for="(option, index) in question.options" :key="index" :label="option.value">  
              {{ option.text }}  
            </el-radio>  
          </el-radio-group>  
        </el-form-item>  
     
        <el-form-item>  
          <el-button type="primary" @click="submitForm" :disabled="!question.options.length">提交</el-button>  
        </el-form-item>  
      </el-form>  
    </div>  
  </template>  
    
  <script>  
  import api from '@/common/api';
  export default {  
    data() {  
      return {  
        form: {  
          selectedAnswer: '',   
        },  
        question: {  
          text: '',   
          options: [], 
        },  
        questionId, // 假设你已经有了questionId  
      };  
    },  
    created() {  
      this.fetchQuestiontext();
      this.fetchQuestionOptions();  
      this.fetchQuestionId();
    },  
    methods: {  
        fetchQuestionId() {  
        api.getOptions(this.$route.params.id)  
          .then(response => {  
            this.questionId = response.data.data;  
          })  
          .catch(error => {  
            console.error('Error fetching question options:', error);  
          });  
      },
        
      fetchQuestionOptions() {  
        api.getOptions(this.questionId)  
          .then(response => {  
            this.question.options = response.data.data.questionId;  
          })  
          .catch(error => {  
            console.error('Error fetching question options:', error);  
          });  
      },  
        fetchQuestiontext() {  
        api.getOptions(this.$route.params.id,)  
          .then(response => {  
            this.question.text = response.data.data.questionContent;  
          })  
          .catch(error => {  
            console.error('Error fetching question options:', error);  
          });  
      },  

      
      submitForm() {  
        // 提交表单逻辑，可以打印选中的答案  
        console.log('选中的答案是:', this.form.selectedAnswer);  
        // 这里可以添加发送到服务器的逻辑  
      },  
    },  
  };  
  </script>