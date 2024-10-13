<template>
  <div>
    <h2 style="color:red">注意：提交考试前请勿关闭此页面！！！否则将自动提交！！！</h2>
    <h2 v-if="examStarted">考试名称：{{ examName1 }}</h2>
    <p v-if="examStarted">考试描述：{{ examDescription1 }}</p>
    <p v-if="examStarted">考试时间：{{ examStartDate1 }} 至 {{ examEndDate1 }}</p>

    <el-input v-if="!examStarted" v-model="email" placeholder="请输入邮箱"></el-input>
    <el-button v-if="!examStarted && !fetchingPassword" type="primary" :disabled="fetchingPassword"
      @click="sendPasswordByEmail">
      {{ fetchingPassword ? '获取中...' : '获取考试密码' }}
    </el-button>
    <el-input v-if="!examStarted" v-model="password" placeholder="请输入考试密码" show-password></el-input>
    <el-button v-if="!examStarted" type="primary" @click="startExam">开始考试</el-button>

    <div v-if="examStarted" class="webcam-container">
      <video ref="video" autoplay></video>
    </div>

    <el-form v-if="examStarted" ref="examForm" :model="examForm" label-width="80px">
      <el-row v-for="(question, index) in exam.selectQuestionVOs" :key="question.questionId">
        <el-col :span="24">
          <h3>{{ '题目 ' + (index + 1) }}</h3>
          <p>{{ question.questionContentVOs[0].questionContent }}</p>

          <template v-if="question.questionContentVOs[0].questionType.toString() === '1'">
            <el-radio-group v-model="examForm[question.questionId]" @change="handleOptionChange(question.questionId)">
              <el-radio v-for="(option, optIndex) in question.options" :key="option.id" :label="option.id">
                {{ String.fromCharCode(65 + optIndex) }}. {{ option.optionContent }}
              </el-radio>
            </el-radio-group>
          </template>
          <template v-else-if="question.questionContentVOs[0].questionType.toString() === '2'">
            <el-checkbox-group v-model="examForm[question.questionId]"
              @change="handleOptionChange(question.questionId)">
              <el-checkbox v-for="(option, optIndex) in question.options" :key="option.id" :label="option.id">
                {{ String.fromCharCode(65 + optIndex) }}. {{ option.optionContent }}
              </el-checkbox>
            </el-checkbox-group>
          </template>
          <template v-else-if="question.questionContentVOs[0].questionType.toString() === '4'">
            <el-input v-model="examForm[question.questionId]" placeholder="请输入答案"></el-input>
          </template>
        </el-col>
      </el-row>
    </el-form>

    <div v-if="examStarted && problemIds.length" style="margin-top: 20px;">
      <h3>编程题:</h3>
      <el-row>
        <el-col v-for="(id, index) in problemIds" :key="index" :span="24">
          <el-button type="text" @click="navigateToProblem(id)" style="display: block; margin-bottom: 10px;">
            第{{ index + 1 }}题
          </el-button>
        </el-col>
      </el-row>
    </div>


    <el-button v-if="examStarted" type="primary" @click="submitExam">提交试卷</el-button>

    <el-dialog title="考试结果" :visible="showResultDialog" :close-on-click-modal="false" @close="closeResultDialog">
      <p>您的考试得分为：{{ examScore }}，用时：{{ examTime }}分钟</p>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="closeResultDialog">确定</el-button>
      </span>
    </el-dialog>



  </div>
</template>

<script>
import api from '@/common/api';
import { mapGetters, mapActions } from 'vuex';
import { Notification } from 'element-ui';
import Problem from '../problem/Problem.vue';

export default {
  data() {
    return {
      exam: {},
      examForm: {}, // 存储选中选项的表单数据
      showResultDialog: false,
      examScore: 0,
      examTime: 0,
      examJoinDate: null,  // 初始化为 null，等待用户点击开始考试时赋值
      examStarted: false,  // 初始化为 false，表示未开始考试
      email: '',  // 用于存储用户输入的邮箱地址
      password: '',  // 用于存储用户输入的考试密码
      fetchingPassword: false,
      examName1: '',
      examDescription1: '',
      examStartDate1: '',
      examEndDate1: '',
      showLeaveConfirmDialog: false, // 控制离开页面确认对话框的显示
      examSubmitted: false,
      problemIds: [],
    };
  },
  mounted() {

    const savedState = this.examState;

    if (savedState) {
      this.exam = savedState.exam || {};
      this.examForm = savedState.examForm || {};
      this.showResultDialog = savedState.showResultDialog || false;
      this.examScore = savedState.examScore || 0;
      this.examTime = savedState.examTime || 0;
      this.examJoinDate = savedState.examJoinDate || null;
      this.examStarted = savedState.examStarted || false;
      this.email = savedState.email || '';
      this.password = savedState.password || '';
      this.fetchingPassword = savedState.fetchingPassword || false;
      this.examName1 = savedState.examName1 || '';
      this.examDescription1 = savedState.examDescription1 || '';
      this.examStartDate1 = savedState.examStartDate1 || '';
      this.examEndDate1 = savedState.examEndDate1 || '';
      this.showLeaveConfirmDialog = savedState.showLeaveConfirmDialog || false;
      this.examSubmitted = savedState.examSubmitted || false;
      this.problemIds = savedState.problemIds || [];
    }
    // 如果考试已开始，尝试自动加载考试信息
    if (this.examStarted ) {
      this.startWebcam();
      this.startExamTimer();
    }

  },

  methods: {
    ...mapActions(['saveExamState']),

    saveExamState() {
      this.$store.dispatch('saveExamState', {
        exam: this.exam,
        examForm: this.examForm,
        showResultDialog: this.showResultDialog,
        examScore: this.examScore,
        examTime: this.examTime,
        examJoinDate: this.examJoinDate,
        examStarted: this.examStarted,
        email: this.email,
        password: this.password,
        fetchingPassword: this.fetchingPassword,
        examName1: this.examName1,
        examDescription1: this.examDescription1,
        examStartDate1: this.examStartDate1,
        examEndDate1: this.examEndDate1,
        showLeaveConfirmDialog: this.showLeaveConfirmDialog,
        examSubmitted: this.examSubmitted,
        problemIds: this.problemIds
      });
    },
    fetchExam() {
      const examId = this.$route.params.examId;
      api.getExam(examId, this.password)
        .then(response => {
          this.exam = response.data.data;
          this.examName1 = response.data.data.exam.examName;
          this.examDescription1 = response.data.data.exam.examDescription;
          this.examStartDate1 = response.data.data.exam.examStartDate;
          this.examEndDate1 = response.data.data.exam.examEndDate;
          this.problemIds = response.data.data.problemId || [];
          this.exam.selectQuestionVOs.forEach(question => {
            // 根据题目类型初始化不同类型的表单数据
            if (question.questionContentVOs[0].questionType.toString() === '1') {
              this.$set(this.examForm, question.questionId, '');
            } else if (question.questionContentVOs[0].questionType.toString() === '2') {
              this.$set(this.examForm, question.questionId, []);
            }
          });
          this.examStarted = true;
          // 启动考试定时器
          this.startExamTimer();
          this.startWebcam();
        })
        .catch(error => {
          console.error('获取考试信息时出错', error);
        });
    },

    startExam() {
      this.examJoinDate = new Date();
      console.log('开始考试时间：', this.examJoinDate);


      // 用户点击开始考试后再获取考试信息
      this.fetchExam();
    },

    // 启动摄像头方法
    startWebcam() {
      if (this.webcamStream) {
      this.stopWebcam(); // 确保之前的摄像头流被停止
    }
      navigator.mediaDevices.getUserMedia({ video: true })
        .then(stream => {
          this.$refs.video.srcObject = stream;
          this.webcamStream = stream;
          this.$refs.video.addEventListener('loadedmetadata', this.captureAndUploadImage);
        })
        .catch(error => {
          console.error('无法访问摄像头', error);
          // 处理错误
        });
    },

    // 停止摄像头方法
    stopWebcam() {
      if (this.webcamStream) {
        this.webcamStream.getTracks().forEach(track => track.stop());
        this.$refs.video.srcObject = null;
        this.webcamStream = null;
      }
    },

    startExamTimer() {
      const examEndDate = new Date(this.exam.exam.examEndDate).getTime();
      const captureInterval = 60000;

      // 初始捕获
      //this.captureAndUploadImage();

      const timer = setInterval(() => {
        const now = new Date().getTime();
        if (now > examEndDate) {
          clearInterval(timer);
          this.submitExam(); // 时间到，提交考试
        } else {
          //this.captureAndUploadImage(); // 每分钟捕获一次图像
          const randomInterval = Math.floor(Math.random() * captureInterval); // 生成随机捕获时间点
          setTimeout(() => this.captureAndUploadImage(), randomInterval); // 随机时间点捕获图像
        }
      }, captureInterval);
    },

    captureAndUploadImage() {
      const video = this.$refs.video;
      const canvas = document.createElement('canvas');
      canvas.width = video.videoWidth;
      canvas.height = video.videoHeight;

      const context = canvas.getContext('2d');
      context.drawImage(video, 0, 0, canvas.width, canvas.height);
      canvas.toBlob(blob => {
        if (!blob) {
          console.error('生成的 blob 对象为空');
          return;
        }
        const formData = new FormData();
        formData.append('file', blob, 'image.jpg'); // 检查这里的键名和文件名
        formData.append('exam_id', this.$route.params.examId);

        this.$http.post('/api/examImage/add-examImageShot', formData, {
          headers: { 'Content-Type': 'multipart/form-data' },
        }).then(response => {
          console.log('图像上传成功', response);
          // 处理响应
        }).catch(error => {
          console.error('上传图像失败', error);
          // 处理错误情况
        });
      }, 'image/jpeg');
    },


    handleOptionChange(questionId) {
      // 处理单选或多选题选项更改
      console.log('选中的选项 for question ' + questionId + ':', this.examForm[questionId]);
    },

    submitExam() {
      const answersMap = {};
      const submitTime = new Date();

      Object.keys(this.examForm).forEach(questionId => {
        const selectedOptions = this.examForm[questionId];
        const question = this.exam.selectQuestionVOs.find(q => q.questionId === questionId);
        console.log(question);
        if (question.questionContentVOs[0].questionType.toString() === '4') {
          // 填空题，直接获取输入的内容
          answersMap[questionId] = [selectedOptions];
        }
        else if (Array.isArray(selectedOptions)) {
          const sortedOptions = selectedOptions.sort();
          answersMap[questionId] = sortedOptions.map(optionId => {
            const option = this.exam.selectQuestionVOs.find(question => question.questionId === questionId)
              .options.find(option => option.id === optionId);
            return option.optionContent;
          });
        } else {
          const option = this.exam.selectQuestionVOs.find(question => question.questionId === questionId)
            .options.find(option => option.id === selectedOptions);
          answersMap[questionId] = [option.optionContent];
        }
      });

      const examTimeCost = Math.floor((submitTime - this.examJoinDate) / (1000 * 60));

      const data = {
        answersMap,
        examJoinDate: this.examJoinDate,
        examTimeCost
      };

      // 调用后端接口提交考试数据
      api.finishExam(this.$route.params.examId, data)
        .then(response => {
          console.log('考试提交成功', response);
          alert('考试提交成功！');
          this.examScore = response.data.data.examJoinScore;
          this.examTime = response.data.data.examTimeCost;
          this.showResultDialog = true;
        })
        .catch(error => {
          console.error('提交考试失败', error);
          alert('提交考试失败！');
          Notification.error({
            title: '错误',
            message: '提交考试失败，请稍后重试'
          });
        });

      //关闭摄像头
      this.stopWebcam();
      this.examSubmitted = true;
      sessionStorage.removeItem('examState');
    },

    closeResultDialog() {
      this.stopWebcam();
      // 关闭考试结果弹框
      this.showResultDialog = false;
      // 返回到考试列表页面
      this.$router.push('/exam');

    },

    sendPasswordByEmail() {
      const data1 = {
        examId: this.$route.params.examId,
        email: this.email
      };

      this.fetchingPassword = true; // 开始获取密码，禁用按钮
      // 调用 API 发送考试密码到邮箱
      api.ExamSentPassword(data1)
        .then(response => {
          Notification.success({
            title: '成功',
            message: '考试密码已发送至您的邮箱，请查收。'
          });
          this.fetchingPassword = false;
        })
        .catch(error => {
          console.error('获取考试密码失败', error);
          this.fetchingPassword = false;
        });
    },

    navigateToProblem(problemId) {
      this.$router.push({
        name: 'examProblemDetail',
        params: {
          problemID: problemId,
        },
      });
    },
  },
  computed: {
    ...mapGetters(['examState'])
  },
  beforeRouteLeave(to, from, next) {
    if (this.examStarted  && !this.examSubmitted && to.name !== 'examProblemDetail') {
      if (confirm('您还没有提交考试，是否要离开并自动提交考试？')) {
        this.submitExam().finally(() => {
          next(); // 确保在提交后调用 next() 方法
        });
      } else {
        next(false); // 如果用户选择取消，不离开页面
      }
    } else {
      this.saveExamState(); // 保存当前状态
      next(); // 如果已经提交了考试，正常离开
    }
  },
};
</script>


<style scoped>
.webcam-container {
  position: fixed;
  top: 100px;
  right: 20px;
  z-index: 1000;
  background-color: white;
  border: 1px solid #ccc;
  padding: 10px;
  width: 320px;
  /* 调整为适合的宽度 */
  height: auto;
  /* 根据比例自适应高度 */
}

.webcam-container video {
  width: 100%;
  /* 视频元素宽度占满容器 */
  height: auto;
  /* 根据比例自适应高度 */
}
</style>