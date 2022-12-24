import { useRoute, useRouter } from "@nuxtjs/composition-api";
import { computed } from "vue"

export function useQueryValue<T>(name: string, defaultValue?: T, parse?: (input: string) => T) {
    const route = useRoute()
    const router = useRouter()

    return computed<T>({
        get(): T {
            const value = route.value.query[name] as string || String(defaultValue)

            if (parse) {
                return parse(value)
            }

            return value as T
        },
        set(value: T) {
            router.push({
                query: {
                    ...route.value.query,
                    [name]: String(value)
                }
            })
        }
    })
}