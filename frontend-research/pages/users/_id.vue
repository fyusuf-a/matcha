<template>
  <div v-if="user">
    <nuxt-child :key="userId" :user="user" />
  </div>
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
      userId,
    }
  },
})
</script>
