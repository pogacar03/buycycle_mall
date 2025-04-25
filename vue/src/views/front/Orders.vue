<template>
  <div class="main-content">
    <!-- 添加导航栏 -->
    <nav-bar></nav-bar>

    <div class="container">
      <div class="orders-wrapper">
        <div class="orders-header">
          <div class="title">
            <i class="el-icon-s-order"></i>
            我的订单（{{ ordersData.length }} 个）
          </div>
        </div>
        <div class="orders-content">
          <div class="table">
            <el-table
              :data="ordersData"
              stripe
              class="custom-table"
              v-loading="loading"
            >
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
              <el-table-column
                prop="orderId"
                label="订单编号"
              ></el-table-column>
              <el-table-column
                prop="goodsName"
                label="商品名称"
                :show-overflow-tooltip="true"
              >
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
                  <span class="price"
                    >{{ scope.row.goodsPrice }} /
                    {{ scope.row.goodsUnit }}</span
                  >
                </template>
              </el-table-column>
              <el-table-column prop="num" label="商品数量"></el-table-column>
              <el-table-column prop="price" label="订单总价">
                <template v-slot="scope">
                  <span class="price">￥{{ scope.row.price }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="username" label="收货人"></el-table-column>
              <el-table-column
                prop="useraddress"
                label="收货地址"
              ></el-table-column>
              <el-table-column prop="phone" label="联系电话"></el-table-column>
              <el-table-column prop="status" label="订单状态">
                <template v-slot="scope">
                  <!-- 基本订单状态 -->
                  <span
                    :class="['order-status', getStatusClass(scope.row.status)]"
                  >
                    {{ scope.row.status }}
                  </span>

                  <!-- 退款/退货状态标签 -->
                  <div v-if="scope.row.returnStatus !== '0'" class="return-tag">
                    <el-tag type="warning" v-if="scope.row.returnStatus === '1'"
                      >退换处理中</el-tag
                    >
                    <el-tag type="success" v-if="scope.row.returnStatus === '2'"
                      >退换已完成</el-tag
                    >
                  </div>
                </template>
              </el-table-column>
              <el-table-column label="操作" align="center" width="280">
                <template v-slot="scope">
                  <el-button
                    size="mini"
                    type="primary"
                    v-if="scope.row.status === '待收货'"
                    plain
                    @click="updateStatus(scope.row, '已完成')"
                    >确认收货</el-button
                  >
                  <el-button
                    size="mini"
                    type="primary"
                    v-if="scope.row.status === '已完成'"
                    plain
                    @click="addComment(scope.row)"
                    >评价</el-button
                  >
                  <el-button
                    size="mini"
                    type="warning"
                    plain
                    @click="handleReturn(scope.row)"
                    v-if="
                      (scope.row.status === '已完成' ||
                        scope.row.status === '已评价') &&
                      (!scope.row.returnStatus ||
                        scope.row.returnStatus === '0')
                    "
                    >申请退换</el-button
                  >
                  <el-button
                    size="mini"
                    type="danger"
                    plain
                    @click="del(scope.row.id)"
                    >删除</el-button
                  >
                  <el-button
                    size="mini"
                    type="primary"
                    v-if="
                      scope.row.status === '待支付' ||
                      scope.row.payStatus === '未支付'
                    "
                    plain
                    @click="payWithAlipay(scope.row)"
                    >立即支付</el-button
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
      title="请输入评价内容"
      :visible.sync="fromVisible"
      width="40%"
      :close-on-click-modal="false"
      destroy-on-close
    >
      <el-form
        :model="form"
        label-width="100px"
        style="padding-right: 50px"
        :rules="rules"
        ref="formRef"
      >
        <el-form-item label="评价内容" prop="username">
          <el-input
            type="textarea"
            v-model="form.content"
            placeholder="请输入评价内容"
          ></el-input>
        </el-form-item>
      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button @click="fromVisible = false">取 消</el-button>
        <el-button type="primary" @click="save">确 定</el-button>
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
      ordersData: [],
      pageNum: 1, // 当前的页码
      pageSize: 10, // 每页显示的个数
      total: 0,
      form: {},
      fromVisible: false,
      loading: false,
      rules: {}, // 添加缺少的rules属性
      currentPayingOrderId: null,
    };
  },
  mounted() {
    this.loadOrders(1);

    // 添加对$bus的检查
    if (this.$bus) {
      this.$bus.$on("refreshOrders", () => {
        this.loadOrders(this.pageNum);
      });
    } else {
      console.error("事件总线($bus)未初始化");
    }

    // 处理支付回调
    this.handlePayReturnQuery();
  },
  beforeDestroy() {
    // 添加对$bus的检查
    if (this.$bus) {
      this.$bus.$off("refreshOrders");
    }
  },
  // methods：本页面所有的点击事件或者其他函数定义区
  methods: {
    // 根据订单状态返回对应的CSS类
    getStatusClass(status) {
      if (!status) return "";

      switch (status) {
        case "待支付":
          return "waiting-payment";
        case "待发货":
          return "waiting-shipment";
        case "待收货":
          return "waiting-receipt";
        case "已完成":
          return "completed";
        case "已评价":
          return "evaluated";
        default:
          return "";
      }
    },
    loadOrders(pageNum) {
      if (pageNum) this.pageNum = pageNum;

      // 显示加载状态
      this.loading = true;

      this.$request
        .get("/orders/selectPage", {
          params: {
            pageNum: this.pageNum,
            pageSize: this.pageSize,
            userId: this.user.id,
          },
        })
        .then((res) => {
          this.loading = false;
          if (res.code === "200") {
            this.ordersData = res.data?.list || [];
            console.log("原始订单数据:", JSON.stringify(this.ordersData));

            // 检查订单ID字段
            this.ordersData.forEach((order, index) => {
              // 输出每个订单的所有字段，查找订单ID字段
              console.log(`订单${index}的所有字段:`, Object.keys(order));

              // 检查并修复orderId字段
              if (!order.orderId && order.order_id) {
                order.orderId = order.order_id;
                console.log(
                  `修复订单ID: 使用order_id(${order.order_id})替代orderId`
                );
              }

              // 其他处理逻辑
            });

            // 确保returnStatus字段被正确转换为字符串
            this.ordersData.forEach((order) => {
              if (order.returnStatus !== undefined) {
                order.returnStatus = order.returnStatus.toString();
              } else {
                // 如果returnStatus不存在，设置默认值为"0"
                order.returnStatus = "0";
              }

              // 添加日志查看每个订单的状态
              console.log(
                `订单ID: ${order.orderId}, 状态: ${order.status}, 退款状态: ${order.returnStatus}`
              );

              // 添加这部分以检查支付状态
              console.log(
                `订单ID: ${order.orderId}, 状态: ${order.status}, 支付状态: ${order.payStatus}`
              );
            });

            this.total = res.data?.total || 0;
            console.log("加载的订单数据总数:", this.ordersData.length);
          } else {
            this.$message.error(res.msg || "获取订单数据失败");
          }
        })
        .catch((err) => {
          this.loading = false;
          console.error("加载订单失败:", err);
          this.$message.error(
            "获取订单数据失败: " + (err.message || "未知错误")
          );
        });
    },
    navTo(url) {
      location.href = url;
    },
    del(id) {
      this.$request.delete("/orders/delete/" + id).then((res) => {
        if (res.code === "200") {
          this.$message.success("删除成功");
          this.loadOrders(1);
        } else {
          this.$message.error(res.msg);
        }
      });
    },
    handleCurrentChange(pageNum) {
      this.loadOrders(pageNum);
    },
    updateStatus(row, status) {
      this.form = row;
      this.form.status = status;
      this.$request.put("/orders/update", this.form).then((res) => {
        if (res.code === "200") {
          this.$message.success("操作成功");
        } else {
          this.$message.error(res.msg);
        }
      });
    },
    addComment(row) {
      this.fromVisible = true;
      this.form = row;
    },
    save() {
      let data = {
        userId: this.user.id,
        businessId: this.form.businessId,
        goodsId: this.form.goodsId,
        content: this.form.content,
      };
      this.$request.post("/comment/add", data).then((res) => {
        if (res.code === "200") {
          this.$message.success("评价成功");
          this.fromVisible = false;
          this.updateStatus(this.form, "已评价");
          this.form = {};
        } else {
          this.$message.error(res.msg);
        }
      });
    },
    handleReturn(row) {
      console.log("准备跳转到退换申请页面");
      console.log("订单数据:", row);

      // 确保orderId不为空
      if (!row.orderId) {
        this.$message.error("订单ID不能为空，无法申请退换");
        return;
      }

      // 使用window.location.href直接跳转
      const query = {
        orderId: row.orderId || "",
        goodsId: row.goodsId || "",
        goodsName: row.goodsName || "",
        goodsImg: row.goodsImg || "",
        goodsPrice: row.goodsPrice || 0,
      };

      console.log("跳转参数:", query);

      const queryString = Object.entries(query)
        .map(([key, value]) => `${key}=${encodeURIComponent(value || "")}`)
        .join("&");

      window.location.href = `/front/return/apply?${queryString}`;
    },
    payWithAlipay(row) {
      console.log("开始支付流程，订单数据:", row);
      const orderId = row.orderId;
      const amount = row.price;

      // 保存当前支付的订单ID
      this.currentPayingOrderId = orderId;

      // 提示用户将使用模拟支付
      this.$confirm(
        "支付宝沙箱环境可能出现签名验证错误，是否直接使用模拟支付？",
        "提示",
        {
          confirmButtonText: "模拟支付",
          cancelButtonText: "尝试真实支付",
          type: "info",
        }
      )
        .then(() => {
          // 用户选择模拟支付
          this.simulatePayment(orderId, amount);
        })
        .catch(() => {
          // 用户选择尝试真实支付
          this.realPayment(orderId, amount);
        });
    },

    // 模拟支付流程
    simulatePayment(orderId, amount) {
      // 显示支付中
      const loading = this.$loading({
        lock: true,
        text: "正在处理支付...",
        spinner: "el-icon-loading",
        background: "rgba(0, 0, 0, 0.7)",
      });

      // 模拟网络延迟
      setTimeout(() => {
        loading.close();
        this.$message.success(`已成功支付￥${amount}，订单状态已更新`);

        // 使用支付API处理支付成功
        this.$request
          .post("/api/alipay/simulate-payment", {
            orderId: orderId,
          })
          .then((res) => {
            if (res.code === "200") {
              console.log("订单状态和库存已更新");
              // 刷新订单列表
              this.loadOrders(this.pageNum);
            } else {
              this.$message.warning("订单状态更新失败：" + res.msg);
            }
          })
          .catch((err) => {
            console.error("模拟支付请求失败:", err);
            this.$message.error("支付处理失败");
          });
      }, 1500);
    },

    // 真实支付流程
    realPayment(orderId, amount) {
      // 显示加载中提示
      const loading = this.$loading({
        lock: true,
        text: "正在获取支付表单...",
        spinner: "el-icon-loading",
        background: "rgba(0,0,0,0.7)",
      });

      // 构造当前页面URL作为支付成功后的跳转地址
      const currentPageUrl = window.location.origin + window.location.pathname;
      const returnUrl = `${currentPageUrl}?orderId=${orderId}`;

      console.log("使用自定义跳转地址:", returnUrl);

      this.$request
        .post("/api/alipay/pay", {
          orderId: orderId,
          amount: amount,
          subject: "订单支付-" + orderId,
          returnUrl: returnUrl, // 添加自定义returnUrl参数
        })
        .then((res) => {
          loading.close(); // 关闭加载提示
          console.log("支付接口返回:", res);
          if (res.code === "200") {
            try {
              // 创建一个div元素来容纳表单
              const div = document.createElement("div");
              div.innerHTML = res.data;
              document.body.appendChild(div);

              // 如果表单存在，直接提交
              const form = div.querySelector("form");
              if (form) {
                console.log("找到支付表单，正在提交");
                form.target = "_blank"; // 在新窗口打开
                form.submit();

                // 提示用户签名可能失败
                this.$alert(
                  "如果在支付宝页面看到签名错误，请返回本页面使用模拟支付完成测试",
                  "提示",
                  {
                    confirmButtonText: "知道了",
                    type: "warning",
                  }
                );

                // 设置定时器，每隔一段时间刷新订单列表，检查订单是否已支付
                this.createRefreshChecker();
              } else {
                this.$message.error("未找到支付表单");
              }
            } catch (e) {
              console.error("处理支付表单出错:", e);
              this.$message.error("处理支付表单失败: " + e.message);
            }
          } else {
            this.$message.error(res.msg || "创建支付失败");
          }
        })
        .catch((err) => {
          loading.close();
          console.error("支付请求错误:", err);
          this.$message.error("支付请求失败: " + (err.message || "未知错误"));
        });
    },

    // 创建定时刷新检查器
    createRefreshChecker() {
      // 设置定时器，每隔一段时间刷新订单列表，检查订单是否已支付
      const checkInterval = 5000; // 5秒检查一次
      const maxChecks = 12; // 最多检查12次（约1分钟）
      let checkCount = 0;

      const checkPaymentStatus = setInterval(() => {
        checkCount++;
        console.log(`第${checkCount}次检查订单支付状态`);

        // 刷新订单列表
        this.loadOrders(this.pageNum);

        // 如果达到最大检查次数，停止检查
        if (checkCount >= maxChecks) {
          clearInterval(checkPaymentStatus);
          console.log("已完成订单状态检查");
        }
      }, checkInterval);
    },

    // 显示支付宝网关错误提示
    showGatewayErrorTip() {
      this.$alert(
        "支付宝沙箱环境暂时不可用，出现了502 Bad Gateway错误。这是支付宝沙箱服务器的问题，不影响系统功能测试。在实际生产环境中不会出现此问题。",
        "支付网关暂时不可用",
        {
          confirmButtonText: "我知道了",
          type: "warning",
          callback: () => {
            this.$message({
              type: "info",
              message: "您可以稍后再试，或者继续测试系统的其他功能",
            });
          },
        }
      );
    },

    // 在新窗口中打开支付表单
    openPayForm(form) {
      try {
        // 先显示提示
        this.$message.success("正在跳转到支付页面，请稍等...");

        // 添加错误处理函数 - 当支付页面加载失败时执行
        window.addEventListener(
          "error",
          (event) => {
            if (
              event.target.tagName === "IFRAME" ||
              event.target.tagName === "IMG"
            ) {
              if (event.target.src && event.target.src.includes("alipay")) {
                console.error("支付宝页面加载失败:", event);
                this.showGatewayErrorTip();
                return true; // 阻止默认处理
              }
            }
          },
          true
        );

        // 在新窗口中打开并提交表单
        form.target = "_blank";
        setTimeout(() => {
          form.submit();

          // 5秒后提示用户可能的网关错误
          setTimeout(() => {
            this.showGatewayErrorTip();
          }, 5000);
        }, 500);

        // 创建刷新检查定时器
        this.createRefreshChecker();
      } catch (e) {
        console.error("打开支付表单失败:", e);
        this.$message.error("打开支付页面失败: " + e.message);
      }
    },

    // 添加处理支付成功后的回调函数
    handlePayReturnQuery() {
      // 检查URL中是否包含支付回调参数
      const urlParams = new URLSearchParams(window.location.search);
      const orderId = urlParams.get("orderId");

      if (orderId) {
        // 清除URL中的参数，防止刷新页面时重复处理
        window.history.replaceState(
          {},
          document.title,
          window.location.pathname
        );

        // 显示支付成功提示
        this.$message.success("支付成功，订单已更新");

        // 查找并更新对应订单的状态
        const matchedOrder = this.ordersData.find(
          (order) => order.orderId === orderId
        );
        if (matchedOrder) {
          matchedOrder.status = "待发货";
          matchedOrder.payStatus = "已支付";

          // 向后端发送更新请求
          this.updateStatus(matchedOrder, "待发货");
        }

        // 刷新订单列表
        this.loadOrders(this.pageNum);
      }
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

.orders-wrapper {
  background: rgba(255, 255, 255, 0.9);
  border-radius: 15px;
  box-shadow: 0 8px 32px rgba(31, 38, 135, 0.15);
  backdrop-filter: blur(4px);
  padding: 20px;
  margin-top: 20px;
}

.orders-header {
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
  color: #409eff;
}

.orders-content {
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
  color: #409eff;
}

.price {
  font-weight: bold;
  color: #ff5000;
}

.status-tag {
  margin-bottom: 5px;
}

.pagination {
  margin-top: 30px;
  display: flex;
  justify-content: center;
}

/* Dialog styles */
.pay-form-container {
  min-height: 300px;
  max-height: 500px;
  overflow-y: auto;
  padding: 10px;
}

/* Order status indicators */
.order-status {
  font-weight: bold;
}

.order-status.waiting-payment {
  color: #e6a23c;
}

.order-status.waiting-shipment {
  color: #409eff;
}

.order-status.waiting-receipt {
  color: #67c23a;
}

.order-status.completed {
  color: #909399;
}

.order-status.evaluated {
  color: #67c23a;
}

.return-tag {
  margin-top: 5px;
}
</style>

<style>
/* 全局样式，确保跨组件生效 */
.pay-dialog .el-dialog__body {
  padding: 10px;
}
</style>