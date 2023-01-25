<script lang="ts" setup>
import { onMounted, ref } from 'vue';
import { useMatchaStore } from '@/store';
import axios, { Axios } from 'axios';
import { AxiosError } from 'axios';
import { useRouter } from 'vue-router';
import BaseCard from '@/components/BaseCard.vue';
import { Form, Field, ErrorMessage } from 'vee-validate';

const store = useMatchaStore();

const router = useRouter();

async function onSubmit(values: any) {
  try {
    await store.login(values.username, values.password);
    router.push('/');
  } catch (err: any) {
    console.error(err.message);
  }
}

function validatePassword(value: any) {
  if (!value) {
    return 'Password is required';
  }
  if (value.match(/^\s+$/)) {
    return 'Username cannot be only whitespace';
  }
  return true;
}

function validateUsername(value: any) {
  if (!value) {
    return 'Username is required';
  }
  if (value.length < 3 || value.length > 48) {
    return 'Username must be between 3 and 48 characters';
  }
  return true;
}

onMounted(() => {
  if (store.isLoggedIn) {
    router.push('/');
  }
});
</script>

<template>
  <div class="flex h-screen">
    <div class="m-auto w-1/3 h-fit">
      <header>
        <h1 class="text-2xl text-center pb-12">Sign in to Matcha</h1>
      </header>
      <main>
        <BaseCard>
          <Form @submit="onSubmit">
            <div class="form-field">
              <label for="username">Username</label>
              <Field type="text" name="username" id="username"
                           :rules="validateUsername"/>
              <ErrorMessage name="username" class="text-primary" />
            </div>
            <div class="form-field">
              <label for="password">Password</label>
              <Field type="password" name="password" id="password"
                           :rules="validatePassword"/>
              <ErrorMessage name="password" class="text-primary" />
            </div>
            <button class="btn w-full bg-primary">Sign in</button>
          </Form>
        </BaseCard>
      </main>
    </div>
  </div>
</template>
