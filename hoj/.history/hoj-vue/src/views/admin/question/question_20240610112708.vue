<template>  
  <div>  
    <el-form :model="questionForm" ref="questionForm" label-width="120px">  
        <el-form-item label="题目id">  
        <el-input v-model="questionForm.questionId"></el-input>  
      </el-form-item>  
      <el-form-item label="作者">  
        <el-input v-model="questionForm.author"></el-input>  
      </el-form-item>  
      <el-form-item label="作者id">  
        <el-input v-model="questionForm.authorId"></el-input>  
      </el-form-item>  
      <el-form-item label="题目内容">  
        <el-input v-model="questionForm.questionContent"></el-input>  
      </el-form-item>  
      <el-form-item label="正确答案">  
        <el-input v-model="questionForm.rightAnswer"></el-input>  
      </el-form-item>  
      <el-form-item label="题目类型">  
        <el-radio-group v-model="questionForm.questionType">  
          <el-radio label=1>单选题</el-radio>  
          <el-radio label=2>多选题</el-radio>  
        </el-radio-group>  
      </el-form-item>  
      <el-form-item label="题目分数">  
        <el-input-number v-model="questionForm.questionScore" :min="1"></el-input-number>  
      </el-form-item>  
      <el-form-item label="选项">  
        <div v-for="(option, index) in questionForm.options" :key="index">  
          <el-input v-model="option.optionContent" placeholder="请输入选项内容"></el-input>  
          <!-- 这里可以添加删除选项的按钮 -->  
        </div>  
        <el-button type="primary" @click="addOption">添加选项</el-button>  
      </el-form-item>  
      <el-form-item>  
        <el-button type="primary" @click="submitForm('questionForm')">提交</el-button>  
        <el-button @click="resetForm('questionForm')">重置</el-button>  
      </el-form-item>  
    </el-form> 
    <el-dialog  
    title="提示"  
    :visible.sync="dialogVisible"  
    width="30%"  
    @close="dialogVisible = false">  
    <span>创建成功</span>  
    <span slot="footer" class="dialog-footer">  
      <el-button @click="dialogVisible = false">确 定</el-button>  
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
        questionType: 1,  
        questionScore: 1,
        rightAnswer:'',  
        options: [  
          { optionContent: '' ,
          questionId: '',
        author:'',
        authorId:'',
        },  
        ],  
      },  
    };  
  },   
  created() {  
  // 检查路由参数中是否有questionId  
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
        this.questionForm.questionType=response.data.data.questionType;
        this.questionForm.questionScore=response.data.data.questionScore;
        this.questionForm.rightAnswer=response.data.data.rightAnswer;
        return this.getQuestionOptions
        
      })  
      .catch((error) => {  
        console.error('获取问题详情失败', error);  
      });  
    },  


    getQuestionOptions() {  
    this.admin_getOptions(this.questionForm.questionId)  
      .then((optionsResponse) => {  
        this.questionForm.options = optionsResponse.data.map(option => ({  
              ...option, // 保留原始属性  
              // 如果需要，可以在这里添加或修改属性  
            }));  
         
      })  
      .catch((error) => {  
        console.error('获取问题选项失败', error);  
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
      this.$refs[formName].validate((valid) => {  
        if (valid) {  
          api.admin_addQuestion(formData)  
            .then((response) => {  
              console.log('添加成功666', response);  
              this.resetForm(formName);  
              this.dialogVisible = true;
            })  
            .catch((error) => {  
              console.error('添加失败000', error);  
            });  
        } else {  
          console.log('00000');  
          return false;  
        }  
      });  
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