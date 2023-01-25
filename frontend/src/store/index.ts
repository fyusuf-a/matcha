import { defineStore } from 'pinia';
import { ref } from 'vue';
import type { Ref } from 'vue';
import type { UserDto } from '@/models/user-dto';
import axios from 'axios';
import { useRouter } from 'vue-router';

type State = {
  user: UserDto | undefined;
  refreshToken : string | undefined;
};

let state : State = {
  user: undefined,
  refreshToken: undefined,
};

const router = useRouter();

export const useMatchaStore = defineStore({
  id: 'matcha',
  state: () => {
    return state;
  },
  actions: {
    async login(login: string, password: string) {
      let response = await axios.post('/api/auth/login', {
        login,
        password
      })
      this.refreshToken = response.data.refreshToken;
    },
    async logout() {
      try {
        await axios.post('/api/auth/logout', {
          refreshToken: this.refreshToken
        });
        this.user = undefined;
        this.refreshToken = undefined;
      } catch (e: any) {
        console.error(e.message);
      }
    }
  },
  getters: {
    isLoggedIn(): boolean {
      return this.refreshToken !== undefined;
    }
  },
});
