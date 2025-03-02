<template>
  <ul>
    <li v-for="category in categories" :key="category.id">
      {{ category.name }}
      <!-- 如果有子分类，递归调用自身组件 -->
      <category-tree v-if="category.children && category.children.length > 0" :categories="category.children"></category-tree>
    </li>
  </ul>
</template>

<script>
export default {
  name: 'CategoryTree',
  props: {
    // 接收父组件传递的分类数据
    categories: {
      type: Array,
      default: () => []
    }
  }
}
</script>

<style scoped>
/* 可以在这里添加样式 */
</style>