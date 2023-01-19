<script lang="ts" setup>
import { ref } from 'vue';
import { useMatchaStore } from '@/store';

const store = useMatchaStore();

const id = ref(store.id);

id.value = 5;

</script>

<template>
  <div>
    Login {{ id }}
  </div>
</template>
