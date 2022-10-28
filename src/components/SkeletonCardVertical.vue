<template>
  <div class="relative flex flex-nowrap">
    <q-responsive :ratio="280 / 400" class="rounded-lg w-[110px]">
      <q-skeleton type="rect" class="absolute w-full h-full" />
    </q-responsive>

    <div class="flex-1 h-full overflow-hidden pl-3 py-[3px]">
      <q-skeleton type="text" width="40%" class="text-[16px] leading-snug" />

      <div class="mt-2 flex">
        <q-skeleton type="text" width="20px" class="mr-2" />
        <q-skeleton type="text" width="40px" />
      </div>

      <p class="mt-2">
        <q-skeleton type="text" height="15px" width="60%" />
        <q-skeleton type="text" height="15px" width="40%" />
      </p>
    </div>
  </div>
</template>
