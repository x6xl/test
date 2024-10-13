<template>
    <el-row :gutter="18">
        <el-card shadow>
          <div slot="header">
          <el-row :gutter="20" style="margin-bottom: 0.5em;">  
            <el-col :xs="8" :sm="6" >  
              <el-input v-model="searchKeyword" placeholder="请输入搜索关键词" ></el-input>  
            </el-col>  
            <el-col :xs="4" :sm="6" class="text-right">  
              <el-button type="primary" icon="el-icon-search" @click="searchQuestions"></el-button>  
              <el-button type="primary" icon="el-icon-circle-plus-outline" @click="addquestions"></el-button>
            </el-col>  
          </el-row>  

          

            <el-row :gutter="20" style="margin-bottom: 0.5em;">
              <el-col :xs="24" :sm="6">
                <span class="problem-list-title">{{ $t('m.Problem_List') }}</span>
              </el-col>
            </el-row>
          </div>
          <el-table :data="questionList" stripe style="width: 100%"  @row-click="handleRowClick"> 
          <el-table-column fixed prop="id" :label="$t('m.Opt_id')" width="50"></el-table-column>   
          <el-table-column prop="questionId" :label="$t('m.Opt_questionId')" width="100"></el-table-column>  
          <el-table-column prop="author" :label="$t('m.Opt_author')" width="100"></el-table-column>   
          <el-table-column prop="authorId" :label="$t('m.Opt_authorId')" width="100"></el-table-column> 
          <el-table-column prop="questionType" :label="$t('m.Opt_questionType')" width="200"></el-table-column> 
          <el-table-column prop="questionScore" label="分数" width="200"></el-table-column> 
          <el-table-column prop="questionContent" :label="$t('m.Opt_questionContent')" width="200" :show-overflow-tooltip='true'></el-table-column>
          <el-table-column prop="rightAnswer" label="正确答案" width="200"></el-table-column> 
          <el-table-column prop="createTime" :label="$t('m.Opt_createTime')" width="250"></el-table-column>  
          <el-table-column fixed="right" label="操作" width="300">  
             <template slot-scope="scope">  
               <el-button  
                 type="danger"  
                 icon="el-icon-delete"  
                 @click.stop="handleDelete(scope.row.questionId)"  
                ></el-button>  
                <el-button @click="showOptions(scope.row)">详情</el-button>
              </template>  
        </el-table-column> 
          </el-table>

          <el-dialog :visible.sync="dialogVisible" title="问题选项">  
            <div v-for="(option, index) in currentQuestionOptions" :key="index">  
              {{ option }}  
            </div>  
            <span slot="footer" class="dialog-footer">  
            <el-button @click="dialogVisible = false">取 消</el-button>  
            <el-button type="primary" @click="dialogVisible = false">确 定</el-button>  
            </span>  
          </el-dialog> 
        </el-card>  
    </el-row>
  </template>
  

  <script>  
  import api from '@/common/api';
  export default {  
    data() {  
      return {  
        searchKeyword: '',
        questionList: [], 
        dialogVisible: false,  
        currentQuestionOptions: [], // 用于存储当前点击问题的选项 
      };  
    },  
    created() {  
      this.getQuestions(); 
    },  
    methods: {  
      getQuestions() {  
      api.admin_getQuestionList().then(response => {  
        this.questionList = response.data.data.map(question => ({  
          ...question, 
          options: [],  
        }));  
      })  
      .catch(error => {  
        console.error('Error fetching questions:', error);  
      });  
     },  

    searchQuestions() {  
      api.admin_getQuestionListFind(this.searchKeyword)
      .then(response => {  
        this.questionList = response.data.data; 
      }).catch(error => {  
        console.error(error);  
      });  
    },  

    addquestions(){
        this.$router.push({name:'admin-add-question'})
    },

    handleDelete(questionId){
        api.admin_deleteQuestion(questionId)
        .then(response => {  
        return this.getQuestions(); 
      }).catch(error => {  
        console.error(error);  
      }); 
    },

      getQuestions() {  
        api.admin_getQuestionList().then(response => {  
          this.questionList = response.data.data;  
        })  
      },
      handleRowClick(row, column, event) {  
        this.currentQuestionOptions = row.options; // 将当前问题的选项赋值给currentQuestionOptions  
        this.dialogVisible = true; // 显示弹窗 
      } ,
      showOptions(question) {  
      api.admin_getOptions(question.questionId)
      .then(response => { 
        this.currentQuestionOptions = response.data.data;
         this.dialogVisible = true; });   
      this.currentQuestionOptions = question.options;  
      this.dialogVisible = true;  
      },  
    },  

/*     watch: {  
    searchKeyword(newVal) {  
      if (newVal.trim()) {  
        this.searchQuestions();  
      }  
    }  
  }  */
  };  
  </script> 

  



  <style scoped>
  .problem-list-title {
    font-size: 2em;
    font-weight: 500;
    line-height: 30px;
  }
  
  .taglist-title {
    font-size: 21px;
    font-weight: 500;
  }
  
  section {
    display: flex;
    align-items: baseline;
    margin-bottom: 0.8em;
  }
  .problem-filter {
    margin-right: 1em;
    font-weight: bolder;
    white-space: nowrap;
    font-size: 16px;
    margin-top: 8px;
  }
  .filter-item {
    margin-right: 1em;
    margin-top: 0.5em;
    font-size: 13px;
  }
  .filter-item:hover {
    cursor: pointer;
  }
  

  @media only screen and (max-width: 767px) {
    .filter-mt {
      margin-top: 8px;
    }
  }
  
  /deep/.el-tag--dark {
    border-color: #d9ecff;
  }
  /deep/.tag-btn {
    margin-left: 4px !important;
    margin-top: 4px;
  }
  /deep/.vxe-checkbox .vxe-checkbox--label {
    overflow: unset !important;
  }
  /deep/ .vxe-input {
    width: 100%;
  }
  #pick-one {
    margin-top: 10px;
  }
  /deep/ .el-card__header {
    border-bottom: 0px;
    padding-bottom: 0px;
  }
  @media screen and (min-width: 1200px) {
    /deep/ .el-card__body {
      padding-top: 0px;
      margin-top: 5px;
    }
  }
  ul {
    float: right;
  }
  .title-a {
    color: #495060;
    font-family: inherit;
    font-size: 14px;
    font-weight: 500;
  }
  .el-progress {
    margin-top: 15px;
  }
  
  @media screen and (min-width: 1050px) {
    /deep/ .vxe-table--body-wrapper {
      overflow-x: hidden !important;
    }
  }
  
  /deep/.el-collapse-item__header{
    font-weight: bolder !important;
    height: 38px !important;
    line-height: 38px !important;
    font-size: 15px !important;
  }
  /deep/.el-collapse-item__content {
    padding-bottom: 10px !important;
  }
  </style>
  