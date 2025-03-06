<template>
  <div class="main-content">
    <div class="container">
      <!-- 搜索框 -->
      <div class="search-container">
        <div class="search-box">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索商品"
            prefix-icon="el-icon-search"
            clearable
            @keyup.enter.native="handleSearch"
          >
            <el-button 
              slot="append" 
              icon="el-icon-search"
              @click="handleSearch">
              搜索
            </el-button>
          </el-input>
        </div>
      </div>

      <div class="content-area">
        <!-- 筛选器侧边栏 -->
        <div class="filter-sidebar">
          <div class="filter-section">
            <h3>价格区间</h3>
            <el-slider
              v-model="priceRange"
              range
              :min="0"
              :max="50000"
              :step="100"
              @change="handleFilter"
            ></el-slider>
            <div class="price-range-text">
              ¥{{ priceRange[0] }} - ¥{{ priceRange[1] }}
            </div>
          </div>

          <div class="filter-section">
            <h3>品牌</h3>
            <el-checkbox-group v-model="selectedBrands" @change="handleFilter">
              <el-checkbox 
                v-for="brand in availableBrands" 
                :key="brand" 
                :label="brand">
                {{ brand }}
              </el-checkbox>
            </el-checkbox-group>
          </div>

          <div class="filter-section">
            <h3>排序方式</h3>
            <el-radio-group v-model="sortBy" @change="handleFilter">
              <el-radio label="default">默认</el-radio>
              <el-radio label="priceAsc">价格从低到高</el-radio>
              <el-radio label="priceDesc">价格从高到低</el-radio>
            </el-radio-group>
          </div>

          <el-button type="primary" @click="resetFilters" class="reset-btn">
            重置筛选
          </el-button>
        </div>

        <!-- 商品展示区域 -->
        <div class="goods-container">
          <div class="goods-header">
            <h2>{{ categoryName }}</h2>
            <span class="goods-count">共 {{ filteredGoods.length }} 个商品</span>
          </div>

          <div class="goods-grid">
            <div 
              v-for="item in filteredGoods" 
              :key="item.id" 
              class="goods-card"
              @click="navTo('/front/detail?id=' + item.id)"
            >
              <div class="goods-img">
                <img :src="item.img" :alt="item.name">
              </div>
              <div class="goods-info">
                <h3>{{ item.name }}</h3>
                <p class="goods-desc" v-if="item.description && !isImageUrl(item.description)">
                  {{ item.description }}
                </p>
                <div class="price">¥{{ item.price }}</div>
              </div>
            </div>
          </div>

          <!-- 无数据提示 -->
          <div v-if="filteredGoods.length === 0" class="no-data">
            <i class="el-icon-warning-outline"></i>
            <p>暂无相关商品</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Type',
  data() {
    return {
      categoryId: null,
      categoryName: '商品分类',
      goods: [],
      originalGoods: [],
      filteredGoods: [],
      searchKeyword: '',
      priceRange: [0, 50000],
      selectedBrands: [],
      sortBy: 'default',
      // 定义品牌关键词列表
      brandKeywords: {
        'Specialized': 'specialized',
        'Trek': 'trek',
        'Giant': 'giant',
        'Cannondale': 'cannondale',
        'Scott': 'scott',
        'Merida': 'merida',
        'BMC': 'bmc'
      }
    }
  },
  computed: {
    // 计算可用的品牌列表（仅显示当前商品中实际存在的品牌）
    availableBrands() {
      const brands = new Set()
      this.originalGoods.forEach(item => {
        for (const [brand, keyword] of Object.entries(this.brandKeywords)) {
          if (item.name.toLowerCase().includes(keyword)) {
            brands.add(brand)
            break
          }
        }
      })
      return Array.from(brands)
    }
  },
  created() {
    this.categoryId = this.$route.query.id
    this.loadGoods()
  },
  methods: {
    loadGoods() {
      this.$request.get('/goods/selectByTypeId', {
        params: {
          id: this.categoryId
        }
      }).then(res => {
        if (res.code === '200') {
          this.goods = res.data
          this.originalGoods = [...res.data]
          this.filteredGoods = [...this.goods]
        } else {
          this.$message.error(res.msg)
        }
      })
    },

    isImageUrl(str) {
      if (!str) return false;
      return str.match(/\.(jpeg|jpg|gif|png)$/) != null || 
             str.startsWith('http') || 
             str.startsWith('data:image');
    },

    handleSearch() {
      if (!this.searchKeyword.trim()) {
        this.goods = [...this.originalGoods]
        this.handleFilter()
        return
      }
      
      this.goods = this.originalGoods.filter(item => 
        item.name.toLowerCase().includes(this.searchKeyword.toLowerCase())
      )
      this.handleFilter()
    },

    handleFilter() {
      let filtered = [...this.goods]

      // 价格筛选
      filtered = filtered.filter(item => 
        item.price >= this.priceRange[0] && item.price <= this.priceRange[1]
      )

      // 品牌筛选
      if (this.selectedBrands.length > 0) {
        filtered = filtered.filter(item => {
          const itemName = item.name.toLowerCase()
          return this.selectedBrands.some(brand => 
            itemName.includes(this.brandKeywords[brand])
          )
        })
      }

      // 排序
      switch (this.sortBy) {
        case 'priceAsc':
          filtered.sort((a, b) => a.price - b.price)
          break
        case 'priceDesc':
          filtered.sort((a, b) => b.price - a.price)
          break
        default:
          break
      }

      this.filteredGoods = filtered
    },

    resetFilters() {
      this.priceRange = [0, 50000]
      this.selectedBrands = []
      this.sortBy = 'default'
      this.searchKeyword = ''
      this.goods = [...this.originalGoods]
      this.filteredGoods = [...this.goods]
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
  padding: 20px 0;
}

.container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 20px;
}

/* 搜索框样式 */
.search-container {
  margin-bottom: 20px;
}

.search-box {
  max-width: 800px;
  margin: 0 auto;
}

.search-box :deep(.el-input__inner) {
  height: 45px;
  line-height: 45px;
  padding-left: 20px;
  border-radius: 25px;
  font-size: 16px;
  border: 2px solid #e8e8e8;
  transition: all 0.3s;
}

.search-box :deep(.el-input__inner):focus {
  border-color: #04BF04;
  box-shadow: 0 0 0 2px rgba(4, 191, 4, 0.1);
}

.search-box :deep(.el-input-group__append) {
  background: #04BF04;
  border-color: #04BF04;
  color: white;
  border-top-right-radius: 25px;
  border-bottom-right-radius: 25px;
  padding: 0 25px;
  transition: all 0.3s;
}

.search-box :deep(.el-input-group__append):hover {
  background: #2ecc71;
  border-color: #2ecc71;
}

.search-box :deep(.el-button) {
  font-size: 16px;
  border: none;
  background: transparent;
  color: white;
}

/* 内容区域布局 */
.content-area {
  display: flex;
  gap: 30px;
  margin-top: 20px;
}

/* 筛选器侧边栏样式 */
.filter-sidebar {
  width: 280px;
  background: white;
  border-radius: 12px;
  padding: 20px;
  height: fit-content;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.filter-section {
  margin-bottom: 25px;
}

.filter-section h3 {
  font-size: 16px;
  color: #2c3e50;
  margin-bottom: 15px;
  font-weight: 600;
}

.price-range-text {
  margin-top: 10px;
  color: #666;
  font-size: 14px;
}

.filter-section :deep(.el-checkbox-group) {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.filter-section :deep(.el-radio-group) {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.reset-btn {
  width: 100%;
  margin-top: 20px;
}

/* 商品展示区域样式 */
.goods-container {
  flex: 1;
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.goods-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
}

.goods-header h2 {
  font-size: 20px;
  color: #2c3e50;
  margin: 0;
}

.goods-count {
  color: #666;
  font-size: 14px;
}

.goods-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
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

.goods-info {
  padding: 15px;
}

.goods-info h3 {
  margin: 0;
  font-size: 16px;
  color: #2c3e50;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.goods-desc {
  margin: 8px 0;
  font-size: 14px;
  color: #666;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  height: 40px;
}

.price {
  color: #ff5000;
  font-size: 18px;
  font-weight: bold;
}

/* 无数据提示样式 */
.no-data {
  text-align: center;
  padding: 40px 0;
  color: #999;
}

.no-data i {
  font-size: 48px;
  margin-bottom: 10px;
}

.no-data p {
  margin: 0;
  font-size: 16px;
}

/* 响应式布局 */
@media (max-width: 1200px) {
  .content-area {
    flex-direction: column;
  }
  
  .filter-sidebar {
    width: 100%;
  }
  
  .filter-section :deep(.el-checkbox-group),
  .filter-section :deep(.el-radio-group) {
    flex-direction: row;
    flex-wrap: wrap;
    gap: 15px;
  }
}

@media (max-width: 768px) {
  .container {
    padding: 0 10px;
  }
  
  .goods-grid {
    grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
    gap: 10px;
  }
  
  .goods-info {
    padding: 10px;
  }
  
  .goods-info h3 {
    font-size: 14px;
  }
  
  .price {
    font-size: 16px;
  }
}
</style>