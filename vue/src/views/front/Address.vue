<template>
  <div class="main-content">
    <!-- 添加导航栏 -->
    <nav-bar></nav-bar>

    <div class="container">
      <div class="address-wrapper">
        <div class="address-header">
          <div class="title">
            <i class="el-icon-location"></i>
            我的收货地址
          </div>
          <div class="actions">
            <el-button type="primary" icon="el-icon-plus" @click="addAddress"
              >添加收货地址</el-button
            >
          </div>
        </div>

        <div class="address-content">
          <div class="table">
            <el-table
              :data="addressData"
              stripe
              class="custom-table"
              v-loading="loading"
            >
              <el-table-column prop="username" label="收货人" width="150">
                <template v-slot="scope">
                  <div class="recipient">
                    <i class="el-icon-user"></i>
                    <span>{{ scope.row.username }}</span>
                  </div>
                </template>
              </el-table-column>

              <el-table-column prop="useraddress" label="收货地址">
                <template v-slot="scope">
                  <div class="address-info">
                    <i class="el-icon-location-outline"></i>
                    <span>{{ scope.row.useraddress }}</span>
                  </div>
                </template>
              </el-table-column>

              <el-table-column prop="phone" label="联系电话" width="180">
                <template v-slot="scope">
                  <div class="phone-info">
                    <i class="el-icon-phone"></i>
                    <span>{{ scope.row.phone }}</span>
                  </div>
                </template>
              </el-table-column>

              <el-table-column label="操作" align="center" width="200">
                <template v-slot="scope">
                  <el-button
                    size="mini"
                    type="primary"
                    icon="el-icon-edit"
                    @click="editAddress(scope.row)"
                    >编辑</el-button
                  >
                  <el-button
                    size="mini"
                    type="danger"
                    icon="el-icon-delete"
                    @click="del(scope.row.id)"
                    >删除</el-button
                  >
                </template>
              </el-table-column>
            </el-table>

            <div class="pagination">
              <el-pagination
                background
                @current-change="handleCurrentChange"
                :current-page="pageNum"
                :page-sizes="[5, 10, 20]"
                :page-size="pageSize"
                layout="total, prev, pager, next"
                :total="total"
              >
              </el-pagination>
            </div>
          </div>
        </div>
      </div>
    </div>

    <el-dialog
      title="地址信息"
      :visible.sync="formVisible"
      width="40%"
      :close-on-click-modal="false"
      destroy-on-close
      class="address-dialog"
    >
      <el-form
        label-width="100px"
        style="padding: 0 20px"
        :model="form"
        :rules="rules"
        ref="formRef"
      >
        <el-form-item prop="username" label="收货人">
          <el-input
            v-model="form.username"
            prefix-icon="el-icon-user"
            placeholder="请输入收货人姓名"
            autocomplete="off"
          ></el-input>
        </el-form-item>
        <el-form-item prop="useraddress" label="收货地址">
          <el-input
            v-model="form.useraddress"
            prefix-icon="el-icon-location-outline"
            placeholder="请输入详细收货地址"
            autocomplete="off"
          ></el-input>
        </el-form-item>
        <el-form-item prop="phone" label="联系电话">
          <el-input
            v-model="form.phone"
            prefix-icon="el-icon-phone"
            placeholder="请输入联系电话"
            autocomplete="off"
          ></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="formVisible = false">取 消</el-button>
        <el-button type="primary" @click="save">保 存</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import NavBar from "@/component/NavBar.vue";

export default {
  components: {
    NavBar,
  },
  data() {
    return {
      user: JSON.parse(localStorage.getItem("xm-user") || "{}"),
      addressData: [],
      pageNum: 1, // 当前的页码
      pageSize: 10, // 每页显示的个数
      total: 0,
      formVisible: false,
      form: {},
      loading: false,
      rules: {
        username: [
          { required: true, message: "请输入收货人", trigger: "blur" },
        ],
        useraddress: [
          { required: true, message: "请输入收货地址", trigger: "blur" },
        ],
        phone: [
          { required: true, message: "请输入联系电话", trigger: "blur" },
          {
            pattern: /^1[3-9]\d{9}$/,
            message: "请输入正确的手机号码",
            trigger: "blur",
          },
        ],
      },
    };
  },
  mounted() {
    this.loadAddress(1);
  },
  // methods：本页面所有的点击事件或者其他函数定义区
  methods: {
    addAddress() {
      this.form = {};
      this.formVisible = true;
    },
    editAddress(row) {
      this.form = JSON.parse(JSON.stringify(row));
      this.formVisible = true;
    },
    save() {
      // 保存按钮触发的逻辑  它会触发新增或者更新
      this.$refs.formRef.validate((valid) => {
        if (valid) {
          this.form.userId = this.user.id;

          // 显示加载状态
          const loading = this.$loading({
            lock: true,
            text: "保存中...",
            spinner: "el-icon-loading",
            background: "rgba(0, 0, 0, 0.7)",
          });

          this.$request({
            url: this.form.id ? "/address/update" : "/address/add",
            method: this.form.id ? "PUT" : "POST",
            data: this.form,
          })
            .then((res) => {
              loading.close();
              if (res.code === "200") {
                // 表示成功保存
                this.$message.success("保存成功");
                this.loadAddress(1);
                this.formVisible = false;
              } else {
                this.$message.error(res.msg); // 弹出错误的信息
              }
            })
            .catch((error) => {
              loading.close();
              this.$message.error("操作失败，请重试");
              console.error(error);
            });
        }
      });
    },
    loadAddress(pageNum) {
      if (pageNum) this.pageNum = pageNum;

      // 显示加载状态
      this.loading = true;

      this.$request
        .get("/address/selectPage", {
          params: {
            pageNum: this.pageNum,
            pageSize: this.pageSize,
          },
        })
        .then((res) => {
          this.loading = false;
          if (res.code === "200") {
            this.addressData = res.data?.list || [];
            this.total = res.data?.total || 0;
          } else {
            this.$message.error(res.msg);
          }
        })
        .catch((error) => {
          this.loading = false;
          this.$message.error("加载地址数据失败");
          console.error(error);
        });
    },
    navTo(url) {
      location.href = url;
    },
    del(id) {
      this.$confirm("确定要删除这个地址吗?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(() => {
          const loading = this.$loading({
            lock: true,
            text: "删除中...",
            spinner: "el-icon-loading",
            background: "rgba(0, 0, 0, 0.7)",
          });

          this.$request
            .delete("/address/delete/" + id)
            .then((res) => {
              loading.close();
              if (res.code === "200") {
                this.$message.success("删除成功");
                this.loadAddress(1);
              } else {
                this.$message.error(res.msg);
              }
            })
            .catch((error) => {
              loading.close();
              this.$message.error("删除失败，请重试");
              console.error(error);
            });
        })
        .catch(() => {
          // 取消删除操作
        });
    },
    handleCurrentChange(pageNum) {
      this.loadAddress(pageNum);
    },
  },
};
</script>

<style scoped>
.main-content {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  padding-bottom: 40px;
}

.container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 20px;
}

.address-wrapper {
  background: rgba(255, 255, 255, 0.9);
  border-radius: 15px;
  box-shadow: 0 8px 32px rgba(31, 38, 135, 0.15);
  backdrop-filter: blur(4px);
  padding: 20px;
  margin-top: 20px;
}

.address-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  height: 80px;
  border-bottom: 1px solid #e0e0e0;
}

.title {
  font-size: 18px;
  font-weight: bold;
  color: #333;
  display: flex;
  align-items: center;
}

.title i {
  margin-right: 10px;
  font-size: 22px;
  color: #409eff;
}

.address-content {
  padding: 20px;
}

.custom-table {
  border-radius: 8px;
  overflow: hidden;
}

.recipient,
.address-info,
.phone-info {
  display: flex;
  align-items: center;
}

.recipient i,
.address-info i,
.phone-info i {
  margin-right: 8px;
  font-size: 16px;
  color: #409eff;
}

.pagination {
  margin-top: 30px;
  display: flex;
  justify-content: center;
}

/* Override element-ui styles */
.el-button--primary {
  background-color: #409eff;
  border-color: #409eff;
}

.el-button--danger {
  background-color: #f56c6c;
  border-color: #f56c6c;
}

/* Form styles */
.address-dialog .el-input__inner {
  border-radius: 4px;
}

.address-dialog .el-form-item {
  margin-bottom: 20px;
}

@media (max-width: 768px) {
  .container {
    padding: 10px;
  }

  .address-wrapper {
    padding: 10px;
  }

  .address-header {
    flex-direction: column;
    height: auto;
    padding: 15px;
  }

  .title {
    margin-bottom: 15px;
  }
}
</style>