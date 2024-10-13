<template>
  <el-row :gutter="18">
    <el-col :sm="24" :md="18" :lg="18">
      <el-card shadow>
        <div slot="header">
          <el-row :gutter="20" style="margin-bottom: 0.5em;">
            <el-col :xs="24" :sm="6">
              <span class="problem-list-title">{{ $t('m.question_List') }}</span>
            </el-col>
            <el-col :xs="24" :sm="12">
              <el-input v-model="keyword" placeholder="{{ $t('m.Please_enter_a_keyword') }}"></el-input>
            </el-col>
            <el-col :xs="24" :sm="6">
              <el-button type="primary" icon="el-icon-search" circle @click="search"></el-button>
            </el-col>
          </el-row>
        </div>

        <el-table :data="questionList" stripe style="width: 100%" @row-click="handleRowClick">
          <el-table-column fixed prop="id" :label="$t('m.Opt_id')" width="50"></el-table-column>
          <el-table-column prop="questionId" :label="$t('m.Opt_questionId')" width="100"></el-table-column>
          <el-table-column prop="author" :label="$t('m.Opt_author')" width="100"></el-table-column>
          <el-table-column prop="questionContent" :label="$t('m.Opt_questionContent')" width="200" :show-overflow-tooltip='true'></el-table-column>
          <el-table-column prop="authorId" :label="$t('m.Opt_authorId')" width="100"></el-table-column>
          <el-table-column :label="$t('m.Opt_questionType')" width="100">
            <template slot-scope="scope">
              {{ getQuestionTypeLabel(scope.row.questionType) }}
            </template>
          </el-table-column>
          <el-table-column prop="questionScore" :label="$t('分数')" width="100"></el-table-column>
          <el-table-column prop="createTime" :label="$t('m.Opt_createTime')" width="250"></el-table-column>
        </el-table>

        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page.sync="currentPage"
          :page-sizes="[10, 20, 30, 40]"
          :page-size="pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total">
        </el-pagination>
      </el-card>
    </el-col>
  </el-row>
</template>

<script>
import api from '@/common/api';

export default {
  data() {
    return {
      questionList: [],
      keyword: '',
      currentPage: 1,
      pageSize: 10,
      total: 0
    };
  },
  created() {
    this.getQuestions();
  },
  methods: {
    getQuestions() {
      const { pageSize, currentPage, keyword } = this;
      api.getQuestionListPage(pageSize, currentPage, keyword)
        .then(response => {
          this.questionList = response.data.data.content;
          this.total = response.data.data.totalElements;
        });
    },
    handleSizeChange(size) {
      this.pageSize = size;
      this.currentPage = 1; 
      this.getQuestions();
    },
    handleCurrentChange(currentPage) {
      this.currentPage = currentPage;
      this.getQuestions();
    },
    search() {
      this.currentPage = 1; 
      this.getQuestions();
    },
    handleRowClick(row) {
      this.$router.push({ name: 'QuestionDetail', params: { id: row.id } });
    },
    getQuestionTypeLabel(type) {
      const types = {
        '1': '单选题',
        '2': '多选题',
        '4': '填空题'
      };
      return types[type] || '未知题型';
    }
  }
};
</script>
