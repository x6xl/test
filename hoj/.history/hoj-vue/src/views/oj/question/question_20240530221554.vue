<template>  
    <div>  
      <el-form :model="form" label-width="120px">  
        <el-form-item label="题号">  
          <span>{{ questionId }}</span>  
        </el-form-item>  
        <el-form-item label="题目">  
          <span>{{ question.text }}</span>  
        </el-form-item>  
    
         
        <el-form-item label="选项" v-if="question.options.length">  
          <el-radio-group v-model="form.selectedAnswer" class="vertical-radio-group" >  
            <el-radio v-for="option in question.options" :key="option.id" :label="option">  
              {{ option.optionContent }}  
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
        questionId: null, 
        questionType: null,
        selectedOptionContent: null, // 这里保存选中的选项内容  
        selectedOptionId: null // 这里将用于保存选中的选项ID 
      };  
    },  
    created() { 
      this.fetchQuestionId(); 
    },  
    methods: {  
        fetchQuestionId() {  
        api.getQuestion(this.$route.params.id)  
          .then(response => {  
            this.questionId = response.data.data.questionId;  
            this.questionType = response.data.data.questionType; 
            return this.fetchQuestiontext();
          })  
          .then(response => {  
            return this.fetchQuestionOptions(); 
          })  
          .catch(error => {  
            console.error('Error fetching question options:', error);  
          });  
        },
         
        fetchQuestiontext() {  
        api.getQuestion(this.$route.params.id)  
          .then(response => {  
            this.question.text = response.data.data.questionContent;  
          })  
          .catch(error => {  
            console.error('Error fetching question options:', error);  
          });  
      },  

      fetchQuestionOptions() {  
        api.getOptions(this.questionId)  
          .then(response => {  
            this.question.options = response.data.data;  
          })  
          .catch(error => {  
            console.error('Error fetching question options:', error);  
          });  
      },
      submitForm() {  
      const questionId = this.questionId;  
      const userId = 'linbei';  
      const questionType = this.questionType;  
      const submitResult = this.selectedOptionContent;
      const params = {  
        questionId: questionId,  
        userId: userId,  
        questionType: questionType,  
        submitResult: submitResult  
      };  
    api.addSubminssionOptions(params) 
        .then(response => {  
            
            console.log('Form submitted successfully', response.data);  
        })  
        .catch(error => {  
              
            console.error('Error submitting form', error);  
        });  
      },
    },
    watch: {  
    selectedOptionContent(newVal) {  
      const option = this.question.options.find(opt => opt.optionContent === newVal);  
      if (option) {  
        this.selectedOptionContent = option.optionContent;  
      } else {  
        this.selectedOptionContent = null;  
      }  
    }  
   }  


  };  
  </script>



<style scoped>  
.vertical-radio-group .el-radio {  
  display: block;  
  margin-bottom: 10px;  
}  
</style>