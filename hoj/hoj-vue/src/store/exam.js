// store/exam.js
import Vue from 'vue';
import Vuex from 'vuex';

Vue.use(Vuex);

export default new Vuex.Store({
  state: {
    exam: {},
    examForm: {},
    examScore: 0,
    examTime: 0,
    examJoinDate: null,
    examStarted: false,
    email: '',
    password: '',
    fetchingPassword: false,
    examName1: '',
    examDescription1: '',
    examStartDate1: '',
    examEndDate1: '',
    problemIds: [],
    examSubmitted: false,
  },
  mutations: {
    SET_EXAM(state, exam) {
      state.exam = exam;
    },
    SET_EXAM_FORM(state, examForm) {
      state.examForm = examForm;
    },
    SET_EXAM_SCORE(state, score) {
      state.examScore = score;
    },
    SET_EXAM_TIME(state, time) {
      state.examTime = time;
    },
    SET_EXAM_JOIN_DATE(state, date) {
      state.examJoinDate = date;
    },
    SET_EXAM_STARTED(state, started) {
      state.examStarted = started;
    },
    SET_EMAIL(state, email) {
      state.email = email;
    },
    SET_PASSWORD(state, password) {
      state.password = password;
    },
    SET_FETCHING_PASSWORD(state, fetching) {
      state.fetchingPassword = fetching;
    },
    SET_EXAM_NAME(state, name) {
      state.examName1 = name;
    },
    SET_EXAM_DESCRIPTION(state, description) {
      state.examDescription1 = description;
    },
    SET_EXAM_START_DATE(state, startDate) {
      state.examStartDate1 = startDate;
    },
    SET_EXAM_END_DATE(state, endDate) {
      state.examEndDate1 = endDate;
    },
    SET_PROBLEM_IDS(state, ids) {
      state.problemIds = ids;
    },
    SET_EXAM_SUBMITTED(state, submitted) {
      state.examSubmitted = submitted;
    },
  },
  actions: {
    setExam({ commit }, exam) {
      commit('SET_EXAM', exam);
    },
    setExamForm({ commit }, examForm) {
      commit('SET_EXAM_FORM', examForm);
    },
    setExamScore({ commit }, score) {
      commit('SET_EXAM_SCORE', score);
    },
    setExamTime({ commit }, time) {
      commit('SET_EXAM_TIME', time);
    },
    setExamJoinDate({ commit }, date) {
      commit('SET_EXAM_JOIN_DATE', date);
    },
    setExamStarted({ commit }, started) {
      commit('SET_EXAM_STARTED', started);
    },
    setEmail({ commit }, email) {
      commit('SET_EMAIL', email);
    },
    setPassword({ commit }, password) {
      commit('SET_PASSWORD', password);
    },
    setFetchingPassword({ commit }, fetching) {
      commit('SET_FETCHING_PASSWORD', fetching);
    },
    setExamName({ commit }, name) {
      commit('SET_EXAM_NAME', name);
    },
    setExamDescription({ commit }, description) {
      commit('SET_EXAM_DESCRIPTION', description);
    },
    setExamStartDate({ commit }, startDate) {
      commit('SET_EXAM_START_DATE', startDate);
    },
    setExamEndDate({ commit }, endDate) {
      commit('SET_EXAM_END_DATE', endDate);
    },
    setProblemIds({ commit }, ids) {
      commit('SET_PROBLEM_IDS', ids);
    },
    setExamSubmitted({ commit }, submitted) {
      commit('SET_EXAM_SUBMITTED', submitted);
    },
  },
});
