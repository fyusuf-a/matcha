import { Plugin } from '@nuxt/types'
import { useAuthStore } from '~/store';

export default ((context, inject) => {
    const { $axios } = context
    const authStore = useAuthStore()

    $axios.interceptors.request.use((config) => {
        if (authStore.logged) {
            config.headers["Authorization"] = `Bearer ${authStore.tokens?.accessToken}`
        }

        return config;
    }, (error) => {
        return Promise.reject(error);
    });
}) as Plugin
