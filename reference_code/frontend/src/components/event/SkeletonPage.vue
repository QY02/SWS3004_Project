<script setup>

</script>

<template>
    <div class="card">
        <div class="card-1">
        </div>
        <div class="right">
            <div class="card-2">
                <el-skeleton :rows="5" />
            </div>
            <div class="card-3">
                <el-skeleton :rows="0" />
            </div>
        </div>
    </div>
    <el-affix :offset="55">
      <t-tabs :value="value_lable" size="large" @change="onTabChange" :affix-props="{ offsetTop: 150 }">
        <t-tab-panel value="events" label="    " ></t-tab-panel>
        <t-tab-panel value="about" label="    "></t-tab-panel>
        <t-tab-panel value="price" label="    "></t-tab-panel>
        <t-tab-panel value="policy" label="    "></t-tab-panel>
        <t-tab-panel value="gallery" label="    "></t-tab-panel>
        <t-tab-panel value="reviews" label="    "></t-tab-panel>
      </t-tabs>
    </el-affix>
    <el-card style="margin:30px;">
        <el-skeleton :rows="5" />
    </el-card>
</template>

<style scoped>
.t-skeleton-demo-paragraph {
    line-height: 25px;
}

.card {
  --gray: rgba(229, 231, 235, 1);
  display: flex;
  grid-gap: 1.25rem;
  gap: 1.25rem;
  background-color: rgba(255, 255, 255, 1);
  padding: 1.5rem;
  height: 200px;
}

.card-1 {
  width: 40%;
  margin-right: 15px;
  border-radius: 0.75rem;
  background-color: var(--gray);
}

.right {
  display: flex;
  flex: 1 1 0%;
  flex-direction: column;
  grid-gap: 1.25rem;
  gap: 1.25rem;
}
.card-2 {
  height: 3.5rem;
  width: 100%;
  border-radius: 1rem;
}
</style>