<template>
  <div class="modal-overlay" v-if="visible">
    <div class="modal-content">
      <h4>{{ title }}</h4>
      <p>{{ message }}</p>
      <button @click="confirm" class="confirm-button">확인</button>
    </div>
  </div>
</template>

<script>
import { defineComponent } from "vue";

export default defineComponent({
  props: {
    visible: {
      type: Boolean,
      required: true,
    },
    title: {
      type: String,
      default: "모달 제목",
    },
    message: {
      type: String,
      default: "모달 내용",
    },
  },
  emits: ["confirm"],
  setup(props, { emit }) {
    const confirm = () => {
      emit("confirm");
    };

    return {
      ...props,  // props 자체를 반환해 중복 제거
      confirm,
    };
  },
});
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
}

.modal-content {
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}
</style>
