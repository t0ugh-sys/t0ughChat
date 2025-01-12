<template>
  <div class="role-list">
    <!-- 角色列表 -->
    <el-card shadow="never" class="list-container">
      <template #header>
        <div class="card-header">
          <span>角色列表</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon> 新增角色
          </el-button>
        </div>
      </template>

      <el-table
        v-loading="loading"
        :data="roleList"
        style="width: 100%"
        border
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column prop="name" label="角色名称" min-width="120" />
        <el-table-column prop="code" label="角色标识" min-width="120" />
        <el-table-column prop="description" label="描述" min-width="200" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 'enabled' ? 'success' : 'danger'" size="small">
              {{ row.status === 'enabled' ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="160">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button-group>
              <el-button type="primary" link @click="handleEdit(row)">
                编辑
              </el-button>
              <el-button type="primary" link @click="handlePermission(row)">
                权限设置
              </el-button>
              <el-button
                type="primary"
                link
                @click="handleToggleStatus(row)"
                :loading="row.statusLoading"
              >
                {{ row.status === 'enabled' ? '禁用' : '启用' }}
              </el-button>
              <el-button
                type="danger"
                link
                @click="handleDelete(row)"
                :disabled="row.code === 'admin'"
              >
                删除
              </el-button>
            </el-button-group>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 角色表单对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '新增角色' : '编辑角色'"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="formRef"
        :model="roleForm"
        :rules="rules"
        label-width="80px"
        class="role-form"
      >
        <el-form-item label="角色名称" prop="name">
          <el-input v-model="roleForm.name" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="角色标识" prop="code">
          <el-input
            v-model="roleForm.code"
            placeholder="请输入角色标识"
            :disabled="dialogType === 'edit'"
          />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="roleForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入角色描述"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="roleForm.status">
            <el-radio label="enabled">启用</el-radio>
            <el-radio label="disabled">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 权限设置对话框 -->
    <el-dialog
      v-model="permissionDialogVisible"
      title="权限设置"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-tree
        ref="permissionTreeRef"
        :data="permissionTree"
        :props="{ label: 'name', children: 'children' }"
        show-checkbox
        node-key="id"
        :default-checked-keys="checkedPermissions"
        :default-expand-all="true"
      />
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="permissionDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handlePermissionSubmit" :loading="assigningPermission">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { formatDate } from '@/utils/date'
import {
  getRoles,
  createRole,
  updateRole,
  deleteRole,
  getPermissions,
  assignPermissions
} from '@/api/admin'

// 角色列表
const loading = ref(false)
const roleList = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 角色表单对话框
const dialogVisible = ref(false)
const dialogType = ref('add')
const formRef = ref(null)
const submitting = ref(false)
const roleForm = ref({
  name: '',
  code: '',
  description: '',
  status: 'enabled'
})

const rules = {
  name: [
    { required: true, message: '请输入角色名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在2-50个字符之间', trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入角色标识', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在2-50个字符之间', trigger: 'blur' }
  ]
}

// 权限设置对话框
const permissionDialogVisible = ref(false)
const permissionTreeRef = ref(null)
const permissionTree = ref([])
const checkedPermissions = ref([])
const currentRoleId = ref(null)
const assigningPermission = ref(false)

// 获取角色列表
const fetchRoles = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value - 1,
      size: pageSize.value
    }
    const response = await getRoles(params)
    roleList.value = response.content
    total.value = response.totalElements
  } catch (error) {
    console.error('获取角色列表失败:', error)
    ElMessage.error('获取角色列表失败')
  } finally {
    loading.value = false
  }
}

// 获取权限树
const fetchPermissionTree = async () => {
  try {
    const response = await getPermissions()
    permissionTree.value = response
  } catch (error) {
    console.error('获取权限列表失败:', error)
    ElMessage.error('获取权限列表失败')
  }
}

// 新增角色
const handleAdd = () => {
  dialogType.value = 'add'
  roleForm.value = {
    name: '',
    code: '',
    description: '',
    status: 'enabled'
  }
  dialogVisible.value = true
}

// 编辑角色
const handleEdit = (row) => {
  dialogType.value = 'edit'
  roleForm.value = {
    id: row.id,
    name: row.name,
    code: row.code,
    description: row.description,
    status: row.status
  }
  dialogVisible.value = true
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    submitting.value = true

    if (dialogType.value === 'add') {
      await createRole(roleForm.value)
    } else {
      await updateRole(roleForm.value.id, roleForm.value)
    }

    ElMessage.success(dialogType.value === 'add' ? '创建成功' : '更新成功')
    dialogVisible.value = false
    fetchRoles()
  } catch (error) {
    console.error(dialogType.value === 'add' ? '创建失败:' : '更新失败:', error)
    ElMessage.error(dialogType.value === 'add' ? '创建失败' : '更新失败')
  } finally {
    submitting.value = false
  }
}

// 权限设置
const handlePermission = async (row) => {
  currentRoleId.value = row.id
  checkedPermissions.value = row.permissions?.map(p => p.id) || []
  await fetchPermissionTree()
  permissionDialogVisible.value = true
}

// 提交权限设置
const handlePermissionSubmit = async () => {
  if (!permissionTreeRef.value) return

  try {
    assigningPermission.value = true
    const checkedKeys = permissionTreeRef.value.getCheckedKeys()
    const halfCheckedKeys = permissionTreeRef.value.getHalfCheckedKeys()
    const permissions = [...checkedKeys, ...halfCheckedKeys]

    await assignPermissions(currentRoleId.value, permissions)
    ElMessage.success('权限设置成功')
    permissionDialogVisible.value = false
    fetchRoles()
  } catch (error) {
    console.error('权限设置失败:', error)
    ElMessage.error('权限设置失败')
  } finally {
    assigningPermission.value = false
  }
}

// 更新角色状态
const handleToggleStatus = async (row) => {
  try {
    row.statusLoading = true
    const newStatus = row.status === 'enabled' ? 'disabled' : 'enabled'
    await updateRole(row.id, { status: newStatus })
    row.status = newStatus
    ElMessage.success('状态更新成功')
  } catch (error) {
    console.error('状态更新失败:', error)
    ElMessage.error('状态更新失败')
  } finally {
    row.statusLoading = false
  }
}

// 删除角色
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该角色吗？此操作不可恢复', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await deleteRole(row.id)
    ElMessage.success('删除成功')
    if (roleList.value.length === 1 && currentPage.value > 1) {
      currentPage.value--
    }
    fetchRoles()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 分页
const handleSizeChange = (val) => {
  pageSize.value = val
  fetchRoles()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchRoles()
}

onMounted(() => {
  fetchRoles()
})
</script>

<style scoped>
.role-list {
  padding: 20px;
}

.list-container {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.role-form {
  padding: 20px 0;
}

:deep(.el-dialog__body) {
  padding-top: 10px;
}

:deep(.el-tree) {
  max-height: 400px;
  overflow-y: auto;
}

:deep(.el-tree-node__content) {
  height: 32px;
}
</style> 