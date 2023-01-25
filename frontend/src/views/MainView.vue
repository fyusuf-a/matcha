<script setup lang="ts">
import { useMatchaStore } from '@/store';
import { useRouter } from 'vue-router';

let store = useMatchaStore();
let router = useRouter();

async function logout() {
  await store.logout();
  router.push({ name: 'Login' });
}
</script>

<template>
  <h1 class="text-3xl font-bold underline">
    Hello world!
  </h1>
  <button class="btn bg-primary" @click="logout">
    Log out
  </button>
</template>
