<template>  
    <div>  
      <el-form :model="form" label-width="120px">  
        <el-form-item :label="$t('m.Opt_questionId')">  
          <span>{{ questionId }}</span>  
        </el-form-item>  
        <el-form-item :label="$t('m.Opt_questionContent')">  
          <span>{{ question.text }}</span>  
        </el-form-item>  
    
      <el-form-item :label="$t('m.Opt_options') " v-if="(questionType == '1'||questionType === '3') && question.options.length">
        <el-radio-group v-model="form.selectedAnswer" class="vertical-radio-group">
          <el-radio
            v-for="option in question.options"
            :key="option.id"
            :label="option.optionContent"
            @change="updateSelectedOptionContent(option.optionContent)"
          >
            {{ option.optionContent }}
          </el-radio>
        </el-radio-group>
      </el-form-item>

      <el-form-item :label="$t('m.Opt_options')" v-if="questionType === '2' && question.options.length">
        <el-checkbox-group v-model="form.selectedAnswers">
          <el-checkbox
            v-for="option in question.options"
            :key="option.id"
            :label="option.optionContent"
            @change="updateSelectedOptionContents(option.optionContent)"
          >
            {{ option.optionContent }}
          </el-checkbox>
        </el-checkbox-group>
      </el-form-item>


      <el-form-item v-if="questionType === '4'">
        <el-input
          v-model="form.selectedAnswer"
          :placeholder=" $t('m.Please_enter_your_answer')"
        ></el-input>
      </el-form-item>

     
      <el-form-item>
        <el-button
          type="primary"
          @click="submitForm"
          :disabled="questionType !== '4' && !question.options.length"
        >{{ $t('m.Submit_Answer')}}</el-button>
      </el-form-item> 
      </el-form> 
      
      <el-dialog  
        :title="$t('m.Submitted_successfully')"  
        :visible.sync="dialogVisible"  
        width="30%"  
        @close="dialogVisible = false"  
        >  
        <p>{{ $t('m.Submitted_successfully')}}</p>  
        <span slot="footer" class="dialog-footer">  
        <el-button @click="dialogVisible = false">{{ $t('m.Close')}}</el-button>  
        <el-button @click="showResultDialog">{{ $t('m.view_result')}}</el-button>
        </span>  
      </el-dialog>

      <el-dialog  
      :title="$t('m.Result')"  
      :visible.sync="resultVisible"  
      width="30%"  
      @close="resultVisible = false"  
      >  
        <p>{{ judgeResult }}</p>

        <p v-if="(questionType !== '4')">{{ $t('m.User_Answer')}}：{{ selectedOptionContent }}</p>
        <p v-if="questionType === '4'">{{ $t('m.User_Answer')}}：{{ form.selectedAnswer }}</p>
        <p>{{ $t('m.Right_answer')}}：{{ rightAnswer }}</p>
        <span slot="footer" class="dialog-footer">  
          <el-button @click="resultVisible = false">{{ $t('m.Close')}}</el-button>  
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
          selectedAnswers: [] // 多选题的输入   
        },  
        question: {  
          text: '',   
          options: [], 
        },  
        questionId: null, 
        questionType: null,
        selectedOptionContent: null, // 这里保存选中的选项内容  
        selectedOptionId: null ,// 这里将用于保存选中的选项ID
        judgeResult: '',
        rightAnswer: '',
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
            this.questionType = response.data.data.questionType.toString();  
            return this.fetchQuestiontext();
          })  
          .then(response => {
            if (this.questionType !== '4') {
        return this.fetchQuestionOptions(); 
      }
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
      updateSelectedOptionContents(optionContent) {
      const index = this.form.selectedAnswers.indexOf(optionContent);
      if (index === -1) {
      // 如果选项不在数组中，添加它
      this.form.selectedAnswers.push(optionContent);
      } else {
     // 如果选项已经在数组中，移除它
      this.form.selectedAnswers.splice(index, 1);
      }
      },
      updateSelectedOptionContent(optionContent) {  
          this.selectedOptionContent = optionContent;  
        },


      submitForm() {  
        let submitResult = '';

        if (this.questionType === '4') {
        // 填空题，提交的是输入框的内容
        submitResult = this.form.selectedAnswer;
    } else if (this.questionType === '2') {
        // 多选题，按原始选项顺序提交
        submitResult = this.question.options
            .filter(option => this.form.selectedAnswers.includes(option.optionContent))
            .map(option => option.optionContent)
            .join(',');
    } else {
        // 选择题，提交的是选项的内容
        submitResult = this.selectedOptionContent;
    }


        const params = {
          questionId: this.questionId,
          questionType: this.questionType,
          submitResult: submitResult
        };    
        api.addSubmissionOptions(params)  
         .then(response => {  
            api.judgeOpt(params.questionId)  
            .then(judgeResponse => {  
            api.getFeedback(params.questionId)
            .then(getFeedbackResponse => {  
              this.selectedOptionContent = judgeResponse.data.data.userAnswer;
              this.judgeResult = getFeedbackResponse.data.data.judgeResult;
              this.rightAnswer = getFeedbackResponse.data.data.rightAnswer;
             this.dialogVisible = true; // 显示外层弹框  
            }) 
            .catch(feedError => {  
              console.error('Error during feedback', feedError);  
            }); 
            }) 
            .catch(judgeError => {  
            console.error('Error during judging', judgeError);  
            }); 
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