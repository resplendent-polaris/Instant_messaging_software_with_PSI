<template>
	<div class="chat-box" @click="closeRefBox()" @mousemove="readedMessage()">
		<el-container>
			<el-header height="56px">
				<span>{{ title }}</span>
				<span title="群聊信息" v-show="this.chat.type == 'GROUP'" class="btn-side el-icon-more"
					@click="showSide = !showSide"></span>
			</el-header>
			<el-main style="padding: 0;">
				<el-container>
					<el-container class="content-box">
						<el-main class="im-chat-main" id="chatScrollBox" @scroll="onScroll">
							<div class="im-chat-box">
								<ul>
									<li v-for="(msgInfo, idx) in chat.messages" :key="idx">
										<chat-message-item v-if="idx >= showMinIdx" @call="onCall(msgInfo.type)"
											:mine="msgInfo.sendId == mine.id" :headImage="headImage(msgInfo)"
											:showName="showName(msgInfo)" :msgInfo="msgInfo"
											:groupMembers="groupMembers" @delete="deleteMessage"
											@recall="recallMessage">
										</chat-message-item>
									</li>
								</ul>
							</div>
						</el-main>
						<el-footer height="240px" class="im-chat-footer">
							<div class="chat-tool-bar">
								<div title="表情" class="icon iconfont icon-emoji" ref="emotion"
									@click.stop="showEmotionBox()">
								</div>
								<div title="发送图片">
									<file-upload :action="'/image/upload'" :maxSize="5 * 1024 * 1024"
										:fileTypes="['image/jpeg', 'image/png', 'image/jpg', 'image/webp', 'image/gif']"
										@before="onImageBefore" @success="onImageSuccess" @fail="onImageFail">
										<i class="el-icon-picture-outline"></i>
									</file-upload>
								</div>
								<div title="发送文件">
									<file-upload :action="'/file/upload'" :maxSize="10 * 1024 * 1024"
										@before="onFileBefore" @success="onFileSuccess" @fail="onFileFail">
										<i class="el-icon-wallet"></i>
									</file-upload>
								</div>
								<div title="回执消息" v-show="chat.type == 'GROUP'" class="icon iconfont icon-receipt"
									:class="isReceipt ? 'chat-tool-active' : ''" @click="onSwitchReceipt">
								</div>
								<div title="发送语音" class="el-icon-microphone" @click="showRecordBox()">
								</div>
								<div title="语音通话" v-show="chat.type == 'PRIVATE'" class="el-icon-phone-outline"
									@click="showPrivateVideo('voice')">
								</div>
								<div title="语音通话" v-show="chat.type == 'GROUP'" class="el-icon-phone-outline"
									@click="onGroupVideo()">
								</div>
								<div title="视频通话" v-show="chat.type == 'PRIVATE'" class="el-icon-video-camera"
									@click="showPrivateVideo('video')">
								</div>
								<div title="聊天记录" class="el-icon-chat-dot-round" @click="showHistoryBox()"></div>
                <div title="共同好友" v-show="chat.type == 'PRIVATE'" class="iconfont icon-jiaoji" @click="showIntersectionBox()"></div>
							</div>
							<div class="send-content-area">
								<div contenteditable="true" v-show="!sendImageUrl" ref="editBox" class="send-text-area"
									:disabled="lockMessage" @paste.prevent="onEditorPaste"
									@compositionstart="onEditorCompositionStart"
									@compositionend="onEditorCompositionEnd" @input="onEditorInput"
									:placeholder="placeholder" @blur="onEditBoxBlur()" @keydown.down="onKeyDown"
									@keydown.up="onKeyUp" @keydown.enter.prevent="onKeyEnter">x
								</div>

								<div v-show="sendImageUrl" class="send-image-area">
									<div class="send-image-box">
										<img class="send-image" :src="sendImageUrl" />
										<span class="send-image-close el-icon-close" title="删除"
											@click="removeSendImage()"></span>
									</div>
								</div>
								<div class="send-btn-area">
									<el-button type="primary" size="small" @click="sendMessage()">发送</el-button>
								</div>
							</div>
						</el-footer>
					</el-container>
					<el-aside class="chat-group-side-box" width="300px" v-if="showSide">
						<chat-group-side :group="group" :groupMembers="groupMembers" @reload="loadGroup(group.id)">
						</chat-group-side>
					</el-aside>
				</el-container>
			</el-main>
			<emotion ref="emoBox" @emotion="onEmotion"></Emotion>
			<chat-at-box ref="atBox" :ownerId="group.ownerId" :members="groupMembers" :search-text="atSearchText"
				@select="onAtSelect"></chat-at-box>
			<chat-record :visible="showRecord" @close="closeRecordBox" @send="onSendRecord"></chat-record>
			<group-member-selector ref="rtcSel" :groupId="group.id" @complete="onInviteOk"></group-member-selector>
			<rtc-group-join ref="rtcJoin" :groupId="group.id"></rtc-group-join>
			<chat-history :visible="showHistory" :chat="chat" :friend="friend" :group="group"
				:groupMembers="groupMembers" @close="closeHistoryBox"></chat-history>
      <friends-intersection :visible="showIntersection" :chat="chat" :friend="friend"
                           @close="closeIntersectionBox"></friends-intersection>
		</el-container>
	</div>
</template>

<script>
	import ChatGroupSide from "./ChatGroupSide.vue";
	import ChatMessageItem from "./ChatMessageItem.vue";
	import FileUpload from "../common/FileUpload.vue";
	import Emotion from "../common/Emotion.vue";
	import ChatRecord from "./ChatRecord.vue";
	import ChatHistory from "./ChatHistory.vue";
  import FriendsIntersection from "./FriendsIntersection.vue";
	import ChatAtBox from "./ChatAtBox.vue"
	import GroupMemberSelector from "../group/GroupMemberSelector.vue"
	import RtcGroupJoin from "../rtc/RtcGroupJoin.vue"
	

	export default {
		name: "chatPrivate",
		components: {
			ChatMessageItem,
			FileUpload,
			ChatGroupSide,
			Emotion,
			ChatRecord,
			ChatHistory,
      FriendsIntersection,
			ChatAtBox,
			GroupMemberSelector,
			RtcGroupJoin
		},
		props: {
			chat: {
				type: Object
			}
		},
		data() {
			return {
				friend: {},
				group: {},
				groupMembers: [],
				sendImageUrl: "",
				sendImageFile: "",
				placeholder: "",
				isReceipt: true,
				showRecord: false, // 是否显示语音录制弹窗
				showSide: false, // 是否显示群聊信息栏
				showHistory: false, // 是否显示历史聊天记录
        showIntersection: false,
				lockMessage: false, // 是否锁定发送，
				showMinIdx: 0, // 下标低于showMinIdx的消息不显示，否则页面会很卡
				atSearchText: "",
				focusNode: null, // 缓存光标所在节点
				focusOffset: null, // 缓存光标所在节点位置
				zhLock: false // 解决中文输入法触发英文的情况
			}
		},
		methods: {
			moveChatToTop() {
				let chatIdx = this.$store.getters.findChatIdx(this.chat);
				this.$store.commit("moveTop", chatIdx);
			},
			closeRefBox() {
				this.$refs.emoBox.close();
				this.$refs.atBox.close();
			},
			onCall(type) {
				if (type == this.$enums.MESSAGE_TYPE.ACT_RT_VOICE) {
					this.showPrivateVideo('voice');
				} else if (type == this.$enums.MESSAGE_TYPE.ACT_RT_VIDEO) {
					this.showPrivateVideo('video');
				}
			},
			onKeyDown() {
				if (this.$refs.atBox.show) {
					this.$refs.atBox.moveDown()
				}
			},
			onKeyUp() {
				if (this.$refs.atBox.show) {
					this.$refs.atBox.moveUp()
				}
			},
			onKeyEnter() {
				if (this.$refs.atBox.show) {
					// 键盘操作不会自动修正焦点，需要手动修正,原因不详
					this.focusOffset += this.atSearchText.length;
					this.$refs.atBox.select();
				} else {
					this.sendMessage();
				}
			},
			onEditBoxBlur() {
				let selection = window.getSelection()
				// 记录光标位置(点击emoji时)
				this.focusNode = selection.focusNode;
				this.focusOffset = selection.focusOffset;
			},
			onEditorInput(e) {
				// 如果触发 @
				if (this.chat.type == "GROUP" && !this.zhLock) {
					if (e.data == '@') {
						// 打开选择弹窗
						this.showAtBox(e);
					} else {
						let selection = window.getSelection()
						let range = selection.getRangeAt(0)
						this.focusNode = selection.focusNode;
						// 截取@后面的名称作为过滤条件
						let stIdx = this.focusNode.textContent.lastIndexOf('@');
						this.atSearchText = this.focusNode.textContent.substring(stIdx + 1);
					}
				}
				this.refreshPlaceHolder();

			},
			onEditorCompositionStart() {
				this.zhLock = true;
			},
			onEditorCompositionEnd(e) {
				this.zhLock = false;
				this.onEditorInput(e);
			},
			showAtBox(e) {
				this.atSearchText = "";
				let selection = window.getSelection()
				let range = selection.getRangeAt(0)
				// 记录光标所在位置
				this.focusNode = selection.focusNode;
				this.focusOffset = selection.focusOffset;
				// 光标所在坐标
				let pos = range.getBoundingClientRect();
				this.$refs.atBox.open({
					x: pos.x,
					y: pos.y
				})
			},
			onAtSelect(member) {
				let range = window.getSelection().getRangeAt(0)
				// 选中输入的 @xx 符
				range.setStart(this.focusNode, this.focusOffset - 1 - this.atSearchText.length)
				range.setEnd(this.focusNode, this.focusOffset)
				range.deleteContents()
				// 创建元素节点
				let element = document.createElement('SPAN')
				element.className = "at"
				element.dataset.id = member.userId;
				element.contentEditable = 'false'
				element.innerText = `@${member.aliasName}`
				range.insertNode(element)
				// 光标移动到末尾
				range.collapse()
				// 插入空格
				let textNode = document.createTextNode('\u00A0');
				range.insertNode(textNode)
				range.collapse()
				this.atSearchText = "";
				this.$refs.editBox.focus()
			},
			onSwitchReceipt() {
				this.isReceipt = !this.isReceipt;
				this.refreshPlaceHolder();
			},
			createSendText() {
				let sendText = this.isReceipt ? "【回执消息】" : "";
				this.$refs.editBox.childNodes.forEach((node) => {
					if (node.nodeName == "#text") {
						sendText += this.html2Escape(node.textContent);
					} else if (node.nodeName == "SPAN") {
						sendText += node.innerHTML;
					} else if (node.nodeName == "IMG") {
						sendText += node.dataset.code;
					}
				})
				return sendText;
			},
			html2Escape(strHtml) {
				return strHtml.replace(/[<>&"]/g, function(c) {
					return {
						'<': '&lt;',
						'>': '&gt;',
						'&': '&amp;',
						'"': '&quot;'
					} [c];
				});
			},
			createAtUserIds() {
				let ids = [];
				this.$refs.editBox.childNodes.forEach((node) => {
					if (node.nodeName == "SPAN") {
						ids.push(node.dataset.id);
					}
				})
				return ids;
			},
			onEditorPaste(e) {
				let txt = e.clipboardData.getData('Text')
				if (typeof(txt) == 'string') {
					let range = window.getSelection().getRangeAt(0)
					let textNode = document.createTextNode(txt);
					range.insertNode(textNode)
					range.collapse();
				}
				let items = (e.clipboardData || window.clipboardData).items
				if (items.length) {
					for (let i = 0; i < items.length; i++) {
						if (items[i].type.indexOf('image') !== -1) {
							let file = items[i].getAsFile();
							this.sendImageFile = file;
							this.sendImageUrl = URL.createObjectURL(file);
						}
					}
				}
			},
			removeSendImage() {
				this.sendImageUrl = "";
				this.sendImageFile = null;
			},
			onImageSuccess(data, file) {
				let msgInfo = JSON.parse(JSON.stringify(file.msgInfo));
				msgInfo.content = JSON.stringify(data);
				msgInfo.receipt = this.isReceipt;
				this.$http({
					url: this.messageAction,
					method: 'post',
					data: msgInfo
				}).then((id) => {
					msgInfo.loadStatus = 'ok';
					msgInfo.id = id;
					this.isReceipt = false;
					this.$store.commit("insertMessage", msgInfo);
				})
			},
			onImageFail(e, file) {
				let msgInfo = JSON.parse(JSON.stringify(file.msgInfo));
				msgInfo.loadStatus = 'fail';
				this.$store.commit("insertMessage", msgInfo);
			},
			onImageBefore(file) {
				let url = URL.createObjectURL(file);
				let data = {
					originUrl: url,
					thumbUrl: url
				}
				let msgInfo = {
					id: 0,
					tmpId: this.generateId(),
					fileId: file.uid,
					sendId: this.mine.id,
					content: JSON.stringify(data),
					sendTime: new Date().getTime(),
					selfSend: true,
					type: 1,
					readedCount: 0,
					loadStatus: "loading",
					status: this.$enums.MESSAGE_STATUS.UNSEND
				}
				// 填充对方id
				this.fillTargetId(msgInfo, this.chat.targetId);
				// 插入消息
				this.$store.commit("insertMessage", msgInfo);
				// 会话置顶
				this.moveChatToTop();
				// 滚动到底部
				this.scrollToBottom();
				// 借助file对象保存
				file.msgInfo = msgInfo;
			},
			onFileSuccess(url, file) {
				let data = {
					name: file.name,
					size: file.size,
					url: url
				}
				let msgInfo = JSON.parse(JSON.stringify(file.msgInfo));
				msgInfo.content = JSON.stringify(data);
				msgInfo.receipt = this.isReceipt
				this.$http({
					url: this.messageAction,
					method: 'post',
					data: msgInfo
				}).then((id) => {
					msgInfo.loadStatus = 'ok';
					msgInfo.id = id;
					this.isReceipt = false;
					this.$store.commit("insertMessage", msgInfo);
				})
			},
			onFileFail(e, file) {
				let msgInfo = JSON.parse(JSON.stringify(file.msgInfo));
				msgInfo.loadStatus = 'fail';
				this.$store.commit("insertMessage", msgInfo);
			},
			onFileBefore(file) {
				let url = URL.createObjectURL(file);
				let data = {
					name: file.name,
					size: file.size,
					url: url
				}
				let msgInfo = {
					id: 0,
					tmpId: this.generateId(),
					sendId: this.mine.id,
					content: JSON.stringify(data),
					sendTime: new Date().getTime(),
					selfSend: true,
					type: 2,
					loadStatus: "loading",
					readedCount: 0,
					status: this.$enums.MESSAGE_STATUS.UNSEND
				}
				// 填充对方id
				this.fillTargetId(msgInfo, this.chat.targetId);
				// 插入消息
				this.$store.commit("insertMessage", msgInfo);
				// 会话置顶
				this.moveChatToTop();
				// 滚动到底部
				this.scrollToBottom();
				// 借助file对象透传
				file.msgInfo = msgInfo;
			},
			onCloseSide() {
				this.showSide = false;
			},
			onScrollToTop() {
				// 多展示10条信息
				this.showMinIdx = this.showMinIdx > 10 ? this.showMinIdx - 10 : 0;
			},
			onScroll(e) {
				let scrollElement = e.target
				let scrollTop = scrollElement.scrollTop
				if (scrollTop < 30) { // 在顶部,不滚动的情况
					// 多展示20条信息
					this.showMinIdx = this.showMinIdx > 20 ? this.showMinIdx - 20 : 0;
				}
			},
			showEmotionBox() {
				let width = this.$refs.emotion.offsetWidth;
				let left = this.$elm.fixLeft(this.$refs.emotion);
				let top = this.$elm.fixTop(this.$refs.emotion);
				this.$refs.emoBox.open({
					x: left + width / 2,
					y: top
				})
			},
			onEmotion(emoText) {
				// 保持输入框焦点
				this.$refs.editBox.focus();
				let range = window.getSelection().getRangeAt(0);
				// 插入光标所在位置
				range.setStart(this.focusNode, this.focusOffset)
				let element = document.createElement('IMG')
				element.className = "emo"
				element.dataset.code = emoText;
				element.contentEditable = 'true'
				element.setAttribute("src", this.$emo.textToUrl(emoText));
				// 选中元素节点
				range.insertNode(element)
				// 光标移动到末尾
				range.collapse()

			},
			showRecordBox() {
				this.showRecord = true;
			},
			closeRecordBox() {
				this.showRecord = false;
			},
			showPrivateVideo(mode) {
				let rtcInfo = {
					mode: mode,
					isHost: true,
					friend: this.friend,
				}
				// 通过home.vue打开单人视频窗口
				this.$eventBus.$emit("openPrivateVideo", rtcInfo);
			},
			onGroupVideo() {
				this.$http({
					url: "/webrtc/group/info?groupId=" + this.group.id,
					method: 'GET'
				}).then((rtcInfo) => {
					if (rtcInfo.isChating) {
						// 已在通话中，可以直接加入通话
						this.$refs.rtcJoin.open(rtcInfo);
					} else {
						// 邀请成员发起通话
						let ids = [this.mine.id];
						let maxChannel = this.$store.state.configStore.webrtc.maxChannel;
						this.$refs.rtcSel.open(maxChannel, ids, ids);

					}
				})

			},
			onInviteOk(members) {
				if(members.length < 2){
					return;
				}
				let userInfos = [];
				members.forEach(m => {
					userInfos.push({
						id: m.userId,
						nickName: m.aliasName,
						headImage: m.headImage,
						isCamera: false,
						isMicroPhone: true
					})
				})
				let rtcInfo = {
					isHost: true,
					groupId: this.group.id,
					inviterId: this.mine.id,
					userInfos: userInfos
				}
				// 通过home.vue打开多人视频窗口
				this.$eventBus.$emit("openGroupVideo", rtcInfo);
			},
			showHistoryBox() {
				this.showHistory = true;
			},
			closeHistoryBox() {
				this.showHistory = false;
			},
      showIntersectionBox(){
        this.showIntersection = true;
      },
      closeIntersectionBox(){
        this.showIntersection = false;
      },
			onSendRecord(data) {
				let msgInfo = {
					content: JSON.stringify(data),
					type: 3,
					receipt: this.isReceipt
				}
				// 填充对方id
				this.fillTargetId(msgInfo, this.chat.targetId);
				this.$http({
					url: this.messageAction,
					method: 'post',
					data: msgInfo
				}).then((id) => {
					msgInfo.id = id;
					msgInfo.sendTime = new Date().getTime();
					msgInfo.sendId = this.$store.state.userStore.userInfo.id;
					msgInfo.selfSend = true;
					msgInfo.status = this.$enums.MESSAGE_STATUS.UNSEND;
					msgInfo.readedCount = 0;
					this.$store.commit("insertMessage", msgInfo);
					// 会话置顶
					this.moveChatToTop();
					// 保持输入框焦点
					this.$refs.editBox.focus();
					// 滚动到底部
					this.scrollToBottom();
					// 关闭录音窗口
					this.showRecord = false;
					this.isReceipt = false;

				})
			},
			fillTargetId(msgInfo, targetId) {
				if (this.chat.type == "GROUP") {
					msgInfo.groupId = targetId;
				} else {
					msgInfo.recvId = targetId;
				}
			},
			sendMessage() {
				if (this.sendImageFile) {
					this.sendImageMessage();
				} else {
					this.sendTextMessage();
				}
				// 消息已读
				this.readedMessage()
			},
			sendImageMessage() {
				let file = this.sendImageFile;
				this.onImageBefore(this.sendImageFile);
				let formData = new FormData()
				formData.append('file', file)
				this.$http.post("/image/upload", formData, {
					headers: {
						'Content-Type': 'multipart/form-data'
					}
				}).then((data) => {
					this.onImageSuccess(data, file);
				}).catch((res) => {
					this.onImageSuccess(res, file);
				})
				this.sendImageFile = null;
				this.sendImageUrl = "";
				this.$nextTick(() => this.$refs.editBox.focus());
				this.scrollToBottom();
			},
			sendTextMessage() {
				let sendText = this.createSendText();
				if (!sendText.trim()) {
					return
				}
				this.$refs.editBox.cle
				let msgInfo = {
					content: sendText,
					type: 0
				}
				// 填充对方id
				this.fillTargetId(msgInfo, this.chat.targetId);
				// 被@人员列表
				if (this.chat.type == "GROUP") {
					msgInfo.atUserIds = this.createAtUserIds();
					msgInfo.receipt = this.isReceipt;
				}
				this.lockMessage = true;
				this.$http({
					url: this.messageAction,
					method: 'post',
					data: msgInfo
				}).then((id) => {
					msgInfo.id = id;
					msgInfo.sendTime = new Date().getTime();
					msgInfo.sendId = this.$store.state.userStore.userInfo.id;
					msgInfo.selfSend = true;
					msgInfo.readedCount = 0;
					msgInfo.status = this.$enums.MESSAGE_STATUS.UNSEND;
					this.$store.commit("insertMessage", msgInfo);
					// 会话置顶
					this.moveChatToTop();
				}).finally(() => {
					// 解除锁定
					this.lockMessage = false;
					this.scrollToBottom();
					this.resetEditor();
					this.isReceipt = false;
				});

			},
			deleteMessage(msgInfo) {
				this.$confirm('确认删除消息?', '删除消息', {
					confirmButtonText: '确定',
					cancelButtonText: '取消',
					type: 'warning'
				}).then(() => {
					this.$store.commit("deleteMessage", msgInfo);
				});
			},
			recallMessage(msgInfo) {
				this.$confirm('确认撤回消息?', '撤回消息', {
					confirmButtonText: '确定',
					cancelButtonText: '取消',
					type: 'warning'
				}).then(() => {
					let url = `/message/${this.chat.type.toLowerCase()}/recall/${msgInfo.id}`
					this.$http({
						url: url,
						method: 'delete'
					}).then(() => {
						this.$message.success("消息已撤回");
						msgInfo = JSON.parse(JSON.stringify(msgInfo));
						msgInfo.type = 10;
						msgInfo.content = '你撤回了一条消息';
						msgInfo.status = this.$enums.MESSAGE_STATUS.RECALL;
						this.$store.commit("insertMessage", msgInfo);
					})
				});
			},
			readedMessage() {
				if (this.chat.unreadCount == 0) {
					return;
				}
				this.$store.commit("resetUnreadCount", this.chat)
				if (this.chat.type == "GROUP") {
					var url = `/message/group/readed?groupId=${this.chat.targetId}`
				} else {
					url = `/message/private/readed?friendId=${this.chat.targetId}`
				}
				this.$http({
					url: url,
					method: 'put'
				}).then(() => {})
			},
			loadReaded(fId) {
				this.$http({
					url: `/message/private/maxReadedId?friendId=${fId}`,
					method: 'get'
				}).then((id) => {
					this.$store.commit("readedMessage", {
						friendId: fId,
						maxId: id
					});
				});
			},
			loadGroup(groupId) {
				this.$http({
					url: `/group/find/${groupId}`,
					method: 'get'
				}).then((group) => {
					this.group = group;
					this.$store.commit("updateChatFromGroup", group);
					this.$store.commit("updateGroup", group);

				});

				this.$http({
					url: `/group/members/${groupId}`,
					method: 'get'
				}).then((groupMembers) => {
					this.groupMembers = groupMembers;
				});
			},
			loadFriend(friendId) {
				// 获取对方最新信息
				this.$http({
					url: `/user/find/${friendId}`,
					method: 'get'
				}).then((friend) => {
					this.friend = friend;
					this.$store.commit("updateChatFromFriend", friend);
					this.$store.commit("updateFriend", friend);
				})
			},
			showName(msgInfo) {
				if (this.chat.type == 'GROUP') {
					let member = this.groupMembers.find((m) => m.userId == msgInfo.sendId);
					return member ? member.aliasName : "";
				} else {
					return msgInfo.sendId == this.mine.id ? this.mine.nickName : this.chat.showName
				}

			},
			headImage(msgInfo) {
				if (this.chat.type == 'GROUP') {
					let member = this.groupMembers.find((m) => m.userId == msgInfo.sendId);
					return member ? member.headImage : "";
				} else {
					return msgInfo.sendId == this.mine.id ? this.mine.headImageThumb : this.chat.headImage
				}
			},
			resetEditor() {
				this.sendImageUrl = "";
				this.sendImageFile = null;
				this.$nextTick(() => {
					this.$refs.editBox.innerHTML = "";
					this.$refs.editBox.focus();
				});
			},
			scrollToBottom() {
				this.$nextTick(() => {
					let div = document.getElementById("chatScrollBox");
					div.scrollTop = div.scrollHeight;
				});
			},
			refreshPlaceHolder() {
				if (this.isReceipt) {
					this.placeholder = "【回执消息】"
				} else if (this.$refs.editBox && this.$refs.editBox.innerHTML) {
					this.placeholder = ""
				} else {
					this.placeholder = "聊点什么吧~";
				}
			},
			generateId(){
				// 生成临时id
				return String(new Date().getTime()) + String(Math.floor(Math.random() * 1000));
			}
		},
		computed: {
			mine() {
				return this.$store.state.userStore.userInfo;
			},
			title() {
				let title = this.chat.showName;
				if (this.chat.type == "GROUP") {
					let size = this.groupMembers.filter(m => !m.quit).length;
					title += `(${size})`;
				}
				return title;
			},
			messageAction() {
				return `/message/${this.chat.type.toLowerCase()}/send`;
			},
			unreadCount() {
				return this.chat.unreadCount;
			},
			messageSize() {
				if (!this.chat || !this.chat.messages) {
					return 0;
				}
				return this.chat.messages.length;
			}

		},
		watch: {
			chat: {
				handler(newChat, oldChat) {
					if (newChat.targetId > 0 && (!oldChat || newChat.type != oldChat.type ||
							newChat.targetId != oldChat.targetId)) {
						if (this.chat.type == "GROUP") {
							this.loadGroup(this.chat.targetId);
						} else {
							this.loadFriend(this.chat.targetId);
							// 加载已读状态
							this.loadReaded(this.chat.targetId)
						}
						// 滚到底部
						this.scrollToBottom();
						this.showSide = false;
						// 消息已读
						this.readedMessage()
						// 初始状态只显示30条消息
						let size = this.chat.messages.length;
						this.showMinIdx = size > 30 ? size - 30 : 0;
						// 重置输入框
						this.resetEditor();
						// 复位回执消息
						this.isReceipt = false;
						// 更新placeholder
						this.refreshPlaceHolder();
					}
				},
				immediate: true
			},
			messageSize: {
				handler(newSize, oldSize) {
					if (newSize > oldSize) {
						// 拉至底部
						this.scrollToBottom();
					}
				}
			}
		},
		mounted() {
			let div = document.getElementById("chatScrollBox");
			div.addEventListener('scroll', this.onScroll)
		}
	}
</script>

<style lang="scss">
	.chat-box {
		position: relative;
		width: 100%;
		background: #f8f8f8;
		border: #dddddd solid 1px;

		.el-header {
			padding: 3px;
			background-color: white;
			line-height: 50px;
			font-size: 20px;
			font-weight: 600;
			border-bottom: 1px #ddd solid;

			.btn-side {
				position: absolute;
				right: 20px;
				line-height: 50px;
				font-size: 25px;
				cursor: pointer;

			}
		}

		.im-chat-main {
			padding: 0;
			background-color: #f8f8f8;

			.im-chat-box {
				>ul {
					padding: 0 20px;

					li {
						list-style-type: none;
					}
				}
			}
		}

		.im-chat-footer {
			display: flex;
			flex-direction: column;
			padding: 0;

			.chat-tool-bar {
				display: flex;
				position: relative;
				width: 100%;
				height: 40px;
				text-align: left;
				box-sizing: border-box;
				border-top: #ccc solid 1px;
				padding: 2px;
				background-color: #E8F2FF;

				>div {
					font-size: 22px;
					cursor: pointer;
					color: black;
					line-height: 34px;
					width: 34px;
					height: 34px;
					text-align: center;
					border-radius: 3px;

					&:hover {
						color: black;
					}

					&.chat-tool-active {
						background: #ddd;
					}
				}
			}

			.send-content-area {
				position: relative;
				display: flex;
				flex-direction: column;
				height: 100%;
				background-color: white !important;

				.send-text-area {
					box-sizing: border-box;
					padding: 5px;
					width: 100%;
					flex: 1;
					resize: none;
					font-size: 16px;
					color: black;
					outline-color: rgba(83, 160, 231, 0.61);

					text-align: left;
					line-height: 30px;

					&:before {
						content: attr(placeholder);
						color: gray;
					}

					.at {
						color: blue;
						font-weight: 600;
					}

					.receipt {
						color: darkblue;
						font-size: 15px;
						font-weight: 600;
					}

					.emo {
						width: 30px;
						height: 30px;
						vertical-align: bottom;
					}
				}

				.send-image-area {
					text-align: left;
					border: #53a0e7 solid 1px;

					.send-image-box {
						position: relative;
						display: inline-block;

						.send-image {
							max-height: 180px;
							border: 1px solid #ccc;
							border-radius: 2%;
							margin: 2px;
						}

						.send-image-close {
							position: absolute;
							padding: 3px;
							right: 7px;
							top: 7px;
							color: white;
							cursor: pointer;
							font-size: 15px;
							font-weight: 600;
							background-color: #aaa;
							border-radius: 50%;
							border: 1px solid #ccc;
						}
					}

				}

				.send-btn-area {
					padding: 10px;
					position: absolute;
					bottom: 0;
					right: 0;
				}
			}

		}

		.chat-group-side-box {
			border: #dddddd solid 1px;
			animation: rtl-drawer-in .3s 1ms;
		}
	}
</style>