import axios from 'axios'
import router from "@/router";
import { Message } from 'element-ui'  // 添加 Element UI 的消息提示

// 创建可一个新的axios对象
const request = axios.create({
    baseURL: process.env.VUE_APP_BASEURL,   // 后端的接口地址  ip:port
    timeout: 30000                        // 减少为10s请求超时
})

// request 拦截器
// 可以自请求发送前对请求做一些处理
// 比如统一加token，对请求参数统一加密
request.interceptors.request.use(config => {
    config.headers['Content-Type'] = 'application/json;charset=utf-8';        // 设置请求头格式

    // 获取缓存的用户信息
    let user = JSON.parse(localStorage.getItem("xm-user") || '{}')
    if (user.token) {
        config.headers['token'] = user.token  // 设置请求头
    }

    return config
}, error => {
    console.error('request error: ' + error) // for debug
    Message.error('请求配置错误：' + error.message)
    return Promise.reject(error)
});

// response 拦截器
// 可以在接口响应后统一处理结果
request.interceptors.response.use(
    response => {
        let res = response.data;

        // 如果是文件下载，直接返回
        if (response.config.responseType === 'blob') {
            return res;
        }

        // 兼容服务端返回的字符串数据
        if (typeof res === 'string') {
            res = res ? JSON.parse(res) : res
        }

        // 处理token过期
        if (res.code === '401') {
            Message.error('登录已过期，请重新登录')
            router.push('/login')
            return Promise.reject('未登录或登录过期')
        }

        // 处理其他错误
        if (res.code !== 200 && res.code !== '200') {
            Message.error(res.msg || '系统错误')
            return Promise.reject(res)
        }

        return res;
    },
    error => {
        console.error('response error: ' + error) // for debug

        // 处理超时错误
        if (error.message.includes('timeout')) {
            Message.error('请求超时，请检查网络连接')
            return Promise.reject(error)
        }

        // 处理网络错误
        if (error.response) {
            switch (error.response.status) {
                case 401:
                    Message.error('登录已过期，请重新登录')
                    router.push('/login')
                    break
                case 403:
                    Message.error('没有权限访问')
                    break
                case 404:
                    Message.error('请求的资源不存在')
                    break
                case 500:
                    Message.error('服务器内部错误')
                    break
                default:
                    Message.error('网络错误：' + error.message)
            }
        } else {
            Message.error('服务器无响应')
        }

        return Promise.reject(error)
    }
)

export default request