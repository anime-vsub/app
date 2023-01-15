<template>
  <router-view v-slot="{ Component }">
    <keep-alive exclude="_season">
      <component :is="Component" />
    </keep-alive>
  </router-view>
</template>
