<template>
    <el-card class="box-card">
     <div slot="header" class="clearfix" style="margin-bottom: -10px">
      <el-input v-model="question.score" style="width: 55px" />
      <span>分</span>
      <el-button style="float: right; border: none; font-size: 20px" icon="el-icon-close" @click="removeQuestion" />
     </div>
     <el-form-item>
      <el-input
       v-model="question.title"
       type="textarea"
       placeholder="请输入题干内容..."
      />
     </el-form-item>
     <!--单选、多选-->
     <el-form-item v-if="question.questionType === 1 || question.questionType === 2" style="margin-bottom: 0px">
      <el-checkbox-group
       v-model="question.answer"
       :min="0"
       :max="question.quesTypeId === 1 ? 1 : 4"
      >
       <el-row
        v-for="(item, index) in ['A', 'B', 'C', 'D']"
        :key="item"
       >
        <el-col :span="1">
         <el-checkbox-button
          v-model="question.answer"
          :label="question.content[index]"
          border
         >
          {{ item }}
         </el-checkbox-button>
        </el-col>
        <el-col :span="23">
         <el-input
          v-model="question.content[index]"
          placeholder="请输入选项..."
          @input="contentChange(question)"
         />
        </el-col>
       </el-row>
      </el-checkbox-group>
     </el-form-item>
     <!--简答、填空-->
     <!-- <el-form-item v-if="question.quesTypeId === 3 || question.quesTypeId === 4" style="margin-bottom: 0px">
      <el-input
       v-model="question.answer[0]"
       type="textarea"
       placeholder="请输入参考答案"
      />
     </el-form-item>
     //判断
     <el-form-item v-if="question.quesTypeId === 5" style="margin-bottom: 0px">
      <el-checkbox-group
       v-model="question.answer"
       :min="0"
       :max="1"
      >
       <el-checkbox v-model="question.answer" label="对" border />
       <el-checkbox v-model="question.answer" label="错" border />
      </el-checkbox-group>
     </el-form-item> -->
    </el-card>
   </template>
    
   <script>
   export default {
    props: {
     question: {
      type: Object,
      required: true
     }
    },
    methods: {
     removeQuestion() {
      this.$emit('removeQuestion', this.question.id)
     },
     contentChange(question) {
      question.answer.splice(0)
     }
    }
   }
   </script>
    
   <style scoped>
   .type-name {
    color: #505050;
    margin-right: 20px;
   }
   </style>