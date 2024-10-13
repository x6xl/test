<template>  
  <div>  
    <el-form :model="questionForm" ref="questionForm" label-width="120px">  
        <el-form-item :label="$t('m.Opt_id')">  
        <el-input v-model="questionForm.questionId"></el-input>  
      </el-form-item>  
      <el-form-item :label="$t('m.Opt_author')">  
        <el-input v-model="questionForm.author"></el-input>  
      </el-form-item>  
      <el-form-item :label="$t('m.Opt_authorId')">  
        <el-input v-model="questionForm.authorId"></el-input>  
      </el-form-item>  
      <el-form-item :label="$t('m.Opt_questionContent')">  
        <el-input v-model="questionForm.questionContent"></el-input>  
      </el-form-item>  
      <el-form-item :label="$t('m.Right_answer')">  
        <el-input v-model="questionForm.rightAnswer"></el-input>  
      </el-form-item>  
      <el-form-item :label="$t('m.Opt_questionType')">  
        <el-radio-group v-model="questionForm.questionType">  
          <el-radio label="1">{{ $t('m.a_choice_questions')}}</el-radio>  
          <el-radio label="2">{{ $t('m.Multiple_choice_questions')}}</el-radio>  
          <el-radio label="3">{{ $t('m.Judge_questions')}}</el-radio>  
          <el-radio label="4">{{ $t('m.Fill_questions')}}</el-radio> 
        </el-radio-group>  
      </el-form-item>  
      <el-form-item :label="$t('m.Opt_score')">  
        <el-input-number v-model="questionForm.questionScore" :min="1"></el-input-number>  
      </el-form-item>  
      <el-form-item :label="$t('m.Opt_options')" v-if="questionForm.questionType !== '4'">  
        <div v-for="(option, index) in questionForm.options" :key="index">  
          <el-input v-model="option.optionContent" :placeholder="$t('m.Please_enter_the_options_content')"></el-input>  
        </div>  
        <el-button type="primary" @click="addOption">{{ $t('m.Add_question_option')}}</el-button>  
      </el-form-item>  
      <el-form-item>  
        <el-button type="primary" @click="submitForm('questionForm')">{{ $t('m.confirm')}}</el-button>  
        <el-button @click="resetForm('questionForm')">{{ $t('m.Reset')}}</el-button>  
      </el-form-item>  
    </el-form> 
    <el-dialog  
    :title="$t('m.prompt')"  
    :visible.sync="dialogVisible"  
    width="30%"  
    @close="dialogVisible = false">  
    <span>{{ $t('m.create_successfully')}}</span>  
    <span slot="footer" class="dialog-footer">  
      <el-button @click="dialogVisible = false,this.$router.push('/admin/question')">确 定</el-button>  
    </span>  
  </el-dialog>   
  </div>  
</template>  
  
<script>  
  import api from '@/common/api';
export default {  
  data() {  
    return {  
      dialogVisible: false,
      questionForm: {  
        questionId: '',
        author:'',
        authorId:'',
        questionContent: '',  
        questionType: '',  
        questionScore: '',
        rightAnswer:'',  
        options: [  
          { id:'',
            optionContent: '' ,
          questionId: '',
        author:'',
        authorId:'',
        },  
        ],  
      },  
    };  
  },   
  created() {  
  const questionId = this.$route.params.questionId;  
  if (questionId) {  
    this.fetchQuestion(questionId);  
  }  
  },
  methods: {  
    /* fetchQuestionId() {  
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
        }, */
    fetchQuestion() {  
    // 假设你有一个方法来获取特定的问题详情  
    // 这里只是一个示例，你需要替换为实际的API调用  
    api.admin_getQuestion(this.$route.params.questionId)  
      .then((response) => {  
        // 假设API响应的数据结构与你的questionForm类似  
        this.questionForm.questionId=response.data.data.questionId;
        this.questionForm.author=response.data.data.author;
        this.questionForm.authorId=response.data.data.authorId;
        this.questionForm.questionContent=response.data.data.questionContent;
        this.questionForm.questionType=response.data.data.questionType.toString();
        this.questionForm.questionScore=response.data.data.questionScore;
        this.questionForm.rightAnswer=response.data.data.rightAnswer;
        if ( response.data.data.questionType.toString() !== '4') {
            return this.getQuestionOptions();
          }
        
      })  
      .catch((error) => {  
        console.error('获取问题详情失败', error);  
        this.$message.error(this.$t('m.failed_to_get_details_of_the_question'));
      });  
    },  


    getQuestionOptions() {  
    api.admin_getOptions(this.questionForm.questionId)  
      .then((optionsResponse) => {  
        this.questionForm.options = optionsResponse.data.data.map(option => ({  
              ...option, // 保留原始属性  
              // 如果需要，可以在这里添加或修改属性  
            }));  
         
      })  
      .catch((error) => {  
        console.error('获取问题选项失败', error);  
        this.$message.error(this.$t('m.failed_to_get_options_of_the_question'));
      });  
    },  

    addOption() {  
        this.questionForm.options.push({ optionContent: ''});
    },  
    submitForm(formName) {  
        const formData = {  
      content: {  
        questionId: this.questionForm.questionId,  
        author: this.questionForm.author,  
        authorId: this.questionForm.authorId,  
        questionType: parseInt(this.questionForm.questionType),  
        questionScore: parseInt(this.questionForm.questionScore),  
        questionContent: this.questionForm.questionContent,  
        rightAnswer: this.questionForm.rightAnswer  
      },  
      options: this.questionForm.options.map(option => ({  
        questionId: this.questionForm.questionId, 
        optionContent: option.optionContent,  
        author: this.questionForm.author, 
        authorId: this.questionForm.authorId  
      }))  
    }; 
     const formData1 = {  
      content: {  
        questionId: this.questionForm.questionId,  
        author: this.questionForm.author,  
        authorId: this.questionForm.authorId,  
        questionType: parseInt(this.questionForm.questionType),  
        questionScore: parseInt(this.questionForm.questionScore),  
        questionContent: this.questionForm.questionContent,  
        rightAnswer: this.questionForm.rightAnswer  
      },  
      options: this.questionForm.options.map(option => ({ 
        id: option.id,
        questionId: this.questionForm.questionId, 
        optionContent: option.optionContent,  
        author: this.questionForm.author, 
        authorId: this.questionForm.authorId  
      }))  
    };
    const fillData = { 
        questionId: this.questionForm.questionId,  
        author: this.questionForm.author,  
        authorId: this.questionForm.authorId,  
        questionType: parseInt(this.questionForm.questionType),  
        questionScore: parseInt(this.questionForm.questionScore),  
        questionContent: this.questionForm.questionContent,  
        rightAnswer: this.questionForm.rightAnswer  
    }; 
      
      if (this.$route.params.questionId) {  
        if (this.questionForm.questionType.toString() == '4') {
            // 编辑操作  
            api.admin_updateFillQuestion(fillData)  
              .then((response) => {  
                console.log('编辑成功', response);  
                this.$message.error(this.$t('m.Update_Successfully'));
                this.resetForm(formName);  
                this.dialogVisible = true; // 假设你要显示一个对话框  
              })  
              .catch((error) => {  
                this.$message.error(this.$t('m.Update_Failed'));
                console.error('编辑失败', error);  
              });
            } else {  
            // 编辑操作  
            api.admin_updateQuestion(formData1)  
              .then((response) => {  
                console.log('编辑成功', response); 
                this.$message.error(this.$t('m.Update_Successfully'));
                this.resetForm(formName);  
                this.dialogVisible = true; // 假设你要显示一个对话框  
              })  
              .catch((error) => {  
                this.$message.error(this.$t('m.Update_Failed'));
                console.error('编辑失败', error);  
              }); 
            } 
          } else {  
      if (this.questionForm.questionType.toString() == '4') {
        api.admin_addFillQuestion(fillData)  
              .then((response) => {  
                this.$message.error(this.$t('m.add_success'));
                console.log('添加成功', response);  
                this.resetForm(formName);  
                this.dialogVisible = true; // 假设你要显示一个对话框  
              })  
              .catch((error) => {  
                this.$message.error(this.$t('m.add_failed'));
                console.error('添加失败', error);  
              });
            } else { 
            // 添加操作  
            api.admin_addQuestion(formData)  
              .then((response) => { 
                this.$message.error(this.$t('m.add_success')); 
                console.log('添加成功', response);  
                this.resetForm(formName);  
                this.dialogVisible = true; // 假设你要显示一个对话框  
              })  
              .catch((error) => {  
                this.$message.error(this.$t('m.add_failed'));
                console.error('添加失败', error);  
              });  
          }  
        }
    },  
    resetForm(formName) { //重置选项
      this.$refs[formName].resetFields();   
      this.questionForm.options = [  
        { optionContent: '' }, 
      ];  
    },    
  },  
};  
</script>