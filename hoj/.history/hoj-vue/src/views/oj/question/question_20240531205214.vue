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
            <el-radio v-for="option in question.options" :key="option.id" :label="option"  @change="updateSelectedOptionContent(option.optionContent)" >  
              {{ option.optionContent }}  
            </el-radio>  
          </el-radio-group>  
        </el-form-item>  
     
        <el-form-item>  
          <el-button type="primary" @click="submitForm" :disabled="!question.options.length">提交</el-button>  
        </el-form-item>  
      </el-form> 
      
      <el-dialog  
        title="提交成功"  
        :visible.sync="dialogVisible"  
        width="30%"  
        @close="dialogVisible = false"  
        >  
        <p>成功提交！</p>  
        <span slot="footer" class="dialog-footer">  
        <el-button @click="dialogVisible = false">关闭</el-button>  
        </span>  
      </el-dialog>
    </div>  
  </template>  
    
  <script>  
  import api from '@/common/api';
  export default {  
    data() {  
      return {  
        dialogVisible: false, // 控制 Dialog 的显示与隐藏 
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
        selectedOptionId: null ,// 这里将用于保存选中的选项ID 
        submissionStatus: '',
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
      updateSelectedOptionContent(optionContent) {  
          this.selectedOptionContent = optionContent;  
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
      console.log('questionId:', this.questionId);  
      console.log('questionType:', this.questionType);  
      console.log('selectedOptionContent:', this.selectedOptionContent);
      console.log('submitting data:', params);  
      api.addSubmissionOptions(params)  
         .then(response => {  
        this.dialogVisible = true; // 显示 Dialog  
        console.log('Form submitted successfully', response.data);  
       })  
         .catch(error => {  
        console.error('Error submitting form', error);  
       });  
      },

      
    },


  };  
  </script>



<style scoped>  
.vertical-radio-group .el-radio {  
  display: block;  
  margin-bottom: 10px;  
}  
</style>