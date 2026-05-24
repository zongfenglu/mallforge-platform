<template>
  <div>
    <div class="mf-page-header">
      <span class="mf-page-title">物流设置</span>
      <el-button type="primary" @click="openProviderDialog(null)">
        <el-icon><Plus /></el-icon> 添加物流商
      </el-button>
    </div>

    <!-- 物流服务商列表 -->
    <div class="mf-card mb-4">
      <div class="section-header">物流服务商配置</div>
      <el-table :data="providers" style="width:100%">
        <el-table-column prop="name"      label="服务商"   width="130" />
        <el-table-column prop="code"      label="编码"     width="80" />
        <el-table-column prop="customerName" label="客户编码" />
        <el-table-column prop="sendSite"  label="发件站点" />
        <el-table-column prop="status"   label="状态"     width="80">
          <template #default="{ row }">
            <el-tag :type="row.status ? 'success' : 'info'" size="small">
              {{ row.status ? '已启用' : '已停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="testedAt" label="最后测试" width="175" />
        <el-table-column label="操作"    width="160">
          <template #default="{ row }">
            <el-button text size="small" @click="testProvider(row)">测试</el-button>
            <el-button text size="small" @click="openProviderDialog(row)">编辑</el-button>
            <el-button text size="small" type="danger" @click="deleteProvider(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 发货地址管理 -->
    <div class="mf-card">
      <div class="section-header" style="justify-content:space-between; display:flex; align-items:center">
        <span>发货地址管理</span>
        <el-button size="small" type="primary" @click="openAddressDialog(null)">+ 新增地址</el-button>
      </div>
      <div class="address-list">
        <div v-for="addr in shipAddresses" :key="addr.id" class="address-card">
          <div class="addr-main">
            <el-tag v-if="addr.isDefault" type="success" size="small">默认</el-tag>
            <span class="addr-name">{{ addr.contactName }}</span>
            <span class="addr-phone">{{ addr.contactPhone }}</span>
          </div>
          <div class="addr-detail">{{ addr.province }}{{ addr.city }}{{ addr.district }}{{ addr.detail }}</div>
          <div class="addr-actions">
            <el-button text size="small" v-if="!addr.isDefault" @click="setDefault(addr)">设为默认</el-button>
            <el-button text size="small" @click="openAddressDialog(addr)">编辑</el-button>
            <el-button text size="small" type="danger" @click="deleteAddress(addr)">删除</el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 物流商配置弹窗 -->
    <el-dialog v-model="providerDialog.visible" :title="providerDialog.id ? '编辑物流商' : '添加物流商'" width="560px">
      <el-form :model="providerForm" label-width="100px">
        <el-form-item label="服务商">
          <el-select v-model="providerForm.code" style="width:100%">
            <el-option label="顺丰速运" value="SF" />
            <el-option label="中通快递" value="ZTO" />
            <el-option label="圆通速递" value="YTO" />
            <el-option label="韵达快递" value="YD" />
            <el-option label="申通快递" value="STO" />
            <el-option label="京东物流" value="JD" />
            <el-option label="EMS邮政" value="EMS" />
            <el-option label="德邦快递" value="DB" />
            <el-option label="极兔速递" value="JT" />
          </el-select>
        </el-form-item>
        <el-form-item label="API Key">
          <el-input v-model="providerForm.apiKey" placeholder="快递公司分配的 API Key" />
        </el-form-item>
        <el-form-item label="API Secret">
          <el-input v-model="providerForm.apiSecret" type="password" show-password />
        </el-form-item>
        <el-form-item label="客户编码">
          <el-input v-model="providerForm.customerName" placeholder="月结账号/客户编码" />
        </el-form-item>
        <el-form-item label="月结卡号">
          <el-input v-model="providerForm.monthlyCard" placeholder="月结卡号（可选）" />
        </el-form-item>
        <el-form-item label="发件站点">
          <el-input v-model="providerForm.sendSite" placeholder="如：深圳科技园营业部" />
        </el-form-item>
        <el-form-item label="发件人">
          <el-input v-model="providerForm.sendStaff" placeholder="发件联系人" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="providerDialog.visible = false">取消</el-button>
        <el-button type="primary" @click="saveProvider">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
const { $http } = useNuxtApp()

const providers     = ref<any[]>([])
const shipAddresses = ref<any[]>([])

const providerDialog = reactive({ visible: false, id: null as number | null })
const providerForm   = reactive<any>({
  code: '', apiKey: '', apiSecret: '', customerName: '',
  monthlyCard: '', sendSite: '', sendStaff: '',
})

const loadProviders = async () => {
  const res: any = await ($http as any).get('/api/v1/logistics/providers')
  providers.value = res.data || []
}
const loadAddresses = async () => {
  const res: any = await ($http as any).get('/api/v1/ship-addresses')
  shipAddresses.value = res.data || []
}

const openProviderDialog = (row: any) => {
  if (row) {
    Object.assign(providerForm, row)
    providerDialog.id = row.id
  } else {
    Object.assign(providerForm, { code: '', apiKey: '', apiSecret: '', customerName: '', monthlyCard: '', sendSite: '', sendStaff: '' })
    providerDialog.id = null
  }
  providerDialog.visible = true
}

const saveProvider = async () => {
  if (providerDialog.id) {
    await ($http as any).put(`/api/v1/logistics/providers/${providerDialog.id}`, providerForm)
  } else {
    await ($http as any).post('/api/v1/logistics/providers', providerForm)
  }
  ElMessage.success('保存成功')
  providerDialog.visible = false
  await loadProviders()
}

const testProvider = async (row: any) => {
  const res: any = await ($http as any).post(`/api/v1/logistics/providers/${row.id}/test`)
  ElMessage[res.data?.success ? 'success' : 'error'](res.data?.success ? '连接正常' : '连接失败')
}

const deleteProvider = async (row: any) => {
  await ElMessageBox.confirm('确定删除该物流商配置？', '提示', { type: 'warning' })
  await ($http as any).delete(`/api/v1/logistics/providers/${row.id}`)
  await loadProviders()
}

const openAddressDialog = (row: any) => { /* 省略实现 */ }
const setDefault = async (addr: any) => {
  await ($http as any).patch(`/api/v1/ship-addresses/${addr.id}/default`)
  await loadAddresses()
}
const deleteAddress = async (addr: any) => {
  await ($http as any).delete(`/api/v1/ship-addresses/${addr.id}`)
  await loadAddresses()
}

onMounted(() => { loadProviders(); loadAddresses() })
</script>

<style scoped>
.mb-4 { margin-bottom: 16px; }
.section-header { font-size: 15px; font-weight: 600; margin-bottom: 16px; }
.address-list { display: flex; flex-direction: column; gap: 12px; }
.address-card {
  border: 1px solid #E5E7EB; border-radius: 8px; padding: 14px 16px;
}
.addr-main { display: flex; align-items: center; gap: 8px; margin-bottom: 6px; }
.addr-name  { font-weight: 600; }
.addr-phone { color: var(--mf-text-secondary); font-size: 13px; }
.addr-detail { font-size: 13px; color: var(--mf-text-secondary); }
.addr-actions { margin-top: 8px; display: flex; gap: 4px; }
</style>
