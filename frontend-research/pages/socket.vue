<template>
  <v-row>
    <v-col>
      <v-card>
        <v-card-title>Socket</v-card-title>
      </v-card>
    </v-col>
  </v-row>
</template>

<script lang="ts">
import {
  defineComponent,
  onMounted,
  onUnmounted,
  ref,
} from '@nuxtjs/composition-api'
import { useAuthStore } from '~/store'
export default defineComponent({
  setup() {
    const authStore = useAuthStore()

    const socket = ref<WebSocket>()

    const connect = () => {
      const url = `ws://${window.location.host}/api/websocket`
      //   const url = 'ws://127.0.01:4567/websocket'
      // , [authStore.tokens?.accessToken || '']
      socket.value = new WebSocket(url)

      socket.value.onopen = (event) => {
        socket.value!.send(
          JSON.stringify({
            event: 'authenticate',
            payload: {
              accessToken: authStore.tokens?.accessToken,
            },
          })
        )
      }
      socket.value.onerror = console.log
      socket.value.onmessage = console.log
    }

    const disconnect = () => {
      socket.value?.close()
    }

    onMounted(connect)
    onUnmounted(disconnect)
  },
})
</script>
