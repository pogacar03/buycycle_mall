<template>
  <div class="main-content">
    <div
      style="
        width: 80%;
        background-color: white;
        margin: 30px auto;
        border-radius: 20px;
      "
    >
      <div style="padding-bottom: 10px">
        <div
          style="
            font-size: 18px;
            color: #000000ff;
            line-height: 80px;
            border-bottom: #cccccc 1px solid;
          "
        >
          <div style="margin-left: 20px">
            我的订单（{{ ordersData.length }} 个）
          </div>
        </div>
        <div style="margin: 20px 0; padding: 0 50px">
          <div class="table">
            <el-table :data="ordersData" stripe v-loading="loading">
              <el-table-column label="商品图片" width="120px">
                <template v-slot="scope">
                  <el-image
                    style="width: 80px; height: 60px; border-radius: 3px"
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
                  <a :href="'/front/detail?id=' + scope.row.goodsId">{{
                    scope.row.goodsName
                  }}</a>
                </template>
              </el-table-column>
              <el-table-column prop="businessName" label="店铺名称">
                <template v-slot="scope">
                  <a :href="'/front/business?id=' + scope.row.businessId">{{
                    scope.row.businessName
                  }}</a>
                </template>
              </el-table-column>
              <el-table-column prop="goodsPrice" label="商品价格">
                <template v-slot="scope">
                  {{ scope.row.goodsPrice }} / {{ scope.row.goodsUnit }}
                </template>
              </el-table-column>
              <el-table-column prop="num" label="商品数量"></el-table-column>
              <el-table-column prop="price" label="订单总价"></el-table-column>
              <el-table-column prop="username" label="收货人"></el-table-column>
              <el-table-column
                prop="useraddress"
                label="收货地址"
              ></el-table-column>
              <el-table-column prop="phone" label="联系电话"></el-table-column>
              <el-table-column prop="status" label="订单状态">
                <template v-slot="scope">
                  <!-- 基本订单状态 -->
                  <span>{{ scope.row.status }}</span>

                  <!-- 退款/退货状态标签 -->
                  <div style="margin-top: 5px">
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
                </template>
              </el-table-column>
            </el-table>

            <div class="pagination" style="margin-top: 20px">
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
export default {
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
  },
  beforeDestroy() {
    // 添加对$bus的检查
    if (this.$bus) {
      this.$bus.$off("refreshOrders");
    }
  },
  // methods：本页面所有的点击事件或者其他函数定义区
  methods: {
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
    payWithAlipay() {
      const orderId = this.orderId;
      const amount = this.totalAmount;

      this.$request
        .post("/api/alipay/pay", {
          orderId: orderId,
          amount: amount,
          subject: "订单支付-" + orderId,
        })
        .then((res) => {
          if (res.code === "200") {
            // 创建一个div插入支付表单并提交
            const div = document.createElement("div");
            div.innerHTML = res.data;
            document.body.appendChild(div);
            document.forms[0].submit();
          } else {
            this.$message.error(res.msg || "创建支付失败");
          }
        });
    },
  },
};
</script>