ue
复制代码
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
          <el-radio label="1">单选题</el-radio>  
          <el-radio label="2">多选题</el-radio>  
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
  </div>  
</template>  
  
<script>  
  import api from '@/common/api';
export default {  
  data() {  
    return {  
      questionForm: {  
        questionId: '',
        author:'',
        authorId:'',
        questionContent: '',  
        questionType: '1',  
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
  methods: {  
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