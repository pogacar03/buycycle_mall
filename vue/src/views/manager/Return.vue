<template>
  <div>
    <!-- 搜索栏 -->
    <div style="margin: 10px 0">
      <el-input
        v-model="search"
        placeholder="请输入订单号"
        style="width: 200px"
        clearable
      ></el-input>
      <el-select
        v-model="status"
        placeholder="状态"
        style="margin-left: 5px; width: 120px"
        clearable
      >
        <el-option label="待处理" value="pending"></el-option>
        <el-option label="已通过" value="approved"></el-option>
        <el-option label="已拒绝" value="rejected"></el-option>
        <el-option label="已完成" value="completed"></el-option>
      </el-select>
      <el-button type="primary" style="margin-left: 5px" @click="loadData"
        >查询</el-button
      >
      <el-button type="warning" style="margin-left: 5px" @click="reset"
        >重置</el-button
      >
    </div>

    <!-- 列表 -->
    <el-table :data="tableData" stripe style="width: 100%" v-loading="loading">
      <el-table-column
        prop="orderId"
        label="订单编号"
        width="180"
      ></el-table-column>
      <el-table-column prop="goodsName" label="商品名称"></el-table-column>
      <el-table-column label="退换类型" width="100">
        <template slot-scope="scope">
          {{ scope.row.type === "1" ? "退货退款" : "换货" }}
        </template>
      </el-table-column>
      <el-table-column prop="reason" label="退换原因" width="120">
        <template slot-scope="scope">
          {{ reasonMap[scope.row.reason] }}
        </template>
      </el-table-column>
      <el-table-column
        prop="createTime"
        label="申请时间"
        width="180"
      ></el-table-column>
      <el-table-column label="状态" width="100">
        <template slot-scope="scope">
          <el-tag :type="getStatusType(scope.row.status)">
            {{ getStatusText(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template slot-scope="scope">
          <el-button
            type="success"
            size="mini"
            @click="handleApprove(scope.row)"
            v-if="scope.row.status === 'pending'"
            >通过</el-button
          >
          <el-button
            type="danger"
            size="mini"
            @click="handleReject(scope.row)"
            v-if="scope.row.status === 'pending'"
            >拒绝</el-button
          >
          <el-button
            type="info"
            size="mini"
            @click="handleComplete(scope.row)"
            v-if="scope.row.status === 'approved'"
            >完成</el-button
          >
          <el-button type="primary" size="mini" @click="handleDetail(scope.row)"
            >详情</el-button
          >
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div style="margin: 10px 0">
      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="currentPage"
        :page-sizes="[5, 10, 20]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next"
        :total="total"
      >
      </el-pagination>
    </div>

    <!-- 详情对话框 -->
    <el-dialog title="退换货详情" :visible.sync="dialogVisible" width="50%">
      <el-form label-width="100px">
        <el-form-item label="订单编号">
          <span>{{ detail.orderId }}</span>
        </el-form-item>
        <el-form-item label="商品名称">
          <span>{{ detail.goodsName }}</span>
        </el-form-item>
        <el-form-item label="退换类型">
          <span>{{ detail.type === "1" ? "退货退款" : "换货" }}</span>
        </el-form-item>
        <el-form-item label="退换原因">
          <span>{{ reasonMap[detail.reason] }}</span>
        </el-form-item>
        <el-form-item label="问题描述">
          <span>{{ detail.description }}</span>
        </el-form-item>
        <el-form-item
          label="申请金额"
          v-if="detail.type === '1' && detail.amount"
        >
          <span style="color: #ff5000">¥{{ detail.amount }}</span>
        </el-form-item>
        <el-form-item label="图片凭证" v-if="detail.images">
          <el-image
            v-for="(url, index) in detail.images.split(',')"
            :key="index"
            style="width: 100px; height: 100px; margin-right: 10px"
            :src="url"
            :preview-src-list="detail.images.split(',')"
          ></el-image>
        </el-form-item>
      </el-form>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: "Return",
  data() {
    return {
      search: "",
      status: "",
      currentPage: 1,
      pageSize: 10,
      total: 0,
      tableData: [],
      dialogVisible: false,
      detail: {},
      loading: false,
      reasonMap: {
        1: "商品质量问题",
        2: "商品损坏",
        3: "商品与描述不符",
        4: "尺寸不合适",
        5: "其他原因",
      },
    };
  },
  mounted() {
    this.loadData();
  },
  methods: {
    loadData() {
      // 确保获取用户信息
      const user = JSON.parse(localStorage.getItem("xm-user") || "{}");

      // 检查是否有用户ID
      if (!user.id) {
        this.$message.error("请先登录");
        this.$router.push("/login");
        return;
      }

      this.loading = true;
      console.log("用户信息:", user);

      // 准备请求参数和头信息
      const params = {
        pageNum: this.currentPage,
        pageSize: this.pageSize,
        search: this.search,
        status: this.status,
        businessId: user.id, // 添加商家ID作为参数
      };

      const headers = {};
      // 添加token和xm-user到请求头
      const token = localStorage.getItem("token");
      if (token) {
        headers["token"] = token;
        console.log("设置token: " + token);
      }

      // 打印请求参数
      console.log("请求参数:", params);
      console.log("请求头:", headers);

      // 发送请求
      this.$request
        .get("/return/page", {
          params,
          headers,
        })
        .then((res) => {
          this.loading = false;
          console.log("响应数据:", res);

          if (res.code === "200") {
            this.tableData = res.data.records || [];
            this.total = res.data.total || 0;
          } else if (res.code === "401") {
            this.$message.error("登录已过期，请重新登录");
            this.$router.push("/login");
          } else {
            this.$message.error(res.msg || "获取数据失败");
          }
        })
        .catch((err) => {
          this.loading = false;
          console.error("获取退换货数据错误:", err);
          console.error("错误详情:", err.response || err);

          if (err.response && err.response.status === 401) {
            this.$message.error("登录已过期，请重新登录");
            this.$router.push("/login");
          } else {
            this.$message.error(
              "获取数据失败：" + (err.response?.data?.msg || err.message)
            );
          }
        });
    },

    reset() {
      this.search = "";
      this.status = "";
      this.currentPage = 1;
      this.loadData();
    },

    getStatusType(status) {
      const types = {
        pending: "warning",
        approved: "success",
        rejected: "danger",
        completed: "info",
      };
      return types[status] || "info";
    },

    getStatusText(status) {
      const texts = {
        pending: "待处理",
        approved: "已通过",
        rejected: "已拒绝",
        completed: "已完成",
      };
      return texts[status] || "未知状态";
    },

    handleApprove(row) {
      this.$confirm("确认通过该退换申请?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(() => {
          // 获取用户信息
          const user = JSON.parse(localStorage.getItem("xm-user") || "{}");

          // 准备请求参数和头信息
          const params = {
            businessId: user.id, // 添加商家ID作为参数
          };

          const headers = {};
          const token = localStorage.getItem("token");
          if (token) {
            headers["token"] = token;
          }

          // 使用表单数据发送请求
          this.$request
            .post("/return/approve/" + row.id, params, {
              headers,
              params, // 也作为query参数传递
            })
            .then((res) => {
              if (res.code === "200") {
                this.$message.success("操作成功");
                this.loadData();
              } else if (res.code === "401") {
                this.$message.error("登录已过期，请重新登录");
                this.$router.push("/login");
              } else {
                this.$message.error(res.msg || "操作失败");
              }
            })
            .catch((err) => {
              console.error("操作失败:", err);
              if (err.response && err.response.status === 401) {
                this.$message.error("登录已过期，请重新登录");
                this.$router.push("/login");
              } else {
                let errorMsg = "操作失败";
                if (
                  err.response &&
                  err.response.data &&
                  err.response.data.msg
                ) {
                  errorMsg += ": " + err.response.data.msg;
                } else if (err.message) {
                  errorMsg += ": " + err.message;
                }
                this.$message.error(errorMsg);
              }
            });
        })
        .catch(() => {
          // 取消操作
        });
    },

    handleReject(row) {
      this.$confirm("确认拒绝该退换申请?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(() => {
          const headers = {};
          const token = localStorage.getItem("token");
          if (token) {
            headers["token"] = token;
          }

          this.$request
            .post("/return/reject/" + row.id, {}, { headers })
            .then((res) => {
              if (res.code === "200") {
                this.$message.success("操作成功");
                this.loadData();
              } else if (res.code === "401") {
                this.$message.error("登录已过期，请重新登录");
                this.$router.push("/login");
              } else {
                this.$message.error(res.msg || "操作失败");
              }
            })
            .catch((err) => {
              if (err.response && err.response.status === 401) {
                this.$message.error("登录已过期，请重新登录");
                this.$router.push("/login");
              } else {
                this.$message.error(
                  "操作失败：" + (err.response?.data?.msg || err.message)
                );
              }
            });
        })
        .catch(() => {
          // 取消操作
        });
    },

    handleComplete(row) {
      this.$confirm("确认完成该退换申请处理?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(() => {
          const headers = {};
          const token = localStorage.getItem("token");
          if (token) {
            headers["token"] = token;
          }

          this.$request
            .post("/return/complete/" + row.id, {}, { headers })
            .then((res) => {
              if (res.code === "200") {
                this.$message.success("操作成功");
                this.loadData();
              } else if (res.code === "401") {
                this.$message.error("登录已过期，请重新登录");
                this.$router.push("/login");
              } else {
                this.$message.error(res.msg || "操作失败");
              }
            })
            .catch((err) => {
              if (err.response && err.response.status === 401) {
                this.$message.error("登录已过期，请重新登录");
                this.$router.push("/login");
              } else {
                this.$message.error(
                  "操作失败：" + (err.response?.data?.msg || err.message)
                );
              }
            });
        })
        .catch(() => {
          // 取消操作
        });
    },

    handleDetail(row) {
      this.detail = JSON.parse(JSON.stringify(row)); // 深拷贝，避免修改原数据
      this.dialogVisible = true;
    },

    handleSizeChange(val) {
      this.pageSize = val;
      this.loadData();
    },

    handleCurrentChange(val) {
      this.currentPage = val;
      this.loadData();
    },
  },
};
</script>