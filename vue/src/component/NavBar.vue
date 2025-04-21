<template>
  <div class="nav-bar">
    <div class="nav-container">
      <div class="left-nav">
        <el-button type="text" icon="el-icon-s-home" @click="goToHome"
          >首页</el-button
        >
      </div>
      <div class="right-nav">
        <el-button type="text" icon="el-icon-shopping-cart-2" @click="goToCart">
          购物车
        </el-button>
        <el-divider direction="vertical"></el-divider>
        <el-button type="text" icon="el-icon-star-off" @click="goToFavorites"
          >我的收藏</el-button
        >
        <el-divider direction="vertical"></el-divider>
        <el-button type="text" icon="el-icon-s-order" @click="goToOrders"
          >我的订单</el-button
        >
        <el-divider direction="vertical"></el-divider>
        <el-button type="text" icon="el-icon-user" @click="goToUser"
          >个人中心</el-button
        >
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "NavBar",
  data() {
    return {
      user: JSON.parse(localStorage.getItem("xm-user") || "{}"),
    };
  },
  methods: {
    // 路由跳转方法
    goToHome() {
      window.location.href = "/front/home";
    },
    goToCart() {
      window.location.href = "/front/cart";
    },
    goToFavorites() {
      window.location.href = "/front/collect";
    },
    goToOrders() {
      window.location.href = "/front/orders";
    },
    goToUser() {
      window.location.href = "/front/person";
    },
  },
};
</script>

<style scoped>
.nav-bar {
  width: 100%;
  background-color: #f5f5f5;
  padding: 10px 0;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 100;
}

.nav-container {
  width: 80%;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.left-nav,
.right-nav {
  display: flex;
  align-items: center;
}

.el-button {
  margin: 0 5px;
  font-size: 14px;
}

.el-divider--vertical {
  height: 20px;
  margin: 0 8px;
}
</style> 