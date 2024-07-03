<template>
    <t-space style="display: flex; width: 100%;">
        <div>
            <div class="title">活动细节</div>
            <div class="line"></div>
        </div>
    </t-space>
    <div style="margin-left: 25px;margin-right: 25px; margin-bottom: 40px;">
        {{ event.description }}
    </div>
    <div v-if="event.date">
        <t-space style="display: flex; width: 100%;">
            <div>
                <div class="title">活动时间</div>
                <div class="line"></div>
            </div>
        </t-space>
    </div>
    <div style="margin-left: 25px;margin-right: 25px; margin-bottom: 40px;">
        {{ event.date }}
    </div>
    <t-space style="display: flex; width: 100%;">
        <div>
            <div class="title">活动票价</div>
            <div class="line"></div>
        </div>
    </t-space>
    <div style="margin-left: 25px; margin-top: -10px;">
        <p style="  margin-top: 0.4rem; line-height: 1.625; color: rgb(70, 73, 79);;" v-show="event.highestPrice===0">
            不需要购买
        </p>
      <p style="  margin-top: 0.4rem; line-height: 1.625; color: rgb(70, 73, 79);;" v-show="event.highestPrice>0&& event.highestPrice===event.lowestPrice">
        门票价格：¥{{ event.lowestPrice }}
      </p>
      <p style="  margin-top: 0.4rem; line-height: 1.625; color: rgb(70, 73, 79);;" v-show="event.highestPrice>0 && event.highestPrice!==event.lowestPrice">
        门票价格：¥{{ event.lowestPrice }} - ¥{{ event.highestPrice }}
      </p>
    </div>
    <div style="height: 20px;"></div>
    <div style="height: 20px;"></div>
    <div v-if="event.policy">
        <t-space style="display: flex; width: 100%;">
            <div>
                <div class="title">退换票规则</div>
                <div class="line"></div>
            </div>
        </t-space>
        <div class="ticket_card" style="margin-left: 25px; margin-top: -10px;">
            <p style="  margin-top: 0.4rem; line-height: 1.625;color: rgb(70, 73, 79);;">
                {{ event.policy }}
            </p>
        </div>

    </div>
    <div style="height: 40px;"></div>

</template>
<script setup>
defineProps({
    event: Object,
})  
</script>

<style scoped>
.title {
    color: #5e6066;
    font-size: 1em;
    font-weight: 700;
    margin: 20px;
    letter-spacing: 2px;
}

.line {
    width: 70px;
    height: 4px;
    margin-top: -15px;
    margin-bottom: 20px;
    margin-left: 20px;
    background-color: black;
}
</style>