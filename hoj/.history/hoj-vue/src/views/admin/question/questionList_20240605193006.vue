<template>
    <div>
      <el-card>
        <div slot="header">
          <span class="panel-title home-title">{{
            query.contestId ? $t('m.Contest_Problem_List') : $t('m.Problem_List')
          }}</span>
          <div class="filter-row">
            <span>
              <el-button
                type="primary"
                size="small"
                @click="goCreateProblem"
                icon="el-icon-plus"
                >{{ $t('m.Create') }}
              </el-button>
            </span>
            <span v-if="query.contestId">
              <el-button
                type="primary"
                size="small"
                icon="el-icon-plus"
                @click="addProblemDialogVisible = true"
                >{{ $t('m.Add_From_Public_Problem') }}
              </el-button>
            </span>
            <!-- <span>
              <el-button
                type="success"
                size="small"
                @click="AddRemoteOJProblemDialogVisible = true"
                icon="el-icon-plus"
                >{{ $t('m.Add_Rmote_OJ_Problem') }}
              </el-button>
            </span> -->
            <span>
              <vxe-input
                v-model="query.keyword"
                :placeholder="$t('m.Enter_keyword')"
                type="search"
                size="medium"
                @search-click="filterByKeyword"
                @keyup.enter.native="filterByKeyword"
              ></vxe-input>
            </span>
  
            <span>
              <el-select
                v-model="query.oj"
                @change="ProblemListChangeFilter"
                size="small"
                style="width: 180px;"
              >
                <el-option
                  :label="$t('m.All_Problem')"
                  :value="'All'"
                ></el-option>
                <el-option :label="$t('m.My_OJ')" :value="'Mine'"></el-option>
                <el-option
                  :label="remoteOj.name"
                  :key="index"
                  :value="remoteOj.key"
                  v-for="(remoteOj, index) in REMOTE_OJ"
                ></el-option>
              </el-select>
            </span>
  
            <span v-if="!query.contestId">
              <el-select
                v-model="query.problemListAuth"
                @change="ProblemListChangeFilter"
                size="small"
                style="width: 180px;"
              >
                <el-option :label="$t('m.All_Problem')" :value="0"></el-option>
                <el-option :label="$t('m.Public_Problem')" :value="1"></el-option>
                <el-option
                  :label="$t('m.Private_Problem')"
                  :value="2"
                ></el-option>
                <el-option
                  :label="$t('m.Contest_Problem')"
                  :value="3"
                ></el-option>
              </el-select>
            </span>
          </div>
        </div>
        <vxe-table
          stripe
          auto-resize
          :data="problemList"
          ref="adminProblemList"
          :loading="loading"
          align="center"
        >
          <vxe-table-column min-width="64" field="id" title="ID">
          </vxe-table-column>
          <vxe-table-column
            min-width="100"
            field="problemId"
            :title="$t('m.Display_ID')"
            v-if="!isContest"
          >
          </vxe-table-column>
  
          <vxe-table-column
            min-width="150"
            :title="$t('m.Original_Display')"
            v-else
            align="left"
          >
            <template v-slot="{ row }">
              <p v-if="query.contestId">
                {{ $t('m.Display_ID') }}：{{ row.problemId }}
              </p>
              <p v-if="query.contestId">{{ $t('m.Title') }}：{{ row.title }}</p>
              <span v-else>{{ row.problemId }}</span>
            </template>
          </vxe-table-column>
  
          <vxe-table-column
            field="title"
            min-width="150"
            :title="$t('m.Title')"
            show-overflow
            v-if="!isContest"
          >
          </vxe-table-column>
  
          <vxe-table-column
            min-width="150"
            :title="$t('m.Contest_Display')"
            v-else
            align="left"
          >
            <template v-slot="{ row }">
              <p v-if="contestProblemMap[row.id]">
                {{ $t('m.Display_ID') }}：{{
                  contestProblemMap[row.id]['displayId']
                }}
              </p>
              <p v-if="contestProblemMap[row.id]">
                {{ $t('m.Title') }}：{{
                  contestProblemMap[row.id]['displayTitle']
                }}
              </p>
              <span v-if="contestProblemMap[row.id]">
                {{ $t('m.Balloon_Color') }}：<el-color-picker
                  v-model="contestProblemMap[row.id].color"
                  show-alpha
                  :predefine="predefineColors"
                  size="small"
                  style="vertical-align: middle;"
                  @change="
                    changeContestProblemColor(
                      contestProblemMap[row.id].id,
                      contestProblemMap[row.id].color
                    )
                  "
                >
                </el-color-picker>
              </span>
              <span v-else>{{ row.title }}</span>
            </template>
          </vxe-table-column>
  
          <vxe-table-column
            field="author"
            min-width="130"
            :title="$t('m.Author')"
            show-overflow
          >
          </vxe-table-column>
          <vxe-table-column min-width="120" :title="$t('m.Created_Time')">
            <template v-slot="{ row }">
              {{ row.gmtCreate | localtime }}
            </template>
          </vxe-table-column>
          <vxe-table-column
            min-width="96"
            field="modifiedUser"
            :title="$t('m.Modified_User')"
            show-overflow
          >
          </vxe-table-column>
          <vxe-table-column min-width="120" :title="$t('m.Auth')">
            <template v-slot="{ row }">
              <el-select
                v-model="row.auth"
                @change="changeProblemAuth(row)"
                size="small"
                :disabled="!isSuperAdmin && !isProblemAdmin && !query.contestId"
              >
                <el-option
                  :label="$t('m.Public_Problem')"
                  :value="1"
                  :disabled="!isSuperAdmin && !isProblemAdmin"
                ></el-option>
                <el-option
                  :label="$t('m.Private_Problem')"
                  :value="2"
                ></el-option>
                <el-option
                  :label="$t('m.Contest_Problem')"
                  :value="3"
                  :disabled="!query.contestId"
                ></el-option>
              </el-select>
            </template>
          </vxe-table-column>
          <vxe-table-column title="Option" min-width="200">
            <template v-slot="{ row }">
              <el-tooltip
                effect="dark"
                :content="$t('m.Edit')"
                placement="top"
                v-if="
                  isSuperAdmin ||
                    isProblemAdmin ||
                    row.author == userInfo.username
                "
              >
                <el-button
                  icon="el-icon-edit-outline"
                  size="mini"
                  @click.native="goEdit(row.id)"
                  type="primary"
                >
                </el-button>
              </el-tooltip>
  
              <el-tooltip
                effect="dark"
                :content="$t('m.Download_Testcase')"
                placement="top"
                v-if="isSuperAdmin || isProblemAdmin"
              >
                <el-button
                  icon="el-icon-download"
                  size="mini"
                  @click.native="downloadTestCase(row.id)"
                  type="success"
                >
                </el-button>
              </el-tooltip>
  
              <el-tooltip
                effect="dark"
                :content="$t('m.Remove')"
                placement="top"
                v-if="query.contestId"
              >
                <el-button
                  icon="el-icon-close"
                  size="mini"
                  @click.native="removeProblem(row.id)"
                  type="warning"
                >
                </el-button>
              </el-tooltip>
  
              <el-tooltip
                effect="dark"
                :content="$t('m.Delete')"
                placement="top"
                v-if="isSuperAdmin || isProblemAdmin"
              >
                <el-button
                  icon="el-icon-delete-solid"
                  size="mini"
                  @click.native="deleteProblem(row.id)"
                  type="danger"
                >
                </el-button>
              </el-tooltip>
            </template>
          </vxe-table-column>
        </vxe-table>
  
        <div class="panel-options" v-if="showPagination">
          <el-pagination
            class="page"
            layout="prev, pager, next, sizes"
            @current-change="currentChange"
            :page-size.sync="query.pageSize"
            :total.sync="total"
            :current-page.sync="query.currentPage"
            @size-change="onPageSizeChange"
            :page-sizes="[10, 30, 50, 100]"
          >
          </el-pagination>
        </div>
      </el-card>
  
      <el-dialog
        :title="$t('m.Add_Contest_Problem')"
        v-if="query.contestId"
        width="90%"
        :visible.sync="addProblemDialogVisible"
        :close-on-click-modal="false"
      >
        <AddPublicProblem
          :contestID="query.contestId"
          @on-change="getProblemList"
        ></AddPublicProblem>
      </el-dialog>
  
      <el-dialog
        :title="$t('m.Add_Rmote_OJ_Problem')"
        width="350px"
        :visible.sync="AddRemoteOJProblemDialogVisible"
        :close-on-click-modal="false"
      >
        <el-form>
          <el-form-item :label="$t('m.Remote_OJ')">
            <el-select v-model="otherOJName" size="small">
              <el-option
                :label="remoteOj.name"
                :value="remoteOj.key"
                v-for="(remoteOj, index) in REMOTE_OJ"
                :key="index"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item :label="$t('m.Problem_ID')" required>
            <el-input v-model="otherOJProblemId" size="small"></el-input>
          </el-form-item>
  
          <el-form-item
            v-if="query.contestId"
            :label="$t('m.Enter_The_Problem_Display_ID_in_the_Contest')"
            required
          >
            <el-input v-model="displayId" size="small"></el-input>
          </el-form-item>
  
          <el-form-item style="text-align:center">
            <el-button
              type="primary"
              icon="el-icon-plus"
              @click="addRemoteOJProblem"
              :loading="addRemoteOJproblemLoading"
              >{{ $t('m.Add') }}
            </el-button>
          </el-form-item>
        </el-form>
      </el-dialog>
    </div>
  </template>