import paymenow.webapp.security.VerificationCodeGenerator

// Place your Spring DSL code here	
beans = {
	verificationCodeGenerator(VerificationCodeGenerator){
		grailsApplication = ref("grailsApplication")
	}
	
}
