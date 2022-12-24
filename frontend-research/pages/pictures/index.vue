<template>
  <v-container>
    <v-row>
      <v-col cols="12">
        <v-card>
          <v-card-title> Pictures </v-card-title>
          <v-card-text>
            <v-file-input
              v-model="file"
              solo-inverted
              placeholder="New picture..."
            />
          </v-card-text>
        </v-card>
      </v-col>
      <v-col v-for="picture in page.content" :key="picture.id">
        <v-card>
          <v-card-text class="d-flex justify-center">
            <v-img
              min-height="400"
              max-height="400"
              min-width="400"
              max-width="400"
              :src="`/api/pictures/${picture.id}/view`"
            />
          </v-card-text>
          <v-card-actions>
            <v-btn color="error" class="grow" @click="promptDelete(picture)"
              >delete</v-btn
            >
          </v-card-actions>
        </v-card>
      </v-col>
      <v-col cols="12">
        <v-pagination
          :length="page.pagination.totalPages"
          :value="page.pagination.pageNumber + 1"
          @input="page.change($event - 1)"
          @next="page.next"
          @previous="page.previous"
        />
      </v-col>
    </v-row>
  </v-container>
</template>

<script lang="ts">
import {
  defineComponent,
  ref,
  useContext,
  watch,
} from '@nuxtjs/composition-api'
import { extractMessage, usePageable } from '~/composables'
import { Picture } from '~/models'
export default defineComponent({
  setup() {
    const { $dialog, $axios } = useContext()

    const page = usePageable<Picture>('/api/pictures', { watchAuth: true })
    const file = ref<File>()

    const promptDelete = async (picture: Picture) => {
      const response = await $dialog.confirm({ title: 'Delete picture?' })
      if (!response) {
        return
      }

      try {
        await $axios.$delete(`/api/pictures/${picture.id}`)
      } catch (error) {
        const message = extractMessage(error)
        $dialog.notify.error(message)
      }

      page.fetch()
    }

    const upload = async () => {
      try {
        const formData = new FormData()
        formData.append('file', file.value!)

        await $axios.$post(`/api/pictures`, formData)
      } catch (error) {
        const message = extractMessage(error)
        $dialog.notify.error(message)
      }

      file.value = undefined
      page.fetch()
    }

    watch(file, (file) => {
      if (file) {
        upload()
      }
    })

    return {
      page,
      file,
      promptDelete,
    }
  },
})
</script>
