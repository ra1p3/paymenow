package paymenowwebapp

import com.grailsrocks.authentication.LoginForm

class AccountController {

	def verificationCodeGenerator
	def userManagementService
	
    def index() { 
		if( !userManagementService.isLoggedIn(request) )
			chain(controller: "home")
			
		def invoices = Invoice.findAllByOwnerInList(userManagementService.getUserVerifiedEmails().email)
		def emailAccounts = EmailAccount.findAllByUser(userManagementService.getUser())
		render(view: "accountIndex", model: [invoices: invoices, emailAccounts: emailAccounts])	
	}
	
	
	//TODO: check all methods requiring authentication -> redirect login if needed
	//TODO: check all methods using authenticated user -> handle  session timeout
	def addEmailAccount(){
		log.info("Adding email account")

		if(EmailAccount.findByEmailAndUser(params.emailAddress,userManagementService.getUser())){
			log.info("Email already exists")
			render(view: "/messageViewer", model: [message: "Email already exists"])
			return
		}
		
				
		def verificationCode = verificationCodeGenerator.createCode()
		def user = userManagementService.getUser()
		def emailAcc = new EmailAccount(
			email: params.emailAddress,	
			confirmationCode: verificationCode,	
			isMaster: false, 
			user: user)
		
		if(emailAcc.save()){
			// TODO: send confirmation email
			
			flash.confirmUrl = "http://localhost:8080/PayMeNowWebApp/account/confirmEmailAccount?confirmationCode=${verificationCode}&forLogin=${user.login}"
			def message = "Verification link for new email account has been sent by email."
			render(view: "/messageViewer", model: [message: message])
		}else {
			// TODO: exception handling
			log.error("ERROR ")
		}
	}
	
	
	// TODO: are you allowed to confirm if not logged in. not. but then this needs to redirect to authenticate and then back.
	// TODO: too much logic in controller! move to service
	def confirmEmailAccount(){
		
		log.info("Confirming emailaccount for user[${params.forLogin}] and confirmationCode[${params.confirmationCode}]")
		
		// Needs to be logged in 
		if(!userManagementService.isLoggedIn(request)){
			// To preset the login name on the login form
			def loginForm = new LoginForm(login: params.forLogin) //TODO: not clean. find better way to resupply the login for the login form without creating coupling with authentication moduele
			flash.loginForm = loginForm
			//commands the authentication plugin
			// TODO: will not work if user fails to login on first try. need propably fix on the authentication plugin
			flash.authSuccessURL = [controller: 'account', action: 'confirmEmailAccount', params: [forLogin: params.forLogin, confirmationCode: params.confirmationCode]] 
			
			flash.message = 'You need to login to confirm new email account'
			forward controller: 'login' // Need to forward for the flash to survive for the authentication plugin controller
			return
		}
		
		
		if(!params.forLogin || !params.confirmationCode){
			throw new SecurityException("Not nice: 9")
		}
		
		def forUser = userManagementService.getUser()
		if(forUser.login != params.forLogin){
			throw new SecurityException("Not nice: 8")
		}
		
		def emailAccountToConfirm = EmailAccount.findByConfirmationCodeAndUser(params.confirmationCode, forUser)
		
		emailAccountToConfirm.confirmationDate = new Date();
		if(emailAccountToConfirm.save()){
			log.info("Email account verified")
			flash.message = "New email account verified"
			chain(action: "index")
			
		}else {
			//TODO: error handling
		}	
	}
	
}
