<template>
  <div class="main-content">
    <nav-bar></nav-bar>

    <div class="product-container">
      <div class="product-details">
        <el-row :gutter="20">
          <el-col :span="12">
            <div class="product-image-container">
              <img :src="goodsData.img" alt="" class="product-image" />
            </div>
          </el-col>
          <el-col :span="12">
            <div class="product-info">
              <h1 class="product-name">{{ goodsData.name }}</h1>

              <div class="product-meta">
                <div class="meta-item">
                  <i class="el-icon-shopping-cart-2"></i>
                  <span>销量: {{ goodsData.count || 0 }}</span>
                </div>
                <div
                  class="meta-item stock-indicator"
                  :class="{ 'low-stock': goodsData.stock < 5 }"
                >
                  <i class="el-icon-goods"></i>
                  <span>库存: {{ goodsData.stock || 0 }}</span>
                  <span v-if="goodsData.stock < 10" class="stock-warning"
                    >库存紧张</span
                  >
                </div>
              </div>

              <div class="product-price">
                <span class="price-label">价格:</span>
                <span class="price-value"
                  >¥{{ goodsData.price }} / {{ goodsData.unit }}</span
                >
              </div>

              

              <div class="product-tags">
                <div class="tag-item">
                  <span class="tag-label">商家:</span>
                  <a
                    href="#"
                    @click="navTo('/front/business?id=' + goodsData.businessId)"
                    class="tag-link"
                  >
                    {{ goodsData.businessName }}
                  </a>
                </div>
                <div class="tag-item">
                  <span class="tag-label">分类:</span>
                  <a
                    href="#"
                    @click="navTo('/front/type?id=' + goodsData.typeId)"
                    class="tag-link"
                  >
                    {{ goodsData.typeName }}
                  </a>
                </div>
              </div>

              <div class="product-actions">
                <el-button
                  type="primary"
                  icon="el-icon-shopping-cart-2"
                  @click="addCart"
                  >加入购物车</el-button
                >
                <el-button
                  type="danger"
                  icon="el-icon-star-off"
                  @click="collect"
                  >收藏</el-button
                >
              </div>
            </div>
          </el-col>
        </el-row>
      </div>

      <div class="product-tabs">
        <el-tabs v-model="activeName" @tab-click="handleClick">
          <el-tab-pane label="商品详情" name="first">
            <div
              class="product-description"
              v-html="goodsData.description"
            ></div>
          </el-tab-pane>
          <el-tab-pane label="用户评价" name="second">
            <div class="comment-section">
              <div
                class="comment-item"
                v-for="(item, index) in commentData"
                :key="index"
              >
                <div class="comment-header">
                  <div class="user-avatar">
                    <img :src="item.userAvatar" alt="用户头像" />
                  </div>
                  <div class="user-info">
                    <div class="user-name">{{ item.userName }}</div>
                    <div class="comment-time">{{ item.time }}</div>
                  </div>
                </div>
                <div class="comment-content">
                  {{ item.content }}
                </div>
              </div>
              <div v-if="commentData.length === 0" class="no-comments">
                暂无评价，购买后来做第一个评价吧~
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>
  </div>
</template>

<script>
import NavBar from "@/component/NavBar.vue";

export default {
  components: {
    NavBar,
  },
  data() {
    let goodsId = this.$route.query.id;
    return {
      user: JSON.parse(localStorage.getItem("xm-user") || "{}"),
      goodsId: goodsId,
      goodsData: {},
      activeName: "first",
      commentData: [],
      quantity: 1,
    };
  },
  mounted() {
    this.loadGoods();
    this.loadComments();
  },
  // methods：本页面所有的点击事件或者其他函数定义区
  methods: {
    loadGoods() {
      this.$request.get("/goods/selectById?id=" + this.goodsId).then((res) => {
        if (res.code === "200") {
          this.goodsData = res.data;
        } else {
          this.$message.error(res.msg);
        }
      });
    },
    handleClick(tab, event) {
      this.activeName = tab.name;
    },
    collect() {
      if (!this.user.id) {
        this.$message.warning("请先登录");
        return;
      }

      let data = {
        userId: this.user.id,
        businessId: this.goodsData.businessId,
        goodsId: this.goodsId,
      };
      this.$request.post("/collect/add", data).then((res) => {
        if (res.code === "200") {
          this.$message.success("收藏成功");
        } else {
          this.$message.error(res.msg);
        }
      });
    },
    addCart() {
      if (!this.user.id) {
        this.$message.warning("请先登录");
        return;
      }

      // 检查库存
      if (this.quantity > this.goodsData.stock) {
        this.$message.error(`库存不足，当前库存: ${this.goodsData.stock}`);
        return;
      }

      let data = {
        num: 1,
        userId: this.user.id,
        goodsId: this.goodsId,
        businessId: this.goodsData.businessId,
      };
      this.$request.post("/cart/add", data).then((res) => {
        if (res.code === "200") {
          this.$message.success("已添加到购物车");
        } else {
          this.$message.error(res.msg);
        }
      });
    },
    navTo(url) {
      location.href = url;
    },
    loadComments() {
      this.$request
        .get("/comment/selectByGoodsId?id=" + this.goodsId)
        .then((res) => {
          if (res.code === "200") {
            this.commentData = res.data;
          } else {
            this.$message.error(res.msg);
          }
        });
    },
  },
};
</script>

<style scoped>
.main-content {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 40px;
}

.product-container {
  width: 80%;
  max-width: 1200px;
  margin: 20px auto;
  background-color: white;
  border-radius: 12px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.product-details {
  padding: 24px;
}

.product-image-container {
  width: 100%;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.product-image {
  width: 100%;
  height: 400px;
  object-fit: cover;
  transition: transform 0.3s;
}

.product-image:hover {
  transform: scale(1.02);
}

.product-info {
  padding: 0 16px;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.product-name {
  font-size: 22px;
  font-weight: bold;
  margin: 0 0 16px 0;
  line-height: 1.4;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.product-meta {
  display: flex;
  flex-wrap: wrap;
  margin-bottom: 16px;
}

.meta-item {
  display: flex;
  align-items: center;
  margin-right: 24px;
  margin-bottom: 8px;
  color: #666;
  font-size: 14px;
}

.meta-item i {
  margin-right: 6px;
  font-size: 16px;
}

.stock-indicator {
  padding: 4px 8px;
  border-radius: 4px;
  background-color: #f0f9eb;
  color: #67c23a;
}

.low-stock {
  background-color: #fef0f0;
  color: #f56c6c;
}

.stock-warning {
  margin-left: 8px;
  font-size: 12px;
  background-color: #f56c6c;
  color: white;
  padding: 2px 6px;
  border-radius: 4px;
}

.product-price {
  margin: 16px 0;
  padding: 12px;
  background-color: #fff9f9;
  border-radius: 6px;
}

.price-label {
  font-size: 14px;
  color: #666;
  margin-right: 8px;
}

.price-value {
  font-size: 24px;
  font-weight: bold;
  color: #f56c6c;
}

.product-promo {
  margin: 16px 0;
}

.promo-image {
  width: 100%;
  max-height: 100px;
  object-fit: cover;
  border-radius: 6px;
}

.product-tags {
  margin: 12px 0;
}

.tag-item {
  margin-bottom: 8px;
  font-size: 14px;
}

.tag-label {
  color: #666;
  margin-right: 8px;
}

.tag-link {
  color: #409eff;
  text-decoration: none;
}

.tag-link:hover {
  text-decoration: underline;
}

.product-actions {
  margin-top: 24px;
  display: flex;
  gap: 16px;
}

.product-tabs {
  padding: 0 24px 24px;
}

.product-description {
  padding: 20px;
  line-height: 1.6;
  color: #333;
}

.comment-section {
  padding: 16px;
}

.comment-item {
  padding: 16px;
  border-bottom: 1px solid #eee;
  margin-bottom: 16px;
}

.comment-header {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  overflow: hidden;
  margin-right: 12px;
}

.user-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.user-info {
  flex: 1;
}

.user-name {
  font-weight: bold;
  font-size: 16px;
  color: #333;
}

.comment-time {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}

.comment-content {
  font-size: 14px;
  line-height: 1.6;
  color: #666;
}

.no-comments {
  text-align: center;
  padding: 30px;
  color: #999;
  font-size: 14px;
}

@media (max-width: 768px) {
  .product-container {
    width: 95%;
  }

  .product-actions {
    flex-direction: column;
  }
}
</style>