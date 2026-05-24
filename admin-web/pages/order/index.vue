<template>
  <div>
    <div class="mf-page-header">
      <span class="mf-page-title">订单管理</span>
    </div>

    <!-- 状态 Tab -->
    <el-tabs v-model="query.status" @tab-change="loadOrders" class="mb-4">
      <el-tab-pane label="全部"   name="" />
      <el-tab-pane label="待付款" name="PENDING_PAYMENT" />
      <el-tab-pane label="待发货" name="PENDING_SHIPMENT" />
      <el-tab-pane label="已发货" name="SHIPPED" />
      <el-tab-pane label="已完成" name="COMPLETED" />
      <el-tab-pane label="已取消" name="CANCELLED" />
    </el-tabs>

    <div class="mf-card">
      <el-table v-loading="loading" :data="orders" style="width:100%">
        <el-table-column prop="orderNo"       label="订单号"  width="200" />
        <el-table-column prop="payAmount"     label="实付金额" width="110">
          <template #default="{ row }">
            <span class="price-text">¥{{ row.payAmount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status"        label="状态"    width="110">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)" size="small">
              {{ statusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="receiverName"  label="收货人"  width="100" />
        <el-table-column prop="receiverPhone" label="手机"    width="130" />
        <el-table-column prop="payMethod"     label="支付方式" width="100">
          <template #default="{ row }">{{ payMethodLabel(row.payMethod) }}</template>
        </el-table-column>
        <el-table-column prop="createdAt"     label="下单时间"  width="175" />
        <el-table-column label="操作"          width="180"    fixed="right">
          <template #default="{ row }">
            <el-button text size="small" @click="viewDetail(row)">详情</el-button>
            <el-button
              v-if="row.status === 'PENDING_SHIPMENT'"
              text size="small" type="primary"
              @click="openShipDialog(row)"
            >发货</el-button>
            <el-button
              v-if="row.status === 'PENDING_PAYMENT'"
              text size="small" type="danger"
              @click="cancelOrder(row)"
            >取消</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-bar">
        <el-pagination
          v-model:current-page="query.page"
          v-model:page-size="query.size"
          :total="total"
          layout="total, prev, pager, next"
          @change="loadOrders"
        />
      </div>
    </div>

    <!-- 发货弹窗 -->
    <el-dialog v-model="shipDialog.visible" title="确认发货" width="460px">
      <el-form :model="shipForm" label-width="100px">
        <el-form-item label="快递公司">
          <el-select v-model="shipForm.logisticsCompany" style="width:100%">
            <el-option label="顺丰速运" value="SF" />
            <el-option label="中通快递" value="ZTO" />
            <el-option label="圆通速递" value="YTO" />
            <el-option label="韵达速递" value="YD" />
            <el-option label="京东物流" value="JD" />
            <el-option label="EMS邮政" value="EMS" />
          </el-select>
        </el-form-item>
        <el-form-item label="快递单号">
          <el-input v-model="shipForm.logisticsNo" placeholder="请输入快递单号" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="shipDialog.visible = false">取消</el-button>
        <el-button type="primary" :loading="shipDialog.loading" @click="confirmShip">确认发货</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
const { $http } = useNuxtApp()
const router = useRouter()

const query   = reactive({ status: '', page: 1, size: 20 })
const orders  = ref<any[]>([])
const total   = ref(0)
const loading = ref(false)

const shipDialog = reactive({ visible: false, orderId: null as number | null, loading: false })
const shipForm   = reactive({ logisticsCompany: '', logisticsNo: '' })

const loadOrders = async () => {
  loading.value = true
  try {
    const params: any = { page: query.page, size: query.size }
    if (query.status) params.status = query.status
    const res: any = await ($http as any).get('/api/v1/orders', { params })
    orders.value = res.data
    total.value  = res.pagination?.total || 0
  } finally {
    loading.value = false
  }
}

const viewDetail    = (row: any) => router.push(`/order/${row.id}`)
const openShipDialog = (row: any) => {
  shipDialog.orderId  = row.id
  shipDialog.visible  = true
  shipForm.logisticsCompany = ''
  shipForm.logisticsNo = ''
}

const confirmShip = async () => {
  if (!shipForm.logisticsNo) { ElMessage.warning('请输入快递单号'); return }
  shipDialog.loading = true
  try {
    await ($http as any).patch(`/api/v1/orders/${shipDialog.orderId}/ship`, null, {
      params: { logisticsCompany: shipForm.logisticsCompany, logisticsNo: shipForm.logisticsNo }
    })
    ElMessage.success('发货成功')
    shipDialog.visible = false
    await loadOrders()
  } finally {
    shipDialog.loading = false
  }
}

const cancelOrder = async (row: any) => {
  await ElMessageBox.confirm('确定要取消该订单吗？', '提示', { type: 'warning' })
  await ($http as any).patch(`/api/v1/orders/${row.id}/cancel`)
  ElMessage.success('订单已取消')
  await loadOrders()
}

const statusLabel = (s: string) => ({
  PENDING_PAYMENT: '待付款', PENDING_SHIPMENT: '待发货',
  SHIPPED: '已发货', COMPLETED: '已完成',
  CANCELLED: '已取消', REFUNDING: '退款中', REFUNDED: '已退款'
}[s] || s)

const statusTagType = (s: string) => ({
  PENDING_PAYMENT: 'warning', PENDING_SHIPMENT: '',
  SHIPPED: 'info', COMPLETED: 'success',
  CANCELLED: 'danger', REFUNDING: 'warning', REFUNDED: 'info'
}[s] || '')

const payMethodLabel = (m: string) => ({
  wechat: '微信支付', alipay: '支付宝', stripe: 'Stripe', paypal: 'PayPal', unionpay: '银联'
}[m] || m || '-')

onMounted(loadOrders)
</script>

<style scoped>
.mb-4 { margin-bottom: 16px; }
.price-text { font-weight: 600; color: #EF4444; }
.pagination-bar { display: flex; justify-content: flex-end; margin-top: 16px; }
</style>
