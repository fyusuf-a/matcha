<script lang="ts" setup>
import { onBeforeMount, ref } from 'vue';
import { useMatchaStore } from '@/store';
import axios from 'axios';
import { useRouter } from 'vue-router';

const store = useMatchaStore();

const userName = ref('');
const password = ref('');
const router = useRouter();

async function login() {
  let res = await axios.post('/api/auth/login', {
    login: userName.value,
    password: password.value,
  })
  store.id = res.data.id;
  router.push('/');
}
</script>

<template>
  <div class="flex h-screen">
    <div class="m-auto w-1/3 h-fit">
      <header>
        <h1 class="text-2xl text-center pb-12">Sign in to Matcha</h1>
      </header>
      <main>
        <form class="mx-auto p-4 bg-light font-darker">
          <div class="form-field">
            <label for="username">Username</label>
            <input type="text" id="username" v-model.trim="userName" />
          </div>
          <div class="form-field">
            <label for="password">Password</label>
            <input type="password" id="password" v-model="password" />
          </div>
          <button type="submit" class="btn w-full bg-primary"
                        @click.prevent="login" >Sign in</button>
        </form>
      </main>
    </div>
  </div>
</template>
