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
        <el-button @click="showResultDialog">查看结果</el-button>
        </span>  
      </el-dialog>

      <el-dialog  
      title="判题结果"  
      :visible.sync="resultVisible"  
      width="50%"  
      @close="resultVisible = false"  
      >  
        <p>{{ judgeResult }}</p> <!-- 假设你有一个judgeResult属性来存储判题结果 -->  
        <span slot="footer" class="dialog-footer">  
          <el-button @click="resultVisible = false">关闭</el-button>  
       </span>  
      </el-dialog>  


    </div>  
  </template>  
    
  <script>  
  import api from '@/common/api';
  export default {  
    data() {  
      return {  
        dialogVisible: false, // 控制 提交弹框 的显示与隐藏 
        resultVisible: false, //控制 反馈弹框 的显示与隐藏 
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
        judgeResult: '',//判题结果
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
      api.addSubmissionOptions(params)  
         .then(response => {  
            api.judgeOpt(params.questionId)  
            .then(judgeResponse => {  
            console.log('判题完成6666');  

            }) 
            .catch(judgeError => {  
            console.error('Error during judging', judgeError);  
            }); 

           api.getFeedback(params.questionId)
            .then(getFeedbackResponse => {  
              console.log('反馈666');  
              this.judgeResult = getFeedbackResponse.data.data.judgeResult;
              console.log('judgeresult:', getFeedbackResponse.data.data.user_answer);
              console.log('judgeresult:', this.judgeResult);
            }) 
            .catch(feedError => {  
              console.error('Error during feedback', feedError);  
            }); 
          this.dialogVisible = true; // 显示 Dialog  
          console.log('Form submitted successfully', response.data);  
       })  
         .catch(error => {  
        console.error('Error submitting form', error);  
       });  
      },
      showResultDialog() {  
      this.resultVisible = true; // 显示内层弹框  
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