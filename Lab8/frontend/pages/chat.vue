<template>
  <div style="padding: 20px;">
    <h1>Quản lý nhân viên</h1>

    <!-- Form -->
    <div style="border: 1px solid #ccc; padding: 20px; margin-bottom: 20px;">
      <h2>{{ isEditing ? 'Cập nhật nhân viên' : 'Thêm nhân viên' }}</h2>
      
      <div v-if="isEditing" style="margin-bottom: 10px;">
        <label>Mã NV</label><br>
        <input v-model="form.maNV" disabled style="width: 300px; background: #eee;">
      </div>

      <div style="margin-bottom: 10px;">
        <label>Họ và tên *</label><br>
        <input v-model="form.hoTen" required style="width: 300px;">
      </div>

      <div style="margin-bottom: 10px;">
        <label>Địa chỉ *</label><br>
        <input v-model="form.diaChi" required style="width: 300px;">
      </div>

      <div style="margin-bottom: 10px;">
        <label>Giới tính *</label><br>
        <select v-model="form.gioiTinh" style="width: 300px;">
          <option :value="true">Nam</option>
          <option :value="false">Nữ</option>
        </select>
      </div>

      <button @click="save" style="padding: 10px 20px; margin-right: 10px;">
        {{ isEditing ? 'Cập nhật' : 'Thêm' }}
      </button>
      <button v-if="isEditing" @click="cancel" style="padding: 10px 20px;">
        Hủy
      </button>
    </div>

    <!-- Table -->
    <h2>Danh sách nhân viên</h2>
    <table border="1" cellpadding="10" cellspacing="0" style="width: 100%; border-collapse: collapse;">
      <thead>
        <tr>
          <th>MaNV</th>
          <th>Họ Tên</th>
          <th>Địa chỉ</th>
          <th>Giới tính</th>
          <th>Edit</th>
          <th>Delete</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="emp in employees" :key="emp.maNV">
          <td>{{ emp.maNV }}</td>
          <td>{{ emp.hoTen }}</td>
          <td>{{ emp.diaChi }}</td>
          <td>{{ emp.gioiTinh ? 'true' : 'false' }}</td>
          <td><button @click="edit(emp)">Edit</button></td>
          <td><button @click="deleteEmp(emp.maNV)">Delete</button></td>
        </tr>
        <tr v-if="employees.length === 0">
          <td colspan="6" style="text-align: center;">Chưa có nhân viên</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script>
import { ref as dbRef, push, set, remove, onValue } from 'firebase/database'

export default {
  data() {
    return {
      employees: [],
      form: {
        maNV: '',
        hoTen: '',
        diaChi: '',
        gioiTinh: true
      },
      isEditing: false
    }
  },

  mounted() {
    this.loadEmployees()
  },

  methods: {
    loadEmployees() {
      const employeesRef = dbRef(this.$db, 'employees')
      onValue(employeesRef, (snapshot) => {
        const data = snapshot.val()
        this.employees = []
        if (data) {
          Object.keys(data).forEach(key => {
            this.employees.push({
              maNV: key,
              hoTen: data[key].hoTen,
              diaChi: data[key].diaChi,
              gioiTinh: data[key].gioiTinh
            })
          })
        }
      })
    },

    save() {
      if (!this.form.hoTen || !this.form.diaChi) {
        alert('Vui lòng nhập đầy đủ thông tin!')
        return
      }

      if (this.isEditing) {
        // Update
        const empRef = dbRef(this.$db, `employees/${this.form.maNV}`)
        set(empRef, {
          hoTen: this.form.hoTen,
          diaChi: this.form.diaChi,
          gioiTinh: this.form.gioiTinh
        }).then(() => {
          alert('Cập nhật thành công!')
          this.cancel()
        })
      } else {
        // Create
        const employeesRef = dbRef(this.$db, 'employees')
        const newEmpRef = push(employeesRef)
        set(newEmpRef, {
          hoTen: this.form.hoTen,
          diaChi: this.form.diaChi,
          gioiTinh: this.form.gioiTinh
        }).then(() => {
          alert('Thêm thành công!')
          this.form.hoTen = ''
          this.form.diaChi = ''
          this.form.gioiTinh = true
        })
      }
    },

    edit(emp) {
      this.form = { ...emp }
      this.isEditing = true
      window.scrollTo(0, 0)
    },

    cancel() {
      this.form = {
        maNV: '',
        hoTen: '',
        diaChi: '',
        gioiTinh: true
      }
      this.isEditing = false
    },

    deleteEmp(id) {
      if (confirm('Bạn có chắc muốn xóa nhân viên này?')) {
        const empRef = dbRef(this.$db, `employees/${id}`)
        remove(empRef).then(() => {
          alert('Xóa thành công!')
        })
      }
    }
  }
}
</script>