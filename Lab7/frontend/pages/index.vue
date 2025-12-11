<template>
  <div class="min-h-screen bg-gray-50">
    <!-- Navigation -->
    <nav class="bg-blue-600 shadow-lg">
      <div class="container mx-auto px-6 py-4">
        <div class="flex items-center justify-between">
          <div class="text-white font-bold text-xl">
            Hệ thống Quản lý
          </div>
          <div class="flex space-x-4">
            <NuxtLink 
              to="/categories" 
              class="text-white hover:bg-blue-700 px-4 py-2 rounded transition"
            >
              Loại Hàng
            </NuxtLink>
            <NuxtLink 
              to="/products" 
              class="text-white hover:bg-blue-700 px-4 py-2 rounded transition"
            >
              Sản Phẩm
            </NuxtLink>
            <NuxtLink 
              to="/users" 
              class="text-white hover:bg-blue-700 px-4 py-2 rounded transition"
            >
              Người Dùng
            </NuxtLink>
          </div>
        </div>
      </div>
    </nav>

    </div>
</template>

<script setup>
useHead({
  title: 'Hệ thống Quản lý - Nuxt 3 + Spring Boot'
})
</script>