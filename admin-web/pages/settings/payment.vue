<template>
  <div>
    <div class="mf-page-header">
      <span class="mf-page-title">支付设置</span>
    </div>

    <div class="channels-grid">
      <div
        v-for="ch in channels"
        :key="ch.channel"
        class="channel-card mf-card"
      >
        <!-- 头部 -->
        <div class="ch-header">
          <div class="ch-icon" :style="{ background: ch.color }">{{ ch.icon }}</div>
          <div class="ch-info">
            <div class="ch-name">{{ ch.label }}</div>
            <div class="ch-desc">{{ ch.desc }}</div>
          </div>
          <el-switch
            v-model="ch.enabled"
            @change="toggleChannel(ch)"
            class="ch-switch"
          />
        </div>

        <!-- 配置表单 -->
        <el-collapse-transition>
          <div v-show="ch.expanded" class="ch-form">
            <el-form :model="ch.config" label-position="top" size="small">
              <template v-for="field in ch.fields" :key="field.key">
                <el-form-item :label="field.label">
                  <el-input
                    v-if="field.type !== 'file'"
                    v-model="ch.config[field.key]"
                    :type="field.secret ? 'password' : 'text'"
                    :show-password="field.secret"
                    :placeholder="field.placeholder"
                  />
                  <div v-else class="file-upload">
                    <el-button size="small">上传证书文件</el-button>
                    <span class="file-hint">{{ field.placeholder }}</span>
                  </div>
                </el-form-item>
              </template>
              <el-form-item>
                <el-checkbox v-model="ch.config.enableTest">启用沙箱/测试模式</el-checkbox>
              </el-form-item>
            </el-form>

            <!-- 操作栏 -->
            <div class="ch-actions">
              <el-button size="small" :loading="ch.testing" @click="testConnection(ch)">
                测试连接
              </el-button>
              <div class="test-result" v-if="ch.testResult">
                <el-icon :class="ch.testResult.ok ? 'ok' : 'fail'">
                  <component :is="ch.testResult.ok ? 'CircleCheck' : 'CircleClose'" />
                </el-icon>
                {{ ch.testResult.message }}
              </div>
              <el-button size="small" type="primary" @click="saveChannel(ch)">保存</el-button>
            </div>
          </div>
        </el-collapse-transition>

        <div class="ch-expand" @click="ch.expanded = !ch.expanded">
          <el-icon><component :is="ch.expanded ? 'ArrowUp' : 'ArrowDown'" /></el-icon>
          {{ ch.expanded ? '收起' : '展开配置' }}
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
const { $http } = useNuxtApp()

interface ChannelConfig {
  channel: string
  label: string
  desc: string
  icon: string
  color: string
  enabled: boolean
  expanded: boolean
  testing: boolean
  testResult: { ok: boolean; message: string } | null
  configId: number | null
  config: Record<string, any>
  fields: Array<{ key: string; label: string; type?: string; secret?: boolean; placeholder?: string }>
}

const channels = ref<ChannelConfig[]>([
  {
    channel: 'wechat', label: '微信支付', desc: '小程序/H5/Native 支付',
    icon: '微', color: '#07C160', enabled: false, expanded: false,
    testing: false, testResult: null, configId: null, config: {},
    fields: [
      { key: 'appId',        label: 'AppID',        placeholder: 'wx8a****c3d5' },
      { key: 'mchId',        label: '商户号(MchId)', placeholder: '148888****01' },
      { key: 'apiV3Key',     label: 'API v3 密钥',   secret: true, placeholder: '32位密钥' },
      { key: 'certSerialNo', label: '证书序列号',     placeholder: 'XXXXX' },
      { key: 'privateKey',   label: '商户私钥证书',   type: 'file', placeholder: 'apiclient_key.pem' },
      { key: 'notifyUrl',    label: '回调地址',       placeholder: 'https://api.xxx.com/api/v1/payment/notify/wechat?tenantId=1' },
    ],
  },
  {
    channel: 'alipay', label: '支付宝', desc: '手机网站/APP 支付',
    icon: '宝', color: '#1677FF', enabled: false, expanded: false,
    testing: false, testResult: null, configId: null, config: {},
    fields: [
      { key: 'appId',            label: 'AppID',       placeholder: '2021000000000000' },
      { key: 'privateKey',       label: '应用私钥',     secret: true, placeholder: 'RSA2 私钥' },
      { key: 'alipayPublicKey',  label: '支付宝公钥',   placeholder: '支付宝公钥字符串' },
      { key: 'notifyUrl',        label: '异步通知地址', placeholder: 'https://...' },
      { key: 'gatewayUrl',       label: '网关地址',     placeholder: 'https://openapi.alipay.com/gateway.do' },
    ],
  },
  {
    channel: 'stripe', label: 'Stripe', desc: '国际信用卡（Visa/MC/Amex）',
    icon: 'S', color: '#635BFF', enabled: false, expanded: false,
    testing: false, testResult: null, configId: null, config: {},
    fields: [
      { key: 'publishableKey', label: 'Publishable Key', placeholder: 'pk_live_...' },
      { key: 'secretKey',      label: 'Secret Key',      secret: true, placeholder: 'sk_live_...' },
      { key: 'webhookSecret',  label: 'Webhook Secret',  secret: true, placeholder: 'whsec_...' },
      { key: 'currency',       label: '货币',             placeholder: 'USD' },
    ],
  },
  {
    channel: 'paypal', label: 'PayPal', desc: '海外 PayPal 账户支付',
    icon: 'P', color: '#003087', enabled: false, expanded: false,
    testing: false, testResult: null, configId: null, config: {},
    fields: [
      { key: 'clientId',     label: 'Client ID',     placeholder: 'AXxx...' },
      { key: 'clientSecret', label: 'Client Secret', secret: true, placeholder: 'EXxx...' },
      { key: 'webhookId',    label: 'Webhook ID',    placeholder: 'WH-xxx' },
      { key: 'mode',         label: '模式',           placeholder: 'live 或 sandbox' },
    ],
  },
  {
    channel: 'unionpay', label: '银联支付', desc: '银行卡在线/企业网银',
    icon: '银', color: '#E60026', enabled: false, expanded: false,
    testing: false, testResult: null, configId: null, config: {},
    fields: [
      { key: 'merId',        label: '商户号',     placeholder: 'XXXXXXXXXX' },
      { key: 'signCertPwd',  label: '签名证书密码', secret: true },
      { key: 'frontUrl',     label: '前台通知URL', placeholder: 'https://...' },
      { key: 'backUrl',      label: '后台通知URL', placeholder: 'https://...' },
    ],
  },
])

const loadConfigs = async () => {
  try {
    const res: any = await ($http as any).get('/api/v1/payment/configs')
    const existing: any[] = res.data || []
    existing.forEach(cfg => {
      const ch = channels.value.find(c => c.channel === cfg.channel)
      if (ch) {
        ch.enabled  = cfg.status === 'enabled'
        ch.configId = cfg.id
      }
    })
  } catch (e) {}
}

const toggleChannel = async (ch: ChannelConfig) => {
  if (!ch.configId) {
    ch.expanded = true
    ElMessage.info('请先完成配置后保存')
    ch.enabled = false
    return
  }
  // 实际调用 saveChannel 更新状态
}

const saveChannel = async (ch: ChannelConfig) => {
  const body = {
    id:         ch.configId,
    channel:    ch.channel,
    channelName: ch.label,
    status:     ch.enabled ? 'enabled' : 'disabled',
    enableTest: ch.config.enableTest || false,
    configJson: JSON.stringify(ch.config),
  }
  await ($http as any).post('/api/v1/payment/configs', body)
  ElMessage.success('配置已保存')
  await loadConfigs()
}

const testConnection = async (ch: ChannelConfig) => {
  if (!ch.configId) { ElMessage.warning('请先保存配置'); return }
  ch.testing = true
  try {
    const res: any = await ($http as any).post(`/api/v1/payment/configs/${ch.configId}/test`)
    ch.testResult = {
      ok: res.data?.success,
      message: res.data?.success ? '连接正常' : '连接失败，请检查配置',
    }
  } finally {
    ch.testing = false
  }
}

onMounted(loadConfigs)
</script>

<style scoped>
.channels-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(480px, 1fr));
  gap: 16px;
}

.channel-card { padding: 20px; }

.ch-header {
  display: flex; align-items: center; gap: 14px; margin-bottom: 4px;
}
.ch-icon {
  width: 44px; height: 44px; border-radius: 10px;
  display: grid; place-items: center;
  color: #fff; font-size: 16px; font-weight: 700; flex-shrink: 0;
}
.ch-name { font-size: 15px; font-weight: 600; }
.ch-desc { font-size: 12px; color: var(--mf-text-muted); }
.ch-switch { margin-left: auto; }

.ch-form { padding-top: 16px; border-top: 1px solid #F3F4F6; margin-top: 12px; }

.ch-actions {
  display: flex; align-items: center; gap: 10px;
  margin-top: 12px; flex-wrap: wrap;
}
.test-result { display: flex; align-items: center; gap: 4px; font-size: 13px; }
.test-result .ok   { color: #10B981; }
.test-result .fail { color: #EF4444; }

.ch-expand {
  display: flex; align-items: center; gap: 4px; justify-content: center;
  font-size: 12px; color: var(--mf-text-muted); cursor: pointer;
  margin-top: 12px; padding-top: 12px; border-top: 1px solid #F3F4F6;
}
.ch-expand:hover { color: var(--mf-primary); }

.file-upload { display: flex; align-items: center; gap: 8px; }
.file-hint { font-size: 12px; color: var(--mf-text-muted); }
</style>
