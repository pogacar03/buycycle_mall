<template>
  <div class="main-content">
    <div class="success-container">
      <div class="success-icon">
        <i class="el-icon-check"></i>
      </div>
      <h2>支付成功</h2>
      <p>订单号: {{ orderId }}</p>
      <p>感谢您的购买!</p>
      <div class="buttons">
        <el-button type="primary" @click="viewOrder">查看订单</el-button>
        <el-button @click="backToHome">返回首页</el-button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      orderId: ''
    };
  },
  created() {
    // 从URL参数中获取订单号
    this.orderId = this.$route.query.orderId || '';
  },
  methods: {
    viewOrder() {
      this.$router.push('/front/orders');
    },
    backToHome() {
      this.$router.push('/front/home');
    }
  }
};
</script>

<style scoped>
.main-content {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  padding: 20px 0;
}
.success-container {
  width: 500px;
  margin: 100px auto;
  text-align: center;
  padding: 30px;
  border-radius: 4px;
  background-color: white;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}
.success-icon {
  font-size: 80px;
  color: #67C23A;
  margin-bottom: 20px;
}
.buttons {
  margin-top: 30px;
}
</style>