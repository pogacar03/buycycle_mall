<template>
  <div>
    

    <!-- AI 客服按钮 -->
    <el-button 
      type="primary" 
      @click="showAIDialog = true" 
      class="ai-service-btn">
      <i class="el-icon-service"></i> AI 客服
    </el-button>

    <!-- AI 客服对话框 -->
    <el-dialog 
      :visible.sync="showAIDialog" 
      title="AI 智能客服" 
      width="50%"
      :close-on-click-modal="false">
      <div class="ai-chat-container">
        <!-- 聊天记录区域 -->
        <div class="chat-messages" ref="chatContainer">
          <div v-if="chatHistory.length === 0" class="welcome-message">
            您好！我是BuyCycle的AI客服助手，请问有什么可以帮您？
          </div>
          <div v-for="(msg, index) in chatHistory" 
               :key="index" 
               :class="['message', msg.type]">
            <div class="message-content">{{ msg.content }}</div>
          </div>
        </div>
        
        <!-- 输入区域 -->
        <div class="chat-input">
          <el-input
            v-model="question"
            type="textarea"
            :rows="3"
            placeholder="请输入您的问题"
            @keyup.enter.native.exact="askAI"
          ></el-input>
          <el-button 
            type="primary" 
            @click="askAI"
            :loading="loading"
            style="margin-top: 10px">
            {{ loading ? '正在思考...' : '发送' }}
          </el-button>
        </div>
      </div>
    </el-dialog>

    <!--主体-->
    <div class="main-body">
      <router-view ref="child" @update:user="updateUser" />
    </div>
  </div>
</template>

<script>
export default {
  name: "FrontLayout",

  data() {
    return {
      top: '',
      notice: [],
      user: JSON.parse(localStorage.getItem("xm-user") || '{}'),
      showAIDialog: false,
      question: '',
      chatHistory: [],
      loading: false
    }
  },

  mounted() {
    this.loadNotice()
  },

  methods: {
    // ... 保持原有的其他方法不变 ...

    askAI() {
      if (this.question.trim() === '') {
        this.$message.warning('请输入问题');
        return;
      }

      // 添加用户问题到聊天记录
      this.chatHistory.push({
        type: 'user',
        content: this.question
      });

      this.loading = true;
      
      // 发送请求
      this.$request.post('/ai/ask', {
        question: this.question
      }).then(res => {
        if (res.code === 200) {
          // 添加AI回答到聊天记录
          this.chatHistory.push({
            type: 'ai',
            content: res.answer
          });
        } else {
          this.$message.error(res.msg || '请求失败');
        }
      }).catch(error => {
        console.error('AI请求失败:', error);
        this.$message.error('网络错误，请稍后再试');
      }).finally(() => {
        this.loading = false;
        this.question = ''; // 清空输入框
        this.$nextTick(() => {
          // 滚动到底部
          const container = this.$refs.chatContainer;
          if (container) {
            container.scrollTop = container.scrollHeight;
          }
        });
      });
    }
  }
}
</script>

<style scoped>
@import "@/assets/css/front.css";

/* AI客服相关样式 */
.ai-service-btn {
  position: fixed;
  bottom: 20px;
  right: 20px;
  z-index: 999;
  border-radius: 50%;
  width: 60px;
  height: 60px;
  padding: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
  font-size: 12px;
}

.ai-service-btn i {
  font-size: 24px;
  margin-bottom: 2px;
}

.ai-chat-container {
  height: 500px;
  display: flex;
  flex-direction: column;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 4px;
  margin-bottom: 20px;
}

.welcome-message {
  text-align: center;
  color: #909399;
  padding: 20px;
}

.message {
  margin-bottom: 15px;
  display: flex;
  flex-direction: column;
}

.message.user {
  align-items: flex-end;
}

.message.ai {
  align-items: flex-start;
}

.message-content {
  max-width: 80%;
  padding: 10px 15px;
  border-radius: 4px;
  word-break: break-word;
}

.user .message-content {
  background: #409EFF;
  color: white;
  border-radius: 15px 15px 0 15px;
}

.ai .message-content {
  background: white;
  color: #333;
  border-radius: 15px 15px 15px 0;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.chat-input {
  padding: 10px 0;
}
</style>