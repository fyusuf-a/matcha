<template>
  <v-container>
    <v-row>
      <v-col cols="12">
        <v-card>
          <v-card-title> Email Confirmation </v-card-title>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script lang="ts">
import {
  computed,
  defineComponent,
  useContext,
  useFetch,
  useRoute,
  useRouter,
} from '@nuxtjs/composition-api'
import { Tokens } from '~/models'
import { extractMessage } from '~/composables'
import { useAuthStore } from '~/store'
export default defineComponent({
  setup() {
    const { $axios, $dialog } = useContext()

    const authStore = useAuthStore()

    const router = useRouter()
    const route = useRoute()

    const token = computed(() => route.value.query.token)
    const { fetchState } = useFetch(async () => {
      try {
        const { accessToken, refreshToken }: Tokens = await $axios.$post(
          '/api/auth/confirm',
          {
            token: token.value,
          }
        )

        authStore.updateTokens(accessToken, refreshToken)
        const user = await authStore.fetchUser()

        if (user) {
          $dialog.notify.success(`Welcome ${user.login}`)
        }
      } catch (error) {
        const message = extractMessage(error)
        $dialog.notify.error(message)
      }

      router.push('/')
    })

    return {}
  },
})
</script>
