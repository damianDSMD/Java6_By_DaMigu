<template>
  <div class="container mx-auto p-6">
    <h1 class="text-3xl font-bold mb-6">Quản lý Sản Phẩm</h1>
    
    <!-- Form thêm/sửa -->
    <div class="bg-white shadow-md rounded px-8 pt-6 pb-8 mb-4">
      <h2 class="text-xl font-semibold mb-4">
        {{ editingId ? 'Sửa sản phẩm' : 'Thêm sản phẩm mới' }}
      </h2>
      <form @submit.prevent="saveProduct">
        <div class="grid grid-cols-2 gap-4 mb-4">
          <div>
            <label class="block text-gray-700 text-sm font-bold mb-2">
              Tên sản phẩm
            </label>
            <input
              v-model="form.name"
              type="text"
              required
              class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
              placeholder="Nhập tên sản phẩm"
            />
          </div>
          <div>
            <label class="block text-gray-700 text-sm font-bold mb-2">
              Giá
            </label>
            <input
              v-model.number="form.price"
              type="number"
              step="0.01"
              required
              class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
              placeholder="Nhập giá"
            />
          </div>
        </div>
        
        <div class="grid grid-cols-2 gap-4 mb-4">
          <div>
            <label class="block text-gray-700 text-sm font-bold mb-2">
              Ngày nhập
            </label>
            <input
              v-model="form.date"
              type="date"
              required
              class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
            />
          </div>
          <div>
            <label class="block text-gray-700 text-sm font-bold mb-2">
              Loại hàng
            </label>
            <select
              v-model.number="form.categoryId"
              required
              class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
            >
              <option value="">-- Chọn loại hàng --</option>
              <option v-for="cat in categories" :key="cat.id" :value="cat.id">
                {{ cat.name }}
              </option>
            </select>
          </div>
        </div>
        
        <div class="flex gap-2">
          <button
            type="submit"
            class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline"
          >
            {{ editingId ? 'Cập nhật' : 'Thêm mới' }}
          </button>
          <button
            v-if="editingId"
            type="button"
            @click="cancelEdit"
            class="bg-gray-500 hover:bg-gray-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline"
          >
            Hủy
          </button>
        </div>
      </form>
    </div>

    <!-- Danh sách -->
    <div class="bg-white shadow-md rounded overflow-x-auto">
      <table class="min-w-full">
        <thead class="bg-gray-100">
          <tr>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              Mã
            </th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              Tên sản phẩm
            </th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              Giá
            </th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              Ngày nhập
            </th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              Loại hàng
            </th>
            <th class="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
              Thao tác
            </th>
          </tr>
        </thead>
        <tbody class="bg-white divide-y divide-gray-200">
          <tr v-for="product in products" :key="product.id">
            <td class="px-6 py-4 whitespace-nowrap">{{ product.id }}</td>
            <td class="px-6 py-4 whitespace-nowrap">{{ product.name }}</td>
            <td class="px-6 py-4 whitespace-nowrap">{{ formatPrice(product.price) }}</td>
            <td class="px-6 py-4 whitespace-nowrap">{{ product.date }}</td>
            <td class="px-6 py-4 whitespace-nowrap">{{ getCategoryName(product.categoryId) }}</td>
            <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
              <button
                @click="editProduct(product)"
                class="text-indigo-600 hover:text-indigo-900 mr-3"
              >
                Sửa
              </button>
              <button
                @click="deleteProduct(product.id)"
                class="text-red-600 hover:text-red-900"
              >
                Xóa
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
const config = useRuntimeConfig()
const apiBase = config.public.apiBase

const products = ref([])
const categories = ref([])
const form = ref({ 
  name: '', 
  price: 0, 
  date: new Date().toISOString().split('T')[0],
  categoryId: ''
})
const editingId = ref(null)

// Load categories
const loadCategories = async () => {
  try {
    const response = await $fetch(`${apiBase}/categories`)
    categories.value = response
  } catch (error) {
    console.error('Lỗi khi tải loại hàng:', error)
  }
}

// Load products
const loadProducts = async () => {
  try {
    const response = await $fetch(`${apiBase}/products`)
    products.value = response
  } catch (error) {
    console.error('Lỗi khi tải sản phẩm:', error)
  }
}

// Thêm/Sửa
const saveProduct = async () => {
  try {
    if (editingId.value) {
      await $fetch(`${apiBase}/products/${editingId.value}`, {
        method: 'PUT',
        body: form.value
      })
    } else {
      await $fetch(`${apiBase}/products`, {
        method: 'POST',
        body: form.value
      })
    }
    form.value = { 
      name: '', 
      price: 0, 
      date: new Date().toISOString().split('T')[0],
      categoryId: ''
    }
    editingId.value = null
    await loadProducts()
  } catch (error) {
    console.error('Lỗi khi lưu:', error)
  }
}

// Sửa
const editProduct = (product) => {
  form.value = { ...product }
  editingId.value = product.id
}

// Hủy sửa
const cancelEdit = () => {
  form.value = { 
    name: '', 
    price: 0, 
    date: new Date().toISOString().split('T')[0],
    categoryId: ''
  }
  editingId.value = null
}

// Xóa
const deleteProduct = async (id) => {
  if (confirm('Bạn có chắc muốn xóa sản phẩm này?')) {
    try {
      await $fetch(`${apiBase}/products/${id}`, { method: 'DELETE' })
      await loadProducts()
    } catch (error) {
      console.error('Lỗi khi xóa:', error)
    }
  }
}

// Format giá
const formatPrice = (price) => {
  return new Intl.NumberFormat('vi-VN', { 
    style: 'currency', 
    currency: 'VND' 
  }).format(price)
}

// Lấy tên loại hàng
const getCategoryName = (categoryId) => {
  const category = categories.value.find(c => c.id === categoryId)
  return category ? category.name : 'N/A'
}

onMounted(() => {
  loadCategories()
  loadProducts()
})
</script>