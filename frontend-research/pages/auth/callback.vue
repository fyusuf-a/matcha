<template>
  <div />
</template>

<script lang="ts">
import {
  defineComponent,
  useContext,
  useFetch,
  useRoute,
  useRouter,
} from '@nuxtjs/composition-api'
export default defineComponent({
  setup() {
    const { $axios } = useContext()
    const route = useRoute()
    const router = useRouter()

    useFetch(async () => {
      await $axios
        .$get('/api/auth/oauth/callback', {
          params: {
            code: route.value.query.code,
          },
        })
        .catch(console.log)

      router.replace('/')
    })

    return {}
  },
})
</script>
