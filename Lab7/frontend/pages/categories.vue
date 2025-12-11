<template>
  <div class="container mx-auto p-6">
    <h1 class="text-3xl font-bold mb-6">Quản lý Loại Hàng</h1>
    
    <!-- Form thêm/sửa -->
    <div class="bg-white shadow-md rounded px-8 pt-6 pb-8 mb-4">
      <h2 class="text-xl font-semibold mb-4">
        {{ editingId ? 'Sửa loại hàng' : 'Thêm loại hàng mới' }}
      </h2>
      <form @submit.prevent="saveCategory">
        <div class="mb-4">
          <label class="block text-gray-700 text-sm font-bold mb-2">
            Tên loại hàng
          </label>
          <input
            v-model="form.name"
            type="text"
            required
            class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
            placeholder="Nhập tên loại hàng"
          />
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
    <div class="bg-white shadow-md rounded">
      <table class="min-w-full">
        <thead class="bg-gray-100">
          <tr>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              Mã
            </th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              Tên loại hàng
            </th>
            <th class="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
              Thao tác
            </th>
          </tr>
        </thead>
        <tbody class="bg-white divide-y divide-gray-200">
          <tr v-for="category in categories" :key="category.id">
            <td class="px-6 py-4 whitespace-nowrap">{{ category.id }}</td>
            <td class="px-6 py-4 whitespace-nowrap">{{ category.name }}</td>
            <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
              <button
                @click="editCategory(category)"
                class="text-indigo-600 hover:text-indigo-900 mr-3"
              >
                Sửa
              </button>
              <button
                @click="deleteCategory(category.id)"
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

const categories = ref([])
const form = ref({ name: '' })
const editingId = ref(null)

// Load danh sách
const loadCategories = async () => {
  try {
    const response = await $fetch(`${apiBase}/categories`)
    categories.value = response
  } catch (error) {
    console.error('Lỗi khi tải danh sách:', error)
  }
}

// Thêm/Sửa
const saveCategory = async () => {
  try {
    if (editingId.value) {
      await $fetch(`${apiBase}/categories/${editingId.value}`, {
        method: 'PUT',
        body: form.value
      })
    } else {
      await $fetch(`${apiBase}/categories`, {
        method: 'POST',
        body: form.value
      })
    }
    form.value = { name: '' }
    editingId.value = null
    await loadCategories()
  } catch (error) {
    console.error('Lỗi khi lưu:', error)
  }
}

// Sửa
const editCategory = (category) => {
  form.value = { name: category.name }
  editingId.value = category.id
}

// Hủy sửa
const cancelEdit = () => {
  form.value = { name: '' }
  editingId.value = null
}

// Xóa
const deleteCategory = async (id) => {
  if (confirm('Bạn có chắc muốn xóa loại hàng này?')) {
    try {
      await $fetch(`${apiBase}/categories/${id}`, { method: 'DELETE' })
      await loadCategories()
    } catch (error) {
      console.error('Lỗi khi xóa:', error)
    }
  }
}

onMounted(() => {
  loadCategories()
})
</script>