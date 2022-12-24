<template>
  <v-container>
    <v-row>
      <v-col cols="12">
        <user-edit-info :user="user" />
      </v-col>
    </v-row>
  </v-container>
</template>

<script lang="ts">
import {
  defineComponent,
  PropType,
  ref,
  toRefs,
  useContext,
  useFetch,
} from '@nuxtjs/composition-api'
import { Tag, User } from '~/models'
export default defineComponent({
  props: {
    user: {
      type: Object as PropType<User>,
      required: true,
    },
  },
  setup(props) {
    const { user } = toRefs(props)
    const { $axios } = useContext()

    const tags = ref(Array<Tag>())
    useFetch(async () => {
      tags.value = (
        await $axios.$get(`/api/users/${user.value.id}/tags`)
      ).content
    })

    return {
      user,
      tags,
    }
  },
})
</script>
