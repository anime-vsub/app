<template>
  <q-card flat dense class="bg-transparent">
    <div class="relative pt-[142.85714285714286%]">
      <q-skeleton
        width="100%"
        height="100%"
        class="absolute top-0 left-0 !rounded-[4px]"
        square
      />
    </div>

    <span class="line-clamp-2 min-h-10">
      <q-skeleton type="text" width="100%" class="py-0 my-0" height="20px" />
      <q-skeleton type="text" width="60%" class="py-0 my-0" height="20px" />
    </span>
  </q-card>
</template>

<style lang="scss" scoped>
span {
  text-decoration: none;
  user-select: none;
  color: rgb(255, 255, 255);

  min-height: 42px;
  position: relative;
  padding: 0.1rem 0px 0px;
  padding: {
    left: 4px;
    right: 4px;
  }
  // font-size: ;
  transition: color 0.3s ease 0s;

  font-weight: 500;

  @media (max-width: 720px) {
    font-weight: 400;
  }
}
</style>
