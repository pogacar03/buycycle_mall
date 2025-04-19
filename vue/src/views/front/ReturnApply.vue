<template>
  <div class="main-content">
    <div
      style="
        width: 70%;
        background-color: white;
        margin: 30px auto;
        border-radius: 20px;
        padding: 20px;
      "
    >
      <div class="return-header">
        <div class="title">申请退换货</div>
        <div class="order-info">
          <span>订单号：{{ orderId }}</span>
        </div>
      </div>

      <!-- 商品信息 -->
      <div class="goods-info" v-if="goods.img">
        <img :src="goods.img" :alt="goods.name" />
        <div class="goods-detail">
          <div class="goods-name">{{ goods.name }}</div>
          <div class="goods-price">￥{{ goods.price }}</div>
        </div>
      </div>
      <div v-else class="loading-info">正在加载商品信息...</div>

      <!-- 退货表单 -->
      <el-form
        :model="returnForm"
        :rules="rules"
        ref="returnForm"
        label-width="100px"
        v-loading="loading"
      >
        <el-form-item label="退换类型" prop="type">
          <el-radio-group v-model="returnForm.type">
            <el-radio label="1">退货退款</el-radio>
            <el-radio label="2">换货</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="退换原因" prop="reason">
          <el-select
            v-model="returnForm.reason"
            placeholder="请选择退换原因"
            style="width: 100%"
          >
            <el-option label="商品质量问题" value="1"></el-option>
            <el-option label="商品损坏" value="2"></el-option>
            <el-option label="商品与描述不符" value="3"></el-option>
            <el-option label="尺寸不合适" value="4"></el-option>
            <el-option label="其他原因" value="5"></el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="问题描述" prop="description">
          <el-input
            type="textarea"
            v-model="returnForm.description"
            :rows="4"
            placeholder="请详细描述商品问题..."
            :maxlength="500"
            show-word-limit
          ></el-input>
        </el-form-item>

        <el-form-item label="上传凭证">
          <el-upload
            :action="uploadUrl"
            list-type="picture-card"
            :on-success="handleUploadSuccess"
            :on-error="handleUploadError"
            :before-upload="beforeUpload"
            :headers="headers"
            :file-list="fileList"
            :on-remove="handleRemove"
          >
            <i class="el-icon-plus"></i>
          </el-upload>
          <div class="upload-tip">最多上传3张图片，每张不超过2M</div>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="submitForm" :loading="submitting">
            {{ submitting ? "提交中..." : "提交申请" }}
          </el-button>
          <el-button @click="goBack">返回</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
export default {
  name: "ReturnApply",
  data() {
    return {
      orderId: "",
      goods: {
        id: "",
        name: "",
        img: "",
        price: 0,
      },
      returnForm: {
        type: "1",
        reason: "",
        description: "",
        images: [],
      },
      uploadUrl: "/api/upload",
      headers: {
        Authorization: localStorage.getItem("token"),
      },
      fileList: [],
      loading: false,
      submitting: false,
      rules: {
        type: [{ required: true, message: "请选择退换类型" }],
        reason: [{ required: true, message: "请选择退换原因" }],
        description: [
          { required: true, message: "请填写问题描述" },
          { min: 10, max: 500, message: "描述长度应在10-500字符之间" },
        ],
      },
    };
  },
  created() {
    console.log("ReturnApply 组件创建，路由参数:", this.$route.query);

    // 直接从路由参数读取基本信息
    const query = this.$route.query;
    this.orderId = query.orderId;

    // 首先尝试从路由参数直接填充商品信息
    if (query.goodsId && query.goodsName && query.goodsImg) {
      this.goods = {
        id: query.goodsId,
        name: query.goodsName,
        img: query.goodsImg,
        price: query.goodsPrice || 0,
      };
      console.log("已从路由参数加载商品信息:", this.goods);
    }

    // 检查必要数据
    if (!this.orderId) {
      this.$message.error("缺少订单信息");
      this.goBack();
      return;
    }

    // 如果还缺少商品信息，再从后端加载
    if (!this.goods.id || !this.goods.name) {
      this.loadOrderDetail();
    }
  },
  methods: {
    loadOrderDetail() {
      this.loading = true;
      console.log("从服务器加载订单详情，orderId:", this.orderId);

      this.$request
        .get("/orders/detail/" + this.orderId)
        .then((res) => {
          console.log("返回数据:", res);
          if (res.code === "200" && res.data) {
            // 处理返回的订单数据
            this.goods = {
              id: res.data.goodsId,
              name: res.data.goodsName,
              img: res.data.goodsImg,
              price: res.data.goodsPrice,
            };
            console.log("已加载商品详情:", this.goods);
          } else {
            this.$message.error(
              res.msg || "获取订单详情失败，错误码:" + res.code
            );
            console.error("订单详情API返回错误:", res);
            this.goBack();
          }
        })
        .catch((err) => {
          console.error("加载订单详情错误:", err);
          this.$message.error(
            "获取订单详情失败：" + (err.response?.data?.msg || err.message)
          );
          this.goBack();
        })
        .finally(() => {
          this.loading = false;
        });
    },

    handleUploadSuccess(res, file) {
      if (res.code === "200") {
        this.returnForm.images.push(res.data);
        this.fileList.push({
          name: file.name,
          url: res.data,
        });
        this.$message.success("图片上传成功");
      } else {
        this.$message.error(res.msg || "上传失败");
      }
    },

    handleUploadError(err) {
      this.$message.error("图片上传失败：" + err.message);
    },

    handleRemove(file) {
      const index = this.fileList.indexOf(file);
      if (index !== -1) {
        this.fileList.splice(index, 1);
        this.returnForm.images.splice(index, 1);
      }
    },

    beforeUpload(file) {
      const isImage = file.type.startsWith("image/");
      const isLt2M = file.size / 1024 / 1024 < 2;

      if (!isImage) {
        this.$message.error("只能上传图片文件！");
        return false;
      }
      if (!isLt2M) {
        this.$message.error("图片大小不能超过 2MB！");
        return false;
      }
      if (this.fileList.length >= 3) {
        this.$message.error("最多只能上传3张图片！");
        return false;
      }
      return true;
    },

    submitForm() {
      if (this.submitting) return;

      this.$refs.returnForm.validate((valid) => {
        if (valid) {
          if (!this.goods.id || !this.goods.name) {
            this.$message.error("商品信息不完整");
            return;
          }

          // 检查用户是否登录
          const user = JSON.parse(localStorage.getItem("xm-user") || "{}");
          if (!user.id) {
            this.$message.error("请先登录");
            this.$router.push("/login");
            return;
          }

          this.submitting = true;

          // 使用JSON对象
          const data = {
            orderId: this.orderId,
            goodsId: this.goods.id,
            goodsName: this.goods.name,
            type: this.returnForm.type,
            reason: this.returnForm.reason,
            description: this.returnForm.description,
            userId: user.id, // 添加用户ID
          };

          // 退款才添加金额
          if (this.returnForm.type === "1") {
            data.amount = this.goods.price;
          }

          console.log("发送数据:", data);

          // 添加token到请求头
          const headers = {};
          const token = localStorage.getItem("token");
          if (token) {
            headers["token"] = token;
          }

          // 使用JSON方式发送请求
          this.$request
            .post("/return/apply", data, { headers })
            .then((res) => {
              if (res.code === "200") {
                this.$message.success("申请提交成功");
                this.$router.push("/front/orders");
                // 发送刷新事件
                this.$bus.$emit("refreshOrders");
              } else if (res.code === "401") {
                this.$message.error("登录已过期，请重新登录");
                this.$router.push("/login");
              } else {
                this.$message.error(res.msg || "提交失败");
              }
            })
            .catch((err) => {
              console.error("提交错误:", err);
              if (err.response && err.response.status === 401) {
                this.$message.error("登录已过期，请重新登录");
                this.$router.push("/login");
              } else {
                this.$message.error(
                  "提交失败：" + (err.response?.data?.msg || err.message)
                );
              }
            })
            .finally(() => {
              this.submitting = false;
            });
        }
      });
    },

    goBack() {
      this.$router.push("/front/orders");
    },
  },
};
</script>

<style scoped>
.main-content {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  padding: 20px 0;
}

.return-header {
  border-bottom: 1px solid #eee;
  padding-bottom: 15px;
  margin-bottom: 20px;
}

.title {
  font-size: 20px;
  font-weight: bold;
  margin-bottom: 10px;
}

.order-info {
  color: #666;
  font-size: 14px;
}

.goods-info {
  display: flex;
  padding: 15px;
  background: #f8f8f8;
  border-radius: 8px;
  margin-bottom: 20px;
}

.loading-info {
  text-align: center;
  padding: 20px;
  color: #909399;
}

.goods-info img {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 4px;
}

.goods-detail {
  margin-left: 15px;
  flex: 1;
}

.goods-name {
  font-size: 16px;
  margin-bottom: 10px;
  color: #303133;
}

.goods-price {
  color: #ff5000;
  font-weight: bold;
}

.upload-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}

.el-upload--picture-card {
  width: 100px;
  height: 100px;
  line-height: 100px;
}

.el-upload-list--picture-card .el-upload-list__item {
  width: 100px;
  height: 100px;
}
</style>