<template>
  <div class="container">
    <div class="register-box">
      <div class="register-header">
        <img src="@/assets/imgs/logo.png" alt="logo" class="logo">
        <h2>欢迎加入自行车交易平台</h2>
        <p>开启您的骑行之旅</p>
      </div>
      
      <el-form :model="form" :rules="rules" ref="formRef" class="register-form">
        <el-form-item prop="username">
          <el-input 
            prefix-icon="el-icon-user" 
            placeholder="请输入账号" 
            v-model="form.username"
            class="custom-input">
          </el-input>
        </el-form-item>
        
        <el-form-item prop="password">
          <el-input 
            prefix-icon="el-icon-lock" 
            placeholder="请输入密码" 
            show-password  
            v-model="form.password"
            class="custom-input">
          </el-input>
        </el-form-item>
        
        <el-form-item prop="confirmPass">
          <el-input 
            prefix-icon="el-icon-key" 
            placeholder="请确认密码" 
            show-password  
            v-model="form.confirmPass"
            class="custom-input">
          </el-input>
        </el-form-item>
        
        <el-form-item>
          <el-select 
            v-model="form.role" 
            placeholder="请选择角色" 
            class="custom-select">
            <el-option label="商家" value="BUSINESS"></el-option>
            <el-option label="用户" value="USER"></el-option>
          </el-select>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" class="register-btn" @click="register">
            立即注册
          </el-button>
        </el-form-item>
        
        <div class="form-footer">
          <span>已有账号？</span>
          <a href="/login" class="login-link">立即登录</a>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script>
export default {
  name: "Register",
  data() {
    const validatePassword = (rule, confirmPass, callback) => {
      if (confirmPass === '') {
        callback(new Error('请确认密码'))
      } else if (confirmPass !== this.form.password) {
        callback(new Error('两次输入的密码不一致'))
      } else {
        callback()
      }
    }
    return {
      form: {},
      rules: {
        username: [
          { required: true, message: '请输入账号', trigger: 'blur' },
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
        ],
        confirmPass: [
          { validator: validatePassword, trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    register() {
      this.$refs['formRef'].validate((valid) => {
        if (valid) {
          this.$request.post('/register', this.form).then(res => {
            if (res.code === '200') {
              this.$router.push('/')
              this.$message.success('注册成功')
            } else {
              this.$message.error(res.msg)
            }
          })
        }
      })
    }
  }
}
</script>

<style scoped>
.container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  padding: 20px;
}

.register-box {
  width: 420px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 15px;
  padding: 40px;
  box-shadow: 0 8px 32px rgba(31, 38, 135, 0.15);
  backdrop-filter: blur(4px);
}

.register-header {
  text-align: center;
  margin-bottom: 30px;
}

.logo {
  width: 80px;
  height: 80px;
  margin-bottom: 15px;
}

.register-header h2 {
  color: #2c3e50;
  font-size: 24px;
  margin-bottom: 10px;
}

.register-header p {
  color: #7f8c8d;
  font-size: 14px;
}

.register-form {
  margin-top: 20px;
}

.custom-input :deep(.el-input__inner) {
  height: 45px;
  line-height: 45px;
  border-radius: 8px;
  border: 1px solid #dcdfe6;
  transition: all 0.3s;
}

.custom-input :deep(.el-input__inner):focus {
  border-color: #409EFF;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
}

.custom-select {
  width: 100%;
}

.custom-select :deep(.el-input__inner) {
  height: 45px;
  line-height: 45px;
  border-radius: 8px;
}

.register-btn {
  width: 100%;
  height: 45px;
  border-radius: 8px;
  font-size: 16px;
  letter-spacing: 2px;
  background: linear-gradient(to right, #04BF04, #2ecc71);
  border: none;
  transition: all 0.3s;
}

.register-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(46, 204, 113, 0.3);
}

.form-footer {
  margin-top: 20px;
  text-align: center;
  color: #7f8c8d;
  font-size: 14px;
}

.login-link {
  color: #04BF04;
  text-decoration: none;
  margin-left: 5px;
  font-weight: 500;
  transition: all 0.3s;
}

.login-link:hover {
  color: #2ecc71;
  text-decoration: underline;
}

@media (max-width: 480px) {
  .register-box {
    width: 100%;
    padding: 30px 20px;
  }
}
</style>