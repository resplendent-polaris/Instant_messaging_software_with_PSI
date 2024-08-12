<template>
  <el-drawer size="400px" :visible.sync="visible" direction="rtl" :before-close="onClose">
    <template #title>
      <div class="custom-title">
        共同好友
      </div>
    </template>
    <div class="friends-in" v-loading="loading" element-loading-text="拼命加载中">
      <!-- 按钮：点击后显示共同好友 -->
      <div>
        <el-button class="custom-button"  @click="onButtonClick" round>显示共同好友</el-button>
      </div>
        <!-- 显示共同好友列表 -->
      <div v-if="mutualFriends.length">
        <el-scrollbar class="showfriend">
          <ul>
            <li v-for="friend in mutualFriends" :key="friend.username">
              <headImage :url="friend.headImage" :name="friend.username" :size="50"  />
              <span>{{ friend.username }}</span>
            </li>
          </ul>
        </el-scrollbar>
      </div>
    </div>
  </el-drawer>
</template>

<script>
import ChatMessageItem from './ChatMessageItem.vue';
import axios from 'axios';
import HeadImage from "@/components/common/HeadImage.vue";

export default {
  name: 'friendsIntersection',
  components: {
    ChatMessageItem,
    HeadImage
  },
  props: {
    visible: {
      type: Boolean
    },
    chat: {
      type: Object
    },
    friend: {
      type: Object
    }
  },
  data() {
    return {
      page: 1,
      size: 10,
      userId1: null,  // 第一个用户的 ID
      userId2: null,  // 第二个用户的 ID
      loadAll: false,
      loading: false,
      //lastScrollTime: new Date(),
      mutualFriends: [] // 用于存储共同好友列表
    }
  },
  mounted() {
    // 在组件挂载后，设置 userId1 和 userId2
    this.userId1 = this.mine.id; // 本人用户的 ID
    this.userId2 = this.chat.targetId; // 对方用户的 ID

  },
  methods: {
    onClose() {
      this.page = 1;
      this.loadAll = false;
      this.$emit('close');
    },
    async onButtonClick() {
      // 这里是按钮点击后的操作

      this.mutualFriends = [
        {
          username: 'Alice',
          headImage: this.mine.headImageThumb
        },
        {
          username: 'Bob',
          headImage: this.chat.headImageThumb
        },]
      console.log("id1:",this.userId1)
      console.log("id2:",this.userId2)
    },
    // 获取共同好友的方法
    async getMutualFriends() {
      try {
        // 通过 Axios 发送请求到后端 API
        const response = await this.$http.post('../../api/getmutualfriends', {
          userId1: this.userId1,
          userId2: this.userId2
        });

        // 将后端返回的数据赋值给 mutualFriends 数组
        this.mutualFriends = response.data.mutualFriends;
      } catch (error) {
        console.error('获取共同好友失败:', error);
      }
    },
    showName(msgInfo) {
      return msgInfo.sendId == this.mine.id ? this.mine.nickName : this.friend.nickName;
    },
    headImage(msgInfo) {
      return msgInfo.sendId == this.mine.id ? this.mine.headImageThumb : this.friend.headImage;
    },
  },
  computed: {
    mine() {
      return this.$store.state.userStore.userInfo;
    },
    histroyAction() {
      return `/message/friend/history`;
    }
  },

}
</script>


<style scoped lang="scss">
.custom-title {
  font-size: 20px; /* 更改字体大小 */
  font-weight: bold; /* 更改字体粗细 */
  color: #8f939c; /* 更改字体颜色 */
  text-align: center; /* 文本居中 */
  padding: 10px 0; /* 增加上下内边距 */
}
.custom-button {
  background-color: #2C405A; /* 设置背景色 */
  color: white;              /* 设置文字颜色 */
  width: 150px;          /* 设置按钮宽度 */
  height: 40px;
  border-radius: 5px;       /* 设置圆角 */
  padding: 10px 20px;       /* 设置内边距 */
  font-size: 16px;          /* 设置字体大小 */
}

.friends-in{
  .showfriend{
    display: flex;
    flex-direction: column;
    border: #dddddd solid 1px;
    flex: 1;
    margin: 20px 0;
    background: #F8F8F8;
    ul{
      list-style-type: none;
      padding: 0;
      margin: 0;
    }
    li{
      display: flex;
      align-items: center; /* 垂直居中对齐 */
      padding: 5px 0;
      magin-left: 75px;
      span{
        font-size: 16px;
        font-weight: 700;
        line-height: 40px;
        margin-left: 25px
      }
      img {
        width: 75px;
        height: 75px;
        margin: 0 25px;
      }
    }
  }
}


</style>