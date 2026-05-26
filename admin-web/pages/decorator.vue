<template>
  <div class="decorator-page">
    <!-- 顶部工具栏 -->
    <div class="decorator-toolbar">
      <div class="toolbar-left">
        <el-input v-model="pageName" placeholder="页面名称" size="small" style="width:200px" />
      </div>
      <div class="toolbar-center">
        <el-button-group>
          <el-button size="small" :disabled="!canUndo" @click="undo">
            <el-icon><RefreshLeft /></el-icon> 撤销
          </el-button>
          <el-button size="small" :disabled="!canRedo" @click="redo">
            <el-icon><RefreshRight /></el-icon> 重做
          </el-button>
        </el-button-group>
      </div>
      <div class="toolbar-right">
        <el-button size="small" @click="saveDraft">保存草稿</el-button>
        <el-button size="small" type="primary" @click="publish">发布上线</el-button>
      </div>
    </div>

    <!-- 三栏编辑器 -->
    <div class="decorator-editor">

      <!-- 左侧：组件库 -->
      <div class="component-panel">
        <div v-for="group in componentGroups" :key="group.label" class="comp-group">
          <div class="comp-group-title">{{ group.label }}</div>
          <div class="comp-grid">
            <div
              v-for="comp in group.items"
              :key="comp.type"
              class="comp-item"
              draggable="true"
              @dragstart="onDragStart($event, comp)"
            >
              <el-icon><component :is="comp.icon" /></el-icon>
              <span>{{ comp.label }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 中间：手机预览 -->
      <div class="preview-wrapper">
        <div
          class="phone-frame"
          :style="{ background: pageBackground }"
          @dragover.prevent
          @drop="onDrop"
        >
          <div class="phone-statusbar">9:41</div>

          <div
            v-for="(comp, idx) in components"
            :key="comp.id"
            class="preview-component"
            :class="{ selected: selectedId === comp.id }"
            @click="selectComponent(comp.id)"
            draggable="true"
            @dragstart="onComponentDragStart($event, idx)"
            @dragover.prevent="onComponentDragOver($event, idx)"
            @drop.stop="onComponentDrop($event, idx)"
          >
            <component :is="previewRenderer(comp.type)" :props="comp.props" :type="comp.type" />
            <div class="comp-actions">
              <el-icon class="action-del" @click.stop="removeComponent(comp.id)"><Delete /></el-icon>
            </div>
          </div>

          <div v-if="components.length === 0" class="drop-hint">
            从左侧拖入组件开始搭建
          </div>
        </div>
      </div>

      <!-- 右侧：属性面板 & 图层 -->
      <div class="props-panel">
        <el-tabs>
          <el-tab-pane label="属性">
            <template v-if="selectedComponent">
              <div class="prop-title">{{ selectedComponent.type }}</div>

              <!-- 通用属性编辑（根据类型动态渲染） -->
              <template v-if="selectedComponent.type === 'banner'">
                <el-form label-position="top" size="small">
                  <el-form-item label="高度(px)">
                    <el-input-number v-model="selectedComponent.props.height" :min="80" :max="400" />
                  </el-form-item>
                  <el-form-item label="自动播放">
                    <el-switch v-model="selectedComponent.props.autoplay" />
                  </el-form-item>
                  <el-form-item label="间隔(秒)">
                    <el-input-number v-model="selectedComponent.props.interval" :min="1" :max="10" />
                  </el-form-item>
                </el-form>
              </template>

              <template v-else-if="selectedComponent.type === 'section-title'">
                <el-form label-position="top" size="small">
                  <el-form-item label="标题">
                    <el-input v-model="selectedComponent.props.title" />
                  </el-form-item>
                  <el-form-item label="显示更多">
                    <el-switch v-model="selectedComponent.props.showMore" />
                  </el-form-item>
                </el-form>
              </template>

              <template v-else-if="selectedComponent.type === 'product-grid'">
                <el-form label-position="top" size="small">
                  <el-form-item label="列数">
                    <el-radio-group v-model="selectedComponent.props.cols">
                      <el-radio-button :label="2">2列</el-radio-button>
                      <el-radio-button :label="3">3列</el-radio-button>
                    </el-radio-group>
                  </el-form-item>
                  <el-form-item label="显示数量">
                    <el-input-number v-model="selectedComponent.props.count" :min="2" :max="20" />
                  </el-form-item>
                </el-form>
              </template>

              <div v-else class="prop-empty">选择组件后编辑属性</div>
            </template>
            <div v-else class="prop-empty">点击预览区组件进行编辑</div>
          </el-tab-pane>

          <el-tab-pane label="图层">
            <div class="layer-list">
              <div
                v-for="(comp, idx) in components"
                :key="comp.id"
                class="layer-item"
                :class="{ active: selectedId === comp.id }"
                @click="selectComponent(comp.id)"
              >
                <el-icon class="layer-drag"><Rank /></el-icon>
                <span>{{ getCompLabel(comp.type) }}</span>
                <el-icon class="layer-del" @click.stop="removeComponent(comp.id)"><Delete /></el-icon>
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { nanoid } from 'nanoid'

const { $http } = useNuxtApp()
const route = useRoute()

interface PageComponent {
  id: string
  type: string
  order: number
  props: Record<string, any>
}

const pageId         = ref<number | null>(Number(route.query.id) || null)
const pageName       = ref('新页面')
const pageBackground = ref('#F5F5F5')
const components     = ref<PageComponent[]>([])
const selectedId     = ref<string | null>(null)
const undoStack      = ref<PageComponent[][]>([])
const redoStack      = ref<PageComponent[][]>([])

const selectedComponent = computed(() => components.value.find(c => c.id === selectedId.value))
const canUndo = computed(() => undoStack.value.length > 0)
const canRedo = computed(() => redoStack.value.length > 0)

// ── 组件库配置 ────────────────────────────────────────
const componentGroups = [
  {
    label: '基础组件',
    items: [
      { type: 'banner',        label: '轮播图',  icon: 'PictureFilled', defaultProps: { height: 180, autoplay: true, interval: 3, images: [] } },
      { type: 'search-bar',    label: '搜索栏',  icon: 'Search',        defaultProps: { placeholder: '搜索商品' } },
      { type: 'notice-bar',    label: '公告栏',  icon: 'Bell',          defaultProps: { text: '欢迎光临本店' } },
      { type: 'nav-grid',      label: '导航组',  icon: 'Grid',          defaultProps: { cols: 5, items: [] } },
      { type: 'section-title', label: '标题栏',  icon: 'Tickets',       defaultProps: { title: '精品推荐', showMore: true } },
      { type: 'divider',       label: '分割线',  icon: 'Minus',         defaultProps: { height: 12, color: '#F5F5F5' } },
    ],
  },
  {
    label: '商品组件',
    items: [
      { type: 'product-grid',      label: '商品网格', icon: 'Grid',      defaultProps: { cols: 2, count: 6, sourceType: 'auto', sourceValue: 'recommend' } },
      { type: 'product-list',      label: '商品列表', icon: 'List',      defaultProps: { style: 'card', count: 4 } },
      { type: 'product-recommend', label: '推荐商品', icon: 'Star',      defaultProps: { algo: 'sales', count: 4 } },
      { type: 'flash-product',     label: '秒杀商品', icon: 'Lightning', defaultProps: { showCountdown: true } },
    ],
  },
  {
    label: '营销组件',
    items: [
      { type: 'coupon-group', label: '优惠券',  icon: 'Ticket',  defaultProps: { style: 'slide' } },
      { type: 'flash-sale',   label: '限时抢购', icon: 'Timer',   defaultProps: { style: 'banner' } },
    ],
  },
]

const getCompLabel = (type: string) => {
  for (const g of componentGroups) {
    const found = g.items.find(i => i.type === type)
    if (found) return found.label
  }
  return type
}

// ── 拖拽 ─────────────────────────────────────────────
let dragComp: any = null
let dragFromIdx: number | null = null
let dragOverIdx: number | null = null

const onDragStart = (e: DragEvent, comp: any) => { dragComp = comp; dragFromIdx = null }
const onComponentDragStart = (e: DragEvent, idx: number) => { dragFromIdx = idx; dragComp = null }
const onComponentDragOver  = (_e: DragEvent, idx: number) => { dragOverIdx = idx }

const onDrop = () => {
  if (!dragComp) return
  pushUndo()
  const newComp: PageComponent = {
    id: nanoid(8), type: dragComp.type,
    order: components.value.length,
    props: { ...dragComp.defaultProps },
  }
  components.value.push(newComp)
  selectedId.value = newComp.id
  dragComp = null
}

const onComponentDrop = (_e: DragEvent, toIdx: number) => {
  if (dragFromIdx === null || dragOverIdx === null) return
  pushUndo()
  const arr = [...components.value]
  const [moved] = arr.splice(dragFromIdx, 1)
  arr.splice(dragOverIdx, 0, moved)
  components.value = arr.map((c, i) => ({ ...c, order: i }))
  dragFromIdx = null; dragOverIdx = null
}

// ── 操作 ─────────────────────────────────────────────
const selectComponent = (id: string) => { selectedId.value = id }
const removeComponent  = (id: string) => {
  pushUndo()
  components.value = components.value.filter(c => c.id !== id)
  if (selectedId.value === id) selectedId.value = null
}

const pushUndo = () => {
  undoStack.value.push(JSON.parse(JSON.stringify(components.value)))
  if (undoStack.value.length > 20) undoStack.value.shift()
  redoStack.value = []
}
const undo = () => {
  if (!canUndo.value) return
  redoStack.value.push(JSON.parse(JSON.stringify(components.value)))
  components.value = undoStack.value.pop()!
}
const redo = () => {
  if (!canRedo.value) return
  undoStack.value.push(JSON.parse(JSON.stringify(components.value)))
  components.value = redoStack.value.pop()!
}

// ── 保存 & 发布 ──────────────────────────────────────
const buildPageJson = () => JSON.stringify({
  version: 1, name: pageName.value, background: pageBackground.value,
  components: components.value.map((c, i) => ({ ...c, order: i })),
})

const saveDraft = async () => {
  const body = { name: pageName.value, pageJson: buildPageJson(), type: 'home' }
  if (pageId.value) {
    await ($http as any).put(`/api/v1/pages/${pageId.value}`, body)
  } else {
    const res: any = await ($http as any).post('/api/v1/pages', body)
    pageId.value = res.data?.id
  }
  ElMessage.success('草稿已保存')
}

const publish = async () => {
  await saveDraft()
  await ($http as any).post(`/api/v1/pages/${pageId.value}/publish`)
  ElMessage.success('已发布上线')
}

// ── 预览渲染 ─────────────────────────────────────────
import BannerPreview from '~/components/decorator/BannerPreview.vue'
import SectionTitlePreview from '~/components/decorator/SectionTitlePreview.vue'
import ProductGridPreview from '~/components/decorator/ProductGridPreview.vue'
import CouponGroupPreview from '~/components/decorator/CouponGroupPreview.vue'
import DefaultPreview from '~/components/decorator/DefaultPreview.vue'

const previewComponentMap: Record<string, any> = {
  'banner': BannerPreview,
  'section-title': SectionTitlePreview,
  'product-grid': ProductGridPreview,
  'product-list': ProductGridPreview,
  'product-recommend': ProductGridPreview,
  'flash-product': ProductGridPreview,
  'coupon-group': CouponGroupPreview,
  'flash-sale': CouponGroupPreview,
}

const previewRenderer = (type: string) => {
  return previewComponentMap[type] || DefaultPreview
}
</script>

<style scoped>
.decorator-page {
  display: flex; flex-direction: column;
  height: calc(100vh - var(--mf-topbar-h) - 48px);
  margin: -24px;
}

.decorator-toolbar {
  display: flex; align-items: center; justify-content: space-between;
  padding: 10px 16px;
  background: #fff;
  border-bottom: 1px solid #E5E7EB;
  flex-shrink: 0;
}
.toolbar-center, .toolbar-left, .toolbar-right { display: flex; align-items: center; gap: 8px; }

.decorator-editor {
  display: grid;
  grid-template-columns: 240px 1fr 280px;
  flex: 1; overflow: hidden;
}

/* ── 组件库 ── */
.component-panel {
  background: #fff; border-right: 1px solid #E5E7EB;
  overflow-y: auto; padding: 12px;
}
.comp-group-title {
  font-size: 11px; color: var(--mf-text-muted); font-weight: 600;
  letter-spacing: 0.05em; padding: 8px 4px 6px; text-transform: uppercase;
}
.comp-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 6px; margin-bottom: 8px; }
.comp-item {
  display: flex; flex-direction: column; align-items: center; gap: 4px;
  padding: 10px 4px; border-radius: 8px;
  border: 1px dashed #E5E7EB; cursor: grab;
  font-size: 12px; color: var(--mf-text-secondary);
  transition: all 0.15s;
}
.comp-item .el-icon { font-size: 20px; color: var(--mf-primary); }
.comp-item:hover { border-color: var(--mf-primary); background: #F5F3FF; }

/* ── 预览区 ── */
.preview-wrapper {
  background: #E5E7EB; display: flex;
  align-items: flex-start; justify-content: center;
  padding: 24px; overflow-y: auto;
}
.phone-frame {
  width: 375px; min-height: 667px;
  background: #fff;
  border-radius: 36px;
  box-shadow: 0 0 0 8px #1F2937, 0 24px 48px rgba(0,0,0,0.35);
  overflow: hidden; position: relative;
}
.phone-statusbar {
  height: 28px; background: rgba(0,0,0,0.05);
  display: flex; align-items: center; padding: 0 20px;
  font-size: 12px; font-weight: 600;
}
.preview-component {
  position: relative; cursor: pointer;
  border: 2px solid transparent; transition: border-color 0.15s;
}
.preview-component.selected { border-color: var(--mf-primary); }
.comp-actions {
  display: none; position: absolute; top: 4px; right: 4px;
  background: rgba(0,0,0,0.5); border-radius: 6px; padding: 4px;
}
.preview-component.selected .comp-actions { display: flex; }
.action-del { color: #fff; cursor: pointer; }
.drop-hint {
  margin: 80px auto; text-align: center;
  color: var(--mf-text-muted); font-size: 14px;
}

/* ── 属性面板 ── */
.props-panel {
  background: #fff; border-left: 1px solid #E5E7EB;
  overflow-y: auto; padding: 12px;
}
.prop-title { font-weight: 600; font-size: 14px; margin-bottom: 12px; }
.prop-empty { color: var(--mf-text-muted); font-size: 13px; text-align: center; padding: 40px 0; }

/* ── 图层 ── */
.layer-item {
  display: flex; align-items: center; gap: 8px;
  padding: 8px 10px; border-radius: 8px; cursor: pointer;
  font-size: 13px; margin-bottom: 4px;
}
.layer-item:hover { background: #F3F4F6; }
.layer-item.active { background: #EDE9FE; color: var(--mf-primary); }
.layer-drag { color: #9CA3AF; cursor: grab; }
.layer-del  { color: #9CA3AF; margin-left: auto; }
.layer-del:hover { color: var(--mf-danger); }
</style>
