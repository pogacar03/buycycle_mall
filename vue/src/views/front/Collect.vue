<template>
  <div class="main-content">
    <!-- 添加导航栏 -->
    <nav-bar></nav-bar>
    <div class="container">
      <div class="collect-wrapper">
        <div class="collect-header">
          <div class="title">
            <i class="el-icon-star-off"></i>
            我的收藏（{{ collectData.length }}件）
          </div>
        </div>

        <div class="collect-content">
          <div class="table">
            <el-table :data="collectData" stripe class="custom-table">
              <el-table-column label="商品图片" width="120px">
                <template v-slot="scope">
                  <el-image
                    class="goods-img"
                    v-if="scope.row.goodsImg"
                    :src="scope.row.goodsImg"
                    :preview-src-list="[scope.row.goodsImg]"
                  ></el-image>
                </template>
              </el-table-column>
              <el-table-column prop="goodsName" label="商品名称" width="350px">
                <template v-slot="scope">
                  <a
                    :href="'/front/detail?id=' + scope.row.goodsId"
                    class="goods-link"
                  >
                    {{ scope.row.goodsName }}
                  </a>
                </template>
              </el-table-column>
              <el-table-column prop="businessName" label="店铺名称">
                <template v-slot="scope">
                  <a
                    :href="'/front/business?id=' + scope.row.businessId"
                    class="shop-link"
                  >
                    {{ scope.row.businessName }}
                  </a>
                </template>
              </el-table-column>
              <el-table-column prop="goodsPrice" label="商品价格">
                <template v-slot="scope">
                  <span class="price">¥{{ scope.row.goodsPrice }}</span>
                </template>
              </el-table-column>
              <el-table-column label="操作" align="center" width="180">
                <template v-slot="scope">
                  <el-button
                    size="mini"
                    type="danger"
                    plain
                    @click="del(scope.row.id)"
                    class="remove-btn"
                    >移除收藏</el-button
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
      collectData: [],
      pageNum: 1, // 当前的页码
      pageSize: 10, // 每页显示的个数
      total: 0,
    };
  },
  mounted() {
    this.loadCollect(1);
  },
  // methods：本页面所有的点击事件或者其他函数定义区
  methods: {
    loadCollect(pageNum) {
      if (pageNum) this.pageNum = pageNum;
      this.$request
        .get("/collect/selectPage", {
          params: {
            pageNum: this.pageNum,
            pageSize: this.pageSize,
          },
        })
        .then((res) => {
          if (res.code === "200") {
            this.collectData = res.data?.list;
            this.total = res.data?.total;
          } else {
            this.$message.error(res.msg);
          }
        });
    },
    navTo(url) {
      location.href = url;
    },
    del(id) {
      this.$request.delete("/collect/delete/" + id).then((res) => {
        if (res.code === "200") {
          this.$message.success("移除成功");
          this.loadCollect(1);
        } else {
          this.$message.error(res.msg);
        }
      });
    },
    handleCurrentChange(pageNum) {
      this.loadCollect(pageNum);
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
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.collect-wrapper {
  background: rgba(255, 255, 255, 0.9);
  border-radius: 15px;
  box-shadow: 0 8px 32px rgba(31, 38, 135, 0.15);
  backdrop-filter: blur(4px);
  padding: 20px;
  margin-top: 20px;
}

.collect-header {
  display: flex;
  align-items: center;
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
  color: #ffb800;
}

.collect-content {
  padding: 20px;
}

.custom-table {
  border-radius: 8px;
  overflow: hidden;
}

.goods-img {
  width: 80px;
  height: 60px;
  border-radius: 6px;
  object-fit: cover;
  transition: transform 0.3s;
}

.goods-img:hover {
  transform: scale(1.05);
}

.goods-link,
.shop-link {
  color: #333;
  text-decoration: none;
  transition: color 0.3s;
}

.goods-link:hover,
.shop-link:hover {
  color: #ff5000;
}

.price {
  font-weight: bold;
  color: #ff5000;
}

.remove-btn:hover {
  background-color: #f56c6c;
  color: white;
  border-color: #f56c6c;
}

.pagination {
  margin-top: 30px;
  display: flex;
  justify-content: center;
}
</style>