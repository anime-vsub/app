<template>
  <div class="flex items-center font-weight-medium">
    <svg
      width="16px"
      height="16px"
      viewBox="0 0 28 27"
      version="1.1"
      xmlns="http://www.w3.org/2000/svg"
      class="mr-1"
    >
      <g
        id="V1.12.0_UI_4391_Watch-Page-Add-Rating"
        stroke="none"
        stroke-width="1"
        fill="none"
        fill-rule="evenodd"
      >
        <g
          id="4391-6_1920_info"
          transform="translate(-948.000000, -906.000000)"
          fill="#07f217"
          fill-rule="nonzero"
        >
          <g
            id="Group-10-Copy-10"
            transform="translate(906.000000, 880.000000)"
          >
            <g id="ic/star_green" transform="translate(40.000000, 24.000000)">
              <path
                d="M16.7983826,2.56356746 L19.7968803,11.2875241 L29.1657516,11.3941138 C29.9719564,11.4033379 30.3057022,12.4128653 29.6590696,12.8853446 L22.1424877,18.3829131 L24.9344802,27.1724634 C25.17436,27.9288402 24.3014061,28.55198 23.643301,28.0938493 L16.0005215,22.7674392 L8.35669898,28.0928244 C7.69963687,28.5509551 6.82563997,27.9267904 7.06551979,27.1714385 L9.85751226,18.3818882 L2.34093036,12.8843197 C1.69429781,12.4118404 2.02804364,11.402313 2.83424842,11.3930889 L12.2031197,11.2864992 L15.2016174,2.56254256 C15.4602704,1.81231509 16.5407725,1.81231509 16.7983826,2.56356746 Z"
                id="Star"
              ></path>
            </g>
          </g>
        </g>
      </g>
    </svg>

    <template v-if="label">{{ label }}</template>
  </div>
</template>

<script lang="ts" setup>
defineProps<{
  label?: string | number
}>()
</script>
