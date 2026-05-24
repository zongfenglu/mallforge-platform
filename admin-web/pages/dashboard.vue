<template>
  <div>
    <div class="mf-page-header">
      <span class="mf-page-title">数据概览</span>
      <span class="text-sm text-gray-400">{{ today }}</span>
    </div>

    <!-- KPI 卡片 -->
    <div class="kpi-grid">
      <div class="kpi-card" v-for="kpi in kpis" :key="kpi.label">
        <div class="kpi-icon" :style="{ background: kpi.bg }">
          <el-icon><component :is="kpi.icon" /></el-icon>
        </div>
        <div class="kpi-body">
          <div class="kpi-value">{{ kpi.value }}</div>
          <div class="kpi-label">{{ kpi.label }}</div>
          <div class="kpi-trend" :class="kpi.trendUp ? 'up' : 'down'">
            <el-icon><component :is="kpi.trendUp ? 'Top' : 'Bottom'" /></el-icon>
            {{ kpi.trend }}
          </div>
        </div>
      </div>
    </div>

    <!-- 图表区 -->
    <div class="charts-row">
      <div class="mf-card chart-card">
        <div class="chart-title">近7日销售趋势</div>
        <v-chart :option="salesOption" style="height: 300px;" />
      </div>
      <div class="mf-card chart-card">
        <div class="chart-title">订单状态分布</div>
        <v-chart :option="orderPieOption" style="height: 300px;" />
      </div>
    </div>

    <!-- 最新订单 -->
    <div class="mf-card mt-6">
      <div class="chart-title">最新订单</div>
      <el-table :data="recentOrders" style="width:100%" size="small">
        <el-table-column prop="orderNo"       label="订单号"   width="180" />
        <el-table-column prop="payAmount"     label="金额"     width="100">
          <template #default="{ row }">¥{{ row.payAmount }}</template>
        </el-table-column>
        <el-table-column prop="status"        label="状态"     width="120">
          <template #default="{ row }">
            <el-tag :type="statusTag(row.status)" size="small">{{ statusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="receiverName"  label="收货人"   />
        <el-table-column prop="createdAt"     label="下单时间"  />
        <el-table-column label="操作" width="80">
          <template #default="{ row }">
            <el-button text size="small" @click="$router.push(`/order/${row.id}`)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { LineChart, PieChart } from 'echarts/charts'
import { TitleComponent, TooltipComponent, LegendComponent, GridComponent } from 'echarts/components'
import VChart from 'vue-echarts'
import dayjs from 'dayjs'

use([CanvasRenderer, LineChart, PieChart, TitleComponent, TooltipComponent, LegendComponent, GridComponent])

const { $http } = useNuxtApp()
const today = dayjs().format('YYYY年MM月DD日')

// KPI 数据（实际项目从 /api/v1/dashboard 获取）
const kpis = ref([
  { label: '今日销售额', value: '¥12,480', trend: '较昨日 +18%', trendUp: true,  icon: 'Money',    bg: 'linear-gradient(135deg,#6C3AFA,#8B5CF6)' },
  { label: '今日订单数', value: '86',       trend: '较昨日 +12%', trendUp: true,  icon: 'List',     bg: 'linear-gradient(135deg,#06B6D4,#0EA5E9)' },
  { label: '商品总数',   value: '1,248',    trend: '上架 896 件', trendUp: true,  icon: 'Goods',    bg: 'linear-gradient(135deg,#10B981,#34D399)' },
  { label: '待处理订单', value: '23',       trend: '需及时处理',  trendUp: false, icon: 'Warning',  bg: 'linear-gradient(135deg,#F59E0B,#FBBF24)' },
])

// 近7日销售趋势
const days  = Array.from({ length: 7 }, (_, i) => dayjs().subtract(6 - i, 'day').format('MM-DD'))
const sales = [3200, 4800, 3900, 6100, 5200, 8400, 7600]

const salesOption = computed(() => ({
  tooltip: { trigger: 'axis' },
  grid:    { left: 40, right: 20, bottom: 30, top: 20 },
  xAxis:   { type: 'category', data: days, axisLine: { lineStyle: { color: '#E5E7EB' } } },
  yAxis:   { type: 'value',    axisLabel: { formatter: '¥{value}' } },
  series:  [{
    name: '销售额', type: 'line', smooth: true, data: sales,
    lineStyle: { color: '#6C3AFA', width: 3 },
    areaStyle: { color: { type: 'linear', x:0, y:0, x2:0, y2:1,
      colorStops: [{ offset:0, color:'rgba(108,58,250,0.25)' }, { offset:1, color:'rgba(108,58,250,0)' }] } },
    itemStyle: { color: '#6C3AFA' },
  }],
}))

// 订单状态分布
const orderPieOption = computed(() => ({
  tooltip: { trigger: 'item' },
  legend:  { bottom: 0, orient: 'horizontal' },
  series:  [{
    name: '订单状态', type: 'pie', radius: ['45%', '70%'],
    data: [
      { value: 23, name: '待付款', itemStyle: { color: '#F59E0B' } },
      { value: 15, name: '待发货', itemStyle: { color: '#6C3AFA' } },
      { value: 38, name: '已发货', itemStyle: { color: '#06B6D4' } },
      { value: 86, name: '已完成', itemStyle: { color: '#10B981' } },
      { value: 5,  name: '已取消', itemStyle: { color: '#9CA3AF' } },
    ],
    label: { show: false },
  }],
}))

// 最新订单
const recentOrders = ref([
  { id: 1, orderNo: 'MF202605240001', payAmount: '288.00', status: 'PENDING_SHIPMENT', receiverName: '张三', createdAt: '2026-05-24 10:30' },
  { id: 2, orderNo: 'MF202605240002', payAmount: '128.00', status: 'PENDING_PAYMENT',  receiverName: '李四', createdAt: '2026-05-24 10:15' },
  { id: 3, orderNo: 'MF202605240003', payAmount: '568.00', status: 'SHIPPED',          receiverName: '王五', createdAt: '2026-05-24 09:48' },
])

const statusLabel = (s: string) => ({
  PENDING_PAYMENT: '待付款', PENDING_SHIPMENT: '待发货',
  SHIPPED: '已发货', COMPLETED: '已完成', CANCELLED: '已取消'
}[s] || s)

const statusTag = (s: string) => ({
  PENDING_PAYMENT: 'warning', PENDING_SHIPMENT: '', SHIPPED: 'info',
  COMPLETED: 'success', CANCELLED: 'danger'
}[s] || '')
</script>

<style scoped>
.kpi-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 24px;
}
.kpi-card {
  background: var(--mf-bg-card);
  border-radius: var(--mf-radius);
  box-shadow: var(--mf-shadow);
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
}
.kpi-icon {
  width: 52px; height: 52px;
  border-radius: 12px;
  display: grid; place-items: center;
  color: #fff;
  font-size: 24px;
  flex-shrink: 0;
}
.kpi-value  { font-size: 24px; font-weight: 700; }
.kpi-label  { font-size: 13px; color: var(--mf-text-secondary); margin-top: 2px; }
.kpi-trend  { font-size: 12px; margin-top: 4px; display: flex; align-items: center; gap: 2px; }
.kpi-trend.up   { color: #10B981; }
.kpi-trend.down { color: #EF4444; }

.charts-row {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 16px;
  margin-bottom: 0;
}
.chart-card { }
.chart-title { font-size: 15px; font-weight: 600; margin-bottom: 16px; }
.mt-6 { margin-top: 24px; }
</style>
