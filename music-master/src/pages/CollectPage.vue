<template>
  <div class="table">
    {{id}}
    <div class="crumbs">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item>
          <i class="el-icon-tickets"></i> 收藏信息
        </el-breadcrumb-item>
      </el-breadcrumb>
    </div>
    <div class="container">
      <div class="handle-box">
        <el-button type="primary" size="mini" class="handle-del mr10" @click="delAll" icon="el-icon-delete">批量删除</el-button>
        <el-input v-model="select_word" size="mini" placeholder="根据歌名筛选" class="handle-input mr10"></el-input>
      </div>
      <el-table
        :data="collectList"
        size="mini"
        border
        style="width: 100%"
        ref="multipleTable"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="50"></el-table-column>
        <el-table-column prop="songName" label="歌曲名"></el-table-column>
        <el-table-column prop="username" label="发布者"></el-table-column>
        <el-table-column label="操作" width="85">
          <template slot-scope="scope">
            <el-button size="mini" type="danger" @click="handleDelete(scope.row)" >删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <div class="pagination">
        <el-pagination
          background
          :total="tableData.length"
          :current-page="currentPage"
          :page-size="pageSize"
          :page-sizes="[5, 10, 15, 20]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        >
        </el-pagination>
      </div>

    <!-- 删除提示框 -->
    <el-dialog title="提示" :visible.sync="delVisible" width="300px" center>
      <div class="del-dialog-cnt" align="center">删除不可恢复，是否确定删除？</div>
      <span slot="footer" class="dialog-footer">
        <el-button size="mini" @click="delVisible = false">取 消</el-button>
        <el-button type="primary" size="mini" @click="deleteRow">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { mixin } from '../mixins'

export default {
  name: 'collect-page',
  mixins: [mixin],
  props: ['id'],
  data () {
    return {
      pageSize: 5, // 页数
      currentPage: 1, // 当前页
      tableData: [], // 记录歌曲，用于显示
      tempDate: [], // 记录歌曲，用于搜索时能临时记录一份歌曲列表
      tempId: [], // 记录列表中歌曲的id
      multipleSelection: [], // 记录要删除的歌曲
      delVisible: false, // 显示删除框
      select_word: '', // 记录输入框输入的内容
      idx: -1, // 记录当前要删除的歌曲
      getCollectByUserIdUrl: 'collect/collectOfUserId',
      delCollectUrl: 'collect/delete',
      getSongByIdUrl: 'song/detail',
      delCollByUserIdSongIdUrl: 'collect/deleteByUserIdSongId'
    }
  },
  watch: {
    select_word: function () {
      if (this.select_word === '') {
        this.tableData = this.tempDate
      } else {
        this.tableData = []
        for (let item of this.tempDate) {
          if (item.songName.includes(this.select_word)) {
            this.tableData.push(item)
          }
        }
      }
    }
  },
  mounted () {
    this.getCommentData()
  },
  computed: {
    // 计算当前表格中的数据
    collectList () {
      return this.tableData.slice((this.currentPage - 1) * this.pageSize, this.currentPage * this.pageSize)
    }
  },
  methods: {
    // 分页导航改变页码大小
    handleSizeChange (size) {
      this.pageSize = size
      this.current = 1
      this.getCommentData()
    },
    // 获取当前页
    handleCurrentChange (val) {
      this.currentPage = val
    },
    // 通过用户id获取用户收藏的歌曲id
    getCommentData () {
      this.getData({userId: this.$route.query.userId}, this.getCollectByUserIdUrl)
        .then(res => {
          this.tableData = []
          for (let item of res.data) {
            this.getSongList(item.songId)
          }
        })
        .catch(err => {
          console.log('getCommentData_err', err)
        })
    },
    // 通过歌曲ID获取歌曲
    getSongList (id) {
      this.getData({songId: id}, this.getSongByIdUrl)
        .then(res => {
          if (res.data.length !== 0) {
            this.tableData.push(res.data)
            this.tempDate.push(res.data)
          }
        })
        .catch(err => {
          console.log('err_getSongList', err)
        })
    },
    // 删除一首歌曲
    deleteRow () {
      var params = {
        userId: this.$route.query.userId,
        songId: this.idx.songId
      }
      this.getData(params, this.delCollByUserIdSongIdUrl)
        .then(res => {
          if (res.data === true) {
            this.getCommentData()
            this.notify('删除成功', 'success')
          } else {
            this.notify('删除失败', 'error')
          }
        })
        .catch(err => {
          console.log('deleteRow_err', err)
        })
      this.delVisible = false
    },
    // 批量删除
    delAll () {
      var i = 0
      var length = this.multipleSelection.length
      for (let item of this.multipleSelection) {
        var params = {
          userId: this.$route.query.userId,
          songId: item.songId
        }
        this.getData(params, this.delCollByUserIdSongIdUrl)
          .then(res => {
            if (res.data === true) {
              i++
              if (i === length) {
                this.notify(i + '条数据删除成功', 'success')
                this.getCommentData()
              }
            } else {
              this.notify('删除失败', 'error')
            }
          })
          .catch(err => {
            console.log('deleteRow_err', err)
          })
      }
      this.multipleSelection = []
    }
  }
}
</script>

<style scoped>
.handle-box {
  margin-bottom: 20px;
}

.handle-input {
  width: 300px;
  display: inline-block;
}

.pagination {
  display: flex;
  justify-content: center;
}
</style>
