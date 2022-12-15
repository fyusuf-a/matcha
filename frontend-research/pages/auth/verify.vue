<template>
  <v-container>
    <v-row>
      <v-col cols="12">
        <v-card>
          <v-card-title> Verify </v-card-title>
          <v-card-text> Please verify your account! </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script lang="ts">
import {
  defineComponent,
  onMounted,
  onUnmounted,
  reactive,
  ref,
  unref,
  useContext,
  useRouter,
  watch,
} from '@nuxtjs/composition-api'
import { extractMessage } from '~/composables'
import { extractValidation } from '~/utils'
import { Tokens } from '~/models'
import { useAuthStore } from '~/store'
export default defineComponent({
  setup() {
    const { $dialog } = useContext()
    const router = useRouter()
    const authStore = useAuthStore()

    const interval = ref()
    onMounted(() => {
      interval.value = setInterval(async () => {
        const user = await authStore.fetchUser()

        if (!user) {
          router.push('/auth/login')
        } else if (user.emailConfirmed) {
          router.push('/')
        }
      }, 1000)
    })

    onUnmounted(() => {
      clearInterval(interval.value)
    })

    return {}
  },
})
</script>
