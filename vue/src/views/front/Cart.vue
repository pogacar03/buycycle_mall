<template>
  <div class="main-content">
    <!-- 添加导航栏 -->
    <nav-bar></nav-bar>

    <div class="container">
      <div class="cart-wrapper">
        <div class="cart-header">
          <div class="title">
            <i class="el-icon-shopping-cart-2"></i>
            全部商品（{{ goodsData.length }}件）
          </div>
          <div class="address-select">
            <el-select
              v-model="addressId"
              placeholder="请选择收货地址"
              class="address-dropdown"
            >
              <el-option
                v-for="item in addressData"
                :key="item.id"
                :label="
                  item.username + ' - ' + item.useraddress + ' - ' + item.phone
                "
                :value="item.id"
              ></el-option>
            </el-select>
          </div>
          <div class="checkout-info">
            <div class="price-display">
              已选商品 <span>￥{{ totalPrice }}</span>
            </div>
            <el-button type="danger" round @click="pay" class="checkout-btn"
              >下单</el-button
            >
          </div>
        </div>

        <div class="cart-content">
          <div class="table">
            <el-table
              :data="goodsData"
              stripe
              @selection-change="handleSelectionChange"
              class="custom-table"
            >
              <el-table-column
                type="selection"
                width="55"
                align="center"
              ></el-table-column>
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
              <el-table-column prop="goodsName" label="商品名称" width="240px">
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
              <el-table-column
                prop="goodsPrice"
                label="商品价格"
              ></el-table-column>
              <el-table-column prop="num" label="选择数量" width="100px">
                <template v-slot="scope">
                  <el-input-number
                    v-model="scope.row.num"
                    class="num-input"
                    @change="handleChange(scope.row)"
                    :min="1"
                  ></el-input-number>
                </template>
              </el-table-column>
              <el-table-column label="操作" align="center" width="160">
                <template v-slot="scope">
                  <el-button
                    size="mini"
                    type="danger"
                    plain
                    @click="del(scope.row.id)"
                    >移除购物车</el-button
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
      goodsData: [],
      pageNum: 1, // 当前的页码
      pageSize: 10, // 每页显示的个数
      totalPrice: 0,
      total: 0,
      addressId: null,
      addressData: [],
      selectedData: [],
    };
  },
  mounted() {
    this.loadGoods(1);
    this.loadAddress();
  },
  // methods：本页面所有的点击事件或者其他函数定义区
  methods: {
    loadAddress() {
      this.$request.get("/address/selectAll").then((res) => {
        if (res.code === "200") {
          this.addressData = res.data;
        } else {
          this.$message.error(res.msg);
        }
      });
    },
    loadGoods(pageNum) {
      if (pageNum) this.pageNum = pageNum;
      this.$request
        .get("/cart/selectPage", {
          params: {
            pageNum: this.pageNum,
            pageSize: this.pageSize,
          },
        })
        .then((res) => {
          if (res.code === "200") {
            this.goodsData = res.data?.list;
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
      this.$request.delete("/cart/delete/" + id).then((res) => {
        if (res.code === "200") {
          this.$message.success("移除成功");
          this.loadGoods(1);
        } else {
          this.$message.error(res.msg);
        }
      });
    },
    handleCurrentChange(pageNum) {
      this.loadGoods(pageNum);
    },
    handleSelectionChange(rows) {
      this.totalPrice = 0;
      this.selectedData = rows;
      // 计算总价格

      this.selectedData.forEach((item) => {
        this.totalPrice += item.goodsPrice * item.num;
      });
    },
    handleChange(row) {
      this.totalPrice = 0;
      this.selectedData.forEach((item) => {
        this.totalPrice += item.goodsPrice * item.num;
      });
    },
    pay() {
      if (!this.addressId) {
        this.$message.warning("请选择收货地址");
        return;
      }
      if (!this.selectedData || this.selectedData.length === 0) {
        this.$message.warning("请选择商品");
        return;
      }
      let data = {
        userId: this.user.id,
        addressId: this.addressId,
        status: "待发货",
        cartData: this.selectedData,
      };
      this.$request.post("/orders/add", data).then((res) => {
        if (res.code === "200") {
          this.$message.success("操作成功");
          this.loadGoods(1);
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
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  padding-bottom: 40px;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.cart-wrapper {
  background: rgba(255, 255, 255, 0.9);
  border-radius: 15px;
  box-shadow: 0 8px 32px rgba(31, 38, 135, 0.15);
  backdrop-filter: blur(4px);
  padding: 20px;
  margin-top: 20px;
}

.cart-header {
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
  flex: 1;
  display: flex;
  align-items: center;
}

.title i {
  margin-right: 10px;
  font-size: 22px;
  color: #ff5000;
}

.address-select {
  flex: 2;
  text-align: right;
  padding-right: 20px;
}

.address-dropdown {
  width: 70%;
}

.checkout-info {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: flex-end;
}

.price-display {
  margin-right: 15px;
  font-size: 16px;
}

.price-display span {
  color: #ff5000;
  font-weight: bold;
}

.checkout-btn {
  font-weight: bold;
}

.cart-content {
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

.num-input {
  width: 90px;
}

.pagination {
  margin-top: 30px;
  display: flex;
  justify-content: center;
}
</style>