<template>
  <v-row>
    <v-col>
      <v-card>
        <v-card-title> Messages </v-card-title>
        <v-list dense>
          <v-list-item v-for="message in messages" :key="message.id">
            <v-list-item-avatar>
              <v-avatar>
                <v-icon>mdi-account</v-icon>
              </v-avatar>
            </v-list-item-avatar>
            <v-list-item-content>
              <v-list-item-title>
                {{ getUser(message).login }}
              </v-list-item-title>
              <v-list-item-subtitle>
                {{ message.content }}
              </v-list-item-subtitle>
            </v-list-item-content>
          </v-list-item>
        </v-list>
        <v-card-text>
          <v-form @submit.prevent>
            <v-text-field
              v-model="content"
              solo-inverted
              hide-details
              @keydown.enter="send"
              autofocus
            />
          </v-form>
        </v-card-text>
      </v-card>
    </v-col>
  </v-row>
</template>

<script lang="ts">
import {
  defineComponent,
  onMounted,
  onUnmounted,
  PropType,
  ref,
  useContext,
} from '@nuxtjs/composition-api'
import { extractMessage } from '~/composables'
import { Message, User } from '~/models'
import { useAuthStore, useSocketStore } from '~/store'

const MESSAGE_CREATED_EVENT = 'message.created'

export default defineComponent({
  props: {
    user: {
      type: Object as PropType<User>,
      required: true,
    },
  },
  setup(props) {
    const { $axios, $dialog } = useContext()
    const authStore = useAuthStore()
    const socketStore = useSocketStore()

    const messages = ref(Array<Message>())

    const onPacket = (payload: Message) => {
      if (
        !(
          (payload.userId == authStore.user?.id &&
            payload.peerId == props.user.id) ||
          (payload.userId == props.user.id &&
            payload.peerId == authStore.user?.id)
        )
      ) {
        return
      }

      messages.value.push(payload)

      if (messages.value.length > 5) {
        messages.value.splice(0, 1)
      }
    }

    onMounted(() => socketStore.addListener(MESSAGE_CREATED_EVENT, onPacket))
    onUnmounted(() =>
      socketStore.removeListener(MESSAGE_CREATED_EVENT, onPacket)
    )

    const getUser = (message: Message) => {
      if (message.userId == authStore.user?.id) {
        return authStore.user!
      }

      return props.user
    }

    const content = ref('')
    const send = async () => {
      if (!content.value) {
        return
      }

      try {
        await $axios.$post('/api/messages', {
          content: content.value,
          peerId: props.user.id,
        })

        content.value = ''
      } catch (error) {
        const message = extractMessage(error)
        $dialog.notify.error(message)
      }
    }

    return {
      messages,
      getUser,
      content,
      send,
    }
  },
})
</script>
