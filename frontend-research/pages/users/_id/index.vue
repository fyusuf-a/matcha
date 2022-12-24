<template>
  <v-container>
    <v-row>
      <v-col cols="12">
        <v-card>
          <v-card-title> {{ user.login }} </v-card-title>
          <v-card-text>
            <pre><code class="d-block">{{ user }}</code></pre>
          </v-card-text>
          <v-card-text>
            <v-chip
              v-for="tag in tags"
              :key="tag.id"
              :color="tag.color"
              :to="`/tags/${tag.id}/users`"
            >
              #{{ tag.name }}
            </v-chip>
          </v-card-text>
          <v-card-actions>
            <like-button :peer="user" />
            <v-btn color="primary" :to="`/users/${user.id}/likes`">
              who liked?
            </v-btn>
          </v-card-actions>
        </v-card>
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
