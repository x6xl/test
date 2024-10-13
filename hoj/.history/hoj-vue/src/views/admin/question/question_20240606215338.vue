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
        questionType: '1', // 默认为单选题  
        questionScore: 1,
        rightAnswer:'',  
        options: [  
          { optionContent: '' ,
        }, // 初始一个空选项  
          // 可以根据需要添加更多默认选项  
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
        questionType: parseInt(this.questionForm.questionType), // 转换为数字，如果后端期望数字类型  
        questionScore: parseInt(this.questionForm.questionScore), // 同样转换为数字  
        questionContent: this.questionForm.questionContent,  
        rightAnswer: this.questionForm.rightAnswer // 注意：这里可能需要根据实际情况设置正确答案  
      },  
      options: this.questionForm.options.map(option => ({  
        questionId: this.questionForm.questionId, // 假设所有选项都共享相同的 questionId  
        optionContent: option.optionContent,  
        author: this.questionForm.author, // 假设所有选项都共享相同的 author 和 authorId  
        authorId: this.questionForm.authorId  
      }))  
    };  
      this.$refs[formName].validate((valid) => {  
        if (valid) {  
            console.log('qid',formData.content.questionId); 
            console.log('author:',formData.content.author); 
            console.log('opc:',formData.options.optionContent);
            console.log('opauthor:',formData.options.options.author); 
          api.admin_addQuestion(formData)  
            .then((response) => {  
              // 处理响应，比如显示成功消息、清空表单等  
              console.log('添加成功', response);  
              this.resetForm(formName);  
            })  
            .catch((error) => {  
              console.error('添加失败', error);  
            });  
        } else {  
          console.log('error submit!!');  
          return false;  
        }  
      });  
    },  
    resetForm(formName) { 
      this.$refs[formName].resetFields();  
      // 如果需要重置选项列表，也可以在这里做  
      this.questionForm.options = [  
        { optionContent: '' }, // 重置为初始状态  
      ];  
    },    
  },  
};  
</script>