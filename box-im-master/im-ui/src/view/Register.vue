<template>
	<el-container class="register-view">
		<div class="register-content">
			<el-form :model="registerForm" status-icon :rules="rules" ref="registerForm" class="web-ruleForm">
        <div class="tips">
          <span>register</span>
        </div>
        <div class="register-brand">Welcome!</div>
				<el-form-item class="username input-item" prop="userName">
					<el-input type="userName" v-model="registerForm.userName" autocomplete="off" placeholder="用户名(登录使用)"></el-input>
				</el-form-item>
				<el-form-item class="nickname input-item" prop="nickName">
					<el-input type="nickName" v-model="registerForm.nickName" autocomplete="off" placeholder="昵称"></el-input>
				</el-form-item>
				<el-form-item class="password input-item" prop="password">
					<el-input type="password" v-model="registerForm.password" autocomplete="off" placeholder="密码"></el-input>
				</el-form-item>
				<el-form-item class="confirmpassword input-item" prop="confirmPassword">
					<el-input type="password" v-model="registerForm.confirmPassword" autocomplete="off" placeholder="确认密码"></el-input>
				</el-form-item>
				<div>
					<el-button class="btn" @click="submitForm('registerForm')">Register</el-button>
					<el-button class="btn" @click="resetForm('registerForm')">Reset</el-button>
				</div>
				<div class="to-login">
					<router-link to="/login">Click To Login</router-link>
				</div>
			</el-form>
		</div>
		<!--<icp></icp>-->
	</el-container>
</template>

<script>
	import Icp from '../components/common/Icp.vue'
	export default {
		name: "login",
		components: {
			Icp
		},
		data() {
			var checkUserName = (rule, value, callback) => {
				if (!value) {
					return callback(new Error('请输入用户名'));
				}
				callback();
			};
			var checkNickName = (rule, value, callback) => {
				if (!value) {
					return callback(new Error('请输入昵称'));
				}
				callback();
			};
			var checkPassword = (rule, value, callback) => {
				if (value === '') {
					return callback(new Error('请输入密码'));
				}
				callback();
			};

			var checkConfirmPassword = (rule, value, callback) => {
				if (value === '') {
					return callback(new Error('请输入密码'));
				}
				if (value != this.registerForm.password) {
					return callback(new Error('两次密码输入不一致'));
				}
				callback();
			};


			return {
				registerForm: {
					userName: '',
					nickName: '',
					password: '',
					confirmPassword: ''
				},
				rules: {
					userName: [{
						validator: checkUserName,
						trigger: 'blur'
					}],
					nickName: [{
						validator: checkNickName,
						trigger: 'blur'
					}],
					password: [{
						validator: checkPassword,
						trigger: 'blur'
					}],
					confirmPassword: [{
						validator: checkConfirmPassword,
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
								url: "/register",
								method: 'post',
								data: this.registerForm
							})
							.then((data) => {
								this.$message.success("注册成功!");
							})
					}
				});
			},
			resetForm(formName) {
				this.$refs[formName].resetFields();
			}
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
	.register-view {
    .register-content {
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
      .web-ruleForm {
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
          height: 7%;
          box-sizing: border-box;
          font-size: 25px;
          margin: 3vh;
          span{
            margin: 0 10px 0 0;
            cursor: pointer;
            padding-bottom: 5px;
            transition: 0.1s;
            border-bottom: 4px solid rgb(249, 177, 122);
            color: #fff;
          }
        }
        .input-item{
          width: 75%;
          height: 70px;
          margin: 10px 0 10px 25px;
          //transition: 0.5s;
        }
        .username::before{
          content: "Username";
        }
        .nickname::before{
          content: "Nickname";
        }
        .password::before{
          content: "Password";
        }
        .confirmpassword::before{
          content: "ConfirmPassword";
        }
        .register-brand {
          line-height: 25px;
          //margin: 5px 0 5px 0;
          font-size: 32px;
          font-weight: 600;
          letter-spacing: 2px;
          text-align: center;
          text-transform: uppercase;
          font-family: NeueMachina-Regular;
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

          display: inline-flex;
          justify-content: center; //水平
          align-items: center; //垂直
          font-family: NeueMachina-Regular;
        }
        .btn:hover{
          box-shadow: 0px 0px 15px rgb(249, 177, 122);
        }
        .to-login {
          font-size: 24px;
          display: flex;
          flex-direction: row-reverse;
          line-height: 30px;
          text-align: left;
          padding-left: 20px;
          margin-top:5px ;
        }
      }
    }
	}
</style>
