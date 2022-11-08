import "vue-router"

export {}

declare module "vue-router" {
  interface RouteMeta {
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    styleFn?: (offset: number, height: number) => any
  }
}
