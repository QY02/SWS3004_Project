<template>
  <VaCarousel
      v-model="value"
      :items="items"
      style="margin-bottom: 16px"
      :ratio="21 / 6"
      indicators
      indicator-trigger="hover"
      autoscroll
      :autoscroll-interval="4000"
      swipable
  />
  <h4 class="va-h4 gap-9" style="text-align: center;margin-top: 20px">Welcome Admin! </h4>
</template>

<script>
import {defineComponent} from "vue";
import {useColors} from "vuestic-ui";
export default defineComponent({
  name: 'HomePage',
  setup() {
    const {colors} = useColors();
    colors.primary = sessionStorage.getItem('primary-color')
  },
  data() {
    return {
      value: 0,
      items: [
        "https://liveimages.videocc.net/uploaded/images/2022/07/gbrdf8rnqv.jpg",
        "https://ts1.cn.mm.bing.net/th/id/R-C.9c00f8dbf628037bcece575e09aaac02?rik=QUBUlw%2bFA6eWXw&riu=http%3a%2f%2fwww.help315.com.cn%2fuploads%2fallimg%2f190820%2f27-1ZR0161947.jpg&ehk=REUyyVBXI9BdZHsz0hlUcXz4SJ1nsMzAGIHtE42Yvr4%3d&risl=&pid=ImgRaw&r=0&sres=1&sresct=1",
        "https://picsum.photos/1502",
        "https://picsum.photos/1503",
        "https://picsum.photos/1504",
      ],
    };
  },
})
</script>