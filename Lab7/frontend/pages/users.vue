<template>
  <div class="container mx-auto p-6">
    <h1 class="text-3xl font-bold mb-6">Quản lý Người Dùng</h1>
    
    <!-- Form thêm/sửa -->
    <div class="bg-white shadow-md rounded px-8 pt-6 pb-8 mb-4">
      <h2 class="text-xl font-semibold mb-4">
        {{ editingUsername ? 'Sửa người dùng' : 'Thêm người dùng mới' }}
      </h2>
      <form @submit.prevent="saveUser">
        <div class="grid grid-cols-2 gap-4 mb-4">
          <div>
            <label class="block text-gray-700 text-sm font-bold mb-2">
              Tên đăng nhập
            </label>
            <input
              v-model="form.username"
              type="text"
              required
              :disabled="editingUsername"
              class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline disabled:bg-gray-100"
              placeholder="Nhập tên đăng nhập"
            />
          </div>
          <div>
            <label class="block text-gray-700 text-sm font-bold mb-2">
              Mật khẩu
            </label>
            <input
              v-model="form.password"
              type="password"
              required
              class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
              placeholder="Nhập mật khẩu"
            />
          </div>
        </div>
        
        <div class="grid grid-cols-3 gap-4 mb-4">
          <div>
            <label class="block text-gray-700 text-sm font-bold mb-2">
              Họ và tên
            </label>
            <input
              v-model="form.fullname"
              type="text"
              required
              class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
              placeholder="Nhập họ và tên"
            />
          </div>
          <div>
            <label class="block text-gray-700 text-sm font-bold mb-2">
              Vai trò
            </label>
            <select
              v-model="form.role"
              required
              class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
            >
              <option value="USER">USER</option>
              <option value="ADMIN">ADMIN</option>
            </select>
          </div>
          <div>
            <label class="block text-gray-700 text-sm font-bold mb-2">
              Trạng thái
            </label>
            <div class="flex items-center h-10">
              <input
                v-model="form.enabled"
                type="checkbox"
                class="w-4 h-4 text-blue-600 bg-gray-100 border-gray-300 rounded focus:ring-blue-500"
              />
              <label class="ml-2 text-sm font-medium text-gray-900">
                {{ form.enabled ? 'Kích hoạt' : 'Vô hiệu hóa' }}
              </label>
            </div>
          </div>
        </div>
        
        <div class="flex gap-2">
          <button
            type="submit"
            class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline"
          >
            {{ editingUsername ? 'Cập nhật' : 'Thêm mới' }}
          </button>
          <button
            v-if="editingUsername"
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
              Tên đăng nhập
            </th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              Họ và tên
            </th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              Vai trò
            </th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              Trạng thái
            </th>
            <th class="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
              Thao tác
            </th>
          </tr>
        </thead>
        <tbody class="bg-white divide-y divide-gray-200">
          <tr v-for="user in users" :key="user.username">
            <td class="px-6 py-4 whitespace-nowrap font-medium">{{ user.username }}</td>
            <td class="px-6 py-4 whitespace-nowrap">{{ user.fullname }}</td>
            <td class="px-6 py-4 whitespace-nowrap">
              <span 
                class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full"
                :class="user.role === 'ADMIN' ? 'bg-purple-100 text-purple-800' : 'bg-green-100 text-green-800'"
              >
                {{ user.role }}
              </span>
            </td>
            <td class="px-6 py-4 whitespace-nowrap">
              <span 
                class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full"
                :class="user.enabled ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'"
              >
                {{ user.enabled ? 'Kích hoạt' : 'Vô hiệu hóa' }}
              </span>
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
              <button
                @click="editUser(user)"
                class="text-indigo-600 hover:text-indigo-900 mr-3"
              >
                Sửa
              </button>
              <button
                @click="deleteUser(user.username)"
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

const users = ref([])
const form = ref({ 
  username: '', 
  password: '', 
  fullname: '',
  enabled: true,
  role: 'USER'
})
const editingUsername = ref(null)

// Load users
const loadUsers = async () => {
  try {
    const response = await $fetch(`${apiBase}/users`)
    users.value = response
  } catch (error) {
    console.error('Lỗi khi tải người dùng:', error)
  }
}

// Thêm/Sửa
const saveUser = async () => {
  try {
    if (editingUsername.value) {
      await $fetch(`${apiBase}/users/${editingUsername.value}`, {
        method: 'PUT',
        body: form.value
      })
    } else {
      await $fetch(`${apiBase}/users`, {
        method: 'POST',
        body: form.value
      })
    }
    form.value = { 
      username: '', 
      password: '', 
      fullname: '',
      enabled: true,
      role: 'USER'
    }
    editingUsername.value = null
    await loadUsers()
  } catch (error) {
    console.error('Lỗi khi lưu:', error)
  }
}

// Sửa
const editUser = (user) => {
  form.value = { ...user }
  editingUsername.value = user.username
}

// Hủy sửa
const cancelEdit = () => {
  form.value = { 
    username: '', 
    password: '', 
    fullname: '',
    enabled: true,
    role: 'USER'
  }
  editingUsername.value = null
}

// Xóa
const deleteUser = async (username) => {
  if (confirm('Bạn có chắc muốn xóa người dùng này?')) {
    try {
      await $fetch(`${apiBase}/users/${username}`, { method: 'DELETE' })
      await loadUsers()
    } catch (error) {
      console.error('Lỗi khi xóa:', error)
    }
  }
}

onMounted(() => {
  loadUsers()
})
</script>