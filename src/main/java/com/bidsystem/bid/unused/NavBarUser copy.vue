<template>
  <nav class="navbar navbar-expand-sm navbar-light bg-light">
    <div class="container-fluid">
      <a class="navbar-brand" href="#" src="/billiards.JPG"></a>
      <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
      </button>
      <!-- <div class="collapse navbar-collapse" id="navbarSupportedContent"> -->
        <div >
        <ul class="navbar-nav me-auto mb-2 mb-lg-0">
          <li class="nav-item">
            <a class="nav-link" @click="handleClick('selectvenue')" :class="{ 'disabled': !isLoggedIn }">홈</a>
          </li>
          <!-- <li class="nav-item">
            <a class="nav-link" @click="handleClick('bidseats')" :class="{ 'disabled': !isLoggedIn || !hasSelectedMatch }">입찰등록</a>
          </li> -->
          <!-- <li class="nav-item">
            <a class="nav-link" @click="handleClick('paybids')" :class="{ 'disabled': !isLoggedIn || !hasSelectedMatch }">낙찰현황 및 결제</a>
          </li> -->
          <li class="nav-item">
            <a class="nav-link" @click="handleClick('userlogin')">로그인</a>
          </li>
          <li class="nav-item">
            <a class="nav-link" @click="handleClick('updateuser')" :class="{ 'disabled': !isLoggedIn }">마이페이지</a>
          </li>

          <li class="nav-item">
            <a class="nav-link" @click="handleClick('userlogout')" :class="{ 'disabled': !isLoggedIn }">로그아웃</a>
          </li>
        </ul>
      </div>
    </div>
  </nav>
</template>

<script>
import { ref, watch, onMounted } from 'vue';

export default {
  name: 'NavBarUser',
  props: {
    isLoggedIn: {
      type: Boolean,
      default: false,
    },
    hasSelectedMatch: {
      type: Boolean,
      default: false,
    },
  },
  setup(props, { emit }) {
    const isLoggedIn = ref(props.isLoggedIn);
    const hasSelectedMatch = ref(props.hasSelectedMatch);

    const handleClick = (action) => {
      emit('link-action', action);
    };

    const updateStatus = () => {
      isLoggedIn.value =  props.isLoggedin;
      hasSelectedMatch.value = props.hasSelectedMatch ;
    };

    watch([() => props.isLoggedIn, () => props.hasSelectedMatch], () => {
      updateStatus();
    });

    onMounted(() => {
      updateStatus();
    });

    return {
      handleClick
    };
  }
};
</script>

<style scoped>
/* 추가 스타일을 원하면 여기에 작성 */
</style>
