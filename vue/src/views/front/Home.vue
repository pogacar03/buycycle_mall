<template>
  <div class="main-content">
    <div class="main-container">
      <div class="content-wrapper">
        <!-- 主题市场标题 -->
        <div class="section-title">
          <i class="el-icon-shopping-bag-1"></i>
          主题市场
        </div>

        <div class="main-content-area">
          <!-- 左侧分类菜单 -->
          <div class="category-menu">
            <div class="category-item" v-for="item in typeData" :key="item.id" @click="navTo('/front/type?id=' + item.id)">
              <span>{{item.name}}</span>
            </div>
          </div>

          <!-- 中间轮播区域 -->
          <div class="carousel-section">
            <!-- 顶部大轮播图 -->
            <el-carousel height="300px" class="main-carousel" indicator-position="outside">
              <el-carousel-item v-for="(item, index) in carousel_top" :key="index">
                <img :src="item" alt="carousel">
              </el-carousel-item>
            </el-carousel>
            
            <!-- 下方小轮播图 -->
            <div class="sub-carousel-container">
              <el-carousel height="200px" class="sub-carousel" :interval="4000">
                <el-carousel-item v-for="(item, index) in carousel_left" :key="index">
                  <img :src="item" alt="carousel">
                </el-carousel-item>
              </el-carousel>
              <el-carousel height="200px" class="sub-carousel" :interval="4500">
                <el-carousel-item v-for="(item, index) in carousel_right" :key="index">
                  <img :src="item" alt="carousel">
                </el-carousel-item>
              </el-carousel>
            </div>
          </div>

          <!-- 右侧用户信息区 -->
          <div class="user-panel">
            <div class="user-info" v-if="user.username">
              <img @click="navTo('/front/person')" :src="user.avatar" :alt="user.name">
              <div class="welcome">Hi，{{user.name}}</div>
              <el-button type="text" class="logout-btn" @click="handleLogout">
                <i class="el-icon-switch-button"></i> 退出登录
              </el-button>
            </div>
            <div class="user-info" v-else>
              <div class="login-tips">
                <el-button type="primary" @click="navTo('/login')">登录</el-button>
                <el-button @click="navTo('/register')">注册</el-button>
              </div>
            </div>

            <div class="promo-banner">
              <img src="@/assets/imgs/right.png" alt="promotion">
            </div>

            <div class="quick-actions">
              <div class="action-item" @click="navTo('/front/collect')">
                <i class="el-icon-star-off"></i>
                <span>我的收藏</span>
              </div>
              <div class="action-item" @click="navTo('/front/address')">
                <i class="el-icon-location"></i>
                <span>我的地址</span>
              </div>
              <div class="action-item" @click="navTo('/front/cart')">
                <i class="el-icon-shopping-cart-1"></i>
                <span>购物车</span>
              </div>
              <div class="action-item" @click="navTo('/front/orders')">
                <i class="el-icon-document"></i>
                <span>我的订单</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 热卖商品区域 -->
        <div class="section-block">
          <div class="section-header">
            <i class="el-icon-hot-water"></i>
            热卖商品
          </div>
          <div class="goods-grid">
            <div v-for="item in goodsData" :key="item.id" class="goods-card" @click="navTo('/front/detail?id=' + item.id)">
              <div class="goods-img">
                <img :src="item.img" :alt="item.name">
              </div>
              <h3>{{item.name}}</h3>
              <div class="price">￥{{item.price}} / {{item.unit}}</div>
            </div>
          </div>
        </div>

        <!-- 猜你喜欢区域 -->
        <div class="section-block">
          <div class="section-header">
            <i class="el-icon-magic-stick"></i>
            猜你喜欢
          </div>
          <div class="goods-grid">
            <div v-for="item in recommendData" :key="item.id" class="goods-card" @click="navTo('/front/detail?id=' + item.id)">
              <div class="goods-img">
                <img :src="item.img" :alt="item.name">
              </div>
              <h3>{{item.name}}</h3>
              <div class="price">￥{{item.price}} / {{item.unit}}</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Home',
  data() {
    return {
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      typeData: [],
      goodsData: [],
      recommendData: [],
      carousel_top: [
        require('@/assets/imgs/carousel-1.png'),
        require('@/assets/imgs/carousel-2.png'),
        require('@/assets/imgs/carousel-9.png'),
      ],
      carousel_left: [
        require('@/assets/imgs/carousel-3.png'),
        require('@/assets/imgs/carousel-4.png'),
        require('@/assets/imgs/carousel-5.png'),
      ],
      carousel_right: [
        require('@/assets/imgs/carousel-6.png'),
        require('@/assets/imgs/carousel-7.png'),
        require('@/assets/imgs/carousel-8.png'),
      ],
    }
  },
  mounted() {
    this.loadType()
    this.loadGoods()
    this.loadRecommend()
  },
  methods: {
    loadRecommend() {
      this.$request.get('/goods/recommend').then(res => {
        if (res.code === '200') {
          this.recommendData = res.data
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    loadType() {
      this.$request.get('/type/selectAll').then(res => {
        if (res.code === '200') {
          this.typeData = res.data
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    loadGoods() {
      this.$request.get('/goods/selectTop15').then(res => {
        if (res.code === '200') {
          this.goodsData = res.data
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    handleLogout() {
      this.$confirm('确认退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        localStorage.removeItem('xm-user')
        this.$message.success('退出成功')
        this.navTo('/login')
      }).catch(() => {})
    },
    navTo(url) {
      location.href = url
    }
  }
}
</script>
<style scoped>
.main-content {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  padding-bottom: 40px;
}

.main-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 20px;
}

.content-wrapper {
  background: rgba(255, 255, 255, 0.9);
  border-radius: 15px;
  box-shadow: 0 8px 32px rgba(31, 38, 135, 0.15);
  backdrop-filter: blur(4px);
  padding: 25px;
  margin-top: 20px;
}

.section-title {
  color: #2c3e50;
  font-weight: bold;
  font-size: 20px;
  margin-bottom: 25px;
  display: flex;
  align-items: center;
  gap: 10px;
  padding-bottom: 10px;
  border-bottom: 2px solid #e8e8e8;
}

.main-content-area {
  display: flex;
  gap: 25px;
  margin-bottom: 30px;
}

.category-menu {
  width: 220px;
  background: white;
  border-radius: 12px;
  padding: 15px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.category-item {
  padding: 12px 15px;
  cursor: pointer;
  transition: all 0.3s;
  border-radius: 8px;
  color: #2c3e50;
  margin-bottom: 5px;
}

.category-item:hover {
  background: #f0f2f5;
  color: #04BF04;
}

.carousel-section {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.main-carousel {
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.main-carousel :deep(.el-carousel__item) img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.sub-carousel-container {
  display: flex;
  gap: 20px;
}

.sub-carousel {
  flex: 1;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.sub-carousel :deep(.el-carousel__item) img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.user-panel {
  width: 280px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.user-info {
  background: white;
  border-radius: 12px;
  padding: 20px;
  text-align: center;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.user-info img {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  margin-bottom: 10px;
  cursor: pointer;
  transition: transform 0.3s;
}

.user-info img:hover {
  transform: scale(1.05);
}

.welcome {
  font-size: 16px;
  color: #2c3e50;
  margin-bottom: 15px;
}

.login-tips {
  display: flex;
  gap: 10px;
  justify-content: center;
}

.logout-btn {
  color: #ff4757;
}

.promo-banner {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.promo-banner img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.quick-actions {
  background: white;
  border-radius: 12px;
  padding: 15px;
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 15px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 15px;
  cursor: pointer;
  border-radius: 8px;
  transition: all 0.3s;
}

.action-item:hover {
  background: #f0f2f5;
  color: #04BF04;
}

.action-item i {
  font-size: 24px;
}

.section-block {
  background: white;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 25px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.section-header {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 18px;
  color: #2c3e50;
  font-weight: bold;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #e8e8e8;
}

.goods-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 20px;
}

.goods-card {
  background: white;
  border-radius: 8px;
  overflow: hidden;
  transition: all 0.3s;
  cursor: pointer;
  border: 1px solid #eee;
}

.goods-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
}

.goods-img {
  position: relative;
  padding-top: 100%;
}

.goods-img img {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.goods-card h3 {
  margin: 10px;
  font-size: 14px;
  color: #2c3e50;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.price {
  margin: 10px;
  color: #ff5000;
  font-size: 16px;
  font-weight: bold;
}

@media (max-width: 1200px) {
  .main-content-area {
    flex-direction: column;
  }
  
  .category-menu {
    width: 100%;
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
    gap: 10px;
  }
  
  .user-panel {
    width: 100%;
  }
  
  .quick-actions {
    grid-template-columns: repeat(4, 1fr);
  }
}

@media (max-width: 768px) {
  .sub-carousel-container {
    flex-direction: column;
  }
  
  .quick-actions {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .goods-grid {
    grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  }
}
</style>