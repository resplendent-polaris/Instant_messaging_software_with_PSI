<template>
	<div class="login-view">
		<div class="login-content">
			<el-form class="login-form" :model="loginForm" status-icon :rules="rules" ref="loginForm"
				@keyup.enter.native="submitForm('loginForm')">
        <div class="tips">
          <span>login</span>
        </div>
        <h1 >Hello!</h1>
				<el-form-item label="终端" prop="userName" v-show="false">
					<el-input type="terminal" v-model="loginForm.terminal" autocomplete="off"></el-input>
				</el-form-item>
				<el-form-item class="username input-item" prop="userName">
					<el-input type="userName" v-model="loginForm.userName" autocomplete="off"></el-input>
				</el-form-item>
				<el-form-item class="password input-item" prop="password">
					<el-input type="password" v-model="loginForm.password" autocomplete="off"></el-input>
				</el-form-item>
        <div>
          <el-button class="btn" @click="submitForm('loginForm')">Login</el-button>
          <el-button class="btn" @click="resetForm('loginForm')">Reset</el-button>
        </div>
				<div class="register">
					<router-link to="/register">Click To Register</router-link>
				</div>
			</el-form>
		</div>
		<!--<icp></icp>-->
	</div>

</template>

<script>
	import Icp from '../components/common/Icp.vue'
	export default {
		name: "login",
		components: {
			Icp
		},
		data() {
			var checkUsername = (rule, value, callback) => {
				if (!value) {
					return callback(new Error('请输入用户名'));
				}
				callback();
			};
			var checkPassword = (rule, value, callback) => {
				if (value === '') {
					callback(new Error('请输入密码'));
				}
				callback();

			};
			return {
				loginForm: {
					terminal: this.$enums.TERMINAL_TYPE.WEB,
					userName: '',
					password: ''
				},
				rules: {
					userName: [{
						validator: checkUsername,
						trigger: 'blur'
					}],
					password: [{
						validator: checkPassword,
						trigger: 'blur'
					}]
				}
			};
		},
		methods: {
			submitForm(formName) {
				this.$refs[formName].validate((valid) => {
					if (valid) {
						this.$http({
								url: "/login",
								method: 'post',
								data: this.loginForm
							})
							.then((data) => {
								// 保存密码到cookie(不安全)
								this.setCookie('username', this.loginForm.userName);
								this.setCookie('password', this.loginForm.password);
								// 保存token
								sessionStorage.setItem("accessToken", data.accessToken);
								sessionStorage.setItem("refreshToken", data.refreshToken);
								this.$message.success("登陆成功");
								this.$router.push("/home/chat");
							})

					}
				});
			},
			resetForm(formName) {
				this.$refs[formName].resetFields();
			},
			// 获取cookie、
			getCookie(name) {
				let reg = new RegExp("(^| )" + name + "=([^;]*)(;|$)");
				let arr = document.cookie.match(reg)
				if (arr) {
					return unescape(arr[2]);
				}
				return '';
			},
			// 设置cookie,增加到vue实例方便全局调用
			setCookie(name, value, expiredays) {
				var exdate = new Date();
				exdate.setDate(exdate.getDate() + expiredays);
				document.cookie = name + "=" + escape(value) + ((expiredays == null) ? "" : ";expires=" + exdate
					.toGMTString());
			},
			// 删除cookie
			delCookie(name) {
				var exp = new Date();
				exp.setTime(exp.getTime() - 1);
				var cval = this.getCookie(name);
				if (cval != null) {
					document.cookie = name + "=" + cval + ";expires=" + exp.toGMTString();
				}
			}
		},
		mounted() {
			this.loginForm.userName = this.getCookie("username");
			// cookie存密码并不安全，暂时是为了方便
			this.loginForm.password = this.getCookie("password");
		}
	}
</script>

<style scoped lang="scss">
@import '../assets/iconfont/login_register_font.css';
* {
  padding: 0;
  margin: 0;
}
a{
  text-decoration: none;
  color: #a9cff3;
}

	.login-view {
		.login-content {
      width: 80vw;
      height: 80vh;
      background: url(../assets/image/bg.jpg);
      background-attachment: fixed;
      background-size: cover;
      position: absolute;
      left: 50%;
      top: 50%;
      transform: translate(-50%, -50%);
      border-radius: 30px;
      font-family: NeueMachina-Regular;
      overflow: hidden;


			.login-form {
				height: 100%;
				width: 50%;
        background-color: rgb(45, 50, 80);
        border-radius: 30px 0 0 30px;
        color: rgb(255, 255, 255);
        display: flex;
        flex-direction: column;
        align-items: center;
        box-sizing: border-box;
        position: relative;
        .tips{
          width: 70%;
          height: 10%;
          box-sizing: border-box;
          font-size: 25px;
          margin: 4vh;
          span{
            margin: 0 10px 0 0;
            cursor: pointer;
            padding-bottom: 5px;
            transition: 0.1s;
            border-bottom: 4px solid rgb(249, 177, 122);
            color: #fff;
          }
        }
        h1 {
          width: 70%;
          top: 20%;
          margin-bottom: 10px;
          font-size: 32px;
        }
        .username::before{
          content: "Username";
        }
        .input-item{
          width: 75%;
          height: 70px;
          margin: 10px 0 10px 25px;
          //transition: 0.5s;
        }
        /*.input-item>>>.el-input__inner{
          background-color: rgb(66, 71, 105);
        }*/

        .password::before{
          content: "Password";
        }
        .btn{
          width: 100px;
          height: 50px;
          top: 20%;
          cursor: pointer;
          border: 0;
          background-color: rgb(249, 177, 122);
          color: rgb(45, 50, 80);
          margin: 10px 20px;
          border-radius: 5px;
          font-size: 20px;
          box-shadow: 0px 0px 10px rgb(249, 177, 122);
          transition: 0.3s;
          margin-top: 3vw;
          font-family: NeueMachina-Regular;
        }
        .btn:hover{
          box-shadow: 0px 0px 15px rgb(249, 177, 122);
        }


				.register {
          font-size: 24px;
					display: flex;
					flex-direction: row-reverse;
					line-height: 40px;
					text-align: left;
					padding-left: 20px;
          margin-top:25px ;
				}
			}
		}
	}
</style>