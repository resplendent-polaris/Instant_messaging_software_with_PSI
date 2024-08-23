<template>
	<div class="login-view">
		<div class="login-content">
			<el-form class="login-form" :model="loginForm" status-icon :rules="rules" ref="loginForm"
				@keyup.enter.native="submitForm('loginForm')">
        <div class="tips">
          <span>login</span>
        </div>
        <h1>Hello!</h1>
				<el-form-item label="终端" prop="userName" v-show="false">
					<el-input type="terminal" v-model="loginForm.terminal" autocomplete="off"></el-input>
				</el-form-item>
				<el-form-item class="username input-item" prop="userName" placeholder="用户名" >
					<el-input type="userName" v-model="loginForm.userName" autocomplete="off"></el-input>
				</el-form-item>
				<el-form-item class="password input-item" prop="password" placeholder="密码">
					<el-input :type="passwordVisible ? 'text' : 'password'" v-model="loginForm.password" autocomplete="off">
            <template #suffix>
              <el-button
                  icon="el-icon-view"
                  @click="togglePasswordVisibility"
                  circle
                  :class="passwordVisible ? 'visible' : ''"
              ></el-button>
            </template>
          </el-input>
				</el-form-item>
        <div>
          <el-button class="btn" @click="submitForm('loginForm')">登录</el-button>

        </div>
				<div class="register">
					<router-link to="/register" @click="resetForm('loginForm')">新用户点击去注册</router-link>
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
					password: '',
				},
        passwordVisible: false ,// 控制密码是否可见
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
      togglePasswordVisibility() {
        this.passwordVisible = !this.passwordVisible; // 切换密码可见性
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
::v-deep .el-input__inner{
  width: 100%;
  height: 100%;
  background-color: rgb(66, 71, 105);
  outline: none;
  border: none;
  padding: 11px 10px 9px 15px;
  box-sizing: border-box;
  color: #fff;
  font-size: 20px;
  &:focus{
    border-left: 3px solid rgb(255, 187, 124);
  }
}
.login-view {
		.login-content {
      width: 40vw;
      height: 80vh;
      position: absolute;
      left: 50%;
      top: 50%;
      transform: translate(-50%, -50%);
      //border-radius: 30px;
      font-family: NeueMachina-Regular;
      overflow: hidden;

			.login-form {
				height: 100%;
				width: 100%;
        background-color: rgba(45, 50, 80 , 0.7);
        border-radius: 30px;
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

        .el-button.is-circle {
          padding: 0;
          border: none;
          background: none;
          color: #909399;
          width: 40px;  /* 设置按钮的宽度 */
          height: 30px; /* 设置按钮的高度，保持与宽度相同 */
        }
        h1 {
          width: 70%;
          top: 20%;
          margin-bottom: 10px;
          font-size: 32px;
          text-align: center;
        }
        .username::before{
          content: "Username";
        }
        .input-item{
          width: 75%;
          height: 70px;
          margin: 10px 0 10px 25px;
        }

        .password::before{
          content: "Password";
        }
        .btn{
          width: 200px;
          height: 50px;
          top: 20%;
          cursor: pointer;
          border: 0;
          background-color: rgb(249, 177, 122);
          color: rgb(45, 50, 80);
          margin: 10px 20px;
          border-radius: 50px;
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
          margin-top:25px ;
				}
			}
		}
	}
</style>