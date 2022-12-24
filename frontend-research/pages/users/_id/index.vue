<template>
  <v-container>
    <v-row>
      <v-col cols="12">
        <v-card>
          <v-card-title> {{ user.login }} </v-card-title>
          <v-card-text>
            <pre><code class="d-block">{{ user }}</code></pre>
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
import { defineComponent, PropType, toRefs } from '@nuxtjs/composition-api'
import { User } from '~/models'
export default defineComponent({
  props: {
    user: {
      type: Object as PropType<User>,
      required: true,
    },
  },
  setup(props) {
    const { user } = toRefs(props)

    return {
      user,
    }
  },
})
</script>
