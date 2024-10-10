<template>
  <div class="common-container">
    <br/>
    <BidStatus :bidStatus="bidStatus" />
    <div class="content-container">
      <div class="seat-list">
        <br/>
        <h5>입찰 내역 {{ message }}</h5>
        <div v-if="totalSeatsSelected === 0" class="empty-seats">입찰 좌석이 없습니다.</div>
        <div v-else class="seat-input-grid">
          <table>
            <thead>
              <tr>
                <th>좌석 번호</th>
                <th>위치</th>
                <th>입찰 가격</th>
                <th>현재 최고가</th>
                <th>낙찰결과</th>
                <th>결제 상태</th>
                <th>결제 방법</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="seat in seatBids" :key="seat.uniqueSeatId">
                <td>{{ seat.seat_no }}</td>
                <td>{{ seat.row_no }} 열 {{ seat.col_no }}번</td>
                <td>{{ seat.bid_amount.toLocaleString() }}원</td>
                <td>{{ (seat.highest_bid_amount || 0).toLocaleString() }}원</td>
                <td>{{ seat.bidWonStatus }}</td>
                <td>{{ seat.paidStatus }}</td>
                <td>{{ seat.bid_pay_method }}</td>
              </tr>
            </tbody>
          </table>
        </div>
        <div>
          <br/>
          <h5>입찰가격 합계: {{ totalBidAmount.toLocaleString() }}원</h5>
        </div>
        <div class="buttons-container">
          <button
            @click="handlePaySubmit"
            :disabled="isApproved"
          >
            결제 진행
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue';
import axios from 'axios';
import { useRouter } from 'vue-router';
import BidStatus from '../BidStatus.vue';
import { url, API, messageCommon, statusMsgGeneral } from '../../utils/messagesAPIs.js';

export default {
  components: { BidStatus },
  setup() {
    const router = useRouter();
    const sessionUserId = ref('');
    const matchNumber = ref(0);
    const bidStatus = ref({});
    const seatBids = ref([]);
    const paymentData = ref({});
    const totalBidAmount = ref(0);
    const isApproved = ref(false);
    const winCount = ref(0);
    const message = ref('');
    const totalSeatsSelected = computed(() => seatBids.value.length);

    const fetchSessionUserId = async () => {
      try {
        const response = await axios.get(API.GET_SESSION_USERID, { withCredentials: true });
        if (response.status === 200) {
          sessionUserId.value = response.data.userId;

        } else {
          handleError({ response: { status: response.status } });
        }
      } catch (error) {
        alert('로그인이 필요합니다.');
        // router.push(url.userlogin);
      }
    };

    const fetchBidStatus = async (sessionMatchNumber) => {
      try {
        const response = await axios.get(API.GET_BIDSTATUS, {
          params: { matchNumber: sessionMatchNumber },
          withCredentials: true
        });
        if (response.status === 200) {
          bidStatus.value = response.data;
          if (response.data.bidStatusCode !== 'O') {
            // alert('현재는 입찰할 수 없습니다.');
          }
        } else {
          handleError({ response: { status: response.status } });
        }
      } catch (error) {
        handleError(error);
      }
    };

    const fetchMyBids = async () => {
      try {
        const response = await axios.get(API.GET_MYBIDS, {
          params: { userId: sessionUserId.value, matchNumber: matchNumber.value }
        });
        if (response.status === 200) {
          const fetchedData = response.data || [];
          seatBids.value = fetchedData.map(seat => ({
            ...seat,
            bidWonStatus: seat.bid_won === 'Y' ? '낙찰' : '',
            paidStatus: seat.bid_paid === 'Y' ? '결제완료' : ''
          }));

          winCount.value = seatBids.value.length;

          totalBidAmount.value = seatBids.value.reduce((sum, seat) => sum + (seat.highest_bid_amount || 0), 0);
        } else {
          handleError({ response: { status: response.status } });
        }
      } catch (error) {
        handleError(error);
      }
    };

    const handlePaySubmit = () => {
      localStorage.setItem('userId', sessionUserId.value);
      paymentData.value = {
        ...paymentData.value,
        userId: sessionUserId.value,
        price: totalBidAmount.value,
        goodname: `좌석입찰 총 ${winCount.value} 건`
      };

      const objectToQueryString = (obj) => {
        return Object.keys(obj)
          .map(key => `${encodeURIComponent(key)}=${encodeURIComponent(obj[key])}`)
          .join('&');
      };

      const queryString = objectToQueryString(paymentData.value);
      const redirectUrl = `${API.PG_START}?${queryString}`;

      window.location.href = redirectUrl;
    };

    // const restoreSession = async () => {
    //   const storedUserId = localStorage.getItem('userId');
    //   if (storedUserId) {
    //     try {
    //       const response = await axios.post(API.RESTORE_SESSION, { userId: storedUserId });
    //       if (response.status !== 200) {
    //         localStorage.removeItem('userId');
    //         router.push(url.login);
    //       }
    //     } catch (error) {
    //       console.error('세션 복원 오류:', error);
    //       router.push(url.login);
    //     }
    //   } else {
    //     router.push(url.login);
    //   }
    // };

    const handleError = (error) => {
      if (error.response) {
        const msg = statusMsgGeneral[error.response.status] || messageCommon.ERR_SERVER_DEFAULT;
        message.value = `오류: ${msg}`;
      } else if (error.request) {
        message.value = messageCommon.ERR_NETWORK;
      } else {
        message.value = messageCommon.ERR_ETC;
      }
    };

    onMounted(async() => {
      const sessionMatchNumber = sessionStorage.getItem('matchNumber');
      if (sessionMatchNumber) {
        matchNumber.value = sessionMatchNumber;
      } else {
        alert('경기를 먼저 선택해주세요.');
        router.push(url.selectmatchuser);
        return;
      }

      const queryParams = new URLSearchParams(window.location.search);
      const action = queryParams.get('action');
      if (action === 'approve') {
        alert('approved and returned');
        const userId = localStorage.getItem('userId');
        console.log(`로컬 저장된 userId: ${userId}`);
        isApproved.value = true;
      }
      try {
          await fetchSessionUserId();
          await fetchBidStatus(sessionMatchNumber);
          await fetchMyBids(sessionUserId.value, sessionMatchNumber);
      } catch (error) {
        console.error('비동기 호출 오류:', error);
      }
    });

    return {
      sessionUserId,
      matchNumber,
      bidStatus,
      seatBids,
      paymentData,
      totalBidAmount,
      isApproved,
      winCount,
      message,
      totalSeatsSelected,
      handlePaySubmit
    };
  }
};
</script>

<style scoped>
/* 여기에 CSS를 추가하세요 */
</style>
