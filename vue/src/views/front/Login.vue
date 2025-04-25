<template>
  <!-- No changes to template section -->
</template>

<script>
export default {
  // No changes to component options
};
</script>

<style>
/* No changes to style section */
</style>

<script>
export default {
  methods: {
    handleLogin() {
      if (!this.form.username || !this.form.password) {
        this.$message.error("请输入用户名和密码");
        return;
      }

      this.loading = true;
      this.$request
        .post("/api/login", this.form)
        .then((res) => {
          if (res.code === "200") {
            this.$message.success("登录成功");
            // 保存用户信息到localStorage
            localStorage.setItem("user", JSON.stringify(res.data));
            // 保存token
            localStorage.setItem("token", res.data.token);
            // 跳转到首页
            this.$router.push("/");
          } else {
            // 错误信息已经在拦截器中处理，这里不需要重复处理
            console.error("登录失败:", res);
          }
        })
        .catch((error) => {
          // 错误信息已经在拦截器中处理，这里不需要重复处理
          console.error("登录错误:", error);
        })
        .finally(() => {
          this.loading = false;
        });
    },

    handleRegister() {
      if (!this.form.username || !this.form.password) {
        this.$message.error("请输入用户名和密码");
        return;
      }

      if (this.form.password !== this.form.confirmPassword) {
        this.$message.error("两次输入的密码不一致");
        return;
      }

      this.loading = true;
      this.$request
        .post("/api/register", this.form)
        .then((res) => {
          if (res.code === "200") {
            this.$message.success("注册成功，请登录");
            this.isLogin = true;
          } else {
            // 错误信息已经在拦截器中处理，这里不需要重复处理
            console.error("注册失败:", res);
          }
        })
        .catch((error) => {
          // 错误信息已经在拦截器中处理，这里不需要重复处理
          console.error("注册错误:", error);
        })
        .finally(() => {
          this.loading = false;
        });
    },
  },
};
</script> 