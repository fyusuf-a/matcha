<template>
  <v-container v-if="user">
    <v-row>
      <v-col cols="12">
        <v-card>
          <v-card-title> {{ user.login }} </v-card-title>
          <v-card-text>
            <pre><code class="d-block">{{ user }}</code></pre>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script lang="ts">
import {
  defineComponent,
  ref,
  useContext,
  useFetch,
  watch,
} from '@nuxtjs/composition-api'
import { usePathValue } from '~/composables'
import { User } from '~/models'
export default defineComponent({
  setup() {
    const { $axios } = useContext()

    const user = ref<User>()
    const userId = usePathValue('id')

    const { fetch } = useFetch(async () => {
      user.value = await $axios.$get(`/api/users/${userId.value}`)
    })

    watch(userId, fetch)

    return {
      user,
    }
  },
})
</script>
